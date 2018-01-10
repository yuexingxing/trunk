package com.zhiduan.crowdclient.wallet;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.R.color;
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
import com.zhiduan.crowdclient.view.GeneralDialog.TwoButtonListener;
import com.zhiduan.crowdclient.view.ImportPassword;
import com.zhiduan.crowdclient.view.ImportPassword.PasswordListener;

/**
 * 提现
 * 
 * @author wfq
 * 
 */
public class WithdrawDepositActivity extends BaseActivity implements
		OnClickListener {

	private RelativeLayout mRlWay;
	private EditText mEtMoney;
	private TextView mTvDepositMoney;
	private Button mBtDeposit;
	private String name;
	private String number;
	private boolean isBind = false;
	private TextView mTvName;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 3) {
				// 系统最高限额
				// ll_wallet_wechat_name.setText((String)msg.obj);

				mTvDepositMoney.setText("每日最多可提现 ￥"
						+ CommandTools.longTOString(finalLimit));

			}
			mTvCiShu.setText("" + times);
		}

	};
	private TextView mTvCiShu;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_withdraw_deposit, this);
	}

	@Override
	public void initView() {
		mRlWay = (RelativeLayout) findViewById(R.id.withdraw_deposit_rl_way);
		mEtMoney = (EditText) findViewById(R.id.withdraw_deposit_et_money);
		mTvName = (TextView) findViewById(R.id.withdraw_deposit_tv_name);
		mTvDepositMoney = (TextView) findViewById(R.id.withdraw_deposit_tv_depositmoney);
		mTvCiShu = (TextView) findViewById(R.id.withdraw_deposit_tv_cishu);
		mBtDeposit = (Button) findViewById(R.id.withdraw_deposit_bt_deposit);
		initListener();

		setTitle("提现");

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		times = intent.getIntExtra("intent", 0);
		getPayNumber();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.withdraw_deposit_rl_way:
			// 选择支付方式
			Intent way = new Intent(this, WithdrawDepositWayActivity.class);
			if (alipayList != null && alipayList.size() != 0) {
				alipay = alipayList.get(0);
			}
			if (weiXinList != null && weiXinList.size() != 0) {
				weiXin = weiXinList.get(0);
			}
			way.putExtra("alipay", alipay);
			way.putExtra("weiXin", weiXin);
			startActivityForResult(way, 0);
			break;
		case R.id.withdraw_deposit_bt_deposit:
			// 提现
			if (pay == null) {
				CommandTools.showToast("请选择提现账户");
			} else {
				if (TextUtils.isEmpty(mEtMoney.getText())) {
					CommandTools.showToast("金额不能为空哦");
					return;
				}
				//

				withdrawDepositDialog();
			}

			break;

		default:
			break;
		}

	}

	/**
	 * 提现对话框
	 */
	private void withdrawDepositDialog() {
		float parseFloat = Float.parseFloat(mEtMoney.getText().toString());
		final long money = (long) (parseFloat * 100);
		if (money == 0) {
			CommandTools.showToast("金额不能为零哦");
			return;
		}

		if (money < 100) {
			CommandTools.showToast("提现金额不能小于1元");
			return;
		}

		ImportPassword.showImportPassWord(mContext,
				CommandTools.longTOString(money), new PasswordListener() {

					@Override
					public void passwordSucceed(final String passWord) {
						if (acctLimit <= money) {
							ImportPassword.showAuthCode(mContext,
									new PasswordListener() {

										@Override
										public void passwordSucceed(
												String authCode) {

											withdrawDeposit(authCode, money,
													passWord, pay.getThdId());

										}

									});
						} else {
							withdrawDeposit("", money, passWord, pay.getThdId());

						}
					}
				});

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

				(InputMethodManager) mEtMoney.getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);

				inputManager.showSoftInput(mEtMoney, 0);

			}

		},

		998);
	}

	private void initListener() {
		mRlWay.setOnClickListener(this);
		mBtDeposit.setOnClickListener(this);

		mEtMoney.setFocusable(true);
		mEtMoney.setFocusableInTouchMode(true);
		mEtMoney.requestFocus();

		mEtMoney.addTextChangedListener(new TextWatcher() {
			@SuppressLint("ResourceAsColor")
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
				if (TextUtils.isEmpty(mEtMoney.getText())) {
					mTvDepositMoney.setText("每日最多可提现 ￥"
							+ CommandTools.longTOString(finalLimit));
					mTvDepositMoney.setTextColor(color.gray_1);
					return;
				}
				long l = (long) (Double.valueOf(mEtMoney.getText().toString()) * 100);
				if (l > finalLimit) {
					mTvDepositMoney.setText("您输入的金额大于每日最多可提现金额 ");
					mTvDepositMoney.setTextColor(color.red);
				} else {
					mTvDepositMoney.setText("每日最多可提现 ￥"
							+ CommandTools.longTOString(finalLimit));
					mTvDepositMoney.setTextColor(color.gray_1);
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

	}

	private PayAccount alipay;
	private PayAccount weiXin;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (requestCode == 0) {
		// if (data != null) {
		// pay = (PayAccount) data.getSerializableExtra("pay");
		//
		// if (pay != null) {
		//
		// mTvName.setText(pay.getAccountName() + " "
		// + pay.getAccountNumber());
		// }
		//
		// }
		//
		// if(resultCode==1){
		getPayNumber();
		// }
		//
		// }
	}

	private long finalLimit;
	private long acctLimit;
	private long sysLimit;
	private long balance;
	private boolean isBindAlipay;
	private boolean isBindWeiXin;
	private ArrayList<PayAccount> alipayList = new ArrayList<PayAccount>();
	private ArrayList<PayAccount> weiXinList = new ArrayList<PayAccount>();
	private PayAccount pay;
	private int times;

	/**
	 * 获取用户最多可提现金额
	 */
	private void getPayNumber() {

		OnPostExecuteListener listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					alipay = null;
					weiXin = null;
					LoadTextResult mresult = (LoadTextResult) result;
					weiXinList.clear();
					alipayList.clear();
					JSONObject jsonObj;
					try {
						jsonObj = new JSONObject(mresult.m_strContent);
						Logs.i("zd", jsonObj.toString() + "---");
						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONObject data = jsonObj.getJSONObject("data");
							acctLimit = data.getLong("acctLimit");
							sysLimit = data.getLong("sysLimit");
							finalLimit = data.getLong("finalLimit");
							balance = data.getLong("balance");
							times = data.getInt("times");
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
									if (isDefault == 1) {
										if (typeCode == 2) {

											isBindAlipay = true;

											alipay = new PayAccount(acctName,
													acctNo, true, thdId);

										} else {
											isBindWeiXin = true;
											weiXin = new PayAccount(acctName,
													acctNo, true, thdId);
										}
									}
									if (typeCode == 2) {
										PayAccount payAccount = new PayAccount();
										payAccount.setAccountName(acctName);
										payAccount.setAccountNumber(acctNo);
										payAccount
												.setPitchOn(isDefault == 1 ? true
														: false);
										payAccount.setThdId(thdId);
										alipayList.add(payAccount);

									} else {
										PayAccount payAccount = new PayAccount();
										payAccount.setAccountName(acctName);
										payAccount.setAccountNumber(acctNo);
										payAccount
												.setPitchOn(isDefault == 1 ? true
														: false);
										payAccount.setThdId(thdId);
										weiXinList.add(payAccount);

									}
								}

							}
							msg.what = 3;
							msg.obj = sysLimit;
							mHandler.sendMessage(msg);

						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
						}

					} catch (JSONException e) {
						e.printStackTrace();
						Util.showJsonParseErrorMessage(mContext);
					}
				} else {
					Util.showNetErrorMessage(mContext, result.m_nResultCode);
				}

			}
		};
		CustomProgress.showDialog(mContext, "查询中", false, null);
		PayMentService.getPayNumber(listener, null);
	}

	/**
	 * 钱包提现
	 * 
	 * @param authCode
	 * @param money
	 * @param passWord
	 */
	private void withdrawDeposit(String authCode, long money, String passWord,
			long thdId) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {
				CustomProgress.dissDialog();
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
				// CustomProgress.dissDialog();

			}
		};
		CustomProgress.showDialog(mContext, "正在提现", false, null);
		String phone = (String) SharedPreferencesUtils.getParam(
				Constant.SP_LOGIN_NAME, "");

		PayMentService.withdrawDeposit(authCode, money, passWord, phone, thdId,
				listener, null);
	}
}
