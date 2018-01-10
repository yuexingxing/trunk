package com.zhiduan.crowdclient.menuindex;

import java.util.ArrayList;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.adapter.MFragmentPagerAdapter;
import com.zhiduan.crowdclient.util.Res;
import com.zhiduan.crowdclient.util.ScreenUtil;
import com.zhiduan.crowdclient.util.Utils;
import de.greenrobot.event.EventBus;

/** 
 * 任务栏菜单
 * 
 * @author yxx
 *
 * @date 2016-11-24 上午9:38:13
 * 
 */
public class TaskOrderMenuActivity extends FragmentActivity {

	private TextView tvTask1;
	private TextView tvTask2;

	private ViewPager mViewPager;	//实现Tab滑动效果
	private int currIndex = 0;//当前页卡编号
	private ArrayList<Fragment> fragmentArrayList;//存放Fragment
	private FragmentManager fragmentManager;	//管理Fragment

	//动画图片偏移量
	private int position_one;
	private ImageView imgCursor;

	private EventBus mEventBus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_task_menu);

		if (getIntent() != null) {
			currIndex = getIntent().getIntExtra("currIndex", 0);
		}
		
		findViewById();
		InitImageView();
		InitFragment();
		InitViewPager();
		setImmerseLayout();
		
		setAnimation(currIndex, 0);
		setMenuColor(currIndex);
	}
	
	protected void setImmerseLayout() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	public void onResume(){
		super.onResume();

		if(mEventBus == null){
			mEventBus = EventBus.getDefault();
			mEventBus.register(this);
		}

	}

	private void findViewById() {

		Utils.addActivity(this);

		((TextView) findViewById(R.id.layout_top_menu_center)).setText("赏金猎人");
		((ImageView) findViewById(R.id.layout_top_menu_left)).setBackgroundResource(R.drawable.back);

		tvTask1 = (TextView) findViewById(R.id.tv_task_pager_1);
		tvTask2 = (TextView) findViewById(R.id.tv_task_pager_2);

		tvTask1.setOnClickListener(new MyOnClickListener(0));
		tvTask2.setOnClickListener(new MyOnClickListener(1));
	}

	public void onEventMainThread(Message msg) {

	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// 获取分辨率宽度
		int screenW = dm.widthPixels;

		int bmpW = (screenW/5);//

		//动画图片偏移量赋值
		position_one = (int) (screenW / 5.0) * 2;

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_order_menu_bottom_line);
		imgCursor = new ImageView(TaskOrderMenuActivity.this);
		imgCursor.setBackgroundColor(Res.getColor(R.color.main_color));

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(bmpW, 3);
		layoutParams.topMargin = 0;
		layoutParams.leftMargin = bmpW;
		layoutParams.rightMargin = 0;
		layoutParams.bottomMargin = 0;

		layout.addView(imgCursor, layoutParams);
	}

	/**
	 * 初始化页卡内容区
	 */
	private void InitViewPager() {

		mViewPager = (ViewPager) findViewById(R.id.vPager_taskmenu);
		mViewPager.setAdapter(new MFragmentPagerAdapter(fragmentManager, fragmentArrayList));

		//让ViewPager缓存2个页面
		mViewPager.setOffscreenPageLimit(2);

		//设置默认打开第一页
		mViewPager.setCurrentItem(currIndex);

		//设置viewpager页面滑动监听事件
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * 初始化Fragment，并添加到ArrayList中
	 */
	private void InitFragment(){

		fragmentArrayList = new ArrayList<Fragment>();
		fragmentArrayList.add(new TaskOrderMenuSecond());
		fragmentArrayList.add(new TaskOrderMenuFisrt());

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

			setAnimation(position, 300);
			setMenuColor(position);
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
	private void setMenuColor(int position){

		tvTask1.setTextColor(Res.getColor(R.color.gray_4));
		tvTask2.setTextColor(Res.getColor(R.color.gray_4));

		if(position == 0){
			tvTask1.setTextColor(Res.getColor(R.color.main_color));
		}else if(position == 1){
			tvTask2.setTextColor(Res.getColor(R.color.main_color));
		}
	}

	/**
	 * 设置动画
	 * @param position
	 * @param durationMillis 动画持续时间毫秒
	 */
	private void setAnimation(int position, long durationMillis){

		Animation animation = null ;

		if(position == 0){
			animation = new TranslateAnimation(position_one, 0, 0, 0);
		}else  if(position == 1){
			animation = new TranslateAnimation(0, position_one, 0, 0);
		}

		if(animation != null){
			animation.setFillAfter(true);// true:图片停在动画结束位置
			animation.setDuration(durationMillis);
			imgCursor.startAnimation(animation);
		}
	}

	/**
	 * 左侧标题点击事件
	 * @param v
	 */
	public void onLeftClick(View v){

		finish();
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
}
