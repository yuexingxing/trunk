package com.zhiduan.crowdclient.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
 * @description 已取消
 */
public class TaskCancelActivity extends Fragment implements IXListViewListener{
	private PaymentAdapter mCancelAdapter;
	private DropDownListView mTaskCancelListView;
	private List<TaskInfo> mCancelData = new ArrayList<TaskInfo>();
	private int refresh = 0;
	private int task_pageNumber = 1;
	private String mBeginTime; // 开始时间
	private String mEndTime; // 结束时间
	private LoadTextNetTask mGetCancelTask;
	
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		view = inflater.inflate(R.layout.activity_task_cancel, container, false);

		initView();
		initData();
		return view;
	}

	public void initView() {
		mTaskCancelListView = (DropDownListView) view.findViewById(R.id.lv_task_cancel_list);
		mTaskCancelListView.setPullLoadEnable(true);
		mTaskCancelListView.setPullRefreshEnable(true);
		mTaskCancelListView.setXListViewListener(this);
		
	}
	
	public void initData() {
		mCancelAdapter = new PaymentAdapter(TaskCancelActivity.this.getActivity(), mCancelData, PaymentAdapter.TASK_CANCEL_TYPE);
		mTaskCancelListView.setAdapter(mCancelAdapter);
	}

	public void onResume() {
		super.onResume();
		
		mBeginTime = CommandTools.getTimeDate(0);
		mEndTime = CommandTools.getTimeDate(1);
		getOneData();
	}
	
	protected LoadTextNetTask requestGetCancelData(String beginTime, String endTime, int pageNumber) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mGetCancelTask = null;

				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							mTaskCancelListView.setPullLoadEnable(true);
							JSONObject dataObj = jsonObj.getJSONObject("data");
							JSONArray jsonArray = dataObj.getJSONArray("responseDto");
							
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								String theme = jsonObject.getString("theme");
								String sex = jsonObject.getString("sex");
								long reward = jsonObject.getLong("reward");
								String claim = jsonObject.getString("claim");
								String deadline = jsonObject.getString("deadline");
								String orderId = jsonObject.getString("orderId");
								String coverImage = jsonObject.getString("coverImage");
								
								SimpleDateFormat OldDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								SimpleDateFormat NewFormat = new SimpleDateFormat("yyyy-MM-dd");
								
								String newDate = "";
								try {
									newDate = NewFormat.format(OldDateFormat.parse(deadline));
								} catch (java.text.ParseException e) {
									e.printStackTrace();
								}
								
								TaskInfo info = new TaskInfo();
								info.setTask_title(theme);
								info.setTask_id(orderId);
								info.setTask_people_sex(sex);
								info.setTask_logo(coverImage);
								info.setTask_money(reward);
								info.setTask_require(claim);
								info.setTask_time(newDate);
								mCancelData.add(info);
							}
							mCancelAdapter.notifyDataSetChanged();
							
							refreshInit();
							if (jsonArray.length() < 10) {
								mTaskCancelListView.setLoadHide();
							} else {
								mTaskCancelListView.setLoadShow();
							}
						} else {
							String message = jsonObj.getString("message");
							// CommandTools.showToast(message);
							refreshInit();

							String code = jsonObj.getString("code");
							if (code.equals("105")) {
								mCancelData.clear();
								mCancelAdapter.notifyDataSetChanged();
								mTaskCancelListView.setPullLoadEnable(false);
							} else if (code.equals("010013")) {
								mTaskCancelListView.setLoadHide();
							}
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(TaskCancelActivity.this.getActivity());
						e.printStackTrace();
						refreshInit();
					}
				} else {
					Util.showNetErrorMessage(TaskCancelActivity.this.getActivity(), result.m_nResultCode);
					refreshInit();
				}
			}
		};

		LoadTextNetTask task = TaskService.getCancelTask(beginTime, endTime, pageNumber, listener, null);
		return task;
	}
	
	public void onDestroy() {
		super.onDestroy();
		
		if (mGetCancelTask != null) {
			mGetCancelTask.cancel(true);
			mGetCancelTask = null;
		}
	}
	
	// 下拉刷新
	private void refreshInit() {
		mTaskCancelListView.stopRefresh();
		mTaskCancelListView.stopLoadMore();
		// 也可以用系统时间
		mTaskCancelListView.setRefreshTime("刚刚");

		refresh = 0;
	}
	
	private void getOneData() {
		mCancelData.clear();
		task_pageNumber = 1;
		mGetCancelTask = requestGetCancelData(CommandTools.getChangeDate(-7)+" 00:00:00", CommandTools.getChangeDate(0)+" 23:59:59",task_pageNumber);
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

		mGetCancelTask = requestGetCancelData(CommandTools.getChangeDate(-7)+" 00:00:00", CommandTools.getChangeDate(0)+" 23:59:59",task_pageNumber);
	}
}
