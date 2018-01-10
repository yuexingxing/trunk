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
 * 确认签收对话框
 * 
 * @author yxx
 *
 * @date 2016-5-3 下午10:00:31
 * 
 */
public class SureSignDialog{

	private static Context mContext;
	public static boolean isShow = false;
	static Dialog dialog;

	// 弹窗结果回调函数
	public static abstract class SignCallback {
		public abstract void callback(boolean result);
	}

	public SureSignDialog(Context context){

	}

	/**
	 * @param context
	 * @param strMessage
	 */
	public static void showMyDialog(Context context, final SignCallback callback){

		if(dialog != null){
			dialog.dismiss();
		}

		mContext = context;
		if(mContext == null){
			return;
		}

		dialog = new Dialog(mContext, R.style.dialog);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_sure_sign, null);

		TextView btnOK = (TextView) layout.findViewById(R.id.tv_sure_sign_ok);
		TextView btnCancel = (TextView) layout.findViewById(R.id.tv_sure_sign_cancel);

		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				callback.callback(true);
				dialog.dismiss();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				callback.callback(false);
				dialog.dismiss();
			}
		});

		dialog.setContentView(layout);

		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		try{
			dialog.show();
			setDialogWindowAttr(dialog, mContext);
			isShow = true;
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}

	/**
	 * 关闭窗口
	 */
	public static void closeDialog(){

		SureSignDialog.isShow = false;
		if(dialog != null){
			dialog.dismiss();
		}
	}

	//在dialog.show()之后调用
	@SuppressWarnings("deprecation")
	public static void setDialogWindowAttr(Dialog dlg,Context ctx){

		WindowManager wm = ((Activity) ctx).getWindowManager();
		Display display = wm.getDefaultDisplay(); // 为获取屏幕宽、高

		Window window = dlg.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.width = (int) (display.getWidth()/1.5);
		lp.height = (int) (display.getHeight()/4);
		dlg.getWindow().setAttributes(lp);
	}

}
