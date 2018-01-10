package com.zhiduan.crowdclient.menuorder;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.SignedAdapter;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.menuorder.task.TaskDetailSignedActivity;
import com.zhiduan.crowdclient.menuorder.task.ThoughtActivity;
import com.zhiduan.crowdclient.util.BottomCallBackInterface;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtilTools;
import com.zhiduan.crowdclient.util.OrderUtil.DataCallback;
import com.zhiduan.crowdclient.util.OrderUtil.RefreshCallback;
import com.zhiduan.crowdclient.view.ComplainDialog;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.ComplainDialog.ComplainCallback;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;
import com.zhiduan.crowdclient.view.dialog.DialogUtils.DialogCallback;
import de.greenrobot.event.EventBus;
import android.os.Bundle;
import android.os.Message;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * 已签收
 * 
 * @author yxx
 *
 * @date 2016-5-25 下午3:50:11
 * 
 */
public class FragmentSigned extends Fragment  implements IXListViewListener, BottomCallBackInterface{

	private Context mContext;

	private DropDownListView listView;
	private SignedAdapter mAdapter;

	private List<OrderInfo> dataList = new ArrayList<OrderInfo>();//所有数据
	private List<OrderInfo> sortList = new ArrayList<OrderInfo>();//筛选后的数据

	private int pageNumber = 1;//当前页

	private EventBus mEventBus;
	private TextView tvNoData;
	private ImageView imgNoData;
	private LinearLayout layoutNoData;

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		view = inflater.inflate(R.layout.activity_signed, container, false);

		findViewById();
		return view;
	}

	public void onResume(){
		super.onResume();

	}

	private void findViewById(){

		if(mEventBus == null){
			mEventBus = EventBus.getDefault();
			mEventBus.register(this);
		}

		if(OrderUtil.FROM_SEARCH == 0){
			mContext = FragmentSigned.this.getActivity().getParent();
		}else{
			mContext = SearchWaitingActivity.getActivity();
		}

		tvNoData = (TextView) view.findViewById(R.id.tv_no_data);
		imgNoData = (ImageView) view.findViewById(R.id.img_no_data);
		layoutNoData = (LinearLayout) view.findViewById(R.id.layout_no_data);

		listView = (DropDownListView) view.findViewById(R.id.lv_public_dropdown);
		mAdapter = new SignedAdapter(mContext, sortList, new OnBottomClickListener() {

			@Override
			public void onBottomClick(View v, int position) {

				Intent intent = null;
				OrderInfo info = sortList.get(position);
				if(v.getId() == R.id.item_order_sign_layout_detail){

//					if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_PACKET)){
						intent = new Intent(mContext, OrderDetailSignedActivity.class);
//					}else if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_AGENT)){
//						intent = new Intent(mContext, RunningDetailSignedActivity.class);
//					}else if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_RUN)){
//						intent = new Intent(mContext, RunningDetailSignedActivity.class);
//					}else if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_ZIYING)){
//						intent = new Intent(mContext, RunningDetailSignedActivity.class);
//					}else if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_COMMON)){
//						intent = new Intent(mContext, TaskDetailSignedActivity.class);
//					}else{
//						intent = new Intent(mContext, RunningDetailSignedActivity.class);
//					}

					intent.putExtra("orderType", info.getCategoryId());
					intent.putExtra("takerId", info.getTakerId());
					startActivity(intent);
				}else if(v.getId() == R.id.item_order_signed_complain){
					onComplain(position, info.getTakerId());
				}else if(v.getId() == R.id.include_order_wati_2_revoke){
					backComplain(position, info.getTakerId());
				}else if(v.getId() == R.id.item_order_signed_thought){
					intent = new Intent(mContext, ThoughtActivity.class);
					intent.putExtra("takerId", info.getTakerId());
					startActivity(intent);
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

	public void onStart(){
		super.onStart();

	}

	public void onEventMainThread(Message msg) {

		if(msg.what == OrderUtil.SEARCH_DATA_BILLCODE){
			onRefresh();
		}else if(msg.what == OrderUtil.ORDER_MENU_DATA_SORT){
			onRefresh();
		}else if(msg.what == OrderUtil.REFRESH_SIGN_DATA){
			onRefresh();
		}else if(msg.what == OrderUtil.NO_NET_CONNECTION){
			OrderUtilTools.setNoNetChanged(listView, dataList, sortList, mAdapter, tvNoData, imgNoData, layoutNoData);
		}
	}

	public void onStop(){
		super.onStop();

		if(listView != null){
			listView.stopRefresh();
			listView.stopLoadMore();
		}
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
		//			jsonObject.put("tabCode", OrderUtil.COMPLETED);
		//		} catch (JSONException e) {
		//			e.printStackTrace();
		//		}
		//
		//		OrderUtil.refreshData(mContext, jsonObject, mAdapter, listView, dataList, sortList, new RefreshCallback() {
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
		//				}else{
		//					listView.setPullLoadEnable(false);
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
			jsonObject.put("tabCode", OrderUtil.COMPLETED);
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

						CommandTools.showToast(remark);
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


	protected void onDestory(){
		super.onDestroy();

	}

}
