package com.zhiduan.crowdclient.data;

import java.io.Serializable;

/**
 * <pre>
 * Description	任务
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-17 上午9:56:32
 * </pre>
 */

public class TaskInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String task_id;//任务id
	private String task_title;// 任务标题
	private long task_money;// 任务打赏金额
	private String task_require;// 任务要求
	private String task_people_sex;// 任务要求性别
	private int task_status;// 任务状态
	private String task_logo;// 任务标识图片
	private String task_time;// 截止时间
	private long task_actual_money; //实际收入
	private double task_grade; //评分
	private String deliveryStartDate;//配送开始时间
	private String deliveryEndDate;//配送结束时间
	private String remark;//备注
	
	
	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getDeliveryStartDate() {
		return deliveryStartDate;
	}

	public void setDeliveryStartDate(String deliveryStartDate) {
		this.deliveryStartDate = deliveryStartDate;
	}

	public String getDeliveryEndDate() {
		return deliveryEndDate;
	}

	public void setDeliveryEndDate(String deliveryEndDate) {
		this.deliveryEndDate = deliveryEndDate;
	}
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public long getTask_actual_money() {
		return task_actual_money;
	}

	public void setTask_actual_money(long task_actual_money) {
		this.task_actual_money = task_actual_money;
	}

	public double getTask_grade() {
		return task_grade;
	}

	public void setTask_grade(double task_grade) {
		this.task_grade = task_grade;
	}

	public String getTask_time() {
		return task_time;
	}

	public void setTask_time(String task_time) {
		this.task_time = task_time;
	}

	public String getTask_title() {
		return task_title;
	}

	public void setTask_title(String task_title) {
		this.task_title = task_title;
	}

	public long getTask_money() {
		return task_money;
	}

	public void setTask_money(long task_money) {
		this.task_money = task_money;
	}

	public String getTask_require() {
		return task_require;
	}

	public void setTask_require(String task_require) {
		this.task_require = task_require;
	}

	public String getTask_people_sex() {
		return task_people_sex;
	}

	public void setTask_people_sex(String task_people_sex) {
		this.task_people_sex = task_people_sex;
	}

	public int getTask_status() {
		return task_status;
	}

	public void setTask_status(int task_status) {
		this.task_status = task_status;
	}

	public String getTask_logo() {
		return task_logo;
	}

	public void setTask_logo(String task_logo) {
		this.task_logo = task_logo;
	}

	public TaskInfo() {

	}

	public TaskInfo(String task_title, long task_money, String task_require,
			String task_people_sex, int task_status, String task_logo,
			String task_time) {
		super();
		this.task_title = task_title;
		this.task_money = task_money;
		this.task_require = task_require;
		this.task_people_sex = task_people_sex;
		this.task_status = task_status;
		this.task_logo = task_logo;
		this.task_time = task_time;
	}
}
