package com.zhiduan.crowdclient.view.dialog;

import org.json.JSONArray;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.Res;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * 排序筛选对话框
 * 
 * @author yxx
 *
 * @date 2016-5-3 下午10:00:31
 * 
 */
public class OrderSortMenuDialog{

	private static CheckBox radioButton1;
	private static CheckBox radioButton2;
	private static CheckBox radioButton3;
	private static CheckBox radioButton4;
	private static CheckBox checkBox5;
	private static CheckBox checkBox6;
	private static CheckBox checkBox7;
	private static CheckBox checkBox8;
	private static CheckBox checkBox9;
	private static CheckBox checkBox10;
	private static CheckBox checkBox11;
	private static CheckBox checkBox12;
	static Dialog dialog;

	public static JSONArray categoryIdList = new JSONArray();
	public static int orderType = 0;//排序类型 1:价格：2：配送时间
	public static int sex = 0;//按照性别查询 1：男 2：女
	public static int sortType = 0;//排序方式类型 1：DESC,2：ASC 默认为DESC
	public static int taskMode = 0;//需求人数 1:单人，2:多人

	public static abstract class ResultCallback {
		public abstract void callback(int position);
	}

	public OrderSortMenuDialog(Context context){

	}

	/**
	 * @param context
	 * @param strMessage
	 */
	public static void showDialog(final Context context, final ResultCallback resultCallback){

		if(dialog != null){
			dialog.dismiss();
		}

		if(context == null){
			return;
		}

		dialog = new Dialog(context, R.style.dialog_sort);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_order_sort, null);

		View layoutBody = (View) layout.findViewById(R.id.layout_order_sort_top);

		radioButton1 = (CheckBox) layout.findViewById(R.id.order_sort_1);
		radioButton2 = (CheckBox) layout.findViewById(R.id.order_sort_2);
		radioButton3 = (CheckBox) layout.findViewById(R.id.order_sort_3);
		radioButton4 = (CheckBox) layout.findViewById(R.id.order_sort_4);

		checkBox5 = (CheckBox) layout.findViewById(R.id.order_sort_5);
		checkBox6 = (CheckBox) layout.findViewById(R.id.order_sort_6);
		checkBox7 = (CheckBox) layout.findViewById(R.id.order_sort_7);
		checkBox8 = (CheckBox) layout.findViewById(R.id.order_sort_8);
		checkBox9 = (CheckBox) layout.findViewById(R.id.order_sort_9);
		checkBox10 = (CheckBox) layout.findViewById(R.id.order_sort_10);
		checkBox11 = (CheckBox) layout.findViewById(R.id.order_sort_11);
		checkBox12 = (CheckBox) layout.findViewById(R.id.order_sort_12);

		TextView btnReset = (TextView) layout.findViewById(R.id.order_sort_reset);
		Button btnOk = (Button) layout.findViewById(R.id.order_sort_ok);

		if(orderType == 1 && sortType == 1){
			radioButton1.setChecked(true);
			radioButton1.setTextColor(Res.getColor(R.color.white));
		}else if(orderType == 1 && sortType == 2){
			radioButton2.setChecked(true);
			radioButton2.setTextColor(Res.getColor(R.color.white));
		}else if(orderType == 2 && sortType == 2){
			radioButton3.setChecked(true);
			radioButton3.setTextColor(Res.getColor(R.color.white));
		}else if(orderType == 2 && sortType == 1){
			radioButton4.setChecked(true);
			radioButton4.setTextColor(Res.getColor(R.color.white));
		}

		if(sex == 0){
			checkBox5.setChecked(false);
			checkBox6.setChecked(false);
		}else if(sex == 1){
			checkBox5.setChecked(true);
			checkBox5.setTextColor(Res.getColor(R.color.white));
		}else if(sex == 2){
			checkBox6.setChecked(true);
			checkBox6.setTextColor(Res.getColor(R.color.white));
		}else if(sex == 3){
			checkBox5.setChecked(true);
			checkBox6.setChecked(true);
			checkBox5.setTextColor(Res.getColor(R.color.white));
			checkBox6.setTextColor(Res.getColor(R.color.white));
		}

		if(taskMode == 0){
			checkBox7.setChecked(false);
			checkBox8.setChecked(false);
		}else if(taskMode == 1){
			checkBox7.setChecked(true);
			checkBox7.setTextColor(Res.getColor(R.color.white));
		}else if(taskMode == 2){
			checkBox8.setChecked(true);
			checkBox8.setTextColor(Res.getColor(R.color.white));
		}else if(taskMode == 3){
			checkBox7.setChecked(true);
			checkBox8.setChecked(true);
			checkBox7.setTextColor(Res.getColor(R.color.white));
			checkBox8.setTextColor(Res.getColor(R.color.white));
		}

		if(categoryIdList.toString().contains(OrderUtil.EXPRESS_TYPE + "")){
			checkBox9.setChecked(true);
			checkBox9.setTextColor(Res.getColor(R.color.white));
		}
		if(categoryIdList.toString().contains(OrderUtil.ERRANDS_TYPE + "")){
			checkBox10.setChecked(true);
			checkBox10.setTextColor(Res.getColor(R.color.white));
		}
		if(categoryIdList.toString().contains(OrderUtil.TASK_TYPE + "")){
			checkBox11.setChecked(true);
			checkBox11.setTextColor(Res.getColor(R.color.white));
		}
		if(categoryIdList.toString().contains(OrderUtil.PRODUCT_TYPE + "")){
			checkBox12.setChecked(true);
			checkBox12.setTextColor(Res.getColor(R.color.white));
		}

		radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 

			@Override 
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { 

				if(isChecked){
					setPriceAndTime(1, true);
				}else{
					radioButton1.setTextColor(Res.getColor(R.color.text_point));
				}
			} 
		}); 

		radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 

			@Override 
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { 

				if(isChecked){
					setPriceAndTime(2, true);
				}else{
					radioButton2.setTextColor(Res.getColor(R.color.text_point));
				}
			} 
		}); 

		radioButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 

			@Override 
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { 

				if(isChecked){
					setPriceAndTime(3, true);
				}else{
					radioButton3.setTextColor(Res.getColor(R.color.text_point));
				}
			} 
		}); 

		radioButton4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 

			@Override 
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { 

				if(isChecked){
					setPriceAndTime(4, true);
				}else{
					radioButton4.setTextColor(Res.getColor(R.color.text_point));
				}
			} 
		}); 

		checkBox5.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1 == true){
					checkBox5.setChecked(true);
					checkBox5.setTextColor(Res.getColor(R.color.white));
				}else{
					checkBox5.setChecked(false);
					checkBox5.setTextColor(Res.getColor(R.color.text_point));
				}
			}
		});

		checkBox6.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1 == true){
					checkBox6.setChecked(true);
					checkBox6.setTextColor(Res.getColor(R.color.white));
				}else{
					checkBox6.setChecked(false);
					checkBox6.setTextColor(Res.getColor(R.color.text_point));
				}
			}
		});

		checkBox7.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1 == true){
					checkBox7.setTextColor(Res.getColor(R.color.white));
				}else{
					checkBox7.setTextColor(Res.getColor(R.color.text_point));
				}
			}
		});

		checkBox8.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1 == true){
					checkBox8.setTextColor(Res.getColor(R.color.white));
				}else{
					checkBox8.setTextColor(Res.getColor(R.color.text_point));
				}
			}
		});

		checkBox9.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1 == true){
					checkBox9.setTextColor(Res.getColor(R.color.white));
				}else{
					checkBox9.setTextColor(Res.getColor(R.color.text_point));
				}
			}
		});

		checkBox10.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1 == true){
					checkBox10.setTextColor(Res.getColor(R.color.white));
				}else{
					checkBox10.setTextColor(Res.getColor(R.color.text_point));
				}
			}
		});

		checkBox11.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1 == true){
					checkBox11.setTextColor(Res.getColor(R.color.white));
				}else{
					checkBox11.setTextColor(Res.getColor(R.color.text_point));
				}
			}
		});

		checkBox12.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1 == true){
					checkBox12.setTextColor(Res.getColor(R.color.white));
				}else{
					checkBox12.setTextColor(Res.getColor(R.color.text_point));
				}
			}
		});


		btnReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				//				public static ArrayList<Integer> categoryIdList = new ArrayList<Integer>();
				//				public static int orderType = 0;//排序类型 1:价格：2：配送时间
				//				public static int sex = 0;//按照性别查询 1：男 2：女
				//				public static int sortType = 0;//排序方式类型 1：DESC,2：ASC 默认为DESC
				//				public static int taskMode = 0;//需求人数 1:单人，2:多人

				categoryIdList = new JSONArray();
				orderType = 0;
				sex = 0;
				sortType = 0;
				taskMode = 0;

				radioButton1.setChecked(false);
				radioButton2.setChecked(false);
				radioButton3.setChecked(false);
				radioButton4.setChecked(false);
				checkBox5.setChecked(false);
				checkBox6.setChecked(false);
				checkBox7.setChecked(false);
				checkBox8.setChecked(false);
				checkBox9.setChecked(false);
				checkBox10.setChecked(false);
				checkBox11.setChecked(false);
				checkBox12.setChecked(false);
			}
		});

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				getData(resultCallback);
			}
		});

		layoutBody.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				closeDialog();
			}
		});

		dialog.setContentView(layout);

		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

		dialog.show();
		//		setDialogWidth(context, layoutBody);
		setDialogWindowAttr(dialog, context);
	}

	/**
	 * 关闭窗口
	 */
	public static void closeDialog(){

		if(dialog != null && dialog.isShowing()){
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
		lp.gravity = (Gravity.CENTER);
		lp.width = (int) (display.getWidth());
		lp.height = (int) (display.getHeight());
		dlg.getWindow().setAttributes(lp);
	}

	/**
	 * 排序检查
	 */
	private static void getData(ResultCallback resultCallback){

		if(radioButton1.isChecked()){

			orderType = 1;
			sortType = 1;
		}else if(radioButton2.isChecked()){

			orderType = 1;
			sortType = 2;
		}else if(radioButton3.isChecked()){

			orderType = 2;
			sortType = 2;
		}else if(radioButton4.isChecked()){

			orderType = 2;
			sortType = 1;
		}else{

			orderType = 0;
			sortType = 0;
		}

		if(checkBox5.isChecked() && !checkBox6.isChecked()){//选择男
			sex = 1;
		}else if(!checkBox5.isChecked() && checkBox6.isChecked()){//选择女
			sex = 2;
		}else if(checkBox5.isChecked() && checkBox6.isChecked()){//都选
			sex = 3;
		}else if(!checkBox5.isChecked() && !checkBox6.isChecked()){//都不选
			sex = 0;
		}

		if(checkBox7.isChecked() && !checkBox8.isChecked()){//选择单人
			taskMode = 1;
		}else if(!checkBox7.isChecked() && checkBox8.isChecked()){//选择多人
			taskMode = 2;
		}else if(checkBox7.isChecked() && checkBox8.isChecked()){//都选
			taskMode = 3;
		}else if(!checkBox7.isChecked() && !checkBox8.isChecked()){//都不选
			taskMode = 0;
		}

		categoryIdList = new JSONArray();
		if(checkBox9.isChecked()){
			categoryIdList.put(OrderUtil.EXPRESS_TYPE);//快递
		}
		if(checkBox10.isChecked()){
			categoryIdList.put(OrderUtil.ERRANDS_TYPE);//跑腿
		}
		if(checkBox11.isChecked()){
			categoryIdList.put(OrderUtil.TASK_TYPE);//任务
		}
		if(checkBox12.isChecked()){
			categoryIdList.put(OrderUtil.PRODUCT_TYPE);//商品
		}

		closeDialog();
		resultCallback.callback(0);
	}

	/**
	 * 设置时间和价格的选择变化
	 * 选择后刷新字体颜色和背景框
	 * @param pos
	 */
	public static void setPriceAndTime(int pos, boolean flag){

		radioButton1.setChecked(false);
		radioButton2.setChecked(false);
		radioButton3.setChecked(false);
		radioButton4.setChecked(false);

		radioButton1.setTextColor(Res.getColor(R.color.text_point));
		radioButton2.setTextColor(Res.getColor(R.color.text_point));
		radioButton3.setTextColor(Res.getColor(R.color.text_point));
		radioButton4.setTextColor(Res.getColor(R.color.text_point));

		if(pos == 1){
			radioButton1.setChecked(true);
			radioButton1.setTextColor(Res.getColor(R.color.white));
		}else if(pos == 2){
			radioButton2.setChecked(true);
			radioButton2.setTextColor(Res.getColor(R.color.white));
		}else if(pos == 3){
			radioButton3.setChecked(true);
			radioButton3.setTextColor(Res.getColor(R.color.white));
		}else if(pos == 4){
			radioButton4.setChecked(true);
			radioButton4.setTextColor(Res.getColor(R.color.white));
		}
	}

	/**
	 * 设置当前对话框宽度
	 * @param v
	 */
	@SuppressWarnings("deprecation")
	public static void setDialogWidth(Context context, View v){

		WindowManager wm = ((Activity) context).getWindowManager();
		Display display = wm.getDefaultDisplay(); // 为获取屏幕宽、高

		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) v.getLayoutParams();// 取控件aaa当前的布局参数

		linearParams.width = display.getWidth()/2;//显示2/3
		linearParams.height = display.getHeight(); 
		v.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件aaa
	}

}
