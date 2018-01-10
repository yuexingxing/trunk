package com.zhiduan.crowdclient.menucenter;

import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
/**
 * 关于我们
 * @author wfq
 *
 */
public class AboutWeActivity extends BaseActivity {

	private WebView webView;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		webView = new WebView(mContext);

		setContentViewId(webView, this);
	}

	@Override
	public void initView() {
		
		setTitle("关于");
		webView.loadUrl(Constant.ABOUT_AXP + CommandTools.getVersionName(mContext));

		//启用支持javascript
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

}
