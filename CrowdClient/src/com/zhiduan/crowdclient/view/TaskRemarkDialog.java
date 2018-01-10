package com.zhiduan.crowdclient.view;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.R;

/**
 * <pre>
 * Description  选择信息对话框
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-5-25 上午10:01:08
 * </pre>
 */

public class TaskRemarkDialog {
	private android.app.AlertDialog ad;
	private TextView titleView, tv_dialog_content, tv_dialog_single_ok;
	private RelativeLayout rl_remark;

	LinearLayout buttonLayout, ll_dialog_content;

	public TaskRemarkDialog(Context context) {
		ad = new android.app.AlertDialog.Builder(context).create();
		ad.show();
		// 自定义对话框窗口的布局
		Window window = ad.getWindow();
		window.setContentView(R.layout.dialog_task_remark);
		// 单个按钮的对话框
		// 对话框文本布局
		rl_remark = (RelativeLayout) window.findViewById(R.id.rl_remark);
		ll_dialog_content = (LinearLayout) window.findViewById(R.id.ll_dialog_content);
		// 文本内容
		tv_dialog_content = (TextView) window.findViewById(R.id.tv_dialog_content);
		// 单个确认按钮
		tv_dialog_single_ok = (TextView) window.findViewById(R.id.tv_dialog_single_ok);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();
		LayoutParams params = rl_remark.getLayoutParams();
		params.height = height / 2;
		// 对话框标题
		titleView = (TextView) window.findViewById(R.id.tv_dialog_title);
		tv_dialog_content.setMovementMethod(new ScrollingMovementMethod());
	}

	public void setTitle(int resId) {
		titleView.setText(resId);
	}

	public void setTitle(String title) {
		titleView.setText(title);
	}

	/**
	 * 设置按钮点击事件
	 * 
	 * @param text
	 * @param listener
	 */
	public void setSingleOKButton(String text, View.OnClickListener listener) {
		tv_dialog_single_ok.setText(text);
		tv_dialog_single_ok.setOnClickListener(listener);
	}

	/**
	 * 设置对话框文本
	 * 
	 * @param text
	 */
	public void setMessage(String text) {
		tv_dialog_content.setText("    " + text);
	}

	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		ad.dismiss();
	}
}
