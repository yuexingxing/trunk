package com.zhiduan.crowdclient.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.RelativeLayout;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.im.ImMainActivity;
import com.zhiduan.crowdclient.menucenter.MyActivity;
import com.zhiduan.crowdclient.menuindex.IndexActivity;
import com.zhiduan.crowdclient.menuorder.OrderMenuPagerActivity;
import com.zhiduan.crowdclient.net.NetUtil;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.PCheckUpdate;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.util.ScreenUtil;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.view.BadgeView;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;

/**
 * <pre>
 * Description  主界面
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-5-23 下午6:07:45
 * </pre>
 */
@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	private final String mPageName = "MainActivity";

	public static TextView mNotOrderNum;
	private  TabHost mainTabHost;
	private static TabWidget tabWidget;
	private ImageView image_indexTab;
	private ImageView image_OrderTab;
	private ImageView image_TaskTab;
	private ImageView image_CenterTab;
	private ImageView image_SettingTab;
	public static RelativeLayout parent;
	private String token;
	public MyApplication myApp = MyApplication.getInstance();
	private Context context;
	private Timer timerToken;
	private boolean main_first;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		context = this;
		Utils.clearActivityList();
		setImmerseLayout();
		setAll(3);
		Utils.addActivity(this);
		mainTabHost.setCurrentTab(0);
		Sethide();
//		checkClientId();
		//		main_first = (Boolean) SharedPreferencesUtils.getParam("main_first", false);
		//		if (!main_first) {
		//			SharedPreferencesUtils.setParam("main_first", true);
		//			ShowVersionDialog.showMyDialog(context, R.layout.dialog_show_version_view1);
		//		}

//		PCheckUpdate.check(context, null);
	}

	/**
	 * 判断个推初始化是否成功
	 * clientId是否获取到
	 * 如果为空给提示
	 */
	private void checkClientId() {
		String clientId = MyApplication.getInstance().m_strPushMessageClientId;
		if (TextUtils.isEmpty(clientId)) {
			clientId = PushManager.getInstance().getClientid(getApplicationContext());
			if (clientId != null && !clientId.isEmpty()) {
				MyApplication.getInstance().m_strPushMessageClientId = clientId;
			}
		} else {
			return;
		}

		GeneralDialog.showOneButtonDialog(MainActivity.this, GeneralDialog.DIALOG_ICON_TYPE_8, "", "系统繁忙，请手动刷新订单列表 !", "确定", new GeneralDialog.OneButtonListener() {
			@Override
			public void onExitClick(Dialog dialog) {
				if (dialog != null) {
					dialog.dismiss();
				}
			}

			@Override
			public void onButtonClick(Dialog dialog) {
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 友盟统计
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(this);

		token = myApp.m_userInfo.toKen;
		MyApplication.baseActivity = this;

		/**
		 * token续约 开启一个计时器，当tokentime距离失效10分钟重新获取
		 */
		long tokenTime = MyApplication.getInstance().m_userInfo.tokenTime
				* 1000 - 1000 * 60 * 10;
		if (timerToken == null) {
			if (tokenTime < 0) {
				tokenTime = 42900 * 1000 - 1000 * 60 * 10;
			}

			timerToken = new Timer(true);
//			timerToken.schedule(taskToken, tokenTime, tokenTime); // 延时1000ms后执行，tokenTime
		} else {
			Logs.v("zd", "计时器已启动");
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		// 友盟统计
		MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(this);

		Log.v("pic", "onResume");
		int type = getIntent().getIntExtra("type", 0) ;
		if(type == OrderUtil.APP_CURRENT_PAGE_MONEY){//点击通知栏跳转单小派页面
//
//			Log.v("pic", "APP_CURRENT_PAGE_MONEY");
//			Message msg = new Message();
//			msg.what = OrderUtil.APP_CURRENT_PAGE_MONEY;
//			MyApplication.getInstance().getEventBus().post(msg);
		}
	}

	/**
	 * 加载TabHost
	 */
	public void setAll(int tabId) {

		mainTabHost = this.getTabHost();
		tabWidget = this.getTabWidget();
		/**
		 * tabhost界面,自定义widget
		 */
		// 去除tabwidget分割线
		tabWidget.setDividerDrawable(null);


		// 首页
		View indexTab = (View) LayoutInflater.from(this).inflate(R.layout.tabhost_widget, null);
		image_indexTab = (ImageView) indexTab.findViewById(R.id.tab_image);
		image_indexTab.setBackgroundResource(R.drawable.tabbar_homepage_highlight);
		TextView indexTabNum = (TextView) indexTab.findViewById(R.id.not_read_number);
		indexTabNum.setVisibility(View.GONE);

		// 订单
		View orderTab = (View) LayoutInflater.from(this).inflate(R.layout.tabhost_widget, null);
		image_OrderTab = (ImageView) orderTab.findViewById(R.id.tab_image);
		image_OrderTab.setBackgroundResource(R.drawable.tabbar_order);
		mNotOrderNum = (TextView) orderTab.findViewById(R.id.not_read_number);
		SharedPreferences sp = MainActivity.this.getSharedPreferences(Constant.SAVE_NOT_ORDER_NUMBER, Context.MODE_PRIVATE);
		int orderNumber = sp.getInt("OrderNumber", 0);
		mNotOrderNum.setText(orderNumber + "");
		if (orderNumber == 0) {
			mNotOrderNum.setVisibility(View.GONE);
		} else {
			mNotOrderNum.setVisibility(View.VISIBLE);
		}

		// 消息
		View taskTab = (View) LayoutInflater.from(this).inflate(R.layout.tabhost_widget, null);
		image_TaskTab = (ImageView) taskTab.findViewById(R.id.tab_image);
		image_TaskTab.setBackgroundResource(R.drawable.tabbar_news);

		// 中心
		View centerTab = (View) LayoutInflater.from(this).inflate(R.layout.tabhost_widget, null);
		image_CenterTab = (ImageView) centerTab.findViewById(R.id.tab_image);
		image_CenterTab.setBackgroundResource(R.drawable.tabbar_i_have);
		TextView centerTabNum = (TextView) centerTab.findViewById(R.id.not_read_number);
		centerTabNum.setVisibility(View.GONE);

		// 设置
		//		View settingTab = (View) LayoutInflater.from(this).inflate(R.layout.tabhost_widget, null);
		//		image_SettingTab = (ImageView) settingTab.findViewById(R.id.tab_image);
		//		image_SettingTab.setBackgroundResource(R.drawable.tabbar_setup);

		mainTabHost.setup();

		mainTabHost.addTab(mainTabHost.newTabSpec("首页").setIndicator(indexTab)
				.setContent(new Intent(this, IndexActivity.class)));

		mainTabHost.addTab(mainTabHost.newTabSpec("已接单").setIndicator(orderTab)
				.setContent(new Intent(this, OrderMenuPagerActivity.class)));

//		mainTabHost.addTab(mainTabHost.newTabSpec("消息").setIndicator(taskTab)
//				.setContent(new Intent(this, ImMainActivity.class)));

		mainTabHost.addTab(mainTabHost.newTabSpec("我的").setIndicator(centerTab)
				.setContent(new Intent(this, MyActivity.class)));
		tabWidget.setVisibility(View.GONE);
		//		mainTabHost.addTab(mainTabHost.newTabSpec("设置").setIndicator(settingTab)
		//				.setContent(new Intent(this, SettingActivity.class)));

		/**
		 * tab的监听，改变icon与textcolor
		 */
		mainTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				if (tabId.equals("首页")) {
					// 友盟统计
					MobclickAgent.onEvent(MainActivity.this, "index_tab");

					setStatus(0);
				} else if (tabId.equals("已接单")) {
					// 友盟统计
					MobclickAgent.onEvent(MainActivity.this, "orider_tab");

					//删除未读消息显示
					Util.removeNotNumber(MainActivity.this, Util.order_type);
					mNotOrderNum.setVisibility(View.GONE);

//					if (TextUtils.isEmpty(token)) {
//						show_Registerdialog();
//						mainTabHost.setCurrentTab(0);
//						setStatus(0);
//					} else {
						setStatus(1);
//					}
				}else if (tabId.equals("消息")) {
					// 友盟统计
					MobclickAgent.onEvent(MainActivity.this, "task_tab");

//					if (TextUtils.isEmpty(token)) {
//						show_Registerdialog();
//						mainTabHost.setCurrentTab(0);
//						setStatus(0);
//					} else {
						setStatus(2);
//					}
				}else if (tabId.equals("我的")) {
					// 友盟统计
					MobclickAgent.onEvent(MainActivity.this, "personal_tab");

//					if (TextUtils.isEmpty(token)) {
//						show_Registerdialog();
//						mainTabHost.setCurrentTab(0);
//						setStatus(0);
//					} else {
						setStatus(3);
//					}
				} 
			}
		});

	}

	public static int[] endLocation;
	public static BadgeView buyNumView;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		//		TextView image_TaskTab = (TextView) findViewById(R.id.task_text);

		buyNumView = new BadgeView(MainActivity.this, image_TaskTab);
		buyNumView.setTextColor(Color.WHITE);
		buyNumView.setBackgroundColor(Color.RED);
		//		buyNumView.setBadgeMargin(118);
		//		image_TaskTab.setPadding(160, 0, 0, 100);
		buyNumView.setTextSize(12);


		endLocation = new int[2];// 存储动画结束位置的X、Y坐标
		//		image_TaskTab.getLocationInWindow(endLocation);// shopCart是那个购物车
		image_TaskTab.getLocationInWindow(endLocation);
		Log.i("-----hexiuhui===", endLocation[0] + ":" + endLocation[1]);

	}

	public void show_Registerdialog() {

		DialogUtils.showLoginDialog(MainActivity.this);
	}

	protected void setImmerseLayout(View view) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			int statusBarHeight = ScreenUtil.getStatusBarHeight(this
					.getBaseContext());
			view.setPadding(0, statusBarHeight, 0, 0);
		}
	}

	protected void setImmerseLayout() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	public void setStatus(int position) {
		switch (position) {
		case 0:
			image_indexTab
			.setBackgroundResource(R.drawable.tabbar_homepage_highlight);
			image_OrderTab.setBackgroundResource(R.drawable.tabbar_order);
			image_CenterTab.setBackgroundResource(R.drawable.tabbar_i_have);
			image_TaskTab.setBackgroundResource(R.drawable.tabbar_news);
			break;
		case 1:
			image_indexTab.setBackgroundResource(R.drawable.tabbar_homepage);
			image_OrderTab
			.setBackgroundResource(R.drawable.tabbar_order_highlight);
			image_TaskTab.setBackgroundResource(R.drawable.tabbar_news);
			image_CenterTab.setBackgroundResource(R.drawable.tabbar_i_have);
			break;
		case 2:
			image_indexTab.setBackgroundResource(R.drawable.tabbar_homepage);
			image_OrderTab.setBackgroundResource(R.drawable.tabbar_order);
			image_CenterTab.setBackgroundResource(R.drawable.tabbar_i_have);
			image_TaskTab.setBackgroundResource(R.drawable.tabbar_task_news);
			break;
		case 3:
			image_indexTab.setBackgroundResource(R.drawable.tabbar_homepage);
			image_OrderTab.setBackgroundResource(R.drawable.tabbar_order);
			image_CenterTab.setBackgroundResource(R.drawable.tabbar_tack_i_have);
			image_TaskTab.setBackgroundResource(R.drawable.tabbar_news);
			break;
		default:
			break;
		}
	}

	int keyBackClickCount = 0;

	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && IndexActivity.mUserWebView != null && IndexActivity.mUserWebView.canGoBack()) {

			IndexActivity.mUserWebView.goBack();// 返回前一个页面
			return true;
		}
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyBackClickCount++) {
			case 0:
				CommandTools.showToast("再按一次退出程序");
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						keyBackClickCount = 0;
					}
				}, 2000);
				break;
			case 1:
				exitApp();
				break;
			default:
				break;
			}
			//			back();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 返回
	 */
	private static Boolean isQuit = false;
	private Timer timerBack = new Timer();

	private void back() {

		if (isQuit == false) {
			isQuit = true;
			CommandTools.showToast("再按一次退出程序");
			TimerTask task = null;
			task = new TimerTask() {
				@Override
				public void run() {
					isQuit = false;
				}
			};
			timerBack.schedule(task, 2000);
		} else {
			exitApp();
		}
	}
	/**
	 * 隐藏底部菜单
	 */
	public static void Sethide() {
		tabWidget.setVisibility(View.GONE);
	}
	/**
	 * 显示底部菜单
	 */
	public static void Setshow() {
		tabWidget.setVisibility(View.VISIBLE);
	}
	/**
	 * 退出程序，调用退出接口
	 * 不管接口返回多少，一律强制退出
	 */
	private void exitApp() {

		JSONObject jsonObject = new JSONObject();

		String strUrl = "user/loginout.htm";
		RequestUtilNet.postDataToken(MainActivity.this, strUrl, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {
				// 注销网络监听
//				NetUtil.unregisterNetworkEventRecevier(MyApplication.getInstance());
//				Utils.finishAllActivities();
			}
		});

		Utils.finishAllActivities();
	}

	TimerTask taskToken = new TimerTask() {

		public void run() {

			Message message = new Message();
			message.what = 0x0030;
			mHandler.sendMessage(message);
		}
	};

	/**
	 * token续约
	 */
	private void requestToken() {
		if(TextUtils.isEmpty(MyApplication.getInstance().m_userInfo.toKen)){
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("token", MyApplication.getInstance().m_userInfo.toKen);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "user/keeptoken.htm";
		RequestUtilNet.postDataToken(MainActivity.this, strUrl, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {
				if (success != 0) {
					return;
				}
				try {
					jsonObject = jsonObject.getJSONObject("data");

					String strToken = jsonObject.getString("token");
					String strTimeOut = jsonObject.getString("timeout");

					Logs.v("zd", "续约token: " + strToken);
					Logs.v("zd", "续约tiemOut: " + strTimeOut);

					MyApplication.getInstance().m_userInfo.toKen = strToken;
					MyApplication.getInstance().m_userInfo.tokenTime = Integer.parseInt(strTimeOut);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	public Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			if(msg.what == 0x0030){
				requestToken();
			}

		}
	};

}
