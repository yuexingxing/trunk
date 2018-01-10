package com.zhiduan.crowdclient.data;

import java.util.ArrayList;

public class UserInfo {

	// 用户已签收数量
	public String m_strUserSign = "";
	
	// 姓名
	public String m_strUserName = "";
	
	//性别
	public String m_strUserGender = "";
	//学校
	public String m_strUserSchool="";
	//学校Id
	public int m_strUserSchoolId;
	// 用户头像
	public String m_strUserHeadPic = "";
	
	//工作状态
	public String m_strUserWorkState = "0";
	
	// 账户余额
	public long m_user_income;
	
	//用户派件总收入
	public long m_user_total_income;
	//用户余额
	public long m_user_balance;
	public String m_nick_name="";

	// 电话
	public String m_strUserPhone = "";

	// 权限签名(加密)
	public String m_strUserAuthSign = "";

	// 数据来源
	public String m_strUserDataSourcePCode = "";

	public String toKen = "";
	public int tokenTime = 6900;//token有效时间 默认6900秒

	public int verifyStatus ;// 认证状态码0 未提交信息 1 提交信息未审核 2 审核通过 3审核失败
	
	public String m_strUserOffice  = "";//楼长，字段为空表示非楼长

	public ArrayList<String> getInfoAsStringList() {
		ArrayList<String> list = new ArrayList<String>();

		list.add(m_strUserSign);
		list.add(m_strUserName);
		list.add(m_strUserGender);
		list.add(m_strUserHeadPic);
		list.add(m_strUserWorkState);
		list.add(m_nick_name);
		list.add(String.valueOf(m_user_income));
		list.add(String.valueOf(m_user_total_income));
		list.add(m_strUserPhone);
		list.add(m_strUserAuthSign);
		list.add(m_strUserDataSourcePCode);
		list.add(toKen);
		list.add(m_strUserSchool);
		list.add(String.valueOf(m_strUserSchoolId));
		list.add(String.valueOf(verifyStatus));
		
		return list;
	}

	public void setInfoFromStringList(ArrayList<String> list) {
		int i = 0;
		m_strUserSign = list.get(i++);
		m_strUserName = list.get(i++);
		m_strUserGender = list.get(i++);
		m_nick_name=list.get(i++);
		m_strUserHeadPic = list.get(i++);
		m_strUserSchool=list.get(i++);
		m_strUserSchoolId=Integer.parseInt(list.get(i++));
		m_strUserWorkState = list.get(i++);
		m_user_income = Long.parseLong(list.get(i++));
		m_user_total_income = Long.parseLong(list.get(i++));
		m_strUserPhone = list.get(i++);
		m_strUserAuthSign = list.get(i++);
		m_strUserDataSourcePCode = list.get(i++);
		toKen = list.get(i++);
		verifyStatus = Integer.parseInt(list.get(i++));
	}
}
