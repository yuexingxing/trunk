package com.zhiduan.crowdclient.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.UserService;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.LoginUtil;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CustomProgress;

/**
 * 绑定手机
 * 
 * @author hexiuhui
 */
public class BindPhoneActivity extends BaseActivity implements OnClickListener {

	private final String mPageName = "EditSmsActivity";
	private EditText mBindPhone;
	private EditText mBindCode;
	private TextView btn_bind;
	private LoadTextNetTask mTaskRequestBind;
	
	private String source;
	private String thirdpartyToken;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_bind_phone, BindPhoneActivity.this);
		setTitle("绑定手机号");
	}

	public void initView() {
		mBindPhone = (EditText) findViewById(R.id.bind_phone);
		mBindCode = (EditText) findViewById(R.id.bind_code);
		btn_bind = (TextView) findViewById(R.id.btn_bind_code);

		Button mLoginBtn = (Button) findViewById(R.id.bind_btn);

		mBindPhone.setText(SharedPreferencesUtils.getParam(Constant.SP_LOGIN_NAME, "").toString());

		mLoginBtn.setOnClickListener(this);
		btn_bind.setOnClickListener(this);
	}
	
	@Override
	public void initData() {
		source = getIntent().getStringExtra("source");
		thirdpartyToken = getIntent().getStringExtra("thirdpartyToken");
	}
	
	public void readDeal(View v) {
		Intent intent = new Intent(BindPhoneActivity.this, DealActivity.class);
		startActivity(intent);
	}

	protected void setImmerseLayout() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 友盟统计
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 友盟统计
		MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bind_btn:
			// 友盟统计
			MobclickAgent.onEvent(BindPhoneActivity.this, "bind_btn");

			// 登陆按钮
			String bindPhone = mBindPhone.getText().toString();
			String bindCode = mBindCode.getText().toString();

			if (bindPhone.equals("") || bindCode.equals("")) {
				Toast.makeText(BindPhoneActivity.this, "手机或验证码不能为空", 1).show();
				return;
			}

			if (!CommandTools.isMobileNO(bindPhone)) {
				Toast.makeText(BindPhoneActivity.this, "请输入合法的手机号", 1).show();
				return;
			}

			// 获取个推clientId
			String clientId = MyApplication.getInstance().m_strPushMessageClientId;
			if (clientId.isEmpty()) {
				clientId = PushManager.getInstance().getClientid(
						BindPhoneActivity.this);
				if (clientId != null && !clientId.isEmpty()) {
					MyApplication.getInstance().m_strPushMessageClientId = clientId;
				}
			}
			
			String imei = CommandTools.getMIME(BindPhoneActivity.this);

			mTaskRequestBind = requestBind(clientId, bindCode, imei, bindPhone, source, thirdpartyToken);

			break;
		case R.id.btn_bind_code:
			getVerCode();
			break;
		default:
			break;
		}
	}

	protected LoadTextNetTask requestBind(String clientId, String code, String imei, String phone, String source, String thirdpartyToken) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestBind = null;
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							CommandTools.showToast("登录成功");
							MyApplication myApplication = MyApplication.getInstance();

							JSONObject jsonObject = jsonObj.optJSONObject("data");
							myApplication.m_userInfo.toKen = jsonObject.optString("token");
							
							SharedPreferencesUtils.setParam(Constant.SP_LOGIN_TOKEN, jsonObject.optString("token"));
							
							LoginUtil.getPersonalMessage();
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(BindPhoneActivity.this);
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(BindPhoneActivity.this, "获取数据中...", false, null);
		LoadTextNetTask task = UserService.bind(clientId, code, imei, phone, source, thirdpartyToken, listener, null);
		return task;
	}

	/**
	 * 获取验证码
	 * 
	 * @param v
	 */
	public void getVerCode() {
		String phone = mBindPhone.getText().toString();
		if (TextUtils.isEmpty(phone)) {
			CommandTools.showToast("请输入手机号");
			return;
		}

		if (!CommandTools.isMobileNO(phone)) {
			CommandTools.showToast("手机号不合法");
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("phone", phone);
			jsonObject.put("codeType", "thdbinding");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postData(mContext, Constant.getVerCode_url, "验证码获取中", true, jsonObject, new MyCallback() {
			@Override
			public void callback(JSONObject jsonObject) {
				Log.i("hexiuhui---", jsonObject.toString());
				CustomProgress.dissDialog();
				try {
					TimeCount tc = new TimeCount(90000, 1000);
					tc.start();

					CommandTools.showToast(jsonObject.getString("message"));
					mBindCode.requestFocus();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发

			btn_bind.setText("重新验证");
			btn_bind.setTextColor(Color.WHITE);
			btn_bind.setBackgroundResource(R.drawable.register_valid);
			btn_bind.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示

			btn_bind.setClickable(false);

			String strSec = millisUntilFinished / 1000 + "s";
			SpannableString spanStack = new SpannableString(strSec);
			spanStack.setSpan(new ForegroundColorSpan(Color.RED), 0,
					spanStack.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

			btn_bind.setBackgroundResource(R.drawable.register_valid_gray);
			btn_bind.setText("");
			btn_bind.append(spanStack);
			btn_bind.append("后重发");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mTaskRequestBind != null) {
			mTaskRequestBind.cancel(true);
			mTaskRequestBind = null;
		}
	}
}
