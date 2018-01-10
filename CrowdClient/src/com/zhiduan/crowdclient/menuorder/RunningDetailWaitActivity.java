package com.zhiduan.crowdclient.menuorder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.im.PersonalHomepageActivity;
import com.zhiduan.crowdclient.order.timecount.CountdownView;
import com.zhiduan.crowdclient.order.timecount.CountdownView.OnCountdownEndListener;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtilTools;
import com.zhiduan.crowdclient.util.Res;
import com.zhiduan.crowdclient.util.OrderUtil.DataCallback;
import com.zhiduan.crowdclient.util.OrderUtil.ObjectCallback;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.view.ComplainDialog;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.ComplainDialog.ComplainCallback;
import com.zhiduan.crowdclient.view.MLImageView;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;
import com.zhiduan.crowdclient.view.dialog.DialogUtils.DialogCallback;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * 跑腿业务---待处理
 * 
 * @author yxx
 *
 * @date 2016-5-24 下午6:23:35
 * 
 */
public class RunningDetailWaitActivity extends BaseActivity {

	@ViewInject(R.id.include_1_detail_icon) MLImageView icon;
	@ViewInject(R.id.include_1_detail_name) TextView tvUserName;
	@ViewInject(R.id.include_1_detail_sex) ImageView sex;

	@ViewInject(R.id.include_2_detail_type) ImageView type;
	@ViewInject(R.id.include_2_detail_name) TextView tvRecName;
	@ViewInject(R.id.include_2_detail_orderid) TextView tvOrderId;
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

	@ViewInject(R.id.include_4_detail_layout) LinearLayout layoutRemark;
	@ViewInject(R.id.include_4_detail_1) TextView tvRemark;

	@ViewInject(R.id.item_remind_detail_timecount) CountdownView countdownView;
	@ViewInject(R.id.item_remind_detail_menu) TextView tvTransMenu;
	@ViewInject(R.id.include_order_time_count) LinearLayout layoutTimeCount;//转单中倒计时
	@ViewInject(R.id.wait_running_detail_traning) LinearLayout layoutTraning;

	@ViewInject(R.id.task_detail_complain) LinearLayout layoutComplain;//申述
	@ViewInject(R.id.task_detail_back_complain) LinearLayout layoutBackComplain;//撤销申述
	@ViewInject(R.id.include_order_detail_6_btn_revoke) Button btnBackComplain;//撤销申诉按钮

	@ViewInject(R.id.include_order_detail_reward_title) TextView tvRewardTitle;
	@ViewInject(R.id.include_order_detail_reward) TextView tvReward;
	@ViewInject(R.id.include_order_detail_goods_title) TextView tvGoodsFeeTitle;
	@ViewInject(R.id.include_order_detail_goods) TextView tvGoodsFee;
	@ViewInject(R.id.include_order_detail_plusremark) TextView tvPlusRemark;
	@ViewInject(R.id.include_order_detail_goods_layout) LinearLayout layoutGoods;
	@ViewInject(R.id.include_order_detail_plusremark_layout) LinearLayout layoutPlusRemark;

	@ViewInject(R.id.include_3_layout_run_3) LinearLayout layoutTaskRequire;//任务要求，派件、代取件显示，其他类型隐藏

	private String takerId;//订单编号
	private String orderType;//订单类型

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_wait_running_detail, this);

		ViewUtils.inject(this);
		Utils.addActivity(this);
	}

	int appraiserOther;
	String userIcon;
	String userSex;
	String userName;

	@Override
	public void initView() {

		countdownView.setOnCountdownEndListener(new OnCountdownEndListener() {

			@Override
			public void onEnd(CountdownView cv) {

				getOrderDetail(takerId);
			}
		});

		icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mContext, PersonalHomepageActivity.class);
				intent.putExtra("appraiserOther", appraiserOther);
				intent.putExtra("userIcon", userIcon);
				intent.putExtra("userSex", userSex);
				intent.putExtra("userName", userName);
				startActivity(intent);
			}
		});
	}

	@Override
	public void initData() {

		setTitle("订单详情");
		setRightTitleText("备注");
		setRightTitleTextColor(Res.getColor(R.color.main_color));

		takerId = getIntent().getStringExtra("takerId");
		orderType = getIntent().getStringExtra("orderType");

		layoutGoods.setVisibility(View.GONE);
		layoutBaiChi.setVisibility(View.GONE);
		layoutTaskRequire.setVisibility(View.GONE);

		if(orderType.equals(OrderUtil.ORDER_TYPE_AGENT)){
			layoutTaskRequire.setVisibility(View.VISIBLE);
		}
		if(orderType.startsWith(OrderUtil.ORDER_TYPE_RUN)){
			layoutGoods.setVisibility(View.VISIBLE);
		}

		if(orderType.equals(OrderUtil.ORDER_TYPE_ZIYING)){
			layoutBaiChi.setVisibility(View.VISIBLE);
		}
	}

	protected void onResume(){
		super.onResume();

		getOrderDetail(takerId);
	}

	/**
	 * 获取订单详情
	 * 接口名称 获取订单详情接口
	 * 请求类型 post
	 * 请求Url  /order/packet/getorderdetail.htm
	 */
	private void getOrderDetail(String takerId){
		OrderUtil.getOrderDetail(mContext, takerId, new ObjectCallback() {

			@Override
			public void callback(Object remark) {
				// TODO Auto-generated method stub

				OrderInfo info = (OrderInfo) remark;

				appraiserOther = info.getReceiveId();
				userIcon = info.getReceiveIcon();
				userSex = info.getReceiveSex();
				userName = info.getDeliveryName();

				tvUserName.setText(info.getDeliveryName());

				tvOrderId.setText(info.getTakerId());
				tvRecAdd.setText(info.getReceiveAddress());
				tvRecPhone.setText(info.getReceivePhone());
				tvRecName.setText(info.getReceiveName());
				tvServerTime.setText(info.getServiceDate());

				tvSortType.setText(info.getCategoryName());
				tvRequire.setText(info.getStoreAddress());
				tvRecRemark.setText(info.getDeliveryRequire());

				tvBaiUser.setText(info.getTraderName());
				tvBaiPhone.setText(info.getTraderMobile());
				tvBaiAddress.setText(info.getTraderAddress());

				OrderUtilTools.setRunningOrderDetailReward(mContext, info, tvReward, tvRewardTitle, tvGoodsFee, tvGoodsFeeTitle, layoutGoods, tvPlusRemark, layoutPlusRemark);

				tvRemark.setText(info.getRemark());
				if(TextUtils.isEmpty(info.getRemark())){
					layoutRemark.setVisibility(View.GONE);
				}else{
					layoutRemark.setVisibility(View.VISIBLE);
				}

				if(info.getGenderId().equals(OrderUtil.MALE)){
					sex.setBackgroundResource(R.drawable.profile_boy);
				}else{
					sex.setBackgroundResource(R.drawable.profile_girl);
				}

				if(!TextUtils.isEmpty(info.getReceiveIcon())){
					MyApplication.getImageLoader().displayImage(info.getReceiveIcon(), icon, MyApplication.getOrderDetailOptions(), null);
				}

				OrderUtilTools.setOrderDetailByLockState(info, layoutTimeCount, layoutTraning, layoutComplain, layoutBackComplain, tvTransMenu, countdownView, btnBackComplain);
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
								layoutComplain.setVisibility(View.VISIBLE);
								layoutBackComplain.setVisibility(View.GONE);
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
	 * 跳转到确认收件接口
	 * @param v
	 */
	public void handle(View v){

		JSONObject jsonObject = new JSONObject();
		try {

			JSONArray jsonArray = new JSONArray();
			jsonArray.put(takerId);
			jsonObject.put("takerIdList", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OrderUtil.sureGetGoods(mContext, jsonObject, mEventBus, new ObjectCallback() {

			@Override
			public void callback(Object object) {
				// TODO Auto-generated method stub
				if((Integer) object == 0){
					finish();
				}
			}
		});
	}


	/**
	 * 备注 
	 * (non-Javadoc)
	 * @see com.zhiduan.crowdclient.activity.BaseActivity#rightClick()
	 */
	public void rightClick(){

		Intent intent = new Intent(RunningDetailWaitActivity.this, EditRemarkActivity.class);
		intent.putExtra("takerId", takerId);
		intent.putExtra("remarkType", OrderUtil.REMARK_OTHER);
		intent.putExtra("content", tvRemark.getText().toString());
		startActivity(intent);
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
