package com.zhiduan.crowdclient.share;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.json.JSONObject;

import com.google.zxing.WriterException;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.R.anim;
import com.zhiduan.crowdclient.R.drawable;
import com.zhiduan.crowdclient.R.id;
import com.zhiduan.crowdclient.R.layout;
import com.zhiduan.crowdclient.R.style;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.menucenter.UserInfoActivity;
import com.zhiduan.crowdclient.photoalbum.MyPhotoAlbumActivity;
import com.zhiduan.crowdclient.photoalbum.camera.MyCameraActivity;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.GlobalInstanceStateHelper;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.GeneralDialog.TwoButtonListener;
import com.zhiduan.crowdclient.view.InviteFaceToFaceDialog;
import com.zhiduan.crowdclient.view.InviteFaceToFaceDialog.SavePicCallback;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * <pre>
 * Description	分享邀请界面
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2017-1-4 下午9:20:03
 * </pre>
 */
public class InviteActivity extends BaseActivity {
	/** 首先默认个文件保存路径 */
	private static final String SAVE_PIC_PATH = Environment
			.getExternalStorageState().equalsIgnoreCase(
					Environment.MEDIA_MOUNTED) ? Environment
			.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";// 保存到SD卡
	private static final String SAVE_REAL_PATH = SAVE_PIC_PATH + "/axp";// 保存的确切位置
	private Context mContext;
	private ImageView tv_share_qq, tv_share_wx, tv_friends_circle,
			iv_invite_qrcode;
	private UMShareListener umShareListener;
	private TextView tv_invite_code, tv_invite_people, tv_invite_income,tv_copy;
	private static float imagt_halfWidth = 20;
	UMImage image ;
	private MyApplication myapp = MyApplication.getInstance();
	private String rule_url = "activerule.htm";// 活动规则地址
	private String invite_url = "";
	private String invite_code = "";
	private String share_content = "";
	private int invite_num;//邀请数量
	private Long invite_income;//邀请收入
	private int inviteExpPoint;//邀请获得积分
	private int inviteVoucherCount;//邀请获得代金券数量
	private Button btn_face_share;
	private Button btn_share_dialog;
	private TextView tv_look_rule;
	private Bitmap mBitmap;
	private Bitmap creatTwoBitMap;
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentViewId(R.layout.activity_invite, this);
		mContext = this;
		setTitle("邀请有赏");
		image =new UMImage(mContext, R.drawable.app_logo);// 资源文件
		if (savedInstanceState != null) {
			// 因为用到了myapplication里的全局变量，被系统回收后要恢复
			GlobalInstanceStateHelper.restoreInstanceState(this,
					savedInstanceState);
		}
		setRightTitleText("活动规则");
		setRightTitleTextColor(R.color.red_share);
		Utils.addActivity(this);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		tv_copy=(TextView) findViewById(R.id.tv_copy);
		tv_look_rule=(TextView) findViewById(R.id.tv_look_rule);
		btn_face_share=(Button) findViewById(R.id.btn_face_share);
		btn_share_dialog=(Button) findViewById(R.id.btn_share_dialog);
		tv_invite_people = (TextView) findViewById(R.id.tv_invite_people);
		tv_invite_income = (TextView) findViewById(R.id.tv_invite_income);
		iv_invite_qrcode = (ImageView) findViewById(R.id.iv_invite_qrcode);
		tv_invite_code = (TextView) findViewById(R.id.tv_invite_code);
		tv_share_qq = (ImageView) findViewById(R.id.tv_share_qq);
		tv_share_wx = (ImageView) findViewById(R.id.tv_share_wx);
		tv_friends_circle = (ImageView) findViewById(R.id.tv_friends_circle);
	
	}
	/**
	 * 活动规则
	 */
	@Override
	public void rightClick() {
		// TODO Auto-generated method stub
		super.rightClick();
		// TODO Auto-generated method stub
		Intent intent = new Intent(InviteActivity.this,
				ActiveRuleActivity.class);
		 intent.putExtra("title", "活动规则");
		 intent.putExtra("url", Constant.RULE_URL);
		startActivity(intent);
	}
	@Override
	public void initData() {
		get_InviteInfo();
		// TODO Auto-generated method stub
		mBitmap = BitmapFactory.decodeResource(mContext.getResources(),
						R.drawable.icon_center);
				
				Matrix m = new Matrix();
				float sx = (float) 2 * imagt_halfWidth / mBitmap.getWidth();
				float sy = (float) 2 * imagt_halfWidth / mBitmap.getHeight();
				m.setScale(sx, sy);

				mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
						mBitmap.getHeight(), m, false);

				
				try {
					creatTwoBitMap = CommandTools.creatTwoBitMap(Constant.INVITE_URL+myapp.m_userInfo.m_strUserPhone,mBitmap);
				} catch (WriterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		/**
		 * 面对面邀请
		 */
		btn_face_share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				InviteFaceToFaceDialog.showMyDialog(mContext, MyApplication.getInstance().m_userInfo.m_strUserPhone, creatTwoBitMap, new SavePicCallback() {
					
					@Override
					public void callback(int pos) {
						// TODO Auto-generated method stub
						try {
							saveFile(creatTwoBitMap);
						} catch (Exception e) {
							CommandTools.showToast("保存失败");
							e.printStackTrace();
						}
					}
				});
				
			}
		});
		/**
		 * 点击分享邀请
		 */
		btn_share_dialog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new InviteWindows(mContext, durian_head_layout);
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
									InviteActivity.this,
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
						if (invite_income == 0&&inviteExpPoint==0) {
							Toast.makeText(mContext, "你还没有奖励!", 0).show();
						} else {
							startActivity(new Intent(
									InviteActivity.this,
									InviteIncomeActivity.class));
						}

					}
				});
		/**
		 * 长按复制邀请码
		 */
		tv_copy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				CopyDialog(tv_invite_code);
			}
		});
	}
	//分享底部弹窗
	public class InviteWindows extends PopupWindow {

		public InviteWindows(Context mContext, View parent) {

			super(mContext);

			View view = View
					.inflate(mContext, R.layout.invite_dialog, null);

			final LinearLayout ll_invite_dialog = (LinearLayout) view
					.findViewById(R.id.ll_invite_dialog);
			getBackground().setAlpha(80);
			setAnimationStyle(R.style.AnimationFade);
			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setOutsideTouchable(false);
			setFocusable(false);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			RelativeLayout layout = (RelativeLayout) view
					.findViewById(R.id.invite_pop_window);
ImageView  tv_share_qq=(ImageView) view.findViewById(R.id.tv_share_qq);
ImageView  tv_friends_circle=(ImageView) view.findViewById(R.id.tv_friends_circle);
ImageView  tv_share_wx=(ImageView) view.findViewById(R.id.tv_share_wx);
ImageView  tv_share_message=(ImageView) view.findViewById(R.id.tv_share_message);
TextView tv_btn_cancel_share=(TextView) view.findViewById(R.id.tv_btn_cancel_share);

			layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					ll_invite_dialog.startAnimation(AnimationUtils.loadAnimation(
							InviteActivity.this.mContext, R.anim.fade_out));
					dismiss();
				}
			});
			tv_btn_cancel_share.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dismiss();
				}
			});
			/**
			 * 分享到QQ
			 */
			tv_share_qq.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					new ShareAction(InviteActivity.this)
							.setPlatform(SHARE_MEDIA.QQ).withTitle("「邀请有赏」")
							.withTargetUrl(Constant.INVITE_URL+myapp.m_userInfo.m_strUserPhone).withMedia(image)
							.withText(share_content).setCallback(umShareListener)
							.share();
					dismiss();
				}
			});
			/**
			 * 分享到微信
			 */
			tv_share_wx.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					// TODO Auto-generated method stub
					new ShareAction(InviteActivity.this)
							.setPlatform(SHARE_MEDIA.WEIXIN).withTitle("「邀请有赏」")
							.withTargetUrl(Constant.INVITE_URL+myapp.m_userInfo.m_strUserPhone).withMedia(image)
							.withText(share_content).setCallback(umShareListener)
							.share();
					dismiss();
				}
			});
			/**
			 * 分享到朋友圈
			 */
			tv_friends_circle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					new ShareAction(InviteActivity.this)
							.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
							.withTitle("「邀请有赏」").withTargetUrl(Constant.INVITE_URL+myapp.m_userInfo.m_strUserPhone)
							.withMedia(image).withText(share_content)
							.setCallback(umShareListener).share();
					dismiss();
				}
			});
			/**
			 * 分享短信
			 */
			tv_share_message.setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							new ShareAction(InviteActivity.this)
									.setPlatform(SHARE_MEDIA.SMS)
									.withTitle("「邀请有赏」")
									.withText(""+share_content+"点击前往下载APP, "+Constant.INVITE_URL+myapp.m_userInfo.m_strUserPhone+" "+"完成任务立即领取奖励!" )
									.setCallback(umShareListener).share();
							dismiss();
						}
					});

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
								inviteExpPoint=jsonObject.optInt("inviteExpPoint");
								inviteVoucherCount=jsonObject.optInt("inviteVoucherCount");
								tv_invite_code.setText(invite_code);

								tv_invite_income.setText(AmountUtils
										.changeF2Y(invite_income) );
								tv_invite_people.setText(invite_num+"");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

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
	/**
	 * 保存图片到本地相册
	 * @param bm
	 * @param fileName
	 * @param path
	 * @throws IOException
	 */
		public void saveFile(Bitmap bm) throws IOException {
			// 根据当前时间生成图片名称
			Calendar c = Calendar.getInstance();
//			String fileName = "" + c.get(Calendar.YEAR) + c.get(Calendar.MONTH+1)
//					+ c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.HOUR_OF_DAY)
//					+ c.get(Calendar.MINUTE) + c.get(Calendar.SECOND) + ".png";
			String fileName="AXPQRCode"+".png";
			String subForder = SAVE_REAL_PATH;
			File folder = new File(subForder);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			File myCaptureFile = new File(subForder, fileName);
			if (!myCaptureFile.exists()) {
				myCaptureFile.createNewFile();
			}else {
				CommandTools.showToast("文件已存在");
			}
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			CommandTools.showToast("保存成功");
			NOtifyPhoto(folder);
	
		}
	
	// 更新相册
		private  void NOtifyPhoto(File file) {
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			Uri uri = Uri.fromFile(file);
			intent.setData(uri);
			mContext.sendBroadcast(intent);// 这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！
		}
}
