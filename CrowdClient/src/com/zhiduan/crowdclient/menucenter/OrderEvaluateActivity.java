package com.zhiduan.crowdclient.menucenter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.adapter.EvaluateAdapter;
import com.zhiduan.crowdclient.data.EvaluateInfo;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.UserService;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;

/**
 * <pre>
 * Description  订单评价
 * Author:		hexiuhui
 * </pre>
 */
public class OrderEvaluateActivity extends BaseActivity implements IXListViewListener{

	private EvaluateAdapter mEvaluateAdapter;
	private DropDownListView mEvaluateListView;
	private List<EvaluateInfo> mListDate = new ArrayList<EvaluateInfo>();

	private LoadTextNetTask mTaskRequestGetData;

	private TextView mPraiseTxt;
	private TextView mCancelTxt;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_order_evaluate, this);
		setTitle("订单评价");
	}

	@Override
	public void initView() {
		mPraiseTxt = (TextView) findViewById(R.id.praise_txt);
		mCancelTxt = (TextView) findViewById(R.id.cancel_txt);

		mEvaluateListView = (DropDownListView) findViewById(R.id.evaluate_listview);
		mEvaluateListView.setPullRefreshEnable(true);
		mEvaluateListView.setPullLoadEnable(false);
		mEvaluateListView.setXListViewListener(this);
	}

	@Override
	public void initData() {
		mEvaluateAdapter = new EvaluateAdapter(OrderEvaluateActivity.this, mListDate);

		mEvaluateListView.setAdapter(mEvaluateAdapter);

		//		getOneData();
		onRefresh();
	}

	protected LoadTextNetTask requestGetEvaluateData(int pageNumber, final int type) {

		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestGetData = null;

				mEvaluateListView.stopRefresh();
				mEvaluateListView.stopLoadMore();

				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONObject jsonObject = jsonObj.getJSONObject("data");

							JSONObject countObj = jsonObject.getJSONObject("statistics");
							String cancelRatio = countObj.getString("cancelRatio");
							String favourableRatio = countObj.getString("favourableRatio");

							mCancelTxt.setText(cancelRatio + "%");
							mPraiseTxt.setText(favourableRatio + "%");

							List<EvaluateInfo> list = new ArrayList<EvaluateInfo>();
							JSONArray jsonArray = jsonObject.getJSONArray("responseDto");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject2 = jsonArray.getJSONObject(i);
								String appraiserIcon = jsonObject2.getString("appraiserIcon");
								Double overallScore = jsonObject2.optDouble("overallScore");
								String overallDesc = jsonObject2.optString("overallDesc");
								String createDate = jsonObject2.optString("createDate");
								EvaluateInfo info = new EvaluateInfo();
								info.setAppraiserGender(jsonObject2.optString("appraiserGender"));
								info.setAppraiserName(jsonObject2.optString("appraiserName"));
								info.setEvaluateContent(overallDesc);
								info.setEvaluateTime(createDate);
								info.setEvaluateUrl(appraiserIcon);
								info.setOverallScore(overallScore);

								list.add(info);
							}

							if(type == OrderUtil.REFRESH_DATA){
								mListDate.clear();
							}
							mListDate.addAll(list);
							
							mEvaluateAdapter.notifyDataSetChanged();

							// 也可以用系统时间
							mEvaluateListView.setRefreshTime("刚刚");

							if (jsonArray.length() < 10) {
								mEvaluateListView.setLoadHide();
							} else {
								mEvaluateListView.setPullLoadEnable(true);
								mEvaluateListView.setLoadShow();
							}
						} else {
							String strMsg = jsonObj.getString("message");
							Toast.makeText(OrderEvaluateActivity.this, strMsg, Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(OrderEvaluateActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(OrderEvaluateActivity.this, result.m_nResultCode);
				}
			}

		};

		CustomProgress.showDialog(OrderEvaluateActivity.this, "获取数据中...", false, null);
		LoadTextNetTask task = UserService.getEvaluateLists(pageNumber, listener, null);

		return task;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mTaskRequestGetData != null) {
			mTaskRequestGetData.cancel(true);
			mTaskRequestGetData = null;
		}
	}

	private int m_currentPage = 1;
	//获取第一页数据
	private void getOneData() {
		mListDate.clear();
		m_currentPage = 1;
		mTaskRequestGetData = requestGetEvaluateData(m_currentPage, OrderUtil.REFRESH_DATA);
	}

	@Override
	public void onRefresh() {

		getOneData();
	}

	@Override
	public void onLoadMore() {
		m_currentPage++;
		mTaskRequestGetData = requestGetEvaluateData(m_currentPage, OrderUtil.LOADMORE_DATA);
	}
}
