package com.zhiduan.crowdclient.view;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

/** 
 * 已抢单排序筛选对话框
 * 
 * @author yxx
 *
 * @date 2016-5-3 下午10:00:31
 * 
 */
@SuppressLint("ResourceAsColor")
public class DeliveryOrderSortDialog{

	public static boolean isShow = false;
	static Dialog dialog;

	private static Button[] arrBtn = new Button[6];
	private static LinearLayout[] arrLayout = new LinearLayout[6];

	private static int[] arrId = {
		R.id.sort_time_1, R.id.sort_time_2, 
		R.id.sort_address_1, R.id.sort_address_2, 
		R.id.sort_sex_1, R.id.sort_sex_2};

	private static int[] arrLayoutId = {
		R.id.layout_dialog_sort_1, R.id.layout_dialog_sort_2,
		R.id.layout_dialog_sort_3, R.id.layout_dialog_sort_4,
		R.id.layout_dialog_sort_5, R.id.layout_dialog_sort_6
	};

	// 弹窗结果回调函数
	public static abstract class ResultCallback {
		public abstract void callback(int position);
	}

	public DeliveryOrderSortDialog(Context context){

	}

	/**
	 * @param context
	 * @param strMessage
	 */
	public static void showMyDialog(Context context, final int currentPage, final ResultCallback resultCallback){

		if(dialog != null){
			dialog.dismiss();
		}

		if(context == null){
			return;
		}

		dialog = new Dialog(context, R.style.dialog_sort);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_sort_delivery, null);

		LinearLayout layoutBody = (LinearLayout) layout.findViewById(R.id.layout_sort_top);

		int sortType = (Integer) SharedPreferencesUtils.getParam(Constant.SP_DELIVERY_ORDER_SORT, 1);

		for(int i=0; i<6; i++){

			final int pos = i;
			arrBtn[i] = (Button) layout.findViewById(arrId[i]);
			arrLayout[i] = (LinearLayout) layout.findViewById(arrLayoutId[i]);
			
			arrLayout[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if(pos % 2 == 0){
						OrderUtil.DELIVERY_SORT_TYPE = 2;//1：DESC,2：ASC 默认为DESC
					}else{
						OrderUtil.DELIVERY_SORT_TYPE = 1;//1：DESC,2：ASC 默认为DESC
					}

					if(pos == 0 || pos == 1){//按照时间排序

						if(currentPage == 0 || currentPage == 1){
							OrderUtil.DELIVERY_ORDER_TYPE = 3;//按照配送时间
						}else{
							OrderUtil.DELIVERY_ORDER_TYPE = 4;//按照签收时间
						}
					}else if(pos == 2 || pos == 3){//按照地址排序
						OrderUtil.DELIVERY_ORDER_TYPE = 2;
					}else{//按照性别排序
						OrderUtil.DELIVERY_ORDER_TYPE = 1;
						if(pos % 2 == 0){
							OrderUtil.DELIVERY_SORT_TYPE = 1;
						}else{
							OrderUtil.DELIVERY_SORT_TYPE = 2;
						}
					}

					dialog.dismiss();

					SharedPreferencesUtils.setParam(Constant.SP_DELIVERY_ORDER_TYPE, OrderUtil.DELIVERY_ORDER_TYPE);
					SharedPreferencesUtils.setParam(Constant.SP_DELIVERY_SORT_TYPE, OrderUtil.DELIVERY_SORT_TYPE);
					SharedPreferencesUtils.setParam(Constant.SP_DELIVERY_ORDER_SORT, pos);

					resultCallback.callback(1);
				}
			});

			//这里的颜色值设置要通过上下文获取，否则无效
			if(sortType == i){
				arrBtn[i].setTextColor(MyApplication.getInstance().getResources().getColor(R.color.main_color));
				arrBtn[i].setBackgroundResource(R.drawable.shape_radius_order_main_color);
			}else{
				arrBtn[i].setTextColor(context.getResources().getColor(R.color.text_gray));
				arrBtn[i].setBackgroundResource(R.drawable.shape_radius_gray_round);
			}
		}
		
		layoutBody.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				dialog.dismiss();
			}
		});

		dialog.setContentView(layout);

		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

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
	public static void setDialogWindowAttr(Dialog dlg,Context ctx){

		WindowManager wm = ((Activity) ctx).getWindowManager();
		Display display = wm.getDefaultDisplay(); // 为获取屏幕宽、高

		Window window = dlg.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.gravity = (Gravity.RIGHT | Gravity.TOP);
		lp.width = (int) (display.getWidth());
		lp.height = (int) (display.getHeight());
		dlg.getWindow().setAttributes(lp);
	}

}
