package com.zhiduan.crowdclient.net;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @author HeXiuHui
 *
 */
public class NetUtil 
{
	public static final boolean TOKEN = true;
	public static final boolean NOT_TOKEN = false;
	
	public static boolean isNetworkAvailable(Context context)
	{
		// 判断当前是否联网
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();

		return (info != null && info.isAvailable());
	}
	
	public static String encodeParams2Json(ArrayList<NameValuePair> listArg)
	{
		StringBuilder sb = new StringBuilder();
		
		int nLen = listArg.size();
		for(int i=0; i<nLen; i++)
		{
			if(i > 0)
			{
				sb.append('&');
			}
			sb.append(listArg.get(i).getName());
			sb.append('=');
			sb.append(listArg.get(i).getValue());
		}

		return sb.toString();
	}
	
	public static String encodeParams2Url(ArrayList<NameValuePair> listArg)
	{
		StringBuilder sb = new StringBuilder();
		
		int nLen = listArg.size();
		for(int i=0; i<nLen; i++)
		{
			if(i == 0)
			{
				sb.append('?');
			}
			else
			{
				sb.append('&');
			}
			sb.append(listArg.get(i).getName());
			sb.append('=');
			sb.append(listArg.get(i).getValue());
		}

		return sb.toString();
	}
	
	//  16位随即数，定长
	private static long MAX_VALUE = ((long)(10E15) - 1);
	public static String getToken()
	{
		return String.format("%016d", (long)(Math.random() * MAX_VALUE));
	}	
	
	public static String getFileNameFromUrl(String strUrl)
	{
		String strFileName = "";
		
		// 是根据id取图片
		final String strGetPicById = "?";
		if(strUrl.indexOf(strGetPicById) <= 0)
		{
			strFileName = strUrl.substring(strUrl.lastIndexOf('/') - 1);
			
			if(strFileName == null || strFileName.length() == 0)
			{
				strFileName = Integer.toString(Math.abs(strUrl.hashCode()));
			}
			
			strFileName = getPicNameById(strFileName.substring(strGetPicById.length()));
			
		} else {
			String fileName = strUrl.substring(strUrl.lastIndexOf('?') - 16);
			
			strFileName = fileName.replaceAll("/", "");
			
			Log.i("hexiuhui===", strFileName + "strFileName");
			
			if(strFileName == null || strFileName.length() == 0)
			{
				strFileName = Integer.toString(Math.abs(strUrl.hashCode()));
			}
			
			// 是根据id取图片
			final String getPicById = "?";
			if(strFileName.indexOf(getPicById) == 0)
			{
				strFileName = getPicNameById(strFileName.substring(getPicById.length()));
				
			}
			else
			{
				// 处理url中jpg图片名后带"?"的情况，如http://asmss.chevrolet.com.cn/Upload/Pic/d834525c-aff9-48e0-84b0-cfb1451d1e67.jpg?w=640&h=360
				int nExtIndex = strFileName.indexOf("?");
				if(nExtIndex > 0)
				{
					strFileName = strFileName.substring(0, nExtIndex);
				}
			}
		}
		
		return DirSettings.getAppCacheDir() + strFileName;
	}
	
	private static String getPicNameById(final String strId)
	{
		int nIndex = strId.indexOf("_");
		if(nIndex > 0)
		{
			return strId.replace("_", ".");
		}
		else
		{
			return strId + ".jpg";
		}
	}
	
	private static NetworkEventRecevier networkEventRecevier = null;
	
	// 注册NetworkEventRecevier
	public static void registerNetworkEventRecevier(Context context)
	{
		networkEventRecevier = new NetworkEventRecevier();
				
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		context.registerReceiver(networkEventRecevier, filter);
	}
	
	// 取消注册NetworkEventRecevier
	public static void unregisterNetworkEventRecevier(Context context)
	{
		if(networkEventRecevier != null)
		{
			context.unregisterReceiver(networkEventRecevier);
			networkEventRecevier = null;
		}
	}
}
