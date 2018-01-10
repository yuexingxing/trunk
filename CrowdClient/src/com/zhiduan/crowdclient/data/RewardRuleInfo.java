package com.zhiduan.crowdclient.data;

import java.util.List;

/**
 * 奖励规则
 * 
 * @author yxx
 * 
 * @date 2016-11-25 上午11:53:00
 * 
 */
public class RewardRuleInfo {

	private String flowState;
	private String title = "";
	private String content = "";
	private String type = "";
	private String experence = "";
	private String itemDesc;
	private int needCompleteNumber; // 总数量
	private int completedNum; // 已完成数量
	private int cash = 0;

	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public int getNeedCompleteNumber() {
		return needCompleteNumber;
	}

	public void setNeedCompleteNumber(int needCompleteNumber) {
		this.needCompleteNumber = needCompleteNumber;
	}

	public int getCompletedNum() {
		return completedNum;
	}

	public void setCompletedNum(int completedNum) {
		this.completedNum = completedNum;
	}

	public String getFlowState() {
		return flowState;
	}

	public void setFlowState(String flowState) {
		this.flowState = flowState;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getExperence() {
		return experence;
	}

	public void setExperence(String experence) {
		this.experence = experence;
	}

	public int getCash() {
		return cash;
	}

	public void setCash(int cash) {
		this.cash = cash;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
