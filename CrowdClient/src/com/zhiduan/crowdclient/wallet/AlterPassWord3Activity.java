package com.zhiduan.crowdclient.wallet;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.activity.ForgetPasswordActivity;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.PayMentService;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CustomProgress;

/**
 * 设置交易密码
 * 
 * @author hexiuhui
 * 
 */
public class AlterPassWord3Activity extends BaseActivity implements
		OnClickListener, OnKeyListener {

	private EditText mEt1;
	private EditText mEt2;
	private EditText mEt3;
	private EditText mEt4;
	private EditText mEt5;
	private EditText mEt6;
	private EditText mEt7;
	private int length = 0;

	private RelativeLayout mForgetPassword;
	private TextView mTvText;
	private String number;
	private String oldPwd;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_update_wallet_password_raw, this);
	}

	@Override
	public void initView() {
		// 设置标题栏
		setTitleView();

		mForgetPassword = (RelativeLayout) findViewById(R.id.update_wallet_password_raw_rl_forget);

		mEt1 = (EditText) findViewById(R.id.update_wallet_password_raw_et1);
		mEt2 = (EditText) findViewById(R.id.update_wallet_password_raw_et2);
		mEt3 = (EditText) findViewById(R.id.update_wallet_password_raw_et3);
		mEt4 = (EditText) findViewById(R.id.update_wallet_password_raw_et4);
		mEt5 = (EditText) findViewById(R.id.update_wallet_password_raw_et5);
		mEt6 = (EditText) findViewById(R.id.update_wallet_password_raw_et6);
		mEt7 = (EditText) findViewById(R.id.update_wallet_password_raw_et7);

		mTvText = (TextView) findViewById(R.id.update_wallet_password_raw_tv_text);

		mEt7.setOnKeyListener(this);
		mEt7.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				String text = arg0.toString();
				if (text.length() == length) {

				} else {
					switch (text.length()) {
					case 1:
						mEt1.setText(text.subSequence(text.length() - 1,
								text.length()));
						length = text.length();
						break;
					case 2:
						mEt2.setText(text.subSequence(text.length() - 1,
								text.length()));
						length = text.length();
						break;
					case 3:
						mEt3.setText(text.subSequence(text.length() - 1,
								text.length()));
						length = text.length();
						break;
					case 4:
						mEt4.setText(text.subSequence(text.length() - 1,
								text.length()));
						length = text.length();
						break;
					case 5:
						mEt5.setText(text.subSequence(text.length() - 1,
								text.length()));

						length = text.length();
						break;
					case 6:
						mEt6.setText(text.subSequence(text.length() - 1,
								text.length()));
						length = text.length();
						if (number.equals(text)) {
							alterPassWord(oldPwd, text);
						} else {
							CommandTools.showToast("两次输入的密码不一致");
							empty();
						}
						// i.passwordSucceed(text);
						break;
					default:
						break;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		mTvText.setText("确认新密码");
		mForgetPassword.setVisibility(View.GONE);

		mEt7.setFocusable(true);
		mEt7.setFocusableInTouchMode(true);
		mEt7.requestFocus();

		InputMethodManager inputManager =

		(InputMethodManager) mEt7.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);

		inputManager.showSoftInput(mEt7, 0);

	}
	
	@Override
	protected void onResume() {
		super.onResume();

		Timer timer = new Timer();

		timer.schedule(new TimerTask()

		{

			public void run()

			{

				InputMethodManager inputManager =

				(InputMethodManager) mEt7.getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);

				inputManager.showSoftInput(mEt7, 0);

			}

		},

		998);
	}
	@Override
	public void initData() {
		Intent intent = getIntent();
		number = intent.getStringExtra("number");
		oldPwd = intent.getStringExtra("oldPwd");
	}

	private void setTitleView() {
		setTitle("修改交易密码");
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent arg2) {
		if (keyCode == KeyEvent.KEYCODE_DEL) {
			empty();
		}
		return false;
	}

	// 清空
	private void empty() {
		mEt1.setText("");
		mEt2.setText("");
		mEt3.setText("");
		mEt4.setText("");
		mEt5.setText("");
		mEt6.setText("");
		mEt7.setText("");
		length = 0;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.update_wallet_password_raw_rl_forget:
			Intent intent = new Intent(AlterPassWord3Activity.this,
					ForgetPasswordActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	/**
	 * 修改钱包交易密码
	 * 
	 * @param oldPwd
	 * @param text
	 */
	private void alterPassWord(String oldPwd, String text) {

		OnPostExecuteListener listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					JSONObject jsonObj;
					try {
						jsonObj = new JSONObject(mresult.m_strContent);
						Logs.i("zd", jsonObj.toString() + "---");
						int success = jsonObj.getInt("success");
						if (success == 0) {
							CommandTools.showToast("修改密码成功");
							setResult(10);
							finish();

						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
							setResult(11);
							finish();
						}

					} catch (JSONException e) {
						e.printStackTrace();
						Util.showJsonParseErrorMessage(mContext);
					}
				} else {
					Util.showNetErrorMessage(mContext, result.m_nResultCode);
				}
				CustomProgress.dissDialog();

			}
		};
		CustomProgress.showDialog(mContext, "正在修改", false, null);
		PayMentService.alterPassWord(text, oldPwd, listener, null);
	}
}
