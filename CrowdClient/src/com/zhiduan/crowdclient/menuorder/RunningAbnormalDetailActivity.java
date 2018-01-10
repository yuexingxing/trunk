package com.zhiduan.crowdclient.menuorder;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.util.Res;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.view.CustomProgress;
import de.greenrobot.event.EventBus;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/** 
 * 代取件---异常件
 * 
 * @author yxx
 *
 * @date 2016-5-24 下午6:23:35
 * 
 */
public class RunningAbnormalDetailActivity extends BaseActivity {

	private Context mContext;

	private String orderId;//订单编号

	private EventBus mEventBus;

	private String imgUrl;//异常图片
	private String thumbnailUrl;//缩略图
	
	private TextView tvGetAddress;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_abnormal_running_detail, this);

		Utils.addActivity(this);
	}

	@Override
	public void initView() {

		mEventBus = EventBus.getDefault();
		mEventBus.register(this);
		mContext = RunningAbnormalDetailActivity.this;
		
		tvGetAddress = (TextView) findViewById(R.id.tv_running_detail_abnormal_get_address);
	}

	@Override
	public void initData() {

		setTitle("订单详情");

		orderId = getIntent().getStringExtra("orderId");
		getOrderDetail(orderId);
		
		thumbnailUrl = getIntent().getStringExtra("thumbnailUrl");
		imgUrl = getIntent().getStringExtra("imgUrl");
		if(!TextUtils.isEmpty(thumbnailUrl)){
			MyApplication.getInstance().getImageLoader().displayImage(thumbnailUrl, ((ImageView)findViewById(R.id.img_running_abnormal_detail_icon)), MyApplication.getInstance().getOptions(), null);
		}
	}

	protected void onResume(){
		super.onResume();

	}

	public void onEventMainThread(Message msg) {

		if(msg.what == OrderUtil.REFRESH_WAIT_DETAIL_DATA){

			finish();
		}
	}

	/**
	 * 获取订单详情
	 * 接口名称 获取订单详情接口
	 * 请求类型 post
	 * 请求Url  /order/packet/getorderdetail.htm
	 */
	private void getOrderDetail(String orderId){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("orderId", orderId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "waybill/problem/queryproblemdetail.htm";
		CustomProgress.showDialog(mContext, "数据加载中", false, null);
		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CustomProgress.dissDialog();
				if(success != 0){
					finish();
					CommandTools.showToast(remark);
					return;
				}

				jsonObject = jsonObject.optJSONObject("data");

				((TextView)findViewById(R.id.tv_running_detail_phone)).setText(jsonObject.optString("phone"));//收件人手机号

				((TextView)findViewById(R.id.tv_running_detail_name)).setText(jsonObject.optString("consignee"));

				((TextView)findViewById(R.id.tv_running_detail_deli_address)).setText(jsonObject.optString("destination"));

				String startTime = jsonObject.optString("deliveryStartDate");
				String endTime = jsonObject.optString("deliveryEndDate");

				((TextView)findViewById(R.id.tv_running_detail_rec_time)).setText(jsonObject.optString("createTime"));//申请时间
				
				String genderId = jsonObject.optString("genderId");
				if(genderId.equals(OrderUtil.MALE)){
					findViewById(R.id.tv_running_detail_sex).setBackgroundResource(R.drawable.profile_boy);
				}else{
					findViewById(R.id.tv_running_detail_sex).setBackgroundResource(R.drawable.profile_girl);
				}

				String strFee = getResources().getString(R.string.fee);

				int reward = (int) jsonObject.optLong("reward", 0);//打赏金额

				String strDeliveryMsg = Res.getString(R.string.delivery_message);//服务内容
				((TextView)findViewById(R.id.tv_running_detail_abnormal_server_message)).setText(strDeliveryMsg + jsonObject.optString("remark"));

				JSONObject jsonObject2 = jsonObject.optJSONObject("waybillProblemInfo");

				((TextView)findViewById(R.id.tv_running_abnormal_detail_reward)).setText(String.format(strFee, AmountUtils.changeF2Y(reward, 0)));//打赏金额
				((TextView)findViewById(R.id.tv_running_abnormal_detail_reason)).setText(jsonObject2.optString("problemReason"));//异常原因

				int state = jsonObject.optInt("state");
				if(state == 6){
					((TextView)findViewById(R.id.tv_running_abnormal_detail_state)).setText("已退件");
				}else{
					((TextView)findViewById(R.id.tv_running_abnormal_detail_state)).setText("异常件");
				}
				
				tvGetAddress.setText("取件地址: " + jsonObject.optString("storeAddress"));
			}
		});
	}

	/**
	 * 异常图片浏览
	 * @param v
	 */
	public void viewImage(View v){

		if(TextUtils.isEmpty(imgUrl)){
			return;
		}

		Intent intent = new Intent(mContext, ZoomImageActivity.class);
		intent.putExtra("imgUrl", imgUrl);
		startActivity(intent);
	}
}
