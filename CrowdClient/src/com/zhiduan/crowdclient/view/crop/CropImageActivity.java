package com.zhiduan.crowdclient.view.crop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.menucenter.UserInfoActivity;
import com.zhiduan.crowdclient.util.ScreenUtil;
import com.zhiduan.crowdclient.view.CustomProgress;


/*
 * 图片裁剪处理，裁剪的图片为方形
 * Modified from original in AOSP.
 */
public class CropImageActivity extends MonitoredActivity {

	private static final int SIZE_DEFAULT = 2048;
	private static final int SIZE_LIMIT = 4096;

	private final Handler handler = new Handler();

	private int aspectX;
	private int aspectY;

	// Output image
	private int maxX;
	private int maxY;
	private int exifRotation;
	private boolean saveAsPng;

	private Uri sourceUri;
	private Uri saveUri;

	private boolean isSaving;

	private int sampleSize;
	private RotateBitmap rotateBitmap;
	private CropImageView imageView;
	private HighlightView cropView;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.crop__activity_crop);
		setupViews();

		loadInput();
		if (rotateBitmap == null) {
			finish();
			return;
		}
		startCrop();
	}

	private void setupViews() {

		setImmerseLayout(findViewById(R.id.crop_image));

		imageView = (CropImageView) findViewById(R.id.crop_image);
		imageView.context = this;
		imageView.setRecycler(new ImageViewTouchBase.Recycler() {
			@Override
			public void recycle(Bitmap b) {
				b.recycle();
				System.gc();
			}
		});

		findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onSaveClicked();
			}
		});
	}

	private void loadInput() {
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		if (extras != null) {
			aspectX = extras.getInt(Crop.Extra.ASPECT_X);
			aspectY = extras.getInt(Crop.Extra.ASPECT_Y);
			maxX = extras.getInt(Crop.Extra.MAX_X);
			maxY = extras.getInt(Crop.Extra.MAX_Y);
			saveAsPng = extras.getBoolean(Crop.Extra.AS_PNG, false);
			saveUri = extras.getParcelable(MediaStore.EXTRA_OUTPUT);
		}

		sourceUri = intent.getData();
		if (sourceUri != null) {
			exifRotation = CropUtil.getExifRotation(CropUtil.getFromMediaUri(this, getContentResolver(), sourceUri));

			InputStream is = null;
			try {
				sampleSize = calculateBitmapSampleSize(sourceUri);
				is = getContentResolver().openInputStream(sourceUri);
				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inSampleSize = sampleSize;
				rotateBitmap = new RotateBitmap(BitmapFactory.decodeStream(is, null, option), exifRotation);
			} catch (IOException e) {
				Log.e("Error reading image: " + e.getMessage(), e);
				setResultException(e);
			} catch (OutOfMemoryError e) {
				Log.e("OOM reading image: " + e.getMessage(), e);
				setResultException(e);
			} finally {
				CropUtil.closeSilently(is);
			}
		}
	}

	private int calculateBitmapSampleSize(Uri bitmapUri) throws IOException {
		InputStream is = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		try {
			is = getContentResolver().openInputStream(bitmapUri);
			BitmapFactory.decodeStream(is, null, options); // Just get image size
		} finally {
			CropUtil.closeSilently(is);
		}

		int maxSize = getMaxImageSize();
		int sampleSize = 1;
		while (options.outHeight / sampleSize > maxSize || options.outWidth / sampleSize > maxSize) {
			sampleSize = sampleSize << 1;
		}
		return sampleSize;
	}

	private int getMaxImageSize() {
		int textureLimit = getMaxTextureSize();
		if (textureLimit == 0) {
			return SIZE_DEFAULT;
		} else {
			return Math.min(textureLimit, SIZE_LIMIT);
		}
	}

	private int getMaxTextureSize() {
		// The OpenGL texture size is the maximum size that can be drawn in an ImageView
		int[] maxSize = new int[1];
		GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
		return maxSize[0];
	}

	private void startCrop() {
		if (isFinishing()) {
			return;
		}
		imageView.setImageRotateBitmapResetBase(rotateBitmap, true);
		//		CropUtil.startBackgroundJob(this, null, getResources().getString(R.string.crop__wait),
		//				new Runnable() {
		//			public void run() {
		//				final CountDownLatch latch = new CountDownLatch(1);
		//				handler.post(new Runnable() {
		//					public void run() {
		//						if (imageView.getScale() == 1F) {
		//							imageView.center();
		//						}
		//						latch.countDown();
		//					}
		//				});
		//				try {
		//					latch.await();
		//				} catch (InterruptedException e) {
		//					throw new RuntimeException(e);
		//				}
		new Cropper().crop();
		//			}
		//		}, handler
		//				);
	}

	private class Cropper {

		private void makeDefault() {
			if (rotateBitmap == null) {
				return;
			}

			HighlightView hv = new HighlightView(imageView);
			final int width = rotateBitmap.getWidth();
			final int height = rotateBitmap.getHeight();

			Rect imageRect = new Rect(0, 0, width, height);

			// Make the default size about 4/5 of the width or height
			int cropWidth = Math.min(width, height) * 4 / 5;
			@SuppressWarnings("SuspiciousNameCombination")
			int cropHeight = cropWidth;

			if (aspectX != 0 && aspectY != 0) {
				if (aspectX > aspectY) {
					cropHeight = cropWidth * aspectY / aspectX;
				} else {
					cropWidth = cropHeight * aspectX / aspectY;
				}
			}

			int x = (width - cropWidth) / 2;
			int y = (height - cropHeight) / 2;

			RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
			hv.setup(imageView.getUnrotatedMatrix(), imageRect, cropRect, aspectX != 0 && aspectY != 0);
			imageView.add(hv);
		}

		public void crop() {
			handler.post(new Runnable() {
				public void run() {
					makeDefault();
					imageView.invalidate();
					if (imageView.highlightViews.size() == 1) {
						cropView = imageView.highlightViews.get(0);
						cropView.setFocus(true);
					}
				}
			});
		}
	}

	private void onSaveClicked() {

		if (cropView == null || isSaving) {
			return;
		}
		isSaving = true;

		mHandler.sendEmptyMessage(0x0012);
	}

	private Handler mHandler = new Handler(){

		public void handleMessage(Message msg){

		if(msg.what == 0x0012){

				CustomProgress.showDialog(CropImageActivity.this, "图片裁剪中", false, null);
				imageView.highlightViews.clear();
				Bitmap croppedImage = null;
				Rect r = cropView.getScaledCropRect(sampleSize);
				int width = r.width();
				int height = r.height();

				int outWidth = width;
				int outHeight = height;
				if (maxX > 0 && maxY > 0 && (width > maxX || height > maxY)) {
					float ratio = (float) width / (float) height;
					if ((float) maxX / (float) maxY > ratio) {
						outHeight = maxY;
						outWidth = (int) ((float) maxY * ratio + .5f);
					} else {
						outWidth = maxX;
						outHeight = (int) ((float) maxX / ratio + .5f);
					}
				}

				try {
					croppedImage = decodeRegionCrop(r, outWidth, outHeight);
				} catch (IllegalArgumentException e) {
					setResultException(e);
					finish();
					return;
				}

//				CustomProgress.dissDialog();
				if(croppedImage != null){
					
					//这个判断是否为null，支持多个
					if(UserInfoActivity.getActivity() != null){
						UserInfoActivity.getActivity().mBitmap = croppedImage;
					}
					
					setResult(RESULT_OK, new Intent().putExtra(MediaStore.EXTRA_OUTPUT, 0));
				}

				finish();
			}
		}
	};

	private void saveImage(Bitmap croppedImage) {
		//        if (croppedImage != null) {
		//            final Bitmap b = croppedImage;
		//            CropUtil.startBackgroundJob(this, null, getResources().getString(R.string.crop__saving),
		//                    new Runnable() {
		//                        public void run() {
		//                            saveOutput(b);
		//                        }
		//                    }, handler
		//            );
		//        } else {
		//            finish();
		//        }

		if(croppedImage != null){
			saveOutput(croppedImage);
		}else{
			finish();
		}

	}

	private Bitmap decodeRegionCrop(Rect rect, int outWidth, int outHeight) {
		// Release memory now
		//		clearImageView();

		InputStream is = null;
		Bitmap croppedImage = null;
		try {
			is = getContentResolver().openInputStream(sourceUri);
			BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
			final int width = decoder.getWidth();
			final int height = decoder.getHeight();

			if (exifRotation != 0) {
				// Adjust crop area to account for image rotation
				Matrix matrix = new Matrix();
				matrix.setRotate(-exifRotation);

				RectF adjusted = new RectF();
				matrix.mapRect(adjusted, new RectF(rect));

				// Adjust to account for origin at 0,0
				adjusted.offset(adjusted.left < 0 ? width : 0, adjusted.top < 0 ? height : 0);
				rect = new Rect((int) adjusted.left, (int) adjusted.top, (int) adjusted.right, (int) adjusted.bottom);
			}

			try {
				croppedImage = decoder.decodeRegion(rect, new BitmapFactory.Options());
				if (croppedImage != null && (rect.width() > outWidth || rect.height() > outHeight)) {
					Matrix matrix = new Matrix();
					matrix.postScale((float) outWidth / rect.width(), (float) outHeight / rect.height());
					croppedImage = Bitmap.createBitmap(croppedImage, 0, 0, croppedImage.getWidth(), croppedImage.getHeight(), matrix, true);
				}
			} catch (IllegalArgumentException e) {
				// Rethrow with some extra information
				throw new IllegalArgumentException("Rectangle " + rect + " is outside of the image ("
						+ width + "," + height + "," + exifRotation + ")", e);
			}

		} catch (IOException e) {
			Log.e("Error cropping image: " + e.getMessage(), e);
			setResultException(e);
		} catch (OutOfMemoryError e) {
			Log.e("OOM cropping image: " + e.getMessage(), e);
			setResultException(e);
		} finally {
			CropUtil.closeSilently(is);
		}
		return croppedImage;
	}

	private void clearImageView() {
		imageView.clear();
		if (rotateBitmap != null) {
			rotateBitmap.recycle();
		}
		System.gc();
	}

	private void saveOutput(Bitmap croppedImage) {
		if (saveUri != null) {
			OutputStream outputStream = null;
			try {
				outputStream = getContentResolver().openOutputStream(saveUri);
				if (outputStream != null) {
					croppedImage.compress(saveAsPng ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG,
							90,     // note: quality is ignored when using PNG
							outputStream);
				}
			} catch (IOException e) {
				setResultException(e);
				Log.e("Cannot open file: " + saveUri, e);
			} finally {
				CropUtil.closeSilently(outputStream);
			}

			CropUtil.copyExifRotation(
					CropUtil.getFromMediaUri(this, getContentResolver(), sourceUri),
					CropUtil.getFromMediaUri(this, getContentResolver(), saveUri)
					);

			setResultUri(saveUri);
		}

		final Bitmap b = croppedImage;
		handler.post(new Runnable() {
			public void run() {
				imageView.clear();
				b.recycle();
			}
		});

		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (rotateBitmap != null) {
			rotateBitmap.recycle();
		}
	}

	@Override
	public boolean onSearchRequested() {
		return false;
	}

	public boolean isSaving() {
		return isSaving;
	}

	private void setResultUri(Uri uri) {
		setResult(RESULT_OK, new Intent().putExtra(MediaStore.EXTRA_OUTPUT, uri));
	}

	private void setResultException(Throwable throwable) {
		setResult(Crop.RESULT_ERROR, new Intent().putExtra(Crop.Extra.ERROR, throwable));
	}

}
