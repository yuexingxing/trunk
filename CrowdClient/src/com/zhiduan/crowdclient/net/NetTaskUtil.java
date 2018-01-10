package com.zhiduan.crowdclient.net;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.activity.LoginActivity;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.MD5;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.view.CustomProgress;

/**
 * @author HeXiuHui
 * 
 */
public class NetTaskUtil {
	private static final String TAG = "NetTaskUtil";

	private static final int CONTENT_LENGTH_MAX = 2000 * 1024;
	private static final int READ_MAX = 2048;
	private static final int RETRY_COUNT = 1;// 3;
	private static final int RETRY_IDLE = 1000;

	// 设置消息头
	public static void setCommonHeader(HttpUriRequest request) {

		if (MyApplication.getInstance() == null) {
			return;
		}
		NetSettings netSettings = MyApplication.getInstance().m_netSettings;

		if (netSettings.getHttpSessionID().length() > 0) {
			try {
				request.setHeader("Cookie", NetSettings.HTTP_SESSIONID_NAME
						+ "=" + netSettings.getHttpSessionID());
				request.setHeader("accept", "text/json");
			} catch (Exception e) {

			}
		}
	}

	private static String getHttpSessionId(HttpResponse response) {
		Header[] headers = response.getHeaders("Set-Cookie");
		if (headers == null || headers.length == 0) {
			return "";
		}

		for (int i = 0; i < headers.length; i++) {
			String cookie = headers[i].getValue();
			String[] cookievalues = cookie.split(";");
			for (int j = 0; j < cookievalues.length; j++) {
				String[] keyPair = cookievalues[j].split("=");
				String key = keyPair[0].trim();
				if (key != null
						&& key.equalsIgnoreCase(NetSettings.HTTP_SESSIONID_NAME)) {
					String value = keyPair.length > 1 ? keyPair[1].trim() : "";
					return value;
				}
			}
		}

		return "";
	}

	/** 创建从服务器获取文本信息的异步任务并执行, 默认HTTP请求方式是POST */
	public static LoadTextNetTask makeTextNetTask(final String strUrl,
			String data, boolean isToken, final OnPostExecuteListener listener,
			final Object tag) {

		return makeTextNetTask(strUrl, data, isToken, HttpSendType.HTTP_POST,
				listener, tag);
	}

	/** 创建从服务器获取文本信息的异步任务并执行 */
	public static LoadTextNetTask makeTextNetTask(final String strUrl,
			String data, boolean isToken, final HttpSendType httpSendType,
			final OnPostExecuteListener listener, final Object tag) {
		// MyApplication.getInstance().sendTime = CommandTools.getTimes();
		// MyApplication.getInstance().mRandom = CommandTools.CeShi();
		// data = AESUtil.encryptAES(data);
		
		if(1-1 == 0){
			CustomProgress.dissDialog();
			return null;
		}

		if(!Utils.getNetWorkState()){
			Message msg = new Message();
			msg.what = OrderUtil.NO_NET_CONNECTION;
			MyApplication.getInstance().getEventBus().post(msg);
//			CommandTools.showToast("网络异常，请检查网络设置");
//			CustomProgress.dissDialog();
			return null;
		}

		if ("".equals(MyApplication.getInstance().sendTime) || MyApplication.getInstance().sendTime == null) {
			MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		}

		//		MyApplication.getInstance().m_userInfo.toKen = "token_8a099d5fcffb44cd5f90241ea27a827a";
		//		MD5.appId = "axp.android";

		final Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("data", data);
		paramMap.put("appid", MD5.appId);
		paramMap.put("format", "json");
		paramMap.put("version", "1.0");
		paramMap.put("appver", CommandTools.getVersionName(MyApplication.getInstance()));
		paramMap.put("timestamp", MyApplication.getInstance().sendTime);

//		if (isToken) {
//			if ("".equals(MyApplication.getInstance().m_userInfo.toKen) || MyApplication.getInstance().m_userInfo.toKen == null) {
//				CommandTools.showToast("登录失效，请重新登录!");
//				if(MyApplication.baseActivity != null){
//					Intent intent = new Intent(MyApplication.baseActivity, LoginActivity.class);
//					MyApplication.baseActivity.startActivity(intent);
//					MyApplication.baseActivity.finish();
//				}
//			} else {
//				paramMap.put("token", MyApplication.getInstance().m_userInfo.toKen);
//			}
//			//			paramMap.put("token", MyApplication.getInstance().m_userInfo.toKen);
//		}

		String md5Sign = MD5.getMd5Sign(paramMap, Constant.appKey);
		paramMap.put("sign", md5Sign);

		final ArrayList<NameValuePair> listArg = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			listArg.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));// CommandTools.toURLEncoded(
		}

		LoadTextParams params = NetTaskParamsMaker.makeLoadTextParams(strUrl,
				listArg, httpSendType);
		LoadTextNetTask task = (LoadTextNetTask) AsyncTaskManager
				.createAsyncTask(AsyncNetTask.TaskType.GET_TEXT, params);

		task.addOnPostExecuteListener(listener, tag);
		task.executeMe();

		return null;
	}

	/** 创建从服务器获取文本信息的异步任务并执行 */
	public static LoadTextNetTask makeTextNetTask(String strUrl, final HttpSendType httpSendType,
			final OnPostExecuteListener listener, final Object tag) {
		// MyApplication.getInstance().sendTime = CommandTools.getTimes();
		// MyApplication.getInstance().mRandom = CommandTools.CeShi();
		// data = AESUtil.encryptAES(data);

		final ArrayList<NameValuePair> listArg = new ArrayList<NameValuePair>();

		LoadTextParams params = NetTaskParamsMaker.makeLoadTextParams(strUrl,
				listArg, httpSendType);
		LoadTextNetTask task = (LoadTextNetTask) AsyncTaskManager
				.createAsyncTask(AsyncNetTask.TaskType.GET_TEXT, params);

		task.addOnPostExecuteListener(listener, tag);
		task.executeMe();

		return null;
	}

	public static LoadResult doLoadWork(AsyncNetTask task,
			NetTaskParams... params) {
		final NetTaskParams params0 = (NetTaskParams) params[0];
		LoadResult result = new LoadResult();
		int nRetryCount = 0; 

		// 如果请求中包含事务号，底层不做重试发送请求
		// if( (params0.m_strURL.indexOf("requestId=") >= 0)
		// || (params0.m_strContent != null &&
		// params0.m_strContent.indexOf("requestId=") >= 0) )
		// {
		nRetryCount = RETRY_COUNT;
		// }

		if (MyApplication.getInstance() == null) {
			result.m_nResultCode = -1;
			result.m_strResultDesc = "can't get net setting";
			return result;
		}
		NetSettings netSettings = MyApplication.getInstance().m_netSettings;

		while (nRetryCount++ <= RETRY_COUNT) {
			HttpClient client = HttpClientManager.getHttpClient();
			HttpGet get = null;
			HttpPost post = null;

			try {
				HttpResponse response;
				HttpUriRequest request;
				// if ((params0.m_strContent == null) ||
				// params0.m_strContent.equals(""))
				if (task.mParams.m_eHttpSendType == HttpSendType.HTTP_GET) {
					get = new HttpGet(params0.m_strURL);
					request = get;
				} else {
					post = new HttpPost(params0.m_strURL);

					if (params0.m_strContent == null) {
						params0.m_strContent = null;
					}

					Logs.i("NetTaskUtil", params0.m_strURL + "");
					Logs.i("NetTaskUtil", "POST BODY : " + params0.m_strContent + "");

					// StringEntity entity = new
					// StringEntity(params0.m_strContent, HTTP.UTF_8);

					if ((params0.m_strContentType != null)
							&& !params0.m_strContentType.equals("")) {
						// MCrypt mCrypt = new MCrypt();
						// post.setHeader("Content-Type",
						// params0.m_strContentType);
						// post.setHeader("accept","text/json");
						// post.addHeader("sendtime",MyApplication.getInstance().sendTime);
						// post.addHeader("sign",Base64.encodeToString(mCrypt.encrypt(MyApplication.getInstance().mSign),
						// Base64.NO_WRAP)+MyApplication.getInstance().mRandom);
						// Logs.i("hexiuhui----",
						// MyApplication.getInstance().mSign + "sign");
					}
					post.setEntity(new UrlEncodedFormEntity(
							params0.m_strContent, HTTP.UTF_8));

					// post.setEntity(entity);
					request = post;
				}

				// XFK TEST
				// Thread.sleep(500);

				// 设置消息头
				setCommonHeader(request);

				response = client.execute(request);

				// 保存服务器返回的HTTP SESSION
				if (netSettings.getHttpSessionID().length() == 0) {
					String JSESSIONID = getHttpSessionId(response);
					if (JSESSIONID.length() > 0) {
						netSettings.setHttpSessionID(JSESSIONID);
					}
				}

				StatusLine line = response.getStatusLine();
				if (line == null) {
					result.m_nResultCode = -1;
					result.m_strResultDesc = "StatusLine N/A";
					return result;
				}
				int code = line.getStatusCode();
				Logs.i("zdkj", "statusCode: " + code);
				result.m_nStatusCode = code;
				if (code != 200) {
					result.m_nResultCode = -1;
					result.m_strResultDesc = "StatusCode: " + code
							+ " Not Expected";
					return result;
				}

				HttpEntity entity = response.getEntity();
				if (params0.m_strExpectContentType != null) {
					Header header = entity.getContentType();
					if (header == null) {
						result.m_nResultCode = -1;
						result.m_strResultDesc = "ContentType N/A";
						return result;
					}
					String contentType = header.getValue();
					Logs.i("zdkj", "contentType: " + contentType);
					if (!contentType.regionMatches(true, 0,
							params0.m_strExpectContentType, 0,
							params0.m_strExpectContentType.length())) {
						String str = params0.m_strExpectContentType.replace(" ", "");
						if (!contentType.regionMatches(true, 0, str, 0, str.length())) // 避免因为字段间有空格导致不匹配
						{
							result.m_nResultCode = -1;
							result.m_strResultDesc = "ContentType Not Expected";
							return result;
						}
					}
				}

				long contentLength = entity.getContentLength();
				Logs.i("zdkj", "contentLength: " + contentLength);

				if (contentLength < 0 && contentLength != -1) {
					result.m_nResultCode = -1;
					result.m_strResultDesc = "ContentLength N/A";
					return result;
				}

				// 服务器有时返回的contentLength为0 (此时用浏览器也取不到图片)
				if(contentLength == 0)
				{
					//					EntityUtils.toByteArray(entity);
					result.m_nResultCode = -1;
					result.m_strResultDesc = "ContentLength = 0";
					return result;
				}

				Logs.i("zdkj", contentLength + "---" + CONTENT_LENGTH_MAX);

				if (contentLength > CONTENT_LENGTH_MAX
						|| contentLength > Integer.MAX_VALUE) {
					result.m_nResultCode = -1;
					result.m_strResultDesc = "ContentLength overflow";
					return result;
				}

				InputStream is = entity.getContent();
				byte[] buf = null;

				if (contentLength != -1) {
					buf = new byte[(int) contentLength];
					int l = 0, count;
					task.publishProgress(2L, contentLength, (long) l);
					while ((count = is.read(buf, l, READ_MAX)) != -1) {
						if (task.isCancelled()) {
							break;
						}
						l += count;
						task.publishProgress(2L, contentLength, (long) l);
					}

					if (l != contentLength) {
						result.m_nResultCode = -1;
						result.m_strResultDesc = "Read != ContentLength";
						break;
					}
				} else {
					byte[] bufTemp = new byte[CONTENT_LENGTH_MAX];
					int nLen = 0, count;
					while ((count = is.read(bufTemp, nLen, READ_MAX)) != -1) {
						if (task.isCancelled()) {
							break;
						}
						nLen += count;

						if (nLen + READ_MAX > CONTENT_LENGTH_MAX) {
							is.close();

							result.m_nResultCode = -1;
							result.m_strResultDesc = "ContentLength overflow";
							Log.i("hexiuhui===", "result:" + result);
							return result;
						}
					}
					buf = new byte[(int) nLen];
					System.arraycopy(bufTemp, 0, buf, 0, nLen);
				}

				result.buf = buf;

				result.m_nResultCode = 0;
				result.m_strResultDesc = "";

				return result;
			} catch (Exception e) {
				e.printStackTrace();
				result.m_nResultCode = -1;
				result.m_strResultDesc = e.toString();
			} finally {
				if (get != null) {
					get.abort();
				}
				if (post != null) {
					post.abort();
				}
			}

			try {
				Thread.sleep(RETRY_IDLE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/** 创建从服务器获取文本信息的异步任务并执行 */
	public static LoadTextNetTask makeTextNetTaskData(final String strUrl,
			String data, boolean isToken, final HttpSendType httpSendType,
			final OnPostExecuteListener listener, final Object tag) {

		if(!Utils.getNetWorkState()){
			CommandTools.showToast("网络异常，请检查网络设置");
			CustomProgress.dissDialog();
			return null;
		}

		if ("".equals(MyApplication.getInstance().sendTime) || MyApplication.getInstance().sendTime == null) {
			MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		}

		//		MyApplication.getInstance().m_userInfo.toKen = "token_8a099d5fcffb44cd5f90241ea27a827a";
		//		MD5.appId = "axp.android";

		final Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("data", data);
		paramMap.put("appid", MD5.appId);
		paramMap.put("format", "json");
		paramMap.put("version", "1.0");
		paramMap.put("appver", CommandTools.getVersionName(MyApplication.getInstance()));
		paramMap.put("timestamp", MyApplication.getInstance().sendTime);

		if (isToken) {
			if ("".equals(MyApplication.getInstance().m_userInfo.toKen) || MyApplication.getInstance().m_userInfo.toKen == null) {
				CommandTools.showToast("登录失效，请重新登录!");
				if(MyApplication.baseActivity != null){
					Intent intent = new Intent(MyApplication.baseActivity, LoginActivity.class);
					MyApplication.baseActivity.startActivity(intent);
					MyApplication.baseActivity.finish();
				}
			} else {
				paramMap.put("token", MyApplication.getInstance().m_userInfo.toKen);
			}
			//			paramMap.put("token", MyApplication.getInstance().m_userInfo.toKen);
		}

		String md5Sign = MD5.getMd5Sign(paramMap, Constant.appKey);
		paramMap.put("sign", md5Sign);

		final ArrayList<NameValuePair> listArg = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			listArg.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));// CommandTools.toURLEncoded(
		}

		LoadTextParams params = NetTaskParamsMaker.makeLoadTextParams(strUrl,
				listArg, httpSendType);
		LoadTextNetTask task = (LoadTextNetTask) AsyncTaskManager
				.createAsyncTask(AsyncNetTask.TaskType.GET_TEXT, params);

		task.addOnPostExecuteListener(listener, tag);
		task.executeMe();

		return null;
	}
}
