package com.zhiduan.crowdclient.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
 * 快速登录
 * 
 * @author hexiuhui
 */

public class QuickLoginActivity extends BaseActivity implements OnClickListener {

	private final String mPageName = "EditSmsActivity";
	private EditText mUserName;
	private EditText mUserCode;
	private Button btn_number_register;
	private LinearLayout ll_quick_login_img;
	private LoadTextNetTask mTaskRequestQuickLogin;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_quick_login, QuickLoginActivity.this);
		setTitle("快速登录");
		setRightPic(R.drawable.shut_down);
		ShowRightPic();
		
	}

	@Override
	public void rightPicClick() {
		// TODO Auto-generated method stub
		super.rightPicClick();
		finish();
	}
	public void initView() {
		ll_quick_login_img = (LinearLayout) findViewById(R.id.ll_quick_login_img);
		mUserName = (EditText) findViewById(R.id.quick_user_name);
		mUserCode = (EditText) findViewById(R.id.edt_number_register);
		btn_number_register = (Button) findViewById(R.id.btn_number_register);

		Button mLoginBtn = (Button) findViewById(R.id.quick_login_btn);

		mUserName.setText(SharedPreferencesUtils.getParam(
				Constant.SP_LOGIN_NAME, "").toString());

		mLoginBtn.setOnClickListener(this);
		btn_number_register.setOnClickListener(this);

		mUserName.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				ll_quick_login_img.setVisibility(View.GONE);
				return false;
			}
		});
		mUserCode.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				ll_quick_login_img.setVisibility(View.GONE);
				return false;
			}
		});
	}
	/**
     * 隐藏软键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
 
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
 
            if (CommandTools.isShouldHideInput(v, ev)) {
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                CommandTools.hideSoftInput(v.getWindowToken(), im);
                ll_quick_login_img.setVisibility(View.VISIBLE);
            }else {
            	ll_quick_login_img.setVisibility(View.GONE);
			}
        }
        return super.dispatchTouchEvent(ev);
    }
	@Override
	public void initData() {

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
		case R.id.quick_login_btn:
			// 友盟统计
			MobclickAgent.onEvent(QuickLoginActivity.this, "quick_login_btn");

			// 登陆按钮
			String LoginName = mUserName.getText().toString();
			String LoginPwd = mUserCode.getText().toString();

			if (LoginName.equals("") || LoginPwd.equals("")) {
				Toast.makeText(QuickLoginActivity.this, "手机或验证码不能为空", 1).show();
				return;
			}

			if (!CommandTools.isMobileNO(LoginName)) {
				Toast.makeText(QuickLoginActivity.this, "请输入合法的手机号", 1).show();
				return;
			}

			// 获取个推clientId
			String clientId = MyApplication.getInstance().m_strPushMessageClientId;
			if (clientId.isEmpty()) {
				clientId = PushManager.getInstance().getClientid(QuickLoginActivity.this);
				if (clientId != null && !clientId.isEmpty()) {
					MyApplication.getInstance().m_strPushMessageClientId = clientId;
				}
			}

			mTaskRequestQuickLogin = requestQuickLogin(LoginName, LoginPwd, clientId);
			break;
		case R.id.btn_number_register:
			getVerCode();
			break;
		default:
			break;
		}
	}
	
	public void readDeal(View v) {
		Intent intent = new Intent(QuickLoginActivity.this, DealActivity.class);
		startActivity(intent);
	}

	protected LoadTextNetTask requestQuickLogin(String userName, String userCode, String clientId) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestQuickLogin = null;
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
						Util.showJsonParseErrorMessage(QuickLoginActivity.this);
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(QuickLoginActivity.this, "获取数据中...", false, null);
		LoadTextNetTask task = UserService.quickLogin(userName, userCode, clientId, listener, null);
		return task;
	}

	/**
	 * 获取验证码
	 * 
	 * @param v
	 */
	public void getVerCode() {
		String phone = mUserName.getText().toString();
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
			jsonObject.put("codeType", "userlogin");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postData(mContext, Constant.getVerCode_url, "验证码获取中",
				true, jsonObject, new MyCallback() {
					@Override
					public void callback(JSONObject jsonObject) {
						Log.i("hexiuhui---", jsonObject.toString());
						CustomProgress.dissDialog();
						try {
							TimeCount tc = new TimeCount(90000, 1000);
							tc.start();

							CommandTools.showToast(jsonObject
									.getString("message"));
							mUserCode.requestFocus();
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

			btn_number_register.setText("重新验证");
			btn_number_register.setTextColor(Color.WHITE);
			btn_number_register
					.setBackgroundResource(R.drawable.register_valid);
			btn_number_register.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示

			btn_number_register.setClickable(false);

			String strSec = millisUntilFinished / 1000 + "s";
			SpannableString spanStack = new SpannableString(strSec);
			spanStack.setSpan(new ForegroundColorSpan(Color.RED), 0,
					spanStack.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

			btn_number_register
					.setBackgroundResource(R.drawable.register_valid_gray);
			btn_number_register.setText("");
			btn_number_register.append(spanStack);
			btn_number_register.append("后重发");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mTaskRequestQuickLogin != null) {
			mTaskRequestQuickLogin.cancel(true);
			mTaskRequestQuickLogin = null;
		}
	}
}
