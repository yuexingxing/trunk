package com.zhiduan.crowdclient.util;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.service.CommonService;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.UpdateAppDialog;
import com.zhiduan.crowdclient.view.UpdateAppDialog.ResultCallBack;

/** 
 * 版本检查更新逻辑处理
 * 
 * @author yxx
 *
 * @date 2016-11-16 下午3:18:01
 * 
 */
public class PCheckUpdate {

	private static Context mCntext;
	private static String remark;//更新说明

	private static int mClientVersion;// 客户端版本号
	private static int mServerVersion;// 服务器端版本号

	public static abstract class Callback {
		public abstract void callback(String remark);
	}

	public static void check(final Context context, final Callback callback) {

		mCntext = context;
		mClientVersion = CommandTools.getVersionCode();

		OnPostExecuteListener listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				// TODO Auto-generated method stub
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {

					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("result", jsonObj.toString() + "---");

						String vercode = jsonObj.optString("vercode");
						if(TextUtils.isEmpty(vercode)){
							vercode = "0";
						}

						mClientVersion = CommandTools.getVersionCode();
						mServerVersion = Integer.parseInt(vercode);//服务器上版本号

						String strName = jsonObj.optString("vername");//服务器上版本名称
						final String downloadUrl = jsonObj.optString("url");
						final String beforce = jsonObj.optString("beforce");//是否强制更新
						remark =  jsonObj.optString("remark");

						Log.v("updateAPK", "mClientVersion = " + mClientVersion);
						Log.v("updateAPK", "mServerVersion = " + mServerVersion);

						if (mClientVersion < mServerVersion) {

							UpdateAppDialog.showDialog(context, beforce, "更新检测", "发现新版本号 " + strName + "，确定更新吗?", remark, new ResultCallBack() {
								@Override
								public void callback(boolean flag) {

									if (flag == true) {
										HttpUtils.download(context, downloadUrl, mHandler);
									}else{
										//如果是强制更新，点击后退出程序
										if(beforce.equals("1")){
											UpdateAppDialog.dissDialog();
											Utils.finishAllActivities();
										}
									}
								}
							});
						} else {
							if(callback != null){
								callback.callback("当前已是最新版本");
							}
							return;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Logs.v("checkupdate", "更新检查失败");
					Util.showNetErrorMessage(context, result.m_nResultCode, new View.OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
				}
			}
		};

		CustomProgress.showDialog(context, "正在检查程序更新", true, null);
		String strUrl = Constant.UPDATEURL + CommandTools.getVersionCode();
		CommonService.getAppVersion(listener, strUrl, null);
	}

	public static Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.what == 0x0001) {
				UpdateAppDialog.showDialog(mCntext, "0", "正在更新程序", "正在更新程序", "", null);
			}else if (msg.what == 0x0002) {
				CustomProgress.dissDialog();
			}else if (msg.what == 0x11) {

				Bundle bundle = msg.getData();
				int totalSize = bundle.getInt("totalSize") / 100;
				int curSize = bundle.getInt("curSize") / 100;

				if (curSize >= totalSize) {
					UpdateAppDialog.dissDialog();
					Utils.finishAllActivities();
					return;
				}

				UpdateAppDialog.updateProgress(totalSize, curSize);
			}

		}
	};
}
