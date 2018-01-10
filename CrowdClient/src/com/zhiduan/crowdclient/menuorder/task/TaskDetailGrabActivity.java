package com.zhiduan.crowdclient.menuorder.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.adapter.CommonAdapter;
import com.zhiduan.crowdclient.adapter.ViewHolder;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtilTools;
import com.zhiduan.crowdclient.util.OrderUtil.ObjectCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.MyGridView;

/** 
 * 单人任务、多人任务-------接任务
 * 
 * @author yxx
 *
 * @date 2017-1-5 下午4:40:57
 * 
 */
public class TaskDetailGrabActivity extends BaseActivity {

	private String orderId;//订单编号

	@ViewInject(R.id.include_1_detail_icon) ImageView icon;
	@ViewInject(R.id.include_1_detail_name) TextView tvUserName;
	@ViewInject(R.id.include_1_detail_sex) ImageView sex;
	@ViewInject(R.id.include_order_detail_sms_call) RelativeLayout layoutSMS;

	@ViewInject(R.id.include_5_detail_1) TextView tvTheme;
	@ViewInject(R.id.include_5_detail_2) TextView tvOrderId;
	@ViewInject(R.id.include_5_detail_3) TextView tvServerTime;
	@ViewInject(R.id.include_5_detail_4) TextView tvManCount;
	@ViewInject(R.id.include_5_detail_4_1) TextView tvManTotal;
	@ViewInject(R.id.include_5_detail_5) TextView tvSortType;
	@ViewInject(R.id.include_5_detail_6) TextView tvDetail;
	@ViewInject(R.id.include_5_detail_layout_baichi) LinearLayout layoutBaiChi;

	@ViewInject(R.id.layout_order_detail_5_orderId) LinearLayout layoutOrderId;
	@ViewInject(R.id.layout_order_detail_5_orderId_line) View layoutOrderIdLine;
	@ViewInject(R.id.task_order_detail_layout_mulity_icon) LinearLayout layoutMulity;

	@ViewInject(R.id.tv_task_detail_reward) TextView tvReward;

	private List<HashMap<String, Object>> dataListPicture = new ArrayList<HashMap<String, Object>>();
	private CommonAdapter<HashMap<String, Object>> commonAdapterPicture;
	@ViewInject(R.id.include_6_gridview_picture) MyGridView mGridViewPicture;
	@ViewInject(R.id.task_order_detail_layout_gridview_picture) LinearLayout layoutViewPicture; 

	private List<HashMap<String, Object>> dataListIcon = new ArrayList<HashMap<String, Object>>();
	private CommonAdapter<HashMap<String, Object>> commonAdapterIcon;
	@ViewInject(R.id.include_6_gridview_icon) MyGridView mGridViewIcon;
	@ViewInject(R.id.task_order_detail_layout_gridview_icon) LinearLayout layoutViewIcon; 

	@ViewInject(R.id.include_4_detail_layout) LinearLayout layoutRemark;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_task_detail_grab, this);

		ViewUtils.inject(this);
	}

	@Override
	public void initView() {

		layoutRemark.setVisibility(View.GONE);
		initPicture();
		initIcon();
	}

	@Override
	public void initData() {

		setTitle("任务详情");

		layoutSMS.setVisibility(View.GONE);//隐藏短信与拨打电话
		layoutBaiChi.setVisibility(View.GONE);

		orderId = getIntent().getStringExtra("orderId");
		getOrderDetail(orderId);
	}

	protected void onResume(){
		super.onResume();

	}

	private void initPicture(){

		mGridViewPicture = new MyGridView(this);
		mGridViewPicture.setVerticalSpacing(10);
		mGridViewPicture.setHorizontalSpacing(15);
		mGridViewPicture.setNumColumns(5);
		mGridViewPicture.setSelector(new ColorDrawable(Color.TRANSPARENT));

		commonAdapterPicture = new CommonAdapter<HashMap<String, Object>>(mContext, dataListPicture, R.layout.item_task_order_detail_picture) {

			@Override
			public void convert(ViewHolder helper, HashMap<String, Object> item) {
				// TODO Auto-generated method stub

				MyApplication.getImageLoader().displayImage(item.get("icon").toString(), (ImageView)helper.getView(R.id.item_task_order_detail_picture), MyApplication.getInstance().getOptionsNoPic(), null);
			}
		};

		mGridViewPicture.setAdapter(commonAdapterPicture);    

		LinearLayout layoutView_two = new LinearLayout(TaskDetailGrabActivity.this);  
		layoutView_two.setPadding(10, 0, 0, 0);  
		layoutView_two.addView(mGridViewPicture, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));  

		layoutViewPicture.addView(layoutView_two, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); 
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

		LinearLayout layoutView_two = new LinearLayout(TaskDetailGrabActivity.this);  
		layoutView_two.setPadding(10, 0, 0, 0);  
		layoutView_two.addView(mGridViewIcon, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));  

		layoutViewIcon.addView(layoutView_two, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); 
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

	/**
	 * 获取订单详情
	 * @param orderId
	 */
	private void getOrderDetail(String orderId){

		OrderUtil.getPoolOrderDetail(mContext, orderId, new ObjectCallback() {

			@Override
			public void callback(Object object) {

				OrderInfo info = (OrderInfo) object;

				tvUserName.setText(info.getDeliveryName());

				tvOrderId.setText(info.getOrderNo());
				tvTheme.setText(info.getTheme());
				tvServerTime.setText(OrderUtil.getBetweenDate(info.getDeliveryStartDate(), info.getDeliveryEndDate()));
				tvSortType.setText(info.getCategoryName());
				tvDetail.setText(info.getDeliveryRequire());
				tvReward.setText(OrderUtil.changeF2Y(info.getDeliveryFee(), 1));

				if(info.getNeedPplQty() == 1){

					layoutOrderId.setVisibility(View.GONE);
					layoutOrderIdLine.setVisibility(View.GONE);
					layoutMulity.setVisibility(View.GONE);

					tvManTotal.setText("1人");
				}
				else{

					layoutOrderId.setVisibility(View.VISIBLE);
					layoutOrderIdLine.setVisibility(View.VISIBLE);
					layoutMulity.setVisibility(View.VISIBLE);

					tvManCount.setText(info.getGrabedNumber() + "");
					tvManTotal.setText("/" + info.getNeedPplQty());
				}

				if(info.getGenderId().equals(OrderUtil.MALE)){
					sex.setBackgroundResource(R.drawable.profile_boy);
				}else{
					sex.setBackgroundResource(R.drawable.profile_girl);
				}

				if(!TextUtils.isEmpty(info.getReceiveIcon())){
					MyApplication.getImageLoader().displayImage(info.getReceiveIcon(), icon, MyApplication.getInstance().getOptions(), null);
				}

				OrderUtilTools.setTaskDetailIcon(info, dataListPicture, dataListIcon, commonAdapterPicture, commonAdapterIcon, layoutViewPicture, layoutViewIcon);
			}
		});
	}
}
