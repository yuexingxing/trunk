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
import android.widget.TextView;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.CommandTools;

/**
 * @author hexiuhui
 * 
 */
public class ReviewTaskDialog {

	public static boolean isShow = false;
	static Dialog dialog;

	// 弹窗结果回调函数
	public static abstract class ResultCallback {
		public abstract void callback(int position);
	}

	public ReviewTaskDialog(Context context) {

	}

	public static void showTaskDialog(Context context, final ResultCallback resultCallback) {
		showMyDialog(context, resultCallback);
	}

	/**
	 * @param context
	 * @param strMessage
	 */
	public static void showMyDialog(Context context, final ResultCallback resultCallback) {
		if (dialog != null) {
			dialog.dismiss();
		}

		if (context == null) {
			return;
		}

		dialog = new Dialog(CommandTools.getGlobalActivity(), R.style.dialog);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.system_task_review, null);

		TextView tv_dialog_cancel = (TextView) layout.findViewById(R.id.tv_dialog_cancel);

		tv_dialog_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				resultCallback.callback(-1);
			}
		});

		dialog.setContentView(layout);

		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

		try{
			dialog.show();
			setDialogWindowAttr(dialog, context);
			isShow = true;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 关闭窗口
	 */
	public static void closeDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	// 在dialog.show()之后调用
	public static void setDialogWindowAttr(Dialog dlg, Context ctx) {
		WindowManager wm = ((Activity) ctx).getWindowManager();
		Display display = wm.getDefaultDisplay(); // 为获取屏幕宽、高

		Window window = dlg.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.width = (int) (display.getWidth() / 1.5);
		lp.height = (int) (display.getHeight() / 2.5);
		dlg.getWindow().setAttributes(lp);
	}
}
