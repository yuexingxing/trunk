package com.zhiduan.crowdclient.menucenter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.adapter.CommonAdapter;
import com.zhiduan.crowdclient.adapter.ViewHolder;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.data.PageInfo;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.OrderUtil.RefreshCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.CalendarWindows;
import com.zhiduan.crowdclient.view.CalendarWindows.CalendarClickListener;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;
import com.zhiduan.crowdclient.view.DropDownListView;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/** 
 * 完成查询
 * 
 * @author yxx
 *
 * @date 2016-5-30 上午10:11:40
 * 
 */
public class SignedQueryActivity extends BaseActivity implements IXListViewListener{

	private Context mContext;

	private DropDownListView listView;
	private CommonAdapter<OrderInfo> mAdapter;
	private List<OrderInfo> dataList = new ArrayList<OrderInfo>();
	private List<OrderInfo> sortList = new ArrayList<OrderInfo>();

	private TextView tvTime;
	private TextView tvCount;

	private int pageNumber = 1;//当前页
	private String searchTime = CommandTools.getTimeDate(-1);

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {

		setContentViewId(R.layout.activity_signed_query, this);
	}

	@Override
	public void initView() {

		mContext = SignedQueryActivity.this;

		tvTime = (TextView) findViewById(R.id.tv_signed_query_time);
		tvCount = (TextView) findViewById(R.id.tv_signed_query_count);

		tvTime.setText(CommandTools.getTimeDate(-1));
		tvCount.setText("0");

		listView = (DropDownListView) findViewById(R.id.lv_public_dropdown);

		listView.setAdapter(mAdapter = new CommonAdapter<OrderInfo>(mContext, dataList, R.layout.item_signed_query) {

			@Override
			public void convert(ViewHolder helper, OrderInfo item) {

				helper.setText(R.id.tv_sign_query_billcode, item.getBillcode());
				helper.setText(R.id.tv_sign_query_address_1, item.getDeliveryAddress());
				helper.setText(R.id.tv_sign_query_name, item.getReceiveName());
				helper.setText(R.id.tv_sign_query_phone, item.getReceivePhone());
				helper.setText(R.id.tv_sign_query_address_2, item.getReceiveAddress());
			}
		});

		listView.setDivider(new ColorDrawable(Color.TRANSPARENT)); 
		listView.setDividerHeight(20);
		listView.setPullRefreshEnable(true);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);

		onRefresh();
	}

	@Override
	public void initData() {

		setTitle("完成详情");
		setRightTitleBackground(R.drawable.profile_calendar);

		dataList.clear();
	}
	

	@Override
	protected void onStop(){
		super.onStop();

		if(listView != null){
			listView.stopRefresh();
			listView.stopLoadMore();
		}
	}

	private void showPopWindow(){

		View v1 = findViewById(R.id.layout_sign_query_top);

		CalendarWindows.showWindows(mContext, v1, new CalendarClickListener() {

			@Override
			public void pitchOnDate(String date) {

				searchTime = date;
				tvTime.setText(date);
				onRefresh();
			}
		});
	}

	@Override
	public void onRefresh() {
		
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("beginTime", searchTime + " 00:00:00");
			jsonObject.put("endTime", searchTime + " 23:59:59");
			jsonObject.put("pageNumber", 1);//当前页
		} catch (JSONException e) {
			e.printStackTrace();
		}

		listView.setRefreshTime(CommandTools.getTime());
		String strUrl = "order/packet/querysigned.htm";
		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) { 
				
				dataList.clear();
				sortList.clear();

				listView.stopRefresh();
				if(success != 0){
					setLayoutVisible();
					CommandTools.showToast(remark);
					mAdapter.notifyDataSetChanged();
					return;
				}

				JSONObject jsonObjectTotal = jsonObject.optJSONObject("data");

				//获取数据总条数、页数等
				JSONObject jsonObjectPage = jsonObjectTotal.optJSONObject("queryResult");
				PageInfo pageInfo = new PageInfo();
				pageInfo.setTotalCount(jsonObjectPage.optInt("totalCount"));
				pageInfo.setTotalPageCount(jsonObjectPage.optInt("totalPageCount"));
				pageInfo.setPageNumber(jsonObjectPage.optInt("pageNumber"));
				pageInfo.setPageSize(jsonObjectPage.optInt("pageSize"));
				pageInfo.setOrderBy(jsonObjectPage.optString("orderBy"));

				JSONArray jsonArray1 = new JSONArray();
				jsonArray1 = jsonObjectTotal.optJSONArray("responseDto");
				for(int i=0; i<jsonArray1.length(); i++){

					jsonObject = jsonArray1.optJSONObject(i);
					OrderInfo info = new OrderInfo();

					info.setTotalCount(pageInfo.getTotalCount());
					info.setTotalPageCount(pageInfo.getTotalPageCount());
					info.setPageNumber(pageInfo.getPageNumber());
					info.setPageSize(pageInfo.getPageSize());
					info.setOrderBy(pageInfo.getOrderBy());

					info.setOrderNo(jsonObject.optString("orderId"));//订单编号
					info.setBillcode(jsonObject.optString("waybillNo"));//运单号
					info.setDeliveryAddress(jsonObject.optString("storeAddress"));//门店地址(取件地址)
					info.setStack(jsonObject.optString("position"));//货位号
					info.setDeliveryRequire(jsonObject.optString("remark"));//说明
					info.setDeliveryFee(jsonObject.optInt("reward", 0));//派件小费
					
					info.setSignTime(jsonObject.optString("showDateTime"));//签收时间
					info.setReceiveName(jsonObject.optString("consignee"));//收件人名称
					info.setReceiveAddress(jsonObject.optString("destination"));//收件人地址
					info.setReceivePhone(jsonObject.optString("phone"));//收件人手机号

					sortList.add(info);
					dataList.add(info);
				}

				pageNumber = 2;;
				tvCount.setText(dataList.size() + "");
				if(dataList.size() >= pageInfo.getPageSize()){
					listView.setPullLoadEnable(true);
				}else{
					listView.setPullLoadEnable(false);
				}

				setLayoutVisible();
				mAdapter.notifyDataSetChanged();
			}
		});

	}

	@Override
	public void onLoadMore() {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("beginTime", searchTime + " 00:00:00");
			jsonObject.put("endTime", searchTime + " 23:59:59");
			jsonObject.put("pageNumber", pageNumber);//当前页
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OrderUtil.loadMoreData(mContext, mAdapter, listView, dataList, sortList, jsonObject, new RefreshCallback() {

			@Override
			public void callback(int success, int totalCount, int pageSize) {

				if(success != 0){
					return;
				}

				++pageNumber;

				setLayoutVisible();
			}
		});
	}

	/**
	 * 文字的点击事件
	 */
	public void rightClick() {

		showPopWindow();
	}

	/**
	 * 当前界面是否有数据，如果没有则显示图片
	 * @param type
	 */
	private void setLayoutVisible(){
		
		tvCount.setText(sortList.size() + "");

		if(sortList.size() == 0){
			findViewById(R.id.layout_no_data).setVisibility(View.VISIBLE);
			listView.setPullLoadEnable(false);
		}else{
			findViewById(R.id.layout_no_data).setVisibility(View.GONE);
		}

	}
}
