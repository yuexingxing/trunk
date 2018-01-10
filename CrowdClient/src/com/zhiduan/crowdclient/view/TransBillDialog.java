package com.zhiduan.crowdclient.view;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.CommandTools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/** 
 * 确认转单对话框
 * 
 * @author yxx
 *
 * @date 2016-7-26 下午10:00:31
 * 
 */
public class TransBillDialog{

	private static Context mContext;
	public static boolean isShow = false;
	static Dialog dialog;
	
	private static String strTip = "您确认把用户为的用户订单转给小派";//您确认把用户为(%1$s)的用户订单转给小派(%1$s)么？

	// 弹窗结果回调函数
	public static abstract class TransCallback {
		public abstract void callback(boolean result);
	}

	public TransBillDialog(Context context){

	}

	/**
	 * @param context
	 * @param strMessage
	 */
	public static void showMyDialog(Context context, String name1, String name2, final TransCallback callback){

		if(dialog != null){
			dialog.dismiss();
		}

		mContext = context;
		if(mContext == null){
			return;
		}

		dialog = new Dialog(mContext, R.style.dialog);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_trans_bill, null);
		
		TextView tvContent = (TextView) layout.findViewById(R.id.tv_trans_content);

		String strSign = "您确认把用户为%s的用户订单转给小派%s么？";
		String strMsg = String.format(strSign, name1, name2);

		TextView btnOK = (TextView) layout.findViewById(R.id.tv_trans_ok);
		TextView btnCancel = (TextView) layout.findViewById(R.id.tv_trans_cancel);
		
	    SpannableString spanString = new SpannableString(strMsg);  
	    ForegroundColorSpan span = new ForegroundColorSpan(MyApplication.getInstance().getResources().getColor(R.color.main_color));  
	    spanString.setSpan(span, strTip.length() + name1.length(), strTip.length() + name1.length() + name2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
	  
	    tvContent.append(spanString);  

		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				callback.callback(true);
				dialog.dismiss();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				callback.callback(false);
				dialog.dismiss();
			}
		});

		dialog.setContentView(layout);

		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		try{
			dialog.show();
			setDialogWindowAttr(dialog, mContext);
			isShow = true;
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * 关闭窗口
	 */
	public static void closeDialog(){

		TransBillDialog.isShow = false;
		if(dialog != null){
			dialog.dismiss();
		}
	}

	//在dialog.show()之后调用
	@SuppressWarnings("deprecation")
	public static void setDialogWindowAttr(Dialog dlg,Context ctx){

		WindowManager wm = ((Activity) ctx).getWindowManager();
		Display display = wm.getDefaultDisplay(); // 为获取屏幕宽、高

		Window window = dlg.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.width = (int) (display.getWidth()/1.5);
		lp.height = (int) (display.getHeight()/4);
		dlg.getWindow().setAttributes(lp);
	}

}
