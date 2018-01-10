package com.zhiduan.crowdclient.menuorder;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.CaptureActivity;
import com.zhiduan.crowdclient.adapter.AbnormalAdapter;
import com.zhiduan.crowdclient.data.AbnormalInfo;
import com.zhiduan.crowdclient.util.BottomCallBackInterface;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtil.RefreshCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.util.UMengUtil;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;

import de.greenrobot.event.EventBus;

/** 
 * 异常件
 * 
 * @author yxx
 *
 * @date 2016-5-25 下午1:09:17
 * 
 */
public class AbnormalFragment extends Fragment implements IXListViewListener, OnClickListener, BottomCallBackInterface{

	private Context mContext;
	private DropDownListView listView;
	private AbnormalAdapter mAdapter;

	private List<AbnormalInfo> dataList = new ArrayList<AbnormalInfo>();//所有数据
	private List<AbnormalInfo> sortList = new ArrayList<AbnormalInfo>();//筛选后的数据

	private final int CAPTURE_QRCODE = 0x0021;
	private int pageNumber = 1;//当前页

	private EditText edtSearch;
	private EventBus mEventBus;
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		view = inflater.inflate(R.layout.activity_abnormal, container, false);

		findViewById();
		return view;
	}

	private void findViewById(){

		if(mEventBus == null){
			mEventBus = EventBus.getDefault();
			mEventBus.register(this);
		}

		mContext = AbnormalFragment.this.getActivity().getParent();

		edtSearch = ((EditText) view.findViewById(R.id.edt_order_search_billcode));
		view.findViewById(R.id.layout_order_search_scan).setOnClickListener(this);

		listView = (DropDownListView) view.findViewById(R.id.lv_public_dropdown);
		mAdapter = new AbnormalAdapter(mContext, sortList, new OnBottomClickListener() {

			@Override
			public void onBottomClick(View v, int position) {

				AbnormalInfo info = sortList.get(position);

				if(v.getId() == R.id.item_order_abnormal_back){

					Intent intent = new Intent(mContext, AbnormalDealActivity.class);
					intent.putExtra("abnormaltype","return");
					intent.putExtra("problemId", info.getProblemId());
					intent.putExtra("waybillNo", info.getWaybillNo());
					intent.putExtra("expressName", info.getEcName());
					intent.putExtra("imgUrl", info.getImgUrl());
					intent.putExtra("thumbnailUrl", info.getThumbnailUrl());
					intent.putExtra("problemTypeCode", info.getProblemTypeCode());
					intent.putExtra("problemReason", info.getProblemReason());
					intent.putExtra("orderType", info.getType());
					startActivity(intent);
				}else if(v.getId() == R.id.item_order_abnormal_edit){

					Intent intent = new Intent(mContext, AbnormalDealActivity.class);
					intent.putExtra("abnormaltype", "edit");
					intent.putExtra("problemId", info.getProblemId());
					intent.putExtra("waybillNo", info.getWaybillNo());
					intent.putExtra("expressName", info.getEcName());
					intent.putExtra("imgUrl", info.getImgUrl());
					intent.putExtra("thumbnailUrl", info.getThumbnailUrl());
					intent.putExtra("problemTypeCode", info.getProblemTypeCode());
					intent.putExtra("problemReason", info.getProblemReason());
					intent.putExtra("orderType", info.getType());
					startActivity(intent);
				}else if(v.getId() == R.id.item_order_abnormal_cancel){
					cancel_abnormal(info.getProblemId(), info.getType());
				}else if(v.getId() == R.id.item_order_abnormal_layout_detail){

					Intent intent = new Intent();

					if(info.getType().equals(OrderUtil.PACKET)){
						intent = new Intent(mContext, AbnormalDetailActivity.class);
					}else{
						//						intent = new Intent(mContext, RunningAbnormalDetailActivity.class);
					}

					intent.putExtra("status", info.getState());
					intent.putExtra("orderId", info.getOrderId());
					intent.putExtra("imgUrl", info.getImgUrl());
					intent.putExtra("thumbnailUrl", info.getThumbnailUrl());
					intent.putExtra("orderType", info.getType());
					intent.putExtra("orderId", info.getOrderId());
					intent.putExtra("orderType", info.getType());
					intent.putExtra("orderState", "4");
					startActivity(intent);
				}
			}
		});

		listView.setAdapter(mAdapter);

		listView.setDivider(new ColorDrawable(Color.TRANSPARENT)); 
		listView.setDividerHeight(0);
		listView.setPullRefreshEnable(true);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);

		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
//				OrderUtil.checkDataList_2(arg0.toString(), sortList, dataList, mAdapter);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	public void onResume(){
		super.onResume();

		mContext = AbnormalFragment.this.getActivity().getParent();

		onRefresh();
	}

	public void onPause(){
		super.onStart();
		UMengUtil.onResume(mContext, mContext.getClass().getName());
	}

	/**
	 * 取消异常件
	 * @param problemId
	 * @param type
	 */
	private void cancel_abnormal(long problemId, String type) {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("problemId", problemId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl;
		if(type.equals(OrderUtil.PACKET)){
			strUrl = "waybill/problem/cancel.htm";
		}else{
			strUrl = "waybill/problem/agentcancel.htm";
		}

		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {
				CommandTools.showToast(remark);
				if (success==0) {

					Message msg = new Message();//通知刷新订单数量

					msg.what = OrderUtil.REFRESH_DISTRIBUTION_DATA;
					mEventBus.post(msg);

					onRefresh();
				}
			}

		});

	}

	public void onStop(){
		super.onStop();

		if(listView != null){
			listView.stopRefresh();
			listView.stopLoadMore();
		}
	}

	public void onEventMainThread(Message msg) {

		if(msg.what == OrderUtil.REFRESH_ABNORMAL_DATA){

			onRefresh();
		}
	}

	@Override
	public void onRefresh() {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("beginTime", CommandTools.getChangeDate(-7) + " 00:00:00");
			jsonObject.put("endTime", CommandTools.getChangeDate(0) + " 23:59:59");
			jsonObject.put("pageNumber", 1);//当前页
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OrderUtil.queryAbnormalData(mContext, 0, jsonObject, mAdapter, listView, dataList, sortList, new RefreshCallback() {

			@Override
			public void callback(int success, int totalCount, int pageSize) {

				pageNumber = 2;
				if(success != 0){
					return;
				}

				if(dataList.size() >= pageSize){
					listView.setLoadShow();
					listView.setPullLoadEnable(true);
				}

			}
		});
	}

	@Override
	public void onLoadMore() {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("beginTime", CommandTools.getChangeDate(-7) + " 00:00:00");
			jsonObject.put("endTime", CommandTools.getChangeDate(0) + " 23:59:59");
			jsonObject.put("pageNumber", pageNumber);//当前页
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OrderUtil.queryAbnormalData(mContext, 1, jsonObject, mAdapter, listView, dataList, sortList, new RefreshCallback() {

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
	 * 当前界面是否有数据，如果没有则显示图片
	 * @param type
	 */
	private void setLayoutVisible(){

		if(sortList.size() == 0){
			view.findViewById(R.id.layout_no_data).setVisibility(View.VISIBLE);
		}else{
			view.findViewById(R.id.layout_no_data).setVisibility(View.GONE);
		}

	}

	protected void onDestory(){
		super.onDestroy();

		Logs.v("result", "DistributionActivity-------onDestory");
	}

	@Override
	public void onClick(View arg0) {

		if(arg0.getId() == R.id.layout_order_search_scan){

			Intent intent = new Intent();
			intent.setClass(mContext, CaptureActivity.class);
			startActivityForResult(intent, CAPTURE_QRCODE);
		}
	}

	// 回调函数
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(data == null){
			return;
		}

		if(requestCode == CAPTURE_QRCODE){

			String msg = data.getStringExtra("SCAN_RESULT");
			edtSearch.setText(msg);
		}
	}

}
