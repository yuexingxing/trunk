package com.zhiduan.crowdclient.view;

import com.google.zxing.WriterException;
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
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * 二维码支付界面
 * 
 * @author yxx
 *
 * @date 2016-5-3 下午10:00:31
 * 
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class QRcodePayDialog{

	private static Context mContext;
	public static boolean isShow = false;
	static Dialog dialog;
	private static ImageView imgCode;

	/** 
	 * 选择支付方式
	 * 0为线上支付
	 * 1为现金支付
	 */
	public static abstract class PayResultCallback {
		public abstract void callback(int pos);
	}

	public QRcodePayDialog(Context context){

	}

	/**
	 * @param context
	 * @param strMessage
	 */
	public static void showMyDialog(Context context, String fee, String url, final PayResultCallback callback){

		Logs.v("result", url);
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
		TextView tvFee2 = (TextView) layout.findViewById(R.id.tv_qrcode_pay_fee2);
		final TextView tvTitle = (TextView) layout.findViewById(R.id.tv_qrcode_pay_title);
		imgCode = (ImageView) layout.findViewById(R.id.two_dimension_code_iv_code);

		ImageView img = (ImageView) layout.findViewById(R.id.img_pay_close);
		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				dialog.dismiss();
			}
		});

		String strFee = context.getResources().getString(R.string.fee); 
		String strMsg = String.format(strFee, fee); 
		
		tvFee.setText(strMsg);//扫码支付金额
		tvFee2.setText(strMsg);//现金交易金额
		
		LinearLayout layoutLeft = (LinearLayout) layout.findViewById(R.id.layout_qrcode_pay_left);
		LinearLayout layoutrRight = (LinearLayout) layout.findViewById(R.id.layout_qrcode_pay_right);
		
		final LinearLayout layoutPay1 = (LinearLayout) layout.findViewById(R.id.layout_qrcode_pay_1);
		final LinearLayout layoutPay2 = (LinearLayout) layout.findViewById(R.id.layout_qrcode_pay_2);
		
		final ImageView imgLeft= (ImageView) layout.findViewById(R.id.img_qrcode_pay_left);
		final ImageView imgRight= (ImageView) layout.findViewById(R.id.img_qrcode_pay_right);
		
		Button btnOk = (Button) layout.findViewById(R.id.btn_qrcode_pay_ok);
		
		tvTitle.setText("线上收款");
		layoutPay1.setVisibility(View.VISIBLE);
		layoutPay2.setVisibility(View.GONE);
		
		imgLeft.setImageResource(R.drawable.tabbar_left_tow);
		imgRight.setImageResource(R.drawable.tabbar_right_one);
		
		layoutLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				tvTitle.setText("线上收款");
				layoutPay1.setVisibility(View.VISIBLE);
				layoutPay2.setVisibility(View.GONE);
				
				imgLeft.setImageResource(R.drawable.tabbar_left_tow);
				imgRight.setImageResource(R.drawable.tabbar_right_one);
			}
		});

		layoutrRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				tvTitle.setText("现金收款");
				layoutPay2.setVisibility(View.VISIBLE);
				layoutPay1.setVisibility(View.GONE);
				
				imgLeft.setImageResource(R.drawable.tabbar_left_one);
				imgRight.setImageResource(R.drawable.tabbar_right_tow);
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
		lp.width = (int) (display.getWidth()/1.2);
		lp.height = (int) (display.getHeight()/1.5);
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
