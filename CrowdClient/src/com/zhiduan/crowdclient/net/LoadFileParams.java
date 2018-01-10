package com.zhiduan.crowdclient.net;


/**
 * @author HeXiuHui
 *
 */
public class LoadFileParams extends NetTaskParams
{
	//public boolean m_bLoadBitmap = false;
	//public boolean m_bWriteToFile = true;
	public String m_strFileName;
	
	//public String mFileName;
	public long m_nPos = 0;
	public String m_strRange = "";
	public long m_nRecommendLen = 0;

	@Override
	public String getTargetFileName()
	{
		return NetUtil.getFileNameFromUrl(m_strURL);
	}
}
