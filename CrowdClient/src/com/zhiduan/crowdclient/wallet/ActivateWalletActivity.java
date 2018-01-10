package com.zhiduan.crowdclient.wallet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.service.PayMentService;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.ClearEditText;
import com.zhiduan.crowdclient.view.CustomProgress;

/**
 * 激活钱包或忘记交易密码
 * 
 * @author yxx
 * 
 * @date 2016-5-20 下午2:30:26
 * 
 */
public class ActivateWalletActivity extends BaseActivity {

	private Context mContext;

	private EditText edtId;
	private EditText edtCode;
	private Button btnCode;
	private Button btnNext;
	private String id_no="";
	private String strPhone;
	private TextView tv_activate_id;
	private SmsObserver smsObserver;
	private LinearLayout ll_auth_user;
	MyApplication myApp = MyApplication.getInstance();
	private Uri SMS_INBOX = Uri.parse("content://sms/");
	private TextView tv_activate_phone;
	private TextView tv_activate_name;
	private ClearEditText edt_activate_name;
	String strId="";
	String strName="";
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_activate_wallet, this);

	}

	@Override
	public void initView() {

		mContext = ActivateWalletActivity.this;
		id_no=getIntent().getStringExtra("idNo");
		ll_auth_user=(LinearLayout) findViewById(R.id.ll_auth_user);
		tv_activate_phone=(TextView) findViewById(R.id.tv_activate_phone);
		tv_activate_name=(TextView) findViewById(R.id.tv_activate_name);
		tv_activate_id=(TextView) findViewById(R.id.tv_activate_id);
		edtId = (EditText) findViewById(R.id.edt_activate_id);
		edtCode = (EditText) findViewById(R.id.edt_activate_code);
		edt_activate_name=(ClearEditText) findViewById(R.id.edt_activate_name);
		btnCode = (Button) findViewById(R.id.btn_activate_code);
		btnNext = (Button) findViewById(R.id.btn_activate_next);

		setEditableListener(edtId);
		setEditableListener(edtCode);
		
	}

	private int type = 0;
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		getPersonalMessage();
		super.onResume();
	}
	@Override
	public void initData() {
		
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		if ("忘记交易密码".equals(title)) {
			type = 1;
		}
		if (Constant.is_h5==true) {
			ll_auth_user.setVisibility(View.VISIBLE);
		}
		setTitle(title);
		
	}
	/**
	 * 用户实名认证
	 */
	private void UserCertification(String name,final String id) {
		JSONObject jsonObject = new JSONObject();
		try {
		jsonObject.put("idNo", id);
		jsonObject.put("realName", name);
		} catch (Exception e) {
			// TODO: handle exception
		}
		RequestUtilNet.postDataToken(ActivateWalletActivity.this, Constant.Certification_url,
				jsonObject, new RequestCallback() {
					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						try {
							if (success == 0) {
								getPersonalMessage();
								Intent intent = new Intent(ActivateWalletActivity.this, SettingTradePwdActivity.class);
								String strCode = edtCode.getText().toString();
								intent.putExtra("idNo", strId);
								intent.putExtra("phone", strPhone);
								intent.putExtra("code", strCode);
								intent.putExtra("type", type);
								intent.putExtra("title", "激活钱包");
								startActivityForResult(intent, 0);
							}else {
								CommandTools.showToast(remark);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

	}
	/**
	 * 对EditText进行录入监听
	 * 
	 * @param edt
	 */
	private void setEditableListener(EditText edt) {

		edt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

				checkStatus();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

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
		String strId="";
		if (myApp.m_userInfo.verifyStatus==2||myApp.m_userInfo.verifyStatus==4) {
			strId=tv_activate_id.getText().toString();
			strPhone=tv_activate_phone.getText().toString();
			
		}else {
			strId = edtId.getText().toString();
			 strPhone = tv_activate_phone.getText().toString();
		}
		
		String strCode = edtCode.getText().toString();
		if (strId.length() < 5 || strPhone.length() != 11
				|| strCode.length() != 6) {
			btnNext.setBackgroundResource(R.drawable.register_valid_gray);
			btnNext.setClickable(false);
		} else {
			btnNext.setBackgroundResource(R.drawable.register_valid);
			btnNext.setClickable(true);

		}
	}

	/**
	 * 获取验证码
	 * 
	 * @param v
	 */
	public void getVerCode(View v) {
		
		strPhone = tv_activate_phone.getText().toString();
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
			if (type == 0) {

				jsonObject.put("codeType", "activatewallet");
			} else {
				jsonObject.put("codeType", "resetpwd");

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "user/code/send.htm";
		RequestUtilNet.postData(mContext, strUrl, "验证码获取中", true, jsonObject,
				new MyCallback() {

					@Override
					public void callback(JSONObject jsonObject) {

						CustomProgress.dissDialog();
						try {

							TimeCount tc = new TimeCount(90000, 1000);
							tc.start();

							CommandTools.showToast(jsonObject
									.getString("message"));
							edtCode.requestFocus();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

	}

	/**
	 * 下一步
	 * 
	 * @param v
	 */
	public void nextStep(View v) {
		
		strName=edt_activate_name.getText().toString();
		if (myApp.m_userInfo.verifyStatus==2||myApp.m_userInfo.verifyStatus==4) {
			strId = tv_activate_id.getText().toString();
			strPhone=tv_activate_phone.getText().toString();
			strName=tv_activate_name.getText().toString();
		}else {
			strName=edt_activate_name.getText().toString();
			strId = edtId.getText().toString();
			strPhone=tv_activate_phone.getText().toString();
		}
		if (Constant.is_h5==true&&myApp.m_userInfo.verifyStatus==0) {
			UserCertification(strName, strId);
		}else {
			Intent intent = new Intent(ActivateWalletActivity.this, SettingTradePwdActivity.class);
			String strPhone = tv_activate_phone.getText().toString();
			String strCode = edtCode.getText().toString();
			intent.putExtra("idNo", strId);
			intent.putExtra("phone", strPhone);
			intent.putExtra("code", strCode);
			intent.putExtra("type", type);
			intent.putExtra("title", "激活钱包");
			startActivityForResult(intent, 0);
		
		}
	}

	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发

			btnCode.setText("重新验证");
			btnCode.setTextColor(Color.WHITE);
			btnCode.setBackgroundResource(R.drawable.register_valid);
			btnCode.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示

			btnCode.setClickable(false);

			String strSec = millisUntilFinished / 1000 + "s";
			SpannableString spanStack = new SpannableString(strSec);
			spanStack.setSpan(new ForegroundColorSpan(Color.RED), 0,
					spanStack.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

			btnCode.setBackgroundResource(R.drawable.register_valid_gray);
			btnCode.setText("");
			btnCode.append(spanStack);
			btnCode.append("后重发");
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
				edtCode.setText(res);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			setResult(10);
			finish();
		}
	}
	/**
	 * 获取个人信息
	 */
	private void getPersonalMessage() {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("userType", "1");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postData(CommandTools.getGlobalActivity(),
				Constant.getUserInfo_url, jsonObject, new MyCallback() {

					@Override
					public void callback(JSONObject jsonObject) {
						try {
							jsonObject = jsonObject.optJSONObject("data");
							myApp.m_userInfo.m_strUserName = jsonObject
									.optString("realName");
							myApp.m_userInfo.m_strUserHeadPic = jsonObject
									.optString("icon");
							myApp.m_userInfo.m_user_income = jsonObject
									.optLong("totalIncome");
							myApp.m_userInfo.verifyStatus = jsonObject
									.optInt("state");
							myApp.m_userInfo.m_strUserSign = jsonObject
									.optString("signNumber");
							myApp.m_userInfo.m_strUserGender = jsonObject
									.optString("gender");
							myApp.m_userInfo.m_nick_name = jsonObject
									.optString("nickname");
							Logs.i("zdkj--sfz---", "sfz = "
									+ jsonObject
									.optString("idNo"));
							String idNo=jsonObject
									.optString("idNo");
							String strPhone = (String) SharedPreferencesUtils.getParam(
							Constant.SP_LOGIN_NAME, "");
							int state=jsonObject.optInt("state");
							if (state==4) {
								
							}
							if (!TextUtils.isEmpty(strPhone)) {
								tv_activate_phone.setText(strPhone);
							}
							if (!TextUtils.isEmpty(idNo)) {
								tv_activate_id.setText(jsonObject
										.optString("idNo"));
								tv_activate_name.setText(jsonObject
									.optString("realName"));
								edt_activate_name.setVisibility(View.GONE);
								tv_activate_name.setVisibility(View.VISIBLE);
								edtId.setVisibility(View.GONE);
								tv_activate_id.setVisibility(View.VISIBLE);
							}
							
							// load_img(jsonObject.optString("icon"));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

}
