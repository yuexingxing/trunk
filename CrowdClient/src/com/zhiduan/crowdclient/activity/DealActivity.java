package com.zhiduan.crowdclient.activity;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Utils;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.app.Activity;
import android.content.Intent;

/**
 * 用户协议
 * 
 * @author yxx
 * 
 * @date 2016-4-26 下午12:06:55
 * 
 */
public class DealActivity extends BaseActivity {

	private WebView webView;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_deal, DealActivity.this);
		setTitle("用户协议");
	}

	@Override
	public void initView() {
		if (Utils.getNetWorkState()==false) {
			CommandTools.showToast("网络连接失败,请稍后再试");
			return;
		}
		webView = (WebView) findViewById(R.id.webView_deal);
		WebSettings webSettings = webView.getSettings();
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// 找到Html文件，也可以用网络上的文件
		webView.loadUrl("http://pda.shzhiduan.com:15007/爱学派众包用户协议.html");

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	/**
	 * 同意协议
	 * 
	 * @param v
	 */
	public void nextStep(View v) {

		Intent intent = new Intent(DealActivity.this, RegisterActivity.class);
		intent.putExtra("flag", "1");
		setResult(RESULT_OK, intent);
		DealActivity.this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {// 获取 back键

			back(null);
		}

		return false;
	}

}
