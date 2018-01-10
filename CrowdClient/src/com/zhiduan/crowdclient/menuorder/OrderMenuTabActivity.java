package com.zhiduan.crowdclient.menuorder;

import org.json.JSONException;
import org.json.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.CaptureActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.GlobalInstanceStateHelper;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.Res;
import com.zhiduan.crowdclient.util.ScreenUtil;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.DeliveryOrderSortDialog;
import com.zhiduan.crowdclient.view.DeliveryOrderSortDialog.ResultCallback;
import de.greenrobot.event.EventBus;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/** 
 * 订单模块Tab栏
 * 
 * @author yxx
 *
 * @date 2016-10-26 上午11:03:05
 * 
 */
public class OrderMenuTabActivity extends Activity{

	private TextView tvTitle1;
	private TextView tvTitle2;
	private TextView tvTitle3;
	private TextView tvTitle4;

	private View view1;
	private View view2;
	private View view3;
	private View view4;

	private final int CAPTURE_QRCODE = 0x0011;
	private int CURRENT_PAGE = 0;

	private EventBus mEventBus;

	private FragmentWaitTaking waitTakingFragment;
	private FragmentDistribution distributionFragment;
	private FragmentSigned signedFragment;
	private AbnormalFragment abnormalFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_menu_tab);

		findViewById();
	}

	protected void onResume(){
		super.onResume();

		if(mEventBus == null){
			mEventBus = EventBus.getDefault();
			mEventBus.register(this);
		}

		OrderUtil.FROM_SEARCH = 0;
		
		updateOrderMenuCount();
	}

	private void findViewById() {

		Utils.addActivity(this);

		waitTakingFragment = new FragmentWaitTaking();
		distributionFragment = new FragmentDistribution();
		signedFragment = new FragmentSigned();
		abnormalFragment = new AbnormalFragment();

		tvTitle1 = (TextView) findViewById(R.id.tv_order_wait);
		tvTitle2 = (TextView) findViewById(R.id.tv_order_sending);
		tvTitle3 = (TextView) findViewById(R.id.tv_order_signed);
		tvTitle4 = (TextView) findViewById(R.id.tv_order_abnormal);

		view1 = (View) findViewById(R.id.view_wait);
		view2 = (View) findViewById(R.id.view_sending);
		view3 = (View) findViewById(R.id.view_signed);
		view4 = (View) findViewById(R.id.view_abnormal);

		setDefaultFragment();
	}

	public void onEventMainThread(Message msg) {

	}

	/**
	 * 待取件
	 * 
	 * @param v
	 */
	public void waitTaking(View v) {
		
		MobclickAgent.onEvent(OrderMenuTabActivity.this, "tv_order_wait");

		CURRENT_PAGE = 0;
		tvTitle1.setTextColor(Res.getColor(R.color.main_color));
		tvTitle2.setTextColor(Res.getColor(R.color.gray_4));
		tvTitle3.setTextColor(Res.getColor(R.color.gray_4));
		tvTitle4.setTextColor(Res.getColor(R.color.gray_4));

		view1.setBackgroundResource(R.color.main_color);
		view2.setBackgroundResource(R.color.transparent);
		view3.setBackgroundResource(R.color.transparent);
		view4.setBackgroundResource(R.color.transparent);

		changeFragment();
	}

	/**
	 * 配送中
	 * 
	 * @param v
	 */
	public void distribution(View v) {

		MobclickAgent.onEvent(OrderMenuTabActivity.this, "tv_order_sending");

		if (OrderUtil.isFastClick()) {
			return;
		}

		CURRENT_PAGE = 1;
		tvTitle1.setTextColor(Res.getColor(R.color.gray_4));
		tvTitle2.setTextColor(Res.getColor(R.color.main_color));
		tvTitle3.setTextColor(Res.getColor(R.color.gray_4));
		tvTitle4.setTextColor(Res.getColor(R.color.gray_4));

		view1.setBackgroundResource(R.color.transparent);
		view2.setBackgroundResource(R.color.main_color);
		view3.setBackgroundResource(R.color.transparent);
		view4.setBackgroundResource(R.color.transparent);

		changeFragment();
	}

	/**
	 * 已签收
	 * 
	 * @param v
	 */
	public void signed(View v) {

		MobclickAgent.onEvent(OrderMenuTabActivity.this, "tv_order_signed");

		if (OrderUtil.isFastClick()) {
			return;
		}

		CURRENT_PAGE = 2;
		tvTitle1.setTextColor(Res.getColor(R.color.gray_4));
		tvTitle2.setTextColor(Res.getColor(R.color.gray_4));
		tvTitle3.setTextColor(Res.getColor(R.color.main_color));
		tvTitle4.setTextColor(Res.getColor(R.color.gray_4));

		view1.setBackgroundResource(R.color.transparent);
		view2.setBackgroundResource(R.color.transparent);
		view3.setBackgroundResource(R.color.main_color);
		view4.setBackgroundResource(R.color.transparent);

		changeFragment();
	}

	/**
	 * 异常件
	 * 
	 * @param v
	 */
	public void abNormal(View v) {

		MobclickAgent.onEvent(OrderMenuTabActivity.this, "tv_order_abnormal");

		if (OrderUtil.isFastClick()) {
			return;
		}

		CURRENT_PAGE = 3;
		tvTitle1.setTextColor(Res.getColor(R.color.gray_4));
		tvTitle2.setTextColor(Res.getColor(R.color.gray_4));
		tvTitle3.setTextColor(Res.getColor(R.color.gray_4));
		tvTitle4.setTextColor(Res.getColor(R.color.main_color));

		view1.setBackgroundResource(R.color.transparent);
		view2.setBackgroundResource(R.color.transparent);
		view3.setBackgroundResource(R.color.transparent);
		view4.setBackgroundResource(R.color.main_color);

		changeFragment();
	}

	/**
	 * Fragment切换处理
	 */
	private void changeFragment() {

		// FragmentManager fm = getSupportFragmentManager();
		// FragmentTransaction transaction = fm.beginTransaction();//
		// 开启Fragment事务

		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();// 开启Fragment事务

//		if (CURRENT_PAGE == 0) {
//			transaction.replace(R.id.content, waitTakingFragment);
//		} else if (CURRENT_PAGE == 1) {
//			transaction.replace(R.id.content, distributionFragment);
//		} else if (CURRENT_PAGE == 2) {
//			transaction.replace(R.id.content, signedFragment);
//		} else if (CURRENT_PAGE == 3) {
//			transaction.replace(R.id.content, abnormalFragment);
//		}

		checkPageOrder();
		transaction.commitAllowingStateLoss();
	}

	/**
	 * 通知顶部订单栏隐藏、显示排序按钮
	 */
	private void checkPageOrder() {

		Message msg = new Message();
		msg.what = OrderUtil.HIDEN_ORDER_MENU_SORT;

		msg.arg1 = 1;
		if (CURRENT_PAGE == 3) {
			msg.arg1 = 0;
		}

		mEventBus.post(msg);
	}

	// 回调函数
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CAPTURE_QRCODE) {

			if (resultCode == RESULT_OK) {

				data.getStringExtra("SCAN_RESULT");
			}
		}

		distributionFragment.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 开启扫描
	 * 
	 * @param v
	 */
	public void scan(View v) {

		Intent intent = new Intent();
		intent.setClass(OrderMenuTabActivity.this, CaptureActivity.class);
		startActivityForResult(intent, CAPTURE_QRCODE);
	}

	/**
	 * 刷新待取件数据
	 */
	public void refreshWaitData() {

		Message msg = new Message();
		msg.what = OrderUtil.REFRESH_WAIT_DATA;
		mEventBus.post(msg);
	}

	/**
	 * 刷新配送中数据
	 */
	public void refreshDistributionData() {

		Message msg = new Message();
		msg.what = OrderUtil.REFRESH_DISTRIBUTION_DATA;
		mEventBus.post(msg);
	}

	/**
	 * 刷新已签收数据
	 */
	public void refreshSignData() {

		Message msg = new Message();
		msg.what = OrderUtil.REFRESH_SIGN_DATA;
		mEventBus.post(msg);
	}

	/**
	 * 刷新异常件数据
	 */
	public void refreshAbnormalData() {

		Message msg = new Message();
		msg.what = OrderUtil.REFRESH_ABNORMAL_DATA;
		mEventBus.post(msg);
	}

	/**
	 * 接口名称 (730)查询已抢单、已取件、已完成、异常件订单数量列表(已调通) 请求类型 post 请求Url
	 * /order/packet/selectorderscount.htm
	 */
	private void updateOrderMenuCount() {

		JSONObject jsonObject = new JSONObject();
		try {

			jsonObject.put("beginTime", CommandTools.getChangeDate(-7) + " 00:00:00");
			jsonObject.put("endTime", CommandTools.getChangeDate(0) + " 23:59:59");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "order/packet/selectorderscount.htm";
		RequestUtilNet.postDataToken(OrderMenuTabActivity.this, strUrl, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				if (success != 0) {
					CommandTools.showToast(remark);
					return;
				}
				
				jsonObject = jsonObject.optJSONObject("data");
				int count1 = jsonObject.optInt("grabCount", 0);//
				int count2 = jsonObject.optInt("deliveryCount", 0);
				int count3 = jsonObject.optInt("completeCount", 0);
				int count4 = jsonObject.optInt("exceptionCount", 0);

				String strWait = Res.getString(R.string.wait_take);
				String strMsg1 = String.format(strWait, count1);
				tvTitle1.setText(strMsg1);

				String strDis = Res.getString(R.string.distribution);
				String strMsg2 = String.format(strDis, count2);
				tvTitle2.setText(strMsg2);

				String strSign = Res.getString(R.string.signed);
				String strMsg3 = String.format(strSign, count3);
				tvTitle3.setText(strMsg3);

				String strAbnormal = Res.getString(R.string.abnormal);
				String strMsg4 = String.format(strAbnormal, count4);
				tvTitle4.setText(strMsg4);
			}
		});

	}

	protected void setImmerseLayout(View view) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			/*
			 * window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
			 * , WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			 */
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			int statusBarHeight = ScreenUtil.getStatusBarHeight(this .getBaseContext());
			view.setPadding(0, statusBarHeight, 0, 0);
		} else {
			view.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置默认显示页(待取件) fragment
	 */
	private void setDefaultFragment() {

//		FragmentManager fm = getFragmentManager();
//		FragmentTransaction transaction = fm.beginTransaction();
//		transaction.replace(R.id.content, waitTakingFragment);
//		transaction.show(waitTakingFragment);
//		transaction.commit();
	}

	/**
	 * 排序对话框派件与寄件分开调用dialog
	 * 按照时间、地址、性别进行升序、降序排列
	 * 按照选择的排序规则
	 * 重新调用接口进行数据刷新
	 * @param v
	 */
	public void orderSort(View view) {

		DeliveryOrderSortDialog.showMyDialog(OrderMenuTabActivity.this, CURRENT_PAGE, new ResultCallback() {

			@Override
			public void callback(int position) {

				Message msg = new Message();

				if(CURRENT_PAGE == 0){
					msg.what = OrderUtil.REFRESH_WAIT_DATA;
				}else if(CURRENT_PAGE == 1){
					msg.what = OrderUtil.REFRESH_DISTRIBUTION_DATA;
				}else if(CURRENT_PAGE == 2){
					msg.what = OrderUtil.REFRESH_SIGN_DATA;
				}else if(CURRENT_PAGE == 3){
					msg.what = OrderUtil.REFRESH_ABNORMAL_DATA;
				}

				mEventBus.post(msg);
			}
		});

	}

	/**
	 * 全局搜索
	 * @param v
	 */
	public void orderSearch(View v){

		OrderUtil.FROM_SEARCH = 1;

		Intent intent = new Intent(OrderMenuTabActivity.this, SearchWaitingActivity.class);
		intent.putExtra("searchType", CURRENT_PAGE);
		startActivity(intent);
	}

	/**
	 * 排序
	 * @param v
	 */
	public void orderType(View v){

		Intent intent = new Intent(OrderMenuTabActivity.this, FilterOrderActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		GlobalInstanceStateHelper.saveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		GlobalInstanceStateHelper.restoreInstanceState(this, savedInstanceState);
	}

}
