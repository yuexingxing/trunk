package com.zhiduan.crowdclient.wallet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.adapter.CommonAdapter;
import com.zhiduan.crowdclient.adapter.ViewHolder;
import com.zhiduan.crowdclient.data.RecordsInfo;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.PayMentService;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CalendarWindows;
import com.zhiduan.crowdclient.view.CalendarWindows.CalendarClickListener;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;

/**
 * 提成收入
 * 
 * @author wfq
 * 
 */
public class WalletDeductRecordActivity extends BaseActivity implements
IXListViewListener {

	private DropDownListView mLv;
	private List<RecordsInfo> mData = new ArrayList<RecordsInfo>();
	private int[] pic = new int[] { R.drawable.wallet_withdrawals_01,
			R.drawable.wallet_withdrawals_02, R.drawable.wallet_withdrawals_03,
			R.drawable.wallet_withdrawals_04 };
	private Random ra;
	private String date;
	private boolean isAcivate = false;
	private CommonAdapter<RecordsInfo> mAdapter;
	private String lastTime = CommandTools.getTimess();
	private String nextTime = CommandTools.getTimess();

	private int count = 1;
	private int number = 20;
	private ImageView mLvXian;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_wallet_record, this);
		ra = new Random();
	}

	@Override
	public void initView() {
		setTitle("提成收入");
		hidenRightGone();
		ShowRightPic();
		setRightPic(R.drawable.wallet_calendar);
		mLv = (DropDownListView) findViewById(R.id.wallet_record_lv);
		mLvXian = (ImageView) findViewById(R.id.wallet_record_lv_xian);
		findViewById(R.id.wallet_record_rl).setVisibility(View.GONE);
		mLv.setAdapter(mAdapter = new CommonAdapter<RecordsInfo>(this, mData, R.layout.item_wallet_record) {
			@Override
			public void convert(ViewHolder helper, RecordsInfo item) {
			//	helper.setBackgroundResource(R.id.item_wallet_record_iv_pic, pic[getPosition() >= 3 ? 3 : getPosition()]);
				helper.setText(R.id.item_wallet_record_tv_time, item.getCreateTime());
				String payType = "提成收入";
				String type = "+";
				int rgb = Color.rgb(53, 154, 43);
				TextView view = helper.getView(R.id.item_wallet_record_tv_money);
				view.setTextColor(rgb);
				helper.setText(R.id.item_wallet_record_tv_type, payType);
				helper.setText(R.id.item_wallet_record_tv_money, type + CommandTools.longTOString(item.getOptAmount()));
			}
		});

		mLv.setPullLoadEnable(true);
		mLv.setPullRefreshEnable(true);
		mLv.setXListViewListener(this);
	}

	@Override
	public void initData() {

			billRecord(0);
	}

	public void rightPicClick() {
		CalendarWindows.showWindows(WalletDeductRecordActivity.this, getRightPic(), new CalendarClickListener() {
			@Override
			public void pitchOnDate(String date) {
				count = 1;
				lastTime = date;
				nextTime = date;
				mLv.setPullLoadEnable(true);
				billRecord(0);

			}
		});
	}

	private long expense;
	private long income;

	/**
	 * 账单记录
	 * 
	 * @param date
	 * @param i
	 * @param j
	 */
	private void billRecord(final int type) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mLv.stopRefresh();
				mLv.stopLoadMore();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					JSONObject jsonObj;
					try {
						jsonObj = new JSONObject(mresult.m_strContent);
						Logs.i("zd", jsonObj.toString() + "---");
						int success = jsonObj.getInt("success");
						if (success == 0) {
							if (type == 0) {
								mData.clear();
							}
							JSONObject data = jsonObj.getJSONObject("data");
							expense = data.getLong("expense");
							income = data.getLong("income");
							JSONArray billlist = data.getJSONArray("billlist");

							for (int i = 0; i < billlist.length(); i++) {
								JSONObject bill = billlist.getJSONObject(i);
								long detailId = bill.getLong("detailId");
								long optAmount = bill.getLong("optAmount");
								int payType = bill.getInt("payType");
								String createTime = bill.getString("createTime");
								RecordsInfo recordsInfo = new RecordsInfo(detailId, optAmount, payType, createTime);
								mData.add(recordsInfo);
							}
							mAdapter.notifyDataSetChanged();

							count++;
							if (billlist.length() == 0) {
								mLv.setPullLoadEnable(false);
								hideXian();
							}else{
								showXian();
							}
						} else {
							String message = jsonObj.getString("message");
							// CommandTools.showToast(message);
							mLv.setPullLoadEnable(false);
							mData.clear();
							hideXian();
						}

					} catch (JSONException e) {
						e.printStackTrace();
						Util.showJsonParseErrorMessage(mContext);
						hideXian();
					}
				} else {
					Util.showNetErrorMessage(mContext, result.m_nResultCode);
					hideXian();
				}
				CustomProgress.dissDialog();

			}
		};
		CustomProgress.showDialog(mContext, "查询中", false, null);
		PayMentService.billRecord(lastTime, nextTime, count, number, 4, listener, null);
	}

	@Override
	public void onRefresh() {
		count = 1;
		billRecord(0);
	}

	@Override
	public void onLoadMore() {
		billRecord(1);
	}
	
	public void hideXian() {
		mLvXian.setVisibility(View.INVISIBLE);
	}

	public void showXian() {
		mLvXian.setVisibility(View.VISIBLE);
	}
}
