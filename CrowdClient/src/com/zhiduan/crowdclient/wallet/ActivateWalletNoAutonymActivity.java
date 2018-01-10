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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.util.CommandTools;

/**
 * 激活钱包
 * 
 * @author wfq
 * 
 * @date 2016-5-20 下午2:30:26
 * 
 */
public class ActivateWalletNoAutonymActivity extends BaseActivity {

	private Context mContext;

	private EditText edtPhone;
	private EditText edtCode;

	private Button btnCode;
	private Button btnNext;

	private String strPhone;

	private SmsObserver smsObserver;
	private Uri SMS_INBOX = Uri.parse("content://sms/");

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_activate_wallet_no_autonym, this);

	}

	@Override
	public void initView() {

		mContext = ActivateWalletNoAutonymActivity.this;

		edtPhone = (EditText) findViewById(R.id.activate_wallet_no_autonym_et_number);
		edtCode = (EditText) findViewById(R.id.activate_wallet_no_autonym_et_code);

		btnCode = (Button) findViewById(R.id.activate_wallet_no_autonym_bt_code);
		btnNext = (Button) findViewById(R.id.activate_wallet_no_autonym_et_next);
		btnNext.setClickable(true);

		setEditableListener(edtPhone);
		setEditableListener(edtCode);
	}

	@Override
	public void initData() {

		setTitle("钱包激活");
		// hideUploadBtn();
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

		if (strPhone.length() != 11 || strCode.length() != 6) {
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

		strPhone = edtPhone.getText().toString();
		if (TextUtils.isEmpty(strPhone)) {
			// CommandTools.showToast(mContext, "请输入手机号");
			return;
		}

		if (!CommandTools.isMobileNO(strPhone)) {
			// CommandTools.showToast(mContext, "手机号不合法");
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("phone", strPhone);
			jsonObject.put("codeType", "register");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "user/code/send.htm";

	}

	/**
	 * 下一步
	 * 
	 * @param v
	 */
	public void nextStep(View v) {

		// Intent intent = new Intent(this, WelletAutonymActivity.class);
		// startActivityForResult(intent, 10);
		// finish();
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
		if (requestCode == 10 && resultCode == 10) {
			setResult(10);
			finish();
		}
	}
}
