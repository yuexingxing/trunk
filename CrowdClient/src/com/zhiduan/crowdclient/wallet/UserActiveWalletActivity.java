package com.zhiduan.crowdclient.wallet;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.R.drawable;
import com.zhiduan.crowdclient.R.id;
import com.zhiduan.crowdclient.R.layout;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
import com.zhiduan.crowdclient.util.VoiceHint;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.ClearEditText;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.wallet.ActivateWalletActivity.TimeCount;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * 	
 * <pre>
 * Description	用户实名认证界面
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2017-3-8 下午3:29:11  
 * </pre>
 */
public class UserActiveWalletActivity extends BaseActivity {

	private Button btn_user_active;
	private ClearEditText edt_name_auth_manager;// 姓名
	private ClearEditText edt_id_auth_manager;// 身份证
	private ClearEditText edt_activate_phone;
	private ClearEditText edt_activate_code;
	private String strPhone;
	private Button btnCode;
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_user_active_wallet, this);
		setTitle("激活钱包");
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		btn_user_active=(Button) findViewById(R.id.btn_user_active);
		btnCode = (Button) findViewById(R.id.btn_activate_code);
		edt_id_auth_manager = (ClearEditText) findViewById(R.id.edt_activate_id);
		edt_name_auth_manager = (ClearEditText) findViewById(R.id.edt_activate_name);
		edt_activate_phone = (ClearEditText) findViewById(R.id.edt_activate_phone);
		edt_activate_code = (ClearEditText) findViewById(R.id.edt_activate_code);
		setEditableListener(edt_id_auth_manager);
		setEditableListener(edt_name_auth_manager);
		setEditableListener(edt_activate_phone);
		setEditableListener(edt_activate_code);
		
		String phone = (String) SharedPreferencesUtils.getParam(
				Constant.SP_LOGIN_NAME, "");
		edt_activate_phone.setText(phone);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 检查数据录入是否完整 修改注册、确定、下一步按钮状态
	 */
	private void checkStatus() {

		String tv_id = edt_id_auth_manager.getText().toString();
		String tv_name = edt_name_auth_manager.getText().toString();
		String strPhone = edt_activate_phone.getText().toString();
		String strCode = edt_activate_code.getText().toString();
		if (tv_name.length() == 0 || tv_id.length() == 0 ||strPhone.length() != 11
				|| strCode.length() != 6) {
			btn_user_active
					.setBackgroundResource(R.drawable.register_valid_gray);
			btn_user_active.setClickable(false);
		} else {
			btn_user_active
					.setBackgroundResource(R.drawable.register_valid);
			btn_user_active.setClickable(true);
		}
	}
	/**
	 * 对TextView进行录入监听
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
	public void ActiveUser(View view) {
		//校验验证码
		ValidateIdNo(edt_activate_code.getText().toString());
		
	}
	/**
	 * 获取验证码
	 * 
	 * @param v
	 */
	public void getVerCode(View v) {

		strPhone = edt_activate_phone.getText().toString();
		String idNo=edt_id_auth_manager.getText().toString();
		if (!CommandTools.personIdValidation(idNo)) {
			VoiceHint.playErrorSounds();
			CommandTools.showToast("身份证无效");
			return;
		}
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

			jsonObject.put("codeType", "activatewallet");
		
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
							edt_activate_code.requestFocus();
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
		RequestUtilNet.postDataToken(UserActiveWalletActivity.this, Constant.Certification_url,
				jsonObject, new RequestCallback() {
					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						try {
							if (success == 0) {
								jsonObject = jsonObject.optJSONObject("data");
								CommandTools.showToast(remark);
								Intent intent = new Intent(UserActiveWalletActivity.this, ActivateWalletActivity.class);
								intent.putExtra("idNo", id);
								intent.putExtra("title", "激活钱包");
								startActivity(intent);
								finish();
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
	 * 用户激活钱包验证码校验
	 */
	private void ValidateIdNo(String id) {
		
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("code", edt_activate_code.getText().toString());
		jsonObject.put("phone", strPhone);
		jsonObject.put("codeType", "activatewallet");
		} catch (Exception e) {
			e.printStackTrace();
		}
		RequestUtilNet.postDataToken(UserActiveWalletActivity.this, Constant.validateCode_url,
				jsonObject, new RequestCallback() {
					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						try {
							if (success == 0) {
								String tv_id = edt_id_auth_manager.getText().toString();
								String tv_name = edt_name_auth_manager.getText().toString();
								UserCertification(tv_name, tv_id);
							}else {
								CommandTools.showToast(remark);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

	}
}
