package com.zhiduan.crowdclient.view;

import com.google.zxing.WriterException;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Logs;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

/** 
 * 确认取件弹出二维码供货源扫描
 * 
 * @author yxx
 *
 * @date 2016-5-3 下午10:00:31
 * 
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class QRcodeScanDialog{

	private static Context mContext;
	public static boolean isShow = false;
	private static Dialog dialog;
	private static ImageView imgCode;
	private static Bitmap mBitmap;

	// 弹窗结果回调函数
	public static abstract class ScanResultCallback {
		public abstract void callback(int position);
	}

	public QRcodeScanDialog(Context context){

	}

	/**
	 * @param context
	 * @param strMessage
	 */
	public static void showMyDialog(Context context, final String strMsg, final ScanResultCallback callback){

		mContext = context;

		if(dialog != null){
			dialog.dismiss();
		}

		dialog = new Dialog(context, R.style.dialog);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_qrcode_scan, null);

		imgCode = (ImageView) layout.findViewById(R.id.two_dimension_code_iv_code);

		String strUrl = MyApplication.getInstance().m_userInfo.m_strUserHeadPic;

		Logs.v("result", strMsg);
//		ImageLoader loader=ImageLoader.getInstance();
//		loader.loadImage(strUrl, new ImageLoadingListener() {
//
//			@Override
//			public void onLoadingStarted(String arg0, View arg1) {
//				// TODO Auto-generated method stub
//				CustomProgress.showDialog(mContext, "二维码生成中", true, null);
//			}
//
//			@Override
//			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
//				// TODO Auto-generated method stub
//				Message msg = new Message();
//				msg.obj = strMsg;
//				msg.what = 0;
//				myHandler.sendMessage(msg);
//			}
//
//			@Override
//			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
//				// TODO Auto-generated method stub
//				mBitmap = arg2;
//
//				Message msg = new Message();
//				msg.obj = strMsg;
//				msg.what = 0;
//				myHandler.sendMessage(msg);
//			}
//
//			@Override
//			public void onLoadingCancelled(String arg0, View arg1) {
//				// TODO Auto-generated method stub
//				Message msg = new Message();
//				msg.obj = strMsg;
//				msg.what = 0;
//				myHandler.sendMessage(msg);
//			}
//		});
		
		Message msg = new Message();
		msg.obj = strMsg;
		msg.what = 0;
		myHandler.sendMessage(msg);

		Button btnOk = (Button) layout.findViewById(R.id.btn_dialog_qrcode_scan_ok);

		ImageView img = (ImageView) layout.findViewById(R.id.btn_dialog_qrcode_scan_cancel);
		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				dialog.dismiss();
			}
		});

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				dialog.dismiss();
				callback.callback(1);
			}
		});

		dialog.setContentView(layout);

		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);

		dialog.show();
		setDialogWindowAttr(dialog, context);
		isShow = true;
	}

	/**
	 * 关闭窗口
	 */
	public static void closeDialog(){

		if(dialog != null){
			dialog.dismiss();
		}
	}

	//在dialog.show()之后调用
	public static void setDialogWindowAttr(Dialog dlg,Context ctx){

		WindowManager wm = ((Activity) ctx).getWindowManager();
		Display display = wm.getDefaultDisplay(); // 为获取屏幕宽、高

		Window window = dlg.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.width = (int) (display.getWidth()/1.5);
		lp.height = (int) (display.getHeight()/2);
		dlg.getWindow().setAttributes(lp);
	}

	private static float imagt_halfWidth = 20;
	static Handler myHandler = new Handler() {

		public void handleMessage(Message msg) {
			
			CustomProgress.dissDialog();
			if (msg.what == 0) {

				if(mBitmap == null){
					mBitmap = BitmapFactory.decodeResource(mContext.getResources(),
							R.drawable.login_logo);
				}


				Matrix m = new Matrix();
				float sx = (float) 2 * imagt_halfWidth / mBitmap.getWidth();
				float sy = (float) 2 * imagt_halfWidth / mBitmap.getHeight();
				m.setScale(sx, sy);

				mBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
						mBitmap.getWidth(), mBitmap.getHeight(), m, false);

				try {
					Bitmap creatTwoBitMap = CommandTools.creatTwoBitMap(
							(String)msg.obj, mBitmap);
					imgCode.setBackground(new BitmapDrawable(creatTwoBitMap));

				} catch (WriterException e) {
					e.printStackTrace();
				}
			}
		}
	};
}
