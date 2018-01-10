package com.zhiduan.crowdclient.data;

import java.io.Serializable;

import android.R.integer;

/**
 * 
 * <pre>
 * Description	邀请Object
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-11-26 下午3:34:57  
 * </pre>
 */

public class InviteInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private String invite_icon;//邀请人头像
	private String invite_name;//邀请人名字
	private String invite_phone;// 邀请人电话
	private String invite_date;// 邀请日期
	private Long invite_income;// 邀请收入
	private String invite_sex;//邀请人性别
	private int expPoint;//经验值
	
	
	/**
	 * @return the {@link #expPoint}
	 */
	public int getExpPoint() {
		return expPoint;
	}


	/**
	 * @param expPoint the {@link #expPoint} to set
	 */
	public void setExpPoint(int expPoint) {
		this.expPoint = expPoint;
	}


	/**
	 * @return the {@link #invite_icon}
	 */
	public String getInvite_icon() {
		return invite_icon;
	}


	/**
	 * @return the {@link #invite_sex}
	 */
	public String getInvite_sex() {
		return invite_sex;
	}


	/**
	 * @param invite_sex the {@link #invite_sex} to set
	 */
	public void setInvite_sex(String invite_sex) {
		this.invite_sex = invite_sex;
	}


	/**
	 * @param invite_icon the {@link #invite_icon} to set
	 */
	public void setInvite_icon(String invite_icon) {
		this.invite_icon = invite_icon;
	}


	/**
	 * @return the {@link #invite_name}
	 */
	public String getInvite_name() {
		return invite_name;
	}


	/**
	 * @param invite_name the {@link #invite_name} to set
	 */
	public void setInvite_name(String invite_name) {
		this.invite_name = invite_name;
	}


	/**
	 * @return the {@link #invite_phone}
	 */
	public String getInvite_phone() {
		return invite_phone;
	}


	/**
	 * @param invite_phone the {@link #invite_phone} to set
	 */
	public void setInvite_phone(String invite_phone) {
		this.invite_phone = invite_phone;
	}


	/**
	 * @return the {@link #invite_date}
	 */
	public String getInvite_date() {
		return invite_date;
	}


	/**
	 * @param invite_date the {@link #invite_date} to set
	 */
	public void setInvite_date(String invite_date) {
		this.invite_date = invite_date;
	}


	/**
	 * @return the {@link #invite_income}
	 */
	public Long getInvite_income() {
		return invite_income;
	}


	/**
	 * @param invite_income the {@link #invite_income} to set
	 */
	public void setInvite_income(Long invite_income) {
		this.invite_income = invite_income;
	}


	@Override
	public String toString() {
		return "InviteInfo [invite_icon=" + invite_icon + ", invite_name="
				+ invite_name + ", invite_phone=" + invite_phone
				+ ", invite_date=" + invite_date + ", invite_income="
				+ invite_income + "]";
	}
	
	
	
	
}
