package com.zhiduan.crowdclient.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.CommonAdapter;
import com.zhiduan.crowdclient.adapter.ViewHolder;
import com.zhiduan.crowdclient.util.CommandTools;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * 抽屉
 * 
 * @author wfq
 * 
 */
@TargetApi(19)
public class DrawerUpView extends RelativeLayout {
	private Context mContext;
	private ArrayList<String> mData = new ArrayList<String>();
	private CommonAdapter<String> mAdapter;
	private ListView listView;
	private int screenHeight;
	private int titleHeight;
	private int height;
	private boolean disappear = false;

	public DrawerUpView(Context context) {
		super(context);
		mContext = context;
		initView();
	}

	public DrawerUpView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	public DrawerUpView(Context context, AttributeSet attrs, int i) {
		super(context, attrs, i);
		mContext = context;
		initView();
	}

	private void initView() {
		lock = new ReentrantLock();
		Rect frame = new Rect();
		MyApplication.baseActivity.getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(frame);
		statusBarHeight = frame.top;
		titleHeight = CommandTools.dip2px(mContext, 45) + statusBarHeight;
		int screenWidth = MyApplication.baseActivity.getWindowManager()
				.getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
		//TODO
		if(android.os.Build.MANUFACTURER.contains("samsung")){
			screenHeight = MyApplication.baseActivity.getWindowManager()
					.getDefaultDisplay().getHeight()
					- titleHeight-getTypeHeight();
		}else{
			screenHeight = MyApplication.baseActivity.getWindowManager()
					.getDefaultDisplay().getHeight()
					- titleHeight;
		}
		ImageView imageView = new ImageView(mContext);
		listView = new ListView(mContext);

		addView(imageView);
		addView(listView);
		DrawerUpView.LayoutParams layoutParams = (DrawerUpView.LayoutParams) imageView
				.getLayoutParams();
		layoutParams.width = screenWidth;
		layoutParams.height = screenHeight;
		imageView.setBackgroundResource(R.color.gray_lucency);

		listView.setAdapter(mAdapter = new CommonAdapter<String>(mContext,
				mData, R.layout.textview) {

			@Override
			public void convert(ViewHolder helper, String item) {
				helper.setText(R.id.text, item);
				View view = helper.getView(R.id.text);
				view.getLayoutParams().height = itemHeight;
			}
		});
		listView.setBackgroundResource(R.color.white);
		DrawerUpView.LayoutParams listViewParams = (DrawerUpView.LayoutParams) listView
				.getLayoutParams();
		listViewParams.topMargin = screenHeight;
		listView.setLayoutParams(listViewParams);
		listView.setVerticalScrollBarEnabled(false);
		Drawable drawable = mContext.getResources().getDrawable(R.color.gray_line);
		listView.setDivider(drawable);
		listView.setDividerHeight(1);
		
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (disappear) {
					return;
				}
				hideDrawerTime(mAnimationTime,mAlphaTime);
			}
		});
		setVisibility(View.GONE);
	}

	/**
	 * 设置数据
	 * 
	 * @param data
	 */
	public void setData(ArrayList<String> data) {
		mData.clear();
		mData.addAll(data);
		mAdapter.notifyDataSetChanged();
		valueHeight = screenHeight - itemHeight * mData.size() + mData.size();
		height = itemHeight * mData.size() + mData.size();
		listView.getLayoutParams().height = height;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	private int valueHeight;
	private boolean isDrawer = false;
	private boolean isshowDrawer = false;
	private int statusBarHeight;
	private int itemHeight = CommandTools.dip2px(MyApplication.baseActivity, 40);
	private Lock lock;

	/**
	 * 显示抽屉
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	public synchronized void showDrawerTime(final int animationTime,final int alphaTime) {
		if (isDrawer) {
			return;
		}

		if (animator != null && animator.isPaused()) {
			return;
		}
		lock.lock();
		isUnLock = false;
		isDrawer = true;
		setVisibility(View.VISIBLE);

		// 初始化
		Animation alphaAnimation = new AlphaAnimation(0f, 1.0f);
		// 设置动画时间
		alphaAnimation.setDuration(alphaTime);
		alphaAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				animator = ValueAnimator.ofInt(screenHeight, valueHeight);

				animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@SuppressLint("NewApi")
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {

						DrawerUpView.LayoutParams listViewParams = (DrawerUpView.LayoutParams) listView
								.getLayoutParams();
						listViewParams.topMargin = (Integer) animation
								.getAnimatedValue();
						listView.setLayoutParams(listViewParams);
						int valueHeight = (Integer) animation
								.getAnimatedValue();
						if (valueHeight == screenHeight) {
							if (!isUnLock) {

								isDrawer = false;
								isshowDrawer = true;

								lock.unlock();
								isUnLock = true;
							}
						}
					}

				});

				animator.setDuration(animationTime);
				animator.start();
			}
		});
		startAnimation(alphaAnimation);

	}

	private boolean isUnLock = false;
	private boolean isAnimationStop = false;

	/**
	 * 隐藏抽屉
	 * 
	 * @return
	 */
	public synchronized void hideDrawerTime(final int animationTime,final int alphaTime) {
		if (isDrawer) {
			return;
		}
		if (!isshowDrawer) {
			return;
		}
		if (animator != null && animator.isPaused()) {
			return;
		}
		Rect r = new Rect();
		listView.getGlobalVisibleRect(r);
		if (valueHeight > r.top - titleHeight) {
			return;
		}
		lock.lock();
		isUnLock = false;
		isDrawer = true;
		isAnimationStop = false;
		animator = ValueAnimator.ofInt(valueHeight, screenHeight);

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@SuppressLint("NewApi")
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {

				DrawerUpView.LayoutParams listViewParams = (DrawerUpView.LayoutParams) listView
						.getLayoutParams();
				listViewParams.topMargin = (Integer) animation
						.getAnimatedValue();
				listView.setLayoutParams(listViewParams);
				int valueHeight = (Integer) animation.getAnimatedValue();
				if (valueHeight == screenHeight) {
					// 初始化
					Animation alphaAnimation = new AlphaAnimation(1.0f, 0f);
					// 设置动画时间
					alphaAnimation.setDuration(alphaTime);
					alphaAnimation
							.setAnimationListener(new AnimationListener() {

								@Override
								public void onAnimationStart(Animation arg0) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onAnimationRepeat(Animation arg0) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onAnimationEnd(Animation arg0) {
									if (!isUnLock) {
										DrawerUpView.this
												.setVisibility(View.GONE);
										isDrawer = false;
										isshowDrawer = false;

										lock.unlock();
										isUnLock = true;
										isAnimationStop = true;
									}
								}
							});
					startAnimation(alphaAnimation);

				}
			}
		});
		animator.setDuration(animationTime);
		animator.start();
	}

	/**
	 * 抽屉是否显示
	 * 
	 * @return
	 */
	public boolean isShowDrawer() {
		return isshowDrawer;

	}
	/**
	 * 隐藏抽屉
	 * 
	 * @return
	 */
	public void hideDrawer() {
		hideDrawerTime(mAnimationTime, mAlphaTime);
		
	}
	/**
	 * 显示抽屉
	 * 
	 * @return
	 */
	public void showDrawer() {
		showDrawerTime(mAnimationTime, mAlphaTime);
		
	}
	

	private int mAnimationTime = 220;
	private int mAlphaTime = 220;
	private ValueAnimator animator;

	/**
	 * 设置动画抽屉时间
	 * 
	 * @return
	 */
	public void setAnimationTime(int animationTime) {
		mAnimationTime = animationTime;

	}

	/**
	 * 设置渐变时间
	 * 
	 * @return
	 */
	public void setAlphaTime(int alphaTime) {
		mAlphaTime = alphaTime;

	}

	/**
	 * 点击空白是否取消，默认取消
	 * 
	 * @param isDisappear
	 *            true取消，false不取消
	 */
	public void setTouchNoDisappear(boolean isDisappear) {
		disappear = isDisappear;

	}
	public int getTypeHeight(){
		Class c;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			int y = getResources().getDimensionPixelSize(x);
			return y;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}

	/**
	 * 设置没有标题的高度
	 */
	public void setNoTitle() {
		
		
		
		titleHeight = statusBarHeight;
		//TODO
		if(android.os.Build.MANUFACTURER.contains("samsung")){
			screenHeight = MyApplication.baseActivity.getWindowManager()
					.getDefaultDisplay().getHeight()
					- titleHeight-getTypeHeight();
		}else{
			screenHeight = MyApplication.baseActivity.getWindowManager()
					.getDefaultDisplay().getHeight()
					- titleHeight;
		}
		
		
	}

	/**
	 * 设置item高度
	 */
	public void setItemHeight(int height) {
		itemHeight = height;
		mAdapter.notifyDataSetChanged();

	}

	/**
	 * 获取item高度
	 */
	public int getItemHeight() {
		return itemHeight;
	}

	/**
	 * 设置item的点击事件
	 * 
	 * @param listener
	 */
	public void setOnItemClickListener(
			final OnItemClickListener listener) {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				listener.onItemClick(parent, view, position, id);

			}
		});
	}
	

}
