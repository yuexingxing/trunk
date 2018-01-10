package com.zhiduan.crowdclient.data;

/** 
 * 短信聊天
 * 
 * @author yxx
 *
 * @date 2016-5-25 下午2:03:45
 * 
 */
public class SmsChatInfo {

	private String type;
	private String phone;
	private String content;
	private String time;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
