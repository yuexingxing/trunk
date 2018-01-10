package com.zhiduan.crowdclient.data;

public class TransOrderInfo {

	private String categoryId;//订单类型
	private String assignDate;//转单日期
	private String assignId = "1000";//转单ID
	private String assignIcon;
	private String assignMobile;//转单人手机号
	private String assignUser;//转单人姓名
	private String orderId;//订单号
	private Long reward;//打赏金额
	private String serviceEndDate;//派件结束时间
	private String serviceStartDate;//派件开始时间
	private String sex;//收件人性别
	private int state;//订单状态
	private int lockState;//锁定 0:未锁定 1:众包转单锁定 2:申诉锁定
	private String takerId;//接单ID
	private String theme;//
	private String checked = "";// 是否已选中
	
	private int timeId;
	private long countdown;
	private long endTime;

	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getAssignDate() {
		return assignDate;
	}
	public void setAssignDate(String assignDate) {
		this.assignDate = assignDate;
	}
	public String getAssignId() {
		return assignId;
	}
	public void setAssignId(String assignId) {
		this.assignId = assignId;
	}
	public String getAssignMobile() {
		return assignMobile;
	}
	public void setAssignMobile(String assignMobile) {
		this.assignMobile = assignMobile;
	}
	public String getAssignUser() {
		return assignUser;
	}
	public void setAssignUser(String assignUser) {
		this.assignUser = assignUser;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Long getReward() {
		return reward;
	}
	public void setReward(Long reward) {
		this.reward = reward;
	}
	public String getServiceEndDate() {
		return serviceEndDate;
	}
	public void setServiceEndDate(String serviceEndDate) {
		this.serviceEndDate = serviceEndDate;
	}
	public String getServiceStartDate() {
		return serviceStartDate;
	}
	public void setServiceStartDate(String serviceStartDate) {
		this.serviceStartDate = serviceStartDate;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getTakerId() {
		return takerId;
	}
	public void setTakerId(String takerId) {
		this.takerId = takerId;
	}
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	public String getChecked() {
		return checked;
	}
	public void setChecked(String checked) {
		this.checked = checked;
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
	public String getAssignIcon() {
		return assignIcon;
	}
	public void setAssignIcon(String assignIcon) {
		this.assignIcon = assignIcon;
	}
	public int getLockState() {
		return lockState;
	}
	public void setLockState(int lockState) {
		this.lockState = lockState;
	}

}
