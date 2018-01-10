package com.zhiduan.crowdclient.view.dialog;

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
 * 单选对话框
 * 
 * @author yxx
 *
 * @date 2016-9-27 上午10:48:02
 * 
 */
public class SingleChoiceDialog{

	private static Context mContext;
	public static boolean isShow = false;
	static Dialog dialog;

	// 弹窗结果回调函数
	public static abstract class SingleCallback {
		public abstract void callback(int position);
	}

	public SingleChoiceDialog(Context context){

	}

	/**
	 * @param context
	 * @param leftBtn	左侧按钮文字
	 * @param rightBtn	右侧按钮文字
	 * @param content	显示说明
	 * @param callback	回调(0,1)
	 */
	public static void showMyDialog(Context context, String leftBtn, String rightBtn, String content, final SingleCallback callback){

		if(dialog != null){
			dialog.dismiss();
		}

		mContext = context;
		if(mContext == null){
			return;
		}

		dialog = new Dialog(mContext, R.style.dialog);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_single_choice, null);

		TextView tvContent = (TextView) layout.findViewById(R.id.tv_single_choice_content);
		TextView btnCancel = (TextView) layout.findViewById(R.id.tv_single_choice_cancel);
		TextView btnOK = (TextView) layout.findViewById(R.id.tv_single_choice_ok);
		
		tvContent.setText(content + "");
		btnCancel.setText(leftBtn + "");
		btnOK.setText(rightBtn + "");
		
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				callback.callback(0);
				dialog.dismiss();
			}
		});
		
		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				callback.callback(1);
				dialog.dismiss();
			}
		});

		dialog.setContentView(layout);

		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(true);

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

		SingleChoiceDialog.isShow = false;
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
		lp.width = (int) (display.getWidth()/1.2);
		lp.height = (int) (display.getHeight()/4);
		dlg.getWindow().setAttributes(lp);
	}

}
