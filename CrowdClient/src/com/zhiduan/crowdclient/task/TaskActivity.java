package com.zhiduan.crowdclient.task;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.ScreenUtil;

/**
 * 
 * <pre>
 * Description	任务主界面
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-15 上午9:45:44
 * </pre>
 */
public class TaskActivity extends TabActivity {

	private Context mContext;
	private TabHost mainTabHost;
	private TabWidget tabWidget;
	private TextView tab_task_audit;
	private TextView tab_task_cancel;
	private TextView tab_task_payment;
	private TextView tab_task_underway;
	private View audit_line;
	private View cancel_line;
	private View payment_line;
	private View underway_line;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_task);
		mContext = this;
		setImmerseLayout();
		initMenu(3);
	}

	/**
	 * @param i
	 */
	@SuppressWarnings("deprecation")
	private void initMenu(int i) {

		// tab栏切换出现问题，这个不要删
		if (TextUtils.isEmpty(MyApplication.getInstance().m_userInfo.toKen)) {
			return;
		}

		TextView title_right = (TextView) findViewById(R.id.title_right);
		title_right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TaskActivity.this,
						PeakActivity.class);
				startActivity(intent);
			}
		});

		mainTabHost = this.getTabHost();
		tabWidget = this.getTabWidget();
		/**
		 * tabhost界面,自定义widget
		 */
		// 去除tabwidget分割线
		tabWidget.setDividerDrawable(null);
		// 进行中
		View underway_Tab = (View) LayoutInflater.from(this).inflate(
				R.layout.tabhost_widget_task, null);
		tab_task_underway = (TextView) underway_Tab.findViewById(R.id.tab_name);
		underway_line = underway_Tab.findViewById(R.id.bottom_line);
		tab_task_underway.setText("进行中");
		tab_task_underway.setTextColor(mContext.getResources().getColor(
				R.color.main_color));
		underway_line.setBackgroundResource(R.color.main_color);

		// 审核中
		View audit_Tab = (View) LayoutInflater.from(this).inflate(
				R.layout.tabhost_widget_task, null);
		tab_task_audit = (TextView) audit_Tab.findViewById(R.id.tab_name);
		audit_line = audit_Tab.findViewById(R.id.bottom_line);
		tab_task_audit.setText("审核中");

		// 已计算
		View payment_Tab = (View) LayoutInflater.from(this).inflate(
				R.layout.tabhost_widget_task, null);
		tab_task_payment = (TextView) payment_Tab.findViewById(R.id.tab_name);
		payment_line = payment_Tab.findViewById(R.id.bottom_line);
		tab_task_payment.setText("已结算");

		// 已取消
		View cancelTab = (View) LayoutInflater.from(this).inflate(
				R.layout.tabhost_widget_task, null);
		tab_task_cancel = (TextView) cancelTab.findViewById(R.id.tab_name);
		cancel_line = cancelTab.findViewById(R.id.bottom_line);
		tab_task_cancel.setText("已取消");
		mainTabHost.setup();

		mainTabHost.addTab(mainTabHost.newTabSpec("进行中")
				.setIndicator(underway_Tab)
				.setContent(new Intent(this, TaskUnderWayActivity.class)));

		mainTabHost.addTab(mainTabHost.newTabSpec("审核中")
				.setIndicator(audit_Tab)
				.setContent(new Intent(this, TaskAuditActivity.class)));

		mainTabHost.addTab(mainTabHost.newTabSpec("已结算")
				.setIndicator(payment_Tab)
				.setContent(new Intent(this, TaskPaymentActivity.class)));

		mainTabHost.addTab(mainTabHost.newTabSpec("已取消")
				.setIndicator(cancelTab)
				.setContent(new Intent(this, TaskCancelActivity.class)));

		// mainTabHost.setCurrentTab(0);
		/**
		 * tab的监听，改变icon与textcolor
		 */
		mainTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				if (tabId.equals("进行中")) {
					tab_task_underway.setTextColor(mContext.getResources()
							.getColor(R.color.main_color));
					tab_task_audit.setTextColor(mContext.getResources()
							.getColor(R.color.gray_4));
					tab_task_payment.setTextColor(mContext.getResources()
							.getColor(R.color.gray_4));
					tab_task_cancel.setTextColor(mContext.getResources()
							.getColor(R.color.gray_4));
					underway_line.setBackgroundResource(R.color.main_color);
					payment_line.setBackgroundResource(R.color.transparent);
					cancel_line.setBackgroundResource(R.color.transparent);
					audit_line.setBackgroundResource(R.color.transparent);

				} else if (tabId.equals("审核中")) {
					tab_task_underway.setTextColor(mContext.getResources()
							.getColor(R.color.gray_4));
					tab_task_audit.setTextColor(mContext.getResources()
							.getColor(R.color.main_color));
					tab_task_payment.setTextColor(mContext.getResources()
							.getColor(R.color.gray_4));
					tab_task_cancel.setTextColor(mContext.getResources()
							.getColor(R.color.gray_4));

					underway_line.setBackgroundResource(R.color.transparent);
					payment_line.setBackgroundResource(R.color.transparent);
					cancel_line.setBackgroundResource(R.color.transparent);
					audit_line.setBackgroundResource(R.color.main_color);

				} else if (tabId.equals("已结算")) {
					tab_task_underway.setTextColor(mContext.getResources()
							.getColor(R.color.gray_4));
					tab_task_audit.setTextColor(mContext.getResources()
							.getColor(R.color.gray_4));
					tab_task_payment.setTextColor(mContext.getResources()
							.getColor(R.color.main_color));
					tab_task_cancel.setTextColor(mContext.getResources()
							.getColor(R.color.gray_4));

					underway_line.setBackgroundResource(R.color.transparent);
					payment_line.setBackgroundResource(R.color.main_color);
					cancel_line.setBackgroundResource(R.color.transparent);
					audit_line.setBackgroundResource(R.color.transparent);
				} else if (tabId.equals("已取消")) {
					tab_task_underway.setTextColor(mContext.getResources()
							.getColor(R.color.gray_4));
					tab_task_audit.setTextColor(mContext.getResources()
							.getColor(R.color.gray_4));
					tab_task_payment.setTextColor(mContext.getResources()
							.getColor(R.color.gray_4));
					tab_task_cancel.setTextColor(mContext.getResources()
							.getColor(R.color.main_color));

					underway_line.setBackgroundResource(R.color.transparent);
					payment_line.setBackgroundResource(R.color.transparent);
					cancel_line.setBackgroundResource(R.color.main_color);
					audit_line.setBackgroundResource(R.color.transparent);
				}
			}

		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		MyApplication.baseActivity = this;
	}

	protected void setImmerseLayout(View view) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			int statusBarHeight = ScreenUtil.getStatusBarHeight(this
					.getBaseContext());
			view.setPadding(0, statusBarHeight, 0, 0);
		}
	}

	protected void setImmerseLayout() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

}
