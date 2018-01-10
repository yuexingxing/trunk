package com.zhiduan.crowdclient.wallet;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
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
 * @author yxx
 * 
 * @date 2016-5-20 下午3:38:55
 * 
 */
public class SettingTradePwdActivity extends BaseActivity {

	private TextView tvTips;
	private EditText[] arrEdtPwd;
	private EditText edt7;
	private int[] mArrEdt = { R.id.dialog_input_password_1,
			R.id.dialog_input_password_2, R.id.dialog_input_password_3,
			R.id.dialog_input_password_4, R.id.dialog_input_password_5,
			R.id.dialog_input_password_6 };

	private String strPassword;// 第一次录入密码
	private String idNo;
	private String phone;
	private String code;
	private int type;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_setting_trade_pwd, this);
	}

	@Override
	public void initView() {

		setTitle("设置交易密码");

		tvTips = (TextView) findViewById(R.id.tv_setting_trade_tip);
		arrEdtPwd = new EditText[6];
		for (int i = 0; i < 6; i++) {
			arrEdtPwd[i] = (EditText) findViewById(mArrEdt[i]);
		}
		edt7 = (EditText) findViewById(R.id.dialog_input_password_7);

		edt7.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				String text = arg0.toString();

				if (text.length() >= 6) {

					if (TextUtils.isEmpty(strPassword)) {

						strPassword = text;
						edt7.setText("");
					} else if (strPassword.length() == 6) {

						if (strPassword.equals(text)) {
							activatereal(idNo, text);

						} else {
							CommandTools.showToast("两次密码不一致，请重新输入");
							strPassword = null;
							edt7.setText("");
						}
					}
				}
				setEditText();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

		edt7.setFocusable(true);
		edt7.setFocusableInTouchMode(true);
		edt7.requestFocus();

		Timer timer = new Timer();

		timer.schedule(new TimerTask()

		{

			public void run()

			{

				InputMethodManager inputManager =

				(InputMethodManager) edt7.getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);

				inputManager.showSoftInput(edt7, 0);

			}

		},

		998);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		idNo = intent.getStringExtra("idNo");
		phone = intent.getStringExtra("phone");
		code = intent.getStringExtra("code");
		type = intent.getIntExtra("type", 0);
	}

	/**
	 * 更新密码框
	 */
	private void setEditText() {

		String strMsg = edt7.getText().toString();

		if (TextUtils.isEmpty(strPassword)) {
			tvTips.setText("设置交易密码");
		} else {
			tvTips.setText("再输一次");
		}

		for (int i = 0; i < 6; i++) {
			arrEdtPwd[i].setText("");
		}

		int len = strMsg.length();
		if (len == 0) {
			return;
		}

		if (strMsg.length() >= 1) {
			arrEdtPwd[0].setText(strMsg.subSequence(0, 1));
		}

		if (strMsg.length() >= 2) {
			arrEdtPwd[1].setText(strMsg.subSequence(1, 2));
		}

		if (strMsg.length() >= 3) {
			arrEdtPwd[2].setText(strMsg.subSequence(2, 3));
		}

		if (strMsg.length() >= 4) {
			arrEdtPwd[3].setText(strMsg.subSequence(3, 4));
		}

		if (strMsg.length() >= 5) {
			arrEdtPwd[4].setText(strMsg.subSequence(4, 5));
		}

		if (strMsg.length() >= 6) {
			arrEdtPwd[5].setText(strMsg.subSequence(5, 6));
		}
	}

	private void activatereal(String idNo, String text) {

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
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);

							setResult(1);
							finish();
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
							setResult(0);
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
		CustomProgress.showDialog(mContext, "查询中", false, null);
		if(type==0){
			
			PayMentService.activatereal(idNo, text, phone, code, listener, null);
		}else{
			
			PayMentService.resetpwd(idNo, text,  code, listener, null);
		}
	}
}
