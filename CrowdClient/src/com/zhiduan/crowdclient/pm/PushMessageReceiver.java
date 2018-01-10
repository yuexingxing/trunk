package com.zhiduan.crowdclient.pm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.menuindex.IndexActivity;
import com.zhiduan.crowdclient.task.TaskAuditActivity;
import com.zhiduan.crowdclient.task.TaskPaymentActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.HttpUtils;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.NotificationHelper;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.ReviewTaskDialog;
import com.zhiduan.crowdclient.view.UpdateAppDialog;
import com.zhiduan.crowdclient.view.UpdateAppDialog.ResultCallBack;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;

import de.greenrobot.event.EventBus;

public class PushMessageReceiver extends BroadcastReceiver 
{
	private static final String LOG_TAG = "PushMessageReceiver";

	private Context mContext;
	private EventBus mEventBus;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		mContext = context;
		Bundle bundle = intent.getExtras();
		Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));

		int nAction = bundle.getInt(PushConsts.CMD_ACTION);
		mEventBus = MyApplication.getInstance().getEventBus();

		Logs.v("result", bundle.toString());

		switch (nAction) 
		{
		case PushConsts.GET_MSG_DATA:
			// 获取透传数据
			byte[] payload = bundle.getByteArray("payload");

			//String appid = bundle.getString("appid");
			String taskid = bundle.getString("taskid");
			String messageid = bundle.getString("messageid");

			// smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
			boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);

			Log.d(LOG_TAG, "第三方回执接口调用" + (result ? "成功" : "失败"));
			Log.d(LOG_TAG, "messageid" + messageid);

			if (payload != null)
			{
				String strData = new String(payload);

				Log.i("NetTaskUtil", strData + "");

				if ("".equals(MyApplication.getInstance().m_userInfo.toKen) || MyApplication.getInstance().m_userInfo.toKen == null) {
					Log.i("hexiuhui===", "token为空，没有登录token为空，没有登录");
					break;
				} else {
					try {
						JSONObject pushObj = new JSONObject(strData);
						String msgId = pushObj.getString("msgId");
						String showType = pushObj.getString("showType");
						String title = pushObj.getString("title");
						String bizCode = pushObj.getString("bizCode");
						String bizId = pushObj.getString("bizId");
						String msgType = pushObj.getString("msgType");
						String content = "";
						if (msgType.equals(Constant.PUSH_MSG_NEW_VOUCHER)) {
//							content = pushObj.getString("content");
							content = "新用户代金卷礼包已放入您的账户，宝宝快去下单吧!";
						} else {
							content = pushObj.getString("content");
						}

						boolean isPlayMusic = true;

						if (bizCode.equals(Constant.PUSH_TYPE_NEW_ORDER)) {   //新订单推送
							isPlayMusic = true;
							if (MyApplication.getInstance().m_refreshGetOrderHandler != null) {
								Message msg = new Message();
								msg.what = IndexActivity.REFRESH_GET_ORDER;
								MyApplication.getInstance().m_refreshGetOrderHandler.sendMessage(msg);
							}

							//如果手机处于休眠状态，则唤醒并震动
							if(isScreenOn(MyApplication.getInstance()) == false){

								CommandTools.wakeUpAndUnlock(MyApplication.getInstance());
								CommandTools.Vibrate(MyApplication.getInstance(), 2000);
							}
						} else if (bizCode.equals(Constant.PUSH_TYPE_LOGIN)) {  //登录踢出
							CustomProgress.dissDialog();
							isPlayMusic = false;

							DialogUtils.showSingleLoginDialog(CommandTools.getGlobalActivity(), content);
						} else if (bizCode.equals(Constant.PUSH_TYPE_PAY)) {  //支付消息
							isPlayMusic = false;

						} else if (bizCode.equals(Constant.PUSH_TYPE_ZD_STATE_SUCCESS)) {  //审核成功
							isPlayMusic = false;
							MyApplication.getInstance().m_userInfo.verifyStatus = Constant.REVIEW_STATE_SUCCESS;
							
							if (MyApplication.getInstance().m_refreshGetOrderHandler != null) {
								Message msg = new Message();
								msg.what = IndexActivity.REFRESH_PUSH_REVIEW_STATE_SUCCESS;
								MyApplication.getInstance().m_refreshGetOrderHandler.sendMessage(msg);
							}

							DialogUtils.showReviewingSuccess(CommandTools.getGlobalActivity(), content);
						} else if (bizCode.equals(Constant.PUSH_TYPE_ZD_STATE_FAIL)) {     //审核失败
							isPlayMusic = false;
							MyApplication.getInstance().m_userInfo.verifyStatus = Constant.REVIEW_STATE_FAIL;

							if (MyApplication.getInstance().m_refreshGetOrderHandler != null) {
								Message msg = new Message();
								msg.what = IndexActivity.REFRESH_PUSH_REVIEW_STATE_FAIL;
								MyApplication.getInstance().m_refreshGetOrderHandler.sendMessage(msg);
							}
							
							DialogUtils.showReviewingFailed(CommandTools.getGlobalActivity(), content);
						} else if (bizCode.equals(Constant.PUSH_TYPE_ZD_UPDATE)) {
							isPlayMusic = false;
						} else if (bizCode.equals(Constant.PUSH_TYPE_ASSIGNED)) {  //货源分单通知
							isPlayMusic = false;
						} else if (bizCode.equals(Constant.PUSH_TYPE_DELIVERY) || bizCode.equals(Constant.PUSH_TYPE_DISTRIBUTE_SINGLE)) { //转单通知  定向派单通知
							isPlayMusic = false;
							if (MyApplication.getInstance().m_refreshGetOrderHandler != null) {
								Message msg = new Message();
								msg.what = IndexActivity.REFRESH_PUSH_ASSIGNED;
								Bundle data = new Bundle();
								data.putString("content", content);
								data.putString("bizId", bizId);
								data.putString("bizCode", bizCode);
								msg.setData(data);
								MyApplication.getInstance().m_refreshGetOrderHandler.sendMessage(msg);
							}
							Message msg = new Message();
							msg.what = OrderUtil.UPDATE_ASSIGN_NUMBER;
							mEventBus.post(msg);
						} else if (bizCode.equals(Constant.PUSH_TYPE_ACCEPT)) {  //转单被同意
							isPlayMusic = false;
							handleTransBill();
						} else if (bizCode.equals(Constant.PUSH_TYPE_REFUSE)) {  //转单被拒绝
							isPlayMusic = false;
							handleTransBill();
						} else if (bizCode.equals(Constant.PUSH_TYPE_OVERTIME)) {  //转单超时
							isPlayMusic = false;

							if (MyApplication.getInstance().m_refreshGetOrderHandler != null) {
								Message msg = new Message();
								msg.what = IndexActivity.REFRESH_PUSH_TIMEOUT_ASSIGNED;
								MyApplication.getInstance().m_refreshGetOrderHandler.sendMessage(msg);
							}

							//							DialogUtils.showTransBillFailed(CommandTools.getGlobalActivity(), content);
							handleTransBill();
						} else if (msgType.equals(Constant.PUSH_MSG_AUDIT_TYPE)) {  //任务审核通知
							isPlayMusic = false;
							if (MyApplication.getInstance().m_refreshAuditHandler != null) {
								Message msg = new Message();
								msg.what = TaskAuditActivity.REFRESH_AUDIT;
								MyApplication.getInstance().m_refreshAuditHandler.sendMessage(msg);
							}

							if (MyApplication.getInstance().m_refreshPaymentHandler != null) {
								Message msg = new Message();
								msg.what = TaskPaymentActivity.REFRESH_PAYMENT;
								MyApplication.getInstance().m_refreshPaymentHandler.sendMessage(msg);
							}

							ReviewTaskDialog.showTaskDialog(CommandTools.getGlobalActivity(), new ReviewTaskDialog.ResultCallback() {
								@Override
								public void callback(int position) {

								}
							});
						} else if (msgType.equals(Constant.PUSH_MSG_TASK_USER_CANCEL_TYPE)) {  //手动取消任务
							isPlayMusic = false;
						} else if (msgType.equals(Constant.PUSH_MSG_TASK_CANCEL_TYPE)) {   //任务超时系统取消通知
							isPlayMusic = false;
						} else if (msgType.equals(Constant.PUSH_MSG_ORDER_TIMEOUT_TYPE_1)) {  //已接未取，订单超时推送
							isPlayMusic = false;
						} else if (msgType.equals(Constant.PUSH_MSG_ORDER_TIMEOUT_TYPE_2)) {  //已接，已取，未派送，订单超时推送
							isPlayMusic = false;
						} else if (msgType.equals(Constant.PUSH_MSG_SEND_TIMEOUT_TYPE)) {    //寄件订单超时未取件通知
							isPlayMusic = false;
						} else if (msgType.equals(Constant.PUSH_MSG_ORDER_CANCEL_TYPE)) {    //用户取消订单(寄件，派件，代取)
							isPlayMusic = false;

							handleTransBill();//订单取消后，通知刷新订单列表
						} else if (msgType.equals(Constant.PUSH_MSG_SERVER_CANCEL_TYPE)) {    //管理端取消订单
							isPlayMusic = false;

							handleTransBill();//订单取消后，通知刷新订单列表
						} else if (msgType.equals(Constant.PUSH_MSG_SERVER_DISTRIBUTE_TYPE)) {    //管理端派单
							isPlayMusic = false;
						} else if (msgType.equals(Constant.PUSH_MSG_SERVER_CANCEL_TYPE_2)) {  //管理端派单 (已接单，未取件)
							isPlayMusic = false;
						} else if (msgType.equals(Constant.PUSH_MSG_SERVER_ING)) {  //提交审核
							isPlayMusic = false;
							MyApplication.getInstance().m_userInfo.verifyStatus = Constant.REVIEW_STATE_SUBMITING;
						} else {
							isPlayMusic = false;
						}

						NotificationHelper.addNewOrderNotify(context, title, content, isPlayMusic, mEventBus);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}	

			break;

		case PushConsts.GET_CLIENTID:
			// 获取ClientID(CID)
			// 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
			String cid = bundle.getString("clientid");
			//Util.showTestToast(context, "clientid=" + cid, Toast.LENGTH_LONG);
			MyApplication.getInstance().m_strPushMessageClientId = cid;

			Log.i("hexiuhui====", "clientId=" + MyApplication.getInstance().m_strPushMessageClientId + "");			
			break;

		case PushConsts.THIRDPART_FEEDBACK:
			String appid = bundle.getString("appid");
			String actionid = bundle.getString("actionid");
			long timestamp = bundle.getLong("timestamp");

			Log.d("GetuiSdkDemo", "appid = " + appid);
			Log.d("GetuiSdkDemo", "actionid = " + actionid);
			Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
			break;

		default:
			break;
		}
	}

	/**
	 * 弹出更新对话框
	 * 
	 * @param strTitle
	 * @param string
	 */
	private void showUpdateDialog(String strTitle, String remark, final String downloadUrl) {

		UpdateAppDialog.showDialog(mContext, "0", strTitle, remark, "", new ResultCallBack() {
			@Override
			public void callback(boolean flag) {
				if (flag == true) {
					HttpUtils.download(CommandTools.getGlobalActivity(), downloadUrl, mHandler);
					//					Intent intent = new Intent(mContext, SplashActivity.class);
					//					mContext.startActivity(intent);
				}
			}
		});
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x0001) {
				UpdateAppDialog.showDialog(CommandTools.getGlobalActivity(), "0", "正在更新程序", "正在更新程序", "", null);
			}

			if (msg.what == 0x0002) {
				CustomProgress.dissDialog();
			}

			if (msg.what == 0x11) {
				Bundle bundle = msg.getData();
				String strTotal = bundle.getString("totalSize");
				String strCurr = bundle.getString("curSize");

				if (strTotal.equals(strCurr)) {
					Utils.finishAllActivities();
				}

				UpdateAppDialog.updateProgress(Integer.parseInt(strTotal), Integer.parseInt(strCurr));
			}
		}
	};

	/**
	 * 转单处理
	 */
	private void handleTransBill(){

		Message msg = new Message();

		//待取件
		msg.what = OrderUtil.REFRESH_WAIT_DATA;
		mEventBus.post(msg);

		//配送中
		msg.what = OrderUtil.REFRESH_DISTRIBUTION_DATA;
		mEventBus.post(msg);

		//更新转单数量
		msg.what = OrderUtil.UPDATE_ASSIGN_NUMBER;
		mEventBus.post(msg);
	}

	/**
	 * 判断当前手机是否锁屏，
	 * 如果锁屏则唤醒并震动
	 */
	private void checkUnlock() {

		KeyguardManager kgMgr = (KeyguardManager) MyApplication.getInstance().getSystemService(Context.KEYGUARD_SERVICE);
		boolean showing = kgMgr.inKeyguardRestrictedInputMode();
		if (showing) {
			CommandTools.wakeUpAndUnlock(MyApplication.getInstance());
			CommandTools.Vibrate(MyApplication.getInstance(), 2000);
		}
	}

	/**
	 * 判断手机屏幕是否锁定
	 * PowerManager.isScreenOn()方法；这个方法返回true: 屏幕是唤醒的  返回false:屏幕是休眠的
	 * @param c
	 * @return
	 */
	public boolean isScreenOn(Context c) {

		if(c == null){
			return false;
		}

		PowerManager powermanager;
		powermanager = (PowerManager) c.getSystemService(Context.POWER_SERVICE); 
		return powermanager.isScreenOn();
	}
}
