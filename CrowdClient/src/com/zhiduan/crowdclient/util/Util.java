package com.zhiduan.crowdclient.util;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.SignForSelectInfo;
import com.zhiduan.crowdclient.view.GeneralDialog;
/**
 * @author hexiuhui
 */
public class Util {

	public static void initScreenSize(Context activity) {
		if (activity instanceof Activity) {
			// 得到屏幕分辨率
			WindowManager wm = ((Activity) activity).getWindowManager();
			Display display = wm.getDefaultDisplay(); // 为获取屏幕宽、高
			MyApplication.s_nScreenWidth = (int) display.getWidth();
			MyApplication.s_nScreenHeight = (int) display.getHeight();
		}
	}

	// 显示从服务器传回的错误信息
	public static void showServerReturnErrorMessage(final Context context, int nCode, String strMessage) {
		showServerReturnErrorMessageEx(context, nCode, strMessage, false);
	}

	// 显示从服务器传回的错误信息
	public static void showServerReturnErrorMessageEx(final Context context,
			final int nCode, final String strMessage,
			final boolean bCloseActivity /* 是否需要close Activity */) {
		if (bCloseActivity) {
			showServerReturnErrorMessageEx(context, nCode, strMessage,
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (context instanceof Activity) {
								((Activity) context).finish();
							}
						}
					});
		} else {
			showServerReturnErrorMessageEx(context, nCode, strMessage, null);
		}
	}

	// 显示从服务器传回的错误信息
	public static void showServerReturnErrorMessageEx(final Context context,
			final int nCode, final String strMessage,
			final OnClickListener clickListener) {
		Util.showNotifyMessage(context, "", strMessage, clickListener);
	}

	public static void showNotifyMessage(final Context context,
			final String strTitle, final String strMessage,
			final OnClickListener clickListener) {
//		showConfirmDialog(context, strTitle, strMessage, clickListener, false);
		
		GeneralDialog.showOneButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_7, strTitle, strMessage, "我知道了", null);
	}

	@SuppressLint("NewApi")
	public static void showConfirmDialog(final Context context,
			final String strTitle, final String strMsg,
			final OnClickListener onOkClickListener,
			final Boolean bShowCancelButton) {
		
		if(context == null){
			return;
		}
		View vContent = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null);
		final Dialog dlg = new Dialog(context, R.style.common_dialog);
		dlg.setContentView(vContent);
		dlg.setCanceledOnTouchOutside(false); // 点击窗口外区域不消失

		if (MyApplication.s_nScreenWidth == -1) {
			initScreenSize(context);
		}

		// 必须用代码调整dialog的大小
		android.view.WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
		lp.width = (int) (MyApplication.s_nScreenWidth * 0.95);
		// lp.height = (int) (MyApplication.m_nScreenHeight * 0.5);
		dlg.getWindow().setAttributes(lp);

		TextView tvTitle = (TextView) vContent.findViewById(R.id.tv_title);
		if (strTitle != null && !strTitle.isEmpty()) {
			tvTitle.setText(strTitle);
		} else {
			tvTitle.setVisibility(View.GONE);
		}

		TextView tvMsg = (TextView) vContent.findViewById(R.id.tv_msg);
		tvMsg.setText(strMsg);

		Button btnConfirm = (Button) vContent.findViewById(R.id.btn_confirm);
		View vTwoButtonArea = vContent.findViewById(R.id.ll_two_button_area);

		if (bShowCancelButton) {
			btnConfirm.setVisibility(View.GONE);

			Button btnOk = (Button) vContent.findViewById(R.id.btn_ok);

			assert (onOkClickListener != null);
			// btnOk.setOnClickListener(onOkClickListener);
			btnOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dlg.dismiss();
					onOkClickListener.onClick(v);
				}
			});

			Button btnCancel = (Button) vContent.findViewById(R.id.btn_cancel);
			btnCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dlg.dismiss();
				}
			});
		} else {
			vTwoButtonArea.setVisibility(View.GONE);

			btnConfirm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dlg.dismiss();

					if (onOkClickListener != null) {
						onOkClickListener.onClick(v);
					}
				}
			});
		}

		if (!((Activity) context).isFinishing()) {
			
			try{
				dlg.show();
			}catch(Exception e){
				e.printStackTrace();
			}
		
		}
	}

	// 显示json解析错误信息
	public static void showJsonParseErrorMessage(Context context) {
		showJsonParseErrorMessage(context, null);
	}

	public static void showJsonParseErrorMessage(final Context context, final OnClickListener clickListener) {
		showNotifyMessage(context, "", "从网络取到的数据错误", clickListener);
	}

	public static void showJsonParseErrorMessageEx(final Activity activity) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.finish();
			}
		};
		showJsonParseErrorMessage(activity, clickListener);
	}

	// 显示因网络问题产生的错误信息
	public static void showNetErrorMessage(Context context, int nStatusCode) {
		showNetErrorMessage(context, nStatusCode, null);
	}

	public static void showNetErrorMessage(Context context, int nStatusCode, final OnClickListener clickListener) {
		if (nStatusCode != 200) {
			showNotifyMessage(context, "", "网络连接失败", clickListener);
		} else {
			showNotifyMessage(context, "", "发生未知错误", clickListener);
		}
	}

	/** 设置未上传数量 */
	public static void setpLoadCount(TextView tv, int msgCount) {
		if (msgCount > 0) {
			if (msgCount > 99) {
				tv.setTextSize(8);
				tv.setText("99+");
			} else {
				tv.setTextSize(10);
				tv.setText(msgCount + "");
			}
			tv.setVisibility(View.VISIBLE);
		} else {
			tv.setVisibility(View.GONE);
		}
	}

	public static final int order_type = 0;  //订单
	
	//保存弹窗参数
	public static void saveDialog(Context context, String time) {
		SharedPreferences sp = context.getSharedPreferences(Constant.SAVE_SHOW_DIALOG_TIME, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		
		edit.putString("showTime", time);
		
		edit.commit();
	}
	
	// 保存未读订单数量  
	public static void saveNotNumber(Context context, int number, int type) {
		SharedPreferences sp = context.getSharedPreferences(Constant.SAVE_NOT_ORDER_NUMBER, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		
		edit.putInt("OrderNumber", number);
		
		edit.commit();
	}
	
	//删除未读订单数量
	public static void removeNotNumber(Context context, int type) {
		SharedPreferences sp = context.getSharedPreferences(Constant.SAVE_NOT_ORDER_NUMBER, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		if (type == order_type) {
			edit.remove("OrderNumber");
		} else {
			edit.remove("TaskNumber");
		}
		edit.commit();
	}
	
	
	// 保存分单、转单新通知
	public static void saveDelivery(Context context, String message, String bizId, String bizCode) {
		SharedPreferences sp = context.getSharedPreferences(Constant.PUSH_ASSIGNED_MESSAGE, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString("message", message);
		edit.putString("bizId", bizId);
		edit.putString("bizCode", bizCode);
		edit.commit();
	}
	
	//删除分单、转单通知信息
	public static void removeDelivery(Context context) {
		SharedPreferences sp = context.getSharedPreferences(Constant.PUSH_ASSIGNED_MESSAGE, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.remove("message");
		edit.remove("bizId");
		edit.remove("bizCode");
		edit.commit();
	}

	// 保存短信模版
	public static void saveTemplate(Context context, String templateName, String templateId) {
		SharedPreferences sp = context.getSharedPreferences("TemplateContent", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString("content", templateName);
		edit.putString("templateId", templateId);
		edit.commit();
	}
	
	public static void removeTemplate(Context context) {
		SharedPreferences sp = context.getSharedPreferences("TemplateContent", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.remove("content");
		edit.remove("templateId");
		edit.commit();
	}
	
	// 保存选择的快递公司
	public static void saveExpress(Context context, String name, String url, String code, String id, String gcode) {
		
		SharedPreferences sp = context.getSharedPreferences("Express", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString("express_id", id);
		edit.putString("express_name", name);
		edit.putString("express_url", url);
		edit.putString("express_code", code);
		edit.putString("express_gcode", gcode);
		edit.commit();
	}
	
	// 保存图片地址
	public static void saveFilePath(Context context,  String url, String waybillNo, String expressCompanyId, String fileName) {
		
		SharedPreferences sp = context.getSharedPreferences("Express", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString("filePath", url);
		edit.putString("waybillNo", waybillNo);
		edit.putString("expressCompanyId", expressCompanyId);
		edit.putString("fileName", fileName);
		edit.commit();
	}
	
	// 获取保存图片地址
	public static SignForSelectInfo getFilePath(Context context) {
		SharedPreferences sp = context.getSharedPreferences("Express", Activity.MODE_PRIVATE);
		SignForSelectInfo signForSelectInfo = new SignForSelectInfo();
		signForSelectInfo.filePath=sp.getString("filePath", "");
		signForSelectInfo.waybillNo=sp.getString("waybillNo", "");
		signForSelectInfo.expressCompanyId=sp.getString("expressCompanyId", "");
		signForSelectInfo.fullName=sp.getString("fileName", "");
		return signForSelectInfo;
	}

	// 保存是否选择快递公司的状态
	public static void saveExpressType(Context context, boolean s) {
		
		SharedPreferences sp = context.getSharedPreferences("Express", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putBoolean("isExpressType", s);
		edit.commit();
	}
	
	// 获取是否选择快递公司的状态
	public static boolean isExpressType(Context context) {
		SharedPreferences sp = context.getSharedPreferences("Express", Activity.MODE_PRIVATE);
		return sp.getBoolean("isExpressType", false);
	}

	// 保存锁定货位
	public static void saveLockLocation(Context context, boolean isLockLocation) {
		SharedPreferences sp = context.getSharedPreferences("EditSms", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putBoolean("location", isLockLocation);
		edit.commit();
	}
	
	// 保存设置时间
	public static void saveTime(Context context, int time) {
		SharedPreferences sp = context.getSharedPreferences("SetTime", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putInt("time", time);
		edit.commit();
	}
	
	// 保存设置上传数量
	public static void saveData(Context context, int time) {
		SharedPreferences sp = context.getSharedPreferences("SetData",Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putInt("data", time);
		edit.commit();
	}
	
	// 保存锁定运单号
	public static void saveLockBillCode(Context context, boolean isLockBillCode) {
		SharedPreferences sp = context.getSharedPreferences("EditSms", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putBoolean("billcode", isLockBillCode);
		edit.commit();
	}

	// 保存自增开关状态 true 开
	public static void incrementSet(Context context, boolean isLockBillCode) {
		SharedPreferences sp = context.getSharedPreferences("incrementSet", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putBoolean("increment_set", isLockBillCode);
		edit.commit();
	}
	
	// 保存自增开关状态 true 开
	public static void saveLocation(Context context, String location) {
		SharedPreferences sp = context.getSharedPreferences("Location", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString("location", location);
		edit.commit();
	}

	// 微信支付
	private static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public static byte[] httpPost(String url, String entity) {
		if (url == null || url.length() == 0) {
			Logs.e("nihao.....", "httpPost, url is null");
			return null;
		}

		HttpClient httpClient = getNewHttpClient();

		HttpPost httpPost = new HttpPost(url);

		try {
			httpPost.setEntity(new StringEntity(entity));
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse resp = httpClient.execute(httpPost);
			if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				Logs.e("nihao.....", "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());
				return null;
			}

			return EntityUtils.toByteArray(resp.getEntity());
		} catch (Exception e) {
			Logs.e("nihao.....", "httpPost exception, e = " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private static class SSLSocketFactoryEx extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	/**
	 * 获取当前格式化的日期，精确到秒 如：yyyyMMddHHmmss
	 * 
	 * @return
	 */
	public static String getAtPresentDate() {
		Date curDate = new Date(System.currentTimeMillis());
		return formatter.format(curDate);

	}

	/**
	 * 获取加密后的数据(暂时没有加密)
	 * 
	 * @return
	 */
	public static String getEncryptionData(String object) {
		// String json="";
		// try {
		// MCrypt mCrypt = new MCrypt();
		// json = Base64.encodeToString(mCrypt.encrypt(object),
		// Base64.NO_WRAP) + MyApplication.getInstance().mRandom;
		// } catch (Exception e) {
		// }
		return object;

	}

	/**
	 * 判断微信是否安装
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWeixinAvilible(Context context) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mm")) {
					return true;
				}
			}
		}

        return false;
    }
	
	/**
	 * 判断支付宝是否安装
	 * @param context
	 * @return
	 */
	public static boolean isAlipayAvilible(Context context) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.eg.android.AlipayGphone")) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	 /**
	  * 设置sp中是否激活
	  * @param c
	  * @param is
	  */
//	public static void setIsWallet(Context c,int is){
//		return false;
//	}

	/**
	 * 设置sp中是否激活
	 * 
	 * @param c
	 * @param is
	 */
	public static void setIsWallet(Context c, int is) {
		SharedPreferences settings = c.getSharedPreferences("IsWallet", 0);
		SharedPreferences.Editor localEditor = settings.edit();
		if (is == 0) {

			localEditor.putBoolean("is", false);
		} else {
			localEditor.putBoolean("is", true);

		}
		localEditor.commit();
	}

	/**
	 * 从sp中取出是否激活
	 * 
	 * @param c
	 * @param is
	 */
	public static boolean getIsWallet(Context c) {
		SharedPreferences settings = c.getSharedPreferences("IsWallet", 0);
		boolean boolean1 = settings.getBoolean("is", false);
		return boolean1;
	}
	
	// 防止按钮快速连击
	private static long s_lLastClickTime;

	public static boolean isFastDoubleClick() {
		long lCurTime = System.currentTimeMillis();
		long lPeriod = lCurTime - s_lLastClickTime;
		if (lPeriod > 0 && lPeriod < 2000) {
			return true;
		}

		s_lLastClickTime = lCurTime;
		return false;
	}
	
	private static final float BEEP_VOLUME = 0.10f;
	
	//只有在音声模式的时候播放声音提示
	public static void playMusic(Context context, int musicUri) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int ringerMode = audioManager.getRingerMode();
		if (ringerMode == AudioManager.RINGER_MODE_NORMAL) {
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = context.getResources().openRawResourceFd(musicUri);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}
	
	public static final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	
	/**
	 * 判断给定字符串时间是否为今日(效率不是很高，不过也是一种方法)
	 * 
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate) {
		if ("".equals(sdate)) {
			return false;
		} else {
			boolean b = false;
			Date time = toDate(sdate);
			Date today = new Date();
			if (time != null) {
				String nowDate = dateFormater2.get().format(today);
				String timeDate = dateFormater2.get().format(time);
				if (nowDate.equals(timeDate)) {
					b = true;
				}
			}
			return b;
		}
	}

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater.get().parse(sdate);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
}
