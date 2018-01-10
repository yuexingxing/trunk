package com.zhiduan.crowdclient.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.igexin.sdk.PushManager;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.activity.LoginActivity;
import com.zhiduan.crowdclient.activity.MainActivity;
import com.zhiduan.crowdclient.completeinformation.PerfectNameActivity;
import com.zhiduan.crowdclient.im.DemoDBManager;
import com.zhiduan.crowdclient.im.DemoHelper;
import com.zhiduan.crowdclient.menuindex.IndexActivity;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.UserService;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;

/**
 * 登录封装（单点异常登录，快速登录，登录界面登录）
 * 
 * @author yxx
 * 
 * @date 2016-5-3 下午4:24:20
 * 
 */
public class LoginUtil {

	private static Activity mActivity;

	@SuppressLint("NewApi")
	public static void login(Activity activity, String loginName,
			String loginPwd, Bundle extras, boolean isSplash) {

		mActivity = activity;

		if (!(TextUtils.isEmpty(loginName) || TextUtils.isEmpty(loginPwd))) {
			// 获取个推clientId
			String clientId = MyApplication.getInstance().m_strPushMessageClientId;
			if (clientId.isEmpty()) {
				clientId = PushManager.getInstance().getClientid(activity);
				if (clientId != null && !clientId.isEmpty()) {
					MyApplication.getInstance().m_strPushMessageClientId = clientId;
				}
			}

			requestLogin(loginName, loginPwd, clientId, isSplash);
		} else {
			Intent intent = new Intent(mActivity, MainActivity.class);
			mActivity.startActivity(intent);
			mActivity.finish();
		}
	}

	private static void requestLogin(final String LoginName, final String LoginPwd, String clientId, final boolean isSplash) {

		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {

				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);
						Logs.i("zd", jsonObj.toString() + "---");

						int res = jsonObj.optInt("success");
						String strMsg = jsonObj.optString("message");

						if (res == 0) {
							MyApplication myApplication = MyApplication.getInstance();

							JSONObject jsonObject = jsonObj.optJSONObject("data");
							myApplication.m_userInfo.toKen = jsonObject.optString("token");

							// 账号密码存储到配置文件
							SharedPreferencesUtils.setParam(Constant.SP_LOGIN_NAME, LoginName);
							SharedPreferencesUtils.setParam(Constant.SP_LOGIN_PSD, LoginPwd);
							SharedPreferencesUtils.setParam(Constant.SP_LOGIN_TOKEN, jsonObject.optString("token"));

							getPersonalMessage();
							// Utils.clearActivityList();
						} else {
							CommandTools.showToast(strMsg);
							if (isSplash) {
								Intent intent = new Intent(mActivity, LoginActivity.class);
								mActivity.startActivity(intent);
								mActivity.finish();
							}
						}

					} catch (JSONException e) {
						e.printStackTrace();
						if (isSplash) {
							Intent intent = new Intent(mActivity, LoginActivity.class);
							mActivity.startActivity(intent);
							mActivity.finish();
						}
					}
				} else {
					Util.showNetErrorMessage(mActivity, result.m_nResultCode);
					if (isSplash) {
						Intent intent = new Intent(mActivity, LoginActivity.class);
						mActivity.startActivity(intent);
						mActivity.finish();
					}
				}
			}
		};

		if (mActivity != null) {
			CustomProgress.showDialog(mActivity, "登录中", false, null);
		}

		UserService.login(LoginName, LoginPwd, clientId, listener, null);
	}

	/**
	 * 获取个人信息
	 */
	public static void getPersonalMessage() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("userType", "1");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "user/packet/getuser.htm";
		RequestUtilNet.postData(CommandTools.getGlobalActivity(), strUrl, jsonObject, new MyCallback() {
			@Override
			public void callback(JSONObject jsonObject) {
				try {
					if (jsonObject.optInt("success") == 0) {
						jsonObject = jsonObject.optJSONObject("data");

						MyApplication myApp = MyApplication.getInstance();

						myApp.m_userInfo.m_strUserName = jsonObject.optString("realName");
						myApp.m_userInfo.m_nick_name = jsonObject.optString("nickname");
						myApp.m_userInfo.m_strUserHeadPic = jsonObject.optString("icon");
						myApp.m_userInfo.m_strUserPhone = jsonObject.optString("phone");
						myApp.m_userInfo.m_user_total_income = jsonObject.optLong("totalIncome");
						myApp.m_userInfo.verifyStatus = jsonObject.optInt("state");
						myApp.m_userInfo.m_strUserSign = jsonObject.optString("signNumber");
						myApp.m_userInfo.m_strUserSchool = jsonObject.optString("collegeName");
						myApp.m_userInfo.m_strUserSchoolId=jsonObject.optInt("collegeId");
						myApp.m_userInfo.m_strUserGender = jsonObject.optString("gender");
						myApp.m_userInfo.m_strUserWorkState = jsonObject.optString("grabOrderMode");
						myApp.m_userInfo.m_strUserOffice = jsonObject.optString("office");

						Logs.v("zdkj", "UserName = " + myApp.m_userInfo.m_strUserName);
						// After logout，the DemoDB may still be accessed
						// due to async callback, so the DemoDB will be
						// re-opened again.
						// close it before login to make sure DemoDB not
						// overlap
						DemoDBManager.getInstance().closeDB();

						// reset current user name before login
						DemoHelper.getInstance().setCurrentUserName(myApp.m_userInfo.m_strUserPhone);

						String imPwd = MD5.encode(myApp.m_userInfo.m_strUserPhone.substring(myApp.m_userInfo.m_strUserPhone.length() - 4, myApp.m_userInfo.m_strUserPhone.length()));

						// call login method
						EMClient.getInstance().login(myApp.m_userInfo.m_strUserPhone, imPwd, new EMCallBack() {
									@Override
									public void onSuccess() {
										// ** manually load all local
										// groups and conversation
										EMClient.getInstance().groupManager().loadAllGroups();
										EMClient.getInstance().chatManager().loadAllConversations();

										// update current user's display
										// name for APNs
										boolean updatenick = EMClient.getInstance().updateCurrentUserNick(MyApplication.currentUserNick.trim());
										if (!updatenick) {
											Log.e("LoginActivity", "update current user nick fail");
										}
									}

									@Override
									public void onProgress(int progress, String status) {
									}

									@Override
									public void onError(final int code, final String message) {
										// mActivity.runOnUiThread(new
										// Runnable() {
										// public void run() {
										// Toast.makeText(mActivity,
										// mActivity.getString(R.string.Login_failed)
										// + message,
										// Toast.LENGTH_SHORT).show();
										// }
										// });
									}
								});
						// 性别，昵称，学校有一个为空就提醒用户去完善资料
						if (myApp.m_userInfo.m_strUserSchool.equals("") || myApp.m_userInfo.m_nick_name.equals("") || myApp.m_userInfo.m_strUserGender.equals("")) {
							// DialogUtils.showPerfectDialog(mActivity);
							Intent intent = new Intent(CommandTools.getGlobalActivity(), PerfectNameActivity.class);
							CommandTools.getGlobalActivity().startActivity(intent);
							CommandTools.getGlobalActivity().finish();
						} else {

							Intent intent = new Intent(CommandTools.getGlobalActivity(), MainActivity.class);
							CommandTools.getGlobalActivity()
							.startActivity(intent);
							CommandTools.getGlobalActivity().finish();
						}
					} else {
						Intent intent = new Intent(CommandTools.getGlobalActivity(), LoginActivity.class);
						CommandTools.getGlobalActivity().startActivity(intent);
						CommandTools.getGlobalActivity().finish();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
