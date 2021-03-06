package com.zhiduan.crowdclient.activity;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.view.SwipeBackLayout;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Window;

/** 
 * 这个类是Fragment使用的，activity用另一个
 * 想要实现向右滑动删除Fragment效果只需要继承SwipeBackFragmentActivity即可，如果当前页面含有ViewPager
 * 只需要调用SwipeBackLayout的setViewPager()方法即可
 * 
 * @author 
 *
 * @date 2016-12-15 上午10:42:52
 * 
 */
public class SwipeBackFragmentActivity extends FragmentActivity {
	
	protected SwipeBackLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
				R.layout.activity_swipe_back, null);
		layout.attachToActivity(this);
	}
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
	}

	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}


}
