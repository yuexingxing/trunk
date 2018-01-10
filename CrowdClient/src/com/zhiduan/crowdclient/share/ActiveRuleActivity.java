package com.zhiduan.crowdclient.share;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.R.id;
import com.zhiduan.crowdclient.R.layout;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.util.Constant;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;
/**
 * 
 * <pre>
 * Description	活动规则
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-12-7 下午1:52:14  
 * </pre>
 */
public class ActiveRuleActivity extends BaseActivity {

	private WebView wb_active_rule;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_active_rule, this);
		setTitle("活动规则");
	}

	@Override
	public void initView() {
		//不要设置缓存，设置缓存会导致刷新不及时
	wb_active_rule=(WebView) findViewById(R.id.wb_active_rule);
		
	WebSettings settings = wb_active_rule.getSettings();
	settings.setJavaScriptEnabled(true);
//	settings.setDomStorageEnabled(true);
	settings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
	settings.setSupportZoom(true);//是否可以缩放，默认true
	settings.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
	settings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
	settings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
	}

	/* (non-Javadoc)
	 * @see com.zhiduan.crowdclient.activity.BaseActivity#initData()
	 */
	@Override
	public void initData() {
		wb_active_rule.loadUrl(Constant.RULE_URL);
		
	}

}
