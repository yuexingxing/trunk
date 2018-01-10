package com.zhiduan.crowdclient.data;

import java.io.Serializable;

public class PayAccount implements Serializable{
	private String accountName = "";
	private String accountNumber = "";
	private boolean isPitchOn = false;
	private long thdId = 0;
	
	

	
	public PayAccount(String accountName, String accountNumber,
			boolean isPitchOn, long thdId) {
		super();
		this.accountName = accountName;
		this.accountNumber = accountNumber;
		this.isPitchOn = isPitchOn;
		this.thdId = thdId;
	}

	public long getThdId() {
		return thdId;
	}

	public void setThdId(long thdId) {
		this.thdId = thdId;
	}

	public PayAccount() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public boolean isPitchOn() {
		return isPitchOn;
	}
	public void setPitchOn(boolean isPitchOn) {
		this.isPitchOn = isPitchOn;
	}
}
