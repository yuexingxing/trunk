package com.zhiduan.crowdclient.menuorder;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.WaitTakingAdapter;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.menuorder.task.TaskDetailWaitTakingActivity;
import com.zhiduan.crowdclient.util.BottomCallBackInterface;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtil.DataCallback;
import com.zhiduan.crowdclient.util.OrderUtil.RefreshCallback;
import com.zhiduan.crowdclient.util.OrderUtilTools;
import com.zhiduan.crowdclient.view.ComplainDialog;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.ComplainDialog.ComplainCallback;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;
import com.zhiduan.crowdclient.view.dialog.DialogUtils.DialogCallback;

import de.greenrobot.event.EventBus;

/** 
 * 待取件
 * 
 * @author yxx
 *
 * @date 2016-5-24 下午5:27:11
 * 
 */
public class FragmentWaitTaking extends Fragment implements IXListViewListener, OnClickListener, BottomCallBackInterface{

	private Context mContext;
	private DropDownListView listView;
	private WaitTakingAdapter mAdapter;
	private List<OrderInfo> dataList = new ArrayList<OrderInfo>();//所有数据
	private List<OrderInfo> sortList = new ArrayList<OrderInfo>();//筛选后的数据

	private CheckBox checkBox;

	private View view;

	private int pageNumber = 1;//当前页

	private EventBus mEventBus;
	private TextView tvNoData;
	private ImageView imgNoData;
	private LinearLayout layoutNoData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		view = inflater.inflate(R.layout.activity_wait_taking, container, false);

		findViewById();
		return view;
	}

	public void onResume(){
		super.onResume();
		
		if (null != mAdapter && sortList.size() > 0) {
			mAdapter.startRefreshTime();
		}
	}

	private void findViewById(){

		if(mEventBus == null){
			mEventBus = EventBus.getDefault();
			mEventBus.register(this);
		}

		if(OrderUtil.FROM_SEARCH == 0){
			mContext = FragmentWaitTaking.this.getActivity().getParent();
		}else{
			mContext = SearchWaitingActivity.getActivity();
		}

		checkBox = (CheckBox) view.findViewById(R.id.checkBox_selall);
		listView = (DropDownListView) view.findViewById(R.id.lv_public_dropdown);

		tvNoData = (TextView) view.findViewById(R.id.tv_no_data);
		imgNoData = (ImageView) view.findViewById(R.id.img_no_data);
		layoutNoData = (LinearLayout) view.findViewById(R.id.layout_no_data);

		checkBox.setOnClickListener(this);

		view.findViewById(R.id.btn_order_sure_get).setOnClickListener(this);
		view.findViewById(R.id.layout_order_bottom_select).setOnClickListener(this);

		mAdapter = new WaitTakingAdapter(mContext, sortList, new OnBottomClickListener() {

			@Override
			public void onBottomClick(View v, int position) {

				Intent intent = new Intent();
				OrderInfo info = sortList.get(position);
				if(v.getId() == R.id.item_order_wait_layout_check){

					if(info.getChecked().equals("1")){
						info.setChecked("0");
					}else{
						info.setChecked("1");
					}

					checkSingleChoice();
				}else if(v.getId() == R.id.item_order_wait_layout_detail){

//					if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_PACKET)){
						intent = new Intent(mContext, OrderDetailWaitActivity.class);
//					}else if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_AGENT)){
//						intent = new Intent(mContext, RunningDetailWaitActivity.class);
//					}else if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_RUN)){
//						intent = new Intent(mContext, RunningDetailWaitActivity.class);
//					}else if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_ZIYING)){
//						intent = new Intent(mContext, RunningDetailWaitActivity.class);
//					}else if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_COMMON)){
//						intent = new Intent(mContext, TaskDetailWaitTakingActivity.class);
//					}else{
//						intent = new Intent(mContext, RunningDetailWaitActivity.class);
//					}

					intent.putExtra("orderType", info.getCategoryId());
					intent.putExtra("takerId", info.getTakerId());
					startActivity(intent);
				}else if(v.getId() == R.id.item_order_wait_complain){

					onComplain(position, info.getTakerId());
				}else if(v.getId() == R.id.include_order_wati_2_revoke){

					backComplain(position, info.getTakerId());
				}else if(v.getId() == R.id.item_order_wait_trans){

					JSONArray jsonArray = new JSONArray();
					jsonArray.put(info.getTakerId());
					OrderUtil.transStart(mContext, jsonArray);
				}else if(v.getId() == R.id.item_order_wait_handle){

					JSONObject jsonObject = new JSONObject();
					try {

						JSONArray jsonArray = new JSONArray();
						jsonArray.put(info.getTakerId());
						jsonObject.put("takerIdList", jsonArray);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					OrderUtil.sureGetGoods(mContext, jsonObject, mEventBus, null);
				}
			}
		});

		listView.setAdapter(mAdapter);
		listView.setDividerHeight(0);
		//		listView.setDivider(new ColorDrawable(Res.getColor(R.color.gray_background)));
		listView.setPullRefreshEnable(true);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);

		onRefresh();
	}

	public void onEventMainThread(Message msg) {

		if(msg.what == OrderUtil.SEARCH_DATA_BILLCODE){
			onRefresh();
		}else if(msg.what == OrderUtil.ORDER_MENU_DATA_SORT){
			onRefresh();
		}else if(msg.what == OrderUtil.REFRESH_WAIT_DATA){
			onRefresh();
		}else if(msg.what == OrderUtil.NO_NET_CONNECTION){
			OrderUtilTools.setNoNetChanged(listView, dataList, sortList, mAdapter, tvNoData, imgNoData, layoutNoData);
		}
	}

	public void onStart(){
		super.onStart();

	}

	public void onStop(){
		super.onStop();

		if(listView != null){
			listView.stopRefresh();
			listView.stopLoadMore();
		}
	}

	/**
	 * 全选、反选
	 * @param v
	 */
	public void selectAll(View v){

		boolean flag = checkBox.isChecked();

		int len = sortList.size();
		if(len == 0){
			checkBox.setSelected(false);
			return;
		}

		for(int i=0; i<len; i++){

			OrderInfo info = sortList.get(i);
			if(flag == true){
				info.setChecked("1");
			}else{
				info.setChecked("0");
			}
		}

		checkBox.setSelected(!flag);

		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 取件
	 * 多选更新状态
	 * 接口名称 更新为派送中接口
	 * 请求类型 post
	 * 请求Url  /order/packet/updatedeliverystatus.htm
	 * 接口描述 更新为派送中接口
	 * */
	public void sure(View v){

		int len = sortList.size();

		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		for(int i=0; i<len; i++){

			OrderInfo info = sortList.get(i);

			if(info.getChecked().equals("1")){

				jsonArray.put(info.getTakerId());
			}
		}

		if(jsonArray.length() == 0){
			CommandTools.showToast("请至少选择一条订单");
			return;
		}

		try {
			jsonObject.put("takerIdList", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OrderUtil.sureGetGoods(mContext, jsonObject,mEventBus, null);
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

			OrderInfo info = sortList.get(i);

			if(info.getLockState() == 3){
				++lockCount;
				continue;
			}

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

		mAdapter.notifyDataSetChanged();
	}
	
	public void testData(){
		
		sortList.clear();
		for(int i=0; i<5; i++){
			
			OrderInfo info = new OrderInfo();
			
			sortList.add(info);
		}
		
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onRefresh() {
		
		testData();

//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject.put("pageNumber", 1);//当前页
//			jsonObject.put("tabCode", OrderUtil.PENDING);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		OrderUtil.refreshData(mContext, jsonObject, mAdapter, listView, dataList, sortList, new RefreshCallback() {
//
//			@Override
//			public void callback(int success, int totalCount, int pageSize) {
//
//				checkBox.setChecked(false);
//				checkSingleChoice();
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
//			}
//		});

	}

	@Override
	public void onLoadMore() {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("pageNumber", pageNumber);//当前页
			jsonObject.put("tabCode", OrderUtil.PENDING);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OrderUtil.loadMoreData(mContext, mAdapter, listView, dataList, sortList, jsonObject, new RefreshCallback() {

			@Override
			public void callback(int success, int totalCount, int pageSize) {

				if(success != 0){
					listView.setLoadFinished();
					return;
				}

				++pageNumber;
			}
		});
	}

	/**
	 * 订单申述
	 */
	public void onComplain(final int position, String takerId){

		ComplainDialog.showDialog(mContext, takerId, new ComplainCallback(){

			@Override
			public void callback(JSONObject jsonObject) {

				OrderUtil.complainSubmit(mContext, jsonObject, new DataCallback() {

					@Override
					public void callback(int success, String remark, JSONObject jsonObject) {

						if(success == 0){

							sortList.get(position).setLockState(2);
							dataList.get(position).setLockState(2);
							mAdapter.notifyDataSetChanged();
						}
					}
				});
			}
		});
	}

	/**
	 * 撤销申诉
	 */
	public void backComplain(final int position, final String takerId){

		DialogUtils.showTwoButtonDialog(mContext, GeneralDialog.DIALOG_ICON_TYPE_5, "", "确定要撤回申述？", "取消", "确定", new DialogCallback() {

			@Override
			public void callback(int pos) {
				// TODO Auto-generated method stub
				if(pos == 0){

					OrderUtil.complainRevoke(mContext, takerId, new DataCallback() {

						@Override
						public void callback(int success, String remark, JSONObject jsonObject) {

							sortList.get(position).setLockState(OrderUtil.LOCK_STATE_0);
							dataList.get(position).setLockState(OrderUtil.LOCK_STATE_0);
							mAdapter.notifyDataSetChanged();
						}
					});
				}
			}
		});

	}

	/**
	 * 转单
	 * @param v
	 */
	public void transBill(View v){

		JSONArray jsonArray = new JSONArray();
		int len = sortList.size();

		for(int i=0; i<len; i++){

			OrderInfo info = sortList.get(i);
			if(info.getChecked().equals("1")){
				jsonArray.put(info.getOrderNo());
			}
		}

		OrderUtil.transStart(mContext, jsonArray);
	}

	protected void onDestory(){
		super.onDestroy();

	}

	@Override
	public void onClick(View arg0) {

		if(arg0.getId() == R.id.btn_order_sure_get){
			sure(null);
		}else if(arg0.getId() == R.id.checkBox_selall){
			selectAll(null);
		}
	}

}
