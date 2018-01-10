package com.zhiduan.crowdclient;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.growingio.android.sdk.collection.GrowingIO;
import com.hyphenate.easeui.controller.EaseUI;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.zhiduan.crowdclient.data.UserInfo;
import com.zhiduan.crowdclient.db.DBHelper;
import com.zhiduan.crowdclient.im.DemoHelper;
import com.zhiduan.crowdclient.net.NetSettings;
import com.zhiduan.crowdclient.net.NetUtil;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.FileUtils;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
import com.zhiduan.crowdclient.util.VoiceHint;

import de.greenrobot.event.EventBus;

/**
 * @author HeXiuHui
 */
public class MyApplication extends Application {
	public static MyApplication instance;

	// 扫描处理
	public Handler scanHandler;
	// 屏幕宽、高
	public static int s_nScreenWidth = -1;
	public static int s_nScreenHeight = -1;
	public static Activity baseActivity;

	public NetSettings m_netSettings = new NetSettings();
	public UserInfo m_userInfo = new UserInfo();

	public static long withdrawDepositTime = 0;
	public static int Wallet_Activate = 0;

	public String sendTime = "35916884898255966"; // 加密time
	public String mRandom = "cdefjA"; // 加密需要的随机数

	/** Activity被回收时，保存全局变量的版本号（每保存一次，自增1） */
	public int m_nSaveInstanceStateVersion = 0;

	// 保存activity容器
	public List<Activity> activityList = new LinkedList<Activity>();

	/** 个推client id */
	public String m_strPushMessageClientId = "";

	/** 刷新未上传数量 handler */
	public Handler m_refreshGetOrderHandler = null;

	/** 刷新任务审核中 handler */
	public Handler m_refreshAuditHandler = null;

	/** 刷新任务已结算handler */
	public Handler m_refreshPaymentHandler = null;

	protected static ImageLoader imageLoader = ImageLoader.getInstance();
	protected static DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类
	private static EventBus mEventBus;

	public static Context applicationContext;
	// login user name
	public final String PREF_USERNAME = "username";

	/**
	 * nickname for current user, the nickname instead of ID be shown when user receive notification from APNs
	 */
	public static String currentUserNick = "";

	public void onCreate() {
		super.onCreate();
		com.umeng.socialize.Config.DEBUG = true;
		UMShareAPI.get(this);

		applicationContext = this;

		instance = this;

		Constant.initURL(3);//初始化服务器及相关地址1-测试 2-pre 3-正式库

		EaseUI.getInstance().init(this, null);

		//init demo helper
		DemoHelper.getInstance().init(applicationContext);
		
		// 配置友盟分享平台的APPkey
		PlatformConfig.setWeixin("wxacac0cf7d181974c", "fd4246d1869ea2be190ff8bc2c2ca743");
		PlatformConfig.setSinaWeibo("269917979", "5b3caa31e7e1ee3a12912501703934da");
		com.umeng.socialize.Config.REDIRECT_URL="http://sns.whalecloud.com/sina2/callback";
		PlatformConfig.setQQZone("1105477268", "01XqZBJ8wBy5ckKf");

		initImageLoader();
		if (mEventBus == null) {
			mEventBus = EventBus.getDefault();
			mEventBus.register(this);
		}

		new DBHelper(this);
		DBHelper.SQLiteDBHelper.getWritableDatabase();

		VoiceHint.initSound(instance);
		// Utils.initPDAScanner(getApplicationContext()); //暂时不要

		if (m_nSaveInstanceStateVersion == 0) {
			// 启动网络监听
			NetUtil.registerNetworkEventRecevier(getApplicationContext());
		}

		GrowingIO.startTracing(this, "9a6450ac63580527");
		GrowingIO.setScheme("growing.934e4e542ba42a92");

		// 捕捉异常日志
		// StrictMode.setThreadPolicy(new
		// StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
		// .detectNetwork().penaltyLog().build());
		// StrictMode.setVmPolicy(new
		// StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
		// .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
		// Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
	}

	// 单例模式中获取唯一的GTApplication实例
	public static MyApplication getInstance() {
		return instance;
	}

	/**
	 * 初始化ImageLoader
	 */
	private void initImageLoader() {

		// 如果上一次删除文件不是今天，则删除文件
		if (!SharedPreferencesUtils.getParam(Constant.SP_DELETE_FILE_DAY, "")
				.equals(CommandTools.getChangeDate(-7))) {

			FileUtils.deleteDir(Constant.cacheDir.toString());
			SharedPreferencesUtils.setParam(Constant.SP_DELETE_FILE_DAY,
					CommandTools.getChangeDate(-7));
		}

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCache(new UnlimitedDiscCache(Constant.cacheDir))
				// 缓存路径
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.build();// 开始构建
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 多张图片加载处理
	 * 
	 * @return
	 */
	public static ImageLoader getImageLoader() {

		if (imageLoader == null) {
			imageLoader = ImageLoader.getInstance();
		}

		return imageLoader;
	}

	/**
	 * 捕捉崩溃日志 崩溃信息发送到邮箱
	 */
	private UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {

			String info = null;
			ByteArrayOutputStream baos = null;
			PrintStream printStream = null;
			try {
				baos = new ByteArrayOutputStream();
				printStream = new PrintStream(baos);
				ex.printStackTrace(printStream);
				byte[] data = baos.toByteArray();
				info = new String(data);
				data = null;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

				try {
					if (printStream != null) {
						printStream.close();
					}
					if (baos != null) {
						baos.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				final String deviceid = CommandTools.getMIME(baseActivity);
				final String strTitle = "爱学派货源版---" + deviceid + "----";
				final String strBody = info;

				// 友盟统计
				MobclickAgent.reportError(baseActivity, strBody);

				new Thread(new Runnable() {
					@Override
					public void run() {
						CrashLogUtil.send_log(strTitle, strBody);// 上传到175服务器
					}
				}).start();
			} catch (Exception exs) {
				Log.e("zdkj", exs.getMessage());
			}
		}
	};

	/**
	 * 图片加载
	 * 
	 * @return
	 */
	public DisplayImageOptions getOptions() {

		if (options == null) {
			options = new DisplayImageOptions.Builder()
			// .showStubImage(R.drawable.male)// 设置图片下载期间显示的图片 ，暂时去掉这个属性
			.showImageForEmptyUri(R.drawable.head_portrait) // 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.head_portrait) // 设置图片加载或解码过程中发生错误显示的图片
			.cacheInMemory().cacheOnDisc()// 下载的图片缓存在内存卡中
			.bitmapConfig(Config.RGB_565).build();
		}

		return options;
	}

	/**
	 * 图片加载、加载失败则不显示图片
	 * 
	 * @return
	 */
	public DisplayImageOptions getOptionsNoPic() {

		if (options == null) {
			options = new DisplayImageOptions.Builder()
			// .showStubImage(R.drawable.male)// 设置图片下载期间显示的图片 ，暂时去掉这个属性
			//			.showImageForEmptyUri(0) // 设置图片Uri为空或是错误的时候显示的图片
			//			.showImageOnFail(0) // 设置图片加载或解码过程中发生错误显示的图片
			.cacheInMemory().cacheOnDisc()// 下载的图片缓存在内存卡中
			.bitmapConfig(Config.RGB_565).build();
		}

		return options;
	}

	/**
	 * 订单列表图片加载
	 * 
	 * @return
	 */
	public static DisplayImageOptions getOrderListOptions() {

		if (options == null) {
			options = new DisplayImageOptions.Builder()
			// .showStubImage(R.drawable.male)// 设置图片下载期间显示的图片 ，暂时去掉这个属性
			.showImageForEmptyUri(R.drawable.icon_order_list) // 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.icon_order_list) // 设置图片加载或解码过程中发生错误显示的图片
			.cacheInMemory().cacheOnDisc()// 下载的图片缓存在内存卡中
			.bitmapConfig(Config.RGB_565).build();
		}

		return options;
	}

	/**
	 * 订单详情图片加载
	 * 
	 * @return
	 */
	public static DisplayImageOptions getOrderDetailOptions() {

		if (options == null) {
			options = new DisplayImageOptions.Builder()
			// .showStubImage(R.drawable.male)// 设置图片下载期间显示的图片 ，暂时去掉这个属性
			.showImageForEmptyUri(R.drawable.icon_order_detail) // 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.icon_order_detail) // 设置图片加载或解码过程中发生错误显示的图片
			.cacheInMemory().cacheOnDisc()// 下载的图片缓存在内存卡中
			.bitmapConfig(Config.RGB_565).build();
		}

		return options;
	}

	public EventBus getEventBus() {

		return mEventBus;
	}

	public void onEventMainThread(Message msg) {

	}
}
