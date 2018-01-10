package com.zhiduan.crowdclient.menuorder;

import org.json.JSONException;
import org.json.JSONObject;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.PostOrderInfo;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.GlobalInstanceStateHelper;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.DeliveryOrderSortDialog;
import com.zhiduan.crowdclient.view.DeliveryOrderSortDialog.ResultCallback;
import com.zhiduan.crowdclient.view.PostOrderSortDialog;
import com.zhiduan.crowdclient.view.PostOrderSortDialog.PostResultCallback;
import de.greenrobot.event.EventBus;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.app.ActivityGroup;
import android.content.Intent;

/** 
 * 订单主界面(派件、寄件)
 * 
 * @author yxx
 *
 * @date 2016-8-18 下午5:22:30
 * 
 */
@SuppressWarnings("deprecation")
public class OrderMenuActivity extends ActivityGroup{

	public static OrderMenuActivity orderMenuActivity;
	private TabHost mTabHost = null;

	private LinearLayout layout1;
	private LinearLayout layout2;

	private TextView tvMenu1;
	private TextView tvMenu2;

	private EventBus mEventBus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_menu);

		if (savedInstanceState != null) {
			GlobalInstanceStateHelper.restoreInstanceState(this, savedInstanceState);
		}
	
		findViewById();
	}
	private void findViewById(){

		if(mEventBus == null){
			mEventBus = EventBus.getDefault();
			mEventBus.register(this);
		}

		orderMenuActivity = OrderMenuActivity.this;

		layout1 = (LinearLayout) findViewById(R.id.layout_order_menu_delivery);
		layout2 = (LinearLayout) findViewById(R.id.layout_order_menu_post);

		tvMenu1 = (TextView) findViewById(R.id.tv_order_menu_delivery);
		tvMenu2 = (TextView) findViewById(R.id.tv_order_menu_post);

		mTabHost = (TabHost) findViewById(R.id.tabhost_order_menu);

		mTabHost.setup(this.getLocalActivityManager());

		//派件菜单
		TabSpec ts1 = mTabHost.newTabSpec("delivery_menu").setIndicator("tab1")
				.setContent(new Intent(this, DeliveryOrderMenuActivity.class));
		mTabHost.addTab(ts1);

		//寄件菜单
//		TabSpec ts2 = mTabHost.newTabSpec("post_menu").setIndicator("tab2")
//				.setContent(new Intent(this, PostOrderMenuActivity.class));
//		mTabHost.addTab(ts2);

		postOrder(null);
		deliveryOrder(null);
	}

	protected void onResume(){
		super.onResume();
		MyApplication.baseActivity = this;
		OrderUtil.FROM_SEARCH = 0;
		updatePostOrderMenuCount();
	}

	/**
	 * 派件
	 * @param v
	 */
	public void deliveryOrder(View v){

		layout2.setBackground(getResources().getDrawable(R.drawable.shape_order_menu_right_check));
		layout1.setBackgroundColor(getResources().getColor(R.color.transparent));

		tvMenu2.setTextColor(getResources().getColor(R.color.white));
		tvMenu1.setTextColor(getResources().getColor(R.color.main_color));

		OrderUtil.CURRENT_TYPE = 0;
		mTabHost.setCurrentTab(0);
		setOrderPostMenuCount();
	}

	/**
	 * 寄件
	 * @param v
	 */
	public void postOrder(View v){

		layout1.setBackground(getResources().getDrawable(R.drawable.shape_order_menu_left_check));
		layout2.setBackgroundColor(getResources().getColor(R.color.transparent));

		tvMenu2.setTextColor(getResources().getColor(R.color.main_color));
		tvMenu1.setTextColor(getResources().getColor(R.color.white));

		OrderUtil.CURRENT_TYPE = 1;
		mTabHost.setCurrentTab(1);

		setOrderPostMenuCount();
	}

	/**
	 * 排序对话框派件与寄件分开调用dialog
	 * 按照时间、地址、性别进行升序、降序排列
	 * 按照选择的排序规则
	 * 重新调用接口进行数据刷新
	 * @param v
	 */
	public void orderSort(View view) {

		if(OrderUtil.CURRENT_TYPE == 0){

			final int currentPage = DeliveryOrderMenuActivity.getActivity().CURRENT_PAGE;
			DeliveryOrderSortDialog.showMyDialog(OrderMenuActivity.this, currentPage, new ResultCallback() {

				@Override
				public void callback(int position) {

					Message msg = new Message();

					if(currentPage == 0){
						msg.what = OrderUtil.REFRESH_WAIT_DATA;
					}else if(currentPage == 1){
						msg.what = OrderUtil.REFRESH_DISTRIBUTION_DATA;
					}else if(currentPage == 2){
						msg.what = OrderUtil.REFRESH_SIGN_DATA;
					}else if(currentPage == 3){
						msg.what = OrderUtil.REFRESH_ABNORMAL_DATA;
					}

					mEventBus.post(msg);
				}
			});
		}

	}

	/**
	 * 全局搜索
	 * @param v
	 */
	public void orderSearch(View v){

		OrderUtil.FROM_SEARCH = 1;

		int currentPage = 0;
		if(OrderUtil.CURRENT_TYPE == 0){
			currentPage = DeliveryOrderMenuActivity.getActivity().CURRENT_PAGE;
		}

		Intent intent = new Intent(OrderMenuActivity.this, SearchWaitingActivity.class);
		intent.putExtra("searchType", currentPage);
		startActivity(intent);
	}

	/**
	 * 排序
	 * @param v
	 */
	public void orderType(View v){

		Intent intent = new Intent(OrderMenuActivity.this, FilterOrderActivity.class);
		startActivity(intent);
	}

	public void onEventMainThread(Message msg) {

		if(msg.what == OrderUtil.UPDATE_POST_ORDER_MENU_NUMBER){

			updatePostOrderMenuCount();
		}else if(msg.what == OrderUtil.HIDEN_ORDER_MENU_SORT){

			hidenMenuSort(msg.arg1);
		}

	}

	/**
	 * 隐藏第四栏异常件、退件
	 * @param type  0:隐藏 1：显示
	 */
	private void hidenMenuSort(int type){

		if(type == 0){
			findViewById(R.id.layout_order_menu_sort_0).setVisibility(View.VISIBLE);
			findViewById(R.id.layout_order_menu_sort_3).setVisibility(View.GONE);
		}else{
			findViewById(R.id.layout_order_menu_sort_0).setVisibility(View.GONE);
			findViewById(R.id.layout_order_menu_sort_3).setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置寄件消息数量
	 * 如果当前在派件栏显示数量，否则隐藏
	 * @param n
	 */
	private void setOrderPostMenuCount(){

		((TextView) findViewById(R.id.tv_order_menu_post_count)).setText(OrderUtil.POST_MENU_COUNT + "");

		if(OrderUtil.CURRENT_TYPE == 1){
			((TextView) findViewById(R.id.tv_order_menu_post_count)).setVisibility(View.GONE);
		}else{
			((TextView) findViewById(R.id.tv_order_menu_post_count)).setVisibility(View.GONE);//暂时隐藏掉，不显示
		}

		if(OrderUtil.CURRENT_TYPE == 0){
			if(DeliveryOrderMenuActivity.getActivity().CURRENT_PAGE == 3){
				hidenMenuSort(0);
			}else{
				hidenMenuSort(1);
			}
		}else{
			
		}
	}

	/**
	 * 接口名称 (730)查询待揽件、寄件中、已完成、退件订单数量
	 * 请求类型 post
	 * 请求Url  /order/packet/selectorderscount.htm
	 */
	public void updatePostOrderMenuCount(){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("beginTime", CommandTools.getChangeDate(-7) + " 00:00:00");
			jsonObject.put("endTime", CommandTools.getChangeDate(0) + " 23:59:59");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "order/send/queryordercount.htm";
		RequestUtilNet.postDataToken(OrderMenuActivity.this, strUrl, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				if(success != 0){
					CommandTools.showToast(remark);
					return;
				}

				jsonObject = jsonObject.optJSONObject("data");
				int count1 = jsonObject.optInt("takingCount", 0);//
				int count2 = jsonObject.optInt("deliveryingCount", 0);
				int count3 = jsonObject.optInt("completeCount", 0);
				int count4 = jsonObject.optInt("returnCount", 0);

				PostOrderInfo info = new PostOrderInfo();
				info.setCount1(count1);
				info.setCount2(count2);
				info.setCount3(count3);
				info.setCount4(count4);

				OrderUtil.POST_MENU_COUNT = count1 + count2;

				Message msg = new Message();//设置tab栏订单数量
				msg.what = OrderUtil.SET_POST_ORDER_MENU_NUMBER;
				msg.obj = info;
				mEventBus.post(msg);

				setOrderPostMenuCount();//设置顶部订单消息数量
			}
		});

	}

	public static OrderMenuActivity getActivity(){

		if(orderMenuActivity != null){
			return orderMenuActivity;
		}

		return null;
	}

	protected void onDestory(){
		super.onDestroy();

		mEventBus.unregister(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		GlobalInstanceStateHelper.saveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		GlobalInstanceStateHelper.restoreInstanceState(this, savedInstanceState);
	}
}
