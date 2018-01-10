package com.zhiduan.crowdclient.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.view.ZoomOutPageTransformer;
/**
 * <pre>
 * Description  引导页 界面
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-5-25 下午8:50:17  
 * </pre>
 */
public class GuideActivity extends Activity implements OnPageChangeListener, OnClickListener {
	private ViewPager mViewPager;
	private int[] mImgIds = new int[] { R.drawable.splash_1, R.drawable.splash_2, R.drawable.splash_3 ,R.drawable.splash_4};
	private List<ImageView> mImageViews = new ArrayList<ImageView>();
	private ImageView mButtonRegister;
	private RelativeLayout mLl;
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide);

		initData();

		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		mLl = (RelativeLayout) findViewById(R.id.LinearLayout_login_and_register);
		mButtonRegister = (ImageView) findViewById(R.id.button_register);

		mButtonRegister.setOnClickListener(this);
		mViewPager.setAdapter(new PagerAdapter() {
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(mImageViews.get(position));
				return mImageViews.get(position);
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {

				container.removeView(mImageViews.get(position));
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}

			@Override
			public int getCount() {
				return mImgIds.length;
			}
		});

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MyApplication.baseActivity=this;
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	private void initData() {
		for (int imgId : mImgIds) {
			ImageView imageView = new ImageView(getApplicationContext());
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setImageResource(imgId);
			mImageViews.add(imageView);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

		if(arg0 != 3){
			mLl.setVisibility(View.INVISIBLE);
		}else{
			mLl.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onPageSelected(int arg0) {
		
		Log.i("TAG", "arg0 = " + arg0);
		if (arg0 == 3) {
			mLl.setVisibility(View.VISIBLE);
		} else {
			mLl.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_register:
			//注册
			intent = new Intent(GuideActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}

	}
}
