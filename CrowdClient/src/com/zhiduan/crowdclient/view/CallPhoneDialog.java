package com.zhiduan.crowdclient.view;

import com.zhiduan.crowdclient.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/** 
 * 拨打电话确认对话框
 * 
 * @author yxx
 *
 * @date 2016-5-3 下午10:00:31
 * 
 */
public class CallPhoneDialog{

	public static boolean isShow = false;
	static Dialog dialog;

	// 弹窗结果回调函数
	public static abstract class ResultCallback {
		public abstract void callback(int position);
	}

	public CallPhoneDialog(Context context){

	}

	/**
	 * @param context
	 * @param strMessage
	 */
	public static void showMyDialog(Context context, String phone, final ResultCallback resultCallback){

		if(dialog != null){
			dialog.dismiss();
		}

		dialog = new Dialog(context, R.style.dialog);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_call_phone, null);
		
		TextView tvPhone = (TextView) layout.findViewById(R.id.tv_call_phone);
		tvPhone.setText(phone);
		
		TextView tvCancel = (TextView) layout.findViewById(R.id.tv_call_cancel);
		TextView tvOk = (TextView) layout.findViewById(R.id.tv_call_ok);
		
		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				dialog.dismiss();
				resultCallback.callback(-1);
			}
		});
		
		tvOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				dialog.dismiss();
				resultCallback.callback(1);
			}
		});

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
		lp.height = (int) (display.getHeight()/5);
		dlg.getWindow().setAttributes(lp);
	}

}
