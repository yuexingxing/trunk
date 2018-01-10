package com.zhiduan.crowdclient.menuorder;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtil.ObjectCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtilTools;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.view.MLImageView;

/** 
 * 抢单--订单详情
 * 
 * @author yxx
 *
 * @date 2016-5-24 下午6:23:35
 * 
 */
public class OrderDetailGrabActivity extends BaseActivity {

	@ViewInject(R.id.include_1_detail_icon) MLImageView icon;
	@ViewInject(R.id.include_1_detail_name) TextView tvUserName;
	@ViewInject(R.id.include_1_detail_sex) ImageView sex;

	@ViewInject(R.id.include_order_detail_sms_call) RelativeLayout layoutSmsCall;
	@ViewInject(R.id.include_2_detail_type) ImageView type;
	@ViewInject(R.id.include_2_detail_name) TextView tvRecName;
	@ViewInject(R.id.include_2_detail_orderid) TextView tvOrderId;
	@ViewInject(R.id.include_2_detail_phone) TextView tvRecPhone;
	@ViewInject(R.id.include_2_detail_address) TextView tvRecAdd;
	@ViewInject(R.id.include_2_detail_orderid_layout) LinearLayout layoutOrderId;

	@ViewInject(R.id.include_3_detail_1) TextView tvRequire;
	@ViewInject(R.id.include_3_detail_2) TextView tvServerTime;
	@ViewInject(R.id.include_3_detail_3) TextView tvSortType;
	@ViewInject(R.id.include_3_detail_4) TextView tvRecRemark;

	@ViewInject(R.id.include_4_detail_layout) LinearLayout layoutRemark;
	@ViewInject(R.id.include_4_detail_1) TextView tvRemark;

	@ViewInject(R.id.include_order_detail_reward_title) TextView tvRewardTitle;
	@ViewInject(R.id.include_order_detail_reward) TextView tvReward;
	@ViewInject(R.id.include_order_detail_goods) TextView tvGoodsFee;
	@ViewInject(R.id.include_order_detail_plusremark) TextView tvPlusRemark;
	@ViewInject(R.id.include_order_detail_goods_layout) LinearLayout layoutGoods;
	@ViewInject(R.id.include_order_detail_plusremark_layout) LinearLayout layoutPlusRemark;

	private String orderId;//订单编号

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_order_detail_grab, this);

		ViewUtils.inject(this);
		Utils.addActivity(this);
	}

	@Override
	public void initView() {

		layoutSmsCall.setVisibility(View.INVISIBLE);
		layoutOrderId.setVisibility(View.GONE);
	}

	@Override
	public void initData() {

		setTitle("订单详情");

		orderId = getIntent().getStringExtra("orderId");
	}

	@Override
	protected void onResume() {
		super.onResume();

		getOrderDetail(orderId);
	}

	/**
	 * 获取订单详情
	 * 接口名称 获取订单详情接口
	 * 请求类型 post
	 * 请求Url  /order/packet/getorderdetail.htm
	 */
	private void getOrderDetail(String orderId){

		OrderUtil.getPoolOrderDetail(mContext, orderId, new ObjectCallback() {

			@Override
			public void callback(Object object) {
				// TODO Auto-generated method stub

				OrderInfo info = (OrderInfo) object;

				tvUserName.setText(info.getDeliveryName());

				tvOrderId.setText(info.getOrderNo());
				tvRecAdd.setText(info.getReceiveAddress());
				tvRecPhone.setText(info.getReceivePhone());
				tvRecName.setText(info.getReceiveName());

				tvServerTime.setText(info.getServiceDate());
				tvSortType.setText(info.getCategoryName());

				tvRequire.setText(info.getStoreAddress());
				tvRecRemark.setText("************");

				OrderUtilTools.setOrderDetailReward(mContext, info, tvReward, tvRewardTitle, layoutGoods, tvRemark, layoutRemark, tvPlusRemark, layoutPlusRemark);

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
	 * 跳转到确认收件接口
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
