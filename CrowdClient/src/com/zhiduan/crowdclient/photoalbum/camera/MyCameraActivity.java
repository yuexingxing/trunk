package com.zhiduan.crowdclient.photoalbum.camera;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zhiduan.crowdclient.R;

public class MyCameraActivity extends FragmentActivity implements
		OnClickListener {
	private int flashlight = 0;// ����ƣ�0�أ�1��
	private MyCameraFragment myCamera;
	private MyCameraQueenFragment cameram;
	private int i = 1;
	private long replaceTime = 0;
	private RelativeLayout mRlReversalCanera;
	private ImageView mRlReversalCanera1;
	private int widthWindow;
	private int heightWindow;
	private float xOneStart;
	private float yOneStart;
	private float xTwoStart;
	private float yTwoStart;
	private float xOneEnd;
	private float yOneEnd;
	private float xTwoEnd;
	private float yTwoEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ��������
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// ȥ����Ϣ��
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		widthWindow = metric.widthPixels;
		heightWindow = metric.heightPixels;
		setContentView(R.layout.aa_my);
		// 默认摄像头
		myCamera = new MyCameraFragment();
		// 前摄像头
		cameram = new MyCameraQueenFragment();
		RelativeLayout aaa = (RelativeLayout) findViewById(R.id.my_camera_rl_queen);
		mRlReversalCanera = (RelativeLayout) findViewById(R.id.my_camera_rl_reversal_canera);
		RelativeLayout kuang = (RelativeLayout) findViewById(R.id.my_camera_kuang);
		mRlReversalCanera1 = (ImageView) findViewById(R.id.my_camera_rl_reversal_canera1);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();// ����Fragment����
		transaction.replace(R.id.aaaa, myCamera);
		transaction.commit();

		if (!hasFrontFacingCamera()) {
			aaa.setVisibility(View.GONE);
		}
		if (!hasBackFacingCamera()) {
			aaa.setVisibility(View.GONE);
		}
		if (null != aaa) {
			aaa.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					FragmentManager fm = getSupportFragmentManager();
					FragmentTransaction transaction = fm.beginTransaction();// ����Fragment����

					if (i == 0) {
						long stTime = System.currentTimeMillis();
						if (stTime - replaceTime > 1000) {
							transaction.replace(R.id.aaaa, myCamera);
						}
						i = 1;
					} else if (i == 1) {
						long stTime = System.currentTimeMillis();
						if (stTime - replaceTime > 1000) {
							transaction.replace(R.id.aaaa, cameram);
						}
						// �ر������
						if (myCamera.getCamera() != null) {
							Parameters parameters = myCamera.getCamera()
									.getParameters();
							parameters
									.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
							myCamera.getCamera().setParameters(parameters);
						}
						mRlReversalCanera1
								.setBackgroundResource(R.drawable.camera_flashlight_open);
						flashlight = 0;
						i = 0;
					}

					transaction.commit();
				}
			});
		}

		if (isFlashlight()) {
			// �������
			mRlReversalCanera.setOnClickListener(this);
			mRlReversalCanera.setVisibility(View.VISIBLE);
		} else {
			// û�������
			mRlReversalCanera.setVisibility(View.GONE);
			
			if (!hasFrontFacingCamera()) {
				kuang.setVisibility(View.GONE);
			}
			if (!hasBackFacingCamera()) {
				kuang.setVisibility(View.GONE);
			}

		}
	}

	// û�������
	private boolean isFlashlight() {
		return getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_FLASH);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.my_camera_rl_reversal_canera:
			if (i == 0) {
				return;
			}
			// �����
			if (flashlight == 0) {
				// ��
				if (myCamera.getCamera() != null) {
					Parameters parameters = myCamera.getCamera()
							.getParameters();
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
					myCamera.getCamera().setParameters(parameters);
				}
				mRlReversalCanera1
						.setBackgroundResource(R.drawable.camera_flashlight_close);
				flashlight = 1;
			} else {
				// �ر�
				if (myCamera.getCamera() != null) {
					Parameters parameters = myCamera.getCamera()
							.getParameters();
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
					myCamera.getCamera().setParameters(parameters);
				}
				mRlReversalCanera1
						.setBackgroundResource(R.drawable.camera_flashlight_open);
				flashlight = 0;
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mRlReversalCanera1
				.setBackgroundResource(R.drawable.camera_flashlight_open);
		flashlight = 0;
	}

	private static boolean checkCameraFacing(final int facing) {
		if (getSdkVersion() < Build.VERSION_CODES.GINGERBREAD) {
			return false;
		}
		final int cameraCount = Camera.getNumberOfCameras();
		CameraInfo info = new CameraInfo();
		for (int i = 0; i < cameraCount; i++) {
			Camera.getCameraInfo(i, info);
			if (facing == info.facing) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasBackFacingCamera() {
		final int CAMERA_FACING_BACK = 0;
		return checkCameraFacing(CAMERA_FACING_BACK);
	}

	public static boolean hasFrontFacingCamera() {
		final int CAMERA_FACING_BACK = 1;
		return checkCameraFacing(CAMERA_FACING_BACK);
	}

	public static int getSdkVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}
}
