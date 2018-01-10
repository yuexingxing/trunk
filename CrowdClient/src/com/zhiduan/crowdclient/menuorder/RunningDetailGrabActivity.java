package com.zhiduan.crowdclient.menuorder;

import org.json.JSONException;
import org.json.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtilTools;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.util.OrderUtil.ObjectCallback;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.view.MLImageView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/** 
 * 跑腿业务详情---抢单
 * 
 * @author yxx
 *
 * @date 2016-5-24 下午6:23:35
 * 
 */
public class RunningDetailGrabActivity extends BaseActivity {

	@ViewInject(R.id.include_1_detail_icon) MLImageView icon;
	@ViewInject(R.id.include_1_detail_name) TextView tvUserName;
	@ViewInject(R.id.include_1_detail_sex) ImageView sex;

	@ViewInject(R.id.include_order_detail_sms_call) RelativeLayout layoutSmsCall;

	@ViewInject(R.id.include_2_detail_type) ImageView type;
	@ViewInject(R.id.include_2_detail_name) TextView tvRecName;
	@ViewInject(R.id.include_2_detail_orderid) TextView tvOrderId;
	@ViewInject(R.id.include_2_detail_orderid_layout) LinearLayout layoutOrderId;
	@ViewInject(R.id.include_2_detail_orderid_line) View viewOrderIdLine;

	@ViewInject(R.id.include_2_detail_phone) TextView tvRecPhone;
	@ViewInject(R.id.include_2_detail_address) TextView tvRecAdd;

	@ViewInject(R.id.include_3_detail_run_1) TextView tvRequire;
	@ViewInject(R.id.include_3_detail_run_2) TextView tvServerTime;
	@ViewInject(R.id.include_3_detail_run_3) TextView tvSortType;
	@ViewInject(R.id.include_3_detail_run_4) TextView tvRecRemark;

	@ViewInject(R.id.include_3_layout_baichi) LinearLayout layoutBaiChi;
	@ViewInject(R.id.include_3_detail_run_5) TextView tvBaiUser;
	@ViewInject(R.id.include_3_detail_run_6) TextView tvBaiPhone;
	@ViewInject(R.id.include_3_detail_run_7) TextView tvBaiAddress;

	@ViewInject(R.id.include_order_detail_reward_title) TextView tvRewardTitle;
	@ViewInject(R.id.include_order_detail_reward) TextView tvReward;
	@ViewInject(R.id.include_order_detail_goods_title) TextView tvGoodsFeeTitle;
	@ViewInject(R.id.include_order_detail_goods) TextView tvGoodsFee;
	@ViewInject(R.id.include_order_detail_plusremark) TextView tvPlusRemark;
	@ViewInject(R.id.include_order_detail_goods_layout) LinearLayout layoutGoods;
	@ViewInject(R.id.include_order_detail_plusremark_layout) LinearLayout layoutPlusRemark;
	
	@ViewInject(R.id.include_3_layout_run_3) LinearLayout layoutTaskRequire;//任务要求，派件、代取件显示，其他类型隐藏

	private String orderId;//订单编号
	private String orderType;//订单类型

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_running_detail_grab, this);

		ViewUtils.inject(this);
		Utils.addActivity(this);
	}

	@Override
	public void initView() {

		layoutOrderId.setVisibility(View.GONE);
		layoutSmsCall.setVisibility(View.INVISIBLE);
	}

	@Override
	public void initData() {

		setTitle("订单详情");

		orderId = getIntent().getStringExtra("orderId");
		orderType = getIntent().getStringExtra("orderType");

		layoutBaiChi.setVisibility(View.GONE);
		viewOrderIdLine.setVisibility(View.GONE);
		layoutGoods.setVisibility(View.GONE);
		layoutTaskRequire.setVisibility(View.GONE);

		if(orderType.equals(OrderUtil.ORDER_TYPE_AGENT)){
			layoutTaskRequire.setVisibility(View.VISIBLE);
		}

		if(orderType.startsWith(OrderUtil.ORDER_TYPE_RUN)){
			viewOrderIdLine.setVisibility(View.VISIBLE);
			layoutGoods.setVisibility(View.VISIBLE);
		}

		if(orderType.equals(OrderUtil.ORDER_TYPE_ZIYING)){
			layoutBaiChi.setVisibility(View.VISIBLE);
		}

		getOrderDetail(orderId);
	}

	protected void onResume(){
		super.onResume();

	}

	/**
	 * 获取订单详情
	 * 接口名称 获取订单详情接口
	 */
	private void getOrderDetail(String orderId){

		OrderUtil.getPoolOrderDetail(mContext, orderId, new ObjectCallback() {

			@Override
			public void callback(Object remark) {

				OrderInfo info = (OrderInfo) remark;

				tvUserName.setText(info.getDeliveryName());

				tvOrderId.setText(info.getOrderNo());
				tvRecAdd.setText(info.getReceiveAddress());
				tvRecPhone.setText(info.getReceivePhone());
				tvRecName.setText(info.getReceiveName());

				tvServerTime.setText(info.getServiceDate());
				tvSortType.setText(info.getCategoryName());

				tvRequire.setText(info.getStoreAddress());
				tvRecRemark.setText("************");

				tvBaiUser.setText(info.getTraderName());
				tvBaiPhone.setText(info.getTraderMobile());
				tvBaiAddress.setText(info.getTraderAddress());

				OrderUtilTools.setRunningOrderDetailReward(mContext, info, tvReward, tvRewardTitle, tvGoodsFee, tvGoodsFeeTitle, layoutGoods, tvPlusRemark, layoutPlusRemark);

				if(info.getGenderId().equals(OrderUtil.MALE)){
					sex.setBackgroundResource(R.drawable.profile_boy);
				}else{
					sex.setBackgroundResource(R.drawable.profile_girl);
				}

				if(!TextUtils.isEmpty(info.getReceiveIcon())){
					MyApplication.getImageLoader().displayImage(info.getReceiveIcon(), icon, MyApplication.getOrderDetailOptions(), null);
				}
			}
		});
	}

	/**
	 * 抢单
	 * @param v
	 */
	public void handle(View v){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("orderId", orderId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(mContext, OrderUtil.taker_grab, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CommandTools.showToast(remark);
				if(success != 0){
					return;
				}

				OrderUtilTools.refreshDataList(mEventBus, 1);

				finish();
			}
		});
	}

}
