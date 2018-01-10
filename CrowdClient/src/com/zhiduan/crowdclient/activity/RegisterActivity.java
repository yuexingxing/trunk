package com.zhiduan.crowdclient.activity;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.completeinformation.PerfectNameActivity;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.service.UserService;
import com.zhiduan.crowdclient.util.AES;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.LoginUtil;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
import com.zhiduan.crowdclient.view.ClearEditText;
import com.zhiduan.crowdclient.view.CustomProgress;

/**
 * <pre>
 * Description 注册界面
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-5-24 下午4:58:44
 * </pre>
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {
	private final String mPageName = "RegisterActivity";
	private Context mContext;
	private Button btnNumber;

	private Button btnRegister;

	private ClearEditText edtPhone;
	private EditText edtVerCode;
	private EditText edtPsd;
	private EditText codeEt;
	private ImageView imgPsd;
	private CheckBox mCheckBox;
	private LinearLayout ll_register_img;
	private String strPhone;
	private String strVerCode;
	private String strPsd;
	private boolean isShow = false;
	private SmsObserver smsObserver;
	private Uri SMS_INBOX = Uri.parse("content://sms/");

	private final int READ_DEAL = 0x0011;
	
	private LoadTextNetTask mTaskRequestIsbind;
	private LoadTextNetTask mTaskRequestGaveLogin;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_register, RegisterActivity.this);
		setTitle("注册");
		smsObserver = new SmsObserver(this, smsHandler);
		getContentResolver().registerContentObserver(SMS_INBOX, true, smsObserver);
	}

	@Override
	public void initView() {
		mContext = RegisterActivity.this;
		ll_register_img=(LinearLayout) findViewById(R.id.ll_register_img);
		codeEt = (EditText) findViewById(R.id.edt_code_register);
		edtPhone = (ClearEditText) findViewById(R.id.edt_phone_register);
		edtVerCode = (EditText) findViewById(R.id.edt_number_register);
		edtPsd = (EditText) findViewById(R.id.edt_password_register);
		mCheckBox = (CheckBox) findViewById(R.id.checkBox1);

		imgPsd = (ImageView) findViewById(R.id.imb_password_register);

		btnNumber = (Button) findViewById(R.id.btn_number_register);
		btnRegister = (Button) findViewById(R.id.btn_register);

		setEditableListener(edtPhone);
		setEditableListener(edtVerCode);
		setEditableListener(edtPsd);

		btnNumber.setOnClickListener(this);
		isShow = false;
		showPwd(null);

		ImageView wxLoginBtn = (ImageView) findViewById(R.id.wx_login_txt);
		ImageView qqLoginBtn = (ImageView) findViewById(R.id.qq_login_txt);
		ImageView wbLoginBtn = (ImageView) findViewById(R.id.wb_login_txt);
		
		wxLoginBtn.setOnClickListener(this);
		qqLoginBtn.setOnClickListener(this);
		wbLoginBtn.setOnClickListener(this);
	}
	
//	/**
//     * 隐藏软键盘
//     */
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
//            View v = getCurrentFocus();
// 
//            if (CommandTools.isShouldHideInput(v, ev)) {
//                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                CommandTools.hideSoftInput(v.getWindowToken(), im);
//                ll_register_img.setVisibility(View.VISIBLE);
//            } else {
//            	ll_register_img.setVisibility(View.GONE);
//			}
//        }
//        return super.dispatchTouchEvent(ev);
//    }
    
	@Override
	public void initData() {
		/*codeEt.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				ll_register_img.setVisibility(View.GONE);
				return false;
			}
		});
		
		edtPhone.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				ll_register_img.setVisibility(View.GONE);
				return false;
			}
		});
		
		edtVerCode.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				ll_register_img.setVisibility(View.GONE);
				return false;
			}
		});
		
		edtPsd.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				ll_register_img.setVisibility(View.GONE);
				return false;
			}
		});*/
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//友盟统计
		MobclickAgent.onPageStart(mPageName);
	    MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		//友盟统计
		MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
		
		if (data == null) {
			return;
		}

		if (requestCode == READ_DEAL) {

			if (resultCode == RESULT_OK) {

				if (data.getStringExtra("flag").equals("1")) {
					mCheckBox.setChecked(true);
				}
				checkStatus();
			}
		}
	}

	/**
	 * 获取验证码
	 * 
	 * @param v
	 */
	public void getVerCode() {

		strPhone = edtPhone.getText().toString();
		if (TextUtils.isEmpty(strPhone)) {
			CommandTools.showToast("请输入手机号");
			return;
		}

		if (!CommandTools.isMobileNO(strPhone)) {
			CommandTools.showToast("手机号不合法");
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("phone", strPhone);
			jsonObject.put("codeType", "register");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postData(mContext, Constant.getVerCode_url,"验证码获取中",true, jsonObject, new MyCallback() {
			@Override
			public void callback(JSONObject jsonObject) {
				Log.i("hexiuhui---", jsonObject.toString());
				CustomProgress.dissDialog();
				try {
					TimeCount tc = new TimeCount(90000, 1000);
					tc.start();

					CommandTools.showToast(jsonObject.getString("message"));
					edtVerCode.requestFocus();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * 密码的显示与隐藏
	 * 
	 * @param v
	 */
	public void showPwd(View v) {
		if (isShow == true) {
			isShow = false;
			edtPsd.setTransformationMethod(PasswordTransformationMethod.getInstance());
			imgPsd.setImageResource(R.drawable.img_psd_close);
		} else {
			isShow = true;
			edtPsd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
			imgPsd.setImageResource(R.drawable.img_psd_open);
		}

		edtPsd.setSelection(edtPsd.getText().length());
	}

	/**
	 * 保存数据到下一步
	 * 
	 * @param v
	 */
	public void nextStep(View v) {
		//友盟统计
		MobclickAgent.onEvent(RegisterActivity.this, "btn_register");
		
		strPhone = edtPhone.getText().toString();
		strVerCode = edtVerCode.getText().toString();
		strPsd = edtPsd.getText().toString();
		String codeStr = codeEt.getText().toString();

		if (TextUtils.isEmpty(strPhone) || TextUtils.isEmpty(strVerCode) || TextUtils.isEmpty(strPsd)) {
			CommandTools.showToast("请录入完整数据");
			return;
		}

		if (!CommandTools.isMobileNO(strPhone)) {
			CommandTools.showToast("手机号不合法");
			return;
		}

		if (strVerCode.length() != 6) {
			CommandTools.showToast("请输入6位验证码");
			return;
		}

		if (strPsd.length() < 6 || strPhone.length() > 30) {
			CommandTools.showToast("密码长度在6-30之间");
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {

			jsonObject.put("phone", AES.encrypt(strPhone)); // 手机号
			jsonObject.put("code", strVerCode);// 短信验证码（必填
			jsonObject.put("accountPwd", AES.encrypt(strPsd));// 密码
			if (!codeStr.equals("")) { 
				jsonObject.put("inviteCode", codeStr);// 邀请码
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postData(mContext, Constant.register_url, "",false,jsonObject, new MyCallback() {
			@Override
			public void callback(JSONObject jsonObject) {
				try {
					String strRemark = jsonObject.getString("message");
					CommandTools.showToast(strRemark);
					if (jsonObject.optInt("success") == 0) {
						my_login(strPhone, strPsd);// 调用登录接口 获取token
//							 账号密码存储到配置文件
						SharedPreferencesUtils.setParam(Constant.SP_LOGIN_NAME, strPhone);
						SharedPreferencesUtils.setParam(Constant.SP_LOGIN_PSD, strPsd);
						Intent intent = new Intent(getApplication(), PerfectNameActivity.class);
						startActivity(intent);
						finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});

	}

	/**
	 * 调取登录接口 获取token
	 */
	@SuppressLint("NewApi")
	public void my_login(String userName, String accountPwd) {
		// 获取个推clientId
		String clientId = MyApplication.getInstance().m_strPushMessageClientId;
		if (clientId.isEmpty()) {
			clientId = PushManager.getInstance().getClientid(RegisterActivity.this);
			if (clientId != null && !clientId.isEmpty()) {
				MyApplication.getInstance().m_strPushMessageClientId = clientId;
			}
		}
		JSONObject object = new JSONObject();
		try {
			object.put("userName", AES.encrypt(userName));
			object.put("accountPwd", AES.encrypt(accountPwd));
			object.put("imei", CommandTools.getMIME(MyApplication.getInstance()));//
			object.put("clientId", clientId + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		RequestUtilNet.postData(RegisterActivity.this, Constant.Login_url,"",false, object, new MyCallback() {
			@Override
			public void callback(JSONObject jsonObject) {
				if (jsonObject.optInt("success") == 0) {
					JSONObject jsonObj = jsonObject;
					Logs.e("zdkj", jsonObj.toString() + "---school");
					int res = jsonObj.optInt("success");
					String strMsg = jsonObj.optString("message");
					MyApplication myApplication = MyApplication.getInstance();
					JSONObject dataObject = jsonObj.optJSONObject("data");
					myApplication.m_userInfo.toKen = dataObject.optString("token");
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {// 获取 back键
			back(null);
		}

		return false;
	}

	/**
	 * @param v
	 */
	public void readDeal(View v) {

		Intent intent = new Intent(RegisterActivity.this, DealActivity.class);
		startActivityForResult(intent, READ_DEAL);
	}

	/**
	 * 取消注册
	 * 
	 * @param v
	 */
	public void back(View v) {
		finish();
	}

	public void checkDeal(View v) {

		if (mCheckBox.isChecked()) {
			mCheckBox.setChecked(false);
		} else {
			mCheckBox.setChecked(true);
		}

		checkStatus();
	}

	/**
	 * 对EditText进行录入监听
	 * 
	 * @param edt
	 */
	private void setEditableListener(EditText edt) {

		edt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				checkStatus();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
	}

	/**
	 * 检查数据录入是否完整 修改注册、确定、下一步按钮状态
	 */
	private void checkStatus() {

		String strPhone = edtPhone.getText().toString();
		String strCode = edtVerCode.getText().toString();
		String strPsd = edtPsd.getText().toString();

		if (strPhone.length() != 11 || strCode.length() != 6
				|| strPsd.length() < 6 || !mCheckBox.isChecked()) {
			btnRegister.setBackgroundResource(R.drawable.register_valid_gray);
			btnRegister.setClickable(false);
		} else {
			btnRegister.setBackgroundResource(R.drawable.register_valid);
			btnRegister.setClickable(true);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发

			btnNumber.setText("重新验证");
			btnNumber.setTextColor(Color.WHITE);
			btnNumber.setBackgroundResource(R.drawable.register_valid);
			btnNumber.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示

			btnNumber.setClickable(false);

			String strSec = millisUntilFinished / 1000 + "s";
			SpannableString spanStack = new SpannableString(strSec);
			spanStack.setSpan(new ForegroundColorSpan(Color.RED), 0,
					spanStack.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

			btnNumber.setBackgroundResource(R.drawable.register_valid_gray);
			btnNumber.setText("");
			btnNumber.append(spanStack);
			btnNumber.append("后重发");
		}
	}

	public Handler smsHandler = new Handler() {
		// 这里可以进行回调的操作

	};

	public void getSmsFromPhone() {

		ContentResolver cr = getContentResolver();
		// 这个projection参数初始化要尤其注意哦！由于我只想获取验证码内容，我就只填了“body”，如果给位还需要获取发件人信息的话，可以继续添加
		String[] projection = new String[] { "body" };
		// String where = " address = '1069041810748872' AND date >  "
		// + (System.currentTimeMillis() - 60 * 1000);
		// 设置监听指定号码，10分钟内有效，如果您不需要指定号码的话，只需要去掉相应的条件就ok
		Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
		if (null == cur)
			return;
		if (cur.moveToFirst()) {
			String body = cur.getString(cur.getColumnIndex("body"));
			// 这里我是要获取自己短信服务号码中的验证码，务必记住这里获取的内容需要和projection字符串对应，否则短信数据库可能会找不到内容的。
			Pattern pattern = Pattern.compile("[0-9]{6}");
			// 我获取的验证码是4位阿拉伯数字，正则匹配规则您可能需要修改
			Matcher matcher = pattern.matcher(body);
			if (matcher.find()) {
				String res = matcher.group();
				edtVerCode.requestFocus();
				edtVerCode.setText(res);
			}
		}
	}

	class SmsObserver extends ContentObserver {
		public SmsObserver(Context context, Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			// 每当有新短信到来时，使用我们获取短消息的方法
			getSmsFromPhone();
		}
	}

	protected void onDestory() {
		super.onDestroy();

		getContentResolver().unregisterContentObserver(smsObserver);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_number_register:
			getVerCode();
			break;
		case R.id.wx_login_txt:
			SHARE_MEDIA weixin = SHARE_MEDIA.WEIXIN;
			SnsPlatform snsPlatform = weixin.toSnsPlatform();
			
			UMShareAPI.get(RegisterActivity.this).doOauthVerify(RegisterActivity.this, snsPlatform.mPlatform, authListener);
			break;
		case R.id.qq_login_txt:
			SHARE_MEDIA qq = SHARE_MEDIA.QQ;
			SnsPlatform qqSnsPlatform = qq.toSnsPlatform();
			
			UMShareAPI.get(RegisterActivity.this).doOauthVerify(RegisterActivity.this, qqSnsPlatform.mPlatform, authListener);
			break;
		case R.id.wb_login_txt:
			SHARE_MEDIA sina = SHARE_MEDIA.SINA;
			SnsPlatform platform = sina.toSnsPlatform();
			
			UMShareAPI.get(RegisterActivity.this).doOauthVerify(RegisterActivity.this, platform.mPlatform, authListener);
			break;
		default:
			break;
		}
	}
	
	UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
        	String source = "";
        	String uid = "";
        	if (platform == SHARE_MEDIA.QQ) {
        		uid = data.get("uid");
        		source = "qq";
        	} else if (platform == SHARE_MEDIA.WEIXIN) {
        		uid = data.get("openid");
        		source = "weixin";
        	}
        	
        	mTaskRequestIsbind = requestIsbind(source, uid);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
        	Log.i("hexiuhui---", action + "失败："+t.getMessage());
            Toast.makeText(RegisterActivity.this, "失败："+t.getMessage(),Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(RegisterActivity.this, "取消了",Toast.LENGTH_LONG).show();

        }
    };
    
  //是否绑定手机
    protected LoadTextNetTask requestIsbind(final String source, final String thirdpartyToken) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestIsbind = null;
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONObject jsonObject = jsonObj.getJSONObject("data");
							int rt = jsonObject.getInt("result");
							//如果没有绑定手机就去绑定
							if (rt == 0) {
								Intent intent = new Intent(RegisterActivity.this, BindPhoneActivity.class);
								intent.putExtra("source", source);
								intent.putExtra("thirdpartyToken", thirdpartyToken);
								startActivity(intent);
							} else {  //已经绑定手机直接登录
								// 获取个推clientId
								String clientId = MyApplication.getInstance().m_strPushMessageClientId;
								if (clientId.isEmpty()) {
									clientId = PushManager.getInstance().getClientid(RegisterActivity.this);
									if (clientId != null && !clientId.isEmpty()) {
										MyApplication.getInstance().m_strPushMessageClientId = clientId;
									}
								}
								
								String imei = CommandTools.getMIME(RegisterActivity.this);
								mTaskRequestGaveLogin = requestGeveLogin(clientId, imei, source, thirdpartyToken);
							}
						} else {
							String message = jsonObj.getString("message");

							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(RegisterActivity.this);
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(RegisterActivity.this, "获取数据中...", false, null);
		LoadTextNetTask task = UserService.isBind(source, thirdpartyToken, listener, null);
		return task;
	}

    //第三方登录
    protected LoadTextNetTask requestGeveLogin(String clientId, String imei, final String source, final String thirdpartyToken) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestGaveLogin = null;
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
						Util.showJsonParseErrorMessage(RegisterActivity.this);
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(RegisterActivity.this, "获取数据中...", false, null);
		LoadTextNetTask task = UserService.geveLogin(clientId, imei, source, thirdpartyToken, listener, null);
		return task;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString("strPhone", strPhone);
		outState.putString("strVerCode", strVerCode);
		outState.putString("strPsd", strPsd);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		strPhone = savedInstanceState.getString("strPhone");
		strVerCode = savedInstanceState.getString("strVerCode");
		strPsd = savedInstanceState.getString("strPsd");
	}

}
