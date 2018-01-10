package com.zhiduan.crowdclient.view;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.AmountUtils;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/** 
 * 签收明细
 * 
 * @author yxx
 *
 * @date 2016-5-3 下午10:00:31
 * 
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class SignDetailDialog{

	private static Context mContext;
	public static boolean isShow = false;
	static Dialog dialog;

	/** 
	 * 选择支付方式
	 * 0为线上支付
	 * 1为现金支付
	 */
	public static abstract class PayResultCallback {
		public abstract void callback(int pos);
	}

	public SignDetailDialog(Context context){

	}

	/**
	 * @param context
	 * @param strMessage
	 */
	public static void showMyDialog(Context context, long fee1, long fee2, long fee3, long fee4){

		mContext = context;
		
		if(mContext == null){
			return;
		}

		if(dialog != null){
			dialog.dismiss();
		}

		dialog = new Dialog(context, R.style.dialog);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_sign_detail_view, null);
		dialog.setContentView(layout);
		
		TextView tvFee1 = (TextView) layout.findViewById(R.id.tv_dialog_sign_detail_fee1);
		TextView tvFee2 = (TextView) layout.findViewById(R.id.tv_dialog_sign_detail_fee2);
		TextView tvFee3 = (TextView) layout.findViewById(R.id.tv_dialog_sign_detail_fee3);
		TextView tvFee4 = (TextView) layout.findViewById(R.id.tv_dialog_sign_detail_fee4);
		Button btnOk = (Button) layout.findViewById(R.id.btn_dialog_sign_detail);
		
		String strFee = context.getResources().getString(R.string.fee); 
		
		tvFee1.setText(String.format(strFee, AmountUtils.changeF2Y((int)fee1, 0)));
		tvFee2.setText(String.format(strFee, AmountUtils.changeF2Y((int)fee2, 0)));
		tvFee3.setText(String.format(strFee, AmountUtils.changeF2Y((int)fee3, 0)));
		tvFee4.setText(String.format(strFee, AmountUtils.changeF2Y((int)fee4, 0)));
		
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(dialog != null){
					dialog.dismiss();
				}
			}
		});

		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);

		dialog.show();
		setDialogWindowAttr(dialog, context);
		isShow = true;
	}

	/**
	 * 关闭窗口
	 */
	public static void closeDialog(){

		if(dialog != null){
			dialog.dismiss();
		}
	}

	//在dialog.show()之后调用
	@SuppressWarnings("deprecation")
	public static void setDialogWindowAttr(Dialog dlg, Context ctx){

		WindowManager wm = ((Activity) ctx).getWindowManager();
		Display display = wm.getDefaultDisplay(); // 为获取屏幕宽、高

		Window window = dlg.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.width = (int) (display.getWidth()/1.4);
		lp.height = (int) (display.getHeight()/2);
		dlg.getWindow().setAttributes(lp);
	}

}
