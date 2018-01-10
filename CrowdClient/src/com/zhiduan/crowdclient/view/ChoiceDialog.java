package com.zhiduan.crowdclient.view;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
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

public class ChoiceDialog {
	private android.app.AlertDialog ad;
	private TextView titleView, tv_dialog_content, tv_dialog_single_ok;
	private TextView tv_dialog_left_sfz, tv_dialog_right_sfz;
	private LinearLayout iv_dialog_delete;
	LinearLayout buttonLayout, ll_dialog_content, ll_cancel_ok_sex,
			ll_cancel_ok_sfz, ll_dialog_left, ll_dialog_right;

	// private View dialog_title_line;

	public ChoiceDialog(Context context) {
		ad = new android.app.AlertDialog.Builder(context).create();
		ad.show();
		// 自定义对话框窗口的布局
		Window window = ad.getWindow();
		window.setContentView(R.layout.dialog_choice);
		// 单个按钮的对话框
		ll_cancel_ok_sex = (LinearLayout) window.findViewById(R.id.ll_cancel_ok_sex);
		ll_cancel_ok_sfz = (LinearLayout) window
				.findViewById(R.id.ll_cancel_ok_sfz);

		tv_dialog_right_sfz = (TextView) window
				.findViewById(R.id.tv_dialog_right_sfz);
		tv_dialog_left_sfz = (TextView) window
				.findViewById(R.id.tv_dialog_left_sfz);
		// 对话框文本布局
		ll_dialog_content = (LinearLayout) window
				.findViewById(R.id.ll_dialog_content);
		// 文本内容
		tv_dialog_content = (TextView) window
				.findViewById(R.id.tv_dialog_content);
		// 单个确认按钮
		tv_dialog_single_ok = (TextView) window
				.findViewById(R.id.tv_dialog_single_ok);
		// 对话框标题
		titleView = (TextView) window.findViewById(R.id.tv_dialog_title);
		// 左边按钮
		ll_dialog_left = (LinearLayout) window
				.findViewById(R.id.ll_dialog_left);
		// 右边按钮
		ll_dialog_right = (LinearLayout) window
				.findViewById(R.id.ll_dialog_right);
		// 删除图标
		iv_dialog_delete = (LinearLayout) window
				.findViewById(R.id.iv_dialog_delete);

		// dialog_title_line = (View) window
		// .findViewById(R.id.dialog_title_line);
	}

	// 使用选择身份
	public void UseSex() {
		ll_cancel_ok_sfz.setVisibility(View.GONE);
		ll_cancel_ok_sex.setVisibility(View.VISIBLE);
	}

	// 使用选择性别
	public void UseSfz() {
		ll_cancel_ok_sex.setVisibility(View.GONE);
		ll_cancel_ok_sfz.setVisibility(View.VISIBLE);
	}

	// 使用单个按钮的对话框
	public void UseSingleButton() {
		ll_cancel_ok_sfz.setVisibility(View.GONE);
		ll_cancel_ok_sex.setVisibility(View.GONE);
		tv_dialog_single_ok.setVisibility(View.VISIBLE);
		ll_dialog_content.setVisibility(View.VISIBLE);
		// dialog_title_line.setVisibility(View.INVISIBLE);
	}

	// 使用两个按钮的对话框
	public void UseCancelOKButton() {
		ll_cancel_ok_sfz.setVisibility(View.VISIBLE);
		ll_cancel_ok_sex.setVisibility(View.VISIBLE);
		tv_dialog_single_ok.setVisibility(View.GONE);
		ll_dialog_content.setVisibility(View.GONE);

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
	public void setDeleteButton(View.OnClickListener listener) {
		iv_dialog_delete.setOnClickListener(listener);
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
		tv_dialog_content.setText(text);
	}

	/**
	 * 设置对话框文本
	 * 
	 * @param text
	 */
	public void setMessageColor(int i) {
		tv_dialog_content.setTextColor(i);
	}

	/**
	 * 设置右边按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setRightButton(String text, final View.OnClickListener listener) {
		tv_dialog_right_sfz.setText(text);
		tv_dialog_right_sfz.setOnClickListener(listener);
	}

	/**
	 * 设置左边按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setLeftButton(String text, final View.OnClickListener listener) {

		tv_dialog_left_sfz.setText(text);
		tv_dialog_left_sfz.setOnClickListener(listener);
	}

	/**
	 * 设置确定按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setPositiveButton(String text,
			final View.OnClickListener listener) {
		ll_dialog_left.setOnClickListener(listener);
	}

	/**
	 * 设置取消按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setNegativeButton(String text,
			final View.OnClickListener listener) {

		ll_dialog_right.setOnClickListener(listener);
	}

	/**
	 * 设置当前性别突出显示
	 */
	public void setCurrentStatus() {

		if (MyApplication.getInstance().m_userInfo.m_strUserGender.equals("女")) {
			ll_dialog_left.setBackground(MyApplication.getInstance()
					.getResources().getDrawable(R.drawable.shape_radius_gray));
			ll_dialog_right.setBackground(MyApplication.getInstance()
					.getResources()
					.getDrawable(R.drawable.shape_radius_order_main_color));
		} else if (MyApplication.getInstance().m_userInfo.m_strUserGender
				.equals("男")) {
			ll_dialog_left.setBackground(MyApplication.getInstance()
					.getResources()
					.getDrawable(R.drawable.shape_radius_order_main_color));
			ll_dialog_right.setBackground(MyApplication.getInstance()
					.getResources().getDrawable(R.drawable.shape_radius_gray));
		}
	}

	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		ad.dismiss();
		
	}
}
