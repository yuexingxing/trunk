package com.zhiduan.crowdclient.util;

import java.util.ArrayList;

import android.content.Context;
import android.net.Proxy;
import android.os.Bundle;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.net.HttpClientManager;
import com.zhiduan.crowdclient.net.WifiInfoEx;

/**
 * @author hexiuhui
 *
 */
// 用来在Activity被回收时保存和恢复公共数据
public class GlobalInstanceStateHelper
{
	// 保存数据
	public static void saveInstanceState(Bundle outState)
	{
		MyApplication myApp = MyApplication.getInstance();
		
		/** Activity被回收时，保存全局变量的版本号（每保存一次，自增1）  */
		//public int m_nSaveInstanceStateVersion = 0;
		outState.putInt("m_nSaveInstanceStateVersion", ++myApp.m_nSaveInstanceStateVersion);
	
		//保存用户信息
		saveUserInfo(outState);

		outState.putString("sendTime", myApp.sendTime);
		
		outState.putString("mRandom", myApp.mRandom);
		
		outState.putLong("withdrawDepositTime", myApp.withdrawDepositTime);
		outState.putInt("Wallet_Activate", myApp.Wallet_Activate);
		
		outState.putString("m_strPushMessageClientId", myApp.m_strPushMessageClientId);
		
		//public static int m_nScreenWidth = -1;
		//public static int m_nScreenHeight = -1;	
		outState.putInt("m_nScreenWidth", MyApplication.s_nScreenWidth);
		outState.putInt("m_nScreenHeight", MyApplication.s_nScreenHeight);
	}
	
	// 恢复数据
	public static void restoreInstanceState(Context context, Bundle savedInstanceState)
	{
		MyApplication myApp = MyApplication.getInstance();
		
		/** Activity被回收时，保存全局变量的版本号（每保存一次，自增1）  */
		//public int m_nSaveInstanceStateVersion = 0;
		int nVersion = savedInstanceState.getInt("m_nSaveInstanceStateVersion");
		if(nVersion > myApp.m_nSaveInstanceStateVersion)
		{
			myApp.m_nSaveInstanceStateVersion = nVersion;
		}
		else
		{
			return;	// 不需要恢复（当前数据更新，或与要恢复的数据相同）
		}
		
		restoreUserInfo(savedInstanceState);
		
		myApp.sendTime = savedInstanceState.getString("sendTime");
		
		myApp.mRandom = savedInstanceState.getString("mRandom");
		
		myApp.withdrawDepositTime = savedInstanceState.getLong("withdrawDepositTime");
		myApp.Wallet_Activate = savedInstanceState.getInt("Wallet_Activate");
		
		myApp.m_strPushMessageClientId = savedInstanceState.getString("m_strPushMessageClientId");
		
		/** 屏幕宽、高 */
		//public static int m_nScreenWidth = -1;
		//public static int m_nScreenHeight = -1;	
		MyApplication.s_nScreenWidth = savedInstanceState.getInt("m_nScreenWidth");
		MyApplication.s_nScreenHeight = savedInstanceState.getInt("m_nScreenHeight");
		if(MyApplication.s_nScreenWidth == -1)
		{
			Util.initScreenSize(context);
		}
		
		// 重设网络连接
		WifiInfoEx.initWifi(context);	
		HttpClientManager.resetHttpClient(Proxy.getHost(context.getApplicationContext()),
				  						  Proxy.getPort(context.getApplicationContext()));
	}
	
	private static void saveUserInfo(Bundle outState)
	{
		outState.putStringArrayList("UserInfo", MyApplication.getInstance().m_userInfo.getInfoAsStringList());
	}
	
	private static void restoreUserInfo(Bundle savedInstanceState)
	{
		ArrayList<String> list = savedInstanceState.getStringArrayList("UserInfo");
		MyApplication.getInstance().m_userInfo.setInfoFromStringList(list);
	}
}
