package com.zhiduan.crowdclient.task;

import java.util.ArrayList;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.MFragmentPagerAdapter;
import com.zhiduan.crowdclient.util.Res;
import com.zhiduan.crowdclient.util.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskMenuPagerActivity extends FragmentActivity {

	private ViewPager mViewPager;	//实现Tab滑动效果
	private int currIndex = 0;//当前页卡编号
	private ArrayList<Fragment> fragmentArrayList;//存放Fragment
	private FragmentManager fragmentManager;	//管理Fragment
	
	//动画图片偏移量
	private int position_one;
	private int position_two;
	private int position_three;
	private ImageView imgCursor;
	
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private TextView tv4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_menu_pager);
		
		findViewById();
		InitImageView();
		InitFragment();
		InitViewPager();
	}

	private void findViewById() {

		Utils.addActivity(this);

		tv1 = (TextView) findViewById(R.id.tv_task_pager_1);
		tv2 = (TextView) findViewById(R.id.tv_task_pager_2);
		tv3 = (TextView) findViewById(R.id.tv_task_pager_3);
		tv4 = (TextView) findViewById(R.id.tv_task_pager_4);

		tv1.setOnClickListener(new MyOnClickListener(0));
		tv2.setOnClickListener(new MyOnClickListener(1));
		tv3.setOnClickListener(new MyOnClickListener(2));
		tv4.setOnClickListener(new MyOnClickListener(3));
	}
	
	/**
	 * 初始化动画
	 */
	private void InitImageView() {

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		imgCursor = (ImageView) findViewById(R.id.img_task_line);

		// 获取分辨率宽度
		int screenW = dm.widthPixels;

		int bmpW = (screenW/4);

		//设置动画图片宽度
		setBmpW(imgCursor, bmpW);

		//动画图片偏移量赋值
		position_one = (int) (screenW / 4.0);
		position_two = position_one * 2;
		position_three = position_one * 3;
	}
	
	/**
	 * 初始化页卡内容区
	 */
	private void InitViewPager() {

		mViewPager = (ViewPager) findViewById(R.id.vPager_task);
		mViewPager.setAdapter(new MFragmentPagerAdapter(fragmentManager, fragmentArrayList));

		//让ViewPager缓存2个页面
		mViewPager.setOffscreenPageLimit(4);

		//设置默认打开第一页
		mViewPager.setCurrentItem(0);

		//设置viewpager页面滑动监听事件
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * 初始化Fragment，并添加到ArrayList中
	 */
	private void InitFragment(){

		fragmentArrayList = new ArrayList<Fragment>();
		fragmentArrayList.add(new TaskUnderWayActivity());
		fragmentArrayList.add(new TaskAuditActivity());
		fragmentArrayList.add(new TaskPaymentActivity());
		fragmentArrayList.add(new TaskCancelActivity());

		fragmentManager = getSupportFragmentManager();
	}
	
	/**
	 * 头标点击监听
	 * @author weizhi
	 * @version 1.0
	 */
	public class MyOnClickListener implements View.OnClickListener{

		private int index = 0 ;
		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mViewPager.setCurrentItem(index);
		}
	}

	/**
	 * 页卡切换监听
	 * @author weizhi
	 * @version 1.0
	 */
	public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

		@Override
		public void onPageSelected(int position) {

			Animation animation = null ;
			switch (position){
			case 0:
				if(currIndex == 1){
					animation = new TranslateAnimation(position_one, 0, 0, 0);
				}else if(currIndex == 2){//从页卡1跳转转到页卡3
					animation = new TranslateAnimation(position_two, 0, 0, 0);
				}else if(currIndex == 3){//从页卡1跳转转到页卡3
					animation = new TranslateAnimation(position_three, 0, 0, 0);
				}
				break;
			case 1:
				if(currIndex == 0){
					animation = new TranslateAnimation(0, position_one, 0, 0);
				}else if(currIndex == 2){//从页卡1跳转转到页卡3
					animation = new TranslateAnimation(position_two, position_one, 0, 0);
				}else if(currIndex == 3){//从页卡1跳转转到页卡3
					animation = new TranslateAnimation(position_three, position_one, 0, 0);
				}
				break;
			case 2:
				if(currIndex == 0){
					animation = new TranslateAnimation(0, position_two, 0, 0);
				}else if(currIndex == 1){//从页卡1跳转转到页卡3
					animation = new TranslateAnimation(position_one, position_two, 0, 0);
				}else if(currIndex == 3){//从页卡1跳转转到页卡3
					animation = new TranslateAnimation(position_three, position_two, 0, 0);
				}
				break;
			case 3:
				if(currIndex == 0){
					animation = new TranslateAnimation(0, position_three, 0, 0);
				}else if(currIndex == 1){//从页卡1跳转转到页卡3
					animation = new TranslateAnimation(position_one, position_three, 0, 0);
				}else if(currIndex == 2){//从页卡1跳转转到页卡3
					animation = new TranslateAnimation(position_two, position_three, 0, 0);
				}
				break;
			default:

				break;
			}

			currIndex = position;

			setMenuColor(position);
			if(animation != null){
				animation.setFillAfter(true);// true:图片停在动画结束位置
				animation.setDuration(300);
				imgCursor.startAnimation(animation);
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	};

	/**
	 * 更新顶部菜单栏颜色
	 * @param pos
	 */
	private void setMenuColor(int pos){

		tv1.setTextColor(Res.getColor(R.color.gray_4));
		tv2.setTextColor(Res.getColor(R.color.gray_4));
		tv3.setTextColor(Res.getColor(R.color.gray_4));
		tv4.setTextColor(Res.getColor(R.color.gray_4));
		
		if(pos == 0){
			tv1.setTextColor(Res.getColor(R.color.main_color));
		}else if(pos == 1){
			tv2.setTextColor(Res.getColor(R.color.main_color));
		}else if(pos == 2){
			tv3.setTextColor(Res.getColor(R.color.main_color));
		}else if(pos == 3){
			tv4.setTextColor(Res.getColor(R.color.main_color));
		}
	}
	
	/**
	 * 设置动画图片宽度
	 * @param mWidth
	 */
	private void setBmpW(ImageView imageView,int mWidth){

		ViewGroup.LayoutParams para;
		para = imageView.getLayoutParams();
		para.width = mWidth;
		imageView.setLayoutParams(para);
	}
	
	/**
	 * 排行榜
	 * @param v
	 */
	public void peak(View v){
		
		Intent intent = new Intent(TaskMenuPagerActivity.this, PeakActivity.class);
		startActivity(intent);
	}
}
