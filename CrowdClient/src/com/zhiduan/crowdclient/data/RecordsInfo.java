package com.zhiduan.crowdclient.data;

import java.util.ArrayList;

public class RecordsInfo {
	// 账单明细id
	private long detailId;
	// 操作金额
	private long optAmount;
	// 资金类型（1充值，2提现，3收款）
	private int payType;
	// 创建时间
	private String createTime;
	private boolean is_open=false;
	private ArrayList<RecordSDetail> DescList = new ArrayList<RecordSDetail>();//收入详情展开列表
	
	/**
	 * @return the {@link #descList}
	 */
	public ArrayList<RecordSDetail> getDescList() {
		return DescList;
	}

	/**
	 * @param descList the {@link #descList} to set
	 */
	public void setDescList(ArrayList<RecordSDetail> descList) {
		DescList = descList;
	}

	/**
	 * @return the {@link #is_open}
	 */
	public boolean isIs_open() {
		return is_open;
	}

	/**
	 * @param is_open the {@link #is_open} to set
	 */
	public void setIs_open(boolean is_open) {
		this.is_open = is_open;
	}

	public RecordsInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RecordsInfo(long detailId, long optAmount, int payType,
			String createTime) {
		super();
		this.detailId = detailId;
		this.optAmount = optAmount;
		this.payType = payType;
		this.createTime = createTime;
	}

	public long getDetailId() {
		return detailId;
	}

	public void setDetailId(long detailId) {
		this.detailId = detailId;
	}

	public long getOptAmount() {
		return optAmount;
	}

	public void setOptAmount(long optAmount) {
		this.optAmount = optAmount;
	}

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
