package com.zhiduan.crowdclient.menuorder.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.adapter.CommonAdapter;
import com.zhiduan.crowdclient.adapter.ViewHolder;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.menuorder.EditRemarkActivity;
import com.zhiduan.crowdclient.order.timecount.CountdownView;
import com.zhiduan.crowdclient.order.timecount.CountdownView.OnCountdownEndListener;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtilTools;
import com.zhiduan.crowdclient.util.Res;
import com.zhiduan.crowdclient.util.OrderUtil.DataCallback;
import com.zhiduan.crowdclient.util.OrderUtil.ObjectCallback;
import com.zhiduan.crowdclient.view.ComplainDialog;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.MyGridView;
import com.zhiduan.crowdclient.view.ComplainDialog.ComplainCallback;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;
import com.zhiduan.crowdclient.view.dialog.DialogUtils.DialogCallback;

/** 
 * 单人任务、多人任务-------待处理
 * 
 * @author yxx
 *
 * @date 2017-1-5 下午4:40:57
 * 
 */
public class TaskDetailWaitTakingActivity extends BaseActivity {

	private String takerId;//订单编号
	private String phone;

	@ViewInject(R.id.include_1_detail_icon) ImageView icon;
	@ViewInject(R.id.include_1_detail_name) TextView tvUserName;
	@ViewInject(R.id.include_1_detail_sex) ImageView sex;

	@ViewInject(R.id.include_order_detail_sms_call) RelativeLayout layoutSmsCall;

	@ViewInject(R.id.include_5_detail_1) TextView tvTheme;
	@ViewInject(R.id.include_5_detail_2) TextView tvOrderId;
	@ViewInject(R.id.include_5_detail_3) TextView tvServerTime;
	@ViewInject(R.id.include_5_detail_4) TextView tvManCount;
	@ViewInject(R.id.include_5_detail_4_1) TextView tvManTotal;
	@ViewInject(R.id.include_5_detail_5) TextView tvSortType;
	@ViewInject(R.id.include_5_detail_6) TextView tvDetail;
	@ViewInject(R.id.include_5_detail_layout_baichi) LinearLayout layoutBaiChi;

	@ViewInject(R.id.task_detail_wait_reward) TextView tvReward;

	@ViewInject(R.id.include_6_gridview_picture) MyGridView mGridViewPicture;
	private List<HashMap<String, Object>> dataListPicture = new ArrayList<HashMap<String, Object>>();
	private CommonAdapter<HashMap<String, Object>> commonAdapterPicture;
	@ViewInject(R.id.task_order_detail_layout_gridview_picture) LinearLayout layoutViewPicture; 

	@ViewInject(R.id.include_6_gridview_icon) MyGridView mGridViewIcon;
	private List<HashMap<String, Object>> dataListIcon = new ArrayList<HashMap<String, Object>>();
	private CommonAdapter<HashMap<String, Object>> commonAdapterIcon;
	@ViewInject(R.id.task_order_detail_layout_gridview_icon) LinearLayout layoutViewIcon; 

	@ViewInject(R.id.include_4_detail_layout) LinearLayout layoutRemark;
	@ViewInject(R.id.include_4_detail_1) TextView tvRemark;

	@ViewInject(R.id.item_remind_detail_timecount) CountdownView countdownView;
	@ViewInject(R.id.item_remind_detail_menu) TextView tvTransMenu;
	@ViewInject(R.id.include_task_order_time_count) LinearLayout layoutTimeCount;//转单中倒计时
	@ViewInject(R.id.wait_task_order_detail_traning) LinearLayout layoutTraning;

	@ViewInject(R.id.layout_order_detail_5_orderId) LinearLayout layoutOrderId;
	@ViewInject(R.id.layout_order_detail_5_orderId_line) View layoutOrderIdLine;
	@ViewInject(R.id.task_order_detail_layout_mulity_icon) LinearLayout layoutMulity;

	@ViewInject(R.id.task_detail_complain) LinearLayout layoutComplain;//申述
	@ViewInject(R.id.task_detail_back_complain) LinearLayout layoutBackComplain;//撤销申述
	@ViewInject(R.id.include_order_detail_6_btn_revoke) Button btnBackComplain;//撤销申诉按钮

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_task_detail_wait_taking, this);

		ViewUtils.inject(this);
	}

	@Override
	public void initView() {

		initPicture();
		initIcon();

		countdownView.setOnCountdownEndListener(new OnCountdownEndListener() {

			@Override
			public void onEnd(CountdownView cv) {

				getOrderDetail(takerId);
			}
		});
	}

	@Override
	public void initData() {
		setTitle("任务详情");

		setRightTitleText("备注");
		setRightTitleTextColor(Res.getColor(R.color.main_color));
		layoutBaiChi.setVisibility(View.GONE);
		
		takerId = getIntent().getStringExtra("takerId");
	}

	protected void onResume(){
		super.onResume();

		getOrderDetail(takerId);
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

		LinearLayout layoutView_two = new LinearLayout(TaskDetailWaitTakingActivity.this);  
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

				MyApplication.getImageLoader().displayImage(item.get("icon").toString(), (ImageView)helper.getView(R.id.item_task_order_detail_icon), MyApplication.getOrderDetailOptions(), null);
			}
		};

		mGridViewIcon.setAdapter(commonAdapterIcon);    

		LinearLayout layoutView_two = new LinearLayout(TaskDetailWaitTakingActivity.this);  
		layoutView_two.setPadding(10, 0, 0, 0);  
		layoutView_two.addView(mGridViewIcon, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));  

		layoutViewIcon.addView(layoutView_two, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); 
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

					OrderUtilTools.refreshDataList(mEventBus, 2);

					finish();
				}
			}
		});
	}

	/* 备注
	 * (non-Javadoc)
	 * @see com.zhiduan.crowdclient.activity.BaseActivity#rightClick()
	 */
	public void rightClick() {

		Intent intent = new Intent(mContext, EditRemarkActivity.class);
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

		sendSms(phone);
	}

	/**
	 * 打电话
	 * @param phone
	 */
	public void callPhone(View v){

		callPhone(phone);
	}

	/**
	 * 获取订单详情
	 * 接口名称 获取订单详情接口
	 */
	private void getOrderDetail(String takerId){

		OrderUtil.getOrderDetail(mContext, takerId, new ObjectCallback() {

			@Override
			public void callback(Object object) {

				OrderInfo info = (OrderInfo) object;

				phone = info.getReceivePhone();
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

				tvRemark.setText(info.getRemark());
				if(TextUtils.isEmpty(info.getRemark())){
					layoutRemark.setVisibility(View.GONE);
				}else{
					layoutRemark.setVisibility(View.VISIBLE);
				}

				if(!TextUtils.isEmpty(info.getReceiveIcon())){
					MyApplication.getImageLoader().displayImage(info.getReceiveIcon(), icon, MyApplication.getInstance().getOptions(), null);
				}

				OrderUtilTools.setOrderDetailByLockState(info, layoutTimeCount, layoutTraning, layoutComplain, layoutBackComplain, tvTransMenu, countdownView, btnBackComplain);
				OrderUtilTools.setTaskDetailIcon(info, dataListPicture, dataListIcon, commonAdapterPicture, commonAdapterIcon, layoutViewPicture, layoutViewIcon);
			}
		});
	}
}
