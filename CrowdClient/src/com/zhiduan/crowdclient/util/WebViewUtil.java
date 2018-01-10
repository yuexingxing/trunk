package com.zhiduan.crowdclient.util;

import com.github.lzyzsd.jsbridge.CallBackFunction;
import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class WebViewUtil {
	
	public static String BASE64 = null;
	public static CallBackFunction CALLBACKFUNCTION;

	/** 
	 * 同步一下cookie 
	 */  
	public static void synCookies(Context context, String cookies) {  
		
	    CookieSyncManager.createInstance(context);  
	    CookieManager cookieManager = CookieManager.getInstance();  
	    cookieManager.setAcceptCookie(true);  
	    cookieManager.removeSessionCookie();//移除  
	    cookieManager.setCookie("http://192.168.3.22:7008/axp-web/index.htm", "jsessionid=" + cookies);//cookies是在HttpClient中获得的cookie  "website_token="+"294539a5631280a2cdbf99f0e906dc21"
//	    CookieSyncManager.getInstance().sync();  
	}  
}
