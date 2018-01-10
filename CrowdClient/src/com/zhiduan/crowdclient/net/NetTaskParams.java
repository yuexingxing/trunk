package com.zhiduan.crowdclient.net;

import java.util.List;

import org.apache.http.NameValuePair;

/**
 * @author HeXiuHui
 *
 */
public class NetTaskParams
{
	public String m_strURL;
	
	HttpSendType m_eHttpSendType;		// GET or POST
	
	public List<? extends NameValuePair> m_strContent;			// for POST
	public String m_strContentType;
	
	public String m_strExpectContentType;
	
	public int getKey()
	{
		if(m_eHttpSendType == HttpSendType.HTTP_GET)
		{
			return m_strURL.hashCode();
		}
		else
		{
			return (m_strURL + "?" + m_strContent).hashCode();
		}
	}

	public String getTargetFileName()
	{
		return null;
	}

	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}

		if (NetTaskParams.class.isInstance(o))
		{
			int oKey = ((NetTaskParams) o).getKey();
			int myKey = getKey();
			return ((oKey != 0) && (myKey != 0) && (oKey == myKey));
		}

		return false;
	}
}
