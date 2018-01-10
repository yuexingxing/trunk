package com.zhiduan.crowdclient.wallet;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
 * 收入详情
 * 
 * @author wfq
 * 
 */
public class EarningDetailsActivity extends BaseActivity {

	private TextView mTvBalance;
	private TextView mTvType;
	private TextView mTvTime;
	private TextView mTvNumber;
	private TextView mTvAccount;
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			mTvTime.setText(createTime);
//			mTvBalance.setText(CommandTools.longTOString(optbalance));
			// 1:已提交 2处理中 3:已到账
			mTvNumber.setText("" + thdId);
			mTvAccount.setText(CommandTools.longTOString(optAmount));
			mTvTime.setText(createTime);
		};
	};

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_earning_details, this);
	}

	@Override
	public void initView() {
		setTitle("收入详情");
		mTvBalance = (TextView) findViewById(R.id.earning_details_tv_balance);
		mTvType = (TextView) findViewById(R.id.earning_details_tv_type);
		mTvTime = (TextView) findViewById(R.id.earning_details_tv_time);
		mTvNumber = (TextView) findViewById(R.id.earning_details_tv_number);
		mTvAccount = (TextView) findViewById(R.id.earning_details_tv_account);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		long detailId = intent.getLongExtra("detailId", 0);
		payType = intent.getIntExtra("payType", 0);

		withdrawDepositSchedule(detailId, payType);

	}

	private String createTime;
	private long optAmount;
//	private long optbalance;
	private int payType;
	private String thdId;

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
							createTime = data.optString("createTime");
							optAmount = data.optLong("optAmount");
//							optbalance = data.getLong("optbalance");
							thdId = data.optString("thdId");
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
		PayMentService.withdrawDepositSchedule(detailId, payType, listener, null);
	}

}
