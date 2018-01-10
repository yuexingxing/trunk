package com.zhiduan.crowdclient.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.ScanDataReceiver;
import com.zhiduan.crowdclient.net.DirSettings;
import com.zhiduan.crowdclient.net.HttpClientManager;
import com.zhiduan.crowdclient.net.WifiInfoEx;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;

public class Utils {
	
	public static String getCurrentTime(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}
	public static long getValidTime(String begin_time,String expiry_time){
		SimpleDateFormat hourDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//获得当前时间  
        Long expirytime=null;
        Long now_time=null;
        Long begintiem=null;
        try {
        	expirytime = (hourDateFormat.parse(expiry_time)).getTime();
        	begintiem=(hourDateFormat.parse(begin_time)).getTime();
        	now_time=new Date().getTime();
        	
		} catch (Exception e) {
			// TODO: handle exception
		}
		return now_time-expirytime>0?0:expirytime-now_time;
	}
	public static String getCurrentTime() {
		return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
	}

	// 初始化应用的运行环境
	public static void initAppEnv(Context context) {
		WifiInfoEx.initWifi(context);
		HttpClientManager.resetHttpClient(
				Proxy.getHost(context.getApplicationContext()),
				Proxy.getPort(context.getApplicationContext()));

		FileUtils.creatDirsIfNeed(DirSettings.getAppCacheDir());
		FileUtils.creatDirsIfNeed(DirSettings.getAppCameraDir());
		FileUtils.creatDirsIfNeed(DirSettings.getAppAudioDir());
	}
	/**
	 * 获取网络状态:false->网络未连接；true->网络已连接
	 * 
	 * @return boolean
	 */
	public static boolean getNetWorkState() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) MyApplication
				.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();
		}
		return false;
	}
	/**
	 * S6000 注册扫描广播
	 */
	public static void initPDAScanner(Context context) {
		IntentFilter scanDataIntentFilter = new IntentFilter();
		// scanDataIntentFilter.addAction("com.android.scancontext"); //前台输出
		scanDataIntentFilter.addAction("com.android.scanservice.scancontext"); // 后台输出
		scanDataIntentFilter.setPriority(500);

		ScanDataReceiver scanDataReceiver = new ScanDataReceiver();
		context.registerReceiver(scanDataReceiver, scanDataIntentFilter);
	}

	/**
	 * 添加Activity到容器中
	 * 
	 * @param activity
	 */
	public static void addActivity(Activity activity) {
		
		if (!MyApplication.getInstance().activityList.contains(activity)) {
			MyApplication.getInstance().activityList.add(activity);
		}
	}
	
	/**
	 * 释放所有的Activity
	 */
	public static void finishAllActivities() {
		
		for (Activity activity : MyApplication.getInstance().activityList) {
			if (activity != null) {
				activity.finish();
			}
		}

		MyApplication.getInstance().activityList.clear();
		System.exit(0);
	}

	public static void clearActivityList() {
		
		for (Activity activity : MyApplication.getInstance().activityList) {
			if (activity != null) {
				activity.finish();
				Logs.v("zd", "finish");
			}
		}
		MyApplication.getInstance().activityList.clear();
	}
}
