package com.zhiduan.crowdclient.menuorder;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.MFragmentPagerAdapter;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtil;
import de.greenrobot.event.EventBus;

/**
 * 订单搜索
 * 
 * @author yxx
 * 
 * @date 2016-5-24 下午5:27:11
 * 
 */
public class SearchWaitingActivity extends FragmentActivity {

	private int currIndex;
	public static SearchWaitingActivity mActivity;
	private ViewPager mViewPager;	//实现Tab滑动效果
	private ArrayList<Fragment> fragmentArrayList;//存放Fragment
	private FragmentManager fragmentManager;	//管理Fragment

	private EditText edtSearch;
	private EventBus mEventBus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_waiting);

		findViewById();
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {

		mActivity = SearchWaitingActivity.this;
		return super.onCreateView(name, context, attrs);
	}

	private void findViewById() {

		if(mEventBus == null){
			mEventBus = EventBus.getDefault();
			mEventBus.register(this);
		}

		edtSearch = (EditText) findViewById(R.id.edt_order_search_waiting);
		edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {

				if (arg1 == EditorInfo.IME_ACTION_SEARCH){

					onSearch(null);
					CommandTools.hidenKeyboars(mActivity, edtSearch);
					return true;
				}

				return false;
			}
		});

		setImmerseLayout();
		InitFragment();
		InitViewPager();
	}

	/**
	 * 初始化页卡内容区
	 */
	private void InitViewPager() {

		mViewPager = (ViewPager) findViewById(R.id.vPager_search);
		mViewPager.setAdapter(new MFragmentPagerAdapter(fragmentManager, fragmentArrayList));

		//让ViewPager缓存2个页面
		mViewPager.setOffscreenPageLimit(1);

		//设置默认打开第一页
		mViewPager.setCurrentItem(0);
	}

	/**
	 * 初始化Fragment，并添加到ArrayList中
	 */
	private void InitFragment(){

		fragmentArrayList = new ArrayList<Fragment>();

		currIndex = getIntent().getIntExtra("currIndex", 0);
		if(currIndex == 0){
			fragmentArrayList.add(new FragmentWaitTaking());
		}else if(currIndex == 1){
			fragmentArrayList.add(new FragmentDistribution());
		}else if(currIndex == 2){
			fragmentArrayList.add(new FragmentSigned());
		}

		fragmentManager = getSupportFragmentManager();
	}

	/**
	 * 开始搜索
	 * @param v
	 */
	public void onSearch(View v){

		OrderUtil.SEARCH_MSG = edtSearch.getText().toString();

		Message msg = new Message();
		if(currIndex == 0){
			msg.what = OrderUtil.REFRESH_WAIT_DATA;
		}else if(currIndex == 1){
			msg.what = OrderUtil.REFRESH_DISTRIBUTION_DATA;
		}else if(currIndex == 2){
			msg.what = OrderUtil.REFRESH_SIGN_DATA;
		}

		mEventBus.post(msg);
	}

	public void back(View v) {

		finish();
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

	}

	public void onEventMainThread(Message msg) {

	}

	public static Activity getActivity(){

		return mActivity;
	}
}
