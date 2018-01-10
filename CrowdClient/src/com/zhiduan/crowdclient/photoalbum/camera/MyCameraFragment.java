package com.zhiduan.crowdclient.photoalbum.camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.GeneralDialog;

/**
 * 相机
 * 
 * @author wfq
 * 
 */
public class MyCameraFragment extends Fragment implements OnClickListener,
		SensorEventListener {

	private Activity mContext;
	private SurfaceView mSvShow;
	private Button mBtPhotograph;
	private boolean isPhotograph;
	private SensorManager sensorManager;
	private ImageView mIvFocus;
	private long startHandler = 0;
	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (mIvFocus != null) {
				switch (msg.what) {
				case 0:
					// startHandler = System.currentTimeMillis();
					//
					mIvFocus.setVisibility(View.VISIBLE);
					mIvFocus.setBackgroundResource(R.drawable.camera_scan_1);
					break;
				case 1:

					if (mIvFocus.getVisibility() == View.INVISIBLE) {
						return;
					}
					mIvFocus.setVisibility(View.VISIBLE);
					mIvFocus.setBackgroundResource(R.drawable.camera_scan_2);
					if (camera != null) {
						try {

							try {

								camera.autoFocus(null);
							} catch (Exception e) {
							}
						} catch (Exception e) {
						}
					}
					break;
				case 2:
					mIvFocus.setVisibility(View.INVISIBLE);
					break;

				default:
					break;
				}
			}

		};
	};
	private Button takepicture;
	private ImageView mIvQuXiao;
	private ImageView mIvOK;
	private RelativeLayout mRlWanCheng;
	private autoFocus5Thread autoFocus5 = new autoFocus5Thread();
	private boolean isSensorManager = true;
	private int flashlight = 0;// 闪光灯：0关，1开
	private RelativeLayout mRlQueen;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.activity_my_camera,
				null);
		// 获取系统的传感器管理服务
		sensorManager = (SensorManager) getActivity().getSystemService(
				Context.SENSOR_SERVICE);

		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		widthWindow = metric.widthPixels;
		heightWindow = metric.heightPixels;
		// 相机
		mSvShow = (SurfaceView) view.findViewById(R.id.surfaceView);
		mIvFocus = (ImageView) view.findViewById(R.id.my_camera_iv_focus);
		takepicture = (Button) view.findViewById(R.id.takepicture);
		mIvQuXiao = (ImageView) view.findViewById(R.id.my_camera_iv_quxiao);
		mIvOK = (ImageView) view.findViewById(R.id.my_camera_iv_ok);
		mRlWanCheng = (RelativeLayout) view
				.findViewById(R.id.my_camera_rl_wancheng);
		mRlQueen = (RelativeLayout) view.findViewById(R.id.my_camera_rl_queen);
		mSvShow.setOnClickListener(this);
		mIvFocus.setOnClickListener(this);
		takepicture.setOnClickListener(this);
		mIvQuXiao.setOnClickListener(this);
		mIvOK.setOnClickListener(this);
		mContext = getActivity();
		SurfaceHolder holder = mSvShow.getHolder();
		holder.setFixedSize(widthWindow / 3 * 2, heightWindow / 3 * 2);// 设置分辨率
		holder.setKeepScreenOn(true);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// SurfaceView只有当activity显示到了前台，该控件才会被创建 因此需要监听surfaceview的创建
		holder.addCallback(new MySurfaceCallback());

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		try {
			// 为系统的加速度传感器注册监听器
			sensorManager.registerListener(this,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_GAME);
		} catch (Exception e) {
			isSensorManager = false;
		}

	}

	@Override
	public void onStop() {
		// 取消注册
		if (isSensorManager) {
			sensorManager.unregisterListener(this);
		} else {
			if (autoFocus5 != null) {

				autoFocus5.stopFocus();
				autoFocus5 = null;
			}
		}
		super.onStop();
	}

	private static Camera camera;
	private autoFocusThread autoFocus;
	private int widthWindow;
	private int heightWindow;

	/**
	 * 监听surfaceview的创建
	 * 
	 * @author Administrator Surfaceview只有当activity显示到前台，该空间才会被创建
	 */
	private final class MySurfaceCallback implements SurfaceHolder.Callback {

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int w, int h) {

			try {

				PackageManager pm = getActivity().getPackageManager();
				boolean flag = (PackageManager.PERMISSION_GRANTED == pm
						.checkPermission("android.permission.CAMERA",
								"com.zhiduan.crowdclient"));
				if (!flag) {
					GeneralDialog.showOneButtonDialog(mContext,
							GeneralDialog.DIALOG_ICON_TYPE_5, "",
							"相机权限没有打开或者相机资源被占用", "确定",
							new GeneralDialog.OneButtonListener() {

								@Override
								public void onExitClick(Dialog dialog) {
									if (dialog != null) {
										dialog.dismiss();
									}
									getActivity().finish();
								}

								@Override
								public void onButtonClick(Dialog dialog) {
									if (dialog != null) {
										dialog.dismiss();
									}
									getActivity().finish();

								}
							});

				}
				camera = Camera.open();

				Parameters params = camera.getParameters();
				// 获取预览的各种分辨率
				List<Size> supportedPreviewSizes = params
						.getSupportedPreviewSizes();
				// 获取摄像头支持的各种分辨率
				List<Size> supportedPictureSizes = params
						.getSupportedPictureSizes();
				Log.i("i", params.flatten());
				params.setJpegQuality(100); // 设置照片的质量
				params.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
				Size size = getOptimalPreviewSize(supportedPreviewSizes, w, h);
				Size siz = getOptimalPreviewSize(supportedPictureSizes, w, h);
				params.setPreviewSize(size.width, size.height); // 设置预览大小
				camera.setDisplayOrientation(getPreviewDegree());
				params.setRotation(getPreviewDegree());
				params.setPictureSize(siz.width, siz.height);
				// params.setPreviewFrameRate(50); // 预览帧率
				camera.setParameters(params); // 将参数设置给相机
				// 设置预览显示
				camera.setPreviewDisplay(mSvShow.getHolder());

				// 开启预览
				camera.startPreview();
				if (!isSensorManager) {
					if (autoFocus5 != null) {
						autoFocus5.stopFocus();
						autoFocus5 = null;
					}
					autoFocus5 = new autoFocus5Thread();
					autoFocus5.start();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				GeneralDialog.showOneButtonDialog(mContext,
						GeneralDialog.DIALOG_ICON_TYPE_5, "",
						"相机权限没有打开或者相机资源被占用", "确定",
						new GeneralDialog.OneButtonListener() {

							@Override
							public void onExitClick(Dialog dialog) {
								if (dialog != null) {
									dialog.dismiss();
								}
								getActivity().finish();
							}

							@Override
							public void onButtonClick(Dialog dialog) {
								if (dialog != null) {
									dialog.dismiss();
								}
								getActivity().finish();

							}
						});
				e.printStackTrace();
			}

		}

		@Override
		public void surfaceCreated(SurfaceHolder arg0) {

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			if (camera != null) {
				camera.release();
				camera = null;

			}
		}

	}

	private String pathCamera;
	private byte[] mData;

	private final class MyPictureCallback implements Camera.PictureCallback {
		@Override
		public void onPictureTaken(byte[] data, final Camera camera) {
			// TODO Auto-generated method stub

			camera.stopPreview();
			mData = data;
			if (existSDCard()) {
				if (getAvailableSD() < mData.length) {
					if (getAvailableRAM() < mData.length) {
						GeneralDialog.showOneButtonDialog(mContext,
								GeneralDialog.DIALOG_ICON_TYPE_5, "",
								"手机内存不足请清理手机", "取消",
								new GeneralDialog.OneButtonListener() {

									@Override
									public void onExitClick(Dialog dialog) {
										if (dialog != null) {
											dialog.dismiss();
										}
										// 取消
										// 为系统的加速度传感器注册监听器
										camera.startPreview();
										try {
											sensorManager
													.registerListener(
															MyCameraFragment.this,
															sensorManager
																	.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
															SensorManager.SENSOR_DELAY_GAME);

										} catch (Exception e) {
											isSensorManager = false;
											if (autoFocus5 != null) {
												autoFocus5.stopFocus();
												autoFocus5 = null;
											}
											autoFocus5 = new autoFocus5Thread();
											autoFocus5.start();
										}
										mRlWanCheng.setVisibility(View.GONE);
										mSvShow.setEnabled(true);
										mIvFocus.setEnabled(true);
										takepicture.setEnabled(true);
										camera.startPreview();
									}

									@Override
									public void onButtonClick(Dialog dialog) {
										if (dialog != null) {
											dialog.dismiss();
										}
										// 取消
										// 为系统的加速度传感器注册监听器
										camera.startPreview();
										try {
											sensorManager
													.registerListener(
															MyCameraFragment.this,
															sensorManager
																	.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
															SensorManager.SENSOR_DELAY_GAME);

										} catch (Exception e) {
											isSensorManager = false;
											if (autoFocus5 != null) {
												autoFocus5.stopFocus();
												autoFocus5 = null;
											}
											autoFocus5 = new autoFocus5Thread();
											autoFocus5.start();
										}
										mRlWanCheng.setVisibility(View.GONE);
										mSvShow.setEnabled(true);
										mIvFocus.setEnabled(true);
										takepicture.setEnabled(true);
										camera.startPreview();

									}
								});
						return;
					}
				}
			} else {
				if (getAvailableRAM() < mData.length) {
					GeneralDialog.showOneButtonDialog(mContext,
							GeneralDialog.DIALOG_ICON_TYPE_5, "",
							"手机内存不足请清理手机", "取消",
							new GeneralDialog.OneButtonListener() {

								@Override
								public void onExitClick(Dialog dialog) {
									if (dialog != null) {
										dialog.dismiss();
									}
									// 取消
									// 为系统的加速度传感器注册监听器
									camera.startPreview();
									try {
										sensorManager
												.registerListener(
														MyCameraFragment.this,
														sensorManager
																.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
														SensorManager.SENSOR_DELAY_GAME);

									} catch (Exception e) {
										isSensorManager = false;
										if (autoFocus5 != null) {
											autoFocus5.stopFocus();
											autoFocus5 = null;
										}
										autoFocus5 = new autoFocus5Thread();
										autoFocus5.start();
									}
									mRlWanCheng.setVisibility(View.GONE);
									mSvShow.setEnabled(true);
									mIvFocus.setEnabled(true);
									takepicture.setEnabled(true);
									camera.startPreview();
								}

								@Override
								public void onButtonClick(Dialog dialog) {
									if (dialog != null) {
										dialog.dismiss();
									}
									// 取消
									// 为系统的加速度传感器注册监听器
									camera.startPreview();
									try {
										sensorManager
												.registerListener(
														MyCameraFragment.this,
														sensorManager
																.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
														SensorManager.SENSOR_DELAY_GAME);

									} catch (Exception e) {
										isSensorManager = false;
										if (autoFocus5 != null) {
											autoFocus5.stopFocus();
											autoFocus5 = null;
										}
										autoFocus5 = new autoFocus5Thread();
										autoFocus5.start();
									}
									mRlWanCheng.setVisibility(View.GONE);
									mSvShow.setEnabled(true);
									mIvFocus.setEnabled(true);
									takepicture.setEnabled(true);
									camera.startPreview();

								}
							});
					return;
				}
			}

			mIvOK.setEnabled(true);
			mIvQuXiao.setEnabled(true);
			// camera.startPreview();

		}
	}

	// 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
	public int getPreviewDegree() {
		// 获得手机的方向
		int rotation = getActivity().getWindowManager().getDefaultDisplay()
				.getRotation();
		int degree = 0;
		// 根据手机的方向计算相机预览画面应该选择的角度
		switch (rotation) {
		case Surface.ROTATION_0:
			degree = 90;
			break;
		case Surface.ROTATION_90:
			degree = 0;
			break;
		case Surface.ROTATION_180:
			degree = 270;
			break;
		case Surface.ROTATION_270:
			degree = 180;
			break;
		}
		return degree;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (camera != null) {
			camera.release();
			camera = null;

		}

	}

	// 这里是预览图尺寸处理的方法，就是在这把宽高调换，就可以达到效果
	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			// double ratio = (double) size.width / size.height;
			double ratio = (double) size.height / size.width;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.width - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.width - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the
		// requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.width - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.width - targetHeight);
				}
			}
		}

		return optimalSize;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.surfaceView:
			// TODO camera.autoFocus(null);
			if (autoFocus != null) {
				autoFocus.stopFocus();
				autoFocus.setIsSend(new ISSend() {

					@Override
					public boolean isSend() {
						return true;
					}
				});
			}
			// try {
			// Thread.sleep(400);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			autoFocus = new autoFocusThread();
			autoFocus.start();
			break;
		case R.id.my_camera_iv_focus:
			if (autoFocus != null) {
				autoFocus.stopFocus();
				autoFocus.setIsSend(new ISSend() {

					@Override
					public boolean isSend() {
						return true;
					}
				});
			}
			// try {
			// Thread.sleep(400);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			autoFocus = new autoFocusThread();
			autoFocus.start();
			break;
		case R.id.takepicture:
			if (autoFocus != null) {
				autoFocus.stopFocus();
			}
			autoFocus = null;
			mIvFocus.setVisibility(View.INVISIBLE);
			if (autoFocus5 != null) {
				autoFocus5.stopFocus();
			}
			mRlWanCheng.setVisibility(View.VISIBLE);
			mSvShow.setEnabled(false);
			mIvFocus.setEnabled(false);
			takepicture.setEnabled(false);
			// 取消注册
			if (isSensorManager) {

				sensorManager.unregisterListener(MyCameraFragment.this);
			} else {
			}
			mIvOK.setEnabled(false);
			mIvQuXiao.setEnabled(false);
			camera.takePicture(null, null, new MyPictureCallback());
			break;
		case R.id.my_camera_iv_quxiao:
			// 取消
			// 为系统的加速度传感器注册监听器
			camera.startPreview();
			try {
				sensorManager.registerListener(this, sensorManager
						.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
						SensorManager.SENSOR_DELAY_GAME);

			} catch (Exception e) {
				isSensorManager = false;
				if (autoFocus5 != null) {
					autoFocus5.stopFocus();
					autoFocus5 = null;
				}
				autoFocus5 = new autoFocus5Thread();
				autoFocus5.start();
			}
			mRlWanCheng.setVisibility(View.GONE);
			mSvShow.setEnabled(true);
			mIvFocus.setEnabled(true);
			takepicture.setEnabled(true);
			camera.startPreview();

			break;
		case R.id.my_camera_rl_queen:
			// Intent intent=new Intent(this,MyCameraQueenActivity.class);
			// startActivity(intent);
			// finish();
			break;
		case R.id.my_camera_iv_ok:

			CustomProgress.showDialog(mContext, "正在处理...", false, null);

			new Thread() {
				public void run() {
					String time = getTime();
					File file;
					File pathname;
					try {
						if (existSDCard()) {
							pathname = new File(Environment
									.getExternalStorageDirectory().getPath()
									+ "/aixuepai/photograph/");

							// pathname = new
							// File(Environment.getExternalStorageDirectory(),
							// "camera.jpg");

							if (getAvailableSD() < mData.length) {
								pathname = new File(getActivity().getCacheDir()
										.getPath() + "/aixuepai/photograph/");
								if (getAvailableRAM() < mData.length) {
//									Toast.makeText(mContext, "手机内存不足请清理手机", 0);

									return;
								}
							}
						} else {
							pathname = new File(getActivity().getCacheDir()
									.getPath() + "/aixuepai/photograph/");
							if (getAvailableRAM() < mData.length) {
//								Toast.makeText(mContext, "手机内存不足请清理手机", 0);
								return;
							}
						}
						if (!pathname.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
							pathname.mkdirs();
						}
						Bitmap rotateBitmap = BitmapFactory.decodeByteArray(
								mData, 0, mData.length);

						if (!TextUtils.isEmpty(android.os.Build.BRAND)
								&& android.os.Build.BRAND.contains("samsung")) {
							rotateBitmap = rotateBitmap(rotateBitmap, 90);
						}
						file = new File(pathname, time + ".jpg");
						// file = new
						// File(Environment.getExternalStorageDirectory(),
						// "camera.jpg");
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(Bitmap2Bytes(rotateBitmap));
						fos.close();
						// 在拍照的时候相机是被占用的,拍照之后需要重新预览
						Log.e("wufuqi---", "" + mData.length);
						pathCamera = pathname.getPath() + "/" + time + ".jpg";
						// pathCamera=Environment.getExternalStorageDirectory().getPath()+"/camera.jpg";

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					CustomProgress.dissDialog();
					Intent data = new Intent();
					// 确定
					data.putExtra("pathCamera", pathCamera);
					getActivity().setResult(getActivity().RESULT_OK, data);
					getActivity().finish();
				};

			}.start();

			break;
		case R.id.my_camera_rl_reversal_canera:
			// 闪光灯
			if (flashlight == 0) {
				// 打开
				if (camera != null) {
					Parameters parameters = camera.getParameters();
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
					camera.setParameters(parameters);
				}
				flashlight = 1;
			} else {
				// 关闭
				if (camera != null) {
					Parameters parameters = camera.getParameters();
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
					camera.setParameters(parameters);
				}
				flashlight = 0;
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	private long startTime = System.currentTimeMillis();
	private float mX = 0;
	private float mY = 0;
	private float mZ = 0;
	private boolean isFocus = false;
	// 加速运动状态
	private final int ZCCELERATE = 0;
	// 减速运动状态
	private final int SPEEDCUT = 1;
	// 静止状态
	private final int STATIC = 3;
	// 传感器的状态
	private int mSensorType = STATIC;
	// 静止到加速时的值
	private final int ZCCELERATEPRIVE = 2;
	// 加速到静止时的值
	private final int SPEEDCUTPRIVE = 2;
	// 加速到静止时的值
	private final int STATICPRIVE = 1;
	private Toast toast;
	private long moveTime;

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] values = event.values;
		StringBuilder sb = new StringBuilder();
		float x = values[0];
		float y = values[1];
		float z = values[2];

		if (camera != null) {

			try {
				long endTime = System.currentTimeMillis();
				if (endTime - startTime > 800) {
					// 当传感器为静止状态时
					if (mSensorType == STATIC) {
						if (Math.abs(x - mX) > ZCCELERATEPRIVE
								|| Math.abs(y - mY) > ZCCELERATEPRIVE
								|| Math.abs(z - mZ) > ZCCELERATEPRIVE) {
							// 开始运动
							mSensorType = ZCCELERATE;
							isFocus = !isFocus;
							if (isFocus) {
								moveTime = System.currentTimeMillis();
								// showToast("开始运动x" + x + ",y" + y + ",z" + z);
								isFocus = false;
							}
						}

					} else if (mSensorType == ZCCELERATE) {
						if (Math.abs(x - mX) < SPEEDCUTPRIVE
								|| Math.abs(y - mY) < SPEEDCUTPRIVE
								|| Math.abs(z - mZ) < SPEEDCUTPRIVE) {
							// 开始减速
							// showToast("开始减速x" + x + ",y" + y + ",z" + z);
							mSensorType = SPEEDCUT;

						}
					} else if (mSensorType == SPEEDCUT) {
						//
						if (Math.abs(x - mX) < STATICPRIVE
								|| Math.abs(y - mY) < STATICPRIVE
								|| Math.abs(z - mZ) < STATICPRIVE) {
							long statiTime = System.currentTimeMillis();
							if (statiTime - moveTime < 3000) {
								mSensorType = ZCCELERATE;
							} else {
								// 静止状态
								mSensorType = STATIC;
								isFocus = true;
								if (isFocus) {
									if (autoFocus != null) {
										autoFocus.stopFocus();
										autoFocus.setIsSend(new ISSend() {

											@Override
											public boolean isSend() {
												return true;
											}
										});
									}

									autoFocus = new autoFocusThread();
									autoFocus.start();
								}
								// showToast("静止状态x" + x + ",y" + y + ",z" +
								// z);
								isFocus = false;
							}

						} else {
							// 如果不是静止状态，就是加速状态
							mSensorType = ZCCELERATE;
						}
					}

					startTime = endTime;

					mX = x;
					mY = y;
					mZ = z;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// /**
	// * 弹出toast提示 防止覆盖
	// *
	// * @param msg
	// */
	// public void showToast(String msg) {
	// //
	// if (toast == null) {
	// toast = Toast.makeText(this, msg + "", Toast.LENGTH_SHORT);
	// } else {
	// toast.setText(msg);
	// }
	// toast.show();
	// }

	public class autoFocusThread extends Thread {

		private boolean isFocus = true;

		@Override
		public void run() {
			for (int i = 0; i < 3; i++) {

				if (mSend != null) {
					if (mSend.isSend()) {
						return;
					}
				}

				if (!isFocus) {
					return;
				}
				if (mHandler != null)
					mHandler.sendEmptyMessage(i);

				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		private ISSend mSend;

		public void stopFocus() {
			isFocus = false;
		}

		public void setIsSend(ISSend send) {
			mSend = send;
		}

	}

	interface ISSend {
		boolean isSend();
	}

	public static String getTime() {

		Date nowdate = new Date(); // 褰撳墠鏃堕棿
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss_SSS");
		return dateFormat.format(nowdate);
	}

	/**
	 * 判断手机是否有SD卡。
	 * 
	 * @return 有SD卡返回true，没有返回false。
	 */
	public static boolean existSDCard() {

		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	class autoFocus5Thread extends Thread {
		private boolean isFocus = true;

		@Override
		public void run() {
			while (isFocus) {
				long startTime = System.currentTimeMillis();
				if (camera != null) {
					try {

						camera.autoFocus(null);
					} catch (Exception e) {
					}
				}
				long endTime = System.currentTimeMillis();

				if (endTime - startTime < 5000) {
					try {
						Thread.sleep(5000 - (endTime - startTime));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

		public void stopFocus() {
			isFocus = false;
		}
	}

	// 没有闪光灯
	private boolean isFlashlight() {
		return getActivity().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_FLASH);
	}

	/**
	 * 获取SD卡的剩余空间
	 * 
	 * @return
	 */
	private long getAvailableSD() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		long availableBlocks = stat.getAvailableBlocks();

		long totalSize = totalBlocks * blockSize;
		long availSize = availableBlocks * blockSize;
		return availSize;
	}

	/**
	 * 获取手机的剩余空间
	 * 
	 * @return
	 */
	private long getAvailableRAM() {
		File path2 = Environment.getDataDirectory();
		StatFs stat2 = new StatFs(path2.getPath());
		long blockSize2 = stat2.getBlockSize();
		long totalBlocks2 = stat2.getBlockCount();
		long availableBlocks2 = stat2.getAvailableBlocks();

		long totalSize2 = totalBlocks2 * blockSize2;
		long availSize2 = availableBlocks2 * blockSize2;
		return availSize2;
	}

	/**
	 * 旋转图片，使图片保持正确的方向。
	 * 
	 * @param bitmap
	 *            原始图片
	 * @param degrees
	 *            原始图片的角度
	 * @return Bitmap 旋转后的图片
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
		if (degrees == 0 || null == bitmap) {
			return bitmap;
		}
		Matrix matrix = new Matrix();
		matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
		Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		if (null != bitmap) {
			bitmap.recycle();
		}
		return bmp;
	}

	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public Camera getCamera() {
		return camera;

	}

	/**
	 * 判断手机可不可以变焦
	 * 
	 * @return
	 */
	public boolean isSupportZoom() {
		if (camera == null) {
			return false;
		}
		boolean isSuppport = true;
		if (camera.getParameters().isSmoothZoomSupported()) {
			isSuppport = false;
		}
		return isSuppport;
	}

	/**
	 * 添加焦距
	 */
	public void AddZoom() {
		if (isSupportZoom()) {
			try {
				Parameters params = camera.getParameters();
				final int MAX = params.getMaxZoom();
				if (MAX == 0)
					return;
				int zoomValue = params.getZoom();
				// Trace.Log("-----------------MAX:"+MAX+"   params : "+zoomValue);
				zoomValue += 5;
				if (MAX < zoomValue) {
					zoomValue = MAX;
				}
				params.setZoom(zoomValue);
				camera.setParameters(params);
				Camera.Parameters parameters = camera.getParameters();
				parameters.setFocusMode("infinity");
				camera.setParameters(parameters);
				// Trace.Log("Is support Zoom " + params.isZoomSupported());
			} catch (Exception e) {
				// Trace.Log("--------exception zoom");
//				Toast.makeText(mContext, "变焦失败", 0);
				e.printStackTrace();
			}
		} else {
			// Trace.Log("--------the phone not support zoom");
//			Toast.makeText(mContext, "手机不支持变焦", 0);
		}
	}

	/**
	 * Auto-focus mode.
	 */
	public static final String FOCUS_MODE_AUTO = "auto";

	/**
	 * Focus is set at infinity. Applications should not call
	 * {@link #autoFocus(AutoFocusCallback)} in this mode.
	 */
	public static final String FOCUS_MODE_INFINITY = "infinity";
	public static final String FOCUS_MODE_MACRO = "macro";

	/**
	 * 减少焦距
	 */
	public void ReduceZoom() {
		if (isSupportZoom()) {
			try {
				Parameters params = camera.getParameters();
				final int MAX = params.getMaxZoom();
				if (MAX == 0)
					return;
				int zoomValue = params.getZoom();
				// Trace.Log("-----------------MAX:"+MAX+"   params : "+zoomValue);
				zoomValue += 5;
				if (MAX < zoomValue) {
					zoomValue = MAX;
				}
				params.setZoom(zoomValue);
				camera.setParameters(params);

				Camera.Parameters parameters = camera.getParameters();
				parameters.setFocusMode("macro");
				camera.setParameters(parameters);
				// Trace.Log("Is support Zoom " + params.isZoomSupported());
			} catch (Exception e) {
				// Trace.Log("--------exception zoom");
//				Toast.makeText(mContext, "变焦失败", 0);
				e.printStackTrace();
			}
		} else {
			// Trace.Log("--------the phone not support zoom");
//			Toast.makeText(mContext, "手机不支持变焦", 0);
		}
	}

}
