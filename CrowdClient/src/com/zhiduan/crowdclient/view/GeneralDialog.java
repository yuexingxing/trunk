package com.zhiduan.crowdclient.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.CommandTools;

/** 
 * 
 * 
 * @date 2016-10-17 下午7:30:14
 * 
 */
public class GeneralDialog {

	private static Dialog dialog;

	public static final int DIALOG_ICON_TYPE_1 = 1001;
	public static final int DIALOG_ICON_TYPE_2 = 1002;
	public static final int DIALOG_ICON_TYPE_3 = 1003;
	public static final int DIALOG_ICON_TYPE_4 = 1004;
	public static final int DIALOG_ICON_TYPE_5 = 1005;
	public static final int DIALOG_ICON_TYPE_6 = 1006;
	public static final int DIALOG_ICON_TYPE_7 = 1007;
	public static final int DIALOG_ICON_TYPE_8 = 1008;

	private final int[] arrType = {
			1001, 1002, 
			1003, 1004,
			1005, 1006, 
			1007, 1008
	};

	private final int[] arrDrawable = {
			R.drawable.dialog_icon_1, R.drawable.dialog_icon_2, 
			R.drawable.dialog_icon_3, R.drawable.dialog_icon_4,
			R.drawable.dialog_icon_5, R.drawable.dialog_icon_6, 
			R.drawable.dialog_icon_7, R.drawable.dialog_icon_8
	};

	/**
	 * 一個按鈕的对话框 监听可以为null，默认点击取消对话框
	 * 
	 * @param context
	 * @param type
	 * @param title
	 * @param content
	 * @param buttonText
	 * @param oneButtonListener
	 *            可以为null，默认点击取消对话框
	 */
	public static void showOneButtonDialog(Context context, int type,
			String title, String content, String buttonText,
			OneButtonListener oneButtonListener) {

		if(context == null){
			return;
		}

		try{
			if (dialog != null) {
				dialog.dismiss();
			}
			GeneralDialog generalDialog = new GeneralDialog();
			View view = generalDialog.returnOneButtonViewImport(context, type, title,
					content, buttonText, oneButtonListener);
			dialog = new Dialog(context, R.style.dialog_no_border);
			dialog.setContentView(view);
			dialog.setCanceledOnTouchOutside(false);
			Window window = dialog.getWindow();
			window.getAttributes().width = CommandTools.dip2px(context, 300);
			window.getAttributes().height = CommandTools.dip2px(context, 240);
			dialog.show();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * 兩個按鈕的对话框 监听可以为null，默认点击取消对话框
	 * 
	 * @param context
	 * @param type
	 * @param title
	 * @param content
	 * @param leftButtonText
	 * @param rightButtonText
	 * @param twoButtonListener
	 *            可以为null，默认点击取消对话框
	 */
	public static void showTwoButtonDialog(Context context, int type,
			String title, String content, String leftButtonText,
			String rightButtonText, TwoButtonListener twoButtonListener) {

		if(context == null){
			return;
		}

		try{

			if (dialog != null) {
				dialog.dismiss();
			}

			GeneralDialog generalDialog = new GeneralDialog();
			View view = generalDialog.returnTwoButtonViewImport(context, type, title,
					content, leftButtonText, rightButtonText, twoButtonListener);
			dialog = new Dialog(context, R.style.dialog_no_border);
			dialog.setContentView(view);
			dialog.setCanceledOnTouchOutside(false);
			Window window = dialog.getWindow();
			window.getAttributes().width = CommandTools.dip2px(context, 270);
			window.getAttributes().height = CommandTools.dip2px(context, 230);
			dialog.show();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * 沒有按鈕的对话框 监听可以为null，默认点击取消对话框
	 * 
	 * @param context
	 * @param type
	 * @param content
	 * @param noButtonListener
	 *            可以为null，默认点击取消对话框
	 */
	public static void showNoButtonDialog(Context context, int type, String content, NoButtonListener noButtonListener) {

		if(context == null){
			return;
		}

		if (dialog != null) {
			dialog.dismiss();
		}

		try{
			GeneralDialog generalDialog = new GeneralDialog();
			View view = generalDialog.returnNoButtonViewImport(context, content, type, noButtonListener);
			dialog = new Dialog(context, R.style.dialog_no_border);
			dialog.setContentView(view);
			dialog.setCanceledOnTouchOutside(false);
			Window window = dialog.getWindow();
			window.getAttributes().width = CommandTools.dip2px(context, 300);
			window.getAttributes().height = CommandTools.dip2px(context, 250);
			dialog.show();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 返回一个按钮的View布局 监听可以为null，默认点击取消对话框
	 * 
	 * @param type
	 * @param money
	 * @param string
	 * @param b
	 * @param oneButtonListener
	 *            可以为null，默认点击取消对话框
	 * @return
	 */
	private View returnOneButtonViewImport(Context context, int type, String title,
			String content, String buttonText,
			final OneButtonListener oneButtonListener) {
		View view = View.inflate(context, R.layout.dialog__one_button_view, null);

		final ImageView ivShutDown = (ImageView) view.findViewById(R.id.one_button_iv_shut_down);
		final RelativeLayout rlShutDown = (RelativeLayout) view.findViewById(R.id.one_button_rl_shut_down);
		final ImageView ivCarryOut = (ImageView) view.findViewById(R.id.one_button_iv_carry_out);
		final RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.one_button_rl);
		TextView tvTitle = (TextView) view.findViewById(R.id.one_button_tv_title);
		TextView tvContent = (TextView) view.findViewById(R.id.one_button_tv_content);
		Button bt = (Button) view.findViewById(R.id.one_button_bt_one);

		if(TextUtils.isEmpty(title)){
			tvTitle.setVisibility(View.GONE);
		}else{
			tvTitle.setVisibility(View.VISIBLE);
		}

		tvTitle.setText(title + "");
		tvContent.setText(content + "");
		bt.setText(buttonText);

		checkType(type, ivCarryOut);

		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (oneButtonListener != null) {
					if (dialog != null) {
						MyApplication.getInstance();
						MyApplication.baseActivity.runOnUiThread(
								new Thread(){
									@Override
									public void run() {
										oneButtonListener.onButtonClick(dialog);
									}
								});
					}
				} else {
					if (dialog != null) {

						dialog.dismiss();
					}
				}
			}
		});

		rlShutDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (oneButtonListener != null) {
					if (dialog != null) {
						MyApplication.getInstance();
						MyApplication.baseActivity.runOnUiThread(
								new Thread(){
									@Override
									public void run() {
										oneButtonListener.onExitClick(dialog);
									}
								});
					}
				} else {
					if (dialog != null) {

						dialog.dismiss();
					}
				}
			}
		});

		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

		ivShutDown.measure(w, h);
		ivCarryOut.measure(w, h);

		int widthShutDown = ivShutDown.getMeasuredWidth();
		int heightCarryOut = ivCarryOut.getMeasuredHeight();

		final RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) rl.getLayoutParams();

		if (widthShutDown == 0) {

			final ViewTreeObserver vto = ivShutDown.getViewTreeObserver();
			vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					vto.removeOnPreDrawListener(this);
					int width = ivShutDown.getMeasuredWidth();
					layoutParams.leftMargin = width;
					layoutParams.rightMargin = width;
					rl.setLayoutParams(layoutParams);
					return true;
				}
			});
		} else {
			layoutParams.leftMargin = widthShutDown;
			layoutParams.rightMargin = widthShutDown;
			rl.setLayoutParams(layoutParams);
		}

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

		return view;
	}

	/**
	 * 兩個按鈕的对话框View
	 * 
	 * @param title
	 * @param content
	 * @param leftButtonText
	 * @param rightButtonText
	 * @param twoButtonListener
	 * @return
	 */
	private View returnTwoButtonViewImport(Context context, int type, String title,
			String content, String leftButtonText, String rightButtonText,
			final TwoButtonListener twoButtonListener) {
		View view = View.inflate(context, R.layout.dialog_two_button_view, null);

		final ImageView ivCarryOut = (ImageView) view.findViewById(R.id.two_button_iv_carry_out);
		final RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.two_button_rl);
		TextView tvTitle = (TextView) view.findViewById(R.id.two_button_tv_title);
		TextView tvContent = (TextView) view.findViewById(R.id.two_button_tv_content);
		Button btLeft = (Button) view.findViewById(R.id.two_button_bt_one_left);
		Button btRight = (Button) view.findViewById(R.id.two_button_bt_right);

		checkType(type, ivCarryOut);

		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

		if(TextUtils.isEmpty(title)){
			tvTitle.setVisibility(View.GONE);
		}else{
			tvTitle.setVisibility(View.VISIBLE);
		}

		tvTitle.setText(title);
		tvContent.setText(content);
		btLeft.setText(leftButtonText);
		btRight.setText(rightButtonText);

		ivCarryOut.measure(w, h);
		int heightCarryOut = ivCarryOut.getMeasuredHeight();
		final RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) rl
				.getLayoutParams();
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
		btLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (twoButtonListener != null) {
					if (dialog != null) {
						MyApplication.getInstance();
						MyApplication.baseActivity.runOnUiThread(
								new Thread(){
									@Override
									public void run() {
										twoButtonListener.onLeftClick(dialog);
									}
								});
					}
				} else {
					if (dialog != null) {
						dialog.dismiss();
					}
				}
			}
		});
		btRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (twoButtonListener != null) {
					if (dialog != null) {
						MyApplication.getInstance();
						MyApplication.baseActivity.runOnUiThread(
								new Thread(){
									@Override
									public void run() {
										twoButtonListener.onRightClick(dialog);
									}
								});
					}
				} else {
					if (dialog != null) {
						dialog.dismiss();
					}
				}

			}
		});

		return view;
	}

	/**
	 * 没有按钮的对话框
	 * 
	 * @param content
	 * @param type
	 * @param noButtonListener
	 * @return
	 */
	private View returnNoButtonViewImport(Context context, String content, int type, final NoButtonListener noButtonListener) {

		View view = View.inflate(context, R.layout.dialog_no_button_view, null);

		RelativeLayout rlShut_down = (RelativeLayout) view.findViewById(R.id.no_button_rl_shut_down);
		final ImageView ivShutDown = (ImageView) view.findViewById(R.id.no_button_iv_shut_down);
		final LinearLayout ll = (LinearLayout) view.findViewById(R.id.one_button_ll);
		ImageView ivCarry_out = (ImageView) view.findViewById(R.id.no_button_iv_carry_out);
		TextView tvContent = (TextView) view.findViewById(R.id.no_button_tv_content);
		TextView tvDetail = (TextView) view.findViewById(R.id.no_button_tv_detail);

		tvDetail.setVisibility(View.GONE);

		rlShut_down.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (noButtonListener != null) {
					if (dialog != null) {
						MyApplication.getInstance();
						MyApplication.baseActivity.runOnUiThread(
								new Thread(){
									@Override
									public void run() {
										noButtonListener.onExitClick(dialog);
									}
								});
					}
				} else {
					if (dialog != null) {
						dialog.dismiss();
					}
				}
			}
		});

		tvDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (noButtonListener != null) {
					if (dialog != null) {
						MyApplication.getInstance();
						MyApplication.baseActivity.runOnUiThread(
								new Thread(){
									@Override
									public void run() {
										noButtonListener.onDetailClick(dialog);
									}
								});
					}
				} else {
					if (dialog != null) {
						dialog.dismiss();
					}
				}
			}
		});

		checkType(type, ivCarry_out);

		tvContent.setText(content);
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		ivShutDown.measure(w, h);
		int widthShutDown = ivShutDown.getMeasuredWidth();
		int heightShutDown = ivShutDown.getMeasuredHeight();

		final RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) ll
				.getLayoutParams();

		if (widthShutDown == 0) {
			final ViewTreeObserver vto = ivShutDown.getViewTreeObserver();
			vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				public boolean onPreDraw() {
					vto.removeOnPreDrawListener(this);
					int width = ivShutDown.getMeasuredWidth();
					int height = ivShutDown.getMeasuredHeight();
					layoutParams.leftMargin = width;
					layoutParams.rightMargin = width;
					layoutParams.topMargin = height;
					ll.setLayoutParams(layoutParams);
					return true;
				}
			});
		} else {
			layoutParams.leftMargin = widthShutDown;
			layoutParams.rightMargin = widthShutDown;
			layoutParams.topMargin = heightShutDown;
			ll.setLayoutParams(layoutParams);
		}

		return view;
	}

	/**
	 * 对话框类型检验
	 * @param type
	 * @param imgView
	 */
	private void checkType(int type, ImageView imgView){

		int len = 8;
		for(int i=0; i<len; i++){

			if(type == arrType[i]){

				imgView.setImageResource(arrDrawable[i]);
				break;
			}
		}
	}

	/**
	 * 一个按钮对话框的回调
	 * 
	 * @author wfq
	 * 
	 */
	public interface OneButtonListener {
		void onButtonClick(Dialog dialog);

		void onExitClick(Dialog dialog);
	}

	/**
	 * 两个按钮对话框的回调
	 * 
	 * @author wfq
	 * 
	 */
	public interface TwoButtonListener {
		void onLeftClick(Dialog dialog);

		void onRightClick(Dialog dialog);
	}

	/**
	 * 没有按钮对话框的回调
	 * 
	 * @author wfq
	 * 
	 */
	public interface NoButtonListener {
		void onExitClick(Dialog dialog);

		void onDetailClick(Dialog dialog);
	}

	public static void dismiss(){
		if(dialog!=null){
			dialog.dismiss();
			dialog=null;
		}
	}
}
