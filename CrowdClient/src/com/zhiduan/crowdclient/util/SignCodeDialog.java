package com.zhiduan.crowdclient.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.SignCodeInfo;
import com.zhiduan.crowdclient.util.OrderUtil.OrderSignCallback;

/** 
 * 跑腿业务短信验证码签收
 * 
 * @author yxx
 *
 * @date 2016-11-1 上午10:42:25
 * 
 */
public class SignCodeDialog {

	private static TextView tv_send_code;
	private static EditText edt_msg_code;

	private static String orderId;

	private final static int codeTime = 90;//验证码再次获取时间间隔
	private static SignCodeInfo info = new SignCodeInfo();
	private static List<SignCodeInfo> list = new ArrayList<SignCodeInfo>();

	private static int timeCount = 0;
	private static Timer timer;
	private static MyTimerTask task;

	public static abstract  class SignResultCallback {
		public abstract  void callback(int pos, String code);
	}

	public SignCodeDialog(){

	}

	public static void showDialog(final Context context, String orderIdd, final String phone, final SignResultCallback callback) {

		orderId = orderIdd;

		final Dialog signDialog = new Dialog(context, R.style.dialog_no_border);
		View view = View.inflate(context, R.layout.dialog_sign_order, null);

		edt_msg_code = (EditText) view.findViewById(R.id.edt_msg_code);
		tv_send_code = (TextView) view.findViewById(R.id.tv_send_code);
		Button button_left=(Button) view.findViewById(R.id.two_button_bt_one_left);
		Button button_right=(Button) view.findViewById(R.id.two_button_bt_right);
		final ImageView ivCarryOut = (ImageView) view.findViewById(R.id.two_button_iv_carry_out);
		final RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.two_button_rl);
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		ivCarryOut.measure(w, h);
		int heightCarryOut = ivCarryOut.getMeasuredHeight();
		final LayoutParams layoutParams = (LayoutParams) rl.getLayoutParams();
		if (heightCarryOut == 0) {
			final ViewTreeObserver vto = ivCarryOut.getViewTreeObserver();
			vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					vto.removeOnPreDrawListener(this);
					int height = ivCarryOut.getMeasuredHeight();
					height = height / 2;
					layoutParams.topMargin = height;
					rl.setLayoutParams(layoutParams);
					return true;
				}
			});
		} else {
			heightCarryOut = heightCarryOut / 2;
			layoutParams.topMargin = heightCarryOut;
			rl.setLayoutParams(layoutParams);
		}
		
		//重新获取验证码
		tv_send_code.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				getVerCode(context, phone);
			}
		});
		
		//取消
		button_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (signDialog!=null) {
					signDialog.dismiss();
				}

			}
		});
		
		//确认
		button_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				String code = edt_msg_code.getText().toString();
				if(TextUtils.isEmpty(code) || code.length() != 6){
					
					CommandTools.showToast("请输入6位有效验证码!");
					return;
				}

				if (signDialog!=null) {
					signDialog.dismiss();
				}

				if(callback != null){
					callback.callback(0, code);
				}
			}
		});

		signDialog.setContentView(view);
		signDialog.setCanceledOnTouchOutside(false);
		Window window = signDialog.getWindow();
		window.getAttributes().width = CommandTools.dip2px(context, 300);
		window.getAttributes().height = CommandTools.dip2px(context, 270);
		signDialog.show();
		
		boolean flag = checkDataList();
		if(flag){
			mHandler.sendEmptyMessage(0x0011);
		}else{
			timeCount = 0;
			mHandler.sendEmptyMessage(0x0012);
		}
	}

	public static Handler mHandler = new Handler(){

		public void handleMessage(Message msg){

			if(msg.what == 0x0011){

				tv_send_code.setClickable(false);

				--timeCount;
				if(timeCount < 0){
					mHandler.sendEmptyMessage(0x0012);
					return;
				}

				String strSec = timeCount + "s";
				SpannableString spanStack = new SpannableString(strSec);
				spanStack.setSpan(new ForegroundColorSpan(Color.RED), 0,
						spanStack.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

				tv_send_code.setBackgroundResource(R.drawable.register_valid_gray);
				tv_send_code.setText("");
				tv_send_code.append(spanStack);
				tv_send_code.append("后重发");
			}else if(msg.what == 0x0012){

				tv_send_code.setText("未收到短信?");
				tv_send_code.setTextColor(Color.WHITE);
				tv_send_code.setBackgroundResource(R.drawable.register_valid);
				tv_send_code.setClickable(true);
			}

		}
	};

	/** 
	 * 计时器任务
	 * 
	 * @author yxx
	 *
	 * @date 2016-11-1 上午10:40:33
	 * 
	 */
	public class MyTimerTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Message message = new Message();      
			message.what = 0x0011;      
			mHandler.sendMessage(message);  
		}

	}

	/**
	 * 检查当前集合是否已有该订单
	 * 如果有则删除
	 * @return
	 */
	public static void removeData(){

		int len = list.size();
		for(int i=0; i<len; i++){

			info = list.get(i);
			if(orderId.equals(info.getOrderId())){

				list.remove(i);
				break;
			}
		}
	}

	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发

			tv_send_code.setText("重新验证");
			tv_send_code.setTextColor(Color.WHITE);
			tv_send_code.setBackgroundResource(R.drawable.register_valid);
			tv_send_code.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示

			tv_send_code.setClickable(false);

			String strSec = millisUntilFinished / 1000 + "s";
			SpannableString spanStack = new SpannableString(strSec);
			spanStack.setSpan(new ForegroundColorSpan(Color.RED), 0,
					spanStack.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

			tv_send_code.setBackgroundResource(R.drawable.register_valid_gray);
			tv_send_code.setText("");
			tv_send_code.append(spanStack);
			tv_send_code.append("后重发");
		}
	}

	/**
	 * 进入界面后先检查集合中是否有该订单
	 * 如果有，且时间在90S内，则继续计时器
	 * @return
	 */
	private static boolean checkDataList(){

		boolean flag = false;
		for(int i=0; i<list.size(); i++){

			info = list.get(i);
			timeCount = (int) (codeTime - (System.currentTimeMillis()/1000 - info.getTime()));
			if(orderId.equals(info.getOrderId())){
				
				Log.v("result", "info.getTime():" + info.getTime());
				Log.v("result", "System.currentTimeMillis()/1000:" + System.currentTimeMillis()/1000);
				Log.v("result", "timeCount:" + timeCount);
				flag = true;
				break;
			}else if(timeCount < 0){
				list.remove(i);
				--i;
			}
		}

		return flag;
	}
	
	/**
	 * 获取验证码
	 * 如果成功则开启计时器，如果计时器存在先销毁
	 */
	public static void getVerCode(Context context, String phone){
		
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("phone", phone);
			jsonObject.put("codeType", "signin_" + orderId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OrderUtil.getVerCode(context, jsonObject, new OrderSignCallback() {

			@Override
			public void callback(int success, String remark) {
				// TODO Auto-generated method stub
				if(success == 0){
					
					removeData();

					info = new SignCodeInfo();
					info.setOrderId(orderId);
					info.setTime((int) (System.currentTimeMillis()/1000));
					list.add(info);

					timeCount = codeTime;
					
					if(timer != null){

						if(task != null){
							task.cancel();
						}
					}

					task = new SignCodeDialog().new MyTimerTask();
					timer = new Timer(true);
					timer.schedule(task, 1000, 1000); //延时1000ms后执行，1000ms执行一次
				}
			}
		});
	}
}
