package com.zhiduan.crowdclient.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * hexiuhui
 */
public class HttpUtils {

	protected static final String TAG = "HttpUtils";
	public static int length;// apk文件大小
	public static int downloaded;// apk文件的下载量

	public static final int MESSAGE_FIND_NEWVERSION = 1;
	public static final int MESSAGE_SHOW_PROGRESS = 2;
	public static final int MESSAGE_HIDE_PROGRESS = 3;
	public static final int MESSAGE_JUMP = 4;
	public static final int ERROR_MESSAGE_JUMP = 5;

	public HttpUtils() {

	}

	/**
	 * 获取服务器端APK版本号
	 */
	public static int getServiceVersionCode(String url) {

		Logs.v("zd", "app更新地址: " + url);
		int result;
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setReadTimeout(5000);
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// 得到响应：
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String[] str = reader.readLine().split("@");
				result = Integer.parseInt(str[0]);
			} else {
				result = -1;
			}
		} catch (Exception e) {
			result = -1;
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 下载APK文件:
	 * 
	 * @param context
	 * @param handler
	 */
	public static String APP_NAME = "CrowdClient.apk";

	public static void download(final Context context, final String url, final Handler mHandler) {

		String[] strArr = url.split("filename=");
		Log.v("updateAPK", url);
		if (strArr.length > 1) {
			APP_NAME = strArr[1];
		}

		new Thread() {

			public void run() {

				InputStream in = null;
				FileOutputStream out = null;
				try {
					mHandler.sendEmptyMessage(0x0001);

					HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
					conn.setRequestMethod("GET");
					conn.setReadTimeout(5000);
					in = conn.getInputStream();
					// 系统文件目录：
					File file = new File(context.getFilesDir().getPath(), APP_NAME);
					Logs.v("file", file.getAbsolutePath().toString());
					// 若存在，则删除:
					if (file.exists()) {
						file.delete();
					}

					if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						// 读写：
						out = context.openFileOutput(APP_NAME, Context.MODE_WORLD_WRITEABLE
								+ Context.MODE_WORLD_READABLE);
						byte[] buffer = new byte[4096];
						int len = 1;// 每一次读到的个数：

						while ((len = in.read(buffer)) != -1) {
							out.write(buffer, 0, len);
							// 累加下载量:
							downloaded += len;

							Message msg = new Message();
							Bundle bundle = new Bundle();
							bundle.putInt("totalSize", conn.getContentLength()); // 应用总大小
							bundle.putInt("curSize", downloaded); // 当前已下载大小
							msg.setData(bundle);// mes利用Bundle传递数据
							msg.what = 0x11;

							mHandler.sendMessage(msg);
						}

						CommandTools.install(file, context);
					}

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			};
		}.start();

	}

	public static String URL_APK_DOWNLOAD = "";
	/**
	 * 
	 */
	public static void download(final Context context, final Handler handler) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				InputStream in = null;
				FileOutputStream out = null;
				try {
					length();
					handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
					HttpURLConnection conn = (HttpURLConnection) new URL(URL_APK_DOWNLOAD).openConnection();
					conn.setRequestMethod("GET");
					conn.setReadTimeout(5000);
					in = conn.getInputStream();

					File file = new File(context.getFilesDir().getPath(), APP_NAME);
					if (file.exists()) {
						file.delete();
					}
					if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						out = context.openFileOutput(APP_NAME, Context.MODE_WORLD_WRITEABLE
								+ Context.MODE_WORLD_READABLE);
						byte[] buffer = new byte[4096];
						int len = 0;
						while ((len = in.read(buffer)) != -1) {
							out.write(buffer, 0, len);
							downloaded += len;
						}
					}
					CommandTools.install(file, context);
				} catch (MalformedURLException e) {
					e.printStackTrace();

					Log.e(TAG, e.getMessage());
					// StringWriter sw = new StringWriter();
					// e.printStackTrace(new PrintWriter(sw, true));
					// String errorLog = sw.toString();
					//
					// ErrorLogUtils.writeErrorLog(TAG + ":" + errorLog);
				} catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG, e.getMessage());
					// StringWriter sw = new StringWriter();
					// e.printStackTrace(new PrintWriter(sw, true));
					// String errorLog = sw.toString();
					//
					// ErrorLogUtils.writeErrorLog(TAG + ":" + errorLog);
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
							Log.e(TAG, e.getMessage());
							// StringWriter sw = new StringWriter();
							// e.printStackTrace(new PrintWriter(sw, true));
							// String errorLog = sw.toString();
							// ErrorLogUtils.writeErrorLog(TAG + ":" +
							// errorLog);
						}
					}
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
							Log.e(TAG, e.getMessage());
							// StringWriter sw = new StringWriter();
							// e.printStackTrace(new PrintWriter(sw, true));
							// String errorLog = sw.toString();
							// ErrorLogUtils.writeErrorLog(TAG + ":" +
							// errorLog);
						}
					}
				}
			}
		}).start();
	}

	private static void length() throws MalformedURLException, IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(URL_APK_DOWNLOAD).openConnection();
		conn.setRequestMethod("HEAD");
		conn.setReadTimeout(5000);
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			length = conn.getContentLength();
		}
	}

	public static String Send_Get(String uriAPI) {
		String strResult = "";
		/* 建立HTTP Get对象 */
		HttpGet httpRequest = new HttpGet(uriAPI);
		try {
			/* 发送请求并等待响应 */
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 读 */
				strResult = EntityUtils.toString(httpResponse.getEntity());
			} else {
				strResult = "";
			}
		} catch (Exception e) {
			strResult = "";
			e.printStackTrace();
		}
		return strResult;

	}

}
