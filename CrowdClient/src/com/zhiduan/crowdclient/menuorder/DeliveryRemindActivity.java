package com.zhiduan.crowdclient.menuorder;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.adapter.DeliveryRemindAdapter;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.data.TransOrderInfo;
import com.zhiduan.crowdclient.util.BottomCallBackInterface;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtilTools;
import com.zhiduan.crowdclient.util.OrderUtil.DataCallback;
import com.zhiduan.crowdclient.util.OrderUtil.RefreshCallback;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;

/** 
 * 转单列表
 * 
 * @author yxx
 *
 * @date 2017-1-9 下午1:40:59
 * 
 */
public class DeliveryRemindActivity extends BaseActivity implements IXListViewListener, BottomCallBackInterface {

	private DeliveryRemindAdapter mAdapter;
	private List<TransOrderInfo> dataList = new ArrayList<TransOrderInfo>();
	private List<TransOrderInfo> sortList = new ArrayList<TransOrderInfo>();

	@ViewInject(R.id.remind_count_text) TextView tvCount;
	@ViewInject(R.id.remind_check_all) CheckBox checkBox;
	@ViewInject(R.id.lv_public_dropdown) DropDownListView listView;

	@ViewInject(R.id.tv_no_data) TextView tvNoData;
	@ViewInject(R.id.img_no_data) ImageView imgNoData;
	@ViewInject(R.id.layout_no_data) LinearLayout layoutNoData;

	private int pageNumber = 1;//当前页

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {

		setContentViewId(R.layout.activity_delivery_remind, this);
		ViewUtils.inject(this);
	}

	@Override
	public void initView() {

	}

	@Override
	public void initData() {

		Util.removeDelivery(DeliveryRemindActivity.this);

		setTitle("转单列表");

		mAdapter = new DeliveryRemindAdapter(this, sortList, new OnBottomClickListener() {

			@Override
			public void onBottomClick(View v, int position) {
				// TODO Auto-generated method stub

				TransOrderInfo info = sortList.get(position);
				if(v.getId() == R.id.item_remind_timecount){
					onRefresh();
				}else if(v.getId() == R.id.item_remind_layout_check){

					if(info.getChecked().equals("1")){
						info.setChecked("0");
					}else{
						info.setChecked("1");
					}

					checkSingleChoice();
				}else if(v.getId() == R.id.item_remind_refuse){

					try {
						JSONArray jsonArray = new JSONArray();
						jsonArray.put(info.getAssignId());
						startTrans(position, new JSONObject().put("assignId",  jsonArray), OrderUtil.assign_refuse);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else if(v.getId() == R.id.item_remind_ok){

					try {
						JSONArray jsonArray = new JSONArray();
						jsonArray.put(info.getAssignId());
						startTrans(position, new JSONObject().put("assignId", jsonArray), OrderUtil.assign_agree);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else if(v.getId() == R.id.item_remind_layout_detail){

					Intent intent = new Intent(DeliveryRemindActivity.this, DeliveryRemindDetailActivity.class);
					intent.putExtra("orderType", "123");
					intent.putExtra("assignId", 1000);
					startActivity(intent);
				}else if(v.getId() == R.id.item_remind_timecount){

					dataList.clear();
					sortList.clear();
					mAdapter.notifyDataSetChanged();

					onRefresh();
				}
			}
		});

		listView.setAdapter(mAdapter);

		listView.setDividerHeight(0);
		//		listView.setDivider(new ColorDrawable(Res.getColor(R.color.gray_background)));
		listView.setPullRefreshEnable(true);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (null != mAdapter && sortList.size() > 0) {
			mAdapter.startRefreshTime();
		}

		onRefresh();
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	/**
	 * 判断单选状态
	 * 排除转单中订单
	 * @param info
	 */
	private void checkSingleChoice(){

		int checkCount = 0;
		int notCheckCount = 0;

		int lockCount = 0;
		int len = sortList.size();
		for(int i=0; i<len; i++){

			TransOrderInfo info = sortList.get(i);

			if(info.getChecked().equals("1")){
				checkCount++;
			}else{
				notCheckCount++;
			}
		}

		if(notCheckCount > 0){
			checkBox.setChecked(false);
		}

		if(lockCount + checkCount == sortList.size() && sortList.size() != 0){
			checkBox.setChecked(true);
		}

		tvCount.setText(checkCount + "");
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 全选、反选
	 * @param v
	 */
	public void selectAll(View v){

		boolean flag = checkBox.isChecked();

		int len = sortList.size();
		if(len == 0){
			checkBox.setChecked(false);
			return;
		}

		int count = 0;
		for(int i=0; i<len; i++){

			TransOrderInfo info = sortList.get(i);
			if(flag == true){
				count++;
				info.setChecked("1");
			}else{
				info.setChecked("0");
			}
		}

		tvCount.setText(count + "");
		checkBox.setSelected(!flag);

		mAdapter.notifyDataSetChanged();
	}

	public void testData(){

		sortList.clear();
		for(int i=0; i<5; i++){

			TransOrderInfo info = new TransOrderInfo();

			sortList.add(info);
		}

		mAdapter.notifyDataSetChanged();
	}


	@Override
	public void onRefresh() {

		testData();

		//		JSONObject jsonObject = new JSONObject();
		//		try {
		//			jsonObject.put("pageNumber", 1);
		//		} catch (JSONException e) {
		//			e.printStackTrace();
		//		}
		//
		//		OrderUtil.refreshTransList(mContext, jsonObject, mAdapter, listView, dataList, sortList, new RefreshCallback() {
		//
		//			@Override
		//			public void callback(int success, int totalCount, int pageSize) {
		//
		//				OrderUtilTools.setNoData(sortList.size(), 1, tvNoData, imgNoData, layoutNoData);
		//
		//				if(success != 0){
		//					return;
		//				}
		//
		//				if(sortList.size() >= pageSize){
		//					pageNumber = 2;
		//					listView.setLoadShow();
		//					listView.setPullLoadEnable(true);
		//				}
		//
		//				mAdapter.notifyDataSetChanged();
		//			}
		//		});
	}

	@Override
	public void onLoadMore() {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("pageNumber", pageNumber);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OrderUtil.loadMoreTransList(mContext, jsonObject, mAdapter, listView, dataList, sortList, new RefreshCallback() {

			@Override
			public void callback(int success, int totalCount, int pageSize) {

				if(success != 0){
					return;
				}

				++pageNumber;
			}
		});
	}

	/**
	 * 拒绝
	 * @param v
	 */
	public void refuse(View v){

		JSONObject jsonObject = new JSONObject();
		jsonObject = checkHandleData();
		if(jsonObject == null){
			return;
		}

		startTrans(-1, jsonObject, OrderUtil.assign_refuse);
	}

	/**
	 * 同意
	 * @param v
	 */
	public void agree(View v){

		JSONObject jsonObject = new JSONObject();
		jsonObject = checkHandleData();
		if(jsonObject == null){
			return;
		}

		startTrans(-1, jsonObject, OrderUtil.assign_agree);
	}

	/**
	 * 开始转单的接收和拒绝处理
	 * @param postion -1为批量操作，其他为单个操作执行成功后直接移除即可
	 * @param jsonObject
	 * @param url
	 */
	public void startTrans(final int postion, JSONObject jsonObject, String url){

		OrderUtil.transRefuseOrReject(mContext, url, jsonObject, new DataCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				if(success != 0){
					return;
				}

				if(postion != -1){
					dataList.remove(postion);
					sortList.remove(postion);
					mAdapter.notifyDataSetChanged();
				}else{
					onRefresh();
				}

				checkBox.setChecked(false);
				tvCount.setText("0");

				Message msg = new Message();
				msg.what = OrderUtil.UPDATE_ASSIGN_NUMBER;
				mEventBus.post(msg);
			}
		});
	}


	/**
	 * 筛选要处理的数据
	 * @return
	 */
	private JSONObject checkHandleData(){

		JSONArray jsonArray = new JSONArray();

		int len = sortList.size();
		for(int i=0; i<len; i++){

			TransOrderInfo info = sortList.get(i);
			if(info.getChecked().equals("1")){
				jsonArray.put(info.getAssignId());
			}
		}

		if(jsonArray.length() == 0){
			CommandTools.showToast("请至少选择一条数据");
			return null;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("assignId", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public void onEventMainThread(Message msg) {

		if(msg.what == OrderUtil.NO_NET_CONNECTION){
			OrderUtilTools.setNoNetChangedByDelivery(listView, dataList, sortList, mAdapter, tvNoData, imgNoData, layoutNoData);
		}
	}
}
