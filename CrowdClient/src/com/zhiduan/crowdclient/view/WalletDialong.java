package com.zhiduan.crowdclient.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.CommandTools;

public class WalletDialong {

	private static Context mContext;
	private static Dialog dialog;

	public WalletDialong() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void show(Context context, String content, OnClickListener i) {
		mContext = context;
		WalletDialong importPassword = new WalletDialong();
		View view = importPassword.returnViewPassWordError(
				R.layout.dialog_password_error, content, i, 0);
		// dialog = ad.create();
		// dialog.setView(view,0,0,0,0);
		// dialog.show();
		dialog = new Dialog(mContext, R.style.dialog_no_border);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		window.getAttributes().width = CommandTools.dip2px(mContext, 300);
		dialog.show();
	}

	public static void show(Context context, String content) {
		mContext = context;
		WalletDialong importPassword = new WalletDialong();
		View view = importPassword.returnViewPassWordError(
				R.layout.dialog_password_error, content, null, 1);
		// dialog = ad.create();
		// dialog.setView(view,0,0,0,0);
		// dialog.show();
		dialog = new Dialog(mContext, R.style.dialog_no_border);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		window.getAttributes().width = CommandTools.dip2px(mContext, 300);
		dialog.show();
	}

	private View returnViewPassWordError(int dialogInputPassword, String mark,
			final OnClickListener i, int type) {
		View view = View.inflate(mContext, dialogInputPassword, null);
		TextView content = (TextView) view
				.findViewById(R.id.password_error_tv_content);
		TextView cancel = (TextView) view
				.findViewById(R.id.password_error_tv_cancel);
		ImageView xian = (ImageView) view
				.findViewById(R.id.password_error_iv_xian);
		TextView forgetPassword = (TextView) view
				.findViewById(R.id.password_error_tv_forgetpassword);
		content.setText(mark);
		cancel.setText("取消");
		forgetPassword.setText("确定");
		if (type == 1) {
			forgetPassword.setVisibility(View.GONE);
			xian.setVisibility(View.GONE);
			cancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (dialog != null) {
						dialog.dismiss();
					}
				}
			});
			return view;
		}
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog != null) {
					dialog.dismiss();
				}
				i.cancel();
			}
		});
		if (forgetPassword != null) {
			forgetPassword.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (dialog != null) {
						dialog.dismiss();
					}
					i.ok();
				}
			});
		}

		return view;
	}

	public interface OnClickListener {
		void cancel();

		void ok();
	}
}
