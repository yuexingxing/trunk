package com.zhiduan.crowdclient.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.AES;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.CustomProgress;
/**
 * 
 * <pre>
 * Description	忘记密码
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2017-2-6 上午11:36:03  
 * </pre>
 */
public class ForgetPasswordActivity extends BaseActivity {

	private final String mPageName = "ForgetPasswordActivity";

	private Context mContext;

	private Button btnGetCode;

	private EditText edtPhone;
	private EditText edtCode;

	private EditText edtPsd, et_new_repsw;
	private ImageView imgPsd;

	private String strCode;
	private String strPhone;
	private Button bt_submit;

	private SmsObserver smsObserver;
	private Uri SMS_INBOX = Uri.parse("content://sms/");

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_forget_password, this);

		smsObserver = new SmsObserver(this, smsHandler);
		getContentResolver().registerContentObserver(SMS_INBOX, true,
				smsObserver);
	}

	@Override
	public void initView() {
		bt_submit = (Button) findViewById(R.id.bt_submit);
		et_new_repsw = (EditText) findViewById(R.id.et_new_repsw);
		mContext = ForgetPasswordActivity.this;

		btnGetCode = (Button) findViewById(R.id.time_password);
		edtPhone = (EditText) findViewById(R.id.et_pho_num);
		edtCode = (EditText) findViewById(R.id.et_et_yanzheng);

		edtPsd = (EditText) findViewById(R.id.et_new_psw);
		imgPsd = (ImageView) findViewById(R.id.imb_password_reset);

		setEditableListener(edtCode);
		setEditableListener(edtPhone);
//		setEditableListener(et_new_repsw);
		setEditableListener(edtPsd);
	}

	@Override
	public void initData() {

		setTitle("忘记密码");
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

	/**
	 * 获取验证码
	 * 
	 * @param v
	 */
	public void getVerCode(View v) {

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
			jsonObject.put("codeType", "resetpwd");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postData(mContext, Constant.getVerCode_url, "验证码获取中",
				true, jsonObject, new MyCallback() {

			@Override
			public void callback(JSONObject jsonObject) {
				CustomProgress.dissDialog();

				try {
					CommandTools.showToast(jsonObject
							.getString("message"));
					edtCode.requestFocus();
					TimeCount tc = new TimeCount(90000, 1000);
					tc.start();
				} catch (JSONException e) {

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

		String strPhone = edtPhone.getText().toString();
		String strCode = edtCode.getText().toString();
		String strPsd = edtPsd.getText().toString();
//		String new_repsw = et_new_repsw.getText().toString();

		if (strPhone.length() != 11 || strCode.length() != 6
				|| strPsd.length() < 6 ) {
			bt_submit.setBackgroundResource(R.drawable.register_valid_gray);
			bt_submit.setClickable(false);
		} else {
			bt_submit.setBackgroundResource(R.drawable.register_valid);
			bt_submit.setClickable(true);
		}
	}

	/**
	 * 根据焦点位置判断执行哪个函数 校验短信验证码和保存密码
	 * 
	 * @param v
	 */
	public void ok(View v) {
		//友盟统计
		MobclickAgent.onEvent(ForgetPasswordActivity.this, "bt_submit");

		savePassword();
	}

	/**
	 * 密码的显示与隐藏
	 * 
	 * @param v
	 */
	private boolean isShow = false;

	public void showPwd(View v) {

		if (isShow == true) {
			isShow = false;
			edtPsd.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
			imgPsd.setImageResource(R.drawable.img_psd_close);
		} else {
			isShow = true;
			edtPsd.setTransformationMethod(HideReturnsTransformationMethod
					.getInstance());
			imgPsd.setImageResource(R.drawable.img_psd_open);
		}

		edtPsd.setSelection(edtPsd.getText().length());
	}

	/**
	 * 校验码验证通过后 保存新密码
	 */
	private void savePassword() {

		strPhone = edtPhone.getText().toString();
		strCode = edtCode.getText().toString();
		String strPsd = edtPsd.getText().toString();
//		String repsd = et_new_repsw.getText().toString();

		if (TextUtils.isEmpty(strPhone)) {
			CommandTools.showToast("手机号不能为空");
			return;
		}

		if (TextUtils.isEmpty(strCode)) {
			CommandTools.showToast("校验码不能为空");
			return;
		}

		if (TextUtils.isEmpty(strPsd)) {
			CommandTools.showToast("密码不能为空");
			return;
		}

//		if (TextUtils.isEmpty(repsd)) {
//			CommandTools.showToast("确认密码不能为空");
//			return;
//		}
		if (strCode.length() != 6) {
			CommandTools.showToast("请输入6位验证码");
			return;
		}
//		if (!repsd.equals(strPsd)) {
//			CommandTools.showToast("两次输入的密码不一致");
//			return;
//		}
		if (strPsd.length() < 6 || strPhone.length() > 30) {
			CommandTools.showToast("密码长度在6-30位之间");
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {

			MyApplication.getInstance().sendTime = CommandTools.getTimes();
			MyApplication.getInstance().mRandom = CommandTools.CeShi();

			jsonObject.put("code", strCode); // 短信验证码（必填
			jsonObject.put("userName", AES.encrypt(strPhone));// 用户名（必填）
			jsonObject.put("newPwd", AES.encrypt(strPsd));// （必填）用户新密码
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postData(mContext, Constant.findpwd_url, "密码重置中", false, jsonObject, new MyCallback() {

			@Override
			public void callback(JSONObject jsonObject) {

				try {
					int res = jsonObject.getInt("success");
					String remark = jsonObject.getString("message");
					CommandTools.showToast(remark);

					if(res == 0){
						finish();
					}
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

			btnGetCode.setText("重新验证");
			btnGetCode.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示

			btnGetCode.setClickable(false);
			btnGetCode.setText("请等待" + millisUntilFinished / 1000 + "秒");
		}
	}

	protected void onStop() {
		super.onStop();

		if (smsObserver != null) {
			getContentResolver().unregisterContentObserver(smsObserver);
		}
	}

	public Handler smsHandler = new Handler() {
		// 这里可以进行回调的操作
		// TODO

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
				edtCode.requestFocus();
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

	}
}
