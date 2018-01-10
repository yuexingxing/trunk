package com.zhiduan.crowdclient.data;

/** 
 * 账户信息
 * 
 * @author yxx
 *
 * @date 2017-3-15 上午11:41:04
 * 
 */
public class AccInfo {

	private int activeState;//	账户激活状态，0未激活，1激活
	private int balance;//账户余额
	
	public int getActiveState() {
		return activeState;
	}
	public void setActiveState(int activeState) {
		this.activeState = activeState;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
}
