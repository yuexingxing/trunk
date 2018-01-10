package com.zhiduan.crowdclient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.zhiduan.crowdclient.util.Logs;

/**
 * @author HeXiuHui
 * 扫描广播
 */
public class ScanDataReceiver extends BroadcastReceiver {
	public String PDA_ACTION_GET_SCANDATA1 = "com.yto.action.GET_SCANDATA";//
	public String PDA_ACTION_GET_SCANDATA2 = "com.sim.action.SIMSCAN";//
	public String BILL_CODE_REGULAR_EXP = "^[A-Za-z0-9/\\-]{5,20}$";// "^[A-Za-z0-9]+$";
	public Pattern bill_code_pattern = Pattern.compile(BILL_CODE_REGULAR_EXP, Pattern.CASE_INSENSITIVE);
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String strBarcode = null;
		String action = intent.getAction();

		if (action.equals("com.android.scanservice.scancontext")) {
			String str = intent.getStringExtra("Scan_context");
			//
			Logs.i("zdkj", "后台广播输出: " + str);
			strBarcode = str;
			strBarcode = strBarcode.trim();

			boolean flag = is_valid_billcode(strBarcode);
			if (flag) {
				final Message lMsg = Message.obtain(MyApplication.getInstance().scanHandler, 0, strBarcode);
				lMsg.sendToTarget();
			} else {
				Logs.v("zd", "条码规则验证失败");
			}

			return;
		} else if (action.equals("com.android.scancontext")) {
			// “前台输出”不打勾时，不会发送此Intent
			String str = intent.getStringExtra("Scan_context");
			Logs.v("zd", "前台输出: " + str);
			// strBarcode = "";
			// final Message lMsg = Message.obtain(scanHandler, 0,
			// strBarcode);
			// lMsg.sendToTarget();
			// return;
		}
	}

	/*
	 * 判断是否属于合法的运单号
	 */
	public boolean is_valid_billcode(String billcode) {

		if (billcode.trim().length() < 5) {
			// Constans.MainActivity.playErrorSounds(0); // 报错声音
			return false;
		}
		Matcher matcher = bill_code_pattern.matcher(billcode);
		boolean flag = matcher.matches();
		if (!flag) {
			// Constans.MainActivity.playErrorSounds(0); // 报错声音
		}
		return flag;
	}
}
