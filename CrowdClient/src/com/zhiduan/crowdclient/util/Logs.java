package com.zhiduan.crowdclient.util;

import android.util.Log;

public class Logs {

	private static boolean printLog = true;

	public static void v(String tag, String msg) {
		if (printLog) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (printLog) {
			Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (printLog) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (printLog) {
			Log.w(tag, msg);
		}
	}

	public static void w(String tag, Throwable tr) {
		if (printLog) {
			Log.w(tag, tr);
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (printLog) {
			Log.w(tag, msg, tr);
		}
	}

	public static void e(String tag, String msg) {
		if (printLog) {
			Log.e(tag, msg);
		}
	}
}
