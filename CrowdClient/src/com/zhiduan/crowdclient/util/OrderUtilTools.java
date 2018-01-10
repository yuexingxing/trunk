package com.zhiduan.crowdclient.util;

import java.util.HashMap;
import java.util.List;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.CommonAdapter;
import com.zhiduan.crowdclient.data.CrowedUserInfo;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.data.TakerInfo;
import com.zhiduan.crowdclient.data.TransOrderInfo;
import com.zhiduan.crowdclient.order.timecount.CountdownView;
import com.zhiduan.crowdclient.view.DropDownListView;
import de.greenrobot.event.EventBus;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * 订单逻辑处理类
 * 
 * @author yxx
 *
 * @date 2017-2-24 下午2:59:04
 * 
 */
public class OrderUtilTools {

	/**
	 * 根据订单状态，设置隐藏与现实
	 * @param info
	 * @param layoutTimeCount
	 * @param layoutTraning
	 * @param layoutComplain
	 * @param layoutBackComplain
	 * @param tvTransMenu
	 * @param countdownView
	 */
	public static void setOrderDetailByLockState(OrderInfo info, LinearLayout layoutTimeCount, LinearLayout layoutTraning, 
			LinearLayout layoutComplain,LinearLayout layoutBackComplain, TextView tvTransMenu, CountdownView countdownView,
			Button btnBackComplain){

		if(info.getLockState() == 0){

			layoutTimeCount.setVisibility(View.GONE);
			layoutTraning.setVisibility(View.GONE);
			layoutComplain.setVisibility(View.VISIBLE);
			layoutBackComplain.setVisibility(View.GONE);
		}else if(info.getLockState() == 1){

			layoutTimeCount.setVisibility(View.VISIBLE);
			layoutTraning.setVisibility(View.VISIBLE);
			layoutComplain.setVisibility(View.GONE);
			layoutBackComplain.setVisibility(View.GONE);

			String strTrans = "正在邀请%s %s帮忙处理订单";
			tvTransMenu.setText(String.format(strTrans, info.getReceiveUserName(), info.getReceiveUserMobile()));

			countdownView.setTag("test1");
			countdownView.start(info.getCountdown());
		}else if(info.getLockState() == 2){

			layoutTimeCount.setVisibility(View.GONE);
			layoutTraning.setVisibility(View.GONE);
			layoutComplain.setVisibility(View.GONE);
			layoutBackComplain.setVisibility(View.VISIBLE);

			if(info.getReportRole() == 1){
				btnBackComplain.setVisibility(View.GONE);
			}else{
				btnBackComplain.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 根据订单的类型显示不同字段
	 * @param info
	 * @param tvTitle1
	 * @param tv1
	 * @param tvTitle2
	 * @param tv2
	 */
	public static void setOrderContentByType(OrderInfo info, TextView tvTitle1, TextView tv1, TextView tvTitle2, TextView tv2){

		if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_PACKET)){

			tvTitle1.setText("配送地址:");
			tvTitle2.setText("送达时间:");
			tv1.setText(info.getReceiveAddress());
			tv2.setText(info.getServiceDate());
		}else if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_AGENT)){

			tvTitle1.setText("配送地址:");
			tvTitle2.setText("送达时间:");
			tv1.setText(info.getReceiveAddress());
			tv2.setText(info.getServiceDate());
		}else if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_RUN)){

			tvTitle1.setText("配送地址:");
			tvTitle2.setText("送达时间:");
			tv1.setText(info.getReceiveAddress());
			tv2.setText(info.getServiceDate());
		}else if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_COMMON)){

			tvTitle1.setText("任务人数:");
			tvTitle2.setText("送达时间:");
			tv1.setText(info.getNeedPplQty() + "人");
			tv2.setText(info.getServiceDate());
		}else{

			tvTitle1.setText("配送地址:");
			tvTitle2.setText("送达时间:");
			tv1.setText(info.getReceiveAddress());
			tv2.setText(info.getServiceDate());
		}
	}

	/**
	 * 单多人任务详情头像与图片加载处理
	 * @param info
	 * @param dataListPicture
	 * @param dataListIcon
	 * @param commonAdapterPicture
	 * @param commonAdapterIcon
	 */
	public static void setTaskDetailIcon(OrderInfo info, List<HashMap<String, Object>> dataListPicture, List<HashMap<String, Object>> dataListIcon,
			CommonAdapter<HashMap<String, Object>> commonAdapterPicture, CommonAdapter<HashMap<String, Object>> commonAdapterIcon,
			LinearLayout layoutViewPicture, LinearLayout layoutViewIcon){

		dataListPicture.clear();
		dataListIcon.clear();

		//接单小派
		if(info.getTakerList().size() == 0){
			layoutViewIcon.setVisibility(View.GONE);
		}else{

			for(int i=0; i<info.getTakerList().size(); i++){  

				//					String url = "http://www.yjz9.com/uploadfile/2014/0606/20140606111306232.jpg";
				TakerInfo takerInfo = info.getTakerList().get(i);
				HashMap<String, Object> map = new HashMap<String, Object>();    
				map.put("icon", takerInfo.getIcon());
				dataListIcon.add(map);
			}

			commonAdapterIcon.notifyDataSetChanged();
		}

		//任务图片
		if(info.getTaskIconList().size() == 0){
			layoutViewPicture.setVisibility(View.GONE);
		}else{
			for(int i=0; i<info.getTaskIconList().size(); i++){  

				//					String url = "http://img.7160.com/uploads/allimg/150313/9-150313112P3.jpg";
				String url = info.getTaskIconList().get(i);
				HashMap<String, Object> map = new HashMap<String, Object>();    
				map.put("icon", url);
				dataListPicture.add(map);
			}  
			commonAdapterPicture.notifyDataSetChanged();
		}
	}

	/**
	 * 刷新订单列表
	 * @param mEventBus
	 */
	public static void refreshDataList(EventBus mEventBus, int state){

		Message msg = new Message();

		if(state == 1){

			msg.what = OrderUtil.REFRESH_WAIT_DATA;//待处理
			mEventBus.post(msg);
		}else if(state == 2){

			msg.what = OrderUtil.REFRESH_WAIT_DATA;//待处理
			mEventBus.post(msg);

			msg.what = OrderUtil.REFRESH_DISTRIBUTION_DATA;	//配送中
			mEventBus.post(msg);
		}else if(state == 3){

			msg.what = OrderUtil.REFRESH_DISTRIBUTION_DATA;	//配送中
			mEventBus.post(msg);

			msg.what = OrderUtil.REFRESH_SIGN_DATA;	//已完成
			mEventBus.post(msg);
		}else if(state == 4){

		}
	}

	/**
	 * 派件设置打赏金额
	 * @param mContext
	 * @param orderType
	 * @param fee
	 * @param tvReward
	 * @param tvRewardTitle
	 * @param layoutGoods
	 */
	public static void setOrderDetailReward(Context mContext, OrderInfo info, TextView tvReward, TextView tvRewardTitle, LinearLayout layoutGoods,
			TextView tvRemark, LinearLayout layoutRemark, TextView tvPlusRemark, LinearLayout layoutPlusRemark){

		//		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) tvReward.getLayoutParams();// 取控件aaa当前的布局参数
		//		linearParams.topMargin = 20;
		//		tvReward.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件aaa

		tvRewardTitle.setTextSize(13);
		tvReward.setTextSize(13);
		tvReward.setText(OrderUtil.changeF2Y(info.getDeliveryFee(), 0));
		layoutGoods.setVisibility(View.GONE);

		tvRemark.setText(info.getRemark());
		if(TextUtils.isEmpty(info.getRemark())){
			layoutRemark.setVisibility(View.GONE);
		}else{
			layoutRemark.setVisibility(View.VISIBLE);
		}

		tvPlusRemark.setText(info.getPlusRemark());
		if(TextUtils.isEmpty(info.getPlusRemark())){
			layoutPlusRemark.setVisibility(View.GONE);
		}else{
			layoutPlusRemark.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置跑腿，代取件等金额与加成显示
	 * @param mContext
	 * @param info
	 * @param tvReward
	 * @param tvRewardTitle
	 * @param tvGoodsFee
	 * @param tvGoodsFeeTitle
	 * @param layoutGoods
	 * @param tvPlusRemark
	 * @param layoutPlusRemark
	 */
	public static void setRunningOrderDetailReward(Context mContext, OrderInfo info,
			TextView tvReward, TextView tvRewardTitle, TextView tvGoodsFee, TextView tvGoodsFeeTitle, LinearLayout layoutGoods,
			TextView tvPlusRemark, LinearLayout layoutPlusRemark){

		tvReward.setText(OrderUtil.changeF2Y(info.getDeliveryFee(), 0));
		tvGoodsFee.setText(OrderUtil.changeF2Y(info.getGoodsFee(), 0));

		if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_AGENT) && TextUtils.isEmpty(info.getPlusRemark())){
			tvRewardTitle.setTextSize(13);
			tvReward.setTextSize(13);
		}else{

			if(TextUtils.isEmpty(info.getPlusRemark())){
				setPatams(tvReward, 1, 10);
				setPatams(tvRewardTitle, 1, 10);
			}

			tvRewardTitle.setTextSize(13);
			tvReward.setTextSize(13);
			tvGoodsFeeTitle.setTextSize(13);
			tvGoodsFee.setTextSize(13);
		}

		tvPlusRemark.setText(info.getPlusRemark());
		if(TextUtils.isEmpty(info.getPlusRemark())){
			layoutPlusRemark.setVisibility(View.GONE);
		}else{
			layoutPlusRemark.setVisibility(View.VISIBLE);
		}
	}

	public static void setPatams(View v, int type, int margin){

		LinearLayout.LayoutParams linearParamsReward = (LinearLayout.LayoutParams) v.getLayoutParams();// 取控件aaa当前的布局参数
		linearParamsReward.topMargin = margin;
		v.setLayoutParams(linearParamsReward);
	}

	/**
	 * @param type 1-有网 0-没网
	 * @param listView
	 * @param dataList
	 * @param sortList
	 * @param mAdapter
	 * @param tvNoData
	 * @param imgNoData
	 * @param layoutNoData
	 */
	public static void setNoNetChanged(DropDownListView listView, List<OrderInfo> dataList, List<OrderInfo> sortList, BaseAdapter mAdapter,
			TextView tvNoData, ImageView imgNoData, LinearLayout layoutNoData){

		sortList.clear();
		dataList.clear();
		mAdapter.notifyDataSetChanged();
		listView.stopRefresh();
		listView.stopLoadMore();
		tvNoData.setText("请检查网络连接");
		layoutNoData.setVisibility(View.VISIBLE);
		imgNoData.setBackground(Res.getDrawable(R.drawable.failed_to_load));
	}

	/**
	 * 转入订单处理
	 * @param listView
	 * @param dataList
	 * @param sortList
	 * @param mAdapter
	 * @param tvNoData
	 * @param imgNoData
	 * @param layoutNoData
	 */
	public static void setNoNetChangedByDelivery(DropDownListView listView, List<TransOrderInfo> dataList, List<TransOrderInfo> sortList, BaseAdapter mAdapter,
			TextView tvNoData, ImageView imgNoData, LinearLayout layoutNoData){

		sortList.clear();
		dataList.clear();
		mAdapter.notifyDataSetChanged();
		listView.stopRefresh();
		listView.stopLoadMore();
		tvNoData.setText("请检查网络连接");
		layoutNoData.setVisibility(View.VISIBLE);
		imgNoData.setBackground(Res.getDrawable(R.drawable.failed_to_load));
	}

	/**
	 * 转出订单处理
	 * @param listView
	 * @param dataList
	 * @param sortList
	 * @param mAdapter
	 * @param tvNoData
	 * @param imgNoData
	 * @param layoutNoData
	 */
	public static void setNoNetChangedByTransBill(DropDownListView listView, List<CrowedUserInfo> dataList, List<CrowedUserInfo> sortList, BaseAdapter mAdapter,
			TextView tvNoData, ImageView imgNoData, LinearLayout layoutNoData){

		sortList.clear();
		dataList.clear();
		mAdapter.notifyDataSetChanged();
		listView.stopRefresh();
		listView.stopLoadMore();
		tvNoData.setText("请检查网络连接");
		layoutNoData.setVisibility(View.VISIBLE);
		imgNoData.setBackground(Res.getDrawable(R.drawable.failed_to_load));
	}

	/**
	 * @param length
	 * @param type 1-暂无该相关订单 2-暂无该相关信息
	 * @param tvNoData
	 * @param imgNoData
	 * @param layoutNoData
	 */
	public static void setNoData(int length, int type, TextView tvNoData, ImageView imgNoData, LinearLayout layoutNoData){

		if(type == 1){
			tvNoData.setText("暂无该相关订单");
		}else{
			tvNoData.setText("暂无该相关信息");
		}

		imgNoData.setBackground(Res.getDrawable(R.drawable.load));
		if(length == 0){
			layoutNoData.setVisibility(View.VISIBLE);
		}else{
			layoutNoData.setVisibility(View.GONE);
		}
	}
}
