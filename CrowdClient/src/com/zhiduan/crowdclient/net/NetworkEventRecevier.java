package com.zhiduan.crowdclient.net;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Proxy;
import android.util.Log;

import com.igexin.sdk.PushManager;
import com.zhiduan.crowdclient.MyApplication;

/**
 * @author hexiuhui
 *
 */
@SuppressLint("NewApi")
public class NetworkEventRecevier extends BroadcastReceiver
{
	Context mContext;
	private static String host = null;
	private static int port = -1;

	private boolean isAppRunning(Context context) {
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		boolean isAppRunning = false;
		String pageName = context.getPackageName();
		
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(pageName) || info.baseActivity.getPackageName().equals(pageName)) {
				isAppRunning = true;
				break;
			}
		}
		return isAppRunning;
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (isAppRunning(context) == false) {
			// 注销网络监听
			NetUtil.unregisterNetworkEventRecevier(context.getApplicationContext());
			return;
		}
		
	    if (host != null)
	    {
	        Log.i("NetworkEventRecevier", "host "+host);
	    }
	    else
	    {
	        Log.i("NetworkEventRecevier", "host null");
	    }
	    
		mContext = context;
		String action = intent.getAction();
		Log.i("action", action);
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action))
		{
			NetworkInfo ni = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			
			if (ni.getState() == State.CONNECTED)	//网络连接恢复
			{
				//重新初始化个推
				Log.d("State.CONNECTED", "ReInitializing GeTui sdk...");
				PushManager.getInstance().initialize(context);
				
				String clientId = PushManager.getInstance().getClientid(context);
				if (clientId != null && !clientId.isEmpty())
				{
					MyApplication.getInstance().m_strPushMessageClientId = clientId;
				}
			}
				
			if (ni.getState() == State.CONNECTED && ni.getType() == ConnectivityManager.TYPE_WIFI)
			{
				if (host == null)
				{
					host = Proxy.getHost(mContext);
					port = Proxy.getPort(mContext);
				}
				else
				{
					if (!host.equals(Proxy.getHost(mContext)))
					{
						host = Proxy.getHost(mContext);
						port = Proxy.getPort(mContext);
						Log.v("resetHttpClient", "wifi resetHttpClient");
						HttpClientManager.resetHttpClient(host, port);
					}
				}
			}
			else if (ni.getState() == State.DISCONNECTED && ni.getType() == ConnectivityManager.TYPE_WIFI)
			{
				Log.v("NetworkEventRecevier", "wifi disconnected");
			}

			if (ni.getState() == State.CONNECTED && ni.getType() == ConnectivityManager.TYPE_MOBILE)
			{
				Log.v("NetworkEventRecevier", "TYPE_MOBILE connected");
				if (host == null)
				{
					host = Proxy.getHost(mContext);
					port = Proxy.getPort(mContext);
				}
				else
				{
					if (!host.equals(Proxy.getHost(mContext)))
					{
						host = Proxy.getHost(mContext);
						port = Proxy.getPort(mContext);
						Log.v("resetHttpClient", "TYPE_MOBILE host");
						HttpClientManager.resetHttpClient(host, port);
					}

				}
				return;
			}
			else if (ni.getState() == State.DISCONNECTED && ni.getType() == ConnectivityManager.TYPE_MOBILE)
			{
				Log.v("NetworkEventRecevier", "TYPE_MOBILE disconnected");
			}
		}
		
        if (host != null)
        {
            Log.i("NetworkEventRecevier", "host "+host);
        }
        else
        {
            Log.i("NetworkEventRecevier", "host null");
        }
	}

}
