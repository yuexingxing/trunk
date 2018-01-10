package com.zhiduan.crowdclient.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.zhiduan.crowdclient.util.Logs;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * @author HeXiuHui
 *
 */
public class WifiInfoEx
{
	private static Context mContext;

	public static void initWifi(Context context)
	{
		mContext = context;
	}

	public static boolean isWifiEnable()
	{
		WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		if (wifi.isWifiEnabled())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static int getWifiState()
	{
		WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		return wifi.getWifiState();
	}

	public static String getLocalIpAddress()
	{
		try
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress())
					{
						return inetAddress.getHostAddress().toString();

					}
				}
			}
		}
		catch (SocketException ex)
		{
			Logs.e("WifiPreference IpAddress", ex.toString());
		}
		
		return null;
	}
}
