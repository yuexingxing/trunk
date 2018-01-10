package com.zhiduan.crowdclient.service;

import org.json.JSONObject;

import android.util.Log;

import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.NetTaskUtil;
import com.zhiduan.crowdclient.net.NetUtil;
import com.zhiduan.crowdclient.util.Constant;

/**
 * 
 * @author HeXiuHui
 * 
 */
public class SettingsService {

	/** 切换工作状态 **/
	public static LoadTextNetTask setWorkState(boolean workState, OnPostExecuteListener listener, Object tag) {
		String url = "user/packet/changegrabmode.htm";

		JSONObject object = new JSONObject();
		try {
			Log.i("hexiuhui---", "workState" + workState);
			if (workState) {
				object.put("grabOrderMode", Constant.USER_STATE_WORK);
			} else {
				object.put("grabOrderMode", Constant.USER_STATE_REST);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
}
