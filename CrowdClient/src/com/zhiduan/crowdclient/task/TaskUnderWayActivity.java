package com.zhiduan.crowdclient.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
 * 
 * <pre>
 * Description	任务进行中
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-15 上午11:01:07
 * </pre>
 */
public class TaskUnderWayActivity extends Fragment implements IXListViewListener {

	private DropDownListView lv_task_list;
	private TaskInfo info;
	private int pageCount;
	private List<TaskInfo> datas = new ArrayList<TaskInfo>();
	private TaskAdapter adapter;
	private LoadTextNetTask mTaskAssigned;
	private int task_pageNumber = 1;
	
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		view = inflater.inflate(R.layout.activity_under_way, container, false);

		initView();
		initData();
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		task_pageNumber = 1;
		mTaskAssigned = GetTaskList(CommandTools.getChangeDate(-7)
				+ " 00:00:00", CommandTools.getChangeDate(0) + " 23:59:59",
				task_pageNumber);
	}

	private void initData() {
		adapter = new TaskAdapter(TaskUnderWayActivity.this.getActivity().getParent(), datas, 0);
		lv_task_list.setAdapter(adapter);
		try {

			adapter.setOnBottomClickListener(new OnBottomClickListener() {

				@Override
				public void onBottomClick(View v, int position) {
					// TODO Auto-generated method stub
					if (datas.size() != 0) {
						Intent intent = new Intent(TaskUnderWayActivity.this.getActivity(), TaskDetailActivity.class);
						intent.putExtra("task_status", Constant.TASK_UNDER_WAY_DETAIL);
						intent.putExtra("orderid", datas.get(position).getTask_id());
						startActivity(intent);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		lv_task_list = (DropDownListView) view.findViewById(R.id.lv_task_list);
		lv_task_list.setPullLoadEnable(true);
		lv_task_list.setPullRefreshEnable(true);
		lv_task_list.setXListViewListener(this);
	}

	public LoadTextNetTask GetTaskList(String beginTime, String endTime,
			int pageNumber) {

		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {
				mTaskAssigned = null;
				if (task_pageNumber == 1) {
					datas.clear();
				}
				lv_task_list.setRefreshTime(CommandTools.getTime());
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(
								mresult.m_strContent);
						int success = jsonObj.getInt("success");
						if (success == 0) {

							JSONObject dataobject = jsonObj
									.getJSONObject("data");
							pageCount = dataobject.optInt("totalPageCount");
							if (task_pageNumber >= pageCount) {
								lv_task_list.setLoadHide();
							}
							JSONArray jsonArray = dataobject
									.getJSONArray("responseDto");

							for (int i = 0; i < jsonArray.length(); i++) {
								info = new TaskInfo();
								jsonObj = jsonArray.optJSONObject(i);

								SimpleDateFormat OldDateFormat = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss");
								SimpleDateFormat NewFormat = new SimpleDateFormat(
										"yyyy-MM-dd");

								String newDate = "";
								try {
									newDate = NewFormat.format(OldDateFormat
											.parse(jsonObj
													.optString("deadline")));
								} catch (java.text.ParseException e) {
									e.printStackTrace();
								}

								info.setTask_id(jsonObj.optLong("orderId") + "");
								info.setTask_title(jsonObj.optString("theme"));
								info.setTask_people_sex(jsonObj
										.optString("sex"));
								info.setTask_require(jsonObj.optString("claim"));
								info.setTask_money(jsonObj.optInt("reward"));
								info.setTask_status(jsonObj.optInt("status"));
								info.setTask_time(newDate);
								info.setTask_logo(jsonObj
										.optString("coverImage"));
								datas.add(info);
							}
							adapter.notifyDataSetChanged();
							Log.i("zdkj", "任务个数===" + jsonArray.length());
							Log.i("zdkj", "pagenumber===" + task_pageNumber);
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
						Util.showJsonParseErrorMessage(TaskUnderWayActivity.this
								.getActivity());
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(
							TaskUnderWayActivity.this.getActivity(),
							result.m_nResultCode);
				}
			}
		};

		LoadTextNetTask task = TaskService.getUnderWayTask(beginTime, endTime,
				pageNumber, listener, null);
		return task;
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
		mTaskAssigned = GetTaskList(CommandTools.getChangeDate(-7)
				+ " 00:00:00", CommandTools.getChangeDate(0) + " 23:59:59",
				task_pageNumber);
		lv_task_list.stopRefresh();
		lv_task_list.stopLoadMore();

	}
}
