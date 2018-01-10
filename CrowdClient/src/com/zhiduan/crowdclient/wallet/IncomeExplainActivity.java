package com.zhiduan.crowdclient.wallet;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.service.PayMentService;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CustomProgress;
/**
 * 提现收入说明
 * @author wfq
 *
 */
public class IncomeExplainActivity extends BaseActivity {

	private TextView mTvOrderNumber;
	private TextView mTvNumber;
	private TextView mTvMoney;
	private TextView mTvDeductMoney;
	private RelativeLayout mRlType2;
	private TextView mTvOrderNumber2;
	private TextView mTvNumber2;
	private TextView mTvMoney2;
	private TextView mTvDeductMoney2;
	private TextView mTvContent;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_income_explain, this);
		setTitle("提成明细");
	}

	@Override
	public void initView() {
		mTvOrderNumber = (TextView) findViewById(R.id.income_explain_tv_order_number);
		mTvNumber = (TextView) findViewById(R.id.income_explain_tv_number);
		mTvMoney = (TextView) findViewById(R.id.income_explain_tv_money);
		mTvDeductMoney = (TextView) findViewById(R.id.income_explain_tv_deduct_money);
		
		mRlType2 = (RelativeLayout) findViewById(R.id.income_explain_rl_type2);
		mTvOrderNumber2 = (TextView) findViewById(R.id.income_explain_tv_order_number2);
		mTvNumber2 = (TextView) findViewById(R.id.income_explain_tv_number2);
		mTvMoney2 = (TextView) findViewById(R.id.income_explain_tv_money2);
		mTvDeductMoney2 = (TextView) findViewById(R.id.income_explain_tv_deduct_money2);
		
		mTvContent = (TextView) findViewById(R.id.income_explain_tv_content);
//		getUserStatistics();
	}

	@Override
	public void initData() {

	}

	private long groupOrderCommission;
	private long storeOrderNum;
	private long storeOrderCommission;
	private long groupOrderNum;
	/**
	 * 获取用户信息
	 */
	private void getUserStatistics() {
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
							groupOrderNum = data.getLong("groupOrderNum");
							groupOrderCommission = data.getLong("groupOrderCommission");
							mTvNumber.setText(""+groupOrderNum);//
							mTvMoney.setText("￥"+CommandTools.longTOString(groupOrderCommission));
							storeOrderNum = data.getLong("storeOrderNum");
							storeOrderCommission = data.getLong("storeOrderCommission");
							mTvNumber2.setText(""+storeOrderNum);
							mTvMoney2.setText("￥"+CommandTools.longTOString(storeOrderCommission));
							
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
		CustomProgress.showDialog(this, "查询中", false, null);
		PayMentService.getUserStatistics(SharedPreferencesUtils.getParam(Constant.SP_LOGIN_NAME, "").toString(),listener, null);
	}
}
