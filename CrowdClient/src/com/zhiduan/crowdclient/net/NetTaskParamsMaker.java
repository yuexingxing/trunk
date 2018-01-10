package com.zhiduan.crowdclient.net;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.apache.http.NameValuePair;

/**
 * @author HeXiuHui
 *
 */
public class NetTaskParamsMaker
{
	public static LoadTextParams makeLoadTextParams(String strUrl, ArrayList<NameValuePair> listArg, HttpSendType st)
	{
		LoadTextParams params = new LoadTextParams();
		
		params.m_strURL = strUrl;

		params.m_eHttpSendType = st;
		
		if(listArg != null && listArg.size() > 0)
		{
			if(st == HttpSendType.HTTP_POST)
			{
				params.m_strContent = listArg;
				params.m_strContentType = "application/x-www-form-urlencoded";//HttpContentType.CONTENT_TYPE_JSON;			
			}
			else
			{
				params.m_strURL += NetUtil.encodeParams2Url(listArg);
			}
		}

		params.m_strExpectContentType = null;//HttpContentType.EXPECT_CONTENT_TYPE_JSON;
		
		return params;
	}
	
	public static LoadPicParams makeLoadPictureParams(String strUrl)
	{
		LoadPicParams params = new LoadPicParams();
		params.m_strURL = strUrl;

		params.m_eHttpSendType = HttpSendType.HTTP_GET;
		
		params.m_strExpectContentType = HttpContentType.EXPECT_CONTENT_TYPE_IMAGE;
		
		return params;
	}
	
	public static LoadPicParams makeLoadComPanyParams(String strUrl)
	{
		LoadPicParams params = new LoadPicParams();
		params.m_strURL = strUrl;

		params.m_eHttpSendType = HttpSendType.HTTP_GET;
		
		params.m_strExpectContentType = HttpContentType.EXPECT_CONTENT_TYPE_IMAGE;
		
		return params;
	}
	
	public static LoadFileParams makeLoadFileParams(String strUrl,
													String strTargetFileName,
													String strExpectConteType,
													boolean bRedownload, 
													long nRecommendLen)
	{
		LoadFileParams params = new LoadFileParams();
		params.m_strURL = strUrl;
		params.m_strExpectContentType = strExpectConteType;
		
		if(strTargetFileName == null || strTargetFileName.equals(""))
		{
			params.m_strFileName = params.getTargetFileName();
		}
		else
		{
			params.m_strFileName = strTargetFileName;
		}

		if (params.m_strFileName != null)
		{
			if (bRedownload)
			{
				RandomAccessFile file = null;
				try
				{
					file = new RandomAccessFile(params.m_strFileName, "rw");
					file.setLength(0);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					if (file != null)
						try
						{
							file.close();
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
				}
			}
			else
			{
				File file = new File(params.m_strFileName);
				if (file.exists())
				{
					params.m_strRange = "bytes=" + file.length() + "-";
					params.m_nPos = (int)file.length();
				}
			}
		}
		params.m_nRecommendLen = nRecommendLen;
		return params;
	}
}
