package com.zhiduan.crowdclient.share;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.WriterException;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.UmengTool;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.R.id;
import com.zhiduan.crowdclient.R.layout;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.menuindex.BannerDetailActivity;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.GlobalInstanceStateHelper;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.GeneralDialog.TwoButtonListener;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * <pre>
 * Description	分享邀请好友
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-11-24 下午9:49:31
 * </pre>
 */
public class InviteCenter_Activity extends BaseActivity {

	private ImageView tv_share_qq, tv_share_wx, tv_friends_circle,
			iv_invite_qrcode;
	private UMShareListener umShareListener;
	private TextView tv_invite_code, tv_invite_people, tv_invite_income;
	private static float imagt_halfWidth = 20;
	UMImage image = new UMImage(InviteCenter_Activity.this, R.drawable.app_logo);// 资源文件
	private String rule_url = "activerule.htm";// 活动规则地址
	private String invite_url = "";
	private String invite_code = "";
	private String share_content = "";
	private int invite_num;
	private Long invite_income;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_share_center, this);
		setTitle("邀请有赏");
		setRightTitleText("活动规则");
		if (savedInstanceState != null) {
			// 因为用到了myapplication里的全局变量，被系统回收后要恢复
			GlobalInstanceStateHelper.restoreInstanceState(this,
					savedInstanceState);
		}

		Utils.addActivity(this);
		// UMShareAPI mShareAPI = UMShareAPI.get(ShareCenter_Activity.this);
		// mShareAPI.doOauthVerify(ShareCenter_Activity.this, SHARE_MEDIA.QQ,
		// umAuthListener);

	}

	@Override
	public void initView() {
		tv_invite_people = (TextView) findViewById(R.id.tv_invite_people);
		tv_invite_income = (TextView) findViewById(R.id.tv_invite_income);
		iv_invite_qrcode = (ImageView) findViewById(R.id.iv_invite_qrcode);
		tv_invite_code = (TextView) findViewById(R.id.tv_invite_code);
		tv_share_qq = (ImageView) findViewById(R.id.tv_share_qq);
		tv_share_wx = (ImageView) findViewById(R.id.tv_share_wx);
		tv_friends_circle = (ImageView) findViewById(R.id.tv_friends_circle);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		get_InviteInfo();
		/**
		 * 友盟分享的回调方法
		 */
		umShareListener = new UMShareListener() {

			@Override
			public void onResult(SHARE_MEDIA platform) {
				Log.d("plat", "platform" + platform);

				// Toast.makeText(InviteCenter_Activity.this, platform +
				// " 分享成功啦",
				// Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onError(SHARE_MEDIA platform, Throwable arg1) {
				// TODO Auto-generated method stub
				// Log.d("plat", "platform" + platform);
				// Log.d("plat", "throwable" + arg1);
				// Toast.makeText(InviteCenter_Activity.this, platform +
				// " 分享失败啦 ",
				// Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancel(SHARE_MEDIA platform) {
				// // TODO Auto-generated method stub
				// Toast.makeText(InviteCenter_Activity.this, platform +
				// " 分享取消了",
				// Toast.LENGTH_SHORT).show();
			}
		};
		/**
		 * 分享到QQ
		 */
		tv_share_qq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new ShareAction(InviteCenter_Activity.this)
						.setPlatform(SHARE_MEDIA.QQ).withTitle("「邀请有赏」")
						.withTargetUrl(Constant.INVITE_URL).withMedia(image)
						.withText(share_content).setCallback(umShareListener)
						.share();
			}
		});
		/**
		 * 分享到微信
		 */
		tv_share_wx.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// TODO Auto-generated method stub
				new ShareAction(InviteCenter_Activity.this)
						.setPlatform(SHARE_MEDIA.WEIXIN).withTitle("「邀请有赏」")
						.withTargetUrl(Constant.INVITE_URL).withMedia(image)
						.withText(share_content).setCallback(umShareListener)
						.share();
			}
		});
		/**
		 * 分享到朋友圈
		 */
		tv_friends_circle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new ShareAction(InviteCenter_Activity.this)
						.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
						.withTitle("「邀请有赏」").withTargetUrl(Constant.INVITE_URL)
						.withMedia(image).withText(share_content)
						.setCallback(umShareListener).share();
			}
		});
		/**
		 * 分享短信
		 */
		findViewById(R.id.tv_share_message).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						new ShareAction(InviteCenter_Activity.this)
								.setPlatform(SHARE_MEDIA.SMS)
								.withTitle("「邀请有赏」")
								.withText(""+share_content+"点击前往下载APP, "+Constant.INVITE_URL+" "+"完成任务立即领取奖励!" )
								.setCallback(umShareListener).share();
					}
				});
		/**
		 * 点击邀请人数
		 */
		findViewById(R.id.rl_invite_people).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (invite_num == 0) {
							Toast.makeText(mContext, "你还没有邀请到人!", 0).show();
						} else {

							startActivity(new Intent(
									InviteCenter_Activity.this,
									InvitePeopleActivity.class));
						}
					}
				});
		/**
		 * 点击邀请收入
		 */
		findViewById(R.id.rl_invite_income).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (invite_income == 0) {
							Toast.makeText(mContext, "你还没有收入!", 0).show();
						} else {
							startActivity(new Intent(
									InviteCenter_Activity.this,
									InviteIncomeActivity.class));
						}

					}
				});
		/**
		 * 长按复制邀请码
		 */
		tv_invite_code.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				CopyDialog(tv_invite_code);
				return false;
			}
		});
		
		//根据传入URL生成二维码,并且中间带图标的
		Bitmap mBitmap = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.icon_center);
		
		Matrix m = new Matrix();
		float sx = (float) 2 * imagt_halfWidth / mBitmap.getWidth();
		float sy = (float) 2 * imagt_halfWidth / mBitmap.getHeight();
		m.setScale(sx, sy);

		mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
				mBitmap.getHeight(), m, false);

		Bitmap creatTwoBitMap;
		try {
			creatTwoBitMap = CommandTools.creatTwoBitMap(Constant.INVITE_URL,mBitmap);
			iv_invite_qrcode.setBackground(new BitmapDrawable(creatTwoBitMap));
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 查询用户邀请信息
	 */
	private void get_InviteInfo() {
		JSONObject jsonObject = new JSONObject();
		RequestUtilNet.postDataToken(mContext, Constant.getInviteInfo,
				jsonObject, new RequestCallback() {
					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						try {
							if (success == 0) {
								jsonObject = jsonObject.optJSONObject("data");
								share_content = jsonObject
										.optString("shareContent");
								invite_code = jsonObject
										.optString("inviteCode");
								invite_num = jsonObject.optInt("inviterNum");
								invite_income = jsonObject
										.optLong("inviteIncome");

								tv_invite_code.setText(invite_code);

								tv_invite_income.setText(AmountUtils
										.changeF2Y(invite_income) + "元");
								tv_invite_people.setText(invite_num + "人");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

	}

	/**
	 * 活动规则点击
	 */
	@Override
	public void rightClick() {
		Intent intent = new Intent(InviteCenter_Activity.this,
				ActiveRuleActivity.class);
		// intent.putExtra("title", "活动规则");
		// intent.putExtra("url", Constant.RULE_URL);
		startActivity(intent);
	}

	/**
	 * 长按复制弹框
	 * 
	 * @param textView
	 */
	public void CopyDialog(final TextView textView) {
		GeneralDialog.showTwoButtonDialog(mContext,
				GeneralDialog.DIALOG_ICON_TYPE_8, "", "你确定要复制这条内容吗?", "取消",
				"确定", new TwoButtonListener() {

					@Override
					public void onRightClick(Dialog dialog) {
						ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
						cm.setText(textView.getText().toString());
						GeneralDialog.dismiss();
						Toast.makeText(mContext, "已复制到剪贴板!", 0).show();
					}

					@Override
					public void onLeftClick(Dialog dialog) {
						GeneralDialog.dismiss();

					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

	}

}
