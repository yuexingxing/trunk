package com.zhiduan.crowdclient.data;

import java.io.Serializable;



import android.R.integer;

/**  
 * <pre>
 * Description  消息类
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-7-22 下午2:13:21  
 * </pre>
 */

public class MessageInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	private String MessageTitle;//消息标题
	private String MessageContent;//消息内容
	private String MessageTime;//消息时间
	private int MessageType;//消息类型
	private int firstMsgType;//一级消息类型
	private int readState;//消息状态
	private int unread_num;//未读消息数量
	private String message_icon;//消息图标
	private String new_message_content;//最新一条消息
	private String bizId;//消息业务ID
	private int msgId;
	private String redirectURL;
	
	private String beginDate;
	private String expiryDate;
	
	
	/**
	 * @return the {@link #beginDate}
	 */
	public String getBeginDate() {
		return beginDate;
	}
	/**
	 * @param beginDate the {@link #beginDate} to set
	 */
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	/**
	 * @return the {@link #expiryDate}
	 */
	public String getExpiryDate() {
		return expiryDate;
	}
	/**
	 * @param expiryDate the {@link #expiryDate} to set
	 */
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	/**
	 * @return the {@link #firstMsgType}
	 */
	public int getFirstMsgType() {
		return firstMsgType;
	}
	/**
	 * @param firstMsgType the {@link #firstMsgType} to set
	 */
	public void setFirstMsgType(int firstMsgType) {
		this.firstMsgType = firstMsgType;
	}
	/**
	 * @return the {@link #redirectURL}
	 */
	public String getRedirectURL() {
		return redirectURL;
	}
	/**
	 * @param redirectURL the {@link #redirectURL} to set
	 */
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}
	/**
	 * @return the {@link #msgId}
	 */
	public int getMsgId() {
		return msgId;
	}
	/**
	 * @param msgId the {@link #msgId} to set
	 */
	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}
	/**
	 * @return the {@link #bizId}
	 */
	public String getBizId() {
		return bizId;
	}
	/**
	 * @param bizId the {@link #bizId} to set
	 */
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public int getUnread_num() {
		return unread_num;
	}
	public void setUnread_num(int unread_num) {
		this.unread_num = unread_num;
	}
	public String getMessage_icon() {
		return message_icon;
	}
	public void setMessage_icon(String message_icon) {
		this.message_icon = message_icon;
	}
	public String getNew_message_content() {
		return new_message_content;
	}
	public void setNew_message_content(String new_message_content) {
		this.new_message_content = new_message_content;
	}
	public int getReadState() {
		return readState;
	}
	public void setReadState(int readState) {
		this.readState = readState;
	}
	public String getMessageTitle() {
		return MessageTitle;
	}
	public void setMessageTitle(String messageTitle) {
		MessageTitle = messageTitle;
	}
	public String getMessageContent() {
		return MessageContent;
	}
	public void setMessageContent(String messageContent) {
		MessageContent = messageContent;
	}
	public String getMessageTime() {
		return MessageTime;
	}
	public void setMessageTime(String messageTime) {
		MessageTime = messageTime;
	}
	public int getMessageType() {
		return MessageType;
	}
	public void setMessageType(int messageType) {
		MessageType = messageType;
	}
	public  MessageInfo() {
		
	}
	public MessageInfo(String messageTitle, String messageContent,
			String messageTime, int messageType) {
		super();
		MessageTitle = messageTitle;
		MessageContent = messageContent;
		MessageTime = messageTime;
		MessageType = messageType;
	}
	
	

}
