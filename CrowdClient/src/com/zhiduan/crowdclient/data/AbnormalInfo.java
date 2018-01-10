package com.zhiduan.crowdclient.data;

import java.io.Serializable;

public class AbnormalInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	private String icon = "";//头像
	private String name;
	private String phone;
	private String sex;
	private String createTime;//	异常件登记时间	string	
	private String createUserName;//	派件人姓名	string	
	private String ecName;//	快递公司名称	string	
	private String imgUrl = "";//异常件/退件图片URL	string	已退件返回退件图片，否则返回异常图片
	private String thumbnailUrl = "";//异常图片缩略图
	private long problemId;//	异常件ID	number	
	private String problemReason;//	异常件原因	string	
	
	private String problemTypeCode;//异常类型编码
	private String problemTypeName;//	异常件类型	string	
	
	private String type;//订单类型，派件，代取件
	private String state;//	异常件状态	string	
	
	private String orderId;//订单号
	private String  waybillNo;//
	private String storeName;//商铺名称
	
	private int pageNumber;//	当前页数	number	@mock=1
	private int pageSize;//	每页显示条数	number	@mock=10
	private int totalCount;//	总条数	number	@mock=3
	private int  totalPageCount;//	总页数	number	@mock=1
	
	private String assignCode;//分单编码
	
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public String getEcName() {
		return ecName;
	}
	public void setEcName(String ecName) {
		this.ecName = ecName;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public String getProblemReason() {
		return problemReason;
	}
	public void setProblemReason(String problemReason) {
		this.problemReason = problemReason;
	}
	public String getProblemTypeName() {
		return problemTypeName;
	}
	public void setProblemTypeName(String problemTypeName) {
		this.problemTypeName = problemTypeName;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getWaybillNo() {
		return waybillNo;
	}
	public void setWaybillNo(String waybillNo) {
		this.waybillNo = waybillNo;
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
	public long getProblemId() {
		return problemId;
	}
	public void setProblemId(long problemId) {
		this.problemId = problemId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getProblemTypeCode() {
		return problemTypeCode;
	}
	public void setProblemTypeCode(String problemTypeCode) {
		this.problemTypeCode = problemTypeCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getAssignCode() {
		return assignCode;
	}
	public void setAssignCode(String assignCode) {
		this.assignCode = assignCode;
	}
	
}
