package com.zhiduan.crowdclient.data;

/**
 * <pre>
 * Description	订单评价
 * Author:		hexiuhui
 * </pre>
 */

public class EvaluateInfo {
	private String evaluateUrl;     //评价人头像
	private String evaluateContent;  //评价内容
	private Double overallScore;     //评分
	private String evaluateTime;     //评价时间
	private String appraiserGender;  //评价人性别
	private String appraiserMobile;  //评价人电话号码
	private String appraiserName;    //评价人姓名
	
	public String getAppraiserGender() {
		return appraiserGender;
	}

	public void setAppraiserGender(String appraiserGender) {
		this.appraiserGender = appraiserGender;
	}

	public String getAppraiserMobile() {
		return appraiserMobile;
	}

	public void setAppraiserMobile(String appraiserMobile) {
		this.appraiserMobile = appraiserMobile;
	}

	public String getAppraiserName() {
		return appraiserName;
	}

	public void setAppraiserName(String appraiserName) {
		this.appraiserName = appraiserName;
	}

	public String getEvaluateTime() {
		return evaluateTime;
	}

	public void setEvaluateTime(String evaluateTime) {
		this.evaluateTime = evaluateTime;
	}

	public String getEvaluateUrl() {
		return evaluateUrl;
	}

	public void setEvaluateUrl(String evaluateUrl) {
		this.evaluateUrl = evaluateUrl;
	}

	public String getEvaluateContent() {
		return evaluateContent;
	}

	public void setEvaluateContent(String evaluateContent) {
		this.evaluateContent = evaluateContent;
	}

	public Double getOverallScore() {
		return overallScore;
	}

	public void setOverallScore(Double overallScore) {
		this.overallScore = overallScore;
	}
}
