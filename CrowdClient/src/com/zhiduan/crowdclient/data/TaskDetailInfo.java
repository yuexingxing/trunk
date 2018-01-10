package com.zhiduan.crowdclient.data;

import java.io.Serializable;

/**  
 * <pre>
 * Description	任务详情
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-29 下午8:02:07  
 * </pre>
 */

public class TaskDetailInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	private String auditRemark;//审核备注
	private String claim;//要求
	private String coverImage;//封面图片
	private String createUserName;//发起人
	private String deadline;//任务截止日期
	private String descript;//任务描述
	private String effectiveEndDate;//任务有效结束日期
	private String effectiveStartDate;//任务有效开始日期
	private String normalUrl;//	正常图片
	private String thumbnailUrl;//缩略图片
	private String orderId;//订单号
	private String rule;//	任务规则
	private String sex;//性别
	private String theme;//主题
	private String winnerRemark;//小派备注
	private int isImg;//是否要上传图片
	private String overallScore;//评分等级
	private int state;//订单状态
	private int taskUnitNum;//任务数量
	/**
	 * @return the {@link #auditRemark}
	 */
	public String getAuditRemark() {
		return auditRemark;
	}
	/**
	 * @param auditRemark the {@link #auditRemark} to set
	 */
	public void setAuditRemark(String auditRemark) {
		this.auditRemark = auditRemark;
	}
	/**
	 * @return the {@link #claim}
	 */
	public String getClaim() {
		return claim;
	}
	/**
	 * @param claim the {@link #claim} to set
	 */
	public void setClaim(String claim) {
		this.claim = claim;
	}
	/**
	 * @return the {@link #coverImage}
	 */
	public String getCoverImage() {
		return coverImage;
	}
	/**
	 * @param coverImage the {@link #coverImage} to set
	 */
	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}
	/**
	 * @return the {@link #createUserName}
	 */
	public String getCreateUserName() {
		return createUserName;
	}
	/**
	 * @param createUserName the {@link #createUserName} to set
	 */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	/**
	 * @return the {@link #deadline}
	 */
	public String getDeadline() {
		return deadline;
	}
	/**
	 * @param deadline the {@link #deadline} to set
	 */
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}
	/**
	 * @return the {@link #descript}
	 */
	public String getDescript() {
		return descript;
	}
	/**
	 * @param descript the {@link #descript} to set
	 */
	public void setDescript(String descript) {
		this.descript = descript;
	}
	/**
	 * @return the {@link #effectiveEndDate}
	 */
	public String getEffectiveEndDate() {
		return effectiveEndDate;
	}
	/**
	 * @param effectiveEndDate the {@link #effectiveEndDate} to set
	 */
	public void setEffectiveEndDate(String effectiveEndDate) {
		this.effectiveEndDate = effectiveEndDate;
	}
	/**
	 * @return the {@link #effectiveStartDate}
	 */
	public String getEffectiveStartDate() {
		return effectiveStartDate;
	}
	/**
	 * @param effectiveStartDate the {@link #effectiveStartDate} to set
	 */
	public void setEffectiveStartDate(String effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}
	/**
	 * @return the {@link #normalUrl}
	 */
	public String getNormalUrl() {
		return normalUrl;
	}
	/**
	 * @param normalUrl the {@link #normalUrl} to set
	 */
	public void setNormalUrl(String normalUrl) {
		this.normalUrl = normalUrl;
	}
	/**
	 * @return the {@link #thumbnailUrl}
	 */
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	/**
	 * @param thumbnailUrl the {@link #thumbnailUrl} to set
	 */
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	/**
	 * @return the {@link #orderId}
	 */
	public String getOrderId() {
		return orderId;
	}
	/**
	 * @param orderId the {@link #orderId} to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	/**
	 * @return the {@link #rule}
	 */
	public String getRule() {
		return rule;
	}
	/**
	 * @param rule the {@link #rule} to set
	 */
	public void setRule(String rule) {
		this.rule = rule;
	}
	/**
	 * @return the {@link #sex}
	 */
	public String getSex() {
		return sex;
	}
	/**
	 * @param sex the {@link #sex} to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}
	/**
	 * @return the {@link #theme}
	 */
	public String getTheme() {
		return theme;
	}
	/**
	 * @param theme the {@link #theme} to set
	 */
	public void setTheme(String theme) {
		this.theme = theme;
	}
	/**
	 * @return the {@link #winnerRemark}
	 */
	public String getWinnerRemark() {
		return winnerRemark;
	}
	/**
	 * @param winnerRemark the {@link #winnerRemark} to set
	 */
	public void setWinnerRemark(String winnerRemark) {
		this.winnerRemark = winnerRemark;
	}
	/**
	 * @return the {@link #isImg}
	 */
	public int getIsImg() {
		return isImg;
	}
	/**
	 * @param isImg the {@link #isImg} to set
	 */
	public void setIsImg(int isImg) {
		this.isImg = isImg;
	}
	/**
	 * @return the {@link #overallScore}
	 */
	public String getOverallScore() {
		return overallScore;
	}
	/**
	 * @param overallScore the {@link #overallScore} to set
	 */
	public void setOverallScore(String overallScore) {
		this.overallScore = overallScore;
	}
	/**
	 * @return the {@link #state}
	 */
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getTaskUnitNum() {
		return taskUnitNum;
	}
	public void setTaskUnitNum(int taskUnitNum) {
		this.taskUnitNum = taskUnitNum;
	}
	
	
	
	

}
