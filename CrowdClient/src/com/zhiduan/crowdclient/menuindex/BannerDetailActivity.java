package com.zhiduan.crowdclient.menuindex;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.menusetting.FeedBackActivity;

/**
 * 
 * <pre>
 * Description	活动详情页
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-3 上午9:33:43
 * </pre>
 */
public class BannerDetailActivity extends BaseActivity {

	private WebView wv_banner;
	private String banner_url;
	private String banner_title;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_banner_detail, this);
		setTitle("活动详情");
		//hidenRightTitle();
		
		if (getIntent().getStringExtra("url") != null) {
			banner_url = getIntent().getStringExtra("url");
			banner_title = getIntent().getStringExtra("title");
		}
		
//		if (getIntent().getStringExtra("type") != null) {
//			setRightTitleText("意见反馈");
//		}
		
		if (banner_title != null && !banner_title.equals("")) {
			setTitle(banner_title);
		}
	}

//	//跳转到意见反馈
//	@Override
//	public void rightClick() {
//		// TODO Auto-generated method stub
//		super.rightClick();
//	}
	
	@Override
	public void initView() {
		wv_banner = (WebView) findViewById(R.id.wv_banner);
		
		WebSettings settings = wv_banner.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
		settings.setSupportZoom(true);//是否可以缩放，默认true
		settings.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
		settings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
		settings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
//		settings.setAppCacheEnabled(true);//是否使用缓存
		
		if (wv_banner != null && !wv_banner.equals("")) {
			wv_banner.loadUrl(banner_url);
		}
		
		wv_banner.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		wv_banner.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
	}

	@Override
	public void initData() {

	}
}
