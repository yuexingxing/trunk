package com.zhiduan.crowdclient.data;

public class SignCodeInfo{

	private String orderId;
	private int time = 90000;
	private long currentTimeMills;
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public long getCurrentTimeMills() {
		return currentTimeMills;
	}
	public void setCurrentTimeMills(long currentTimeMills) {
		this.currentTimeMills = currentTimeMills;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	
}
