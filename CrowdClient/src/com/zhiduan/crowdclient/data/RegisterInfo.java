package com.zhiduan.crowdclient.data;

import java.io.Serializable;

/**
 * 
 * <pre>
 * Description  用户注册信息
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-6-2 上午10:15:35  
 * </pre>
 */
public class RegisterInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private String phone = "";//手机号
	private String storeId = "";//后台分配的
	private String storeCode = "";//网点编号
	private String storeName = "";//网点名称
	private String expressCompanyId = "";//快递公司编号
	private String expressName = "";//快递公司名称
	private String realName = ""; //真实姓名
	private String identityNumber = "";//身份证号
	private String idCardHandPic = ""; //手持身份证照
	private String idCardPic = "";//身份证正面照
	private String collegeArea = ""; //大学校区
	private String collegeAddress;
	private String dormitoryAddress;
	private String location = ""; //
	private String isCustomer = "0";//是否是客户经理推荐
	
	
	
	/**
	 * @return the {@link #collegeArea}
	 */
	public String getCollegeArea() {
		return collegeArea;
	}
	/**
	 * @param collegeArea the {@link #collegeArea} to set
	 */
	public void setCollegeArea(String collegeArea) {
		this.collegeArea = collegeArea;
	}
	/**
	 * @return the {@link #collegeAddress}
	 */
	public String getCollegeAddress() {
		return collegeAddress;
	}
	/**
	 * @param collegeAddress the {@link #collegeAddress} to set
	 */
	public void setCollegeAddress(String collegeAddress) {
		this.collegeAddress = collegeAddress;
	}
	/**
	 * @return the {@link #dormitoryAddress}
	 */
	public String getDormitoryAddress() {
		return dormitoryAddress;
	}
	/**
	 * @param dormitoryAddress the {@link #dormitoryAddress} to set
	 */
	public void setDormitoryAddress(String dormitoryAddress) {
		this.dormitoryAddress = dormitoryAddress;
	}
	public String getStoreCode() {
		return storeCode;
	}
	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getExpressCompanyId() {
		return expressCompanyId;
	}
	public void setExpressCompanyId(String expressCompanyId) {
		this.expressCompanyId = expressCompanyId;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getIdentityNumber() {
		return identityNumber;
	}
	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}
	public String getIdCardHandPic() {
		return idCardHandPic;
	}
	public void setIdCardHandPic(String idCardHandPic) {
		this.idCardHandPic = idCardHandPic;
	}
	public String getIdCardPic() {
		return idCardPic;
	}
	public void setIdCardPic(String idCardPic) {
		this.idCardPic = idCardPic;
	}
	
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	public String getExpressName() {
		return expressName;
	}
	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getIsCustomer() {
		return isCustomer;
	}
	public void setIsCustomer(String isCustomer) {
		this.isCustomer = isCustomer;
	}
	
}
