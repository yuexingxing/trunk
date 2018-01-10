package com.zhiduan.crowdclient.wallet;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.data.PayAccount;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.PayMentService;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.GeneralDialog.TwoButtonListener;
import com.zhiduan.crowdclient.view.ImportPassword;
import com.zhiduan.crowdclient.view.ImportPassword.PasswordListener;

/**
 * 提现的界面
 * 
 * @author wfq
 * 
 */
public class WithdrawDepositToActivity extends BaseActivity implements
		OnClickListener {

	private EditText mEtMoney;
	private TextView balance_money, mTvBalance;
	private Button withdaw_deposit_bt_ok;
	private LinearLayout ll_wallet_alipay, ll_wallet_wechat;
	private ImageView iv_switch_alipay, iv_switch_wechat;
	private boolean select_alipay;
	private int SDK_AUTH_FLAG = 10;
	private int isPay = 0;
	private boolean isBindWeiXin = false;
	private boolean isBindAlipay = false;
	private PayAccount alipay = null;
	private PayAccount weiXin = null;
	// 商户私钥，pkcs8格式
	// public static final String RSA_PRIVATE =
	// "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANAxsI8BReqwCRaouYHR4SaPPCC7XWiEkBko6KxzkRZ+b1U8GfzWvfyDbxcLh3a8SitV8qzu1LnaJn9+5WtZc1JUpjfm5xw14/xIocmgODHnPMqC9yrRPS0hAvehfeEfMiJazx+bjuBVbtjgVQdiQZtb5j3I5GlyTy7hSwwH2bbNAgMBAAECgYAyvfmfURseURtV93eHKYUrpbts6t2gTLbmOu9CoMQa7GPsLULUOe6CLfKUdk3k5Y1aNI96EJeuWHoLlv0JhfqyBKA2ZAgN3JpH1cuUSIYOp2eTotOTtre7KNR1nvwiPssZMG8mi4wL4aVYx16k1F5rJ8/EvQjirK2pSvXH7q8FAQJBAPIMgvgXElVQZL6vOPsZd59GFs32Yd0aMOyt0tjTA9/dETEhYKg5j1+UTWeGbHMUUwkm/XRLnZCK0b1EYvJuEsECQQDcMaSU1GZPi6bLnqVqFkM1cuD+NhD1XRXOgLri09d6lCpx6DqaoU9ENM1lS5zr4ypAt8YcghtVv9g8CImRKoMNAkEAjq4hZezzuaayPFU92YahpRnDx5U9CwCtZlbwuy9oUUYXJEbwmzmS8lyRZD0xIIUSjYqWTT9lmha6nD77tLx+AQJBAMG6e47mAM3jlHB5uBdKVv9jf0ip0OnPaCwzXjnLID4ctetZ4pXBbrgXrM5+ZCW1y6r9BTR31AzxqRHIgSBTQtECQDXyP3Pqq/O5Zp582VYugmrGByxC99yXMi7yj7niZ1CRaobWP3nagUDdIDTgaiG+sykbU9dR6xm6VFlUJh+ADg8=";

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (alipay != null) {
				ll_wallet_alipay_name.setText(alipay.getAccountName() + " "
						+ alipay.getAccountNumber());
			}
			if (weiXin != null) {

				ll_wallet_wechat_name.setText(weiXin.getAccountName() + " "
						+ weiXin.getAccountNumber());
			}
			if (msg.what == 3) {
				// 系统最高限额
				// ll_wallet_wechat_name.setText((String)msg.obj);

				double yue = balance / 100.0;
				DecimalFormat df = new DecimalFormat("######0.00");
				strNumber = df.format(yue);
				if (strNumber == null || strNumber.equals("null")) {
					strNumber = "0.00";
				}
				if (balance == 0) {
					strNumber = "0.00";
				}
				// CommandTools.longTOString(balance)
				balance_money.setText(String.format(
						getResources().getString(R.string.fee), strNumber));

				String isDefault = (String) SharedPreferencesUtils.getParam(
						"isDefault", "");

				if (TextUtils.isEmpty(isDefault)) {
					isPay = 0;
					iv_switch_wechat.setImageResource(R.drawable.msg_status);
					iv_switch_alipay.setImageResource(R.drawable.msg_status);
				} else if ("1".equals(isDefault)) {
					if (ll_wallet_alipay_name.getText().equals("暂未绑定")) {
						isPay = 0;
						iv_switch_wechat
								.setImageResource(R.drawable.msg_status);
						iv_switch_alipay
								.setImageResource(R.drawable.msg_status);
					} else {

						isPay = 1;
						iv_switch_wechat
								.setImageResource(R.drawable.msg_status);
						iv_switch_alipay
								.setImageResource(R.drawable.msg_status_select);
					}

				} else if ("2".equals(isDefault)) {
					if (ll_wallet_wechat_name.getText().equals("暂未绑定")) {
						isPay = 0;
						iv_switch_wechat
								.setImageResource(R.drawable.msg_status);
						iv_switch_alipay
								.setImageResource(R.drawable.msg_status);
					} else {
						isPay = 2;
						iv_switch_wechat
								.setImageResource(R.drawable.msg_status_select);
						iv_switch_alipay
								.setImageResource(R.drawable.msg_status);
					}
				}

			}

		}
	};
	private ProgressDialog dialog;
	private TextView ll_wallet_alipay_name;
	private TextView ll_wallet_wechat_name;
	private RelativeLayout withdaw_deposit_rl_add_weixin;
	private RelativeLayout withdaw_deposit_rl_add_zhifubao;
	private ImageView wallet_alipay_iv_tup;
	private View wallet_withdraw_xian;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_withdraw_deposit_to, this);
	}

	@Override
	public void initView() {

		// 提现金额
		mEtMoney = (EditText) findViewById(R.id.withdaw_deposit_et_money);
		balance_money = (TextView) findViewById(R.id.withdaw_deposit_balance_money);
		ll_wallet_alipay = (LinearLayout) findViewById(R.id.ll_wallet_alipay);
		ll_wallet_wechat = (LinearLayout) findViewById(R.id.ll_wallet_wechat);
		iv_switch_alipay = (ImageView) findViewById(R.id.iv_switch_alipay);
		iv_switch_wechat = (ImageView) findViewById(R.id.iv_switch_wechat);
		ll_wallet_alipay_name = (TextView) findViewById(R.id.ll_wallet_alipay_name);
		ll_wallet_wechat_name = (TextView) findViewById(R.id.ll_wallet_wechat_name);
		withdaw_deposit_rl_add_weixin = (RelativeLayout) findViewById(R.id.withdaw_deposit_rl_add_weixin);
		withdaw_deposit_rl_add_zhifubao = (RelativeLayout) findViewById(R.id.withdaw_deposit_rl_add_zhifubao);
		wallet_alipay_iv_tup = (ImageView) findViewById(R.id.wallet_alipay_iv_tup);
		wallet_withdraw_xian = findViewById(R.id.wallet_withdraw_xian);
		// 确认提现
		withdaw_deposit_bt_ok = (Button) findViewById(R.id.withdaw_deposit_bt_ok);

		initListener();

		iv_switch_alipay.setImageResource(R.drawable.msg_status);
		iv_switch_wechat.setImageResource(R.drawable.msg_status);
	}

	private void initListener() {
		withdaw_deposit_bt_ok.setOnClickListener(this);
		ll_wallet_alipay.setOnClickListener(this);
		ll_wallet_wechat.setOnClickListener(this);
		withdaw_deposit_rl_add_zhifubao.setOnClickListener(this);
		withdaw_deposit_rl_add_weixin.setOnClickListener(this);
		ll_wallet_alipay.setOnClickListener(this);

		mEtMoney.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				String text = arg0.toString();
				if (text.length() > 2) {
					String text2 = text.substring(0, text.length() - 1);
					String changed = text.substring(text.length() - 1);
					if (".".equals(changed)) {
						if (text2.contains(".")) {
							if (text.length() > 2) {
								mEtMoney.setText(text2);
								mEtMoney.setSelection(mEtMoney.getText()
										.toString().length());
							} else {
								mEtMoney.setText("");
							}
						}
					}

					String[] split = text.split("\\.");
					if (split != null && split.length > 1) {
						String dian = split[1];
						if (dian.length() > 2) {
							if (text.length() > 2) {

								mEtMoney.setText(text2);
								mEtMoney.setSelection(mEtMoney.getText()
										.toString().length());
							} else {
								mEtMoney.setText("");
							}
						}
					}
				}
				if (".".equals(text)) {
					mEtMoney.setText("0.");
					mEtMoney.setSelection(mEtMoney.getText().toString()
							.length());
				}
				if (!TextUtils.isEmpty(text)) {
					String substring = text.substring(0, 1);
					if ("0".equals(substring) && text.length() == 2) {
						String substring2 = text.substring(1, 2);
						if (!".".equals(substring2)) {

							mEtMoney.setText(substring2);
							mEtMoney.setSelection(mEtMoney.getText().toString()
									.length());
						}
					}
				}
				if (text.length() == 7
						&& ".".equals(text.substring(text.length() - 1))) {
					mEtMoney.setText(text.substring(0, text.length() - 1));
				}
				if (text.length() > 7) {
					mEtMoney.setText(text.substring(0, text.length() - 1));
				}
				mEtMoney.setSelection(mEtMoney.getText().toString().length());
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		mEtMoney.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {

				String text = mEtMoney.getText().toString();
				if (!TextUtils.isEmpty(text)) {
					String[] split = text.split("\\.");
					if (split != null) {
						if (split.length > 1) {
							if (TextUtils.isEmpty(split[1])) {
								text += "00";
							} else {
								if (split[1].length() == 1) {
									text += "0";
								}
							}
						} else {
							if (text.contains(".")) {
								text += "00";
							} else {

								text += ".00";
							}
						}
					}
					mEtMoney.setText(text);
				}

			}
		});

		setTitle("提现");

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		strNumber = intent.getStringExtra("strNumber");
		long number = intent.getLongExtra("number", 0);
		balance_money.setText(String.format(
				getResources().getString(R.string.fee), "0.00"));
//		getPayNumber();

	}

	@Override
	protected void onResume() {
		super.onResume();

		ViewTreeObserver vto = wallet_alipay_iv_tup.getViewTreeObserver();

		vto.addOnPreDrawListener(new

		ViewTreeObserver.OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {

				int measuredWidth = wallet_alipay_iv_tup.getMeasuredWidth();
				LinearLayout.LayoutParams layoutParams = (android.widget.LinearLayout.LayoutParams) wallet_withdraw_xian
						.getLayoutParams();
				layoutParams.leftMargin = CommandTools.dip2px(mContext, 40)
						+ measuredWidth;
				wallet_withdraw_xian.setLayoutParams(layoutParams);
				return true;
			}

		});
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.withdaw_deposit_rl_add_zhifubao:
			// 添加支付宝
			// Intent intentAddAlipay = new
			// Intent(this,ChangeAccountActivity.class);
			// startActivity(intentAddAlipay);
			// 支付宝授权
			// alipayAuth();

			Intent intentChangeAlipay = new Intent(this,
					ChangeAccountActivity.class);
			intentChangeAlipay.putExtra("type", 1);
			startActivityForResult(intentChangeAlipay, 119);
			break;
		case R.id.withdaw_deposit_rl_add_weixin:
			// 添加微信

			// Intent intentAddWeChat = new
			// Intent(this,AddWeChatActivity.class);
			// startActivity(intentAddWeChat);
			// 微信授权
			// dialog = ProgressDialog.show(mContext, "提示", "正在调起微信中。。");
			// weChatAuth();
			Intent intentWechat = new Intent(this, ChangeAccountActivity.class);
			intentWechat.putExtra("type", 2);
			startActivityForResult(intentWechat, 120);

			break;
		case R.id.withdaw_deposit_bt_ok:
			// 确认提现

			withdrawDepositDialog();
			// CommandTools.showDialog(this, "请添加支付方式");

			break;
		case R.id.ll_wallet_alipay:
			isPay = 1;
			iv_switch_alipay.setImageResource(R.drawable.msg_status_select);

			iv_switch_wechat.setImageResource(R.drawable.msg_status);
			if (isHaveAlipay) {
				// Intent intent = new Intent(this, AddAlipayActivity.class);
				// intent.putExtra("isDiyi", 1);
				// startActivityForResult(intent, 177);

				// Intent intent = new Intent(this,
				// ChangeAccountActivity.class);
				// intent.putExtra("type", 1);
				// startActivityForResult(intent, 119);
			} else {
				Intent intent = new Intent(this, AddAlipayActivity.class);
				intent.putExtra("type", 1);
				startActivityForResult(intent, 119);

			}
			break;
		case R.id.ll_wallet_wechat:
			isPay = 2;
			iv_switch_wechat.setImageResource(R.drawable.msg_status_select);
			iv_switch_alipay.setImageResource(R.drawable.msg_status);
			if (isHaveWeiXin) {

				// Intent intentAddWeChat = new Intent(this,
				// AddWeChatActivity.class);
				// startActivityForResult(intentAddWeChat, 166);
				// Intent intent = new Intent(this,
				// ChangeAccountActivity.class);
				// intent.putExtra("type", 2);
				// startActivityForResult(intent, 120);

			} else {
				Intent intent = new Intent(this, AddWeChatActivity.class);
				intent.putExtra("type", 2);
				startActivityForResult(intent, 120);
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getPayNumber();
	}

	private long acctLimit = 0;
	private long sysLimit = 0;
	private long balance = 0;
	private String strNumber = "0.00";
	private boolean isHaveAlipay = false;
	private boolean isHaveWeiXin = false;

	/**
	 * 获取用户最多可提现金额
	 */
	private void getPayNumber() {

		OnPostExecuteListener listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {

				if (result.m_nResultCode == 0) {
					alipay = null;
					weiXin = null;
					LoadTextResult mresult = (LoadTextResult) result;
					JSONObject jsonObj;
					try {
						jsonObj = new JSONObject(mresult.m_strContent);
						Logs.i("zd", jsonObj.toString() + "---");
						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONObject data = jsonObj.getJSONObject("data");
							acctLimit = data.getLong("acctLimit");
							sysLimit = data.getLong("sysLimit");
							balance = data.getLong("balance");
							Message msg = new Message();
							JSONArray payTypeList = data
									.getJSONArray("payTypeList");
							for (int i = 0; i < payTypeList.length(); i++) {
								JSONObject payType = payTypeList
										.getJSONObject(i);
								int typeCode = payType.getInt("typeCode");

								JSONArray thirdList = payType
										.getJSONArray("thirdList");
								for (int j = 0; j < thirdList.length(); j++) {
									JSONObject third = thirdList
											.getJSONObject(j);
									String acctNo = third.getString("acctNo");
									String acctName = third
											.getString("acctName");
									int isDefault = third.getInt("isDefault");
									int thdId = third.getInt("thdId");
									if (TextUtils.isEmpty(acctName)
											|| TextUtils.isEmpty(acctNo)) {
										continue;
									}
									if (typeCode == 2) {
										if (isDefault == 1) {
											isBindAlipay = true;
											alipay = new PayAccount(acctName,
													acctNo, true, thdId);
										}
										isHaveAlipay = true;
									} else {
										if (isDefault == 1) {
											isBindWeiXin = true;
											weiXin = new PayAccount(acctName,
													acctNo, true, thdId);
										}
										isHaveWeiXin = true;
									}
								}

							}
							msg.what = 3;
							msg.obj = sysLimit;
							mHandler.sendMessage(msg);
							if (weiXin == null) {
								getWeiXin();
							}

						} else {
							String message = jsonObj.getString("message");
							// CommandTools.showToast(message);
							GeneralDialog.showOneButtonDialog(mContext,
									GeneralDialog.DIALOG_ICON_TYPE_1, "",
									message, "确定",
									new GeneralDialog.OneButtonListener() {

										@Override
										public void onExitClick(Dialog dialog) {
											// TODO Auto-generated method stub

										}

										@Override
										public void onButtonClick(Dialog dialog) {
											GeneralDialog.dismiss();
											finish();
										}
									});
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
		PayMentService.getPayNumber(listener, null);
	}

	/**
	 * 更新微信支付信息
	 */
	private void getWeiXin() {
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
							JSONObject data = jsonObj.getJSONObject("data");
							Message msg = new Message();

							String acctNo = data.getString("acctNo");
							String acctName = data.getString("acctName");
							int isDefault = data.getInt("isDefault");
							int thdId = data.getInt("thdId");

							isBindWeiXin = true;
							weiXin = new PayAccount(acctName, acctNo, true,
									thdId);
							msg.what = 3;
							msg.obj = sysLimit;
							mHandler.sendMessage(msg);
						} else {
							String message = jsonObj.getString("message");
							// CommandTools.showToast(message);
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
		PayMentService.getWeiXin(listener, null);
	}

	long money;

	/**
	 * 提现对话框
	 */
	private void withdrawDepositDialog() {
		if (TextUtils.isEmpty(mEtMoney.getText())) {
			CommandTools.showToast("金额不能为空哦");
			return;
		}
		//
		float parseFloat = Float.parseFloat(mEtMoney.getText().toString());
		money = 0;
		String str = mEtMoney.getText().toString();
		String substring = str.substring(str.indexOf(".") + 1);
		if (!str.contains(".")) {
			money = Integer.parseInt(str) * 100;
		} else if (substring.length() == 1) {
			str = str.replace(".", "") + "0";
			money = Integer.parseInt(str);

		} else {
			money = Integer.parseInt(str.replace(".", ""));

		}

		// TODO Auto-generated method stub
		double yue = money / 100.0;
		DecimalFormat df = new DecimalFormat("######0.00");
		if (TextUtils.isEmpty(mEtMoney.getText())
				|| Float.parseFloat(mEtMoney.getText().toString()) == 0) {
			CommandTools.showToast("请输入正确的金额哦");
			return;
		}

		if (sysLimit < money) {
			chaoE("当天提现以超额");
			return;

		}
		if (balance < money) {
			chaoE("提现金额超过账户余额");
			return;

		}
		if (isPay == 0) {
			CommandTools.showToast("请选择支付方式");
			return;
		}
		if (isPay == 1 && alipay == null) {

			CommandTools.showToast("请选择支付方式");
			return;
		}
		if (isPay == 2 && weiXin == null) {

			CommandTools.showToast("请选择支付方式");
			return;
		}
		// TODO
		ImportPassword.showImportPassWord(mContext, mEtMoney.getText()
				.toString(), new PasswordListener() {

			@Override
			public void passwordSucceed(final String passWord) {
				if (acctLimit <= money) {
					ImportPassword.showAuthCode(mContext,
							new PasswordListener() {

								@Override
								public void passwordSucceed(String authCode) {
									if (isPay == 1) {

										withdrawDeposit(authCode, money,
												passWord, alipay.getThdId());
									} else {
										withdrawDeposit(authCode, money,
												passWord, weiXin.getThdId());
									}

								}

							});
				} else {
					if (isPay == 1) {

						withdrawDeposit("", money, passWord, alipay.getThdId());
					} else {
						withdrawDeposit("", money, passWord, weiXin.getThdId());
					}
				}
			}
		});

	}

	/**
	 * 钱包提现
	 * 
	 * @param authCode
	 * @param money
	 * @param passWord
	 */
	private void withdrawDeposit(String authCode, long money, String passWord,
			final long thdId) {
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
							MyApplication.withdrawDepositTime = 0;
							JSONObject data = jsonObj.getJSONObject("data");
							long detailId = data.getLong("detailId");
							// 保存默认的支付方式
							SharedPreferencesUtils.setParam("isDefault", ""
									+ isPay);
							Intent intent = new Intent(mContext,
									WithdrawDepositScheduleActivity.class);
							intent.putExtra("detailId", detailId);
							intent.putExtra("payType", 2);
							startActivity(intent);
							finish();
						} else {

							String message = jsonObj.getString("message");
							final String code = jsonObj.getString("code");

							if ("09081".equals(code) || "09085".equals(code)
									|| "09086".equals(code)
									|| "09089".equals(code)
									|| "09090".equals(code)
									|| "09094".equals(code)
									|| "09088".equals(code)) {
								// 取消
								// 09081=提现帐号非法
								// 09085=无此账户
								// 09086=钱包账户未激活
								// 09089=当天提现以超额
								// 09090=当天提现次数以超出
								// 09094=少于最小提现金额
								// 09088=提现金额超过账户余额

								ImportPassword.showErrorOneDialog(mContext,
										message, null);

							} else {
								ImportPassword.showErrorTwoDialog(mContext,
										message, code, new TwoButtonListener() {

											@Override
											public void onRightClick(
													Dialog dialog) {
												if (dialog != null) {

													dialog.dismiss();
												}
												if ("09087".equals(code)
														|| "09093".equals(code)) {
													// 取消 忘记密码
													Intent intentForget = new Intent(
															mContext,
															ActivateWalletActivity.class);
													intentForget.putExtra(
															"title", "忘记交易密码");
													startActivity(intentForget);
												} else {
													withdrawDepositDialog();
												}

											}

											@Override
											public void onLeftClick(
													Dialog dialog) {
												if (dialog != null) {

													dialog.dismiss();
												}
												if ("09091".equals(code)) {
													// 忘记密码 重新输入
													Intent intentForget = new Intent(
															mContext,
															ActivateWalletActivity.class);
													intentForget.putExtra(
															"title", "忘记交易密码");
													startActivity(intentForget);
												}

											}
										});

							}

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
		CustomProgress.showDialog(mContext, "正在提现", false, null);
		String phone = (String) SharedPreferencesUtils.getParam(
				Constant.SP_LOGIN_NAME, "");

		PayMentService.withdrawDeposit(authCode, money, passWord, phone, thdId,
				listener, null);
	}

	private void chaoE(String content) {

		ImportPassword.showErrorOneDialog(mContext, content, null);
	}

}
