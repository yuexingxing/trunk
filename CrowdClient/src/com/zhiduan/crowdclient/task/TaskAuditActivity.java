package com.zhiduan.crowdclient.task;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.TaskAdapter;
import com.zhiduan.crowdclient.adapter.TaskAdapter.OnBottomClickListener;
import com.zhiduan.crowdclient.data.TaskInfo;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.TaskService;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;

/**
 * 审核中
 * 
 * @author yxx
 * 
 * @date 2016-8-31 下午11:55:21
 * 
 */
public class TaskAuditActivity extends Fragment implements IXListViewListener {

	private Context mContext;
	private DropDownListView lv_task_list;
	private TaskInfo info;
	private List<TaskInfo> datas = new ArrayList<TaskInfo>();
	private TaskAdapter adapter;
	private LoadTextNetTask mTaskAssigned;
	private int task_pageNumber = 1;
	private int pageCount;
	
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		view = inflater.inflate(R.layout.activity_under_way, container, false);

		initView();
		initData();
		
		createRefreshHandler();
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		task_pageNumber = 1;
		mTaskAssigned = GetTaskList(CommandTools.getChangeDate(-7) + " 00:00:00", CommandTools.getChangeDate(0) + " 23:59:59", 1);
	}
	private Handler mRefreshHandler;
	public static final int REFRESH_AUDIT = 1; // 刷新审核中

	private void createRefreshHandler() {
		mRefreshHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case REFRESH_AUDIT:
					task_pageNumber = 1;
					mTaskAssigned = GetTaskList(CommandTools.getChangeDate(-7) + " 00:00:00", CommandTools.getChangeDate(0) + " 23:59:59", 1);
					break;
				default:
					break;
				}
			}
		};

		MyApplication.getInstance().m_refreshAuditHandler = mRefreshHandler;
	}

	private void initData() {
		adapter = new TaskAdapter(mContext, datas, Constant.TASK_AUDIT_DETAIL);
		lv_task_list.setAdapter(adapter);
		adapter.setOnBottomClickListener(new OnBottomClickListener() {
			@Override
			public void onBottomClick(View v, int position) {
				Intent intent = new Intent(mContext, TaskDetailActivity.class);
				intent.putExtra("task_status", Constant.TASK_AUDIT_DETAIL);
				intent.putExtra("orderid", datas.get(position).getTask_id());
				startActivity(intent);
			}
		});
	}

	public LoadTextNetTask GetTaskList(String beginTime, String endTime, int pageNumber) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskAssigned = null;
				if (task_pageNumber == 1) {
					datas.clear();
				}
				lv_task_list.setRefreshTime(CommandTools.getTime());
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						int success = jsonObj.getInt("success");
						if (success == 0) {

							JSONObject dataobject = jsonObj.getJSONObject("data");
							pageCount = dataobject.optInt("totalPageCount");
							if (task_pageNumber >= pageCount) {
								lv_task_list.setLoadHide();
							}
							JSONArray jsonArray = dataobject.getJSONArray("responseDto");

							for (int i = 0; i < jsonArray.length(); i++) {
								info = new TaskInfo();
								jsonObj = jsonArray.optJSONObject(i);
								info.setTask_id(jsonObj.optLong("orderId") + "");
								info.setTask_title(jsonObj.optString("theme"));
								info.setTask_people_sex(jsonObj.optString("sex"));
								info.setTask_require(jsonObj.optString("claim"));
								info.setTask_money(jsonObj.optInt("claim"));
								info.setTask_status(jsonObj.optInt("status"));
								info.setTask_time(jsonObj.optString("deadline"));
								info.setTask_logo(jsonObj.optString("coverImage"));

								info.setTask_money(jsonObj.optLong("finalMoney"));

								datas.add(info);
							}
							adapter.notifyDataSetChanged();
							if (jsonArray.length() < 10) {
								lv_task_list.setLoadHide();
							} else {
								lv_task_list.setLoadShow();
							}
						} else {
							String message = jsonObj.getString("message");
							// CommandTools.showToast(message);
							String code = jsonObj.getString("code");
							if (code.equals("105")) {
								datas.clear();
								adapter.notifyDataSetChanged();
								lv_task_list.setPullLoadEnable(false);
							} else if (code.equals("010013")) {
								lv_task_list.setLoadHide();
							}

							// CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(TaskAuditActivity.this.getActivity());
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(TaskAuditActivity.this.getActivity(), result.m_nResultCode);
				}
			}
		};

		LoadTextNetTask task = TaskService.getAuditTask(beginTime, endTime, pageNumber, listener, null);
		return task;
	}

	private void initView() {
		
		mContext = TaskAuditActivity.this.getActivity();
		lv_task_list = (DropDownListView) view.findViewById(R.id.lv_task_list);
		lv_task_list.setPullLoadEnable(true);
		lv_task_list.setPullRefreshEnable(true);
		lv_task_list.setXListViewListener(this);
	}

	@Override
	public void onRefresh() {
		task_pageNumber = 1;
		mTaskAssigned = GetTaskList(CommandTools.getChangeDate(-7)
				+ " 00:00:00", CommandTools.getChangeDate(0) + " 23:59:59",
				task_pageNumber);
		lv_task_list.stopRefresh();
		lv_task_list.stopLoadMore();
	}

	@Override
	public void onLoadMore() {
		task_pageNumber++;
		mTaskAssigned = GetTaskList(CommandTools.getChangeDate(-7) + " 00:00:00", CommandTools.getChangeDate(0) + " 23:59:59", task_pageNumber);
		lv_task_list.stopRefresh();
		lv_task_list.stopLoadMore();

	}

}
