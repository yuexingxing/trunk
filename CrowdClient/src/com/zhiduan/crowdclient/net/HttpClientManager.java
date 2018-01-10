package com.zhiduan.crowdclient.net;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.util.Logs;

import android.content.Context;
import android.net.Proxy;

/**
 * @author HeXiuHui
 *
 */
public class HttpClientManager
{
	private static final String TAG = "HttpClientManager";

	private static final String DEFAULT_USER_AGENT = "Dalvik/1.2.0 (Linux; U; Android 2.3; sdk Build/FRF91)";
	private static final int MAX_TOTAL_CONNECTIONS = 32;
	private static final int MAX_CONNECTIONS_PER_ROUTE = 32;
	private static final int CONNECTION_TIMEOUT = 30 * 1000;//20 * 1000;//30 * 1000;
	private static final int SOCKET_TIMEOUT = 15 * 1000;//20 * 1000;//30 * 1000;
	private static final int HTTP_PORT = 80;
	private static final int RETRY_COUNT = 2;///3;

	private static HttpClient sClient = null;

	public static void resetHttpClient(String proxyHostName, int proxyPort)
	{
		Logs.v(TAG, "proxy: " + proxyHostName + " " + proxyPort);

		int nConnectionTimeOut = CONNECTION_TIMEOUT;
		int nSocketTimeOut = SOCKET_TIMEOUT;
		
		if(WifiInfoEx.isWifiEnable())
		{
			proxyHostName = null;
			proxyPort = -1;
			
//xxx			nConnectionTimeOut /= 2;
//xxx			nSocketTimeOut /= 2;
		}
		
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET/*HTTP.UTF_8*/);
		HttpProtocolParams.setUseExpectContinue(params, false);
		String ua = System.getProperties().getProperty("http.agent");
		if (ua == null)
		{
			ua = DEFAULT_USER_AGENT;
		}
		
		HttpProtocolParams.setUserAgent(params, ua);
		ConnManagerParams.setMaxTotalConnections(params, MAX_TOTAL_CONNECTIONS);
		ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(MAX_CONNECTIONS_PER_ROUTE));
		HttpConnectionParams.setConnectionTimeout(params, nConnectionTimeOut);
		HttpConnectionParams.setSoTimeout(params, nSocketTimeOut);
		if (proxyHostName != null && !proxyHostName.equals(""))
		{
			HttpHost host = new HttpHost(proxyHostName, proxyPort);
			params.setParameter(ConnRouteParams.DEFAULT_PROXY, host);
			params.setParameter(ClientPNames.MAX_REDIRECTS, 5);
		}

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), HTTP_PORT));
		registry.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), HTTP_PORT));

		ClientConnectionManager manager = new ThreadSafeClientConnManager(params, registry);

		sClient = new DefaultHttpClient(manager, params);

		DefaultHttpRequestRetryHandler handler = new DefaultHttpRequestRetryHandler(RETRY_COUNT, true);
		((AbstractHttpClient) sClient).setHttpRequestRetryHandler(handler);
	}

	public static HttpClient getHttpClient()
	{
		if(sClient == null)
		{
			Context context = MyApplication.getInstance().getApplicationContext();
			WifiInfoEx.initWifi(context);	
			HttpClientManager.resetHttpClient(Proxy.getHost(context),
											  Proxy.getPort(context));
		}
		return sClient;
	}
}
