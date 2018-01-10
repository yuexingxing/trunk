package com.zhiduan.crowdclient.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.R;

public class CalendarWindows extends PopupWindow {
	public String date;
	public static CalendarWindows calendarWindows;

	public static CalendarWindows showWindows(Context mContext, View view,
			CalendarClickListener c) {
		if (calendarWindows == null) {
			calendarWindows = new CalendarWindows(mContext);
		}else{
			calendarWindows.dismiss();
		}
		calendarWindows.show(mContext, view, c);
		return calendarWindows;

	}

	private CalendarWindows(Context mContext) {

		super(mContext);
	}

	private void show(Context mContext, View parent, final CalendarClickListener c) {
		dismiss();

		View view = View.inflate(mContext, R.layout.popupwindow_calendar, null);
		view.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.fade_in));
		LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in_1));

		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.FILL_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		update();

		final TextView popupwindow_calendar_month = (TextView) view.findViewById(R.id.popupwindow_calendar_month);
		final KCalendar calendar = (KCalendar) view.findViewById(R.id.popupwindow_calendar);
		Button popupwindow_calendar_bt_enter = (Button) view.findViewById(R.id.popupwindow_calendar_bt_enter);

		final RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.layout_calendar);

		popupwindow_calendar_month.setText(calendar.getCalendarYear() + "年" + calendar.getCalendarMonth() + "月");

		if (null != date) {
			int years = Integer.parseInt(date.substring(0, date.indexOf("-")));
			int month = Integer.parseInt(date.substring(date.indexOf("-") + 1,
					date.lastIndexOf("-")));
			popupwindow_calendar_month.setText(years + "年" + month + "月");

			calendar.showCalendar(years, month);
			calendar.setCalendarDayBgColor(date,
					R.drawable.calendar_date_focused);
		}

		// 监听所选中的日期
		calendar.setOnCalendarClickListener(new KCalendar.OnCalendarClickListener() {
			public void onCalendarClick(int row, int col, String dateFormat) {
				int month = Integer.parseInt(dateFormat.substring(
						dateFormat.indexOf("-") + 1,
						dateFormat.lastIndexOf("-")));

				if (calendar.getCalendarMonth() - month == 1
						|| calendar.getCalendarMonth() - month == -11) {// 跨年跳转
					calendar.lastMonth();

				} else if (month - calendar.getCalendarMonth() == 1
						|| month - calendar.getCalendarMonth() == -11) {// 跨年跳转
					calendar.nextMonth();

				} else {
					calendar.removeAllBgColor();
					calendar.setCalendarDayBgColor(dateFormat,
							R.drawable.calendar_date_focused);
					date = dateFormat;// 最后返回给全局 date

					c.pitchOnDate(date);

					dismiss();
				}
			}
		});

		// 监听当前月份
		calendar.setOnCalendarDateChangedListener(new KCalendar.OnCalendarDateChangedListener() {
			public void onCalendarDateChanged(int year, int month) {
				popupwindow_calendar_month.setText(year + "年" + month + "月");
			}
		});

		// 上月监听按钮
		RelativeLayout popupwindow_calendar_last_month = (RelativeLayout) view
				.findViewById(R.id.popupwindow_calendar_last_month);
		popupwindow_calendar_last_month
		.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				calendar.lastMonth();
			}
		});

		// 下月监听按钮
		RelativeLayout popupwindow_calendar_next_month = (RelativeLayout) view
				.findViewById(R.id.popupwindow_calendar_next_month);
		popupwindow_calendar_next_month
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				calendar.nextMonth();
			}
		});

		// 关闭窗口
		popupwindow_calendar_bt_enter.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});

		//点击外部关闭窗口
		layout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
	}


	public interface CalendarClickListener {
		void pitchOnDate(String date);
	}
}
