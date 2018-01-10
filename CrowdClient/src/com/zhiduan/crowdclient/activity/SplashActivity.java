package com.zhiduan.crowdclient.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.LoginUtil;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.NotificationHelper;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
import com.zhiduan.crowdclient.util.Utils;

public class SplashActivity extends Activity {
	private final String mPageName = "SplashActivity";

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		mContext = this;

		Utils.initAppEnv(this);

		setImmerseLayout();

		try {
			PushManager.getInstance().initialize(MyApplication.getInstance());
		} catch (Exception ex) {
			ex.printStackTrace();
			Logs.e("result", ex.getMessage());
		}

		if (!TextUtils.isEmpty(SharedPreferencesUtils.getParam(Constant.SP_TagName, Constant.FORMAL_TagName).toString())) {
			String name = SharedPreferencesUtils.getParam(Constant.SP_TagName, "").toString();
			if (!name.equals("")) {
				Constant.TagName = SharedPreferencesUtils.getParam(Constant.SP_TagName, Constant.FORMAL_TagName).toString();
				Constant.FormalURL = SharedPreferencesUtils.getParam(Constant.SP_FormalURL, Constant.FORMAL_URL).toString();
				Constant.UPDATEURL = SharedPreferencesUtils.getParam(Constant.SP_UPDATEURL, Constant.FORMAL_UPDATEURL).toString();
				Constant.appKey = SharedPreferencesUtils.getParam(Constant.SP_AppKey, Constant.FORMAL_AppKey).toString();
			}
		}

//		Intent intent = new Intent(SplashActivity.this, ThoughtCompleteActivity.class);
//		startActivity(intent);
//		finish();

		//正式库开启友盟统计
		if(Constant.FormalURL.equals(Constant.FORMAL_URL)){
			MobclickAgent.setDebugMode(true);
			MobclickAgent.openActivityDurationTrack(true);
		}

		goMainActivity();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyApplication.baseActivity=this;

		//友盟统计
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(mContext);
	}

	@Override
	protected void onPause() {
		super.onPause();

		//友盟统计
		MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(mContext);
	}

	protected void setImmerseLayout() {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	/**
	 * 延迟2秒启动
	 */
	private void goMainActivity() {
		//每次登陆清除所有通知
		NotificationHelper.clearAllNotification(SplashActivity.this);

		boolean result = (Boolean) SharedPreferencesUtils.getParam("is_first", false);
		if (result) {  
			String loginName = (String) SharedPreferencesUtils.getParam(Constant.SP_LOGIN_NAME, "");
			String loginPwd = (String) SharedPreferencesUtils.getParam(Constant.SP_LOGIN_PSD, "");
			String token = (String) SharedPreferencesUtils.getParam(Constant.SP_LOGIN_TOKEN, "");

			MyApplication.getInstance().m_userInfo.toKen = token;

//			LoginUtil.getPersonalMessage();
			Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();

			//			LoginUtil.login(SplashActivity.this, loginName, loginPwd, null, true);// 快速登陆
		} else {
			SharedPreferencesUtils.setParam("is_first", true);
			// 第一次进来
			Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {// 获取 back键

			Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
			startActivity(intent);
			SplashActivity.this.finish();
		}

		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
