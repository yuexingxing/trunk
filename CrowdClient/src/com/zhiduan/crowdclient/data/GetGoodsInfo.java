package com.zhiduan.crowdclient.data;

import java.io.Serializable;

public class GetGoodsInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	private String orderId = "";//订单号
	private String waybillCode = "";//运单号
	private String phone = "";
	private String storeName = "";
	private String expressName = "";//快递公司
	private String stack = "";//货位号
	private String pickupCode = "";//取件码
	private String type = "";//订单类型
	private boolean scaned = false;//是否
	
	public String getWaybillCode() {
		return waybillCode;
	}
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}
	public String getExpressName() {
		return expressName;
	}
	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}
	public String getStack() {
		return stack;
	}
	public void setStack(String stack) {
		this.stack = stack;
	}
	public boolean isScaned() {
		return scaned;
	}
	public void setScaned(boolean scaned) {
		this.scaned = scaned;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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
}
