package com.zhiduan.crowdclient.task;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.PeakAdapter;
import com.zhiduan.crowdclient.data.CrowedUserInfo;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.TaskService;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;

/**
 * @author hexiuhui
 * @description 风云榜
 */
public class PeakActivity extends Activity implements IXListViewListener {

	private DropDownListView mPeakListView;
	private PeakAdapter mPeakAdapter;
	private List<CrowedUserInfo> mPeakData = new ArrayList<CrowedUserInfo>();
	
	private ImageView peak_copper_icon;
	private ImageView peak_gold_icon;
	private ImageView peak_silver_icon;
	
	private TextView peak_silver_name;
	private TextView peak_silver_money;
	
	private TextView peak_gold_name;
	private TextView peak_gold_money;
	
	private TextView peak_copper_name;
	private TextView peak_copper_money;
	
	private LinearLayout peak_back_layout;
	private LinearLayout peak_silver_layout;

	private int refresh = 0;
	private int m_currentPage = 1;
	private LoadTextNetTask mTaskAssigned = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_peak);
		setTitle("风云榜");
		initView();
		initData();
	}
	
	public void initView() {
		peak_gold_icon = (ImageView) findViewById(R.id.peak_gold_icon);
		peak_copper_icon = (ImageView) findViewById(R.id.peak_copper_icon);
		peak_silver_icon = (ImageView) findViewById(R.id.peak_silver_icon);
		
		peak_silver_name = (TextView) findViewById(R.id.peak_silver_name);
		peak_silver_money = (TextView) findViewById(R.id.peak_silver_money);
		
		peak_gold_name = (TextView) findViewById(R.id.peak_gold_name);
		peak_gold_money = (TextView) findViewById(R.id.peak_gold_money);
		
		peak_copper_name = (TextView) findViewById(R.id.peak_copper_name);
		peak_copper_money = (TextView) findViewById(R.id.peak_copper_money);
		
		peak_back_layout = (LinearLayout) findViewById(R.id.peak_back);
		peak_silver_layout = (LinearLayout) findViewById(R.id.peak_silver_layout);
		
		mPeakListView = (DropDownListView) findViewById(R.id.peak_listview);
		
		mPeakListView.setPullLoadEnable(false);
		mPeakListView.setPullRefreshEnable(true);
		mPeakListView.setXListViewListener(this);
		
		peak_back_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void initData() {
		mPeakAdapter = new PeakAdapter(this, mPeakData);
		mPeakListView.setAdapter(mPeakAdapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		getOneData();
	}
	
	//确认派单
	public LoadTextNetTask requestAssigned() {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				CustomProgress.dissDialog();
				mTaskAssigned = null;

				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj + "--");

						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONArray jsonArray = jsonObj.getJSONArray("data");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								String icon = jsonObject.getString("icon");
								long packetMoney = jsonObject.getLong("packetMoney");
								String phone = jsonObject.getString("phone");
								String sex = jsonObject.getString("sex");
								String userName = jsonObject.getString("userName");
								if (i == 0) {
									if (icon.equals("")) {
										peak_gold_icon.setImageResource(sex.contains("female") ? R.drawable.female:R.drawable.male);
									} else {
										MyApplication.getInstance().getImageLoader().displayImage(icon, peak_gold_icon, MyApplication.getInstance().getOptions(), null);
									}
									
									try {
										peak_gold_name.setText(userName);
										peak_gold_money.setText(AmountUtils.changeF2Y(packetMoney));
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else if (i == 1) {
									if (icon.equals("")) {
										peak_silver_icon.setImageResource(sex.contains("female") ? R.drawable.female:R.drawable.male);
									} else {
										MyApplication.getInstance().getImageLoader().displayImage(icon, peak_silver_icon, MyApplication.getInstance().getOptions(), null);
									}
									
									try {
										peak_silver_name.setText(userName);
										peak_silver_money.setText(AmountUtils.changeF2Y(packetMoney));
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else if (i == 2) {
									if (icon.equals("")) {
										peak_copper_icon.setImageResource(sex.contains("female") ? R.drawable.female:R.drawable.male);
									} else {
										MyApplication.getInstance().getImageLoader().displayImage(icon, peak_silver_icon, MyApplication.getInstance().getOptions(), null);
									}
									
									try {
										peak_copper_name.setText(userName);
										peak_copper_money.setText(AmountUtils.changeF2Y(packetMoney));
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									CrowedUserInfo info = new CrowedUserInfo();
									info.setIcon(icon);
									info.setMoney(packetMoney);
									info.setPhone(phone);
									info.setGender(sex);
									info.setRealName(userName);
									
									mPeakData.add(info);
								}
							}
							mPeakAdapter.notifyDataSetChanged();
							
							refreshInit();
							
							if (jsonArray.length() < 10) {
								mPeakListView.setLoadHide();
							} else {
								mPeakListView.setLoadShow();
							}
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(PeakActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(PeakActivity.this, result.m_nResultCode);
				}
			}
		};

		CustomProgress.showDialog(PeakActivity.this, "", false, null);
		LoadTextNetTask task = TaskService.getPeak(listener, null);

		return task;
	}
	private boolean isRfeshinit=true;
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mTaskAssigned != null) {
			mTaskAssigned.cancel(true);
			mTaskAssigned = null;
		}
	}

	// 下拉刷新
	private void refreshInit() {
		mPeakListView.stopRefresh();
		mPeakListView.stopLoadMore();
		// 也可以用系统时间
		mPeakListView.setRefreshTime("刚刚");

		refresh = 0;
		isRfeshinit=true;
	}
	
	private void getOneData() {
		mPeakData.clear();
		m_currentPage = 1;
		mTaskAssigned = requestAssigned();
	}
	
	@Override
	public void onRefresh() {
		if(!isRfeshinit){
			return;
		}
		isRfeshinit=false;
		if (refresh == 0) {
			refresh++;
			getOneData();
		}
	}

	@Override
	public void onLoadMore() {
		m_currentPage++;

		mTaskAssigned = requestAssigned();
	}
}
