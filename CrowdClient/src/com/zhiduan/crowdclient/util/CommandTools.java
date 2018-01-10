package com.zhiduan.crowdclient.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;

/** 
 * 工具类
 *
 * @date 2016-5-20 下午2:04:19
 * 
 */
public class CommandTools {
	private static TelephonyManager telephonemanage;
	private static Toast toast;
	private static int imagt_halfWidth = 20;

	@SuppressLint("SimpleDateFormat")
	public static String getTimes() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return dateFormat.format(date).toString();
	}

	/**
	 * 将bitmap转换成base64字符串
	 * 
	 * @param bitmap
	 * @return base64 字符串
	 */
	public static String bitmap2String(Bitmap bitmap, int bitmapQuality) {
		String string = null;
		try {
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, bitmapQuality, bStream);
			byte[] bytes = bStream.toByteArray();
			string = Base64.encodeToString(bytes, Base64.NO_WRAP);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return string;
	}
	public static String bitmapToBase64(Bitmap bitmap) {  

		String result = null;  
		ByteArrayOutputStream baos = null;  
		try {  
			if (bitmap != null) {  
				baos = new ByteArrayOutputStream();  
				bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);  

				baos.flush();  
				baos.close();  

				byte[] bitmapBytes = baos.toByteArray();  
				result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);  
			}  
		} catch (IOException e) {  
			e.printStackTrace();  
		} finally {  
			try {  
				if (baos != null) {  
					baos.flush();  
					baos.close();  
				}  
			} catch (IOException e) {  
				e.printStackTrace();  
			}  
		}  
		return result;  
	}  

	public static Bitmap getBitmapFromPath(String path) {

		if (!new File(path).exists()) {
			System.err.println("getBitmapFromPath: file not exists");
			return null;
		}

		byte[] buf = new byte[1024 * 1024];// 1M
		Bitmap bitmap = null;

		try {

			FileInputStream fis = new FileInputStream(path);
			int len = fis.read(buf, 0, buf.length);
			bitmap = BitmapFactory.decodeByteArray(buf, 0, len);
			if (bitmap == null) {
				System.out.println("len= " + len);
				System.err
				.println("path: " + path + "  could not be decode!!!");
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

		return bitmap;
	}

	public static Bitmap convertToBitmap(String path, int w, int h) {

		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为ture只获取图片大小
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// 返回为空
		BitmapFactory.decodeFile(path, opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		float scaleWidth = 0.f, scaleHeight = 0.f;
		if (width > w || height > h) {
			// 缩放
			scaleWidth = ((float) width) / w;
			scaleHeight = ((float) height) / h;
		}
		opts.inJustDecodeBounds = false;
		float scale = Math.max(scaleWidth, scaleHeight);
		opts.inSampleSize = (int)scale;
		WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
		return Bitmap.createScaledBitmap(weak.get(), w, h, true);
	}

	/**
	 * 身份证号码验证
	 * @param text
	 * @return
	 * 我国当前的身份证号分为三种：
	 * 一、15位身份证号
	 * 二、18位身份证号（前17位位数字，最后一位为字母x/X）
	 * 三、18位身份证号（18位都是数字）
	 */
	public static boolean personIdValidation(String text) {

		if(text == null || text.equals("")){
			return false;
		}

		String regx = "[0-9]{17}x";
		String regx1 = "[0-9]{17}X";
		String regx2 = "[0-9]{15}";
		String regx3 = "[0-9]{18}";

		return text.matches(regx) || text.matches(regx1) || text.matches(regx2) || text.matches(regx3);
	}
	/**
	 * 获取当前时间精确到毫秒
	 * 
	 * @return
	 */
	static String strLastTime;
	public static String initDataTime() {

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS"); // 精确到毫秒
		String suffix = fmt.format(new Date());

		strLastTime = MyApplication.getInstance().sendTime;

		boolean flag = strLastTime.equals(suffix);
		while (flag == true) {

			suffix = fmt.format(new Date());
			strLastTime = MyApplication.getInstance().sendTime;
			flag = strLastTime.equals(suffix);

			try {
				Thread.sleep(300);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return suffix;
	}

	/**
	 * 获取PDA SN号
	 * 
	 * @param context
	 * @return
	 */
	public static String getMIME(Context context) {
		telephonemanage = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			String sn = telephonemanage.getDeviceId();
			return sn;
		} catch (Exception e) {
			Logs.e("MIME", e.getMessage());
			return "00000000000";
		}
	}

	/**
	 * 获取6位随机数
	 * 
	 * @return
	 */
	public static String CeShi() {
		String str = "";
		for (int i = 0; i < 6; i++) {// 你想生成几个字符的，就把4改成几，如果改成1,那就生成一个随机字母．
			str = str + (char) (Math.random() * 26 + 'a');
		}
		Logs.i("hexiuhui==", "输出随机生成的字符串" + str);
		return str;
	}

	/**
	 * 弹出toast提示 防止覆盖
	 * 
	 * @param msg
	 */
	public static void showToast(String msg) {

		if (toast == null) {
			toast = Toast.makeText(MyApplication.getInstance(), msg + "", Toast.LENGTH_SHORT);
		} else {
			toast.setText(msg);
		}
		toast.show();
	}

	/**
	 * 不会覆盖
	 * @param msg
	 */
	public static void showLongToast(String msg) {

		Toast.makeText(MyApplication.getInstance(), msg + "", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 
	 * @param plain
	 *            明文
	 * @return 32位小写密文
	 */
	public static String encryption(String plain) {

		String re_md5 = new String();
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("md5");
			md.update(plain.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			re_md5 = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return re_md5;
	}

	public static String str16(String CertId){
		if (CertId.length() < 16) {

			String str = "\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
			CertId += str;

			CertId = CertId.substring(0, 16);
		}

		Logs.e("Test", "----------" + CertId.length());
		// return new String(CertId.toString().getBytes(), "utf-8");
		return CertId;
	}

	/**
	 * 强制关闭软键盘
	 */
	public static void hidenKeyboars(Context context, View view) {

		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * 验证手机是否合法
	 * 
	 * @param mobiles
	 *            传入的手机号
	 * @return true 合法 false 不合法
	 */
	public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][34587]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}

	/**
	 * yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getTime() {

		Date nowdate = new Date(); // 当前时间
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(nowdate);
	}

	/**
	 * 获取年月日
	 * @return
	 */
	public static String getDate() {

		Date nowdate = new Date(); // 当前时间
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");
		return dateFormat.format(nowdate);
	}

	/**
	 * yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getTime(String time) {

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat(
				"MM-dd HH:mm");

		try {
			Date parse = dateFormat.parse(time);
			return dateFormat1.format(parse);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 年月日拼接
	 * type=0 年：月：日  00:00:00
	 * type=1 年：月：日  23:59:59
	 * type=其他： 年月日
	 * @return
	 */
	public static String getTimeDate(int type) {

		Date nowdate = new Date(); // 当前时间
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		String nowDate = dateFormat.format(nowdate);

		if(type == 0){
			return nowDate + " 00:00:00";
		}else if(type == 1){
			return nowDate + " 23:59:59";
		}

		return nowDate;
	}


	/**
	 * 获取当前上下文
	 * @return
	 */
	public static Activity getGlobalActivity(){

		Class<?> activityThreadClass;
		try {
			activityThreadClass = Class.forName("android.app.ActivityThread");
			Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
			Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
			activitiesField.setAccessible(true);
			Map<?, ?> activities = (Map<?, ?>) activitiesField.get(activityThread);
			for(Object activityRecord:activities.values()){
				Class<? extends Object> activityRecordClass = activityRecord.getClass();
				Field pausedField = activityRecordClass.getDeclaredField("paused");
				pausedField.setAccessible(true);
				if(!pausedField.getBoolean(activityRecord)) {
					Field activityField = activityRecordClass.getDeclaredField("activity");
					activityField.setAccessible(true);
					Activity activity = (Activity) activityField.get(activityRecord);
					return activity;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 生成二维码
	 * 
	 * @param s
	 * @param mBitmap
	 * @return
	 * @throws WriterException
	 * 支持中文编码
	 */
	public static Bitmap creatTwoBitMap(String s, Bitmap mBitmap)
			throws WriterException {

		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

		BitMatrix matrix = new MultiFormatWriter().encode(s,
				BarcodeFormat.QR_CODE, 300, 300, hints);

		int width = matrix.getWidth();

		int height = matrix.getHeight();

		int halfw = width / 2;
		int halfy = height / 2;

		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x > halfw - imagt_halfWidth && x < halfw + imagt_halfWidth
						&& y > halfy - imagt_halfWidth
						&& y < halfy + imagt_halfWidth) {

					if(mBitmap != null){

						pixels[y * width + x] = mBitmap.getPixel(x - halfw
								+ imagt_halfWidth, y - halfy + imagt_halfWidth);
					}

				} else {

					if (matrix.get(x, y)) {

						pixels[y * width + x] = 0xff000000;
					}else {
						pixels[y * width + x] = 0xffffffff;
					}
				}

			}

		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_4444);

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

		return bitmap;
	}

	/**
	 * 生成二维码(中间没图片)
	 * 
	 * @param s
	 * @param mBitmap
	 * @return
	 * @throws WriterException
	 * 支持中文编码
	 */
	public static Bitmap createQRImage(String url)
	{
		Bitmap bitmap = null;
		try
		{
			//判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1)
			{
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, 266, 266, hints);
			int[] pixels = new int[266 * 266];
			//下面这里按照二维码的算法，逐个生成二维码的图片，
			//两个for循环是图片横列扫描的结果
			for (int y = 0; y < 266; y++)
			{
				for (int x = 0; x < 266; x++)
				{
					if (bitMatrix.get(x, y))
					{
						pixels[y * 266 + x] = 0xff000000;
					}
					else
					{
						pixels[y * 266 + x] = 0xffffffff;
					}
				}
			}
			//生成二维码图片的格式，使用ARGB_8888
			bitmap = Bitmap.createBitmap(266, 266, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, 266, 0, 0, 266, 266);
			//显示到一个ImageView上面

		}
		catch (WriterException e)
		{
			e.printStackTrace();
		}
		return bitmap;
	}
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取1000以内随机数
	 * @return
	 */
	public static String getRandomNumber(){

		Random random = new Random();
		return (random.nextInt(100)) + "";
	}

	// 获取版本号
	public static String getVersionName(Context context) {
		try {
			PackageManager pm = MyApplication.getInstance().getPackageManager();
			String versionName = "";
			try {
				PackageInfo packageInfo = pm.getPackageInfo(
						context.getPackageName(), 0);
				versionName = packageInfo.versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return versionName;
		} catch (Exception e) {
			return "1.0";
			// TODO: handle exception
		}

	}

	/**
	 * 获取版本号
	 * @param context
	 * @return
	 */
	public static int getVersionCode() {

		Context context = MyApplication.getInstance();
		int versionCode = 1;
		try {
			PackageManager pm = context.getPackageManager();

			try {
				PackageInfo packageInfo = pm.getPackageInfo(
						context.getPackageName(), 0);
				versionCode = packageInfo.versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return versionCode;
		} catch (Exception e) {
			return versionCode;
		}

	}

	/**
	 * 获取PDA SN号
	 * 
	 * @param context
	 * @return
	 */
	public static String getDevId() {
		Build bd = new Build();  
		String model = bd.MODEL;
		return model;
	}

	/**
	 * 10000  转成  100.00
	 * @return
	 */
	public static String longTOString(long balance){
		double yue=balance/100.0;
		DecimalFormat  df   = new DecimalFormat("######0.00");
		return df.format(yue);
	}

	/**
	 * 时间加一天
	 * @return
	 */
	public static String timeAddAday(String time){
		//将String类型转成Date类型
		//2016-06-07 17:46:10
		String tablename=null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date=sdf.parse(time);
			//1月25号 22:00
			Calendar   calendar   =   new   GregorianCalendar(); 
			calendar.setTime(date); 
			calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动 
			date=calendar.getTime();   //这个时间就是日期往后推一天的结果 
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			tablename=dateFormat.format(date);
		} catch (Exception ex) {

		}
		return tablename;
	}
	@SuppressLint("SimpleDateFormat")
	public static String getTimess() {
		Date nowdate = new Date(); // 当前时间
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(nowdate);
	}


	/**
	 * 验证邮箱是否合法
	 * 
	 * @param mobiles
	 *            传入的邮箱
	 * @return true 合法 false 不合法
	 */
	public static boolean isMailbox(String mobiles) {
		String telRegex = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}
	/**
	 * 用正则判断某个字符串是否为数字
	 * 
	 * @param str
	 *            待 判断的字符串
	 * @return true 是; false 否
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 弹出信息，需要手动关闭
	 * 
	 * @param context
	 * @param strMsg
	 */
	public static void showDialog(final Context context, final String strMsg) {

		Activity mActivity = (Activity) context;
		if (mActivity.isFinishing()) {
			Logs.v("zd", "当前activity界面已关闭，不能显示对话框");
			return;
		}

		new AlertDialog.Builder(context).setTitle("提示").setMessage(strMsg + "")
		.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();

	}
	public interface OkAndCancelListener {
		public void ok();

		public void cancel();
	}
	/**
	 * 弹出信息，有：“确定”“取消” 带回调
	 * 
	 * @param context
	 * @param strMsg
	 * @param listener
	 */
	public static void showDialogListener(final Context context,
			final String strMsg, final OkAndCancelListener listener) {

		Activity mActivity = (Activity) context;
		if (mActivity.isFinishing()) {
			Logs.v("zd", "当前activity界面已关闭，不能显示对话框");
			return;
		}

		new AlertDialog.Builder(context).setTitle("提示").setMessage(strMsg + "")
		.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				listener.cancel();
			}
		})
		.setNegativeButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				listener.ok();
			}
		}).show();

	}

	/**
	 * 过滤特殊字符
	 * 
	 * @param str
	 * @return
	 */
	public static String escapeOtherChar(String str) {

		String reg = "[^a-zA-Z0-9\u4E00-\u9FA5_]";
		return str.replaceAll(reg, " ");
	}

	/**
	 * 手机号中间4位替换为*
	 * 187****2241
	 * @param phone
	 * @return
	 */
	public static String replacePhone(String phone){

		if(TextUtils.isEmpty(phone) || phone.length() != 11){
			return phone;
		}

		return phone.substring(0, 3) + "****" + phone.substring(7, 11);
	}

	/**
	 * 当前日期往前后推n天
	 * @param n
	 * @return
	 */
	public static String getChangeDate(int n){

		Calendar cal = Calendar.getInstance(); 
		cal.add(Calendar.DATE, n); 
		Date date = cal.getTime();

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

		return sf.format(date);
	}

	/***
	 * 图片的缩放方法
	 * 
	 * @param bgimage
	 *            ：源图片资源
	 * @param newWidth
	 *            ：缩放后宽度
	 * @param newHeight
	 *            ：缩放后高度
	 * @return
	 */
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}
	public static Bitmap decodeFile(File f){   
		try {   
			//解码图像大小,对图片进行缩放...防止图片过大导致内存溢出...  
			BitmapFactory.Options o = new BitmapFactory.Options();//实例化一个对象... 

			o.inJustDecodeBounds = true;//这个就是Options的第一个属性,设置为true的时候，不会完全的对图片进行解码操作,不会为其分配内存，只是获取图片的基本信息...               

			BitmapFactory.decodeStream(new FileInputStream(f),null,o); //以码流的形式进行解码....

			/*
			 * 下面也就是对图片进行的一个压缩的操作...如果图片过大，最后会根据指定的数值进行缩放...
			 * 找到正确的刻度值，它应该是2的幂.
			 * 这里我指定了图片的长度和宽度为70个像素...
			 * 
			 * */

			final int REQUIRED_SIZE=70;   
			int width_tmp=o.outWidth, height_tmp=o.outHeight;   
			int scale=1;   
			while(true){   
				if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)   
					break;   
				width_tmp/=2;   
				height_tmp/=2;   
				scale*=2;   
			}   

			BitmapFactory.Options o2 = new BitmapFactory.Options(); //这里定义了一个新的对象...获取的还是同一张图片...
			o2.inSampleSize=scale;   //对这张图片设置一个缩放值...inJustDecodeBounds不需要进行设置...
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2); //这样通过这个方法就可以产生一个小的图片资源了...
		} catch (Exception e) {}   
		return null;   
	}

	/**
	 * 显示一个圆角的图片
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap showRoundConerBitmap(Drawable drawable, float roundPx) {
		Bitmap bitmap = drawable2Bitmap(drawable);
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	//drawable转bitmap
	public static Bitmap drawable2Bitmap(Drawable drawable) {  
		if (drawable instanceof BitmapDrawable) {  
			return ((BitmapDrawable) drawable).getBitmap();  
		} else if (drawable instanceof NinePatchDrawable) {  
			Bitmap bitmap = Bitmap  
					.createBitmap(  
							drawable.getIntrinsicWidth(),  
							drawable.getIntrinsicHeight(),  
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
									: Bitmap.Config.RGB_565);  
			Canvas canvas = new Canvas(bitmap);  
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  
					drawable.getIntrinsicHeight());  
			drawable.draw(canvas);  
			return bitmap;  
		} else {  
			return null;  
		}  
	}

	/**
	 * 显示一个圆角的图片
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap showRoundConerBitmap(Bitmap bitmap, float roundPx) {
		if(bitmap==null){
			Logs.e("wufuqi---", "图片为空");
			return null;
		}

		//		Log.v("zd", "bitmap = " + bitmap.getByteCount()/1024);
		//缩略图形式处理，防止图片太大导致内存溢出
		return ThumbnailUtils.extractThumbnail(bitmap, 100, 100);
		//		try{
		//			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
		//					bitmap.getHeight(), Config.ARGB_8888);
		//			Canvas canvas = new Canvas(output);
		//
		//			final int color = 0xff424242;
		//			final Paint paint = new Paint();
		//			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		//			final RectF rectF = new RectF(rect);
		//
		//			paint.setAntiAlias(true);
		//			canvas.drawARGB(0, 0, 0, 0);
		//			paint.setColor(color);
		//			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		//
		//			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		//			canvas.drawBitmap(bitmap, rect, rect, paint);
		//
		//			return output;
		//		}catch(Exception e){
		//			e.printStackTrace();
		//		}
		//
		//		return null;
	}
	/**
	 * 给图片加毛玻璃效果
	 * @param context
	 * @param sentBitmap
	 * @param radius
	 * @return
	 */
	public static Bitmap fastblur(Context context, Bitmap sentBitmap, int radius) {

		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int temp = 256 * divsum;
		int dv[] = new int[temp];
		for (i = 0; i < temp; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
			sir[1] = (p & 0x00ff00) >> 8;
		sir[2] = (p & 0x0000ff);
		rbs = r1 - Math.abs(i);
		rsum += sir[0] * rbs;
		gsum += sir[1] * rbs;
		bsum += sir[2] * rbs;
		if (i > 0) {
			rinsum += sir[0];
			ginsum += sir[1];
			binsum += sir[2];
		} else {
			routsum += sir[0];
			goutsum += sir[1];
			boutsum += sir[2];
		}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
			sir[1] = (p & 0x00ff00) >> 8;
			sir[2] = (p & 0x0000ff);

			rinsum += sir[0];
			ginsum += sir[1];
			binsum += sir[2];

			rsum += rinsum;
			gsum += ginsum;
			bsum += binsum;

			stackpointer = (stackpointer + 1) % div;
			sir = stack[(stackpointer) % div];

			routsum += sir[0];
			goutsum += sir[1];
			boutsum += sir[2];

			rinsum -= sir[0];
			ginsum -= sir[1];
			binsum -= sir[2];

			yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
						| (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		return (bitmap);
	}

	public static int getPhoneCount(Context context,String phune){
		Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
				null, "type=2 and DURATION!=0 and number="+phune, null, null);

		return cursor.getCount();
	}
	/** 
	 * Compress image by size, this will modify image width/height.  
	 * Used to get thumbnail 
	 *  
	 * @param image 
	 * @param pixelW target pixel of width 
	 * @param pixelH target pixel of height 
	 * @return 
	 */  
	public static Bitmap ratio(Bitmap image, float pixelW, float pixelH) {  
		Bitmap bitmap = null;	
		try {

			ByteArrayOutputStream os = new ByteArrayOutputStream();  
			image.compress(Bitmap.CompressFormat.JPEG, 100, os);  
			if( os.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出      
				os.reset();//重置baos即清空baos    
				image.compress(Bitmap.CompressFormat.JPEG, 80, os);//这里压缩50%，把压缩后的数据存放到baos中    
			}    
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());    
			BitmapFactory.Options newOpts = new BitmapFactory.Options();    
			//开始读入图片，此时把options.inJustDecodeBounds 设回true了    
			newOpts.inJustDecodeBounds = true;  
			newOpts.inPreferredConfig = Config.RGB_565;  
			bitmap= BitmapFactory.decodeStream(is, null, newOpts);    
			newOpts.inJustDecodeBounds = false;    
			int w = newOpts.outWidth;    
			int h = newOpts.outHeight;    
			float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了  
			float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了  
			//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可    
			int be = 1;//be=1表示不缩放    
			if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放    
				be = (int) (newOpts.outWidth / ww);    
			} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放    
				be = (int) (newOpts.outHeight / hh);    
			}    
			if (be <= 0) be = 1;    
			newOpts.inSampleSize = be;//设置缩放比例    
			//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了    
			is = new ByteArrayInputStream(os.toByteArray());    
			bitmap = BitmapFactory.decodeStream(is, null, newOpts);  
			//压缩好比例大小后再进行质量压缩  
			//      return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;  
	}  

	/**
	 * 读取uri所在的图片
	 * @param uri
	 * @return
	 */
	public static Bitmap getBitmapFromUri(Context context, Uri uri){

		try {

			Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
			return ThumbnailUtils.extractThumbnail(bitmap, 100, 100);
		} catch (Exception e) {
			Log.e("[Android]", e.getMessage());
			Log.e("[Android]", "目录为：" + uri);
			e.printStackTrace();
			return null;
		}
	}

	private static final String[]STORE_IMAGES={
		MediaStore.Images.Media.DISPLAY_NAME,
		MediaStore.Images.Media.LATITUDE,
		MediaStore.Images.Media.LONGITUDE,
		MediaStore.Images.Media._ID,
		MediaStore.Images.Media.BUCKET_ID,
		MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
	};

	/**
	 * 唤醒屏幕
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	public static void wakeUpAndUnlock(Context context){

		try{

			KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
			KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
			//解锁
			kl.disableKeyguard();
			//获取电源管理器对象
			PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
			//获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
			PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
			//点亮屏幕
			wl.acquire();
			//释放
			wl.release();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 手机震动
	 * @param context
	 * @param milliseconds震动时长
	 */
	public static void Vibrate(Context context, long milliseconds) {   

		Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);   
		vib.vibrate(milliseconds);   

		//        long [] pattern = {100,500,200,500};//停止 开启 停止 开启
		//        vib.vibrate(pattern, -1);//如果只想震动一次，index设为-1   
	} 

	/**
	 * 解决小米手机上获取图片路径为null的情况
	 * @param intent
	 * @return
	 */
	public static Uri geturi(android.content.Intent intent, Context context) {  
		Uri uri = intent.getData();  
		String type = intent.getType();  
		if (uri.getScheme().equals("file") && (type.contains("image/"))) {  
			String path = uri.getEncodedPath();  
			if (path != null) {  
				path = Uri.decode(path);  
				ContentResolver cr = context.getContentResolver();  
				StringBuffer buff = new StringBuffer();  
				buff.append("(").append(Images.ImageColumns.DATA).append("=")  
				.append("'" + path + "'").append(")");  
				Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI,  
						new String[] { Images.ImageColumns._ID },  
						buff.toString(), null, null);  
				int index = 0;  
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {  
					index = cur.getColumnIndex(Images.ImageColumns._ID);  
					// set _id value  
					index = cur.getInt(index);  
				}  
				if (index == 0) {  
					// do nothing  
				} else {  
					Uri uri_temp = Uri  
							.parse("content://media/external/images/media/"  
									+ index);  
					if (uri_temp != null) {  
						uri = uri_temp;  
					}  
				}  
			}  
		}  
		return uri;  
	}  

	/**
	 * 安装APK
	 */
	public static void install(File file, Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		// 告诉程序这是一个压缩文件
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 检查当前用户状态
	 * 是否登录，资料审核状态
	 * 只有登录且资料审核成功返回true，其他情况返回false
	 * @param context
	 * @return
	 */
	public static boolean checkUserStatus(Context context){

		if (TextUtils.isEmpty(MyApplication.getInstance().m_userInfo.toKen)) {
			DialogUtils.showLoginDialog(context);
			return false;
		}else if (MyApplication.getInstance().m_userInfo.verifyStatus == Constant.REVIEW_STATE_NOT_SUBMIT) {
			DialogUtils.showAuthDialog(context);
			return false;
		} else if (MyApplication.getInstance().m_userInfo.verifyStatus == Constant.REVIEW_STATE_SUBMITING) {
			DialogUtils.showReviewingDialog(context);
			return false;
		} else if (MyApplication.getInstance().m_userInfo.verifyStatus == Constant.REVIEW_STATE_FAIL){
			DialogUtils.showOneButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_5, "", "审核失败，请检查您提交的资料", "我知道了", null);
			return false;
		}

		return true;
	}

	/**
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
	 * 
	 * @param v
	 * @param event
	 * @return
	 */
	public static boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
					+ v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击EditText的事件，忽略它。
				return false;
			} else {
				// hideSoftInput(v.getWindowToken());
				return true;
			}
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
		return false;
	}

	/**
	 * 多种隐藏软件盘方法的其中一种
	 * 
	 * @param token
	 */
	public static void hideSoftInput(IBinder token, InputMethodManager im) {
		if (token != null) {
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 日期转换成秒数
	 * */
	public static long getSecFromDate(String expireDate){

		if(expireDate==null||expireDate.trim().equals(""))
			return 0;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=null;
		try{
			date=sdf.parse(expireDate);
			return (long)(date.getTime()/1000);
		} catch(ParseException e) {
			e.printStackTrace();
			return 0L;
		}
	}

	/**
	 * 传入一个时间，计算与当前的时间差
	 * @param expireDate
	 * @return
	 */
	public static long getCountTime(String expireDate){

		long transSec = 10 * 60;//转单时限，默认为10分钟

		long startSec = CommandTools.getSecFromDate(expireDate);
		long currSec = CommandTools.getSecFromDate(CommandTools.getTime());

		long desSec = (startSec + transSec - currSec) * 1000;

		if(desSec < 0){
			return 0;
		}

		return desSec;
	}
}


