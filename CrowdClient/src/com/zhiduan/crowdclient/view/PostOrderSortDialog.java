package com.zhiduan.crowdclient.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;

/** 
 * 寄件排序筛选对话框
 * 
 * @author yxx
 *
 * @date 2016-5-3 下午10:00:31
 * 
 */
@SuppressLint("ResourceAsColor")
public class PostOrderSortDialog{

	public static boolean isShow = false;
	static Dialog dialog;

	private static Button[] arrBtn = new Button[8];
	private static LinearLayout[] arrLayout = new LinearLayout[8];

	private static int[] arrId = {
		R.id.sort_post_time_1, R.id.sort_post_time_2, 
		R.id.sort_post_address_1, R.id.sort_post_address_2, 
		R.id.sort_post_sex_1, R.id.sort_post_sex_2,
		R.id.sort_post_express_1, R.id.sort_post_express_2};

	private static int[] arrLayoutId = {
		R.id.layout_dialog_sort_post_1, R.id.layout_dialog_sort_post_2,
		R.id.layout_dialog_sort_post_3, R.id.layout_dialog_sort_post_4,
		R.id.layout_dialog_sort_post_5, R.id.layout_dialog_sort_post_6,
		R.id.layout_dialog_sort_post_7, R.id.layout_dialog_sort_post_8
	};

	// 弹窗结果回调函数
	public static abstract class PostResultCallback {
		public abstract void callback(int position);
	}

	public PostOrderSortDialog(Context context){

	}

	/**
	 * @param context
	 * @param strMessage
	 */
	public static void showMyDialog(Context context, final int currentPage, final PostResultCallback resultCallback){

		if(dialog != null){
			dialog.dismiss();
		}

		if(context == null){
			return;
		}

		dialog = new Dialog(context, R.style.dialog_sort);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_sort_post, null);

		LinearLayout layoutBody = (LinearLayout) layout.findViewById(R.id.layout_sort_post_top);
		LinearLayout layoutDialog = (LinearLayout) layout.findViewById(R.id.layout_sort_post_dialog);

		if(currentPage == 1){
			layout.findViewById(R.id.layout_sort_post_time).setVisibility(View.GONE);
			layout.findViewById(R.id.layout_sort_post_time_view).setVisibility(View.GONE);

			LayoutParams lp = layoutDialog.getLayoutParams();
			lp.height = CommandTools.dip2px(context, 150);
		}

		int sortType = (Integer) SharedPreferencesUtils.getParam(Constant.SP_POST_ORDER_SORT, 1);

		for(int i=0; i<8; i++){

			final int pos = i;
			arrBtn[i] = (Button) layout.findViewById(arrId[i]);
			arrLayout[i] = (LinearLayout) layout.findViewById(arrLayoutId[i]);
			arrLayout[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if(pos % 2 == 0){
						OrderUtil.POST_SORT_TYPE = 2;
					}else{
						OrderUtil.POST_SORT_TYPE = 1;
					}

					//1:更新时间,2:寄件人地址,3:寄件人性别,4:快递网点地址
					if(pos == 0 || pos == 1){//按照时间排序
						OrderUtil.POST_ORDER_TYPE = 1;
					}else if(pos == 2 || pos == 3){//按照地址排序
						OrderUtil.POST_ORDER_TYPE = 2;
					}else if(pos == 4 || pos == 5){//按照性别排序
						OrderUtil.POST_ORDER_TYPE = 3;
						if(pos % 2 == 0){
							OrderUtil.POST_SORT_TYPE = 1;
						}else{
							OrderUtil.POST_SORT_TYPE = 2;
						}
					}else{//按照快递公司排序
						OrderUtil.POST_ORDER_TYPE = 4;
					}

					dialog.dismiss();

					SharedPreferencesUtils.setParam(Constant.SP_POST_ORDER_TYPE, OrderUtil.POST_ORDER_TYPE);
					SharedPreferencesUtils.setParam(Constant.SP_POST_SORT_TYPE, OrderUtil.POST_SORT_TYPE);
					SharedPreferencesUtils.setParam(Constant.SP_POST_ORDER_SORT, pos);

					resultCallback.callback(OrderUtil.POST_ORDER_TYPE);
				}
			});

			if(arrBtn[i] == null){
				continue;
			}

			//这里的颜色值设置要通过上下文获取，否则无效
			if(sortType == i){
				arrBtn[i].setTextColor(context.getResources().getColor(R.color.main_color));
				arrBtn[i].setBackgroundResource(R.drawable.shape_radius_order_main_color);
			}else{
				arrBtn[i].setTextColor(context.getResources().getColor(R.color.gray_3));
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

	public void dd(View v){


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
