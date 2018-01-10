package com.zhiduan.crowdclient.wallet;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.DrawerUpView;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;
import com.zhiduan.crowdclient.view.dialog.DialogUtils.DialogCallback;

/**
 * 我的钱包界面
 * 
 * @author wfq
 * 
 */
public class MyWalletActivity extends BaseActivity implements OnClickListener {

	private TextView mTvMoney;
	private Button mBtWithdrawDeposit;
	// public static Handler mHander = new Handler() {
	// public void handleMessage(Message msg) {
	// long b = (Long) msg.obj;
	// if (mTvMoney != null) {
	// mTvMoney.setText("￥" + CommandTools.longTOString(b));
	// }
	//
	// };
	// };

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mTvMoney != null) {
				mTvMoney.setText(String.format(getResources().getString(R.string.fee),CommandTools.longTOString(balance) ));
			}
			mTvTiXianCiShu.setText("免手续费还可提现" + times + "次");
		}

	};
	private TextView mTvTiXianCiShu;
	private DrawerUpView f;
	private TextView mTvExplain;
	private TextView my_wallet_shangxian;
	
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_my_wallet, this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTvMoney = null;
	}

	@Override
	public void initView() {
		mTvMoney = (TextView) findViewById(R.id.my_wallet_bt_money);
		mBtWithdrawDeposit = (Button) findViewById(R.id.my_wallet_bt_withdraw_deposit);
		mTvTiXianCiShu = (TextView) findViewById(R.id.my_wallet_tv_tixianshouxufei);
		mTvExplain = (TextView) findViewById(R.id.my_wallet_tv_explain);
		my_wallet_shangxian = (TextView) findViewById(R.id.my_wallet_shangxian);
		f = (DrawerUpView) findViewById(R.id.my_wallet_bt_withdraw_f);
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("交易记录");
		arrayList.add("修改交易密码");
		arrayList.add("忘记交易密码");
		arrayList.add("取消");
		f.setData(arrayList);

		initListener();

		hidenRightGone();
		ShowRightPic();
		setRightPic(R.drawable.wallet_more);
		setTitle("钱包");
		
		Intent intent = getIntent();
		officeRoleType = intent.getIntExtra("officeRoleType", 1);
		
		if(officeRoleType==1){
			mTvExplain.setVisibility(View.GONE);
		}
		
	}

	private void initListener() {
		mBtWithdrawDeposit.setOnClickListener(this);
		mTvExplain.setOnClickListener(this);

		f.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					f.hideDrawerTime(0, 50);
					// 交易记录
					Intent intentWalletRecord = new Intent(mContext, WalletRecordActivity.class);
					intentWalletRecord.putExtra("payType", 0);
					intentWalletRecord.putExtra("officeRoleType", officeRoleType);
					startActivity(intentWalletRecord);
					break;
				case 1:
					f.hideDrawerTime(0, 50);
					// 修改交易密码
					Intent intentAlter = new Intent(mContext,
							AlterPassWord1Activity.class);
					startActivity(intentAlter);
					break;
				case 2:
					f.hideDrawerTime(0, 50);
					// 忘记交易密码
					Intent intentForget = new Intent(mContext,
							ActivateWalletActivity.class);
					intentForget.putExtra("title", "忘记交易密码");
					startActivity(intentForget);
					break;
				case 3:
					// 取消
					f.hideDrawer();
					break;

				default:
					break;
				}
			}
		});
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_wallet_bt_withdraw_deposit:
			// 提现
			// 提现
			if (!Utils.getNetWorkState()) {

				Util.showNetErrorMessage(mContext, -1);
				return;
			}
			
			if (MyApplication.Wallet_Activate == 0) {
				// 未激活钱包
				isActivate(false);
			} else {

				if (times == 0) {
					CommandTools.showToast("当前已超过每日提现最大次数");
					return;
				}

				Intent intentWithdraw = new Intent(this,
						WithdrawDepositToActivity.class);
				intentWithdraw.putExtra("times", times);
				startActivity(intentWithdraw);
			}
			break;
		case R.id.my_wallet_tv_explain:
			// TODO 提成收入说明
			Intent intentIncomeExplain = new Intent(this,
					IncomeExplainActivity.class);
			startActivity(intentIncomeExplain);
			break;
		default:
			break;
		}
	}

	@Override
	public void rightPicClick() {
		if (MyApplication.Wallet_Activate == 0) {
			// 未激活钱包
			isActivate(false);
		} else {
			// mLlChouTi.setVisibility(isChouTi?View.GONE:View.VISIBLE);
			if (f.isShowDrawer()) {
				f.hideDrawer();
			} else {
				f.showDrawer();
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// do something...
			if (f.isShowDrawer()) {
				f.hideDrawer();
				return true;
			}

			return super.onKeyDown(keyCode, event);
		} else {

			return super.onKeyDown(keyCode, event);
		}
	}

	private long balance;
	private int times;// 当前剩余提现次数
	private String userName;
	private int officeRoleType;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mTvMoney = (TextView) findViewById(R.id.my_wallet_bt_money);
//		getUserInfo();
	}

	/**
	 * 获取用户信息
	 */
	private void getUserInfo() {
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
							// 账户激活状态，0未激活，1激活
							int activeState = data.getInt("activeState");
							MyApplication.Wallet_Activate = activeState;
							balance = data.getLong("balance");
							MyApplication.getInstance().m_userInfo.m_user_income = balance;
							// 账户状态，0不可用，1可用
							int state = data.getInt("state");
							times = data.getInt("times");
							userName = data.getString("userName");
							// 用户认证状态 0：注册未完善信息 1：提交完善提交 2：提交的信息审核通过 3：提交的信息审核失败
							int verifyStatus = data.getInt("verifyStatus");
							mTvMoney.setText(String.format(getResources().getString(R.string.fee),CommandTools.longTOString(balance) ));
							MyApplication.getInstance().m_userInfo.verifyStatus = verifyStatus;
							handler.sendEmptyMessage(0);
							getPayNumber();
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
				CustomProgress.dissDialog();
			}
		};
		CustomProgress.showDialog(MyWalletActivity.this, "查询中", false, null);
		PayMentService.getUserInfo(listener, null);
	}

	/**
	 * 判断实名认证
	 */
	private void isActivate(boolean is) {
		int verifyStatus = MyApplication.getInstance().m_userInfo.verifyStatus;

		// 认证状态码0 未提交信息 1 提交信息未审核 2 审核通过 3审核失败
		Intent intent ;
		switch (verifyStatus) {
		case 0:
//			DialogUtils.showAuthDialog(MyWalletActivity.this);
			intent= new Intent(this, ActivateWalletActivity.class);
			intent.putExtra("title", "激活钱包");
			startActivity(intent);
			break;
		case 1:
			DialogUtils.showReviewingDialog(MyWalletActivity.this);
			break;
		case 2:
			if (is) {
				 intent = new Intent(this, ActivateWalletActivity.class);
				intent.putExtra("title", "激活钱包");
				startActivity(intent);
			} else {

				String title = "激活钱包";
				String content = "您未激活钱包，请先激活钱包！";
				DialogUtils.showTwoButtonDialog(MyWalletActivity.this,
						GeneralDialog.DIALOG_ICON_TYPE_8, title, content, "取消",
						"马上激活", new

						DialogCallback() {

							@Override
							public void callback(int position) {
								// TODO Auto-generated method stub
								if (position == 0) {

									Intent intent = new Intent(mContext,
											ActivateWalletActivity.class);
									intent.putExtra("title", "激活钱包");
									startActivity(intent);
								}
							}
						});

			}
			break;
		case 3:
			String content = "您提供的资料审核未通过, 请继续完善，谢谢！";
			DialogUtils.showReviewingFailed(mContext, content);
			break;
		case 4:
			if (is) {
				 intent = new Intent(this, ActivateWalletActivity.class);
				intent.putExtra("title", "激活钱包");
				startActivity(intent);
			} else {

				String title = "激活钱包";
				String content_str = "您未激活钱包，请先激活钱包！";
				DialogUtils.showTwoButtonDialog(MyWalletActivity.this,
						GeneralDialog.DIALOG_ICON_TYPE_8, title, content_str, "取消",
						"马上激活", new

						DialogCallback() {

							@Override
							public void callback(int position) {
								// TODO Auto-generated method stub
								if (position == 0) {

									Intent intent = new Intent(mContext,
											ActivateWalletActivity.class);
									intent.putExtra("title", "激活钱包");
									startActivity(intent);
								}
							}
						});

			}
			break;
		default:
			break;
		}
	}
	/**
	 * 获取用户最多可提现金额
	 */
	private void getPayNumber() {

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
							long sysLimit = data.getLong("sysLimit");
							
							my_wallet_shangxian.setText("每日提现上限"+CommandTools.longTOString(sysLimit)+"元");
							
						} else {
							
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

}
