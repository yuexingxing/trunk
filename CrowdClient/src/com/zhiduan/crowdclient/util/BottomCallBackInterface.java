package com.zhiduan.crowdclient.util;

import android.view.View;

/** 
 * 订单响应事件接口
 * 
 * @author yxx
 *
 * @date 2016-8-26 上午11:15:06
 * 
 */
public interface BottomCallBackInterface {

	public interface OnBottomClickListener {
		void onBottomClick(View v, int position);
	}
}
