package com.zhiduan.crowdclient.util;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.data.CrowedUserInfo;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.data.PageInfo;
import com.zhiduan.crowdclient.data.TakerInfo;
import com.zhiduan.crowdclient.data.TaskOrderInfo;
import com.zhiduan.crowdclient.data.TransOrderInfo;
import com.zhiduan.crowdclient.util.OrderUtil.ObjectCallback;
import com.zhiduan.crowdclient.util.OrderUtil.RefreshCallback;
import com.zhiduan.crowdclient.util.OrderUtil.TaskCallBack;
import com.zhiduan.crowdclient.view.DropDownListView;

/** 
 * MVP模式--逻辑处理类
 * 
 * @author yxx
 *
 * @date 2017-1-9 下午1:49:27
 * 
 */
public class OrderUtilParse {

	/**
	 * 刷新加载统一处理数据
	 * @param jsonObjectTotal
	 * @param dataList
	 * @param sortList
	 * @param callBack
	 * @param mAdapter
	 */
	public static void parseOrderList(JSONObject jsonObjectTotal, final List<OrderInfo> dataList, 
			final List<OrderInfo> sortList, RefreshCallback callBack, final BaseAdapter mAdapter){

		//获取数据总条数、页数等
		JSONObject jsonObjectPage = jsonObjectTotal.optJSONObject("queryResult");
		PageInfo pageInfo = new PageInfo();
		pageInfo.setTotalCount(jsonObjectPage.optInt("totalCount"));
		pageInfo.setTotalPageCount(jsonObjectPage.optInt("totalPageCount"));
		pageInfo.setPageNumber(jsonObjectPage.optInt("pageNumber"));
		pageInfo.setPageSize(jsonObjectPage.optInt("pageSize"));
		pageInfo.setOrderBy(jsonObjectPage.optString("orderBy"));

		JSONArray jsonArray1 = new JSONArray();

		try{

			jsonArray1 = jsonObjectTotal.optJSONArray("responseDto");
			for(int i=0; i<jsonArray1.length(); i++){

				JSONObject jsonObject = jsonArray1.optJSONObject(i);
				OrderInfo info = new OrderInfo();
				info.setReceiveIcon("");

				info.setTotalCount(pageInfo.getTotalCount());
				info.setTotalPageCount(pageInfo.getTotalPageCount());
				info.setPageNumber(pageInfo.getPageNumber());
				info.setPageSize(pageInfo.getPageSize());
				info.setOrderBy(pageInfo.getOrderBy());

				info.setOrderNo(jsonObject.optLong("orderId") + "");//订单编号
				info.setTakerId(jsonObject.optLong("takerId") + "");

				info.setDeliveryRequire(jsonObject.optString("descript"));//任务需求
				info.setDeliveryFee(jsonObject.optInt("reward", 0));//派件小费
				info.setArriveFee(jsonObject.optInt("money", 0));//到付金额
				info.setPlusRemark(jsonObject.optString("remark"));

				info.setReceiveName(TextUtils.isEmpty(jsonObject.optString("consignee")) ? "小瓜" : jsonObject.optString("consignee"));//收件人姓名
				info.setReceiveAddress(jsonObject.optString("address"));//收件人地址
				info.setReceivePhone(jsonObject.optString("phone"));//收件人手机号
				info.setReceiveSex(jsonObject.optString("sex"));//收件人性别
				info.setReceiveIcon(jsonObject.optString("icon"));//收件人头像

				info.setState(jsonObject.optInt("state"));
				info.setLockState(jsonObject.optInt("lockState"));
				info.setReportRole(jsonObject.optInt("reportRole", 0));//1:用户提交 2:小派提交 默认0

				info.setDeliveryStartDate(jsonObject.optString("serviceStartDate"));//取件时间
				info.setDeliveryEndDate(jsonObject.optString("serviceEndDate"));//取件时间
				info.setServiceDate(jsonObject.optString("serviceDate"));

				info.setGenderId(jsonObject.optString("genderId"));//性别
				info.setCategoryId(jsonObject.optInt("categoryId") + "");
				info.setCategoryName(jsonObject.optString("categoryName"));

				info.setTheme(jsonObject.optString("theme"));
				info.setNeedPplQty(jsonObject.optInt("needPplQty"));//任务所需人数

				info.setTimeId(i);//id必须设置，否则无法同时刷新
				info.setAssignCode(jsonObject.optInt("assignCode", 0));
				info.setCountdown(CommandTools.getCountTime(jsonObject.optString("assignDate")));//转单时间
				info.setBeEvaluated(jsonObject.optBoolean("beEvaluated"));//是否评价过，完成用
				info.setBuildFlag(jsonObject.optBoolean("buildFlag"));//楼栋标识，true:当前用户所在的楼栋

				dataList.add(info);
			}

			// 校对倒计时
			long curTime = System.currentTimeMillis();
			for (OrderInfo itemInfo : dataList) {
				itemInfo.setEndTime(curTime + itemInfo.getCountdown());
			}

			sortList.clear();
			sortList.addAll(dataList);

			callBack.callback(0, pageInfo.getTotalCount(), pageInfo.getPageSize());
			mAdapter.notifyDataSetChanged();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * 刷新加载统一处理数据---------任务列表
	 * @param jsonObjectTotal
	 * @param dataList
	 * @param sortList
	 * @param callBack
	 * @param mAdapter
	 */
	public static void handleTaskMessage(LinearLayout no_order_layout, DropDownListView listView, int type, JSONObject jsonObjectTotal, final List<TaskOrderInfo> dataList, TaskCallBack callBack, final BaseAdapter mAdapter){

		if(type == OrderUtil.REFRESH_DATA){
			dataList.clear();
		}

		//获取数据总条数、页数等
		JSONObject jsonObjectPage = jsonObjectTotal.optJSONObject("queryResult");
		PageInfo pageInfo = new PageInfo();

		if(jsonObjectPage.has("totalCount")){
			pageInfo.setTotalCount(jsonObjectPage.optInt("totalCount"));
			pageInfo.setTotalPageCount(jsonObjectPage.optInt("totalPageCount"));
			pageInfo.setPageNumber(jsonObjectPage.optInt("pageNumber"));
			pageInfo.setPageSize(jsonObjectPage.optInt("pageSize"));
		}

		JSONArray jsonArray1 = new JSONArray();
		jsonArray1 = jsonObjectTotal.optJSONArray("responseDto");

		int len = jsonArray1.length();
		if (len <= 0) {
			no_order_layout.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			dataList.clear();
			mAdapter.notifyDataSetChanged();
			listView.setPullLoadEnable(false);
		}
		for(int i=0; i<len; i++){

			JSONObject jsonObject = jsonArray1.optJSONObject(i);
			TaskOrderInfo info = new TaskOrderInfo();

			if(jsonObject.has("collegeId")){

				info.setCollegeId(jsonObject.optString("collegeId"));//学校id
				info.setCreateDate(jsonObject.optString("createDate"));
				info.setDeliveryEndDate(jsonObject.optString("deliveryEndDate"));
				info.setDeliveryStartDate(jsonObject.optString("deliveryStartDate"));
				info.setDescript(jsonObject.optString("descript"));
				info.setIcon(jsonObject.optString("icon"));
				info.setIncome(jsonObject.optInt("income"));
				info.setLevel(jsonObject.optString("level"));
				info.setMobile(jsonObject.optString("mobile"));
				info.setName(jsonObject.optString("name"));
				info.setOrderId(jsonObject.optString("orderId"));
				info.setSex(jsonObject.optString("sex"));
				info.setTheme(jsonObject.optString("theme"));
				info.setType(jsonObject.optString("type"));

				dataList.add(info);
			}
		}

		mAdapter.notifyDataSetChanged();
		callBack.callback(0, len, pageInfo.getPageSize());
	}

	public static void parseTransList(JSONObject jsonObjectTotal, final List<TransOrderInfo> dataList, final List<TransOrderInfo> sortList, 
			final BaseAdapter mAdapter, final RefreshCallback callBack){

		JSONObject jsonObjectPage = jsonObjectTotal.optJSONObject("queryResult");
		PageInfo pageInfo = new PageInfo();

		if(jsonObjectPage.has("totalCount")){
			pageInfo.setTotalCount(jsonObjectPage.optInt("totalCount"));
			pageInfo.setTotalPageCount(jsonObjectPage.optInt("totalPageCount"));
			pageInfo.setPageNumber(jsonObjectPage.optInt("pageNumber"));
			pageInfo.setPageSize(jsonObjectPage.optInt("pageSize"));
		}

		JSONArray jsonArray1 = new JSONArray();
		jsonArray1 = jsonObjectTotal.optJSONArray("responseDto");

		int len = jsonArray1.length();
		for(int i=0; i<len; i++){

			JSONObject jsonObject = jsonArray1.optJSONObject(i);
			TransOrderInfo info = new TransOrderInfo();

			if(jsonObject.has("takerId")){

				info.setCategoryId(jsonObject.optInt("categoryId")+"");
				info.setTakerId(jsonObject.optString("takerId"));//学校id
				info.setAssignId(jsonObject.optString("assignId"));
				info.setTheme(jsonObject.optString("theme"));
				info.setState(jsonObject.optInt("state"));
				info.setAssignUser(jsonObject.optString("assignName"));
				info.setAssignMobile(jsonObject.optString("assignMobile"));
				info.setReward(jsonObject.optLong("reward"));
				info.setAssignIcon(jsonObject.optString("icon"));
				info.setServiceEndDate(jsonObject.optString("serviceEndDate"));
				info.setServiceStartDate(jsonObject.optString("serviceStartDate"));

				info.setTimeId(i);//id必须设置，否则无法同时刷新
				info.setCountdown(CommandTools.getCountTime(jsonObject.optString("assignDate")));

				dataList.add(info);
			}
		}

		// 校对倒计时
		long curTime = System.currentTimeMillis();
		for (TransOrderInfo itemInfo : dataList) {
			itemInfo.setEndTime(curTime + itemInfo.getCountdown());
		}

		sortList.clear();
		sortList.addAll(dataList);

		callBack.callback(0, pageInfo.getTotalCount(), pageInfo.getPageSize());
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 处理订单详情
	 * 返回OrderInfo对象
	 * @param jsonObject
	 * @param callback
	 */
	public static void parseOrderDetailList(JSONObject jsonObject, ObjectCallback callback){

		OrderInfo info = new OrderInfo();

		info.setOrderNo(jsonObject.optString("orderId"));
		info.setTakerId(jsonObject.optString("takerId"));
		info.setType(jsonObject.optString("type"));

		info.setDeliveryName(TextUtils.isEmpty(jsonObject.optString("consignee")) ? "小瓜" : jsonObject.optString("consignee"));

		info.setReceiveName(jsonObject.optString("contactName"));
		info.setReceiveId(jsonObject.optInt("createUser"));
		info.setReceivePhone(jsonObject.optString("createUserMobile"));//下单人手机号
		info.setRemark(jsonObject.optString("remark"));//小派备注
		info.setReceiveIcon(jsonObject.optString("icon"));

		info.setNeedPplQty(jsonObject.optInt("needPplQty"));//任务所需人数
		info.setGrabedNumber(jsonObject.optInt("grabedNumber"));//当前接单人数

		info.setTheme(jsonObject.optString("theme"));
		info.setGenderId(jsonObject.optString("genderId"));
		info.setReceiveSex(jsonObject.optString("genderId"));
		info.setDeliveryStartDate(jsonObject.optString("serviceStartDate"));
		info.setDeliveryEndDate(jsonObject.optString("serviceEndDate"));
		info.setServiceDate(jsonObject.optString("serviceDate"));

		info.setCategoryId(jsonObject.optInt("categoryId") + "");
		info.setCategoryName(jsonObject.optString("categoryName"));
		info.setState(jsonObject.optInt("state"));
		info.setLockState(jsonObject.optInt("lockState"));
		info.setReportRole(jsonObject.optInt("reportRole", 0));//1:用户提交 2:小派提交 默认0

		info.setBeEvaluated(jsonObject.optBoolean("beEvaluated"));//是否评价过，完成用
		info.setGoodsFee(jsonObject.optLong("itemPrice"));//商品详情
		info.setPlusRemark(jsonObject.optString("plusRemark"));//加成信息

		info.setTraderName(jsonObject.optString("traderName"));
		info.setTraderMobile(jsonObject.optString("traderMobile"));
		info.setTraderAddress(jsonObject.optString("traderAddress"));

		info.setStoreAddress(jsonObject.optString("theme").toString().replace("\n", ""));//任务需求
		info.setDeliveryRequire(jsonObject.optString("orderRemark").toString().replace("\n", ""));//任务要求
		info.setReceiveAddress(jsonObject.optString("destination"));
		info.setDeliveryFee(jsonObject.optInt("reward"));

		info.setReceiveUserName(jsonObject.optString("receiveUserName"));
		info.setReceiveUserMobile(jsonObject.optString("receiveUserMobile"));

		//		if(info.getCategoryId().equals(OrderUtil.ORDER_TYPE_ZIYING)){
		//			info.setStoreAddress(jsonObject.optString("storeAddress"));
		//		}

		info.setTimeId(100);//id必须设置，否则无法同时刷新
		info.setCountdown(CommandTools.getCountTime(jsonObject.optString("assignDate")));//转单时间

		ArrayList<TakerInfo> takerList = new ArrayList<TakerInfo>();
		if(!TextUtils.isEmpty(jsonObject.optString("takerList"))){//接单人列表

			JSONArray jsonArray = jsonObject.optJSONArray("takerList");
			for(int i=0; i<jsonArray.length(); i++){

				JSONObject jsonObject2 = jsonArray.optJSONObject(i);
				TakerInfo takerInfo = new TakerInfo();
				takerInfo.setIcon(jsonObject2.optString("icon"));
				takerInfo.setName(jsonObject2.optString("name"));
				takerInfo.setUserId(jsonObject2.optInt("userId"));

				takerList.add(takerInfo);
			}
		}
		info.setTakerList(takerList);

		ArrayList<String> taskIconList = new ArrayList<String>();
		if(!TextUtils.isEmpty(jsonObject.optString("taskIconList"))){//任务图片列表

			JSONArray jsonArray = jsonObject.optJSONArray("taskIconList");
			for(int i=0; i<jsonArray.length(); i++){

				taskIconList.add(jsonArray.optString(i));
			}
		}
		info.setTaskIconList(taskIconList);

		callback.callback(info);
	}

	/**
	 * 处理订单详情
	 * 返回OrderInfo对象
	 * @param jsonObject
	 * @param callback
	 */
	public static void parseOrderDetailPoolList(JSONObject jsonObject, ObjectCallback callback){

		OrderInfo info = new OrderInfo();

		info.setOrderNo(jsonObject.optString("orderId"));
		info.setTakerId(jsonObject.optString("takerId"));
		info.setType(jsonObject.optString("type"));

		info.setDeliveryName(TextUtils.isEmpty(jsonObject.optString("name")) ? "小瓜" : jsonObject.optString("name"));

		info.setReceiveName(jsonObject.optString("contactName"));
		info.setReceivePhone(jsonObject.optString("contactMobile"));//下单人手机号
		info.setRemark(jsonObject.optString("remark"));//小派备注
		info.setReceiveIcon(jsonObject.optString("icon"));

		info.setNeedPplQty(jsonObject.optInt("needPplQty"));//任务所需人数
		info.setGrabedNumber(jsonObject.optInt("grabedNumber"));//当前接单人数

		info.setTheme(jsonObject.optString("theme"));
		info.setGenderId(jsonObject.optString("sex"));
		info.setDeliveryStartDate(jsonObject.optString("serviceStartDate"));
		info.setDeliveryEndDate(jsonObject.optString("serviceEndDate"));
		info.setServiceDate(jsonObject.optString("serviceDate"));

		info.setCategoryId(jsonObject.optInt("categoryId") + "");
		info.setCategoryName(jsonObject.optString("categoryName"));
		info.setLockState(jsonObject.optInt("lockState"));
		info.setState(jsonObject.optInt("state"));

		info.setBeEvaluated(jsonObject.optBoolean("beEvaluated"));//是否评价过，完成用
		info.setGoodsFee(jsonObject.optLong("itemPrice"));//商品详情
		info.setPlusRemark(jsonObject.optString("plusRemark"));//加成信息
		info.setDeliveryFee(jsonObject.optInt("income"));

		info.setTraderName(jsonObject.optString("traderName"));
		info.setTraderMobile(jsonObject.optString("traderMobile"));
		info.setTraderAddress(jsonObject.optString("traderAddress"));

		//		任务要求descript
		//		任务需求taskRequirement
		//		收件人地址用address
		//		取货地址用traderAddress

		info.setReceiveAddress(jsonObject.optString("address"));//收件人地址
		info.setDeliveryRequire(jsonObject.optString("descript").toString().replace("\n", ""));//任务要求

		if(info.getCategoryId().startsWith(OrderUtil.ORDER_TYPE_PACKET)){
			info.setStoreAddress(jsonObject.optString("theme").toString().replace("\n", ""));//任务需求
		}else if(info.getCategoryId().startsWith(OrderUtil.ORDER_TYPE_AGENT)){
			info.setStoreAddress(jsonObject.optString("theme").toString().replace("\n", ""));//任务需求
		}else{
			info.setStoreAddress(jsonObject.optString("descript").toString().replace("\n", ""));//任务需求
		}

		info.setTimeId(100);//id必须设置，否则无法同时刷新
		info.setCountdown(CommandTools.getCountTime(jsonObject.optString("assignDate")));//转单时间

		ArrayList<TakerInfo> takerList = new ArrayList<TakerInfo>();
		if(!TextUtils.isEmpty(jsonObject.optString("takerList"))){//接单人列表

			JSONArray jsonArray = jsonObject.optJSONArray("takerList");
			for(int i=0; i<jsonArray.length(); i++){

				JSONObject jsonObject2 = jsonArray.optJSONObject(i);
				TakerInfo takerInfo = new TakerInfo();
				takerInfo.setIcon(jsonObject2.optString("icon"));
				takerInfo.setName(jsonObject2.optString("name"));
				takerInfo.setUserId(jsonObject2.optInt("userId"));

				takerList.add(takerInfo);
			}
		}
		info.setTakerList(takerList);

		ArrayList<String> taskIconList = new ArrayList<String>();
		if(!TextUtils.isEmpty(jsonObject.optString("taskIconList"))){//任务图片列表

			JSONArray jsonArray = jsonObject.optJSONArray("taskIconList");
			for(int i=0; i<jsonArray.length(); i++){

				taskIconList.add(jsonArray.optString(i));
			}
		}
		info.setTaskIconList(taskIconList);

		callback.callback(info);
	}

	/**
	 * 分单详情
	 * @param jsonObject
	 * @param callback
	 */
	public static void parseAssignOrderDetail(JSONObject jsonObject, ObjectCallback callback){

		OrderInfo info = new OrderInfo();

		info.setAssignName(jsonObject.optString("assignName"));
		info.setAssignDate(jsonObject.optString("assignDate"));
		info.setAssignMobile(jsonObject.optString("assignMobile"));
		info.setAssignIcon(jsonObject.optString("icon"));
		info.setDeliveryStartDate(jsonObject.optString("serviceStartDate"));
		info.setDeliveryEndDate(jsonObject.optString("serviceEndDate"));
		info.setTheme(jsonObject.optString("theme"));
		info.setOrderNo(jsonObject.optString("orderId"));
		info.setCategoryName(jsonObject.optString("categoryName"));
		info.setCategoryId(jsonObject.optString("categoryId"));
		info.setDeliveryFee(jsonObject.optLong("reward"));
		info.setRemark(jsonObject.optString("remark"));
		info.setGenderId(jsonObject.optString("genderId"));

		info.setNeedPplQty(jsonObject.optInt("needPplQty"));//
		info.setGrabedNumber(jsonObject.optInt("receivedCount"));//

		info.setTraderName(jsonObject.optString("traderName"));
		info.setTraderMobile(jsonObject.optString("traderMobile"));
		info.setTraderAddress(jsonObject.optString("traderAddress"));

		ArrayList<String> taskIconList = new ArrayList<String>();
		if(!TextUtils.isEmpty(jsonObject.optString("receivedUserIcon"))){//任务图片列表

			JSONArray jsonArray = jsonObject.optJSONArray("receivedUserIcon");
			for(int i=0; i<jsonArray.length(); i++){

				taskIconList.add(jsonArray.optString(i));
			}
		}
		info.setTaskIconList(taskIconList);

		callback.callback(info);
	}

	public static void parseCrowedUserList(JSONObject jsonObject, final BaseAdapter mAdapter, final List<CrowedUserInfo> dataList, final List<CrowedUserInfo> sortList, final RefreshCallback callBack){

		JSONObject jsonObjectTotal = jsonObject.optJSONObject("data");

		//获取数据总条数、页数等
		JSONObject jsonObjectPage = jsonObjectTotal.optJSONObject("queryResult");
		PageInfo pageInfo = new PageInfo();
		pageInfo.setTotalCount(jsonObjectPage.optInt("totalCount"));
		pageInfo.setTotalPageCount(jsonObjectPage.optInt("totalPageCount"));
		pageInfo.setPageNumber(jsonObjectPage.optInt("pageNumber"));
		pageInfo.setPageSize(jsonObjectPage.optInt("pageSize"));
		pageInfo.setOrderBy(jsonObjectPage.optString("orderBy"));

		JSONArray jsonArray1 = new JSONArray();
		jsonArray1 = jsonObjectTotal.optJSONArray("responseDto");
		for(int i=0; i<jsonArray1.length(); i++){

			jsonObject = jsonArray1.optJSONObject(i);
			CrowedUserInfo info = new CrowedUserInfo();

			info.setCollegeId(jsonObject.optString("collegeId"));
			info.setGender(jsonObject.optString("gender"));
			info.setGenderId(jsonObject.optString("genderId"));
			info.setIcon(jsonObject.optString("icon"));
			info.setPhone(jsonObject.optString("phone"));
			info.setRealName(jsonObject.optString("realName"));
			info.setUserId(jsonObject.optString("userId"));

			//小派自己不显示在列表中
			if(info.getPhone().equals(MyApplication.getInstance().m_userInfo.m_strUserPhone)){
				continue;
			}

			dataList.add(info);
		}

		sortList.clear();
		sortList.addAll(dataList);

		callBack.callback(0, pageInfo.getTotalCount(), pageInfo.getPageSize());
		mAdapter.notifyDataSetChanged();
	}
}
