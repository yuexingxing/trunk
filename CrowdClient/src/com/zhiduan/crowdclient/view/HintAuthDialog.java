package com.zhiduan.crowdclient.view;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.R;

/**
 * <pre>
 * Description  权限提示对话框
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-5-25 上午10:01:08
 * </pre>
 */

public class HintAuthDialog {

	Context context;
	android.app.AlertDialog ad;
	TextView titleView, tv_auth_cancel, tv_auth_finish;
	TextView messageView;
	LinearLayout buttonLayout;

	public HintAuthDialog(Context context) {
		this.context = context;
		ad = new android.app.AlertDialog.Builder(context).create();
		ad.show();
		//自定义对话框窗口的布局
		Window window = ad.getWindow();
		window.setContentView(R.layout.dialog_hintauth);
		titleView = (TextView) window.findViewById(R.id.dialog_title);
		messageView = (TextView) window.findViewById(R.id.dialog_content);
		tv_auth_cancel = (TextView) window.findViewById(R.id.tv_auth_cancel);
		tv_auth_finish = (TextView) window.findViewById(R.id.tv_auth_finish);
	}

	public void setTitle(int resId) {
		titleView.setText(resId);
	}

	public void setTitle(String title) {
		titleView.setText(title);
	}

	public void setMessage(int resId) {
		messageView.setText(resId);
	}

	public void setMessage(String message) {
		messageView.setText(message);
	}

	/**
	 * 设置确定按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setPositiveButton(String text,
			final View.OnClickListener listener) {
		tv_auth_finish.setText(text);
		tv_auth_finish.setOnClickListener(listener);
	}

	/**
	 * 设置取消按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setNegativeButton(String text,
			final View.OnClickListener listener) {

		tv_auth_cancel.setText(text);
		tv_auth_cancel.setOnClickListener(listener);
	}

	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		ad.dismiss();
	}
}
