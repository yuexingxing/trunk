package com.zhiduan.crowdclient.menuorder;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.CommonAdapter;
import com.zhiduan.crowdclient.adapter.ViewHolder;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.ScreenUtil;
import com.zhiduan.crowdclient.util.OrderUtil.RefreshCallback;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

/** 
 * 订单筛选过滤
 * 
 * @author yxx
 *
 * @date 2016-8-18 上午10:09:24
 * 
 */
public class FilterOrderActivity extends Activity implements IXListViewListener{

	private Context mContext;

	private EditText edtSearch;
	private DropDownListView listView;

	private List<OrderInfo> dataList = new ArrayList<OrderInfo>();//所有数据
	private List<OrderInfo> sortList = new ArrayList<OrderInfo>();//筛选后的数据

	private CommonAdapter<OrderInfo> commonAdapter;

	private int pageNumber = 1;//当前页

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_filter_order);

		findViewById();
	}

	private void findViewById(){

		setImmerseLayout(findViewById(R.id.layout_order_filter_top));
		mContext = FilterOrderActivity.this;

		edtSearch = (EditText) findViewById(R.id.edt_order_search_address);

		listView = (DropDownListView) findViewById(R.id.lv_public_dropdown);
		listView.setAdapter(commonAdapter = new CommonAdapter<OrderInfo>(mContext, sortList, R.layout.item_order_filter) {

			@Override
			public void convert(ViewHolder helper, OrderInfo item) {

				LinearLayout layout = helper.getView(R.id.layout_order_filter);

				ImageView imgIcon = helper.getView(R.id.item_order_filter_icon);
				ImageView imgSex = helper.getView(R.id.item_order_filter_sex);
				ImageView imgType = helper.getView(R.id.item_order_filter_type);

				if(!TextUtils.isEmpty(item.getReceiveIcon())){
					MyApplication.getImageLoader().displayImage(item.getReceiveIcon(), imgIcon, MyApplication.getInstance().getOptions(), null);
				}else{
					if(item.getReceiveSex().equals(OrderUtil.MALE)){
						imgIcon.setImageResource(R.drawable.male);
						imgSex.setImageResource(R.drawable.profile_boy);
					}else{
						imgIcon.setImageResource(R.drawable.female);
						imgSex.setImageResource(R.drawable.profile_girl);
					}
				}

				item.setDeliveryTime(OrderUtil.getBetweenTime(item.getDeliveryStartDate(), item.getDeliveryEndDate()));

				TextView tvTitle1 = helper.getView(R.id.item_order_filter_1);
				TextView tvTitle2 = helper.getView(R.id.item_order_filter_2);
				TextView tvTitle3 = helper.getView(R.id.item_order_filter_3);
				TextView tvTitle4 = helper.getView(R.id.item_order_filter_4);
				TextView tvTitle5 = helper.getView(R.id.item_order_filter_5);

				TextView tvContent1 = helper.getView(R.id.item_order_filter_11);
				TextView tvContent2 = helper.getView(R.id.item_order_filter_21);
				TextView tvContent3 = helper.getView(R.id.item_order_filter_31);
				TextView tvContent4 = helper.getView(R.id.item_order_filter_41);
				TextView tvContent5 = helper.getView(R.id.item_order_filter_51);

				tvTitle1.setTextColor(getResources().getColor(R.color.black));
				tvTitle2.setTextColor(getResources().getColor(R.color.black));
				tvTitle3.setTextColor(getResources().getColor(R.color.black));
				tvTitle4.setTextColor(getResources().getColor(R.color.black));
				tvTitle5.setTextColor(getResources().getColor(R.color.black));

				tvContent1.setTextColor(getResources().getColor(R.color.black));
				tvContent2.setTextColor(getResources().getColor(R.color.black));
				tvContent3.setTextColor(getResources().getColor(R.color.black));
				tvContent4.setTextColor(getResources().getColor(R.color.black));
				tvContent5.setTextColor(getResources().getColor(R.color.black));

				helper.setText(R.id.item_order_filter_name, item.getReceiveName());
				helper.setText(R.id.item_order_filter_phone, item.getReceivePhone());

				//1:创建2:已接单,3:配送中,5:完成,6:取消,7:异常
				if(item.getType().equals(OrderUtil.PACKET)){

					//待取件
					if(item.getState() == 2){

						tvTitle1.setText(getResources().getString(R.string.billcode));
						tvTitle2.setText(getResources().getString(R.string.stack_no));
						tvTitle3.setText(getResources().getString(R.string.delivery_time));
						tvTitle4.setText(getResources().getString(R.string.express_name));
						tvTitle5.setText(getResources().getString(R.string.get_address));

						tvContent1.setText(item.getBillcode());
						tvContent2.setText(item.getStack());
						tvContent4.setText(item.getExpressName());
						tvContent5.setText(item.getDeliveryAddress());

						OrderUtil.setTextColor(mContext, tvContent3, item.getDeliveryTime());

						helper.hideView(R.id.layout_order_filter_item_4, false);
						helper.hideView(R.id.layout_order_filter_item_5, false);

						LayoutParams lp = layout.getLayoutParams();
						lp.height = CommandTools.dip2px(mContext, 120);
					}
					//配送中
					else if(item.getState() == 3){

						tvTitle1.setText(getResources().getString(R.string.billcode));
						tvTitle2.setText(getResources().getString(R.string.receive_fee));
						tvTitle3.setText(getResources().getString(R.string.delivery_time));
						tvTitle4.setText(getResources().getString(R.string.express_name));
						tvTitle5.setText(getResources().getString(R.string.delivery_address));

						tvContent1.setText(item.getBillcode());
						tvContent4.setText(item.getExpressName());
						tvContent5.setText(item.getDeliveryAddress());

						OrderUtil.setTextColor(mContext, tvContent3, item.getDeliveryTime());

						String strFee = MyApplication.getInstance().getResources().getString(R.string.fee);
						String strQrcodeFee = AmountUtils.changeF2Y((int)item.getDeliveryFee(), 0);
						String strMsg = String.format(strFee, strQrcodeFee); 
						tvContent2.setText(strMsg);

						tvContent2.setTextColor(getResources().getColor(R.color.main_color));
						tvContent3.setTextColor(getResources().getColor(R.color.main_color));

						helper.hideView(R.id.layout_order_filter_item_4, false);
						helper.hideView(R.id.layout_order_filter_item_5, false);

						LayoutParams lp = layout.getLayoutParams();
						lp.height = CommandTools.dip2px(mContext, 120);
					}

				}
				//寄件************
				else if(item.getType().equals(OrderUtil.SEND)){

					//待揽件
					if(item.getState() == 2){

						tvTitle1.setText(getResources().getString(R.string.billcode));
						tvTitle2.setText(getResources().getString(R.string.express_name));
						tvTitle3.setText(getResources().getString(R.string.get_time));
						tvTitle5.setText(getResources().getString(R.string.get_address));

						tvContent1.setText(item.getBillcode());
						tvContent2.setText(item.getExpressName());
						tvContent5.setText(item.getDeliveryAddress());

						OrderUtil.setTextColor(mContext, tvContent3, item.getDeliveryTime());

						helper.hideView(R.id.layout_order_filter_item_4, true);

						LayoutParams lp = layout.getLayoutParams();
						lp.height = CommandTools.dip2px(mContext, 100);
					}
					//寄件中
					else if(item.getState() == 3){

						tvTitle1.setText(getResources().getString(R.string.billcode));
						tvTitle2.setText(getResources().getString(R.string.express_name));
						tvTitle5.setText(getResources().getString(R.string.address));

						tvContent1.setText(item.getBillcode());
						tvContent2.setText(item.getExpressName());
						tvContent5.setText(item.getDeliveryAddress());

						helper.hideView(R.id.layout_order_filter_item_3, true);
						helper.hideView(R.id.layout_order_filter_item_4, true);

						LayoutParams lp = layout.getLayoutParams();
						lp.height = CommandTools.dip2px(mContext, 80);
					}

				}
				//代取件
				else if(item.getType().equals(OrderUtil.AGENT_PACKET)){

					//代取件----待取件
					if(item.getState() == 2){

						tvTitle1.setText(getResources().getString(R.string.delivery_time));
						tvTitle5.setText(getResources().getString(R.string.delivery_message));

						OrderUtil.setTextColor(mContext, tvContent1, item.getDeliveryTime());
						tvContent5.setText(item.getDeliveryRequire());

						helper.hideView(R.id.layout_order_filter_item_2, true);
						helper.hideView(R.id.layout_order_filter_item_3, true);
						helper.hideView(R.id.layout_order_filter_item_4, true);
						helper.hideView(R.id.layout_order_filter_item_5, false);

						LayoutParams lp = layout.getLayoutParams();
						lp.height = CommandTools.dip2px(mContext, 60);
					}
					//代取件----配送中
					else{

						tvTitle1.setText(getResources().getString(R.string.delivery_time));
						tvTitle5.setText(getResources().getString(R.string.delivery_message));

						OrderUtil.setTextColor(mContext, tvContent1, item.getDeliveryTime());
						tvContent5.setText(item.getDeliveryRequire());

						helper.hideView(R.id.layout_order_filter_item_2, true);
						helper.hideView(R.id.layout_order_filter_item_3, true);
						helper.hideView(R.id.layout_order_filter_item_4, true);
						helper.hideView(R.id.layout_order_filter_item_5, false);

						LayoutParams lp = layout.getLayoutParams();
						lp.height = CommandTools.dip2px(mContext, 60);
					}

				}

				//packet:取件,agent_packet:代取件,send:寄件
				if(item.getType().equals(OrderUtil.PACKET)){
					imgType.setImageResource(R.drawable.homepage_generation);
				}else if(item.getType().equals(OrderUtil.AGENT_PACKET)){
					imgType.setImageResource(R.drawable.homepage_generation);
				}else if(item.getType().equals(OrderUtil.SEND)){
					imgType.setImageResource(R.drawable.homepage_send);
				}
			}
		});

		listView.setDivider(new ColorDrawable(Color.TRANSPARENT)); 
		listView.setDividerHeight(20);
		listView.setPullRefreshEnable(true);
		listView.setPullLoadEnable(true);
		listView.setXListViewListener(this);

		edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {

				if(arg1 == EditorInfo.IME_ACTION_SEARCH){
					CommandTools.hidenKeyboars(mContext, edtSearch);
					onRefresh();
				}
				return false;
			}
		});
	}

	protected void onResume(){
		super.onResume();
		MyApplication.baseActivity = this;
		onRefresh();
	}

	protected void onStop(){
		super.onStop();

		listView.stopRefresh();
		listView.stopLoadMore();
	}

	/**
	 * 开始搜索
	 * @param v
	 */
	public void searchData(View v){

		onRefresh();
	}

	@Override
	public void onRefresh() {

		JSONObject jsonObject = new JSONObject();
		try {

			jsonObject.put("address", edtSearch.getText().toString());//按地址搜索	string
			jsonObject.put("orderType", 6);//排序类型	number
			jsonObject.put("pageNumber", 1);//当前页数	number
			jsonObject.put("sortType", 2);//排序方式类型1：DESC,2：ASC 默认为DESC
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OrderUtil.queryFilterData(mContext, 0, jsonObject, commonAdapter, listView, dataList, sortList, new RefreshCallback() {

			@Override
			public void callback(int success, int totalCount, int pageSize) {

				pageNumber = 2;
				if(success != 0){
					listView.setLoadHide();
					return;
				}

				if(dataList.size() < totalCount){
					listView.setLoadShow();
					listView.setPullLoadEnable(true);
				}else{
					listView.setLoadHide();
				}

			}
		});
	}

	@Override
	public void onLoadMore() {

		JSONObject jsonObject = new JSONObject();
		try {

			jsonObject.put("address", edtSearch.getText().toString());//按地址搜索	string
			jsonObject.put("orderType", 6);//排序类型	number
			jsonObject.put("pageNumber", pageNumber);//当前页数	number
			jsonObject.put("sortType", 2);//排序方式类型1：DESC,2：ASC 默认为DESC
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OrderUtil.queryFilterData(mContext, 1, jsonObject, commonAdapter, listView, dataList, sortList, new RefreshCallback() {

			@Override
			public void callback(int success, int totalCount, int pageSize) {

				if(success != 0){
					listView.setLoadFinished();
					return;
				}

				if(dataList.size() >= pageSize){
					listView.setLoadShow();
					listView.setPullLoadEnable(true);
				}

				++pageNumber;
			}
		});
	}

	/**
	 * 返回
	 * @param v
	 */
	public void back(View v){

		finish();
	}

	protected void setImmerseLayout(View view) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			/*
			 * window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
			 * , WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			 */
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			int statusBarHeight = ScreenUtil.getStatusBarHeight(this.getBaseContext());
			view.setPadding(0, statusBarHeight, 0, 0);
		}else{
			view.setVisibility(View.GONE);
		}
	}

}
