package com.zhiduan.crowdclient.wallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.adapter.CommonAdapter;
import com.zhiduan.crowdclient.adapter.RecordsAdapter;
import com.zhiduan.crowdclient.adapter.ViewHolder;
import com.zhiduan.crowdclient.adapter.ViewHolder.ResultCallback;
import com.zhiduan.crowdclient.data.RecordSDetail;
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
import com.zhiduan.crowdclient.wallet.widget.DateChooseWheelViewDialog;

/**
 * 交易记录
 * 
 * @author wfq
 * 
 */
public class WalletRecordActivity extends BaseActivity implements
		IXListViewListener, OnClickListener {

	private DropDownListView mLv;
	private List<RecordsInfo> mData = new ArrayList<RecordsInfo>();
	private int[] pic = new int[] { R.drawable.wallet_withdrawals_01,
			R.drawable.wallet_withdrawals_02, R.drawable.wallet_withdrawals_03,
			R.drawable.wallet_withdrawals_04 };
	private String date;
	private boolean isAcivate = false;
	private String lastTime = CommandTools.getTimess();
	private String nextTime = CommandTools.getTimess();

	private int count = 1;
	private int number = 20;
	private RelativeLayout mRl;
	private int officeRoleType;
	private ImageView mLvXian;
	private boolean is_open = true;
	
	private Handler mHandler;
	private ViewHolder mViewHolder;
	private RecordsAdapter adapter;
	private int desc_position=-1;
	private Map<Integer, List<RecordSDetail>>decsMap=new HashMap<Integer, List<RecordSDetail>>();
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_wallet_record, this);
	}

	@Override
	public void initView() {
		setTitle("交易记录");
		hidenRightGone();
		ShowRightPic();
		setRightPic(R.drawable.wallet_calendar);
		mLv = (DropDownListView) findViewById(R.id.wallet_record_lv);
		mRl = (RelativeLayout) findViewById(R.id.wallet_record_rl);
		mLvXian = (ImageView) findViewById(R.id.wallet_record_lv_xian);
		adapter = new RecordsAdapter(mContext, mData,decsMap);
		mLv.setAdapter(adapter);
		mLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				is_open = mData.get(position - 1).isIs_open();
				RecordsInfo info = mData.get(position - 1);
				desc_position=position-1;
				if (info.getPayType() == 2 || info.getPayType() == 5
						|| info.getPayType() == 6 || info.getPayType() == 7) {
					return;
				} else {
					if (!is_open) {

						info.setIs_open(true);
						withdrawDepositSchedule(info.getDetailId(),
								info.getPayType());
						adapter.notifyDataSetChanged();
					} else {
						info.setIs_open(false);
						adapter.notifyDataSetChanged();
					}
				}

			}
		});
		mLv.setPullLoadEnable(true);
		mLv.setPullRefreshEnable(true);
		mLv.setXListViewListener(this);
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		intExtra = intent.getIntExtra("payType", 0);
		officeRoleType = intent.getIntExtra("officeRoleType", 0);
		// setTitle(intExtra == 0 ? "账单记录" : "收入明细");
		if (intExtra == 1) {
			income(0);
		} else {

			billRecord(0);
		}
		if (officeRoleType == 1) {
			mRl.setVisibility(View.GONE);
		} else {
			mRl.setOnClickListener(this);
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLvXian
					.getLayoutParams();
			layoutParams.topMargin = CommandTools.dip2px(mContext, 21);
			mLvXian.setLayoutParams(layoutParams);
			RelativeLayout.LayoutParams mLvParams = (RelativeLayout.LayoutParams) mLv
					.getLayoutParams();
			mLvParams.topMargin = CommandTools.dip2px(mContext, 8);
			mLv.setLayoutParams(mLvParams);
		}
	}

	// 选择日期
	public void rightPicClick() {
		DateChooseWheelViewDialog endDateChooseDialog = new DateChooseWheelViewDialog(
				WalletRecordActivity.this,
				new DateChooseWheelViewDialog.DateChooseInterface() {
					@Override
					public void getDateTime(String time, boolean longTimeChecked) {
						count = 1;
						lastTime = time;
						nextTime = time;
						mLv.setPullLoadEnable(true);
						if (intExtra == 1) {
							income(0);
						} else {

							billRecord(0);
						}
					}
				});
		endDateChooseDialog.setTimePickerGone(true);
		endDateChooseDialog.setDateDialogTitle("选择日期");
		endDateChooseDialog.showDateChooseDialog();
		// CalendarWindows.showWindows(WalletRecordActivity.this, getRightPic(),
		// new CalendarClickListener() {
		// @Override
		// public void pitchOnDate(String date) {
		// count = 1;
		// lastTime = date;
		// nextTime = date;
		// mLv.setPullLoadEnable(true);
		// if (intExtra == 1) {
		// income(0);
		// } else {
		//
		// billRecord(0);
		// }
		// }
		// });
	}

	private long expense;
	private long income;
	private int intExtra;

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
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {
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
								String createTime = bill
										.getString("createTime");
								RecordsInfo recordsInfo = new RecordsInfo(
										detailId, optAmount, payType,
										createTime);
								mData.add(recordsInfo);
							}
							Logs.i("zdkj", "记录data===" + mData.size());
							adapter.notifyDataSetChanged();

							count++;
							if (billlist.length() == 0) {
								mLv.setPullLoadEnable(false);
								hideXian();
							} else {
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
		PayMentService.billRecord(lastTime, nextTime, count, number, 0,
				listener, null);
	}

	@Override
	public void onRefresh() {
		count = 1;

		if (intExtra == 1) {
			income(0);
		} else {
			billRecord(0);
		}
	}

	@Override
	public void onLoadMore() {
		if (intExtra == 1) {
			income(1);
		} else {
			billRecord(1);
		}
	}

	/**
	 * 收入明细
	 */
	private void income(final int type) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {
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
							JSONArray data = jsonObj.getJSONArray("data");

							for (int i = 0; i < data.length(); i++) {
								JSONObject bill = data.getJSONObject(i);
								long detailId = bill.getLong("flowId");
								long optAmount = bill.getLong("optAmount");
								String createTime = bill
										.getString("createTime");
								RecordsInfo recordsInfo = new RecordsInfo(
										detailId, optAmount, 4, createTime);
								mData.add(recordsInfo);
							}
							adapter.notifyDataSetChanged();

							count++;
							if (data.length() == 0) {
								mLv.setPullLoadEnable(false);
								hideXian();
							} else {
								showXian();
							}
						} else {
							String message = jsonObj.getString("message");
							// CommandTools.showToast(message);
							mLv.setPullLoadEnable(false);

							if (type == 0) {
								mData.clear();
							}
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
		PayMentService
				.income(lastTime, nextTime, count, number, listener, null);
	}

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
							ArrayList<RecordSDetail> recordSDetails = new ArrayList<RecordSDetail>();
//							recordSDetails.clear();
							JSONObject data = jsonObj.getJSONObject("data");
							Logs.i("zdkj", "记录详情===" + data);
							JSONArray detailArray = data
									.optJSONArray("detailList");
							RecordSDetail recordSDetail;
							for (int i = 0; i < detailArray.length(); i++) {
								recordSDetail = new RecordSDetail();
								JSONObject object = detailArray
										.optJSONObject(i);
								recordSDetail.setOptAmount(object
										.optLong("optAmount"));
								recordSDetail.setRemark(object
										.optString("remark"));
								recordSDetails.add(recordSDetail);
							}
							decsMap.put(desc_position, recordSDetails);
							Logs.i("zdkj", "记录详情info===" + recordSDetails.get(0).getOptAmount());
							mData.get(desc_position).setDescList(recordSDetails);
							Logs.i("zdkj",
									"recordSDetail===" + recordSDetails.size());
							adapter.notifyDataSetChanged();
							
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

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.wallet_record_rl:
			Intent intent = new Intent(this, WalletDeductRecordActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	public void hideXian() {
		mLvXian.setVisibility(View.INVISIBLE);
	}

	public void showXian() {
		mLvXian.setVisibility(View.VISIBLE);
	}
}
