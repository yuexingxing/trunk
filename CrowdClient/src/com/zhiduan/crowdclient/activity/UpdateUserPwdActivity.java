package com.zhiduan.crowdclient.activity;

import org.json.JSONException;
import org.json.JSONObject;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.R.drawable;
import com.zhiduan.crowdclient.R.id;
import com.zhiduan.crowdclient.R.layout;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.service.UserService;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CustomProgress;

import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * 
 * <pre>
 * Description 修改登录密码
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-6-11 下午3:55:44  
 * </pre>
 */
public class UpdateUserPwdActivity extends BaseActivity implements
		OnClickListener {

	private Button mUpdatePassWordBtn;
	private EditText mOldPassWordText;
	private EditText mNewPassWordText;
	private EditText repeatNewPassWord;
	private LoadTextNetTask mUpdatePassWordRequest;
	private ImageView imgPsd1;
	private ImageView imgPsd2;
	private Button update_password_btn;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_update_user_pwd,
				UpdateUserPwdActivity.this);
		setTitle("修改密码");
	}

	@Override
	public void initView() {
		update_password_btn=(Button) findViewById(R.id.update_password_btn);
		repeatNewPassWord = (EditText) findViewById(R.id.repeat_new_password);
		mUpdatePassWordBtn = (Button) findViewById(R.id.update_password_btn);
		mOldPassWordText = (EditText) findViewById(R.id.old_password_et);
		mNewPassWordText = (EditText) findViewById(R.id.new_password_et);

		imgPsd1 = (ImageView) findViewById(R.id.imb_password_update_1);
		imgPsd2 = (ImageView) findViewById(R.id.imb_password_update_2);

		mUpdatePassWordBtn.setOnClickListener(this);
		setEditableListener(mNewPassWordText);
		setEditableListener(mOldPassWordText);
		setEditableListener(repeatNewPassWord);
	}

	@Override
	public void initData() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mUpdatePassWordRequest != null) {
			mUpdatePassWordRequest.cancel(true);
			mUpdatePassWordRequest = null;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.update_password_btn:
			String oldPwd = mOldPassWordText.getText().toString();
			String newPwd = mNewPassWordText.getText().toString();
			String repeatPwd = repeatNewPassWord.getText().toString();

			if ("".equals(oldPwd)) {
				Toast.makeText(UpdateUserPwdActivity.this, "旧密码不能为空!", 1)
						.show();
				return;
			} else if ("".equals(newPwd)) {
				Toast.makeText(UpdateUserPwdActivity.this, "请输入新密码!", 1).show();
				return;
			} else if (!newPwd.equals(repeatPwd)) {
				Toast.makeText(UpdateUserPwdActivity.this, "新密码输入不一致!", 1)
						.show();
				return;
			}

			if (newPwd.length() < 6 || newPwd.length() > 30) {
				CommandTools.showToast("密码长度在6-30位之间");
				return;
			}
			mUpdatePassWordRequest = updatePassWord(oldPwd, newPwd);
			break;

		default:
			break;
		}
	}
	/**
	 * 对EditText进行录入监听
	 * @param edt
	 */
	private void setEditableListener(EditText edt){

		edt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

				checkStatus();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
	}

	/**
	 * 检查数据录入是否完整
	 * 修改注册、确定、下一步按钮状态
	 */
	private void checkStatus(){

		String OldPass = mOldPassWordText.getText().toString();
		String NewPass = mNewPassWordText.getText().toString();
		String repeatNewPass = repeatNewPassWord.getText().toString();
		
		

		if(OldPass.length() <6 || NewPass.length() <6 || repeatNewPass.length() < 6){
			update_password_btn.setBackgroundResource(R.drawable.register_valid_gray);
			update_password_btn.setClickable(false);
		}else{
			update_password_btn.setBackgroundResource(R.drawable.register_valid);
			update_password_btn.setClickable(true);
		}
	}
	/**
	 * 修改密码
	 * @param oldPwd
	 * @param newPwd
	 * @return
	 */
	private LoadTextNetTask updatePassWord(final String oldPwd,
			final String newPwd) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {
				mUpdatePassWordRequest = null;

				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(
								mresult.m_strContent);
						Log.i("zdkj", jsonObj.toString() + "---");

						int res = jsonObj.getInt("success");

						if (res == 0) {
							String message = jsonObj.getString("message");

							Toast.makeText(UpdateUserPwdActivity.this, message,
									Toast.LENGTH_LONG).show();

							SharedPreferencesUtils.setParam(Constant.SP_LOGIN_PSD,
									"");

							Intent intent = new Intent(
									UpdateUserPwdActivity.this,
									LoginActivity.class);
							startActivity(intent);
							finish();
						} else {
							String strMsg = jsonObj.getString("message");
							Toast.makeText(UpdateUserPwdActivity.this, strMsg,
									Toast.LENGTH_LONG).show();

							String code = jsonObj.getString("code");
							if (code.equals("100")) {
								Intent intent = new Intent(
										UpdateUserPwdActivity.this,
										LoginActivity.class);
								startActivity(intent);
								finish();
							}
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(UpdateUserPwdActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(UpdateUserPwdActivity.this,
							result.m_nResultCode);
				}
				CustomProgress.dissDialog();
			}
		};

		CustomProgress.showDialog(UpdateUserPwdActivity.this, "", false, null);
		LoadTextNetTask task = UserService.updatePassword(oldPwd, newPwd,
				listener, null);

		return task;
	}

	/**
	 * 密码的显示与隐藏
	 * 
	 * @param v
	 */
	private boolean isShow1 = false;

	public void showNewPwd(View v) {

		if (isShow1 == true) {
			isShow1 = false;
			mNewPassWordText
					.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
			imgPsd1.setImageResource(R.drawable.img_psd_close);
		} else {
			isShow1 = true;
			mNewPassWordText
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
			imgPsd1.setImageResource(R.drawable.img_psd_open);
		}

		mNewPassWordText.setSelection(mNewPassWordText.getText().length());
	}

	/**
	 * 密码的显示与隐藏
	 * 
	 * @param v
	 */
	private boolean isShow2 = false;

	public void showRepeat(View v) {

		if (isShow2 == true) {
			isShow2 = false;
			repeatNewPassWord
					.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
			imgPsd2.setImageResource(R.drawable.img_psd_close);
		} else {
			isShow2 = true;
			repeatNewPassWord
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
			imgPsd2.setImageResource(R.drawable.img_psd_open);
		}

		repeatNewPassWord.setSelection(repeatNewPassWord.getText().length());
	}

}
