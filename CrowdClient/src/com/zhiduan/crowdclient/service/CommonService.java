package com.zhiduan.crowdclient.service;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.AsyncTaskManager;
import com.zhiduan.crowdclient.net.HttpSendType;
import com.zhiduan.crowdclient.net.LoadPicNetTask;
import com.zhiduan.crowdclient.net.LoadPicParams;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextParams;
import com.zhiduan.crowdclient.net.NetTaskParamsMaker;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Logs;

/**
 * 
 * @author HeXiuHui
 * 
 */
public class CommonService {

	// 从图片服务器获取图片
	public static LoadPicNetTask getPicture(OnPostExecuteListener listener, Object tag, String strPicUrl) {
		LoadPicParams params = NetTaskParamsMaker.makeLoadPictureParams(strPicUrl);

		LoadPicNetTask task = (LoadPicNetTask) AsyncTaskManager.createAsyncTask(AsyncNetTask.TaskType.GET_PIC, params);
		task.addOnPostExecuteListener(listener, tag);
		task.executeMe();

		return task;
	}
	
	/** 获取服务器端APK版本号 **/
	public static LoadTextNetTask getAppVersion(OnPostExecuteListener listener, String strUrl, Object tag) {
		final ArrayList<NameValuePair> listArg = new ArrayList<NameValuePair>();
		
		Logs.v("checkupdate", "APP更新检查: " + strUrl);
		LoadTextParams params = NetTaskParamsMaker.makeLoadTextParams(strUrl,
				listArg, HttpSendType.HTTP_GET);
		LoadTextNetTask task = (LoadTextNetTask) AsyncTaskManager
				.createAsyncTask(AsyncNetTask.TaskType.GET_TEXT, params);

		task.addOnPostExecuteListener(listener, tag);
		task.executeMe();
		
		return task;
	}
}
