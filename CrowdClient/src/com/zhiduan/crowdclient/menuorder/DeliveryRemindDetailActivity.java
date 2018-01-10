package com.zhiduan.crowdclient.menuorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.adapter.CommonAdapter;
import com.zhiduan.crowdclient.adapter.ViewHolder;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.order.timecount.CountdownView;
import com.zhiduan.crowdclient.order.timecount.CountdownView.OnCountdownEndListener;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtil.DataCallback;
import com.zhiduan.crowdclient.util.OrderUtil.ObjectCallback;
import com.zhiduan.crowdclient.view.MyGridView;

/** 
 * 转单详情
 * 
 * @author yxx
 *
 * @date 2017-1-5 下午4:40:57
 * 
 */
public class DeliveryRemindDetailActivity extends BaseActivity {

	private String orderType;
	private Long assignId;
	private String phone;

	@ViewInject(R.id.include_1_detail_icon) ImageView icon;
	@ViewInject(R.id.include_1_detail_name) TextView tvUserName;
	@ViewInject(R.id.include_1_detail_sex) ImageView sex;

	@ViewInject(R.id.remind_traning) TextView tvTraning;
	@ViewInject(R.id.item_remind_detail_timecount) CountdownView countdownView;

	@ViewInject(R.id.include_5_detail_1) TextView tvContent1;
	@ViewInject(R.id.include_5_detail_2) TextView tvContent2;
	@ViewInject(R.id.include_5_detail_3) TextView tvContent3;
	@ViewInject(R.id.include_5_detail_4) TextView tvContent4;
	@ViewInject(R.id.include_5_detail_5) TextView tvContent5;
	@ViewInject(R.id.include_5_detail_6) TextView tvContent6;

	@ViewInject(R.id.include_5_detail_1_title) TextView tvTitle1;
	@ViewInject(R.id.include_5_detail_2_title) TextView tvTitle2;
	@ViewInject(R.id.include_5_detail_3_title) TextView tvTitle3;
	@ViewInject(R.id.include_5_detail_4_title) TextView tvTitle4;
	@ViewInject(R.id.include_5_detail_5_title) TextView tvTitle5;
	@ViewInject(R.id.include_5_detail_6_title) TextView tvTitle6;

	@ViewInject(R.id.include_5_detail_6_layout) LinearLayout layoutDetail;

	@ViewInject(R.id.include_5_detail_layout_baichi) LinearLayout layoutBaiChi;
	@ViewInject(R.id.include_5_detail_run_1) TextView tvBaiUser;
	@ViewInject(R.id.include_5_detail_run_2) TextView tvBaiPhone;
	@ViewInject(R.id.include_5_detail_run_3) TextView tvBaiAddress;

	@ViewInject(R.id.remind_detail_reward) TextView tvReward;
	@ViewInject(R.id.item_remind_detail_menu) TextView tvRemindTips;
	@ViewInject(R.id.item_remind_detail_state) TextView tvRemindTraning;

	@ViewInject(R.id.include_6_gridview_icon) MyGridView mGridViewIcon;
	private List<HashMap<String, Object>> dataListIcon = new ArrayList<HashMap<String, Object>>();
	private CommonAdapter<HashMap<String, Object>> commonAdapterIcon;
	@ViewInject(R.id.task_order_detail_layout_gridview_icon) LinearLayout layoutViewIcon; 

	@ViewInject(R.id.task_order_detail_layout_mulity_icon) LinearLayout layoutMulity; 
	@ViewInject(R.id.include_order_detail_5_layout_4) LinearLayout layoutMan; 

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_delivery_remind_detail, this);

		ViewUtils.inject(this);
	}

	@Override
	public void initView() {

		initIcon();
		tvTraning.setVisibility(View.GONE);
		tvRemindTraning.setVisibility(View.VISIBLE);

		countdownView.setOnCountdownEndListener(new OnCountdownEndListener() {

			@Override
			public void onEnd(CountdownView cv) {

				finish();
			}
		});

	}

	@Override
	public void initData() {
		setTitle("任务详情");

		assignId = getIntent().getLongExtra("assignId", 0);
		orderType = getIntent().getStringExtra("orderType");

		layoutMan.setVisibility(View.GONE);
		layoutMulity.setVisibility(View.GONE);
		layoutBaiChi.setVisibility(View.GONE);
		layoutDetail.setVisibility(View.GONE);

		tvTitle1.setText("任务需求:");
		tvTitle6.setText("任务要求:");

		if(orderType.equals(OrderUtil.ORDER_TYPE_PACKET)){
			layoutDetail.setVisibility(View.VISIBLE);
		}else if(orderType.equals(OrderUtil.ORDER_TYPE_AGENT)){
			layoutDetail.setVisibility(View.VISIBLE);
		}else if(orderType.equals(OrderUtil.ORDER_TYPE_COMMON)){
			tvTitle1.setText("标题:");
			tvTitle6.setText("任务详情");
			layoutMan.setVisibility(View.VISIBLE);
			layoutMulity.setVisibility(View.VISIBLE);
		}else if(orderType.equals(OrderUtil.ORDER_TYPE_ZIYING)){//自营
			layoutBaiChi.setVisibility(View.VISIBLE);
		}

//		getOrderDetail(assignId);
	}

	private void initIcon(){

		mGridViewIcon = new MyGridView(this);
		mGridViewIcon.setVerticalSpacing(10);
		mGridViewIcon.setHorizontalSpacing(15);
		mGridViewIcon.setNumColumns(10);
		mGridViewIcon.setSelector(new ColorDrawable(Color.TRANSPARENT));

		commonAdapterIcon = new CommonAdapter<HashMap<String, Object>>(mContext, dataListIcon, R.layout.item_task_order_detail_icon) {

			@Override
			public void convert(ViewHolder helper, HashMap<String, Object> item) {
				// TODO Auto-generated method stub

				MyApplication.getImageLoader().displayImage(item.get("icon").toString(), (ImageView)helper.getView(R.id.item_task_order_detail_icon), MyApplication.getInstance().getOptions(), null);
			}
		};

		mGridViewIcon.setAdapter(commonAdapterIcon);    

		LinearLayout layoutView_two = new LinearLayout(DeliveryRemindDetailActivity.this);  
		layoutView_two.setPadding(10, 0, 0, 0);  
		layoutView_two.addView(mGridViewIcon, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));  

		layoutViewIcon.addView(layoutView_two, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); 
	}

	/**
	 * 拒绝
	 * @param v
	 */
	public void refuse(View v){

		OrderUtil.transRefuseOrReject(mContext, OrderUtil.assign_refuse, checkHandleData(), new DataCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				if(success == 0){

					Message msg = new Message();
					msg.what = OrderUtil.UPDATE_ASSIGN_NUMBER;
					mEventBus.post(msg);

					finish();
				}
			}
		});
	}

	/**
	 * 接受
	 * @param v
	 */
	public void apply(View v){

		OrderUtil.transRefuseOrReject(mContext, OrderUtil.assign_agree, checkHandleData(), new DataCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				if(success == 0){

					Message msg = new Message();
					msg.what = OrderUtil.UPDATE_ASSIGN_NUMBER;
					mEventBus.post(msg);

					finish();
				}
			}
		});
	}

	/**
	 * 筛选要处理的数据
	 * @return
	 */
	private JSONObject checkHandleData(){

		JSONObject jsonObject = new JSONObject();
		try {
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(assignId);
			jsonObject.put("assignId", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	/**
	 * 获取订单详情
	 * 接口名称 获取订单详情接口
	 * 请求类型 post
	 * 请求Url  /order/packet/getorderdetail.htm
	 */
	private void getOrderDetail(Long assignId){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("assignId", assignId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OrderUtil.getAssignOrderDetail(mContext, assignId, new ObjectCallback(){

			@Override
			public void callback(Object object) {

				OrderInfo info = (OrderInfo) object;

				long countTime = CommandTools.getCountTime(info.getAssignDate());
				countdownView.setTag("test1");
				countdownView.start(countTime);

				tvUserName.setText(info.getAssignName());
				tvContent1.setText(info.getTheme());
				tvContent2.setText(info.getOrderNo());
				tvContent3.setText(OrderUtil.getBetweenDate(info.getDeliveryStartDate(), info.getDeliveryEndDate()));
				tvContent4.setText(info.getGrabedNumber() + "");
				tvContent5.setText(info.getCategoryName());
				tvContent6.setText(info.getRemark());
				tvReward.setText(OrderUtil.changeF2Y(info.getDeliveryFee(), 0));

				tvBaiUser.setText(info.getTraderName());
				tvBaiPhone.setText(info.getTraderMobile());
				tvBaiAddress.setText(info.getTraderAddress());

				phone = info.getAssignMobile();
				String strTrans = "%s %s请您帮忙处理订单";
				tvRemindTips.setText(String.format(strTrans, info.getAssignName(), phone));

				if(info.getGenderId().equals(OrderUtil.MALE)){
					sex.setBackgroundResource(R.drawable.profile_boy);
				}else{
					sex.setBackgroundResource(R.drawable.profile_girl);
				}

				String iconUrl = info.getAssignIcon();
				if(!TextUtils.isEmpty(iconUrl)){
					MyApplication.getImageLoader().displayImage(iconUrl, icon, MyApplication.getInstance().getOptions(), null);
				}

				//任务图片
				if(info.getTaskIconList().size() == 0){
					layoutViewIcon.setVisibility(View.GONE);
				}else{
					for(int i=0; i<info.getTaskIconList().size(); i++){  

						//					String url = "http://img.7160.com/uploads/allimg/150313/9-150313112P3.jpg";
						String url = info.getTaskIconList().get(i);
						HashMap<String, Object> map = new HashMap<String, Object>();    
						map.put("icon", url);
						dataListIcon.add(map);
					}  

					commonAdapterIcon.notifyDataSetChanged();
				}
			}
		});

	}

	/**
	 * 调用系统发短信界面
	 * @param phone
	 */
	public void sendSms(View v){

		sendSms(phone);
	}

	/**
	 * 打电话
	 * @param phone
	 */
	public void callPhone(View v){

		callPhone(phone);
	}

}
