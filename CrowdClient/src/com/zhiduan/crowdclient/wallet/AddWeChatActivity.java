package com.zhiduan.crowdclient.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;

/**
 * 添加微信
 * 
 * @author wfq
 * 
 */
public class AddWeChatActivity extends BaseActivity {

	private EditText mEtName;
	private EditText mEtUser;
	private Button mBtOk;
	private WebView mWv;
	private WebView mWebView;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
//		 setContentViewId(R.layout.activity_wechat, this);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void initView() {
		try {
			mWebView = new WebView(this);
			setContentViewId(mWebView, this);
			mWebView.requestFocus();

			mWebView.setWebChromeClient(new WebChromeClient() {
				@Override
				public void onProgressChanged(WebView view, int progress) {
				}
			});

			mWebView.setOnKeyListener(new View.OnKeyListener() { // webview can
																	// go back
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK
							&& mWebView.canGoBack()) {
						mWebView.goBack();
						return true;
					}
					return false;
				}
			});
			mWebView.setWebViewClient(new WebViewClient(){
		         @Override
		         public boolean shouldOverrideUrlLoading(WebView view, String url) {
		        	 if(!Constant.WEIXIN.equals(url)){
		        		 return true;
		        	 }
		          view.loadUrl(url);   //在当前的webview中跳转到新的url
		 
		          return true;
		         }
		        });
			WebSettings webSettings = mWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setDefaultTextEncodingName("utf-8");

			mWebView.addJavascriptInterface(getHtmlObject(), "jsObj");
			mWebView.loadUrl(Constant.WEIXIN);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initData() {
		setTitle("添加微信");
	}

	private Object getHtmlObject() {
		Object insertObj = new Object() {
			@JavascriptInterface
			public String HtmlcallJava() {
				if (com.zhiduan.crowdclient.util.Util
						.isWeixinAvilible(mContext)) {
					Intent intent = new Intent();
					PackageManager packageManager = getPackageManager();
					intent = packageManager
							.getLaunchIntentForPackage("com.tencent.mm");
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
//							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				} else {
					CommandTools.showToast("您的手机暂无微信app");
				}

				return "Html call Java";
			}

			public String HtmlcallJava2(final String param) {
				return "Html call Java : " + param;
			}

			public void JavacallHtml() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
					}
				});
			}

			public void JavacallHtml2() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
					}
				});
			}
		};

		return insertObj;
	}

}
