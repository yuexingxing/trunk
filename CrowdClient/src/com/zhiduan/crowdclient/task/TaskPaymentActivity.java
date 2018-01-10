package com.zhiduan.crowdclient.task;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.PaymentAdapter;
import com.zhiduan.crowdclient.data.TaskInfo;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.TaskService;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;

/**
 * @author hexiuhui
 * @description 已结算
 */
public class TaskPaymentActivity extends Fragment implements IXListViewListener {

	private PaymentAdapter mPaymentAdapter;
	private DropDownListView mTaskPaymentListView;
	private List<TaskInfo> mPaymentData = new ArrayList<TaskInfo>();
	private int refresh = 0;
	private int task_pageNumber = 1;
	private String mBeginTime; // 开始时间
	private String mEndTime; // 结束时间
	private LoadTextNetTask mGetPaymentTask;

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		view = inflater.inflate(R.layout.activity_payment, container, false);

		initView();
		initData();

		createRefreshHandler();
		return view;
	}

	public void initView() {
		mTaskPaymentListView = (DropDownListView) view.findViewById(R.id.lv_task_payment_list);
		mTaskPaymentListView.setPullLoadEnable(true);
		mTaskPaymentListView.setPullRefreshEnable(true);
		mTaskPaymentListView.setXListViewListener(this);

		mTaskPaymentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent=new Intent(TaskPaymentActivity.this.getActivity().getParent(), TaskDetailActivity.class);
				intent.putExtra("task_status", Constant.TASK_FINISH_DETAIL);
				intent.putExtra("orderid", mPaymentData.get(position - 1).getTask_id());
				startActivity(intent);
			}
		});
	}
	
	public void initData() {
		mPaymentAdapter = new PaymentAdapter(TaskPaymentActivity.this.getActivity(), mPaymentData, PaymentAdapter.TASK_PAYMENT_TYPE);
		mTaskPaymentListView.setAdapter(mPaymentAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		getOneData();
		//		mGetPaymentTask = requestGetPaymentData(mBeginTime, mEndTime, m_currentPage);
	}

	private Handler mRefreshHandler;
	public static final int REFRESH_PAYMENT = 1; // 刷新任务已结算

	private void createRefreshHandler() {
		mRefreshHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case REFRESH_PAYMENT:
					getOneData();
					break;
				default:
					break;
				}
			}
		};

		MyApplication.getInstance().m_refreshPaymentHandler = mRefreshHandler;
	}

	protected LoadTextNetTask requestGetPaymentData(String beginTime, String endTime, int pageNumber) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mGetPaymentTask = null;

				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							mTaskPaymentListView.setPullLoadEnable(true);
							//
							JSONObject dataObj = jsonObj.getJSONObject("data");
							JSONArray jsonArray = dataObj.getJSONArray("responseDto");

							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								String theme = jsonObject.getString("theme");
								String sex = jsonObject.getString("sex");
								long reward = jsonObject.getLong("reward");
								String claim = jsonObject.getString("claim");
								Double overallScore = jsonObject.getDouble("overallScore");
								long finalMoney = jsonObject.getLong("finalMoney");
								String orderId = jsonObject.getString("orderId");
								String coverImage = jsonObject.getString("coverImage");

								TaskInfo info = new TaskInfo();
								info.setTask_title(theme);
								info.setTask_id(orderId);
								info.setTask_people_sex(sex);
								info.setTask_logo(coverImage);
								info.setTask_money(reward);
								info.setTask_require(claim);
								info.setTask_grade(overallScore);
								info.setTask_actual_money(finalMoney);

								mPaymentData.add(info);
							}
							mPaymentAdapter.notifyDataSetChanged();

							refreshInit();
							//
							if (jsonArray.length() < 10) {
								mTaskPaymentListView.setLoadHide();
							} else {
								mTaskPaymentListView.setLoadShow();
							}
						} else {
							String message = jsonObj.getString("message");
							// CommandTools.showToast(message);
							refreshInit();

							String code = jsonObj.getString("code");
							if (code.equals("105")) {
								mPaymentData.clear();
								mPaymentAdapter.notifyDataSetChanged();
								mTaskPaymentListView.setPullLoadEnable(false);
							} else if (code.equals("010013")) {
								mTaskPaymentListView.setLoadHide();
							}
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(TaskPaymentActivity.this.getActivity());
						e.printStackTrace();
						refreshInit();
					}
				} else {
					Util.showNetErrorMessage(TaskPaymentActivity.this.getActivity(), result.m_nResultCode);
					refreshInit();
				}
			}
		};

		LoadTextNetTask task = TaskService.getPaymentTask(beginTime, endTime, pageNumber, listener, null);
		return task;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mGetPaymentTask != null) {
			mGetPaymentTask.cancel(true);
			mGetPaymentTask = null;
		}
	}

	// 下拉刷新
	private void refreshInit() {
		mTaskPaymentListView.stopRefresh();
		mTaskPaymentListView.stopLoadMore();
		// 也可以用系统时间
		mTaskPaymentListView.setRefreshTime("刚刚");

		refresh = 0;
	}

	private void getOneData() {
		mPaymentData.clear();
		task_pageNumber = 1;
		mGetPaymentTask = requestGetPaymentData(CommandTools.getChangeDate(-7)+" 00:00:00", CommandTools.getChangeDate(0)+" 23:59:59",task_pageNumber);
	}

	@Override
	public void onRefresh() {
		if (refresh == 0) {
			refresh++;
			getOneData();
		}
	}

	@Override
	public void onLoadMore() {
		task_pageNumber++;
		mGetPaymentTask = requestGetPaymentData(CommandTools.getChangeDate(-7)+" 00:00:00", CommandTools.getChangeDate(0)+" 23:59:59",task_pageNumber);
	}
}
