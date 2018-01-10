package com.zhiduan.crowdclient.zxing.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * <pre>
 * Description	倒计时的textview
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-11-15 下午4:04:05
 * </pre>
 */

public class TimerTextView extends TextView implements Runnable {// 时间变量
	private int day, hour, minute, second;
	// 当前计时器是否运行
	private boolean isRun = false;

	public TimerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public TimerTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TimerTextView(Context context) {
		super(context);
	}
	public void setTextStr(String str) {
		this.setText(str);
	}
	/**
	 * 将倒计时时间毫秒数转换为自身变量
	 * 
	 * @param time
	 *            时间间隔毫秒数
	 */
	public void setTimes(long time) {
		//将毫秒数转化为时间
				this.second = (int) (time / 1000) % 60;
				this.minute = (int) (time / (60 * 1000) % 60);
//				this.hour = (int) (time / (60 * 60 * 1000) % 24);
//				this.day = (int) (time / (24 * 60 * 60 * 1000));
	}

	/**
	 * 显示当前时间
	 * 
	 * @return
	 */
	public String showTime() {
		StringBuilder time = new StringBuilder();
//		time.append(day);
//		time.append("天");
//		time.append(hour);
//		time.append("小时");
		time.append("有效时间:");
		time.append(minute);
		time.append("分钟");
		time.append(second);
		time.append("秒");
		return time.toString();
	}

	/**
	 * 实现倒计时
	 */
	private void countdown() {
		
		if (second == 0) {
			if (minute == 0) {
				//当时间归零时停止倒计时
				isRun = false;
				return;
			} else {
				minute--;
			}
			second = 60;
		}

		second--;
	}

	public boolean isRun() {
		return isRun;
	}

	/**
	 * 开始计时
	 */
	public void start() {
		isRun = true;
		run();
	}

	/**
	 * 结束计时
	 */
	public void stop() {
		isRun = false;
	}

	/**
	 * 实现计时循环
	 */
	@Override
	public void run() {
		if (isRun) {
			// Log.d(TAG, "Run");
			countdown();
			this.setText(showTime());
			
			postDelayed(this, 1000);
		} else {
			setTextStr("订单已失效");
			removeCallbacks(this);
		}
	}}
