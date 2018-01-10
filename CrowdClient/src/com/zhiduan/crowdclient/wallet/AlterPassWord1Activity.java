package com.zhiduan.crowdclient.wallet;

import java.util.Timer;
import java.util.TimerTask;

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

/**
 * 设置交易密码
 * 
 * @author wfq
 * 
 */
public class AlterPassWord1Activity extends BaseActivity implements
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
						Intent intent = new Intent(AlterPassWord1Activity.this,
								AlterPassWord2Activity.class);
						intent.putExtra("oldPwd", text);
						startActivityForResult(intent, 10);
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

		mForgetPassword.setOnClickListener(this);

		mEt7.setFocusable(true);
		mEt7.setFocusableInTouchMode(true);
		mEt7.requestFocus();

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
	}

	private void setTitleView() {
		setTitle("修改交易密码");
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent arg2) {
		if (keyCode == KeyEvent.KEYCODE_DEL) {
			mEt1.setText("");
			mEt2.setText("");
			mEt3.setText("");
			mEt4.setText("");
			mEt5.setText("");
			mEt6.setText("");
			mEt7.setText("");
			length = 0;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.update_wallet_password_raw_rl_forget:
			Intent intent = new Intent(AlterPassWord1Activity.this,
					ActivateWalletActivity.class);
			intent.putExtra("title", "忘记交易密码");
			startActivityForResult(intent, 10);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 10) {
			finish();
		}
		if(requestCode==10){
			mEt1.setText("");
			mEt2.setText("");
			mEt3.setText("");
			mEt4.setText("");
			mEt5.setText("");
			mEt6.setText("");
			mEt7.setText("");
			length = 0;
		}
		if (resultCode == 11) {
			mEt1.setText("");
			mEt2.setText("");
			mEt3.setText("");
			mEt4.setText("");
			mEt5.setText("");
			mEt6.setText("");
			mEt7.setText("");
			length = 0;
		}
		if (resultCode == 1) {
			finish();
		}
	}
}
