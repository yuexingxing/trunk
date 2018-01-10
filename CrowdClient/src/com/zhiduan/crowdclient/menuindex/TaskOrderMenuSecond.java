package com.zhiduan.crowdclient.menuindex;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.TaskOrderMenuSecondAdapter;
import com.zhiduan.crowdclient.data.RewardRuleInfo;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtil.TaskCallBack;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;

/** 
 * 奖励规则
 * 
 * @author yxx
 *
 * @date 2016-11-24 下午2:20:58
 * 
 */
public class TaskOrderMenuSecond extends Fragment implements IXListViewListener {

	private Context mContext;

	private DropDownListView listView;
	private LinearLayout no_order_layout;
	private TaskOrderMenuSecondAdapter mAdapter;
	private List<RewardRuleInfo> dataList = new ArrayList<RewardRuleInfo>();//所有数据
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		view = inflater.inflate(R.layout.activity_task_order_menu_second, container, false);

		findViewById();
		return view;
	}

	private void findViewById() {
		mContext = TaskOrderMenuSecond.this.getActivity();
		no_order_layout = (LinearLayout) view.findViewById(R.id.layout_no_data);
		listView = (DropDownListView) view.findViewById(R.id.lv_public_dropdown);
		mAdapter = new TaskOrderMenuSecondAdapter(mContext, dataList);

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

		OrderUtil.getRewardRule(mContext, mAdapter, listView, dataList, no_order_layout, new TaskCallBack() {

			@Override
			public void callback(int success, int dataSize, int pageSize) {
				// TODO Auto-generated method stub
				if(success != 0){
					return;
				}

			}
		});

	}

	@Override
	public void onLoadMore() {

	}
}
