package com.zhiduan.crowdclient.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.CommonAdapter;
import com.zhiduan.crowdclient.data.AbnormalInfo;
import com.zhiduan.crowdclient.data.CrowedUserInfo;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.data.PageInfo;
import com.zhiduan.crowdclient.data.RewardRuleInfo;
import com.zhiduan.crowdclient.data.TaskOrderInfo;
import com.zhiduan.crowdclient.data.TransOrderInfo;
import com.zhiduan.crowdclient.menuorder.TransBillActivity;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.GeneralDialog.TwoButtonListener;
import com.zhiduan.crowdclient.view.dialog.OrderSortMenuDialog;

import de.greenrobot.event.EventBus;

/** 
 * 订单刷新、加载工具类
 * 
 * @author yxx
 *
 * @date 2016-6-6 下午12:08:35
 * 
 */
public class OrderUtil {

	public static int CURRENT_TYPE = 0;
	public static int FROM_SEARCH = 0;//是否显示搜索框，默认不显示

	public static int POST_MENU_COUNT = 0;//寄件数量

	public static final int SEARCH_DATA_BILLCODE = 1001;//已抢单按单号搜索
	public static final int SCAN_DATA_BILLCODE = 1002;//扫描单号
	public static final int ORDER_MENU_DATA_SORT = 1003;//订单列表对话框筛选
	public static final int UPDATE_ASSIGN_NUMBER = 1004;//更新转单列表数量

	public static final int REFRESH_WAIT_DATA = 2001;//待取件刷新
	public static final int REFRESH_DISTRIBUTION_DATA = 2002;//配送中刷新
	public static final int REFRESH_SIGN_DATA = 2003;//已签收刷新
	public static final int REFRESH_ABNORMAL_DATA = 2004;//异常件刷新
	public static final int REFRESH_WAIT_DETAIL_DATA = 2005;//待取件刷新
	public static final int REFRESH_DISTRIBUTION_DETAIL_DATA = 2006;//配送中详情刷新

	public static final int REFRESH_HURDLE_DATA = 2010;//待揽件刷新
	public static final int REFRESH_SENT_ITEM_IN_DATA = 2011;//寄件中刷新
	public static final int REFRESH_FINISH_DATA = 2012;//已完成刷新
	public static final int REFRESH_BOUNCE_DATA = 2013;//退件刷新

	public static final int SIGNED_QUERY_DATA = 3001;//已签收数据查询刷新

	public static final int UPDATE_POST_ORDER_MENU_NUMBER = 4002;//更新订单四个tab栏的数量
	public static final int SET_POST_ORDER_MENU_NUMBER = 4003;//设置订单四个tab栏的数量

	public static final int SCAN_QRCODE_PAY_MESSAGE = 5001;//订单支付消息提醒

	public static final int REMARK_OTHER = 6001;//配送中备注字数限制
	public static final int REMARK_TASK = 6002;//任务备注字数限制

	public static final int NO_NET_CONNECTION = 7001;//网络连接失败

	public static int HIDEN_ORDER_MENU_SORT = 1;//设置订单栏排序的隐藏与现实(0:隐藏 1：显示)

	public static int DELIVERY_ORDER_TYPE = 3;//排序类型1:性别，2：地址，4：更新时间 默认根据更新时间排序
	public static int DELIVERY_SORT_TYPE = 1;//排序方式类型1：DESC,2：ASC 默认为DESC

	public static int POST_ORDER_TYPE = 1;//1:更新时间,2:寄件人地址,3:寄件人性别,4:快递网点地址
	public static int POST_SORT_TYPE = 1;//排序方式类型1：DESC,2：ASC 默认为DESC

	public static final String PACKET = "packet"; //订单类型,packet:派件
	public static final String AGENT_PACKET = "agent_packet";//代取件
	public static final String SEND = "send";//寄件
	public static final String RUNNING = "errands";//跑腿业务

	public static final String ORDER_TYPE_PACKET = "1010";//派件
	public static final String ORDER_TYPE_AGENT = "1011";//代取件
	public static final String ORDER_TYPE_RUN = "11";//万能跑腿
	public static final String ORDER_TYPE_DAIGOU = "1110";//代购
	public static final String ORDER_TYPE_TASK = "2010";//系统任务
	public static final String ORDER_TYPE_COMMON = "1210";//通用
	public static final String ORDER_TYPE_ZIYING = "1310";//自营
	public static final String EXPERIENCE_RELE_CODE = "exp_plus_order";  // 经验加成
	public static final String TASK_RELE_CODE = "reward_plus_order";  // 悬赏加成

	public static final int EXPRESS_TYPE = 10;  //快递
	public static final int ERRANDS_TYPE = 11;  //跑腿
	public static final int TASK_TYPE = 12;  //任务
	public static final int PRODUCT_TYPE = 13;  //商品

	public static final String MALE = "p_gender_male";//男
	public static final String FEMALE = "p_gender_female";//女

	public static final int REFRESH_DATA = 0;//刷新数据
	public static final int LOADMORE_DATA = 1;//加载数据

	public static final String PENDING = "pending";//待处理
	public static final String UNDERWAY = "underway";//进行中
	public static final String COMPLETED = "completed";//已完成

	public static final int LOCK_STATE_0 = 0;
	public static final int LOCK_STATE_1 = 1;//转单状态
	public static final int LOCK_STATE_2 = 2;//申诉中

	public static final int APP_CURRENT_PAGE_USER = 1;//收件人端
	public static final int APP_CURRENT_PAGE_MONEY = 2;//我要赚钱

	public static String SEARCH_MSG = "";//订单搜索

	// 刷新、加载回调
	public static abstract class RefreshCallback {

		/**
		 * @param success	查询成功标记
		 * @param totalCount	总条数
		 * @param pageSize	每页条数
		 */
		public abstract void callback(int success, int totalCount, int pageSize);
	}

	public static abstract class TaskCallBack{

		/**
		 * @param success	查询标记
		 * @param dataSize	当前从服务器取的条数
		 * @param pageSize	每页多少条
		 */
		public abstract void callback(int success, int dataSize, int pageSize);
	}

	//更新为签收状态
	public static abstract class OrderSignCallback {

		public abstract void callback(int success, String remark);
	}

	public static abstract class DataCallback {

		public abstract void callback(int success, String remark, JSONObject jsonObject);
	}

	public static abstract class ObjectCallback {

		public abstract void callback(Object object);
	}

	/**
	 * 分转为元，保留两位小数¥2.00
	 * @param fee
	 * @param type 0带¥，1带/人， 其他不带
	 * @return
	 */
	public static String changeF2Y(long fee, int type){

		if(type == 0){
			return "¥" + String.format("%.2f", fee/100.0);
		}else if(type == 1){
			return "¥" + String.format("%.2f", fee/100.0) + "/人";
		}else {
			return String.format("%.2f", fee/100.0);
		}
	}

	public static void checkRefresh(JSONObject jsonObject){

		try {

			jsonObject.put("beginTime", CommandTools.getChangeDate(-7) + " 00:00:00");
			jsonObject.put("endTime", CommandTools.getChangeDate(0) + " 23:59:59");
			jsonObject.put("orderType", OrderSortMenuDialog.orderType);
			jsonObject.put("searchKey", SEARCH_MSG);

			if(OrderSortMenuDialog.categoryIdList.length() == 0){
				jsonObject.put("categoryIdList", null);
			}else{
				jsonObject.put("categoryIdList", OrderSortMenuDialog.categoryIdList);
			}

			if(OrderSortMenuDialog.orderType == 0){
				jsonObject.put("orderType", null);
			}else{
				jsonObject.put("orderType", OrderSortMenuDialog.orderType);
			}

			if(OrderSortMenuDialog.sex == 0){
				jsonObject.put("sex", null);
			}else{
				jsonObject.put("sex", OrderSortMenuDialog.sex);
			}

			if(OrderSortMenuDialog.sortType == 0){
				jsonObject.put("sortType", null);
			}else{
				jsonObject.put("sortType", OrderSortMenuDialog.sortType);
			}

			if(OrderSortMenuDialog.taskMode == 0){
				jsonObject.put("taskMode", null);
			}else{
				jsonObject.put("taskMode", OrderSortMenuDialog.taskMode);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 接口名称 接单列表
	 * 请求类型 post
	 * 负  责  人 王宇
	 * 版        本 0210
	 * 状        态 开发中
	 * 请求  Url  /taker/list.htm
	 * @param mContext
	 * @param jsonObject
	 * @param mAdapter
	 * @param listView
	 * @param dataList
	 * @param sortList
	 * @param callBack
	 */
	public static void refreshData(Context mContext, JSONObject jsonObject, final BaseAdapter mAdapter, final DropDownListView listView, final List<OrderInfo> dataList, final List<OrderInfo> sortList, final RefreshCallback callBack){

		MyApplication myApp = MyApplication.getInstance();
		if(myApp.m_userInfo.verifyStatus != 2){
			return;
		}

		try {

			jsonObject.put("orderType", OrderSortMenuDialog.orderType);
			jsonObject.put("searchKey", SEARCH_MSG);

			if(OrderSortMenuDialog.categoryIdList.length() == 0){
				jsonObject.put("categoryIdList", "");
			}else{
				jsonObject.put("categoryIdList", OrderSortMenuDialog.categoryIdList);
			}

			if(OrderSortMenuDialog.orderType == 0){
				jsonObject.put("orderType", "");
			}else{
				jsonObject.put("orderType", OrderSortMenuDialog.orderType);
			}

			if(OrderSortMenuDialog.sex == 0 || OrderSortMenuDialog.sex == 3){
				jsonObject.put("sex", "");
			}else{
				jsonObject.put("sex", OrderSortMenuDialog.sex);
			}

			if(OrderSortMenuDialog.sortType == 0){
				jsonObject.put("sortType", "");
			}else{
				jsonObject.put("sortType", OrderSortMenuDialog.sortType);
			}

			if(OrderSortMenuDialog.taskMode == 0 || OrderSortMenuDialog.taskMode == 3){
				jsonObject.put("taskMode", "");
			}else{
				jsonObject.put("taskMode", OrderSortMenuDialog.taskMode);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		CustomProgress.showDialog(mContext, "数据加载中", true, null);
		listView.setRefreshTime(CommandTools.getTime());
		RequestUtilNet.postDataToken(MyApplication.getInstance(), OrderUtil.taker_list, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CustomProgress.dissDialog();
				listView.stopRefresh();
				dataList.clear();
				sortList.clear();
				mAdapter.notifyDataSetChanged();

				if(success != 0) {
					callBack.callback(-1, 0, 0);
					return;
				}

				JSONObject jsonObjectTotal = jsonObject.optJSONObject("data");

				OrderUtilParse.parseOrderList(jsonObjectTotal, dataList, sortList, callBack, mAdapter);
			}
		});
	}

	/**
	 * 刷新数据
	 * 接口名称 查询已抢到订单列表接口
	 * 请求类型 post
	 * 请求Url  /order/packet/querygrabedorders.htm
	 * @param mContext
	 * @param strUrl
	 * @param mHandler
	 * @param mAdapter
	 * @param listView
	 * @param dataList
	 * @param sortList
	 * @param jsonObject
	 * @param callBack
	 */
	public static void loadMoreData(Context mContext, final BaseAdapter mAdapter, final DropDownListView listView, final List<OrderInfo> dataList, final List<OrderInfo> sortList, JSONObject jsonObject, final RefreshCallback callBack){

		MyApplication myApp = MyApplication.getInstance();
		if(myApp.m_userInfo.verifyStatus != 2){
			return;
		}

		try {
			jsonObject.put("categoryIdList", null);
			jsonObject.put("orderType", 2);
			jsonObject.put("searchKey", SEARCH_MSG);
			jsonObject.put("sex", 1);
			jsonObject.put("sortType", 1);
			jsonObject.put("taskMode", 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		CustomProgress.showDialog(mContext, "数据加载中", true, null);
		RequestUtilNet.postDataToken(mContext, OrderUtil.taker_list, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CustomProgress.dissDialog();
				listView.stopLoadMore();
				if(success != 0){
					//					CommandTools.showToast(remark);
					callBack.callback(-1, 0, 0);
					return;
				}

				JSONObject jsonObjectTotal = jsonObject.optJSONObject("data");

				OrderUtilParse.parseOrderList(jsonObjectTotal, dataList, sortList, callBack, mAdapter);
			}
		});

	}

	/**
	 * 更新为已签收状态
	 * 派件、代取件、跑腿用一个接口
	 * @param mContext
	 * @param orderId
	 * @param verifyCode 跑腿类型的订单必填，其他类型的订单可不填
	 * @param callBack
	 */
	public static void updateToSigned(Context mContext, String orderId, final EventBus mEventBus,  final OrderSignCallback callBack){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("takerId", orderId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(mContext, OrderUtil.taker_complete, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				OrderUtilTools.refreshDataList(mEventBus, 3);
				
				callBack.callback(success, remark);
			}
		});

	}

	/**
	 * 查询学校下的众包用户
	 * @param mContext
	 * @param strUrl
	 * @param jsonObject
	 * @param type 类型0：刷新 1：加载
	 * @param mAdapter
	 * @param listView
	 * @param dataList
	 * @param sortList
	 * @param callBack
	 */
	public static void getCrowedUserList(Context mContext, JSONObject jsonObject, final int type, final BaseAdapter mAdapter, final DropDownListView listView, final List<CrowedUserInfo> dataList, final List<CrowedUserInfo> sortList, final RefreshCallback callBack){

		MyApplication myApp = MyApplication.getInstance();
		if(myApp.m_userInfo.verifyStatus != 2){
			return;
		}

		listView.setRefreshTime(CommandTools.getTime());
		RequestUtilNet.postDataToken(mContext, OrderUtil.assign_pusers, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				listView.stopRefresh();
				listView.stopLoadMore();

				if(type == 0){
					dataList.clear();
					sortList.clear();
				}

				if(success != 0){
					CommandTools.showToast(remark);
					callBack.callback(-1, 0, 0);
					mAdapter.notifyDataSetChanged();
					return;
				}

				OrderUtilParse.parseCrowedUserList(jsonObject, mAdapter, dataList, sortList, callBack);
			}
		});
	}

	/**
	 * 查询异常件列表
	 * 请求类型 post
	 * 请求Url  waybill/problem/queryproblembills.htm
	 * @param mContext
	 * @param type类型 0：刷新 1：加载
	 * @param jsonObject
	 * @param mAdapter
	 * @param listView
	 * @param dataList
	 * @param sortList
	 * @param callBack
	 */
	public static void queryAbnormalData(Context mContext, final int type, JSONObject jsonObject, final BaseAdapter mAdapter, final DropDownListView listView, final List<AbnormalInfo> dataList, final List<AbnormalInfo> sortList, final RefreshCallback callBack){

		MyApplication myApp = MyApplication.getInstance();
		if(myApp.m_userInfo.verifyStatus != 2){
			return;
		}

		CustomProgress.showDialog(mContext, "数据加载中", true, null);
		listView.setRefreshTime(CommandTools.getTime());
		String strUrl = "waybill/problem/queryproblembills.htm";
		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CustomProgress.dissDialog();
				listView.stopRefresh();
				listView.stopLoadMore();

				if(type == 0){
					sortList.clear();
					dataList.clear();//如果是刷新，清空列表
				}

				if(success != 0){
					callBack.callback(-1, 0, 0);
					mAdapter.notifyDataSetChanged();
					return;
				}

				JSONObject jsonObjectTotal = jsonObject.optJSONObject("data");

				//获取数据总条数、页数等
				JSONObject jsonObjectPage = jsonObjectTotal.optJSONObject("queryResult");
				PageInfo pageInfo = new PageInfo();
				pageInfo.setTotalCount(jsonObjectPage.optInt("totalCount"));
				pageInfo.setTotalPageCount(jsonObjectPage.optInt("totalPageCount"));
				pageInfo.setPageNumber(jsonObjectPage.optInt("pageNumber"));
				pageInfo.setPageSize(jsonObjectPage.optInt("pageSize"));
				pageInfo.setOrderBy(jsonObjectPage.optString("orderBy"));

				JSONArray jsonArray = jsonObjectTotal.optJSONArray("responseDto");
				for(int i=0; i<jsonArray.length(); i++){

					jsonObject = jsonArray.optJSONObject(i);
					AbnormalInfo info = new AbnormalInfo();

					info.setTotalCount(pageInfo.getTotalCount());
					info.setTotalPageCount(pageInfo.getTotalPageCount());
					info.setPageNumber(pageInfo.getPageNumber());
					info.setPageSize(pageInfo.getPageSize());

					int beVirtual = jsonObject.optInt("beVirtual", 0);
					if(beVirtual == 1){
						info.setWaybillNo("");//虚拟单号
					}else{
						info.setWaybillNo(jsonObject.optString("waybillNo"));//运单号
					}

					info.setCreateTime(jsonObject.optString("createTime"));
					info.setCreateUserName(jsonObject.optString("createUserName"));
					info.setEcName(jsonObject.optString("ecName"));
					info.setImgUrl(jsonObject.optString("imgUrl"));
					info.setThumbnailUrl(jsonObject.optString("thumbnailUrl"));

					info.setProblemId(jsonObject.optLong("problemId"));
					info.setProblemReason(jsonObject.optString("problemReason"));

					info.setProblemTypeCode(jsonObject.optString("problemTypeCode"));
					info.setProblemTypeName(jsonObject.optString("problemTypeName"));

					info.setState(jsonObject.optString("state"));
					info.setType(jsonObject.optString("orderType"));
					info.setOrderId(jsonObject.optLong("orderId") + "");

					info.setName(jsonObject.optString("receiverName"));
					info.setPhone(jsonObject.optString("receiverMobile"));
					info.setIcon(jsonObject.optString("receiverIcon"));
					info.setSex(jsonObject.optString("receiverSex"));

					info.setStoreName(jsonObject.optString("trader"));//商铺名称
					info.setAssignCode(jsonObject.optString("assignCode") + "");//分单编码

					dataList.add(info);
				}

				sortList.clear();
				sortList.addAll(dataList);

				mAdapter.notifyDataSetChanged();
				callBack.callback(0, pageInfo.getTotalCount(), pageInfo.getPageSize());
			}
		});
	}

	/**
	 * 接口名称 获取筛选列表
	 * 请求类型 post
	 * 负  责  人 赵成全
	 * 版        本 830
	 * 状        态 开发已完成
	 * 请求  Url  /order/packet/queryorders.htm
	 * @param mContext
	 * @param type 0：刷新 1：加载
	 * @param jsonObject
	 * @param commonAdapter
	 * @param listView
	 * @param dataList
	 * @param sortList
	 * @param callBack
	 */
	public static void queryFilterData(Context mContext, final int type, JSONObject jsonObject, final CommonAdapter<OrderInfo> commonAdapter, final DropDownListView listView, final List<OrderInfo> dataList, final List<OrderInfo> sortList, final RefreshCallback callBack){

		MyApplication myApp = MyApplication.getInstance();
		if(myApp.m_userInfo.verifyStatus != 2){
			return;
		}

		CustomProgress.showDialog(mContext, "搜索中", true, null);
		listView.setRefreshTime(CommandTools.getTime());
		String strUrl = "order/packet/queryorders.htm";
		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CustomProgress.dissDialog();
				listView.stopRefresh();
				listView.stopLoadMore();

				if(type == 0){
					sortList.clear();
					dataList.clear();
				}

				commonAdapter.notifyDataSetChanged();
				if(success != 0){
					callBack.callback(-1, 0, 0);
					return;
				}

				JSONObject jsonObjectTotal = jsonObject.optJSONObject("data");

				//获取数据总条数、页数等
				JSONObject jsonObjectPage = jsonObjectTotal.optJSONObject("queryResult");
				PageInfo pageInfo = new PageInfo();
				pageInfo.setTotalCount(jsonObjectPage.optInt("totalCount"));
				pageInfo.setTotalPageCount(jsonObjectPage.optInt("totalPageCount"));
				pageInfo.setPageNumber(jsonObjectPage.optInt("pageNumber"));
				pageInfo.setPageSize(jsonObjectPage.optInt("pageSize"));
				pageInfo.setOrderBy(jsonObjectPage.optString("orderBy"));

				JSONArray jsonArray = jsonObjectTotal.optJSONArray("responseDto");
				for(int i=0; i<jsonArray.length(); i++){

					jsonObject = jsonArray.optJSONObject(i);

					OrderInfo info = new OrderInfo();

					int beVirtual = jsonObject.optInt("beVirtual", 0);
					if(beVirtual == 1){
						info.setBillcode("");//虚拟单号
					}else{
						info.setBillcode(jsonObject.optString("waybillNo"));//运单号
					}

					info.setOrderNo(jsonObject.optString("orderId"));

					info.setReceiveIcon(jsonObject.optString("icon"));
					info.setReceivePhone(jsonObject.optString("phone"));
					info.setReceiveName(jsonObject.optString("name"));
					info.setReceiveSex(jsonObject.optString("sex"));

					info.setExpressName(jsonObject.optString("expressName"));

					info.setDeliveryStartDate(jsonObject.optString("deliveryStartDate"));
					info.setDeliveryEndDate(jsonObject.optString("deliveryEndDate"));
					info.setDeliveryFee(jsonObject.optInt("totalMoney"));
					info.setDeliveryAddress(jsonObject.optString("address"));//所有的地址都保存在这个字段中(派件，取件地址，商户地址)

					info.setType(jsonObject.optString("type"));//packet:取件,agent_packet:代取件,send:寄件
					info.setStack(jsonObject.optString("position"));
					info.setPickupCode(jsonObject.optString("pickupCode"));//取件码
					info.setStoreName(jsonObject.optString("storeName"));//商户名称

					info.setState(jsonObject.optInt("state"));//订单当前状态

					info.setDeliveryRequire(jsonObject.optString("remark"));

					dataList.add(info);
				}

				sortList.clear();
				sortList.addAll(dataList);

				commonAdapter.notifyDataSetChanged();
				callBack.callback(0, pageInfo.getTotalCount(), pageInfo.getPageSize());
			}
		});
	}

	private static long lastClickTime;
	public synchronized static boolean isFastClick() {

		long time = System.currentTimeMillis();   

		if ( time - lastClickTime < 500) {   
			return true;   
		}   
		lastClickTime = time;   
		return false;   
	}

	/**
	 * 根据起始时间，结束时间截取中间时间
	 * 2016-12-12 08:23~12:12
	 * @param startTime 2016-12-12 08:23:00
	 * @param endTime	2016-12-12 21:23:00
	 * @return	12-12 08:23~21:23
	 */
	public static String getBetweenTime(String startTime, String endTime){

		//派件时间处理 格式：2016-12-12 08:23~12:12
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date startDate = formatter.parse(startTime);
			Date endDate = formatter.parse(endTime);

			SimpleDateFormat formatDate = new SimpleDateFormat("MM-dd");
			SimpleDateFormat formatHour = new SimpleDateFormat("HH:mm");

			String strDate = formatDate.format(startDate);
			String sEndDate = formatDate.format(endDate);
			String strStartHour = formatHour.format(startDate);
			String strEndHour;
			if (strDate.equals(sEndDate)) {
				strEndHour = formatHour.format(endDate);
			} else {
				strEndHour = sEndDate + " " + formatHour.format(endDate);
			}

			return strDate + " " + strStartHour + "至" + strEndHour;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 根据起始时间，结束时间截取中间时间(日期)
	 * 2016-12-12 08:23~12:12
	 * @param startTime 2016-12-12 08:23:00
	 * @param endTime	2016-12-13 21:23:00
	 * @return	2016-12-12 ~ 12-13
	 */
	public static String getBetweenDate(String startTime, String endTime){

		//派件时间处理 格式：2016-12-12 08:23~12:12
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date startDate = formatter.parse(startTime);
			Date endDate = formatter.parse(endTime);

			SimpleDateFormat formatFirst = new SimpleDateFormat("MM-dd HH:mm");
			SimpleDateFormat formatSecond = new SimpleDateFormat("MM-dd HH:mm");

			String strFirst = formatFirst.format(startDate);
			String strSecond = formatSecond.format(endDate);

			return strFirst + "至" + strSecond;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 确认取件
	 * @param context
	 * @param orderType订单类型
	 * @param dataList取件集合
	 */
	public static void getGoods(Context context, JSONObject jsonObject, final EventBus mEventBus, final ObjectCallback callback){

		CustomProgress.showDialog(context,"确认收件中", false, null);
		RequestUtilNet.postDataToken(context, OrderUtil.taker_ongoing, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CustomProgress.dissDialog();
				CommandTools.showToast(remark);
				if(success != 0){
					return;
				}

				OrderUtilTools.refreshDataList(mEventBus, 2);
				if(callback != null){
					callback.callback(0);
				}
			}
		});

	}

	/**
	 * 订单模块时间设置
	 * 月日为默认黑色，时分范围为主色
	 * @param textView
	 * @param congent
	 */
	public static void setTextColor(Context context, TextView textView, String content){

		SpannableString spannableString = new SpannableString(content);
		spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.main_color)), 6, content.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

		textView.setText("");
		textView.append(spannableString);
	}

	/**
	 * 签收时获取验证码
	 * 
	 * @param v
	 */
	public static void getVerCode(Context context, JSONObject jsonObject, final OrderSignCallback callback) {

		RequestUtilNet.postData(context, Constant.getVerCode_url, "验证码获取中", true, jsonObject, new MyCallback() {

			@Override
			public void callback(JSONObject jsonObject) {

				CustomProgress.dissDialog();
				try {

					CommandTools.showToast(jsonObject.getString("message"));
					int success = jsonObject.optInt("success");
					if(success == 0){

						if(callback != null){

							callback.callback(0, null);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * @param mContext
	 * @param type 刷新，加载
	 * @param mAdapter
	 * @param listView
	 * @param dataList
	 * @param jsonObject
	 * @param callBack
	 * 接口名称 查询抢单池系统任务列表
	 * 请求类型 post
	 * 负  责  人 王宇
	 * 版        本 1130
	 * 状        态 开发中
	 * 请求  Url  /grabpool/systasklist.htm
	 */
	public static void getTaskData(Context mContext, final int type, final BaseAdapter mAdapter, final DropDownListView listView, final List<TaskOrderInfo> dataList, int pagerNumber, final LinearLayout no_order_layout, final TaskCallBack callBack){

		boolean token = true;
		if(TextUtils.isEmpty(MyApplication.getInstance().m_userInfo.toKen)){
			token = false;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("pageNumber", pagerNumber);//当前页
		} catch (JSONException e) {
			e.printStackTrace();
		}

		CustomProgress.showDialog(mContext, "数据加载中", true, null);
		String strUrl = "grabpool/systasklist.htm";
		RequestUtilNet.postDataIfToken(mContext, strUrl, token, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {
				// TODO Auto-generated method stub

				CustomProgress.dissDialog();
				listView.stopRefresh();
				listView.stopLoadMore();

				if(success != 0){
					callBack.callback(-1, 0, 0);
					return;
				}

				JSONObject jsonObjectTotal = jsonObject.optJSONObject("data");

				OrderUtilParse.handleTaskMessage(no_order_layout, listView, type, jsonObjectTotal, dataList, callBack, mAdapter);
			}
		});

	}

	/**
	 * @param mContext
	 * @param type 刷新，加载
	 * @param mAdapter
	 * @param listView
	 * @param dataList
	 * @param jsonObject
	 * @param callBack
	 * 接口名称 展示小派奖励规则列表接口
	 * 请求类型 post
	 * 负  责  人 王宇
	 * 版        本 1130
	 * 状        态 开发中
	 * 请求  Url  /taskrule/list.htm
	 */
	public static void getRewardRule(Context mContext, final BaseAdapter mAdapter, final DropDownListView listView, final List<RewardRuleInfo> dataList, final LinearLayout no_order_layout, final TaskCallBack callBack){

		boolean token = true;
		if(TextUtils.isEmpty(MyApplication.getInstance().m_userInfo.toKen)){
			token = false;
		}

		CustomProgress.showDialog(mContext, "数据加载中", true, null);
		String strUrl = "taskrule/list.htm";
		RequestUtilNet.postDataIfToken(mContext, strUrl, token, new JSONObject(), new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {
				// TODO Auto-generated method stub

				CustomProgress.dissDialog();
				listView.stopRefresh();
				listView.stopLoadMore();

				if(success != 0){
					callBack.callback(-1, 0, 0);
					return;
				}

				JSONArray jsonArrayTotal = jsonObject.optJSONArray("data");

				dataList.clear();

				int len = jsonArrayTotal.length();

				if (len <= 0) {
					no_order_layout.setVisibility(View.VISIBLE);
					listView.setVisibility(View.GONE);
					dataList.clear();
					mAdapter.notifyDataSetChanged();
					listView.setPullLoadEnable(false);
				}
				for(int i=0; i<len; i++){

					jsonObject = jsonArrayTotal.optJSONObject(i);

					RewardRuleInfo info = new RewardRuleInfo();

					if(jsonObject.has("ruleName")){

						info.setTitle(jsonObject.optString("ruleName"));
						info.setExperence(jsonObject.optString("expPoint"));//经验值
						info.setCash(jsonObject.optInt("money"));//金额分
						info.setFlowState(jsonObject.optString("flowState"));
						info.setType(jsonObject.optString("ruleCode"));
						info.setContent(jsonObject.optString("ruleDesc"));
						info.setCompletedNum(jsonObject.optInt("completedNum")); //已完成数量
						info.setNeedCompleteNumber(jsonObject.optInt("needCompleteNumber")); //总数量
						info.setItemDesc(jsonObject.optString("itemDesc"));
					}

					dataList.add(info);
				}

				mAdapter.notifyDataSetChanged();
				callBack.callback(0, 0, 0);
			}
		});
	}

	/**
	 * 开始转单
	 * @param jsonArray
	 */
	public static void transStart(Context context, JSONArray jsonArray){

		if(jsonArray.length() == 0){
			CommandTools.showToast("请至少选择一条订单");
			return;
		}

		Intent intent = new Intent(context, TransBillActivity.class);
		intent.putExtra("orderIdList", jsonArray.toString());
		context.startActivity(intent);
	}

	/**
	 * 确认取件
	 * @param orderType
	 * @param scanList
	 */
	public static void sureGetGoods(final Context context, final JSONObject jsonObject, final EventBus mEventBus, final ObjectCallback callback){

		GeneralDialog.showTwoButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_8, "", "是否确认取件？", "取消", "确认", new TwoButtonListener() {

			@Override
			public void onRightClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if(dialog!=null){
					dialog.dismiss();
				}

				getGoods(context, jsonObject, mEventBus, new ObjectCallback() {

					@Override
					public void callback(Object object) {

						if(callback != null){
							callback.callback((Integer) object);
						}
					}
				});
			}

			@Override
			public void onLeftClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if(dialog!=null){
					dialog.dismiss();
				}
			}
		});

	}

	/**
	 * 获取订单备注信息
	 * 请求类型 post
	 */
	public static void getRemark(Context context, String takerId, final ObjectCallback callback){

		if(TextUtils.isEmpty(takerId)){
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("takerId", takerId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(context, OrderUtil.packet_selectremark, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				if(success != 0){
					CommandTools.showToast(remark);
					return;
				}

				jsonObject = jsonObject.optJSONObject("data");
				callback.callback(jsonObject.optString("remark"));
			}
		});
	}

	/**
	 * 查询转单列表
	 * @param context
	 * @param jsonObject
	 * @param mAdapter
	 * @param listView
	 * @param dataList
	 * @param sortList
	 * @param callBack
	 */
	public static void refreshTransList(Context context, JSONObject jsonObject, final BaseAdapter mAdapter, final DropDownListView listView, final List<TransOrderInfo> dataList, final List<TransOrderInfo> sortList, final RefreshCallback callBack){

		RequestUtilNet.postDataToken(context, OrderUtil.assign_list, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				listView.stopRefresh();
				listView.stopLoadMore();

				dataList.clear();
				sortList.clear();
				mAdapter.notifyDataSetChanged();

				if(success != 0){
					CommandTools.showToast(remark);
					callBack.callback(-1, 0, 0);
					return;
				}

				JSONObject jsonObjectTotal = jsonObject.optJSONObject("data");
				OrderUtilParse.parseTransList(jsonObjectTotal, dataList, sortList, mAdapter, callBack);
			}
		});
	}

	public static void loadMoreTransList(Context context, JSONObject jsonObject, final BaseAdapter mAdapter, final DropDownListView listView, final List<TransOrderInfo> dataList, final List<TransOrderInfo> sortList, final RefreshCallback callBack){

		RequestUtilNet.postDataToken(context, OrderUtil.assign_list, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				listView.stopRefresh();
				listView.stopLoadMore();

				if(success != 0){
					CommandTools.showToast(remark);
					return;
				}

				JSONObject jsonObjectTotal = jsonObject.optJSONObject("data");
				OrderUtilParse.parseTransList(jsonObjectTotal, dataList, sortList, mAdapter, callBack);
			}
		});
	}

	/**
	 * 转单的同意或拒绝
	 * @param context
	 * @param strUrl
	 * @param jsonObject
	 * @param mAdapter
	 */
	public static void transRefuseOrReject(Context context, String strUrl, JSONObject jsonObject, final DataCallback callback){

		RequestUtilNet.postDataToken(context, strUrl, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CommandTools.showLongToast(remark);
				callback.callback(success, remark, jsonObject);
			}
		});
	}

	/**
	 * 提交申述
	 * @param context
	 * @param strUrl
	 * @param jsonObject
	 */
	public static void complainSubmit(Context context, JSONObject jsonObject, final DataCallback callback){

		RequestUtilNet.postDataToken(context, OrderUtil.taker_submit, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CommandTools.showToast(remark);
				if(success != 0){
					return;
				}

				callback.callback(success, remark, jsonObject);
			}
		});
	}

	/**
	 * 撤回申述
	 * @param context
	 * @param strUrl
	 * @param jsonObject
	 * @param callback
	 */
	public static void complainRevoke(Context context, String takerId, final DataCallback callback){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("takerId", takerId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(context, OrderUtil.taker_revoke, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CommandTools.showToast(remark);
				if(success != 0){
					return;
				}

				callback.callback(success, remark, jsonObject);
			}
		});
	}

	/**
	 * 查询订单详情
	 * @param context
	 * @param jsonObject
	 * @param callback
	 */
	public static void getOrderDetail(Context context, String takerId, final ObjectCallback callback){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("takerId", takerId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		CustomProgress.showDialog(context, "数据加载中", false, null);
		RequestUtilNet.postDataToken(context, OrderUtil.taker_detail, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CustomProgress.dissDialog();
				if(success != 0){
					CommandTools.showToast(remark);
					return;
				}

				jsonObject = jsonObject.optJSONObject("data");
				OrderUtilParse.parseOrderDetailList(jsonObject, callback);
			}
		});
	}

	/**
	 * 查询订单详情
	 * @param context
	 * @param jsonObject
	 * @param callback
	 */
	public static void getPoolOrderDetail(Context context, String orderId, final ObjectCallback callback){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("orderId", orderId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		CustomProgress.showDialog(context, "数据加载中", false, null);
		RequestUtilNet.postDataToken(context, OrderUtil.pool_detail, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CustomProgress.dissDialog();
				if(success != 0){
					CommandTools.showToast(remark);
					return;
				}

				jsonObject = jsonObject.optJSONObject("data");
				OrderUtilParse.parseOrderDetailPoolList(jsonObject, callback);
			}
		});
	}

	/**
	 * 
	 * @param context
	 * @param takerId
	 * @param callback
	 */
	public static void getAssignOrderDetail(Context context, Long assignId, final ObjectCallback callback){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("assignId", assignId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		CustomProgress.showDialog(context, "数据加载中", false, null);
		RequestUtilNet.postDataToken(context, OrderUtil.assign_detail, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CustomProgress.dissDialog();
				if(success != 0){
					CommandTools.showToast(remark);
					return;
				}

				jsonObject = jsonObject.optJSONObject("data");
				OrderUtilParse.parseAssignOrderDetail(jsonObject, callback);
			}
		});
	}

	/**
	 * 查询收到转单量
	 * @param context
	 * @param callback
	 */
	public static void assignCount(Context context, final ObjectCallback callback){

		RequestUtilNet.postDataToken(context, OrderUtil.assign_count, new JSONObject(), new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				if(success != 0){
					callback.callback(0);
					CommandTools.showToast(remark);
					return;
				}

				int count = jsonObject.optInt("data");

				callback.callback(count);
			}
		});
	}

	/**
	 * 用
	 * 到
	 * 的
	 * 接
	 * 口
	 * */
	public static final String assign_agree = "taker/assign/agree.htm";//同意转单
	public static final String assign_refuse = "taker/assign/refuse.htm";//拒绝转单
	public static final String assign_detail = "taker/assign/detail.htm";//查询转单详情
	public static final String assign_list = "taker/assign/list.htm";//查询转单列表
	public static final String assign_confirm = "taker/assign/confirm.htm";//发起转单请求
	public static final String assign_surplusnum = "taker/assign/surplusnum.htm";//查询转单剩余次数
	public static final String assign_count = "taker/assign/count.htm";//查询收到转单量

	public static final String packet_updateremark = "taker/updateremark.htm";//更新备注
	public static final String packet_selectremark = "taker/selectremark.htm";//查询备注

	public static final String pool_detail = "order/pool/detail.htm";////抢单池详情
	public static final String taker_list = "taker/list.htm";//已抢单列表
	public static final String taker_grab = "taker/grab.htm";//小派抢单(抢单通用接口)
	public static final String taker_ongoing = "taker/ongoing.htm";//更新为进行中
	public static final String taker_complete = "taker/complete.htm";//更新为已完成
	public static final String taker_detail = "taker/detail.htm";//接单后订单详情

	public static final String assign_pusers = "taker/assign/pusers.htm";//查询小派列表

	public static final String taker_submit = "appeal/taker/submit.htm";//小派提交申述
	public static final String taker_revoke = "appeal/taker/revoke.htm";//小派撤销申述

	public static final String taker_add = "appraise/taker/add.htm";//小派评价用户
}
