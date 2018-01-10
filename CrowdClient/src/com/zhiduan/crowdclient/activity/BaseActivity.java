package com.zhiduan.crowdclient.activity;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.im.ChatActivity;
import com.zhiduan.crowdclient.util.ScreenUtil;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;

import de.greenrobot.event.EventBus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * 
 * <pre>
 * Description  项目Activity的基类
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-5-24 上午9:50:27
 * </pre>
 */
public abstract class BaseActivity extends Activity {

	public TextView mTvCenter;
	public TextView mTvRight;

	protected View contentView;
	private LinearLayout layoutBody;

	public FrameLayout durian_head_layout;
	private LinearLayout layoutLeft;
	private LinearLayout layoutCenter;
	private LinearLayout layoutRight;
	private LinearLayout layoutRightPic;
	private ImageView mIvRightPic;
	public Context mContext;

	public EventBus mEventBus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_base);

		mContext = this;
		Utils.addActivity(this);

		mTvCenter = (TextView) findViewById(R.id.title_center);
		mTvRight = (TextView) findViewById(R.id.title_right);

		layoutBody = (LinearLayout) findViewById(R.id.baseAct_body);

		durian_head_layout = (FrameLayout) findViewById(R.id.durian_head_layout);
		layoutLeft = (LinearLayout) findViewById(R.id.layout_title_left);
		layoutCenter = (LinearLayout) findViewById(R.id.layout_title_center);
		layoutRight = (LinearLayout) findViewById(R.id.layout_title_right);
		layoutRightPic = (LinearLayout) findViewById(R.id.layout_title_right_pic);
		mIvRightPic = (ImageView) findViewById(R.id.title_right_pic);
		onBaseCreate(savedInstanceState);
		setImmerseLayout(findViewById(R.id.durian_head_layout));

		initView();
		initData();

		if (layoutRightPic != null) {

			layoutRightPic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					rightPicClick();
				}
			});
		}

		if (layoutRight != null) {
			layoutRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					rightClick();
				}

			});

		}

	}

	protected void setImmerseLayout(View view) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			/*
			 * window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
			 * , WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			 */
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			int statusBarHeight = ScreenUtil.getStatusBarHeight(this
					.getBaseContext());
			view.setPadding(0, statusBarHeight, 0, 0);
		}
	}

	protected void setImmerseLayout() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyApplication.baseActivity = this;

		if(mEventBus == null){
			mEventBus = EventBus.getDefault();
			mEventBus.register(this);
		}
		Log.i("hexiuhui--", "baseActivity----onResume");
	}

	public void setAlpha(float alpha) {
		durian_head_layout.setAlpha(alpha);
	}

	/**
	 * 创建界面
	 * 
	 * @param savedInstanceState
	 */
	protected abstract void onBaseCreate(Bundle savedInstanceState);

	/**
	 * 初始化控件
	 */
	public abstract void initView();

	/**
	 * 初始化数据
	 */
	public abstract void initData();

	public void setContentViewId(int layoutId, final Activity activity) {

		contentView = getLayoutInflater().inflate(layoutId, null);
		if (layoutBody.getChildCount() > 0) {
			layoutBody.removeAllViews();
		}
		if (contentView != null) {
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			layoutBody.addView(contentView, params);
		}

	}

	public void setContentViewId(View layoutId, final Activity activity) {

		if (layoutBody.getChildCount() > 0) {
			layoutBody.removeAllViews();
		}
		if (layoutId != null) {
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			layoutBody.addView(layoutId, params);
		}

	}

	/**
	 * 隐藏顶部栏
	 */
	public void hidenTopLayout() {

		durian_head_layout.setVisibility(View.GONE);
	}

	/**
	 * 返回键
	 * 
	 * @param v
	 */
	public void back(View v) {

		finish();
	}

	/**
	 * 得到右侧图片
	 */
	public ImageView getRightImg() {
		return mIvRightPic;
	}

	/**
	 * 设置标题
	 * 
	 * @param mReturn
	 */
	public void setTitle(String mTvCenter) {
		this.mTvCenter.setText(mTvCenter);
	}

	/**
	 * 给右侧标题设置背景
	 */
	public void setRightTitleBackground(int id) {

		mTvRight.setBackgroundResource(id);
	}

	/**
	 * 给右侧标题设置文本
	 */
	public void setRightTitleText(String text) {

		mTvRight.setText(text);
	}

	/**
	 * 给右侧标题设置文本颜色
	 * @param color
	 */
	public void setRightTitleTextColor(int color) {

		mTvRight.setTextColor(color);
	}

	/**
	 * 获取右侧标题按钮
	 */
	public TextView GetRightTitleButton() {

		return mTvRight;
	}

	public void setButtonFalse() {
		layoutRight.setClickable(false);
	}

	public void setButtonTrue() {
		layoutRight.setClickable(true);
	}

	/**
	 * 右侧标题的点击事件
	 * 
	 * @param v
	 */
	public void clickRightTitle(View.OnClickListener listener) {
		mTvRight.setOnClickListener(listener);

	}

	/**
	 * 左侧标题的点击事件
	 * 
	 * @param v
	 */
	public void clickLeftTitle(View.OnClickListener listener) {
		layoutLeft.setOnClickListener(listener);

	}

	/**
	 * 隐藏左侧标题
	 */
	public void hidenLeftTitle() {

		layoutLeft.setVisibility(View.INVISIBLE);
	}

	/**
	 * 隐藏右侧标题
	 */
	public void hidenRightTitle() {

		layoutRight.setVisibility(View.INVISIBLE);
	}

	/**
	 * 隐藏右侧标题
	 */
	public void hidenRightGone() {

		layoutRight.setVisibility(View.GONE);
	}

	/**
	 * 显示右侧图片
	 */
	public void ShowRightPic() {

		layoutRightPic.setVisibility(View.VISIBLE);
	}

	/**
	 * 显示右侧图片
	 */
	public View getRightPic() {

		return layoutRightPic;
	}

	/**
	 * 设置右侧图片
	 */
	public void setRightPic(int resId) {

		mIvRightPic.setImageResource(resId);
	}

	protected void onStart() {
		super.onStart();
	}

	/**
	 * 图片的点击事件
	 */
	public void rightPicClick() {

	}

	/**
	 * 文字的点击事件
	 */
	public void rightClick() {
		// TODO Auto-generated method stub

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//在继承BaseActivity的销毁方法中使用销毁Dialog
		//这样跨线程销毁Dialog，就不会出现not attached to window manager的异常

		//		GeneralDialog.dismiss();
	}

	/**
	 * 调用系统发短信界面
	 * @param phone
	 */
	public void sendSms(String phone){
		Log.i("hexiuhui--", "sendSms" + phone);
		// start chat acitivity
		//		Intent intent = new Intent(mContext, ChatActivity.class);
		//		intent.putExtra(ImConstant.EXTRA_CHAT_TYPE, ImConstant.CHATTYPE_CHATROOM);
		//		// it's single chat
		//		intent.putExtra(ImConstant.EXTRA_USER_ID, phone);
		//		startActivity(intent);
		startActivity(new Intent(mContext, ChatActivity.class).putExtra("userId", phone));

	}

	/**
	 * 打电话
	 * @param phone
	 */
	public void callPhone(String phone){

		DialogUtils.showCallPhoneDialog(mContext, phone, null);
	}

	public void onEventMainThread(Message msg) {

	}
}
