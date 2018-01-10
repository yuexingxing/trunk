package com.zhiduan.crowdclient.util;

import java.lang.reflect.Field;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.MainActivity;

import de.greenrobot.event.EventBus;

/**
 * @author hexiuhui
 *
 */
public class NotificationHelper
{

	private NotificationHelper()
	{
		// TODO Auto-generated constructor stub
	}
	
	public static void addCancelOrderNotify(Context context)
	{
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notify = new Notification();
		
		notify.icon = R.drawable.login_logo;
		notify.tickerText = "您有新订单哦";
		notify.when = System.currentTimeMillis();
		notify.flags = Notification.FLAG_AUTO_CANCEL;
		
		Intent intent = new Intent();
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
		notify.setLatestEventInfo(context, "您有一条新订单", "知道了", pi);
		notify.defaults = Notification.DEFAULT_SOUND;
		
		// 利用反射机制修改通知栏图标为App图标
		setNotifyIcon(notify);
				
		nm.notify(0, notify);
	}
	
	public static void addNewOrderNotify(Context context, String title, String content, boolean isPlayMusic, final EventBus mEventBus)
	{
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notify = new Notification();
		
		notify.icon = R.drawable.app_logo;
		notify.tickerText = title;
		notify.when = System.currentTimeMillis();
		notify.flags = Notification.FLAG_AUTO_CANCEL;
		
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra("FROM_NOTIFICATION", true);// 标志是通过点击notification跳转到主界面的
		intent.putExtra("type", OrderUtil.APP_CURRENT_PAGE_MONEY);
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
		
		notify.setLatestEventInfo(context, title, content, pi);
		if (isPlayMusic) {
			notify.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" +R.raw.new_order);
		} else {
			notify.defaults = Notification.DEFAULT_SOUND;
		}
		
		// 利用反射机制修改通知栏图标为App图标
		setNotifyIcon(notify);
				
		nm.notify(0, notify);
	}
	
	/** 利用反射机制修改通知栏图标为App图标 */
	private static void setNotifyIcon(Notification notify)
	{
		try
		{
			Class<?> clazz = Class.forName("com.android.internal.R$id");  
					         
			Field field = clazz.getField("icon");  
			field.setAccessible(true);  
			int nIconId = field.getInt(null);		        
			if(notify.contentView != null)
			{
				notify.contentView.setImageViewResource(nIconId, R.drawable.app_logo);//这个图标是通知下拉栏上显示的图标
			}
		}
		catch(Exception e)
		{
			 e.printStackTrace();
		}
	}
	
	/** 清除所有notification通知 */
	public static void clearAllNotification(Context context)
	{
		NotificationManager managerNotice = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		managerNotice.cancelAll();
	}
}
