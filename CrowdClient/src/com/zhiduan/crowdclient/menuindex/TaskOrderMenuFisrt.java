package com.zhiduan.crowdclient.menuindex;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.activity.MainActivity;
import com.zhiduan.crowdclient.adapter.TaskOrderMenuFirstAdapter;
import com.zhiduan.crowdclient.data.TaskOrderInfo;
import com.zhiduan.crowdclient.service.OrderService;
import com.zhiduan.crowdclient.task.TaskDetailActivity;
import com.zhiduan.crowdclient.util.BottomCallBackInterface.OnBottomClickListener;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.GlobalInstanceStateHelper;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.util.OrderUtil.TaskCallBack;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;

/** 
 * 悬赏任务----任务订单
 * 
 * @author yxx
 *
 * @date 2016-11-24 下午2:20:58
 * 
 */
public class TaskOrderMenuFisrt extends Fragment implements IXListViewListener {

	private Context mContext;

	private DropDownListView listView;
	private LinearLayout no_order_layout;
	private TaskOrderMenuFirstAdapter mAdapter;
	private List<TaskOrderInfo> dataList = new ArrayList<TaskOrderInfo>();//所有数据
	private View view;
	private int pageNumber = 1;//当前页

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		view = inflater.inflate(R.layout.activity_task_menu_fisrt, container, false);

		findViewById();
		return view;
	}

	private void findViewById() {
		mContext = TaskOrderMenuFisrt.this.getActivity();

		no_order_layout = (LinearLayout) view.findViewById(R.id.layout_no_data);
		listView = (DropDownListView) view.findViewById(R.id.lv_public_dropdown);
		mAdapter = new TaskOrderMenuFirstAdapter(mContext, dataList, new OnBottomClickListener() {

			@Override
			public void onBottomClick(View v, int position) {
				// TODO Auto-generated method stub

				TaskOrderInfo info = dataList.get(position);

				if(v.getId() == R.id.item_task_order_first_layout_detail){//任务详情

					Intent intent = new Intent(mContext, TaskDetailActivity.class);
					intent.putExtra("task_status", Constant.TASK_MAIN_DETAIL);
					intent.putExtra("orderid", info.getOrderId());
					startActivity(intent);
				}else if(v.getId() == R.id.item_task_order_first_grab){//接任务

					grabTask(info.getOrderId(), info.getType(), position);
				}
			}
		});

		listView.setAdapter(mAdapter);
		listView.setDivider(new ColorDrawable(Color.TRANSPARENT)); 
		listView.setDividerHeight(0);
		listView.setPullRefreshEnable(true);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);

		onRefresh();
	}

	public void onResume(){
		super.onResume();

		listView.stopRefresh();
		listView.stopLoadMore();
	}

	@Override
	public void onRefresh() {

		OrderUtil.getTaskData(mContext, OrderUtil.REFRESH_DATA, mAdapter, listView, dataList, 1, no_order_layout, new TaskCallBack() {

			@Override
			public void callback(int success, int dataSize, int pageSize) {
				// TODO Auto-generated method stub
				if(success != 0){
					return;
				}

				if(dataSize == pageSize && dataSize != 0){
					pageNumber = 2;
					listView.setPullLoadEnable(true);
				}
			}
		});

	}

	@Override
	public void onLoadMore() {
		OrderUtil.getTaskData(mContext, OrderUtil.LOADMORE_DATA, mAdapter, listView, dataList, pageNumber, no_order_layout, new TaskCallBack() {
			@Override
			public void callback(int success, int dataSize, int pageSize) {
				// TODO Auto-generated method stub
				if(success != 0){
					return;
				}

				++pageNumber;
			}
		});
	}

	/**
	 * 接任务
	 * 接口名称 抢单池点击抢单接口
	 * 请求类型 post
	 * 负  责  人 王宇
	 * 版        本 1105
	 * 状        态 测试通过
	 * 请求  Url  /grabpool/graborder.htm
	 */
	private void grabTask(String orderId, String type, final int postion){

		if(CommandTools.checkUserStatus(mContext) == false){
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("orderId", orderId);
			jsonObject.put("type", type);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String strUrl = "grabpool/graborder.htm";
		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {
				// TODO Auto-generated method stub

				onRefresh();
				
				CommandTools.showToast(remark);
				if(success != 0){
					return;
				}
				
				// 接单成功后设置未读消息数量
				SharedPreferences sp = mContext.getSharedPreferences(Constant.SAVE_NOT_ORDER_NUMBER, Context.MODE_PRIVATE);
				int number = sp.getInt("OrderNumber", 0);
				Util.saveNotNumber(mContext, number + 1, Util.order_type);
				MainActivity.mNotOrderNum.setText(number + 1 + "");
				MainActivity.mNotOrderNum.setVisibility(View.VISIBLE);
			}
		});
	}
}
