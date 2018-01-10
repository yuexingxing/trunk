package com.zhiduan.crowdclient.util;

import com.umeng.analytics.MobclickAgent;
import android.content.Context;

/** 
 * 友盟统计
 * 
 * @author yxx
 *
 * @date 2016-8-25 上午11:44:44
 * 
 */
public class UMengUtil {
	
	public static final String ORDER_ABNORMAL_CANCEL = "order_abnormal_cancel";//异常件取消

	/**
	 * Activity的onResume事件
	 * @param context
	 * @param className
	 */
	public static void onResume(Context context, String className){

		MobclickAgent.onPageStart(className);
		MobclickAgent.onResume(context);
	}

	/**
	 * Activity的onPause事件
	 * @param context
	 * @param className
	 */
	public static void onPause(Context context, String className){

		MobclickAgent.onPageEnd(className);
		MobclickAgent.onPause(context);
	}

	/**
	 * 统计点击事件
	 * @param context
	 * @param actionName 响应名称
	 */
	public static void onAction(Context context, String actionName){

		MobclickAgent.onEvent(context, actionName);
	}
}
