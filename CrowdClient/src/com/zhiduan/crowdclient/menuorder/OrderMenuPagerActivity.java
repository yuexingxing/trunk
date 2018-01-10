package com.zhiduan.crowdclient.menuorder;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.MFragmentPagerAdapter;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.GlobalInstanceStateHelper;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtil.ObjectCallback;
import com.zhiduan.crowdclient.util.Res;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.view.BadgeView;
import com.zhiduan.crowdclient.view.dialog.OrderSortMenuDialog;
import com.zhiduan.crowdclient.view.dialog.OrderSortMenuDialog.ResultCallback;

/** 
 * 订单栏主界面，支持左右滑动切换
 * 
 * @author yxx
 *
 * @date 2016-11-15 下午5:23:12
 * 
 */
public class OrderMenuPagerActivity extends FragmentActivity {

	public Context mContext;

	private ViewPager mViewPager;	//实现Tab滑动效果
	private int currIndex = 0;//当前页卡编号
	private ArrayList<Fragment> fragmentArrayList;//存放Fragment
	private FragmentManager fragmentManager;	//管理Fragment

	//动画图片偏移量
	private int position_one;
	private int position_two;
	private ImageView imgCursor;

	@ViewInject(R.id.layout_order_menu_search) LinearLayout layoutSearch;

	@ViewInject(R.id.tv_order_menu_left) TextView tvLeft;
	@ViewInject(R.id.tv_order_pager_wait) TextView tvWait;
	@ViewInject(R.id.tv_order_pager_sending) TextView tvDis;
	@ViewInject(R.id.tv_order_pager_signed) TextView tvSigned;
	private BadgeView badge;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_menu_pager);

		ViewUtils.inject(this);
		findViewById();
		InitImageView();
		InitFragment();
		InitViewPager();

		setAnimation(currIndex, 0);
		setMenuColor(currIndex);
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {

		mContext = OrderMenuPagerActivity.this;
		return super.onCreateView(name, context, attrs);
	}

	public void onResume(){
		super.onResume();

		OrderUtil.SEARCH_MSG = "";
		OrderUtil.FROM_SEARCH = 0;//这行不能删，否则搜索功能有问题

//		updateAssignNumber();
	}

	private void findViewById() {

		Utils.addActivity(this);

		layoutSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				OrderUtil.FROM_SEARCH = 1;

				Intent intent = new Intent(OrderMenuPagerActivity.this, SearchWaitingActivity.class);
				intent.putExtra("currIndex", currIndex);
				startActivity(intent);
			}
		});

		badge = new BadgeView(mContext, tvLeft);
		tvWait.setOnClickListener(new MyOnClickListener(0));
		tvDis.setOnClickListener(new MyOnClickListener(1));
		tvSigned.setOnClickListener(new MyOnClickListener(2));
	}

	public void onEventMainThread(Message msg) {

		if(msg.what == OrderUtil.UPDATE_ASSIGN_NUMBER){
			updateAssignNumber();
		}
	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// 获取分辨率宽度
		int screenW = dm.widthPixels;
		int bmpW = (screenW/3);

		//动画图片偏移量赋值
		position_one = (int) (screenW / 3.0);
		position_two = position_one * 2;

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_order_menu_bottom_line);
		imgCursor = new ImageView(OrderMenuPagerActivity.this);
		imgCursor.setBackgroundColor(Res.getColor(R.color.main_color));

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(bmpW, 5);
		layoutParams.topMargin = 0;
		layoutParams.leftMargin = 0;
		layoutParams.rightMargin = bmpW;
		layoutParams.bottomMargin = 0;

		layout.addView(imgCursor, layoutParams);
	}

	/**
	 * 初始化页卡内容区
	 */
	@SuppressWarnings("deprecation")
	private void InitViewPager() {

		mViewPager = (ViewPager) findViewById(R.id.vPager);
		mViewPager.setAdapter(new MFragmentPagerAdapter(fragmentManager, fragmentArrayList));

		//让ViewPager缓存2个页面
		mViewPager.setOffscreenPageLimit(3);

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
		fragmentArrayList.add(new FragmentWaitTaking());
		fragmentArrayList.add(new FragmentDistribution());
		fragmentArrayList.add(new FragmentSigned());

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

			setMenuColor(position);
			setAnimation(position, 300);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	};

	/**
	 * 设置动画
	 * @param position
	 * @param durationMillis 动画持续时间毫秒
	 */
	private void setAnimation(int position, long durationMillis){

		Animation animation = null ;

		switch (currIndex){
		case 0:
			if(position == 1){
				animation = new TranslateAnimation(0, position_one, 0, 0);
			}else if(position == 2){//从页卡1跳转转到页卡3
				animation = new TranslateAnimation(0, position_two, 0, 0);
			}
			break;
		case 1:
			if(position == 0){
				animation = new TranslateAnimation(position_one, 0, 0, 0);
			}else if(position == 2){//从页卡1跳转转到页卡3
				animation = new TranslateAnimation(position_one, position_two, 0, 0);
			}
			break;
		case 2:
			if(position == 0){
				animation = new TranslateAnimation(position_two, 0, 0, 0);
			}else if(position == 1){//从页卡1跳转转到页卡3
				animation = new TranslateAnimation(position_two, position_one, 0, 0);
			}
			break;
		default:

			break;
		}

		if(animation != null){
			animation.setFillAfter(true);// true:图片停在动画结束位置
			animation.setDuration(durationMillis);
			imgCursor.startAnimation(animation);
		}

		currIndex = position;
	}

	/**
	 * 更新顶部菜单栏颜色
	 * @param pos
	 */
	private void setMenuColor(int pos){

		tvWait.setTextColor(Res.getColor(R.color.text_ababab));
		tvDis.setTextColor(Res.getColor(R.color.text_ababab));
		tvSigned.setTextColor(Res.getColor(R.color.text_ababab));

		if(pos == 0){
			tvWait.setTextColor(Res.getColor(R.color.main_color));
		}else if(pos == 1){
			tvDis.setTextColor(Res.getColor(R.color.main_color));
		}else if(pos == 2){
			tvSigned.setTextColor(Res.getColor(R.color.main_color));
		}

		updateDataList(pos);
	}

	private void updateDataList(int pos){

		Message msg = new Message();
		if(pos == 0){
			msg.what = OrderUtil.REFRESH_WAIT_DATA;
		}else if(pos == 1){
			msg.what = OrderUtil.REFRESH_DISTRIBUTION_DATA;
		}else if(pos == 2){
			msg.what = OrderUtil.REFRESH_SIGN_DATA;
		}

		MyApplication.getInstance().getEventBus().post(msg);
	}

	/**
	 * 
	 * 按照选择的排序规则
	 * 重新调用接口进行数据刷新
	 * @param v
	 */
	public void orderSort(View view) {

		OrderSortMenuDialog.showDialog(mContext, new ResultCallback() {

			@Override
			public void callback(int position) {

				Message msg = new Message();
				msg.what = OrderUtil.ORDER_MENU_DATA_SORT;
				MyApplication.getInstance().getEventBus().post(msg);
			}
		});
	}

	/**
	 * 全局搜索
	 * @param v
	 */
	public void orderSearch(View v){

		OrderUtil.FROM_SEARCH = 1;

		Intent intent = new Intent(OrderMenuPagerActivity.this, SearchWaitingActivity.class);
		intent.putExtra("currIndex", currIndex);
		startActivity(intent);
	}

	/**
	 * 排序
	 * @param v
	 */
	public void orderType(View v){

		Intent intent = new Intent(OrderMenuPagerActivity.this, FilterOrderActivity.class);
		startActivity(intent);
	}

	/**
	 * 转单请求(进入转单列表)
	 * @param v
	 */
	public void clickLeftMenu(View v){

		Intent intent = new Intent(OrderMenuPagerActivity.this, DeliveryRemindActivity.class);
		startActivity(intent);
	}

	/**
	 * 设置转单数量
	 * @param count
	 */
	private void updateAssignNumber(){

		OrderUtil.assignCount(mContext, new ObjectCallback() {

			@Override
			public void callback(Object object) {
				// TODO Auto-generated method stub

				int count = (Integer) object;
				Message msg = new Message();
				msg.what = 0x0011;
				msg.arg1 = count;
				mHandler.sendMessage(msg);
			}
		});

	}

	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  

		if(requestCode == 0x0011 && resultCode == RESULT_OK){

		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		GlobalInstanceStateHelper.saveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		GlobalInstanceStateHelper.restoreInstanceState(this, savedInstanceState);
	}

	public Handler mHandler = new Handler(){

		public void handleMessage(Message msg){

			if(msg.what == 0x0011){

				int count = msg.arg1;
				badge.setText(count + "");
				badge.setTextSize(12);
				badge.setHeight(CommandTools.dip2px(mContext, 15));
				if(count > 9){
					badge.setWidth(CommandTools.dip2px(mContext, 20));
					tvLeft.setPadding(20, 0, 10, 0);
				}else{
					badge.setWidth(CommandTools.dip2px(mContext, 15));
					tvLeft.setPadding(50, 0, 10, 20);
				}

				badge.setBadgeMargin(0);

				if(count > 0){
					badge.show();
				}else{
					badge.hide();
				}
			}
		}
	};
}
