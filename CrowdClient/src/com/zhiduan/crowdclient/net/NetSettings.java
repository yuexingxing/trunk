package com.zhiduan.crowdclient.net;

import java.util.ArrayList;

/**
 * @author HeXiuHui
 *
 */
public class NetSettings
{
	
	public static final String HTTP_SESSIONID_NAME = "JSESSIONID";
	private String m_strHttpSessionId = "";
	
	public void reset()
	{
		m_strHttpSessionId = "";
	}
	
	public String getHttpSessionID()
	{
		return m_strHttpSessionId;
	}

	public void setHttpSessionID(String strHttpSessionID)
	{
		m_strHttpSessionId = strHttpSessionID;
	}
	
	public ArrayList<String> getAsStringList()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add(m_strHttpSessionId);

		return list;
	}
	
	public void setFromStringList(ArrayList<String> list)
	{
		try
		{
			int i = 0;
			m_strHttpSessionId = list.get(i++);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}