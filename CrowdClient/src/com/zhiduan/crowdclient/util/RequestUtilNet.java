package com.zhiduan.crowdclient.util;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.AsyncTaskManager;
import com.zhiduan.crowdclient.net.HttpSendType;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextParams;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskParamsMaker;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.net.NetTaskUtil;
import com.zhiduan.crowdclient.view.CustomProgress;

/**
 * 网络请求封装
 * 
 * @author yxx
 * 
 * @date 2016-4-5 下午7:41:04
 * 
 */
public class RequestUtilNet {

	public static abstract class MyCallback {
		Bundle mExtras;

		public MyCallback(Bundle extras) {
			mExtras = extras;
		}

		public MyCallback() {
		}

		public abstract void callback(JSONObject jsonObject);
	}

	public static abstract class RequestCallback {
		Bundle mExtras;

		public RequestCallback() {

		}

		public abstract void callback(int success, String remark, JSONObject jsonObject);
	}

	public static abstract class MsgCallback {

		public abstract void callback(String message);
	}

	/**
	 * @param mContext
	 * @param strUrl
	 * @param strTitle
	 * @param flag
	 * @param jsonObject
	 * @param callback
	 */
	public static void postData(final Context mContext, String strUrl, JSONObject jsonObject, final MyCallback callback) {

		OnPostExecuteListener listener = null;
		listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					String strMsg = mresult.m_strContent;
					Logs.e("NetTaskUtil", strMsg + "");

					if (TextUtils.isEmpty(strMsg)) {
						return;
					}

					try {
						JSONObject jsonObject = new JSONObject(strMsg);
						int res = jsonObject.getInt("success");
						String remark = jsonObject.getString("message");

						callback.callback(jsonObject);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(mContext, result.m_nResultCode);
				}

				CustomProgress.dissDialog();
			}
		};

		boolean isToken = true;

		NetTaskUtil.makeTextNetTask(Constant.FormalURL + strUrl, jsonObject.toString(), isToken, HttpSendType.HTTP_POST,
				listener, null);
	}

	/**
	 * @param mContext
	 * @param strUrl
	 * @param strTitle
	 * @param flag
	 * @param jsonObject
	 * @param callback
	 */
	public static void postDataNoToken(final Context mContext, String strUrl, JSONObject jsonObject, final RequestCallback callback) {

		OnPostExecuteListener listener = null;
		listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {

				if (result.m_nResultCode == 0) {

					LoadTextResult mresult = (LoadTextResult) result;
					String strMsg = mresult.m_strContent;
					Logs.e("NetTaskUtil", strMsg + "");

					if (TextUtils.isEmpty(strMsg)) {
						return;
					}

					try {
						JSONObject jsonObject = new JSONObject(strMsg);
						int success = jsonObject.getInt("success");
						String remark = jsonObject.getString("message");

						if (success != 0) {
							CommandTools.showToast(remark);
						} else {
							callback.callback(success, remark, jsonObject);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(mContext, result.m_nResultCode);
				}

				CustomProgress.dissDialog();
			}
		};

		boolean isToken = false;

		NetTaskUtil.makeTextNetTask(Constant.FormalURL + strUrl, jsonObject.toString(), isToken, HttpSendType.HTTP_POST,
				listener, null);
	}

	/**
	 * @param mContext
	 * @param strUrl
	 * @param token是否需要token
	 * @param jsonObject
	 * @param callback
	 */
	public static void postDataIfToken(final Context mContext, String strUrl, boolean token, JSONObject jsonObject, final RequestCallback callback) {

		OnPostExecuteListener listener = null;
		listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {

				if (result.m_nResultCode == 0) {

					LoadTextResult mresult = (LoadTextResult) result;
					String strMsg = mresult.m_strContent;
					Logs.e("NetTaskUtil", strMsg + "");

					if (TextUtils.isEmpty(strMsg)) {
						return;
					}

					try {
						JSONObject jsonObject = new JSONObject(strMsg);
						int success = jsonObject.getInt("success");
						String remark = jsonObject.getString("message");

						//						if (success != 0) {
						//							CommandTools.showToast(remark);
						//						} 

						callback.callback(success, remark, jsonObject);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(mContext, result.m_nResultCode);
				}

				CustomProgress.dissDialog();
			}
		};

		boolean isToken = token;

		if(strUrl.contains("http")){

		}else{
			strUrl = Constant.FormalURL + strUrl;
		}

		NetTaskUtil.makeTextNetTask(strUrl, jsonObject.toString(), isToken, HttpSendType.HTTP_POST,
				listener, null);
	}

	/**
	 * @param mContext
	 * @param strUrl
	 * @param strTitle
	 * @param flag
	 * @param jsonObject
	 * @param callback
	 */
	public static void postData(final Context mContext, String strUrl, String strTitle, boolean flag, JSONObject jsonObject, final MyCallback callback){

		if(flag == true){
			CustomProgress.showDialog(mContext, strTitle, false, null);
		}

		OnPostExecuteListener listener = null;
		listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {

				if (result.m_nResultCode == 0) {

					LoadTextResult mresult = (LoadTextResult) result;
					String strMsg = mresult.m_strContent;
					Logs.e("NetTaskUtil", strMsg + "");

					if(TextUtils.isEmpty(strMsg)){
						return;
					}

					try {
						JSONObject jsonObject = new JSONObject(strMsg);
						int res = jsonObject.getInt("success");
						String remark = jsonObject.getString("message");

						if(res != 0){
							CommandTools.showToast(remark);
						}else{
							callback.callback(jsonObject);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(CommandTools.getGlobalActivity(), result.m_nResultCode);
				}

				CustomProgress.dissDialog();
			}
		};

		boolean isToken = false;

		NetTaskUtil.makeTextNetTask(Constant.FormalURL + strUrl, jsonObject.toString(), isToken,  HttpSendType.HTTP_POST, listener, null);
	}

	/**
	 * @param mContext
	 * @param strUrl
	 * @param strTitle
	 * @param flag
	 * @param jsonObject
	 * @param callback
	 */
	public static void postDataToken(final Context mContext, String strUrl, JSONObject jsonObject, final RequestCallback callback) {

		if(TextUtils.isEmpty(MyApplication.getInstance().m_userInfo.toKen)){
			CustomProgress.dissDialog();
			return;
		}

		OnPostExecuteListener listener = null;
		listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {

				Logs.e("-----", "result.m_nResultCode==="+result.m_nResultCode);

				if (result.m_nResultCode == 0) {

					LoadTextResult mresult = (LoadTextResult) result;
					String strMsg = mresult.m_strContent;

					Logs.e("NetTaskUtil", strMsg + "");

					if (TextUtils.isEmpty(strMsg)) {
						return;
					}

					try {
						JSONObject jsonObject = new JSONObject(strMsg);
						int success = jsonObject.optInt("success");
						String remark = jsonObject.optString("message");

						callback.callback(success, remark, jsonObject);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(CommandTools.getGlobalActivity(), result.m_nResultCode);
				}

				CustomProgress.dissDialog();
			}
		};

		boolean isToken = true;

		NetTaskUtil.makeTextNetTask(Constant.FormalURL + strUrl, jsonObject.toString(), isToken, HttpSendType.HTTP_POST,
				listener, null);
	}

	/**
	 * 
	 * @param mContext
	 * @param strUrl
	 * @param token
	 * @param jsonObject
	 * @param callback
	 */
	public static void postDataMessage(final Context mContext, String strUrl, boolean token, JSONObject jsonObject, final MsgCallback callback) {

		OnPostExecuteListener listener = null;
		listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {

				if (result.m_nResultCode == 0) {

					LoadTextResult mresult = (LoadTextResult) result;
					String strMsg = mresult.m_strContent;
					Logs.e("NetTaskUtil", strMsg + "");

					if (TextUtils.isEmpty(strMsg)) {
						return;
					}

					callback.callback(strMsg);
				} else {
					Util.showNetErrorMessage(mContext, result.m_nResultCode);
				}

				CustomProgress.dissDialog();
			}
		};

		if(strUrl.contains("http")){

		}else{
			strUrl = Constant.FormalURL + strUrl;
		}

		NetTaskUtil.makeTextNetTask(strUrl, jsonObject.toString(), token, HttpSendType.HTTP_POST,
				listener, null);
	}
}
