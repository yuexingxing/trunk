package com.zhiduan.crowdclient.menuorder;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.umeng.analytics.MobclickAgent;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.util.Res;
import com.zhiduan.crowdclient.view.CustomProgress;

/** 
 * 异常件详情
 * 
 * @author yxx
 *
 * @date 2016-5-25 下午6:18:24
 * 
 */
public class AbnormalDetailActivity extends BaseActivity {
	
	private final String mPageName = "DistributeOrderDetailActivity";
	private Context mContext;

	private String imgUrl;//异常图片
	private String thumbnailUrl;//缩略图
	private String orderId;//订单编号
	private String orderType;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_abnormal_detail, this);

	}

	@Override
	public void initView() {

		mContext = AbnormalDetailActivity.this;
		
		thumbnailUrl = getIntent().getStringExtra("thumbnailUrl");
		imgUrl = getIntent().getStringExtra("imgUrl");
		orderType = getIntent().getStringExtra("orderType");
		
		if(!TextUtils.isEmpty(thumbnailUrl)){
			MyApplication.getInstance().getImageLoader().displayImage(thumbnailUrl, ((ImageView)findViewById(R.id.img_abnormal_detail_icon)), MyApplication.getInstance().getOptions(), null);
		}else{
//			((ImageView)findViewById(R.id.img_abnormal_detail_icon)).setImageBitmap(null);
		}
	}

	@Override
	public void initData() {

		setTitle("订单详情");
		
		((TextView)findViewById(R.id.tv_abnormal_detail_status)).setText((String) getIntent().getSerializableExtra("status"));

		orderId = getIntent().getStringExtra("orderId");
		getOrderDetail(orderId);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//友盟统计
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		//友盟统计
		MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(this);
	}

	/**
	 * 获取订单详情
	 * @param orderNumber
	 */
	private void getOrderDetail(String orderId){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("orderId", orderId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String strUrl = "waybill/problem/queryproblemdetail.htm";
		if(orderType.equals(OrderUtil.RUNNING)){
			strUrl = "order/errands/getorderdetail.htm";
		}

		CustomProgress.showDialog(mContext, "数据加载中", false, null);
		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				if(success != 0){
					finish();
					CommandTools.showToast(remark);
					return;
				}

				try{

					jsonObject = jsonObject.optJSONObject("data");
					
					orderType = jsonObject.optString("type");
					if(orderType.equals(OrderUtil.PACKET)){
					
						((TextView)findViewById(R.id.tv_abnormal_express_name_title)).setText(Res.getString(R.string.express_name));
						((TextView)findViewById(R.id.tv_abnormal_stack_title)).setText(Res.getString(R.string.stack_no));
						
						((TextView)findViewById(R.id.tv_abnormal_stack)).setText(jsonObject.optString("position"));
						((TextView)findViewById(R.id.tv_abnormal_express_name)).setText(jsonObject.optString("expressName"));
						
						((LinearLayout)findViewById(R.id.tv_abnormal_orderId_layout)).setVisibility(View.GONE);
					}else{
						
						((TextView)findViewById(R.id.tv_abnormal_express_name_title)).setText(Res.getString(R.string.store_name));
						((TextView)findViewById(R.id.tv_abnormal_stack_title)).setText(Res.getString(R.string.get_no));
						
						((TextView)findViewById(R.id.tv_abnormal_stack)).setText(jsonObject.optString("position"));
						((TextView)findViewById(R.id.tv_abnormal_express_name)).setText(jsonObject.optString("trader"));
						
						((LinearLayout)findViewById(R.id.tv_abnormal_orderId_layout)).setVisibility(View.VISIBLE);
						((TextView)findViewById(R.id.tv_abnormal_orderId)).setText(jsonObject.optString("orderId"));
					}
					
					int beVirtual = jsonObject.optInt("beVirtual", 0);
					if(beVirtual == 1){
						((TextView)findViewById(R.id.tv_abnormal_billcode)).setText("");//虚拟单号
					}else{
						((TextView)findViewById(R.id.tv_abnormal_billcode)).setText(jsonObject.optString("waybillNo"));
					}

					((TextView)findViewById(R.id.tv_abnormal_detail_name)).setText(jsonObject.optString("consignee"));
					((TextView)findViewById(R.id.tv_abnormal_phone)).setText(jsonObject.optString("phone"));//手机号
					((TextView)findViewById(R.id.tv_abnormal_delivery_address)).setText(jsonObject.optString("destination"));
					
					JSONObject jsonObject2 = jsonObject.getJSONObject("waybillProblemInfo");

					((TextView)findViewById(R.id.tv_abnormal_delivery_time)).setText(jsonObject2.getString("createTime"));//创建时间
					
					((TextView)findViewById(R.id.tv_abnormal_get_time)).setText(jsonObject.optString("pickupTime"));

					String strRemark = getResources().getString(R.string.remark);//派件要求 
					((TextView)findViewById(R.id.tv_abnormal_remark)).setText(String.format(strRemark, jsonObject.optString("remark")));
					((TextView)findViewById(R.id.tv_abnormal_get_address)).setText(jsonObject.optString("storeAddress"));
					
					String genderId = jsonObject.optString("genderId");
					if(genderId.equals(OrderUtil.MALE)){
						findViewById(R.id.tv_abnormal_detail_sex).setBackgroundResource(R.drawable.profile_boy);
					}else{
						findViewById(R.id.tv_abnormal_detail_sex).setBackgroundResource(R.drawable.profile_girl);
					}

					String strFee = getResources().getString(R.string.fee); 

					int fee2 = (int) jsonObject.optLong("reward");//打赏金额

					((TextView)findViewById(R.id.tv_abnormal_fee)).setText(String.format(strFee, AmountUtils.changeF2Y(fee2, 0)));

					jsonObject = jsonObject.optJSONObject("waybillProblemInfo");//异常问题
					
					((TextView)findViewById(R.id.tv_abnormal_reason)).setText(jsonObject.optString("problemReason"));//异常原因
				}catch(Exception e){
					e.printStackTrace();
				}

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
		
		Intent intent = new Intent(AbnormalDetailActivity.this, ZoomImageActivity.class);
		intent.putExtra("imgUrl", imgUrl);
		startActivity(intent);
	}
}
