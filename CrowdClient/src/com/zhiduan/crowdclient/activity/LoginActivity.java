package com.zhiduan.crowdclient.activity;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.Log;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.UserService;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.LoginUtil;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.view.CustomProgress;

public class LoginActivity extends Activity implements OnClickListener {

	private final String mPageName = "EditSmsActivity";
	private EditText mUserName;
	private EditText mUserPwd;
	private ImageView imgPsd;
	private LinearLayout ll_login_img;
	private TextView mQuickLoginBtn;
	private ImageView mWxLoginBtn;
	private ImageView mQqLoginBtn;
	private ImageView mWbLoginBtn;

	// 对话框
	private Dialog dialog;
	private android.app.AlertDialog ad;
	private TextView titleView;
	private TextView tv_dialog_single_ok;
	private LinearLayout iv_dialog_delete;
	private EditText edt_switch_pwd;
	private ImageView iv_prepare, iv_test, iv_official;
	private LinearLayout ll_test, ll_official, ll_prepare;
	private PopupWindow inputWindow;
	private String Server_pwd;
	private int server_status = 1;
	private Context mContext;
	private TextView mRegister;

	private LoadTextNetTask mTaskRequestIsbind;
	private LoadTextNetTask mTaskRequestGaveLogin;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			inputWindow = new PopupWindow(mContext);
			View view = View.inflate(mContext, R.layout.dialog_inputpwd, null);
			// 删除图标
			iv_dialog_delete = (LinearLayout) view.findViewById(R.id.iv_dialog_delete);
			tv_dialog_single_ok = (TextView) view.findViewById(R.id.tv_dialog_single_ok);
			edt_switch_pwd = (EditText) view.findViewById(R.id.edt_switch_pwd);
			iv_official = (ImageView) view.findViewById(R.id.iv_official);
			iv_test = (ImageView) view.findViewById(R.id.iv_test);
			iv_prepare = (ImageView) view.findViewById(R.id.iv_prepare);
			ll_official = (LinearLayout) view.findViewById(R.id.ll_official);
			ll_test = (LinearLayout) view.findViewById(R.id.ll_test);
			ll_prepare = (LinearLayout) view.findViewById(R.id.ll_prepare);

			iv_dialog_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					inputWindow.dismiss();

				}
			});
			tv_dialog_single_ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Server_pwd = edt_switch_pwd.getText().toString();
					if (Server_pwd.equals("88886188")) {
						inputWindow.dismiss();
						ShowSwitch();
					} else {
						CommandTools.showToast("口令错误");
					}

				}
			});
			inputWindow.setContentView(view);
			inputWindow.setWidth(CommandTools.dip2px(mContext, 300));
			inputWindow.setHeight(CommandTools.dip2px(mContext, 200));

			//设置弹出窗体可点击
			inputWindow.setFocusable(true);
			inputWindow.setBackgroundDrawable(null);
			//			inputWindow.showAtLocation(tvVersion, Gravity.CENTER, 0, 0);
		};
	};

	/**
	 * 弹出切换服务器对话框
	 */
	public void ShowSwitch() {
		dialog = new Dialog(mContext, R.style.dialog_no_border);
		View view = View.inflate(mContext, R.layout.dialog_switch_server, null);
		// 删除图标
		iv_dialog_delete = (LinearLayout) view.findViewById(R.id.iv_dialog_delete);
		tv_dialog_single_ok = (TextView) view.findViewById(R.id.tv_dialog_single_ok);
		iv_official = (ImageView) view.findViewById(R.id.iv_official);
		iv_test = (ImageView) view.findViewById(R.id.iv_test);
		iv_prepare = (ImageView) view.findViewById(R.id.iv_prepare);
		ll_official = (LinearLayout) view.findViewById(R.id.ll_official);
		ll_test = (LinearLayout) view.findViewById(R.id.ll_test);
		ll_prepare = (LinearLayout) view.findViewById(R.id.ll_prepare);
		// 正式版
		ll_official.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				server_status = 3;
				iv_test.setImageResource(R.drawable.msg_status);
				iv_official.setImageResource(R.drawable.msg_status_select);
				iv_prepare.setImageResource(R.drawable.msg_status);

			}
		});
		// 预发布版
		ll_prepare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				server_status = 2;
				iv_test.setImageResource(R.drawable.msg_status);
				iv_official.setImageResource(R.drawable.msg_status);
				iv_prepare.setImageResource(R.drawable.msg_status_select);

			}
		});
		// 测试库
		ll_test.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				server_status = 1;
				iv_test.setImageResource(R.drawable.msg_status_select);
				iv_official.setImageResource(R.drawable.msg_status);
				iv_prepare.setImageResource(R.drawable.msg_status);

			}
		});
		iv_dialog_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();

			}
		});
		tv_dialog_single_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CommandTools.showToast("切换成功");
				dialog.dismiss();
				Clear_data(server_status);

			}
		});

		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		Window window = dialog.getWindow();
		window.getAttributes().width = CommandTools.dip2px(mContext, 300);
		window.getAttributes().height = CommandTools.dip2px(mContext, 220);
		dialog.show();
	}

	/**
	 * 切换服务器地址
	 * 
	 * @param server_status
	 */
	public void Clear_data(int server_status) {

		switch (server_status) {

		case 1:// 测试版

			Constant.TagName = Constant.DEV_TagName;
			Constant.FormalURL = Constant.DEV_URL;
			Constant.UPDATEURL = Constant.DEV_UPDATEURL;
			Constant.appKey = Constant.DEV_AppKey;
			break;
		case 2:// 预发布版

			Constant.TagName = Constant.PRE_TagName;
			Constant.FormalURL = Constant.PRE_URL;
			Constant.UPDATEURL = Constant.PRE_UPDATEURL;
			Constant.appKey = Constant.PRE_AppKey;
			break;
		case 3:// 正式版本

			Constant.TagName = Constant.FORMAL_TagName;
			Constant.FormalURL = Constant.FORMAL_URL;
			Constant.UPDATEURL = Constant.FORMAL_UPDATEURL;
			Constant.appKey = Constant.FORMAL_AppKey;
			break;
		default:
			break;
		}

		SharedPreferencesUtils.setParam(Constant.SP_TagName, Constant.TagName);
		SharedPreferencesUtils.setParam(Constant.SP_FormalURL, Constant.FormalURL);
		SharedPreferencesUtils.setParam(Constant.SP_UPDATEURL, Constant.UPDATEURL);
		SharedPreferencesUtils.setParam(Constant.SP_AppKey, Constant.appKey);

		SharedPreferencesUtils.setParam(Constant.SP_LOGIN_PSD, "");
		go_login();
	}

	/**
	 * 跳转到登录界面 重新登录
	 */
	public void go_login() {
		Intent intent = new Intent(mContext, LoginActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		mContext = this;

		Utils.addActivity(this);
		setImmerseLayout();

		initView();
		initData();
	}

	public void initView() {
		mUserName = (EditText) findViewById(R.id.user_name);
		mUserPwd = (EditText) findViewById(R.id.user_pwd);
		ll_login_img=(LinearLayout) findViewById(R.id.ll_login_img);
		imgPsd = (ImageView) findViewById(R.id.imb_password_login);
		mRegister = (TextView) findViewById(R.id.register_login);
		//		iv_login_delete = (ImageView) findViewById(R.id.iv_login_delete);
		mQuickLoginBtn = (TextView) findViewById(R.id.quick_login_txt);
		mWxLoginBtn = (ImageView) findViewById(R.id.wx_login_txt);
		mQqLoginBtn = (ImageView) findViewById(R.id.qq_login_txt);
		mWbLoginBtn = (ImageView) findViewById(R.id.wb_login_txt);

		Button mLoginBtn = (Button) findViewById(R.id.login_btn);
		mLoginBtn.setOnClickListener(this);
		mRegister.setOnClickListener(this);
		mWxLoginBtn.setOnClickListener(this);
		mQqLoginBtn.setOnClickListener(this);
		mWbLoginBtn.setOnClickListener(this);
		mQuickLoginBtn.setOnClickListener(this);
		//		iv_login_delete.setOnClickListener(this);
		mUserName.setText(SharedPreferencesUtils.getParam(Constant.SP_LOGIN_NAME, "").toString());
		mUserPwd.setText(SharedPreferencesUtils.getParam(Constant.SP_LOGIN_PSD, "").toString());

		mUserName.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				ll_login_img.setVisibility(View.GONE);
				return false;
			}
		});
		mUserPwd.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				ll_login_img.setVisibility(View.GONE);
				return false;
			}
		});
	}
	/**
	 * 隐藏软键盘
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {

			// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
			View v = getCurrentFocus();

			if (CommandTools.isShouldHideInput(v, ev)) {
				InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				CommandTools.hideSoftInput(v.getWindowToken(), im);
				ll_login_img.setVisibility(View.VISIBLE);
			}else {
				ll_login_img.setVisibility(View.GONE);
			}
		}
		return super.dispatchTouchEvent(ev);
	}
	protected void setImmerseLayout() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	public void initData() {
		// 长按切换服务器
		//		tvVersion.setOnLongClickListener(new OnLongClickListener() {
		//			@Override
		//			public boolean onLongClick(View arg0) {
		////				new Thread(new Mythread()).start();
		//				return false;
		//			}
		//		});
	}

	// 定时器
	public class Mythread implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(4000);
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		// 友盟统计
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(this);

	}

	@Override
	protected void onStart() {
		super.onStart();
		// 登录成功之后，清除UI上输入的密码:
		// imgPsd = (ImageView) findViewById(R.id.imb_password_login);
		// mUserPwd.setText("");

		// 友盟统计
		MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btn:
			// 友盟统计
			MobclickAgent.onEvent(LoginActivity.this, "login_btn");

			// 登陆按钮
			String LoginName = mUserName.getText().toString();
			String LoginPwd = mUserPwd.getText().toString();

//			if (LoginName.equals("") || LoginPwd.equals("")) {
//				Toast.makeText(LoginActivity.this, "账号或者密码不能为空", 1).show();
//				return;
//			}
//
//			if (LoginPwd.length() < 6) {
//				Toast.makeText(LoginActivity.this, "密码不能小于6位", 1).show();
//				return;
//			}
//
//			if (!CommandTools.isMobileNO(LoginName)) {
//				Toast.makeText(LoginActivity.this, "请输入合法的手机号", 1).show();
//				return;
//			}

			// MyAsyncTask tsk = new MyAsyncTask();
			// tsk.execute("");

			/*
			 * String loginName = (String) SharedPreferencesUtils.getParam(
			 * Constant.SP_LOGIN_NAME, LoginName); String loginPwd = (String)
			 * SharedPreferencesUtils.getParam( Constant.SP_LOGIN_PSD,
			 * LoginPwd);
			 */

//			LoginUtil.login(LoginActivity.this, LoginName, LoginPwd, null, false);// 快速登陆

			 Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			 startActivity(intent);
			 finish();
			break;
			//		case R.id.iv_login_delete:
			//			finish();
			//			break;
		case R.id.register_login:
			// 友盟统计
			MobclickAgent.onEvent(LoginActivity.this, "register_login");

			Intent intentRegister = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(intentRegister);
			break;
		case R.id.wx_login_txt:
			SHARE_MEDIA weixin = SHARE_MEDIA.WEIXIN;
			SnsPlatform snsPlatform = weixin.toSnsPlatform();

			UMShareAPI.get(LoginActivity.this).doOauthVerify(LoginActivity.this, snsPlatform.mPlatform, authListener);
			break;
		case R.id.qq_login_txt:
			SHARE_MEDIA qq = SHARE_MEDIA.QQ;
			SnsPlatform qqSnsPlatform = qq.toSnsPlatform();

			UMShareAPI.get(LoginActivity.this).doOauthVerify(LoginActivity.this, qqSnsPlatform.mPlatform, authListener);
			break;
		case R.id.wb_login_txt:
			SHARE_MEDIA sina = SHARE_MEDIA.SINA;
			SnsPlatform platform = sina.toSnsPlatform();

			UMShareAPI.get(LoginActivity.this).doOauthVerify(LoginActivity.this, platform.mPlatform, authListener);
			break;
		case R.id.quick_login_txt:
			intent = new Intent(LoginActivity.this, QuickLoginActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	UMAuthListener authListener = new UMAuthListener() {
		@Override
		public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
			String source = "";
			String uid = "";
			Log.v("zd", data.toString());
			
			if (platform == SHARE_MEDIA.QQ) {
				uid = data.get("uid");
				source = "qq";
			} else if (platform == SHARE_MEDIA.WEIXIN) {
				uid = data.get("openid");
				source = "weixin";
			} else if (platform == SHARE_MEDIA.SINA) {
				uid = data.get("uid");
				source = "sina";
			}

			mTaskRequestIsbind = requestIsbind(source, uid);
		}

		@Override
		public void onError(SHARE_MEDIA platform, int action, Throwable t) {
			Log.i("hexiuhui---", action + "失败："+t.getMessage());
			Toast.makeText(LoginActivity.this, "失败："+t.getMessage(),Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform, int action) {
			Toast.makeText(LoginActivity.this, "取消了",Toast.LENGTH_LONG).show();

		}
	};

	//是否绑定手机
	protected LoadTextNetTask requestIsbind(final String source, final String thirdpartyToken) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestIsbind = null;
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONObject jsonObject = jsonObj.getJSONObject("data");
							int rt = jsonObject.getInt("result");
							//如果没有绑定手机就去绑定
							if (rt == 0) {
								Intent intent = new Intent(LoginActivity.this, BindPhoneActivity.class);
								intent.putExtra("source", source);
								intent.putExtra("thirdpartyToken", thirdpartyToken);
								startActivity(intent);
							} else {  //已经绑定手机直接登录
								// 获取个推clientId
								String clientId = MyApplication.getInstance().m_strPushMessageClientId;
								if (clientId.isEmpty()) {
									clientId = PushManager.getInstance().getClientid(LoginActivity.this);
									if (clientId != null && !clientId.isEmpty()) {
										MyApplication.getInstance().m_strPushMessageClientId = clientId;
									}
								}

								String imei = CommandTools.getMIME(LoginActivity.this);
								mTaskRequestGaveLogin = requestGeveLogin(clientId, imei, source, thirdpartyToken);
							}
						} else {
							String message = jsonObj.getString("message");

							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(LoginActivity.this);
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(LoginActivity.this, "获取数据中...", false, null);
		LoadTextNetTask task = UserService.isBind(source, thirdpartyToken, listener, null);
		return task;
	}

	//第三方登录
	protected LoadTextNetTask requestGeveLogin(String clientId, String imei, final String source, final String thirdpartyToken) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestGaveLogin = null;
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							CommandTools.showToast("登录成功");
							MyApplication myApplication = MyApplication.getInstance();

							JSONObject jsonObject = jsonObj.optJSONObject("data");
							myApplication.m_userInfo.toKen = jsonObject.optString("token");

							SharedPreferencesUtils.setParam(Constant.SP_LOGIN_TOKEN, jsonObject.optString("token"));

							LoginUtil.getPersonalMessage();
						} else {
							String message = jsonObj.getString("message");

							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(LoginActivity.this);
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(LoginActivity.this, "获取数据中...", false, null);
		LoadTextNetTask task = UserService.geveLogin(clientId, imei, source, thirdpartyToken, listener, null);
		return task;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 找回密码
	 * 
	 * @param v
	 */
	public void findPSD(View v) {
		Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
		startActivity(intent);
	}

	/**
	 * 密码的显示与隐藏
	 * 
	 * @param v
	 */
	private boolean isShow = false;

	public void showPwd(View v) {
		if (isShow == true) {
			isShow = false;
			mUserPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
			imgPsd.setImageResource(R.drawable.img_psd_close);
		} else {
			isShow = true;
			mUserPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
			imgPsd.setImageResource(R.drawable.img_psd_open);
		}
		mUserPwd.setSelection(mUserPwd.getText().length());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mTaskRequestGaveLogin != null) {
			mTaskRequestGaveLogin.cancel(true);
			mTaskRequestGaveLogin = null;
		} 

		if (mTaskRequestIsbind != null) {
			mTaskRequestIsbind.cancel(true);
			mTaskRequestIsbind = null;
		}
	}

	int keyBackClickCount = 0;
	//	private ImageView iv_login_delete;

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyBackClickCount++) {
			case 0:
				CommandTools.showToast("再按一次退出程序");
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						keyBackClickCount = 0;
					}
				}, 2000);
				break;
			case 1:
				exitApp();
				break;
			default:
				break;
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 退出程序，调用退出接口
	 */
	private void exitApp() {
		
		Utils.finishAllActivities();
//		LoginActivity.this.finish();
	}

}
