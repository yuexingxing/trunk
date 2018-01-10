package com.zhiduan.crowdclient.menucenter;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.menuindex.BannerDetailActivity;
import com.zhiduan.crowdclient.menuindex.TaskOrderMenuActivity;
import com.zhiduan.crowdclient.menusetting.FeedBackActivity;
import com.zhiduan.crowdclient.message.MessageActivity;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.SettingsService;
import com.zhiduan.crowdclient.share.InviteActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.GlobalInstanceStateHelper;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;
import com.zhiduan.crowdclient.wallet.MyWalletActivity;
import com.zhiduan.crowdclient.zxing.view.CircularImage;
import com.zhiduan.crowdclient.zxing.view.WaveView;

/**
 * 
 * <pre>
 * Description	我的 界面
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2017-1-3 上午10:44:32
 * </pre>
 */
public class MyActivity extends Activity implements OnClickListener {

	private TextView center_tv_name;
	private Button center_tv_level;
	private TextView center_tv_phone;
	private TextView center_tv_campus;
//	private XCRoundRectImageView iv_user_icon;
	private CircularImage iv_user_icon;
	private RelativeLayout rl_my_pocket;
	private RelativeLayout rl_experience;
	private RelativeLayout rl_money_hunter;
	private RelativeLayout rl_user_info;
	private LinearLayout rl_setting;
	private LinearLayout rl_contact_us;
	private ImageView iv_switch_status;
	private MyApplication myapp = MyApplication.getInstance();
	private RelativeLayout  center_rl_my_comment,
			center_rl_news_guide;
	private RelativeLayout center_rl_center_feedback;
	private RelativeLayout center_rl_about_mine;
	private RelativeLayout center_rl_consumer_hotline;
	private RelativeLayout center_rl_invite_friend;
	private final String mPageName = "MyActivity";
	private String mWorkState;
	private String collegeName;// 学校名称
	private int expPoint;// 经验值
	private String levelDesc;
	private int levelCode;//1:新生小派 2:黄金小派 3:铂金小派 4:钻石小派
	private LoadTextNetTask mTaskSetWorkState;
	private boolean m_bIsWorking = false;
	private int officeRoleType;// 职位类型 1-普通小派 2-coo 3-ceo
	private String expPointRule;// H5静态页面URL
	private String user_office;//是否是楼长
	private TextView tv_my_points;
	private WaveView waveview;
	private ImageView iv_user_sex;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String type = "普通小派";
			switch (officeRoleType) {
			case 1:
				type = "";
				break;
			case 2:
				type = "COO";
				break;
			case 3:
				type = "CEO";
				break;
			default:
				type = "";
				break;
			}
			tv_my_points.setText(expPoint+ "/"+"Lv"+levelCode  );
			center_tv_campus.setText(collegeName);
			if (TextUtils.isEmpty(type)&&TextUtils.isEmpty(user_office)) {
				center_tv_name.setText(myapp.m_userInfo.m_nick_name);
			} else if (!TextUtils.isEmpty(type)&&!TextUtils.isEmpty(user_office)){
				
				center_tv_name.setText(myapp.m_userInfo.m_nick_name+"("+type +"/"+user_office+ ")");
			}else if (TextUtils.isEmpty(type)&&!TextUtils.isEmpty(user_office)) {
				center_tv_name.setText(myapp.m_userInfo.m_nick_name+"("+user_office+ ")");
			}else if (!TextUtils.isEmpty(type)&&TextUtils.isEmpty(user_office)) {
				center_tv_name.setText(myapp.m_userInfo.m_nick_name+"("+type + ")");
			}
			center_tv_level.setText("LV"+levelCode);
			center_tv_level.setVisibility(View.VISIBLE);
			
//			center_tv_empirical.setText(expPoint+"("+levelDesc + ")" );

		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);
		if (savedInstanceState != null) {
			// 因为用到了myapplication里的全局变量，被系统回收后要恢复
			GlobalInstanceStateHelper.restoreInstanceState(this, savedInstanceState);
		}

		Utils.addActivity(this);
		initViews();

		setClickListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyApplication.baseActivity = this;
		getPersonalMessage();
		load_img(myapp.m_userInfo.m_strUserHeadPic);
		center_tv_name.setText(myapp.m_userInfo.m_nick_name);
		center_tv_phone.setText(myapp.m_userInfo.m_strUserPhone);
		if (waveview!=null) {
			waveview.start();
		}
		

		

		// 友盟统计
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(this);
	}
	/**
	 * 加载图片
	 * 
	 * @param url
	 */
	public void load_img(String url) {
		Log.i("hexiuhui---", "urlurlurl" + url);
		if (!"".equals(url) && null != url) {
			ImageLoader loader = ImageLoader.getInstance();
			loader.displayImage(url, iv_user_icon);
		} else {
			iv_user_icon.setBackgroundResource(R.drawable.personal_center_avatar);
		}
	}
	/**
	 * 初始化界面控件
	 */
	private void initViews() {
		// TODO Auto-generated method stub
		iv_user_sex=(ImageView) findViewById(R.id.iv_user_sex);
		tv_my_points=(TextView) findViewById(R.id.tv_my_points);
		waveview=(WaveView) findViewById(R.id.waveview);
		rl_user_info=(RelativeLayout) findViewById(R.id.rl_user_info);
		rl_setting=(LinearLayout) findViewById(R.id.rl_setting);
		rl_contact_us=(LinearLayout) findViewById(R.id.rl_contact_us);
		center_rl_news_guide = (RelativeLayout) findViewById(R.id.center_rl_news_guide);
		center_rl_my_comment = (RelativeLayout) findViewById(R.id.center_rl_my_comment);
		// 工作状态
		iv_switch_status = (ImageView) findViewById(R.id.iv_switch_status);
		// 意见反馈
		center_rl_center_feedback = (RelativeLayout) findViewById(R.id.center_rl_center_feedback);
		// 关于爱学派
		center_rl_about_mine = (RelativeLayout) findViewById(R.id.center_rl_about_mine);
		// 邀请好友
		center_rl_invite_friend = (RelativeLayout) findViewById(R.id.center_rl_invite_friend);
		//赏金猎人
		rl_money_hunter = (RelativeLayout) findViewById(R.id.rl_money_hunter);
		//我的钱包
		rl_my_pocket = (RelativeLayout) findViewById(R.id.rl_my_pocket);
		//经验值
		rl_experience=(RelativeLayout) findViewById(R.id.rl_experience);
		center_tv_name = (TextView) findViewById(R.id.center_tv_name);
		center_tv_level = (Button) findViewById(R.id.center_tv_level);
		center_tv_phone = (TextView) findViewById(R.id.center_tv_phone);
		center_tv_campus = (TextView) findViewById(R.id.center_tv_campus);
		iv_user_icon = (CircularImage) findViewById(R.id.iv_user_icon);
		

		mWorkState = MyApplication.getInstance().m_userInfo.m_strUserWorkState;
		if (mWorkState.equals(Constant.USER_STATE_REST)) {
			m_bIsWorking = true;
			iv_switch_status.setBackground(getResources().getDrawable(R.drawable.switch_off));
		} else {
			m_bIsWorking = false;
			iv_switch_status.setBackground(getResources().getDrawable(R.drawable.switch_on));
		}

	}
	/**
	 * 绑定点击事件
	 */
	private void setClickListener() {
		// TODO Auto-generated method stub
		rl_experience.setOnClickListener(this);
		rl_contact_us.setOnClickListener(this);
		rl_setting.setOnClickListener(this);
		center_rl_news_guide.setOnClickListener(this);
		center_rl_my_comment.setOnClickListener(this);
		iv_switch_status.setOnClickListener(this);
		center_rl_about_mine.setOnClickListener(this);
		center_rl_center_feedback.setOnClickListener(this);
		center_rl_invite_friend.setOnClickListener(this);
		rl_money_hunter.setOnClickListener(this);
		rl_my_pocket.setOnClickListener(this);
		rl_user_info.setOnClickListener(this);
		
		
	}
@Override
protected void onPause() {
	// TODO Auto-generated method stub
	waveview.stop();
	super.onPause();
}
@Override
protected void onRestart() {
	//TODO Auto-generated method stub
	waveview.start();
	super.onRestart();
}
	/**
	 * 点击事件
	 * 
	 * @param arg0
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_setting:
			startActivity(new Intent(MyActivity.this,UserInfoActivity.class));
			break;
		
		case R.id.center_rl_center_feedback:
			startActivity(new Intent(MyActivity.this,FeedBackActivity.class));
			break;
		case R.id.rl_user_info:
//			// 友盟统计
//			MobclickAgent.onEvent(MyActivity.this, "rl_user_info");
//
//			startActivity(new Intent(getApplication(), UserInfoActivity.class));
//			if (!"".equals(myapp.m_userInfo.verifyStatus) && myapp.m_userInfo.verifyStatus != Constant.REVIEW_STATE_NOT_SUBMIT) {
//			} else {
//				show_dialog();
//			}
			break;
		case R.id.center_rl_invite_friend:

			if (!"".equals(myapp.m_userInfo.verifyStatus)
					&& myapp.m_userInfo.verifyStatus == Constant.REVIEW_STATE_SUCCESS) {
				startActivity(new Intent(getApplication(),
						InviteActivity.class));
			} else {
				if (myapp.m_userInfo.verifyStatus==1) {
					DialogUtils.showReviewingDialog(MyActivity.this);
				}else if(myapp.m_userInfo.verifyStatus==3){
					DialogUtils.showReviewingFailed(MyActivity.this, "");
				} else if(myapp.m_userInfo.verifyStatus==0){
					DialogUtils.showAuthDialog(MyActivity.this);
				}
			}
		break;
		case R.id.rl_my_pocket://我的钱包
			// 友盟统计
			MobclickAgent.onEvent(MyActivity.this, "rl_user_wallet");

			Intent intent3 = new Intent(getApplication(), MyWalletActivity.class);
			intent3.putExtra("officeRoleType", officeRoleType);
			startActivity(intent3);
			break;
		
		case R.id.center_rl_data_center:
			// TODO 数据中心
			// 友盟统计
			MobclickAgent.onEvent(MyActivity.this, "center_rl_data_center");
			break;
		case R.id.iv_switch_status:
			// TODO 工作状态
			// 友盟统计
			MobclickAgent.onEvent(MyActivity.this, "iv_switch_status");

			// 切换工作状态
			mWorkState = MyApplication.getInstance().m_userInfo.m_strUserWorkState;
			if (mWorkState.equals(Constant.USER_STATE_REST)) {
				m_bIsWorking = true;
			} else {
				m_bIsWorking = false;
			}

			mTaskSetWorkState = setWorkState(m_bIsWorking);
			break;
		case R.id.center_rl_news_guide:
			// TODO 帮助与反馈
			// 友盟统计
			MobclickAgent.onEvent(MyActivity.this, "center_rl_center_feedback");
			Intent intent2 = new Intent(MyActivity.this, BannerDetailActivity.class);
			intent2.putExtra("title", "帮助与反馈");
			intent2.putExtra("url", Constant.QA_URL);
			intent2.putExtra("type", "question");
			startActivity(intent2);
			break;
		case R.id.center_rl_about_mine:
			// TODO 关于爱学派
			// 友盟统计
			MobclickAgent.onEvent(MyActivity.this, "center_rl_center_on");
			Intent intent = new Intent(MyActivity.this, AboutWeActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_contact_us:
			// TODO 客服电话
			// 友盟统计
			MobclickAgent.onEvent(MyActivity.this, "center_rl_consumer_hotline");

			DialogUtils.showCallPhoneDialog(MyActivity.this, "400-166-1098", null);
			break;
		case R.id.rl_experience:
			// TODO 经验值
			// 友盟统计
			MobclickAgent.onEvent(MyActivity.this, "center_tv_leve");

			Intent intentWeb = new Intent(MyActivity.this, BannerDetailActivity.class);
			intentWeb.putExtra("title", "等级说明");
			intentWeb.putExtra("url", expPointRule + "?levelCode=" + levelCode);
			startActivity(intentWeb);

			break;
		case R.id.rl_money_hunter://赏金猎人
			Intent taskIntent = new Intent(MyActivity.this, TaskOrderMenuActivity.class);
			startActivity(taskIntent);
			break;
		case R.id.center_rl_news_center://
			startActivity(new Intent(MyActivity.this, MessageActivity.class));
			break;
		case R.id.center_rl_my_comment://我的评价
			
			Intent evaluateIntent = new Intent(MyActivity.this, OrderEvaluateActivity.class);
			startActivity(evaluateIntent);
			break;
		default:
			break;
		}
	}
	protected LoadTextNetTask setWorkState(final boolean workState) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@SuppressLint("NewApi")
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {
				mTaskSetWorkState = null;
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(
								mresult.m_strContent);
						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							if (workState == false) {
								MyApplication.getInstance().m_userInfo.m_strUserWorkState = Constant.USER_STATE_REST;
								iv_switch_status.setBackground(getResources()
										.getDrawable(R.drawable.switch_off));
							} else {
								MyApplication.getInstance().m_userInfo.m_strUserWorkState = Constant.USER_STATE_WORK;
								iv_switch_status.setBackground(getResources()
										.getDrawable(R.drawable.switch_on));
							}
							CommandTools.showToast("切换成功");
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);

							if (workState) {
								iv_switch_status.setBackground(getResources()
										.getDrawable(R.drawable.switch_off));
							} else {
								iv_switch_status.setBackground(getResources()
										.getDrawable(R.drawable.switch_on));
							}
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(MyActivity.this);
						e.printStackTrace();

						if (workState) {
							iv_switch_status.setBackground(getResources()
									.getDrawable(R.drawable.switch_off));
						} else {
							iv_switch_status.setBackground(getResources()
									.getDrawable(R.drawable.switch_on));
						}
					}
				} else {
					Util.showNetErrorMessage(MyActivity.this,
							result.m_nResultCode);

					if (workState) {
						iv_switch_status.setBackground(getResources()
								.getDrawable(R.drawable.switch_off));
					} else {
						iv_switch_status.setBackground(getResources()
								.getDrawable(R.drawable.switch_on));
					}
				}
			}
		};

		CustomProgress.showDialog(MyActivity.this, "切换状态...", false, null);
		LoadTextNetTask task = SettingsService.setWorkState(workState,
				listener, null);
		return task;
	}
	/**
	 * 获取个人信息
	 */
	private void getPersonalMessage() {
		if (TextUtils.isEmpty(MyApplication.getInstance().m_userInfo.toKen)) {
			return;
		}
		JSONObject jsonObject = new JSONObject();

		String strUrl = "user/packet/getuser.htm";
		RequestUtilNet.postData(CommandTools.getGlobalActivity(), strUrl, jsonObject, new MyCallback() {
			@Override
			public void callback(JSONObject jsonObject) {
				try {
					if (jsonObject.optInt("success") == 0) {
						jsonObject = jsonObject.optJSONObject("data");

						myapp.m_userInfo.m_strUserName = jsonObject.optString("realName");
						myapp.m_userInfo.m_strUserHeadPic = jsonObject.optString("icon");
						myapp.m_userInfo.m_strUserPhone = jsonObject.optString("phone");
						myapp.m_userInfo.m_user_total_income = jsonObject.optLong("totalIncome");
						myapp.m_userInfo.verifyStatus = jsonObject.optInt("state");
						myapp.m_userInfo.m_strUserSign = jsonObject.optString("signNumber");
						myapp.m_userInfo.m_strUserGender = jsonObject.optString("gender");
						myapp.m_userInfo.m_strUserWorkState = jsonObject.getString("grabOrderMode");
						myapp.m_userInfo.m_nick_name=jsonObject.optString("nickname");
						officeRoleType = jsonObject.optInt("officeRoleType");
						collegeName = jsonObject.optString("collegeName");
						user_office=jsonObject.optString("office");
						expPoint = jsonObject.optInt("expPoint");
//						balance = 
						expPointRule = jsonObject.optString("expPointRule");
						levelCode = jsonObject.optInt("levelCode");
						levelDesc = jsonObject.optString("levelDesc");
//						tv_my_pocket.setText(CommandTools.longTOString(balance));
//						tv_my_sign.setText(myapp.m_userInfo.m_strUserSign);
						myapp.m_userInfo.m_user_balance = jsonObject.optLong("balance");
						mHandler.sendEmptyMessage(0);
						
						Log.i("hexiuhui---", myapp.m_userInfo.m_strUserGender + "===");
						
						if (myapp.m_userInfo.m_strUserGender.equals("女")) {
							iv_user_sex.setImageResource(R.drawable.personal_center_female);
						} else if (myapp.m_userInfo.m_strUserGender.equals("男")) {
							iv_user_sex.setImageResource(R.drawable.personal_center_male);
						} 
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 被系统回收时保存myapplication全局变量值
		GlobalInstanceStateHelper.saveInstanceState(outState);
		outState.putString("mWorkState", mWorkState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// 恢复被系统回收后的myapplication值
		GlobalInstanceStateHelper
		.restoreInstanceState(this, savedInstanceState);

		initViews();
		mWorkState = savedInstanceState.getString("mWorkState");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mTaskSetWorkState != null) {
			mTaskSetWorkState.cancel(true);
			mTaskSetWorkState = null;
		}
	}
}
