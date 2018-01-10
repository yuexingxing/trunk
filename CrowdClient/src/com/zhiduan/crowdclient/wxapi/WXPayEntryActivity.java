package com.zhiduan.crowdclient.wxapi;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.PayUtil;
import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

/** 
 * 支付成功后回调
 * 
 * @author yxx
 *
 * @date 2017-1-11 上午10:46:15
 * 
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private EventBus mEventBus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_pay_success);
		IWXAPI api = WXAPIFactory.createWXAPI(this, Constant.PAY_APPID);
		api.handleIntent(getIntent(), this);

		if(mEventBus == null){
			mEventBus = EventBus.getDefault();
			mEventBus.register(this);
		}
		
		Logs.v("result", "微信支付结果: onCreate");
	}

	public void onEventMainThread(Message msg) {

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		// api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {

		Logs.v("result", "微信支付结果: onReq");
	}

	@Override
	public void onResp(BaseResp resp) {
		int code = resp.errCode; 
		String error = resp.errStr;

		Logs.v("result", "微信支付结果: onResp");
		if(mEventBus == null){
			mEventBus = EventBus.getDefault();
			mEventBus.register(this);
		}
		if (code == 0){  //显示充值成功的页面和需要的操作  

			error = "支付成功";
			CommandTools.showToast("支付成功");
		}  

		if (code == -1){  	//错误  

			error = "支付失败";
			CommandTools.showToast("支付失败");
		}  

		if (code == -2){  	//用户取消           

			error = "用户取消支付";
			CommandTools.showToast("用户取消支付");
		}  
		
		Message msg = new Message();
		msg.what = PayUtil.PAY_RESULT;
		msg.arg1 = code;
		msg.obj = error;
		mEventBus.post(msg);

		Logs.v("result", "微信支付结果: " + error);
		finish();
	}

}
