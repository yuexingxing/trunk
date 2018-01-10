package com.zhiduan.crowdclient.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.CommandTools;

/**
 * 
 * <pre>
 * Description	显示提成说明
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-11-5 下午3:33:16  
 * </pre>
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ShowVersionDialog{

	private static Context mContext;
	public static boolean isShow = false;
	static Dialog dialog;

	public static abstract class PayResultCallback {
		public abstract void callback(int pos);
	}

	public ShowVersionDialog(Context context){

	}

	/**
	 * @param context
	 * @param strMessage
	 */
	public static void showMyDialog(Context context, int layougId){
		mContext = context;
		
		if (mContext == null) {
			return;
		}

		if (dialog != null) {
			dialog.dismiss();
		}

		dialog = new Dialog(context, R.style.dialog);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(layougId, null);
		dialog.setContentView(layout);
		dialog.getWindow().setLayout(CommandTools.dip2px(context, 800), CommandTools.dip2px(context,320));
		Button btnOk = (Button) layout.findViewById(R.id.btn_dialog_get);
		
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(dialog != null){
					dialog.dismiss();
				}
			}
		});

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
	@SuppressWarnings("deprecation")
	public static void setDialogWindowAttr(Dialog dlg, Context ctx){

		WindowManager wm = ((Activity) ctx).getWindowManager();
		Display display = wm.getDefaultDisplay(); // 为获取屏幕宽、高
		Window window = dlg.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.width = (int) (display.getWidth()/1.4);
		lp.height = (int) (display.getHeight()/2);
		dlg.getWindow().setAttributes(lp);
	}

}
