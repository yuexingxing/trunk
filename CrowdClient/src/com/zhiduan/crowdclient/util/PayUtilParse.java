package com.zhiduan.crowdclient.util;

import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhiduan.crowdclient.pay.ali.AuthResult;
import com.zhiduan.crowdclient.pay.ali.PayResult;
import com.zhiduan.crowdclient.util.PayUtil.PayCallback;

import de.greenrobot.event.EventBus;

/** 
 * 支付类处理--MVP模式
 * 
 * @author yxx
 *
 * @date 2017-1-10 下午8:15:57
 * 
 */
public class PayUtilParse {

	public static Context context;
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_AUTH_FLAG = 2;
	public static final int TAKEPHOTO_H5 = 3;

	public static String bizId = null;
	public static CallBackFunction callBackFunction;
	public static String base64;

	private static IWXAPI wxAPI;
	public static PayReq req = new PayReq();

	private static EventBus mEventBus;

	public static void initPayInfo(Context mContext, EventBus eventBus){

		mEventBus = eventBus;

		context = mContext;
		wxAPI = WXAPIFactory.createWXAPI(mContext, Constant.PAY_APPID);
		wxAPI.registerApp(Constant.PAY_APPID);
	}

	/**
	 * 获取支付信息
	 * @param context
	 * @param str
	 */
	public static void getAppPayInfo(String str){

		Log.v("result", str);
		JSONObject jsonObject = null;
		try {

			jsonObject = new JSONObject(str);
			jsonObject.put("bizId", jsonObject.opt("bizId").toString());//业务单号
			jsonObject.put("thdType", jsonObject.optInt("payType", 1));//支付方式 1-微信 2-支付宝
			jsonObject.put("bizType", "1");//业务类型 1-抢单类 6-帮我付,除了帮我付的，现在基本都传1
			jsonObject.put("ipAddress", "192.168.1.1");//支付端ip地址

			//			jsonObject.put("bizId", "201726716366857546");//业务单号
			//			jsonObject.put("thdType", 2);//支付方式 1-微信 2-支付宝
		} catch (JSONException e) {
			e.printStackTrace();
		}

		bizId = jsonObject.optString("bizId");
		final int payType = jsonObject.optInt("payType", 1); 
		PayUtil.getAppPayInfo(context, jsonObject, new PayCallback() {

			@Override
			public void callback(int success, String remark, Object object) {

				getOrderInfo(payType, (JSONObject) object);
			}
		});
	}

	/**
	 * 解析预支付订单信息，发起支付
	 * 优先使用账户余额，余额不足剩余部分用第三方支付
	 * @param payType 1：微信 2：支付宝
	 * @param jsonObject
	 */
	public static void getOrderInfo(int payType, JSONObject jsonObject){

		jsonObject = jsonObject.optJSONObject("data");
		int thdPay = jsonObject.optInt("thdPay");//是否需要第三方支付 0-不需要 1-需要(此种情况下才会有第三方支付参数)
		if(thdPay == 0){
			CommandTools.showToast("支付成功");
			Message msg2 = new Message();
			msg2.what = PayUtil.PAY_RESULT;
			msg2.arg1 = 0;//支付成功标志，不可修改
			msg2.obj = "支付成功";
			mEventBus.post(msg2);
			return;
		}

		//余额不足情况下开始第三方支付
		if(payType == 2){

			if(!jsonObject.has("aliPay")){
				return;
			}

			jsonObject = jsonObject.optJSONObject("aliPay");
			final String orderInfo = jsonObject.optString("payInfo");

			Runnable payRunnable = new Runnable() {

				@Override
				public void run() {

					PayTask alipay = new PayTask((Activity) context);
					Map<String, String> result = alipay.payV2(orderInfo, true);

					Message msg = new Message();
					msg.what = SDK_PAY_FLAG;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			};

			Thread payThread = new Thread(payRunnable);
			payThread.start();
		}else if(payType == 1){

			if(!PayUtil.isWeixinAvilible(context)){
				CommandTools.showToast("当前设备没有安装微信客户端!");
				return;
			}

			jsonObject = jsonObject.optJSONObject("tenPay");

			req.appId = jsonObject.optString("appid");
			req.partnerId = jsonObject.optString("partnerid");
			req.prepayId = jsonObject.optString("prepayid");
			req.packageValue = jsonObject.optString("package");
			req.nonceStr = jsonObject.optString("noncestr");
			req.timeStamp = jsonObject.optString("timestamp");
			req.sign = jsonObject.optString("sign");

			wxAPI.registerApp(req.appId);
			wxAPI.sendReq(req);
		}
	}

	@SuppressLint("HandlerLeak")
	public static Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {

			case SDK_PAY_FLAG: {
				@SuppressWarnings("unchecked")
				PayResult payResult = new PayResult((Map<String, String>) msg.obj);
				/**
				 * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				String resultStatus = payResult.getResultStatus();
				Log.v("result", "resultInfo = " + resultInfo + "");
				Log.v("result", "resultStatus = " + resultStatus + "");

				int code = 0;
				// 判断resultStatus 为9000则代表支付成功
				if (TextUtils.equals(resultStatus, "9000")) {
					// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
					code = 0;
					resultInfo = "支付成功";
					Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
				}
				//用户取消支付
				else if(TextUtils.equals(resultStatus, "6001")){
					code = -2;
					resultInfo = "用户取消支付";
					Toast.makeText(context, "取消支付", Toast.LENGTH_SHORT).show();
				}
				else {
					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
					code = -1;
					resultInfo = "支付失败";
					Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();
				}

				Message msg2 = new Message();
				msg2.what = PayUtil.PAY_RESULT;
				msg2.arg1 = code;
				msg2.obj = resultInfo;
				mEventBus.post(msg2);
				break;
			}
			case SDK_AUTH_FLAG: {
				AuthResult authResult = new AuthResult(
						(Map<String, String>) msg.obj, true);
				String resultStatus = authResult.getResultStatus();

				// 判断resultStatus 为“9000”且result_code
				// 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
				if (TextUtils.equals(resultStatus, "9000")
						&& TextUtils.equals(authResult.getResultCode(), "200")) {
					// 获取alipay_open_id，调支付时作为参数extern_token 的value
					// 传入，则支付账户为该授权账户
					Toast.makeText(
							context,
							"授权成功\n"
									+ String.format("authCode:%s",
											authResult.getAuthCode()),
											Toast.LENGTH_SHORT).show();
				} else {
					// 其他状态值则为授权失败
					Toast.makeText(
							context,
							"授权失败"
									+ String.format("authCode:%s",
											authResult.getAuthCode()),
											Toast.LENGTH_SHORT).show();
				}
				break;
			}
			case TAKEPHOTO_H5:
				if(TextUtils.isEmpty(base64) || callBackFunction == null){
					CommandTools.showToast("");
					return;
				}
				callBackFunction.onCallBack(base64);
				break;
			default:
				break;
			}
		};
	};

}
