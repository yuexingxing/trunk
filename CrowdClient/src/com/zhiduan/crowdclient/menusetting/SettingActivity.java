package com.zhiduan.crowdclient.menusetting;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.activity.LoginActivity;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.SettingsService;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.GlobalInstanceStateHelper;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;
import com.zhiduan.crowdclient.view.dialog.DialogUtils.DialogCallback;

/**
 * Description 设置界面 Author: hexiuhui
 */

public class SettingActivity extends BaseActivity implements OnClickListener {
	private final String mPageName = "SettingActivity";

	private TextView mExitTxt; // 退出按钮
	private ImageView mWorkStateBtn; // 切换工作状态
	private RelativeLayout mKnowAxpLayout; // 点击了解爱学派
	private RelativeLayout layoutDiscuss;

	private String mWorkState;
	private boolean m_bIsWorking = false;
	private LoadTextNetTask mTaskSetWorkState;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_setting, this);
		WindowManager.LayoutParams lp = getWindow().getAttributes();  
		lp.alpha =(float) 0.6; //0.0-1.0  
		getWindow().setAttributes(lp); 
		if (savedInstanceState != null) {
			// 因为用到了myapplication里的全局变量，被系统回收后要恢复
			GlobalInstanceStateHelper.restoreInstanceState(this,
					savedInstanceState);
		}

		setTitle("设置");
		hidenLeftTitle();
	}

	@Override
	public void initView() {
		mExitTxt = (TextView) findViewById(R.id.tv_exit);
		mKnowAxpLayout = (RelativeLayout) findViewById(R.id.rl_know_axp);
		mWorkStateBtn = (ImageView) findViewById(R.id.iv_switch_status);

		layoutDiscuss = (RelativeLayout) findViewById(R.id.rl_setting_discuss);

		mExitTxt.setOnClickListener(this);
		mWorkStateBtn.setOnClickListener(this);
		mKnowAxpLayout.setOnClickListener(this);
		layoutDiscuss.setOnClickListener(this);

		//显示当前所用库，正式库隐藏
		TextView tvVersion = (TextView) findViewById(R.id.tv_setting_version_status);
		if(Constant.FormalURL.equals(Constant.FORMAL_URL)){
			tvVersion.setVisibility(View.INVISIBLE);
		}else if(Constant.FormalURL.equals(Constant.DEV_URL)){
			tvVersion.setText("测试版本");
		}else if(Constant.FormalURL.equals(Constant.PRE_URL)){
			tvVersion.setText("预发布版本");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		//友盟统计
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(mContext);
	}

	@Override
	protected void onStart() {
		super.onStart();

		//友盟统计
		MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(mContext);
	}

	@SuppressLint("NewApi")
	@Override
	public void initData() {

		mWorkState = MyApplication.getInstance().m_userInfo.m_strUserWorkState;
		if (mWorkState.equals(Constant.USER_STATE_REST)) {
			m_bIsWorking = true;
			mWorkStateBtn.setBackground(getResources().getDrawable(
					R.drawable.switch_off));
		} else {
			m_bIsWorking = false;
			mWorkStateBtn.setBackground(getResources().getDrawable(
					R.drawable.switch_on));
		}


	}

	public void phone(View v){
		//客服电话
//		Intent intentPhone = new Intent(Intent.ACTION_DIAL);
//		Uri data = Uri.parse("tel:" + "400-166-1098");
//		intentPhone.setData(data);
//		startActivity(intentPhone);
		
		DialogUtils.showCallPhoneDialog(mContext, "400-166-1098", null);
	}

	/**
	 * 退出登录
	 */
	private void Exit_login() {

		DialogUtils.showExitDialog(SettingActivity.this, new DialogCallback() {

			@Override
			public void callback(int position) {
				// TODO Auto-generated method stub
				if(position == 1){

					JSONObject jsonObject = new JSONObject();
					RequestUtilNet.postDataToken(SettingActivity.this, Constant.Exit_url, jsonObject, new RequestCallback() {
						@Override
						public void callback(int success, String remark, JSONObject jsonObject) {
							
							CommandTools.showToast(remark);
							if (success == 0) {
								SharedPreferencesUtils.setParam(Constant.SP_LOGIN_PSD, "");
								MyApplication myApplication = MyApplication.getInstance();
								myApplication.m_userInfo.toKen = "";
								startActivity(new Intent(SettingActivity.this, LoginActivity.class));
								finish();
							}
						}
					});
				}
			}
		});


	}

	protected LoadTextNetTask setWorkState(final boolean workState) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@SuppressLint("NewApi")
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskSetWorkState = null;
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);
						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							if (workState == false) {
								MyApplication.getInstance().m_userInfo.m_strUserWorkState = Constant.USER_STATE_REST;
								mWorkStateBtn.setBackground(getResources().getDrawable(R.drawable.switch_off));
							} else {
								MyApplication.getInstance().m_userInfo.m_strUserWorkState = Constant.USER_STATE_WORK;
								mWorkStateBtn.setBackground(getResources().getDrawable(R.drawable.switch_on));
							}
							CommandTools.showToast("切换成功");
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);

							if (workState) {
								mWorkStateBtn.setBackground(getResources().getDrawable(R.drawable.switch_off));
							} else {
								mWorkStateBtn.setBackground(getResources().getDrawable(R.drawable.switch_on));
							}
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(SettingActivity.this);
						e.printStackTrace();

						if (workState) {
							mWorkStateBtn.setBackground(getResources().getDrawable(R.drawable.switch_off));
						} else {
							mWorkStateBtn.setBackground(getResources().getDrawable(R.drawable.switch_on));
						}
					}
				} else {
					Util.showNetErrorMessage(SettingActivity.this, result.m_nResultCode);

					if (workState) {
						mWorkStateBtn.setBackground(getResources().getDrawable(R.drawable.switch_off));
					} else {
						mWorkStateBtn.setBackground(getResources().getDrawable(R.drawable.switch_on));
					}
				}
			}
		};

		CustomProgress.showDialog(SettingActivity.this, "切换状态...", false, null);
		LoadTextNetTask task = SettingsService.setWorkState(workState, listener, null);
		return task;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_exit:
			//友盟统计
			MobclickAgent.onEvent(SettingActivity.this, "tv_exit");

			Exit_login();
			break;
		case R.id.rl_know_axp:
			//友盟统计
			MobclickAgent.onEvent(SettingActivity.this, "rl_know_axp");
			Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.iv_switch_status:
			//友盟统计
			MobclickAgent.onEvent(SettingActivity.this, "iv_switch_status");

			// 切换工作状态
			mWorkState = MyApplication.getInstance().m_userInfo.m_strUserWorkState;
			if (mWorkState.equals(Constant.USER_STATE_REST)) {
				m_bIsWorking = true;
			} else {
				m_bIsWorking = false;
			}

			mTaskSetWorkState = setWorkState(m_bIsWorking);
			break;
		case R.id.rl_setting_discuss:
			//友盟统计
			MobclickAgent.onEvent(SettingActivity.this, "rl_setting_discuss");

			Intent intent2 = new Intent(SettingActivity.this, FeedBackActivity.class);
			startActivity(intent2);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTaskSetWorkState != null) {
			mTaskSetWorkState.cancel(true);
			mTaskSetWorkState = null;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 被系统回收时保存myapplication全局变量值
		GlobalInstanceStateHelper.saveInstanceState(outState);

		outState.putString("mWorkState", mWorkState);
		outState.putBoolean("m_bIsWorking", m_bIsWorking);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// 恢复被系统回收后的myapplication值
		GlobalInstanceStateHelper
		.restoreInstanceState(this, savedInstanceState);

		initView();

		mWorkState = savedInstanceState.getString("mWorkState");
		m_bIsWorking = savedInstanceState.getBoolean("m_bIsWorking");
	}

}
