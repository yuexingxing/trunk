package com.zhiduan.crowdclient.data;

/** 
 * 接单人信息
 * 
 * @author yxx
 *
 * @date 2017-1-20 下午3:22:24
 * 
 */
public class TakerInfo extends OrderInfo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String icon;//接单人头像
	private String name;//接单人姓名
	private int userId;//用户ID

	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
