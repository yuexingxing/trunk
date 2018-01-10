package com.zhiduan.crowdclient.view;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.CommandTools;

/** 
 * 二维码支付界面
 * 
 * @author yxx
 *
 * @date 2016-5-3 下午10:00:31
 * 
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class QRcodePay{

	private static Context mContext;
	public static boolean isShow = false;
	static Dialog dialog;
	private static ImageView imgCode;

	// 弹窗结果回调函数
	public static abstract class ResultCallback {
		public abstract void callback(boolean result);
	}

	public QRcodePay(Context context){

	}

	/**
	 * @param context
	 * @param strMessage
	 */
	public static void showMyDialog(Context context, String fee, String url){

		mContext = context;

		if(dialog != null){
			dialog.dismiss();
		}

		Message msg = new Message();
		msg.obj = url;
		msg.what = 0;
		myHandler.sendMessage(msg);

		dialog = new Dialog(context, R.style.dialog);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_qrcode_pay, null);

		TextView tvFee = (TextView) layout.findViewById(R.id.two_dimension_code_tv_money);
		imgCode = (ImageView) layout.findViewById(R.id.two_dimension_code_iv_code);

		String strFee = context.getResources().getString(R.string.fee); 
		String strMsg = String.format(strFee, fee); 
		tvFee.setText(strMsg);

		dialog.setContentView(layout);

		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

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
			if (msg.what == 0) {
				Bitmap mBitmap = BitmapFactory.decodeResource(mContext.getResources(),
						R.drawable.login_logo);

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
