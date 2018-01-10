package com.zhiduan.crowdclient.wallet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
 * 提现进度
 * 
 * @author wfq
 * 
 */
public class WithdrawDepositScheduleActivity extends BaseActivity {

	private TextView mTvNumber;
	private TextView mTvName;
	private ImageView mIvTypePic;
	private TextView mTvDepositType;
	private TextView mTvCheckType;
	private TextView mTvTime;
	private TextView mTvCheckTime;
	private ImageView mIvXian;
	private TextView mTvDepositTime;
	private TextView mTvBalance;
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			mTvDepositTime.setText(createTime);
			mTvBalance.setText(String.format(
					getResources().getString(R.string.fee),
					CommandTools.longTOString(optAmount)));
			// 1:已提交 2处理中 3:已到账
			String text = "已提交";
			switch (state) {
			case 1:
				text = "已提交";
				break;
			case 2:
				text = "处理中";
				break;
			case 3:
				text = "已到账";
				break;

			default:
				text = "已提交";
				break;
			}
			mTvType.setText(text);
			if (typeCode == 2) {
				String s = "";
				if (acctNo.contains("@")) {
					String[] split = acctNo.split("@");
					if (split[0].length() < 4) {
						s = split[0] + "@***";
					} else {
						s = split[0].substring(split[0].length() - 4,
								split[0].length())
								+ "@***";
					}
				} else {
					s = acctNo.substring(acctNo.length() - 4, acctNo.length());
				}
				mTvNumber.setText("支付宝（****" + s + "）");
				mTvCheckType.setText("支付宝处理成功");

			} else {

				mTvNumber.setText("微信（" + acctNo + "）");
				mTvCheckType.setText("微信处理成功");
			}
			mTvName.setText("**"
					+ acctName.substring(acctName.length() - 1,
							acctName.length()));
			mTvTime.setText(createTime);
			if (state != 3) {
				time = CommandTools.timeAddAday(time);
			}
			mTvCheckTime.setText("预计到账时间：" + time);
		};
	};
	private TextView mTvType;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_withdraw_deposit_schedule, this);
	}

	@Override
	public void initView() {

		mTvType = (TextView) findViewById(R.id.withdraw_deposit_schedule_tv_type);
		mTvDepositTime = (TextView) findViewById(R.id.withdraw_deposit_schedule_tv_deposittime);
		mTvBalance = (TextView) findViewById(R.id.withdraw_deposit_schedule_tv_balance);
		mTvNumber = (TextView) findViewById(R.id.withdraw_deposit_schedule_tv_number);
		mTvName = (TextView) findViewById(R.id.withdraw_deposit_schedule_tv_name);
		mIvTypePic = (ImageView) findViewById(R.id.withdraw_deposit_schedule_iv_type_pic);
		mTvDepositType = (TextView) findViewById(R.id.withdraw_deposit_schedule_tv_deposit_type);
		mTvCheckType = (TextView) findViewById(R.id.withdraw_deposit_schedule_tv_check_type);
		mTvTime = (TextView) findViewById(R.id.withdraw_deposit_schedule_tv_time);
		mTvCheckTime = (TextView) findViewById(R.id.withdraw_deposit_schedule_tv_check_time);

		mIvXian = (ImageView) findViewById(R.id.withdraw_deposit_schedule_iv_xian);

		setTitle("提现进度");
		WindowManager wm = this.getWindowManager();

		int width = wm.getDefaultDisplay().getWidth();
		int margin = width / 2 / 2;
		RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) mIvXian
				.getLayoutParams();
		layoutParams.leftMargin = margin;
		layoutParams.rightMargin = margin;
		mIvXian.setLayoutParams(layoutParams);

	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		long detailId = intent.getLongExtra("detailId", 0);
		int payType = intent.getIntExtra("payType", 0);

		withdrawDepositSchedule(detailId, payType);
	}

	private String createTime;
	private String acctName;
	private String acctNo;
	private int typeCode;
	private int state;
	private String time;
	private long optAmount;

	/**
	 * 账单明细
	 * 
	 * @param detailId
	 * @param payType
	 */
	private void withdrawDepositSchedule(long detailId, int payType) {
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
							createTime = data.getString("createTime");
							acctName = data.getString("acctName");
							acctNo = data.getString("acctNo");
							typeCode = data.getInt("typeCode");
							optAmount = data.getLong("optAmount");
							JSONArray withdraw = data.getJSONArray("withdraw");
							for (int i = 0; i < withdraw.length(); i++) {
								JSONObject with = withdraw.getJSONObject(i);
								state = with.getInt("state");
								time = with.getString("createTime");
							}
							mHandler.sendEmptyMessage(0);
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
		CustomProgress.showDialog(mContext, "查询中", false, null);
		PayMentService.withdrawDepositSchedule(detailId, payType, listener,
				null);
	}

}
