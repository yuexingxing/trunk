package com.zhiduan.crowdclient.view;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.CommandTools;

/** 
 * 单点登录提示窗口（该账号在另一设备登录）
 * 
 * @author yxx
 *
 * @date 2016-5-3 下午10:00:31
 * 
 */
public class SingleLoginDialog{

	public static boolean isShow = false;
	static Dialog dialog;

	// 弹窗结果回调函数
	public static abstract class ResultCallback {
		public abstract void callback(boolean result);
	}

	public SingleLoginDialog(Context context){

	}

	/**
	 * @param context
	 * @param strMessage
	 */
	public static void showMyDialog(Context context, String title, String content, String buttonTxt, final ResultCallback callback){

		dialog = null;
		if(dialog != null){
			dialog.dismiss();
		}
		
		if(context == null){
			return;
		}

		dialog = new Dialog(CommandTools.getGlobalActivity(), R.style.dialog);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_single_login, null);

		Button btnOK = (Button) layout.findViewById(R.id.btn_single_login_ok);
		TextView dialogTitle = (TextView) layout.findViewById(R.id.dialog_title);
		TextView dialogContent = (TextView) layout.findViewById(R.id.dialog_content);
		ImageView img = (ImageView) layout.findViewById(R.id.imageView1);

		dialogTitle.setText(title);
		dialogContent.setText(content);
		btnOK.setText(buttonTxt);
		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				callback.callback(true);
				dialog.dismiss();
			}
		});
		
		img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		dialog.setContentView(layout);

		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		try{
			dialog.show();
			isShow = true;
			setDialogWindowAttr(dialog, context);
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}

	/**
	 * 关闭窗口
	 */
	public static void closeDialog(){

		SingleLoginDialog.isShow = false;
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
		lp.height = (int) (display.getHeight()/3);
		dlg.getWindow().setAttributes(lp);
	}

}
