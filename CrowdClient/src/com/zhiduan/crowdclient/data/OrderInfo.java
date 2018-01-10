package com.zhiduan.crowdclient.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 订单对应属性
 * 
 * @author yxx
 * 
 * @date 2016-5-24 下午5:21:55
 * 
 */
public class OrderInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cacheid = "";// 唯一标识
	private String orderNo = "";// 订单号
	private String takerId = "";
	private String billcode = "";// 单号
	private long waybillId;// 运单Id
	private String distance = "0";// 距离
	private int beVirtual;

	private String order_type; // 订单类型
	private String order_theme; // 订单主题 寄，派，任务
	private String task_level;  //任务订单级别等级
	private String deliveryName = "";// 派件人
	private String deliveryPhone = "";// 派件人联系方式
	private String deliveryTime = "";// 派件时间
	private String deliveryRequire = "";// 派件要求
	private long deliveryFee = 0;// 派件小费
	private long goodsFee = 0;//商品金额
	private String deliveryAddress = "";// 派件地址，站点地址
	private String deliverySex = ""; // 收件人性别
	private String sendAddress = ""; // 取件地址

	private int receiveId; // 下单人Id
	private String receiveName = "";// 收件人
	private String receivePhone = "";// 收件联系方式
	private String receiveAddress = "";// 收件人地址
	private String receiveSex = "";// 收件人性别
	private String genderId = "";//p_gender_male,p_gender_female
	private String receiveTime = "";// 收件时间
	private String receiveIcon = "";// 收件人头像

	private String signTime = "";// 签收时间
	private String pickTime = "";// 签收时间 今天
	private String pickTime2 = "";// 签收时间

	private String applyForTime = "";// 申请时间
	private String senderTime = "";// 寄件时间
	private long arriveFee = 0;// 到付金额

	private String stack = "";// 货位号
	private String checked = "";// 是否已选中
	private int orderStatus = 0;// 订单状态
	private String type = "";// 类型

	private String grabTime;// 抢单时间
	private String adminSendTime;// 系统发单时间

	private int totalCount = 0;// 总条数
	private int totalPageCount = 0;// 总页数
	private int pageNumber = 0;// 当前页
	private int pageSize = 0;// 每页条数
	private String orderBy = "";// 排序规则

	private String expressName = "";// 快递公司名称
	private String expressLogo = "";// 快递公司图标

	private boolean scaned = false;// 是否已扫描

	private int callCount = 0;// 拨打电话次数
	private int payMode;// 支付类型1:预支付2：货到付款
	private String assignId; // 分单ID
	private String payTypeDesc;// 支付方式，支付宝、微信
	private long finalMoney;// 最终收款金额

	private String storeName;// 商户名称
	private String pickupCode;// 取件码
	private String collegeId;// 学校编码

	private String deliveryEndDate;// 取件结束时间
	private String deliveryStartDate;// 取件开始时间
	private String sendIcon = "";// 寄件人头像
	private String signedTime;// 寄件时间

	private String trader;//
	private String problemReason;// 退件原因
	private String storeAddress;// 退件原因
	private String createTime;// 退件原因
	private String traderSex;//
	private String phone;//
	private String traderIcon;//
	private int state;// 退件状态1:创建2:已接单,3:配送中,5:完成,6:取消,7:异常
	private int lockState;//锁定 0:未锁定 1:众包转单锁定 2:申诉锁定
	private int reportRole;//1-用户提交  2-小派提交 默认为0，未申诉

	private String assignDate;
	private String assignName;
	private String assignIcon;
	private String assignMobile;
	private int assignCode;//分单编码

	private String remark = "";//小派备注

	private int timeId;
	private long countdown;
	private long endTime;

	private String categoryName;//派件、跑腿
	private String categoryId;   //订单类型id

	private int needPplQty = 1;//1:单人任务，>1：多人任务
	private int grabedNumber = 0;//已接单人数
	private boolean beEvaluated = false;//小派是否评价过
	private boolean buildFlag = false;//是否是本栋楼
	private String theme;
	private ArrayList<TakerInfo> takerList = new ArrayList<TakerInfo>();//接单人列表
	private ArrayList<String> taskIconList = new ArrayList<String>();//任务图片列表

	private String plusRemark;//加成信息

	private String traderName;
	private String traderMobile;
	private String traderAddress;

	private String receiveUserName;//转单人姓名
	private String receiveUserMobile;//转单人手机号
	
	private String serviceDate = "";//服务时间

	public int getReceiveId() {
		return receiveId;
	}

	public void setReceiveId(int receiveId) {
		this.receiveId = receiveId;
	}

	public String getTakerId() {
		return takerId;
	}

	public void setTakerId(String takerId) {
		this.takerId = takerId;
	}

	public int getBeVirtual() {
		return beVirtual;
	}

	public void setBeVirtual(int beVirtual) {
		this.beVirtual = beVirtual;
	}

	public String getTask_level() {
		return task_level;
	}

	public void setTask_level(String task_level) {
		this.task_level = task_level;
	}

	public String getTraderIcon() {
		return traderIcon;
	}

	public String getStoreAddress() {
		return storeAddress;
	}

	public void setStoreAddress(String storeAddress) {
		this.storeAddress = storeAddress;
	}

	public void setTraderIcon(String traderIcon) {
		this.traderIcon = traderIcon;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getState() {
		return state;
	}

	public String getTraderSex() {
		return traderSex;
	}

	public void setTraderSex(String traderSex) {
		this.traderSex = traderSex;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getProblemReason() {
		return problemReason;
	}

	public void setProblemReason(String problemReason) {
		this.problemReason = problemReason;
	}

	public String getTrader() {
		return trader;
	}

	public void setTrader(String trader) {
		this.trader = trader;
	}

	public String getSignedTime() {
		return signedTime;
	}

	public void setSignedTime(String signedTime) {
		this.signedTime = signedTime;
	}

	public String getSendIcon() {
		return sendIcon;
	}

	public void setSendIcon(String sendIcon) {
		this.sendIcon = sendIcon;
	}

	public String getOrder_theme() {
		return order_theme;
	}

	public String getDeliveryEndDate() {
		return deliveryEndDate;
	}

	public void setDeliveryEndDate(String deliveryEndDate) {
		this.deliveryEndDate = deliveryEndDate;
	}

	public String getDeliveryStartDate() {
		return deliveryStartDate;
	}

	public void setDeliveryStartDate(String deliveryStartDate) {
		this.deliveryStartDate = deliveryStartDate;
	}

	public String getSendAddress() {
		return sendAddress;
	}

	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
	}

	public void setOrder_theme(String order_theme) {
		this.order_theme = order_theme;
	}

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public String getApplyForTime() {
		return applyForTime;
	}

	public void setApplyForTime(String applyForTime) {
		this.applyForTime = applyForTime;
	}

	public String getSenderTime() {
		return senderTime;
	}

	public void setSenderTime(String senderTime) {
		this.senderTime = senderTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPickTime() {
		return pickTime;
	}

	public void setPickTime(String pickTime) {
		this.pickTime = pickTime;
	}

	public String getPickTime2() {
		return pickTime2;
	}

	public void setPickTime2(String pickTime2) {
		this.pickTime2 = pickTime2;
	}

	public OrderInfo() {

	}

	public String getAssignId() {
		return assignId;
	}

	public void setAssignId(String assignId) {
		this.assignId = assignId;
	}

	public String getDeliverySex() {
		return deliverySex;
	}

	public void setDeliverySex(String deliverySex) {
		this.deliverySex = deliverySex;
	}

	public long getArriveFee() {
		return arriveFee;
	}

	public void setArriveFee(long arriveFee) {
		this.arriveFee = arriveFee;
	}

	public String getCacheid() {
		return cacheid;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public void setCacheid(String cacheid) {
		this.cacheid = cacheid;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getDeliveryName() {
		return deliveryName;
	}

	public void setDeliveryName(String deliveryName) {
		this.deliveryName = deliveryName;
	}

	public String getDeliveryPhone() {
		return deliveryPhone;
	}

	public void setDeliveryPhone(String deliveryPhone) {
		this.deliveryPhone = deliveryPhone;
	}

	public String getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getDeliveryRequire() {
		return deliveryRequire;
	}

	public void setDeliveryRequire(String deliveryRequire) {
		this.deliveryRequire = deliveryRequire;
	}

	public String getReceiveName() {
		return receiveName;
	}

	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}

	public String getReceivePhone() {
		return receivePhone;
	}

	public int getAssignCode() {
		return assignCode;
	}

	public void setAssignCode(int assignCode) {
		this.assignCode = assignCode;
	}

	public void setReceivePhone(String receivePhone) {
		this.receivePhone = receivePhone;
	}

	public String getReceiveAddress() {
		return receiveAddress;
	}

	public void setReceiveAddress(String receiveAddress) {
		this.receiveAddress = receiveAddress;
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getReceiveIcon() {
		return receiveIcon;
	}

	public void setReceiveIcon(String receiveIcon) {
		this.receiveIcon = receiveIcon;
	}

	public String getStack() {
		return stack;
	}

	public void setStack(String stack) {
		this.stack = stack;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public int getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getGrabTime() {
		return grabTime;
	}

	public void setGrabTime(String grabTime) {
		this.grabTime = grabTime;
	}

	public String getAdminSendTime() {
		return adminSendTime;
	}

	public void setAdminSendTime(String adminSendTime) {
		this.adminSendTime = adminSendTime;
	}

	public String getSignTime() {
		return signTime;
	}

	public void setSignTime(String signTime) {
		this.signTime = signTime;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPageCount() {
		return totalPageCount;
	}

	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public long getDeliveryFee() {
		return deliveryFee;
	}

	public void setDeliveryFee(long deliveryFee) {
		this.deliveryFee = deliveryFee;
	}

	public String getBillcode() {
		return billcode;
	}

	public void setBillcode(String billcode) {
		this.billcode = billcode;
	}

	public String getExpressName() {
		return expressName;
	}

	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}

	public boolean isScaned() {
		return scaned;
	}

	public void setScaned(boolean scaned) {
		this.scaned = scaned;
	}

	public int getCallCount() {
		return callCount;
	}

	public void setCallCount(int callCount) {
		this.callCount = callCount;
	}

	public String getReceiveSex() {
		return receiveSex;
	}

	public void setReceiveSex(String receiveSex) {
		this.receiveSex = receiveSex;
	}

	public long getWaybillId() {
		return waybillId;
	}

	public void setWaybillId(long waybillId) {
		this.waybillId = waybillId;
	}

	public int getPayMode() {
		return payMode;
	}

	public void setPayMode(int payMode) {
		this.payMode = payMode;
	}

	public String getPayTypeDesc() {
		return payTypeDesc;
	}

	public void setPayTypeDesc(String payTypeDesc) {
		this.payTypeDesc = payTypeDesc;
	}

	public long getFinalMoney() {
		return finalMoney;
	}

	public void setFinalMoney(long finalMoney) {
		this.finalMoney = finalMoney;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getPickupCode() {
		return pickupCode;
	}

	public void setPickupCode(String pickupCode) {
		this.pickupCode = pickupCode;
	}

	public String getCollegeId() {
		return collegeId;
	}

	public void setCollegeId(String collegeId) {
		this.collegeId = collegeId;
	}

	public String getExpressLogo() {
		return expressLogo;
	}

	public void setExpressLogo(String expressLogo) {
		this.expressLogo = expressLogo;
	}

	public int getLockState() {
		return lockState;
	}

	public void setLockState(int lockState) {
		this.lockState = lockState;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getGenderId() {
		return genderId;
	}

	public void setGenderId(String genderId) {
		this.genderId = genderId;
	}

	public int getTimeId() {
		return timeId;
	}

	public void setTimeId(int timeId) {
		this.timeId = timeId;
	}

	public long getCountdown() {
		return countdown;
	}

	public void setCountdown(long countdown) {
		this.countdown = countdown;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public long getGoodsFee() {
		return goodsFee;
	}

	public void setGoodsFee(long goodsFee) {
		this.goodsFee = goodsFee;
	}

	public int getNeedPplQty() {
		return needPplQty;
	}

	public void setNeedPplQty(int needPplQty) {
		this.needPplQty = needPplQty;
	}

	public boolean isBeEvaluated() {
		return beEvaluated;
	}

	public void setBeEvaluated(boolean beEvaluated) {
		this.beEvaluated = beEvaluated;
	}

	public boolean isBuildFlag() {
		return buildFlag;
	}

	public void setBuildFlag(boolean buildFlag) {
		this.buildFlag = buildFlag;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public ArrayList<TakerInfo> getTakerList() {
		return takerList;
	}

	public void setTakerList(ArrayList<TakerInfo> takerList) {
		this.takerList = takerList;
	}

	public ArrayList<String> getTaskIconList() {
		return taskIconList;
	}

	public void setTaskIconList(ArrayList<String> taskIconList) {
		this.taskIconList = taskIconList;
	}

	public int getGrabedNumber() {
		return grabedNumber;
	}

	public void setGrabedNumber(int grabedNumber) {
		this.grabedNumber = grabedNumber;
	}

	public String getAssignDate() {
		return assignDate;
	}

	public void setAssignDate(String assignDate) {
		this.assignDate = assignDate;
	}

	public String getAssignName() {
		return assignName;
	}

	public void setAssignName(String assignName) {
		this.assignName = assignName;
	}

	public String getAssignMobile() {
		return assignMobile;
	}

	public void setAssignMobile(String assignMobile) {
		this.assignMobile = assignMobile;
	}

	public String getAssignIcon() {
		return assignIcon;
	}

	public void setAssignIcon(String assignIcon) {
		this.assignIcon = assignIcon;
	}

	public String getPlusRemark() {
		return plusRemark;
	}

	public void setPlusRemark(String plusRemark) {
		this.plusRemark = plusRemark;
	}

	public String getTraderName() {
		return traderName;
	}

	public void setTraderName(String traderName) {
		this.traderName = traderName;
	}

	public String getTraderMobile() {
		return traderMobile;
	}

	public void setTraderMobile(String traderMobile) {
		this.traderMobile = traderMobile;
	}

	public String getTraderAddress() {
		return traderAddress;
	}

	public void setTraderAddress(String traderAddress) {
		this.traderAddress = traderAddress;
	}

	public String getReceiveUserName() {
		return receiveUserName;
	}

	public void setReceiveUserName(String receiveUserName) {
		this.receiveUserName = receiveUserName;
	}

	public String getReceiveUserMobile() {
		return receiveUserMobile;
	}

	public void setReceiveUserMobile(String receiveUserMobile) {
		this.receiveUserMobile = receiveUserMobile;
	}

	public int getReportRole() {
		return reportRole;
	}

	public void setReportRole(int reportRole) {
		this.reportRole = reportRole;
	}

	public String getServiceDate() {
		return serviceDate;
	}

	public void setServiceDate(String serviceDate) {
		this.serviceDate = serviceDate;
	}
}
