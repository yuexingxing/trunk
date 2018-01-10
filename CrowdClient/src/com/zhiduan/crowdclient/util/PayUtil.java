package com.zhiduan.crowdclient.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.apache.http.NameValuePair;
import org.json.JSONObject;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/** 
 * 支付相关参数
 * 
 * @author yxx
 *
 * @date 2016-12-6 下午5:35:49
 * 
 */
public class PayUtil {

	//	List<NameValuePair> signParams = new LinkedList<NameValuePair>();
	//	signParams.add(new BasicNameValuePair("appid", req.appId));
	//	signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
	//	signParams.add(new BasicNameValuePair("package", req.packageValue));
	//	signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
	//	signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
	//	signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
	//
	//	//req.sign = PayUtil.genAppSign(signParams);
	//	//Log.v("zd", signParams);
	//	Log.v("zd", "sign:" + jsonObject.optString("sign"));
	//	Log.v("zd", "signParams:" + PayUtil.genAppSign(signParams));
	
	public static final int PAY_RESULT = 0x1101;//支付结果回调

	public static String pay_getapppayinfo = "acct/pay/getapppayinfo.htm";//获取收银台展示数据
	public static String pay_getappbizinfo = "acct/pay/getappbizinfo.htm";//获取收银台支付数据

	public static abstract class PayCallback {

		public abstract void callback(int success, String remark, Object object);
	}


	/**
	 * 获取签名
	 */
	public static String genAppSign(List<NameValuePair> params) {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}

		sb.append("key=");
		sb.append(Constant.PAY_APPID); // 这里必须要用商户的KEY代码,我靠..

		Log.v("zd", sb.toString());
		String appSign = MD5(sb.toString());
		return appSign;
	}

	public static String MD5(String str) {

		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.toString().toUpperCase();
	}


	/**
	 * 判断是否安装微信客户端
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
	 * 获取收银台展示数据
	 * @param context
	 * @param jsonObject
	 * @param callback
	 */
	public static void getAppPayInfo(Context context, JSONObject jsonObject, final PayCallback callback){

		RequestUtilNet.postDataToken(context, PayUtil.pay_getapppayinfo, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {
				// TODO Auto-generated method stub
				if(success != 0){
					CommandTools.showToast(remark);
					return;
				}

				callback.callback(success, remark, jsonObject);
			}
		});
	}

}
