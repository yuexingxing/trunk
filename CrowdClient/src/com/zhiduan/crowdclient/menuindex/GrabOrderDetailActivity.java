package com.zhiduan.crowdclient.menuindex;

import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.activity.MainActivity;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.OrderService;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.GlobalInstanceStateHelper;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;
import com.zhiduan.crowdclient.view.dialog.DialogUtils.DialogCallback;

/**
 * Description 抢单订单详情 
 * Author: hexiuhui
 */
public class GrabOrderDetailActivity extends BaseActivity {

	private TextView tv_deliver_phone;// 订单电话
	private TextView tv_send_address;// 派件地址
	private TextView tv_send_require;// 派件要求
	private TextView tv_billcode;// 运单号
	private TextView tv_orderid;// 订单号
	private TextView tv_reward_money;// 打赏金额
	private TextView tv_grab_order;// 立即抢单 按钮
	private TextView tv_deliver_name; // 下单人姓名
	private TextView tv_send_time; // 派件时间
	private TextView tv_express_name;//快递公司
	private TextView tv_placeorder_name;//下单时间
	private ImageView order_detail_sex;
	private LinearLayout layoutOrderId;

	private String mOrderId;
	private String mOrderType;
	private LoadTextNetTask mTaskRequestGetOrderDetail;  //查看详情
	private LoadTextNetTask mTaskRequestRobOrder;        //抢单

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_grab_order_detail, GrabOrderDetailActivity.this);
		setTitle("订单详情");
	}

	@Override
	public void initView() {
		Intent intent = getIntent();
		mOrderId = intent.getStringExtra("orderId");
		mOrderType = intent.getStringExtra("type");

		tv_send_time = (TextView) findViewById(R.id.tv_send_time);
		tv_deliver_name = (TextView) findViewById(R.id.tv_deliver_name);
		tv_deliver_phone = (TextView) findViewById(R.id.tv_deliver_phone);
		tv_send_address = (TextView) findViewById(R.id.tv_send_address);
		tv_send_require = (TextView) findViewById(R.id.tv_send_require);
		tv_reward_money = (TextView) findViewById(R.id.tv_reward_money);
		tv_grab_order = (TextView) findViewById(R.id.tv_grab_order);
		order_detail_sex = (ImageView) findViewById(R.id.order_detail_sex);
		
		tv_billcode = (TextView) findViewById(R.id.tv_grab_order_detail_billcode);
		tv_orderid = (TextView) findViewById(R.id.tv_grab_order_detail_orderid);
		
		layoutOrderId = (LinearLayout) findViewById(R.id.layout_grab_order_detail_orderid);
		
		tv_express_name = (TextView) findViewById(R.id.tv_grab_detail_express_name);
		tv_placeorder_name = (TextView) findViewById(R.id.tv_grab_detail_place_time);
		
		// 抢单
		tv_grab_order.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				//未登录
				if (TextUtils.isEmpty(MyApplication.getInstance().m_userInfo.toKen)) {
					DialogUtils.showLoginDialog(GrabOrderDetailActivity.this);
					return;
				} else if (MyApplication.getInstance().m_userInfo.verifyStatus == Constant.REVIEW_STATE_SUCCESS) {
					mTaskRequestRobOrder = requestGobOrder(mOrderId, mOrderType);
				} else if (MyApplication.getInstance().m_userInfo.verifyStatus == Constant.REVIEW_STATE_NOT_SUBMIT) {
					DialogUtils.showAuthDialog(GrabOrderDetailActivity.this);
					return;
				} else if (MyApplication.getInstance().m_userInfo.verifyStatus == Constant.REVIEW_STATE_SUBMITING) {
					DialogUtils.showReviewingDialog(GrabOrderDetailActivity.this);
					return;
				} else if (MyApplication.getInstance().m_userInfo.verifyStatus == Constant.REVIEW_STATE_FAIL) {
					DialogUtils.showOneButtonDialog(GrabOrderDetailActivity.this, GeneralDialog.DIALOG_ICON_TYPE_5, "", "审核失败，请检查您提交的资料", "确认", null);
					return;
				}

			}
		});

	}

	@Override
	public void initData() {
		if (!mOrderId.equals("")) {
			mTaskRequestGetOrderDetail = requestGetOrderDetail(mOrderId);
		}
	}

	protected LoadTextNetTask requestGetOrderDetail(String orderId) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestGetOrderDetail = null;

				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;

					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONObject jsonObject = jsonObj.getJSONObject("data");
							String destination = jsonObject.getString("destination");
							String expressName = jsonObject.getString("expressName");
							String orderId = jsonObject.getString("orderId");
							Long packageAmount = jsonObject.getLong("packageAmount");
							String phone = jsonObject.getString("phone");
							String position = jsonObject.getString("position");
							String remark = jsonObject.getString("remark");
							Long reward = jsonObject.getLong("reward");
							String state = jsonObject.getString("state");
							String storeAddress = jsonObject.getString("storeAddress");
							String totalAmount = jsonObject.getString("totalAmount");
							String waybillNo = jsonObject.getString("waybillNo");
							String receiveName = jsonObject.getString("consignee");
							String createTime = jsonObject.getString("createTime");
							String startDeliverTime = jsonObject.getString("deliveryStartDate");
							String endDeliverTime = jsonObject.getString("deliveryEndDate");
							
							int beVirtual = jsonObject.optInt("beVirtual", 0);
							if(beVirtual == 1){
								tv_billcode.setText("");//虚拟单号
								tv_orderid.setText(orderId);
								layoutOrderId.setVisibility(View.VISIBLE);
							}else{
								tv_billcode.setText(jsonObject.optString("waybillNo"));
								layoutOrderId.setVisibility(View.GONE);
							}

							Long finalMoney = jsonObject.getLong("finalMoney");
							String startDate = "";
							String endDate = "";
							//提取日期中的小时分钟
							SimpleDateFormat OldDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							SimpleDateFormat NewFormat = new SimpleDateFormat("MM-dd HH:mm");

							try {
								startDate = NewFormat.format(OldDateFormat.parse(startDeliverTime));
								endDate = NewFormat.format(OldDateFormat.parse(endDeliverTime));
							} catch (java.text.ParseException e) {
								e.printStackTrace();
							}

							tv_deliver_name.setText(receiveName);
							tv_deliver_phone.setText(CommandTools.replacePhone(phone));
							
							String strRemark = getResources().getString(R.string.remark);//派件要求 
							tv_send_require.setText(String.format(strRemark, "*****"));

							tv_send_address.setText(destination);

							OrderUtil.setTextColor(GrabOrderDetailActivity.this, tv_send_time, OrderUtil.getBetweenTime(startDeliverTime, endDeliverTime));

							String genderId = jsonObject.optString("genderId");
							if(genderId.equals(OrderUtil.MALE)){
								order_detail_sex.setBackgroundResource(R.drawable.profile_boy);
							}else{
								order_detail_sex.setBackgroundResource(R.drawable.profile_girl);
							}

							try {
								tv_reward_money.setText(AmountUtils.changeF2Y(reward) + "元");
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							tv_express_name.setText(jsonObject.optString("expressName"));//快递公司
							tv_placeorder_name.setText(jsonObject.optString("createTime"));//下单时间
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(GrabOrderDetailActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(GrabOrderDetailActivity.this, result.m_nResultCode);
				}
			}
		};

		CustomProgress.showDialog(GrabOrderDetailActivity.this, "获取数据中...", false, null);
		LoadTextNetTask task = OrderService.getOrderDetail(orderId, listener, null);

		return task;
	}

	protected LoadTextNetTask requestGobOrder(String orderId, String mOrderType) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestRobOrder = null;

				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;

					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						String message = jsonObj.getString("message");
						
						CommandTools.showToast(message);
						if (success == 0) {
							GrabOrderDetailActivity.this.finish();
							
							SharedPreferences sp = GrabOrderDetailActivity.this.getSharedPreferences(Constant.SAVE_NOT_ORDER_NUMBER, Context.MODE_PRIVATE);
							int number = sp.getInt("OrderNumber", 0);
							Util.saveNotNumber(GrabOrderDetailActivity.this, number + 1, Util.order_type);
							MainActivity.mNotOrderNum.setText(number + 1 + "");
							MainActivity.mNotOrderNum.setVisibility(View.VISIBLE);
						} else {
							String code = jsonObj.optString("code");
							
							//抢单模式没有开启
							if(code.equals("010003")){
								DialogUtils.showOneButtonDialog(GrabOrderDetailActivity.this, GeneralDialog.DIALOG_ICON_TYPE_5, "提示", message, "确认", new DialogCallback() {

									@Override
									public void callback(int position) {
										// TODO Auto-generated method stub

									}
								});
							}else{
								CommandTools.showToast(message);
							}

						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(GrabOrderDetailActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(GrabOrderDetailActivity.this, result.m_nResultCode);
				}
			}

		};

		CustomProgress.showDialog(GrabOrderDetailActivity.this, "获取数据中...", false, null);
		LoadTextNetTask task = OrderService.grabOrder(orderId, mOrderType, listener, null);

		return task;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTaskRequestGetOrderDetail != null) {
			mTaskRequestGetOrderDetail.cancel(true);
			mTaskRequestGetOrderDetail = null;
		}

		if (mTaskRequestRobOrder != null) {
			mTaskRequestRobOrder.cancel(true);
			mTaskRequestRobOrder = null;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 被系统回收时保存myapplication全局变量值
		GlobalInstanceStateHelper.saveInstanceState(outState);

		outState.putString("mOrderId", mOrderId);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// 恢复被系统回收后的myapplication值
		GlobalInstanceStateHelper.restoreInstanceState(this, savedInstanceState);

		initView();

		mOrderId = savedInstanceState.getString("mOrderId");
	}
}
