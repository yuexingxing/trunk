package com.zhiduan.crowdclient.menuorder;

import org.json.JSONArray;
import org.json.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.order.timecount.CountdownView;
import com.zhiduan.crowdclient.order.timecount.CountdownView.OnCountdownEndListener;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtilTools;
import com.zhiduan.crowdclient.util.OrderUtil.DataCallback;
import com.zhiduan.crowdclient.util.OrderUtil.ObjectCallback;
import com.zhiduan.crowdclient.util.OrderUtil.OrderSignCallback;
import com.zhiduan.crowdclient.util.Res;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.view.ComplainDialog;
import com.zhiduan.crowdclient.view.ComplainDialog.ComplainCallback;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.MLImageView;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;
import com.zhiduan.crowdclient.view.dialog.DialogUtils.DialogCallback;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * 派件---进行中
 * 
 * @author yxx
 *
 * @date 2016-5-24 下午6:23:35
 * 
 */
public class OrderDetailDistributeActivity extends BaseActivity{

	private String takerId;//订单编号
	private int state = 0;

	@ViewInject(R.id.include_1_detail_icon) MLImageView icon;
	@ViewInject(R.id.include_1_detail_name) TextView tvUserName;
	@ViewInject(R.id.include_1_detail_sex) ImageView sex;

	@ViewInject(R.id.include_2_detail_type) ImageView type;
	@ViewInject(R.id.include_2_detail_name) TextView tvRecName;
	@ViewInject(R.id.include_2_detail_orderid) TextView tvOrderId;
	@ViewInject(R.id.include_2_detail_phone) TextView tvRecPhone;
	@ViewInject(R.id.include_2_detail_address) TextView tvRecAdd;

	@ViewInject(R.id.include_3_detail_1) TextView tvRequire;
	@ViewInject(R.id.include_3_detail_2) TextView tvServerTime;
	@ViewInject(R.id.include_3_detail_3) TextView tvSortType;
	@ViewInject(R.id.include_3_detail_4) TextView tvRecRemark;

	@ViewInject(R.id.include_4_detail_layout) LinearLayout layoutRemark;
	@ViewInject(R.id.include_4_detail_1) TextView tvRemark;

	@ViewInject(R.id.item_remind_detail_timecount) CountdownView countdownView;
	@ViewInject(R.id.item_remind_detail_menu) TextView tvTransMenu;
	@ViewInject(R.id.dis_order_detail_time_count) LinearLayout layoutTimeCount;//转单中倒计时
	@ViewInject(R.id.dis_order_detail_traning) LinearLayout layoutTraning;

	@ViewInject(R.id.dis_order_detail_complain) LinearLayout layoutComplain;//申述
	@ViewInject(R.id.dis_order_detail_back_complain) LinearLayout layoutBackComplain;//撤销申述
	@ViewInject(R.id.include_order_detail_6_btn_revoke) Button btnBackComplain;//撤销申诉按钮
	
	@ViewInject(R.id.dis_order_detail_signed) LinearLayout layoutSigned;
	@ViewInject(R.id.include_7_order_detail_handle) Button btnHandle;//订单处理
	@ViewInject(R.id.item_order_dis_complain_signed) TextView tvComplainSigned;//已完成订单申述

	@ViewInject(R.id.include_order_detail_reward_title) TextView tvRewardTitle;
	@ViewInject(R.id.include_order_detail_reward) TextView tvReward;
	@ViewInject(R.id.include_order_detail_goods) TextView tvGoodsFee;
	@ViewInject(R.id.include_order_detail_plusremark) TextView tvPlusRemark;
	@ViewInject(R.id.include_order_detail_goods_layout) LinearLayout layoutGoods;
	@ViewInject(R.id.include_order_detail_plusremark_layout) LinearLayout layoutPlusRemark;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_distribute_order_detail, this);

		ViewUtils.inject(this);
		Utils.addActivity(this);
	}

	@Override
	public void initView() {

		countdownView.setOnCountdownEndListener(new OnCountdownEndListener() {

			@Override
			public void onEnd(CountdownView cv) {

				getOrderDetail(takerId);
			}
		});

		tvComplainSigned.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onComplain(null);
			}
		});
	}

	@Override
	public void initData() {

		setTitle("订单详情");
		setRightTitleText("备注");
		setRightTitleTextColor(Res.getColor(R.color.main_color));
		btnHandle.setText("我已完成");

		takerId = getIntent().getStringExtra("takerId");
	}

	protected void onResume(){
		super.onResume();

//		getOrderDetail(takerId);
	}

	public void onEventMainThread(Message msg) {

		if(msg.what == OrderUtil.REFRESH_WAIT_DETAIL_DATA){

			finish();
		}
	}

	/**
	 * 获取订单详情
	 * 接口名称 获取订单详情接口
	 */
	private void getOrderDetail(String takerId){

		OrderUtil.getOrderDetail(mContext, takerId, new ObjectCallback() {

			@Override
			public void callback(Object object) {
				// TODO Auto-generated method stub

				OrderInfo info = (OrderInfo) object;

				tvUserName.setText(info.getDeliveryName());

				tvOrderId.setText(info.getTakerId());
				tvRecAdd.setText(info.getReceiveAddress());
				tvRecPhone.setText(info.getReceivePhone());
				tvRecName.setText(info.getReceiveName());

				tvServerTime.setText(info.getServiceDate());
				tvSortType.setText(info.getCategoryName());

				tvRequire.setText(info.getStoreAddress());
				tvRecRemark.setText(info.getDeliveryRequire());

				OrderUtilTools.setOrderDetailReward(mContext, info, tvReward, tvRewardTitle, layoutGoods, tvRemark, layoutRemark, tvPlusRemark, layoutPlusRemark);

				if(info.getGenderId().equals(OrderUtil.MALE)){
					sex.setBackgroundResource(R.drawable.profile_boy);
				}else{
					sex.setBackgroundResource(R.drawable.profile_girl);
				}

				if(!TextUtils.isEmpty(info.getReceiveIcon())){
					MyApplication.getImageLoader().displayImage(info.getReceiveIcon(), icon, MyApplication.getOrderDetailOptions(), null);
				}

				state = info.getState();
				if(info.getState() == 4){

					layoutTimeCount.setVisibility(View.GONE);
					layoutTraning.setVisibility(View.GONE);
					layoutComplain.setVisibility(View.GONE);
					layoutBackComplain.setVisibility(View.GONE);
					layoutSigned.setVisibility(View.VISIBLE);
				}else{
					OrderUtilTools.setOrderDetailByLockState(info, layoutTimeCount, layoutTraning, layoutComplain, layoutBackComplain, tvTransMenu, countdownView, btnBackComplain);
				}
			}
		});
	}

	/**
	 * 订单申述
	 * @param v
	 */
	public void onComplain(View v){

		ComplainDialog.showDialog(mContext, takerId, new ComplainCallback(){

			@Override
			public void callback(JSONObject jsonObject) {

				OrderUtil.complainSubmit(mContext, jsonObject, new DataCallback() {

					@Override
					public void callback(int success, String remark, JSONObject jsonObject) {

						if(success == 0){
							layoutTimeCount.setVisibility(View.GONE);
							layoutTraning.setVisibility(View.GONE);
							layoutComplain.setVisibility(View.GONE);
							layoutBackComplain.setVisibility(View.VISIBLE);
							layoutSigned.setVisibility(View.GONE);
						}
					}
				});
			}
		});
	}

	/**
	 * 撤销申述
	 * @param v
	 */
	public void backComplain(View v){

		DialogUtils.showTwoButtonDialog(mContext, GeneralDialog.DIALOG_ICON_TYPE_5, "", "确定要撤回申述？", "取消", "确定", new DialogCallback() {

			@Override
			public void callback(int position) {
				// TODO Auto-generated method stub
				if(position == 0){

					OrderUtil.complainRevoke(mContext, takerId, new DataCallback() {

						@Override
						public void callback(int success, String remark, JSONObject jsonObject) {

							if(success == 0){
								layoutTimeCount.setVisibility(View.GONE);
								layoutTraning.setVisibility(View.GONE);
								layoutComplain.setVisibility(View.GONE);
								layoutBackComplain.setVisibility(View.GONE);
								layoutSigned.setVisibility(View.GONE);

								if(state == 0){
									layoutComplain.setVisibility(View.VISIBLE);
								}else{
									layoutSigned.setVisibility(View.VISIBLE);
								}
							}
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
		jsonArray.put(takerId);

		OrderUtil.transStart(mContext, jsonArray);
	}

	/**
	 * 备注 
	 * (non-Javadoc)
	 * @see com.zhiduan.crowdclient.activity.BaseActivity#rightClick()
	 */
	public void rightClick(){

		Intent intent = new Intent(OrderDetailDistributeActivity.this, EditRemarkActivity.class);
		intent.putExtra("takerId", takerId);
		intent.putExtra("remarkType", OrderUtil.REMARK_OTHER);
		intent.putExtra("content", tvRemark.getText().toString());
		startActivity(intent);
	}

	/**
	 * 确认签收
	 * 派件和代取件弹框确认签收
	 * 跑腿业务需要用验证码签收，
	 * 先弹出窗口校验验证码，如果成功则调签收接口
	 * @param v
	 */
	public void handle(View v){

		DialogUtils.showTwoButtonDialog(mContext, GeneralDialog.DIALOG_ICON_TYPE_8, "签收", "确认签收吗?", "取消", "确认", new DialogCallback() {

			@Override
			public void callback(int position) {
				// TODO Auto-generated method stub
				if(position == 0){
					updateToSigned(takerId);
				}
			}
		});

	}

	/**
	 * 更新为已签收状态
	 * @param takerId
	 */
	private void updateToSigned(String takerId){

		OrderUtil.updateToSigned(mContext, takerId, mEventBus, new OrderSignCallback() {

			@Override
			public void callback(int success, String remark) {

				CommandTools.showToast(remark);
				if(success == 0){

					finish();
				}
			}
		});
	}

	/**
	 * 调用系统发短信界面
	 * @param phone
	 */
	public void sendSms(View v){

		sendSms(tvRecPhone.getText().toString());
	}

	/**
	 * 打电话
	 * @param phone
	 */
	public void callPhone(View v){

		callPhone(tvRecPhone.getText().toString());
	}
}
