package com.zhiduan.crowdclient.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.activity.LoginActivity;
import com.zhiduan.crowdclient.activity.MainActivity;
import com.zhiduan.crowdclient.completeinformation.CompleteInformationActivity;
import com.zhiduan.crowdclient.completeinformation.PerfectNameActivity;
import com.zhiduan.crowdclient.completeinformation.SchoolDataActivity;
import com.zhiduan.crowdclient.menucenter.UserInfoActivity;
import com.zhiduan.crowdclient.menuindex.IndexActivity;
import com.zhiduan.crowdclient.menuorder.DeliveryRemindActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.LoginUtil;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.GeneralDialog.NoButtonListener;
import com.zhiduan.crowdclient.view.GeneralDialog.OneButtonListener;
import com.zhiduan.crowdclient.view.GeneralDialog.TwoButtonListener;
import com.zhiduan.crowdclient.wallet.ActivateWalletActivity;
import com.zhiduan.crowdclient.wallet.UserActiveWalletActivity;

/** 
 * 对话框类
 * 
 * @author yxx
 *
 * @date 2016-9-28 上午11:31:24
 * 
 */
public class DialogUtils {

	// 弹窗结果回调函数
	public static abstract class DialogCallback {
		public abstract void callback(int position);
	}

	/**
	 * 弹出单点登录对话框
	 * @param context
	 */
	public static void showSingleLoginDialog(final Context context, String content){

		String title = "异常";
		GeneralDialog.showOneButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_5, title, content, "重新登录", new OneButtonListener() {

			@Override
			public void onExitClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}

			}

			@Override
			public void onButtonClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}

				MyApplication.getInstance().m_userInfo.toKen = "";
				String loginName = (String) SharedPreferencesUtils.getParam(Constant.SP_LOGIN_NAME, "");
				String loginPwd = (String) SharedPreferencesUtils.getParam(Constant.SP_LOGIN_PSD, "");

				LoginUtil.login(CommandTools.getGlobalActivity(), loginName, loginPwd, null, false);
			}
		});
	}

	/**
	 * 拨打电话对话框
	 * @param context
	 * @param phone
	 * @param callback	需要调接口的处理回调，不需要的直接传null即可
	 */
	public static void showCallPhoneDialog(final Context context, final String phone, final DialogCallback callback){

		GeneralDialog.showTwoButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_8, "确认是否拨打电话", phone, "取消", "拨打", new TwoButtonListener() {

			@Override
			public void onRightClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}

				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
				context.startActivity(intent);

				if(callback != null){
					callback.callback(1);
				}
			}

			@Override
			public void onLeftClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
	}

	/**
	 * 弹出登录对话框
	 * @param context
	 */
	public static void showLoginDialog(final Context context){

		String title = "";
		String content = "您还没有登录, 快去登录吧!";
		GeneralDialog.showOneButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_7, title, content, "立即登录", new OneButtonListener() {

			@Override
			public void onExitClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}

			}

			@Override
			public void onButtonClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}

				Intent intent = new Intent(context, LoginActivity.class);
				context.startActivity(intent);
			}
		});
	}
	/**
	 * 弹出登录对话框
	 * @param context
	 */
	public static void showPerfectDialog(final Context context){

		String title = "";
		String content = "您还没有完善资料, 点击完善资料!";
		GeneralDialog.showOneButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_7, title, content, "立即完善", new OneButtonListener() {

			@Override
			public void onExitClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}

			}

			@Override
			public void onButtonClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}

				Intent intent = new Intent(context, PerfectNameActivity.class);
				context.startActivity(intent);
			}
		});
	}
	/**
	 * 弹出确认转单对话框
	 * @param context
	 * @param name1
	 * @param name2
	 * @param callback
	 */
	public static void showTransBillDialog(final Context context, boolean isOffice, final String name1, final String name2, int overPlus, final DialogCallback callback){

		String strSign;
		String strMsg;
		if(isOffice){
			strSign = "您确认把用户为%s的用户订单转给小派%s么？";
			strMsg = String.format(strSign, name1, name2);
		}else{
			strSign = "您确认把用户为%s的用户订单转给小派%s么？(今日剩余转单次数:%s)";
			strMsg = String.format(strSign, name1, name2, overPlus);
		}

		GeneralDialog.showTwoButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_8, "信息确认", strMsg, "取消", "确定", new TwoButtonListener() {

			@Override
			public void onRightClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
				callback.callback(1);
			}

			@Override
			public void onLeftClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
				callback.callback(0);
			}
		});
	}

	/**
	 * 弹出实名认证对话框
	 * @param context
	 */
	public static void showAuthDialog(final Context context){

		String title = "暂未开通权限";
		String content = "您还没有进行身份验证, 请先验证身份才能操作哦!";
		GeneralDialog.showOneButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_7, title, content, "马上认证", new OneButtonListener() {

			@Override
			public void onExitClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}

			}

			@Override
			public void onButtonClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
				Intent intent = new Intent(context, ActivateWalletActivity.class);
				context.startActivity(intent);
			}
		});
	}

	/**
	 * 正在审核中对话框
	 * @param context
	 */
	public static void showReviewingDialog(final Context context){

		String title = "正在审核";
		String content = "已经提交实名认证资料，并在审核中，只有审核通过后，才可以激活钱包";
		GeneralDialog.showOneButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_6, title, content, "我知道了", new OneButtonListener() {

			@Override
			public void onExitClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}

			}

			@Override
			public void onButtonClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});

	}

	/**
	 * 审核成功对话框
	 * @param context
	 */
	public static void showReviewingSuccess(final Context context, String content){

		GeneralDialog.showOneButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_1, "", content, "我知道了", new OneButtonListener() {

			@Override
			public void onExitClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}

			@Override
			public void onButtonClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
	}

	/**
	 * 审核失败对话框
	 * @param context
	 * @param 后台推送传过来值，如果没有则本地赋值
	 */
	public static void showReviewingFailed(final Context context, String content){

		if(TextUtils.isEmpty(content)){
			content = "审核失败, 请检查您提交的资料";
		}

		GeneralDialog.showOneButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_5, "", content, "点击关闭", new OneButtonListener() {

			@Override
			public void onExitClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}

			@Override
			public void onButtonClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
//				Intent intent = new Intent(context, IndexActivity.class);
//				intent.putExtra("perfect", "name");
//				context.startActivity(intent);
			}
		});
	}

	/**
	 * 弹出登录对话框
	 * @param context
	 */
	public static void showExitDialog(final Context context, final DialogCallback callback){

		String content = "确定要退出登录吗？";
		GeneralDialog.showTwoButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_8, "", content, "取消", "确认", new TwoButtonListener() {

			@Override
			public void onRightClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}

				if(callback != null){
					callback.callback(1);
				}
			}

			@Override
			public void onLeftClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
	}

	/**
	 * 系统推送新订单
	 * @param context
	 * @param content
	 * @param callback
	 */
	public static void showNewTransBill(final Context context, final String bizId, final String bizCode, final String push_message){

		GeneralDialog.showNoButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_1, push_message, new NoButtonListener() {

			@Override
			public void onExitClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}

			@Override
			public void onDetailClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}

				Intent intent = new Intent(context, DeliveryRemindActivity.class);
				intent.putExtra("bizId", bizId);
				intent.putExtra("bizCode", bizCode);
				intent.putExtra("push_message", push_message);
				context.startActivity(intent);
			}
		});
	}

	/**
	 * 系统推送转单成功
	 * @param context
	 * @param push_message
	 */
	public static void showTransBillSuccess(final Context context, final String push_message){

		GeneralDialog.showNoButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_1, push_message, new NoButtonListener() {

			@Override
			public void onExitClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}

			@Override
			public void onDetailClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
	}

	/**
	 * 系统推送转单失败、超时
	 * @param context
	 * @param push_message
	 */
	public static void showTransBillFailed(final Context context, final String push_message){

		GeneralDialog.showNoButtonDialog(context, GeneralDialog.DIALOG_ICON_TYPE_5, push_message, new NoButtonListener() {

			@Override
			public void onExitClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}

			@Override
			public void onDetailClick(Dialog dialog) {
				// TODO Auto-generated method stub
				if (dialog != null) {
					dialog.dismiss();
				}
			}
		});
	}

	/**
	 * 弹出一个按钮对话框
	 * @param context
	 * @param title	标题
	 * @param content	显示内容
	 * @param btnMenu
	 * @param callback	选择回调，如果不需要回调，可以传null
	 */
	public static void showOneButtonDialog(final Context context, int type, String title, String content, String btnMenu, final DialogCallback callback){

		GeneralDialog.showOneButtonDialog(context, type, title, content, btnMenu, new OneButtonListener() {

			@Override
			public void onExitClick(Dialog dialog) {
				// TODO Auto-generated method stub

				dialog.dismiss();
				if(callback != null){
					callback.callback(-1);
				}
			}

			@Override
			public void onButtonClick(Dialog dialog) {
				// TODO Auto-generated method stub

				dialog.dismiss();
				if(callback != null){
					callback.callback(0);
				}
			}
		});
	}

	/**
	 * 弹出两个按钮对话框
	 * @param context
	 * @param title	标题
	 * @param content	显示内容
	 * @param leftMenu	左侧按钮
	 * @param rightMenu	右侧按钮
	 * @param callback	选择回调，如果不需要回调，可以传null
	 */
	public static void showTwoButtonDialog(final Context context, final int type, String title, String content, String leftBtn, String rightBtn,  final DialogCallback callback){

		GeneralDialog.showTwoButtonDialog(context, type, title, content, leftBtn, rightBtn, new TwoButtonListener() {

			@Override
			public void onRightClick(Dialog dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();

				if(callback != null){
					callback.callback(0);
				}
			}

			@Override
			public void onLeftClick(Dialog dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();

				if(callback != null){
					callback.callback(-1);
				}
			}
		});
	}
}
