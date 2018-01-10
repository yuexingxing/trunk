package com.zhiduan.crowdclient;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhiduan.crowdclient.activity.LoginActivity;
import com.zhiduan.crowdclient.pay.ali.AuthResult;
import com.zhiduan.crowdclient.pay.ali.PayResult;
import com.zhiduan.crowdclient.photoalbum.camera.MyCameraActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtil.DataCallback;
import com.zhiduan.crowdclient.util.PayUtil;
import com.zhiduan.crowdclient.util.PopupWindows;
import com.zhiduan.crowdclient.util.PopupWindows.Callback;
import com.zhiduan.crowdclient.util.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

/** 
 * 收件人端(webview加载)
 * 
 * @author yxx
 *
 * @date 2016-12-21 下午4:25:48
 * 
 */
public class ServerMainActivity extends Activity {

	private Context mContext;

	private LinearLayout layout;
	private BridgeWebView webView;
	private CallBackFunction callBackFunction;
	private String base64;

	private int RESULT_LOAD_IMAGE = 0x1101;
	private int RESULT_TAKE_PHOTOE = 0x1102;

	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_AUTH_FLAG = 2;

	private IWXAPI wxAPI;
	private PayReq req = new PayReq();
	private String webviewUrl = "http://192.168.3.22:7008/axp-web/index.htm?";
	//	private String webviewUrl = "http://192.168.3.5:9401/axp-web/index.htm?";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_server_main);

		findViewById();
	}

	private void findViewById(){
		
		Utils.addActivity(this);
		
		mContext = ServerMainActivity.this;
		wxAPI = WXAPIFactory.createWXAPI(mContext, Constant.PAY_APPID);
		wxAPI.registerApp(Constant.PAY_APPID);

		layout = (LinearLayout) findViewById(R.id.layout_server_main);
		webView = (BridgeWebView) findViewById(R.id.webView_server_main);

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);//支持javascrip必须有
		webSettings.setDefaultTextEncodingName("utf-8");
		webView.setWebChromeClient(new WebChromeClient() {});//要加上这句，否则部分机型无法弹出窗口
		//		webView.addJavascriptInterface(getHtmlObject(), "app");   
		//
		//		webView.getSettings().setDomStorageEnabled(true);   
		//		webView.getSettings().setAppCacheMaxSize(1024*1024*8);  
		//		String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();  
		//		webView.getSettings().setAppCachePath(appCachePath);  
		//		webView.getSettings().setAllowFileAccess(true); 
		//		webView.getSettings().setAppCacheEnabled(true); 
		webView.setDefaultHandler(new DefaultHandler());
		webviewUrl = webviewUrl + "token=" + MyApplication.getInstance().m_userInfo.toKen;
		CommandTools.showToast(webviewUrl);
		webView.loadUrl(webviewUrl);//http://192.168.3.5:9401/axp-web/bridgeandroid

		//根据ID找到RadioGroup实例
		RadioGroup group = (RadioGroup)this.findViewById(R.id.radioGroup_server);
		//绑定一个匿名监听器
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				//获取变更后的选中项的ID
				int radioButtonId = arg0.getCheckedRadioButtonId();
				if(radioButtonId == R.id.radioButton_server_1){

					finish();
				}
			}
		});

		registerHandler();
	}

	protected void onResume(){
		super.onResume();

	}

	/**
	 * 从图库选择
	 * @param v
	 */
	public void choosePic(View v){

		Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
		chooserIntent.setType("image/*");
		startActivityForResult(chooserIntent, RESULT_LOAD_IMAGE); 
	}

	/**
	 * 拍照
	 * @param v
	 */
	public void takePhoto(View v){

		Intent intent = new Intent(mContext, MyCameraActivity.class);
		startActivityForResult(intent, RESULT_TAKE_PHOTOE);
	}

	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		// TODO Auto-generated method stub  
		super.onActivityResult(requestCode, resultCode, data);  
		if (requestCode == RESULT_TAKE_PHOTOE && resultCode == Activity.RESULT_OK) {  

			String pathCamera = data.getStringExtra("pathCamera");
			Bitmap bitmap = CommandTools.convertToBitmap(pathCamera, 100, 100);

			base64 = CommandTools.bitmapToBase64(bitmap);
			mHandler.sendEmptyMessage(0x0013);
		}else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {  
			Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();

			Bitmap bitmap = CommandTools.getBitmapFromUri(ServerMainActivity.this, result);
			base64 = CommandTools.bitmapToBase64(bitmap);
			mHandler.sendEmptyMessage(0x0013);
		}
	}  

	/**
	 * 解析预支付订单信息
	 * @param payType 1：微信 2：支付宝
	 * @param jsonObject
	 */
	private void getOrderInfo(int payType, JSONObject jsonObject){

		jsonObject = jsonObject.optJSONObject("data");

		if(payType == 2){

			//			final String orderInfo = jsonObject.optString("payInfo");
			//			Log.v("result", orderInfo);

			final String orderInfo = "biz_content=%7B%22total_amount%22%3A%220.15%22%2C%22body%22%3A%22%E6%9C%8D%E5%8A%A1%E8%B4%B9%22%2C%22timeout_express%22%3A%2230m%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22%E7%9F%AD%E4%BF%A1%E6%94%B6%E8%B4%B9%22%2C%22out_trade_no%22%3A%222016122717312543872%22%7D&timestamp=2016-12-27+17%3A31%3A25&sign_type=RSA&notify_url=http%3A%2F%2Fdev.axpapp.com%2Faxp%2Fcashier%2Fsdkpay%2F2.htm&charset=utf-8&method=alipay.trade.app.pay&app_id=2016120804019527&version=1.0&sign=EMVKNKi8jZXkcmwmbSC6Cab4znF1WNQAN0M3H8yCTxX5MA5TchzNmix3ktdAZvShKseNlFgvAN4cB68%2FF7yvicnBIUIkkBbaM%2FN0ydzDajXcl4ALGdo5XMIKE50QqR%2BQ1Nspub4lH7NadQuatvPZnx9ACi0QbMieAXnGne%2Bjzhc%3D";

			Runnable payRunnable = new Runnable() {

				@Override
				public void run() {

					PayTask alipay = new PayTask(ServerMainActivity.this);
					Map<String, String> result = alipay.payV2(orderInfo, true);
					Log.i("result", result.toString());

					Message msg = new Message();
					msg.what = SDK_PAY_FLAG;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			};

			Thread payThread = new Thread(payRunnable);
			payThread.start();
		}else{

			if(!PayUtil.isWeixinAvilible(mContext)){
				CommandTools.showToast("当前设备没有安装微信客户端!");
				return;
			}

			//			{"success":0,"code":"","message":"下单成功","data":{"appid":"wx4c379db7564a35fe","noncestr":"9A1335EF5FFEBB0DE9D089C4182E4868",
			//			"package":"Sign=WXPay","partnerid":"1317115801","prepayid":"wx20161227175025b846a1e6010470900918",
			//			"timestamp":"1482832226026","sign":"848B9BC924749308EEFC2EB57309EBD3"}}

			//			req.appId = jsonObject.optString("appid");
			//			req.partnerId = jsonObject.optString("partnerid");
			//			req.prepayId = jsonObject.optString("prepayid");
			//			req.packageValue = jsonObject.optString("package");
			//			req.nonceStr = jsonObject.optString("noncestr");
			//			req.timeStamp = jsonObject.optString("timestamp");
			//			req.sign = jsonObject.optString("sign");

			req.appId = Constant.PAY_APPID;
			req.partnerId = "1317115801";
			req.prepayId = "wx20161227175025b846a1e6010470900918";
			req.packageValue = "Sign=WXPay";
			req.nonceStr = "9A1335EF5FFEBB0DE9D089C4182E4868";
			req.timeStamp = "1482832226026";

			List<NameValuePair> signParams = new LinkedList<NameValuePair>();
			signParams.add(new BasicNameValuePair("appid", req.appId));
			signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
			signParams.add(new BasicNameValuePair("package", req.packageValue));
			signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
			signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
			signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

			req.sign = PayUtil.genAppSign(signParams);

			wxAPI.registerApp(req.appId);
			wxAPI.sendReq(req);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

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
				// 判断resultStatus 为9000则代表支付成功
				if (TextUtils.equals(resultStatus, "9000")) {
					// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
					Toast.makeText(ServerMainActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
				}
				//用户取消支付
				else if(TextUtils.equals(resultStatus, "6001")){
					Toast.makeText(ServerMainActivity.this, "取消支付", Toast.LENGTH_SHORT).show();
				}
				else {
					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
					Toast.makeText(ServerMainActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
				}
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
							ServerMainActivity.this,
							"授权成功\n"
									+ String.format("authCode:%s",
											authResult.getAuthCode()),
											Toast.LENGTH_SHORT).show();
				} else {
					// 其他状态值则为授权失败
					Toast.makeText(
							ServerMainActivity.this,
							"授权失败"
									+ String.format("authCode:%s",
											authResult.getAuthCode()),
											Toast.LENGTH_SHORT).show();
				}
				break;
			}

			case 0x0011:
				layout.setVisibility(View.VISIBLE);
				break;
			case 0x0012:
				layout.setVisibility(View.GONE);
				break;
			case 0x0013:
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

	/**
	 * H5
	 * java层提供的方法，提供给js调用
	 * 注册方法，提供给h5用
	 */
	private void registerHandler(){

		//拍照
		webView.registerHandler("toTakePictures", new BridgeHandler() {

			@Override
			public void handler(String arg0, CallBackFunction arg1) {

				callBackFunction = arg1;

				new PopupWindows(mContext, findViewById(R.id.layout_server_main), new Callback() {

					@Override
					public void callback(int pos) {
				
						if(pos == 1){
							takePhoto(null);
						}else if(pos == 2){
							choosePic(null);
						}
					}
				});
			}
		});

		//返回到登陆界面
		webView.registerHandler("toLogin", new BridgeHandler() {

			@Override
			public void handler(String arg0, CallBackFunction arg1) {

				callBackFunction = arg1;
				MyApplication myApplication = MyApplication.getInstance();
				myApplication.m_userInfo.toKen = "";
				startActivity(new Intent(mContext, LoginActivity.class));
				Utils.clearActivityList();
				finish();
			}
		});
		
		//调用微信支付接口
		webView.registerHandler("toWeChat", new BridgeHandler() {

			@Override
			public void handler(String arg0, CallBackFunction arg1) {

			}
		});
		
		//调用支付宝支付接口
		webView.registerHandler("toAliPay", new BridgeHandler() {

			@Override
			public void handler(String arg0, CallBackFunction arg1) {

			}
		});
		
		//调用分享接口
		webView.registerHandler("toShare", new BridgeHandler() {

			@Override
			public void handler(String arg0, CallBackFunction arg1) {

			}
		});
	}
}
