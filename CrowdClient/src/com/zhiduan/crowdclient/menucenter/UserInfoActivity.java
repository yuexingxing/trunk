package com.zhiduan.crowdclient.menucenter;

import java.io.File;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.activity.CutPictureActivity;
import com.zhiduan.crowdclient.activity.LoginActivity;
import com.zhiduan.crowdclient.activity.MainActivity;
import com.zhiduan.crowdclient.activity.UpdateUserPwdActivity;
import com.zhiduan.crowdclient.completeinformation.SchoolDataActivity;
import com.zhiduan.crowdclient.completeinformation.SelectSchoolActivity;
import com.zhiduan.crowdclient.data.SchoolInfo;
import com.zhiduan.crowdclient.im.DemoHelper;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.photoalbum.MyPhotoAlbumActivity;
import com.zhiduan.crowdclient.photoalbum.PhotoAlbumReceiver;
import com.zhiduan.crowdclient.photoalbum.camera.MyCameraActivity;
import com.zhiduan.crowdclient.service.UserService;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.Utils;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.util.SharedPreferencesUtils;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.ChoiceDialog;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.crop.Crop;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;
import com.zhiduan.crowdclient.view.dialog.DialogUtils.DialogCallback;
import com.zhiduan.crowdclient.zxing.view.CircularImage;

/**
 * <pre>
 * Description  个人信息界面
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-5-26 上午10:54:16
 * </pre>
 */
public class UserInfoActivity extends BaseActivity implements OnClickListener {
	private final String mPageName = "UserInfoActivity";

	private final int FLAG_MODIFY_FINISH = 3;
	/** 截取结束标志 */
	public static int PHOTO_INDEX = 0;// 记录当前的位置
	private RelativeLayout layout;
	private Context context;
	private SchoolInfo schoolInfo;
	private RelativeLayout rl_school_data, rl_change_psd, rl_select_sex,
	 rl_user_head;
	private RelativeLayout rl_user_name;
//	private XCRoundRectImageView iv_user_icon;
	public final int SELECT_SCHOOL = 1001;
	private CircularImage iv_user_icon;
	public static final String AUTH = "auth";
	private ChoiceDialog dialog;
	private TextView tv_auth_status, tv_user_sex;
	private TextView tv_user_count;
	private TextView tv_user_name;
	private TextView tv_school_name;
//	private TextView tv_dormitry;
	private TextView tv_restart_auth;
//	private ImageView iv_sex;
	public MyApplication myapp = MyApplication.getInstance();
	private Uri destination;

	private static UserInfoActivity mActivity;
	public Bitmap mBitmap;

	private PhotoAlbumReceiver receiver;
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg!=null){
				String path=(String) msg.obj;
				if(!TextUtils.isEmpty(path)){

					File out = new File(path);  
					Uri uri = Uri.fromFile(out);  
					beginCrop(uri);
				}else{
					CommandTools.showToast("请重新选择");
				}
			}

		};
	};
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_user_info, this);
		context = this;
		mActivity = UserInfoActivity.this;
		setTitle("个人信息");
		receiver = new PhotoAlbumReceiver (mHandler); // Create the receiver
		registerReceiver(receiver, new IntentFilter("photograph.photoalbum"));
	}
	
	@Override
	public void initData() {
		get_schoolinfo();
		load_img(myapp.m_userInfo.m_strUserHeadPic);
		if (myapp.m_userInfo.m_strUserGender.equals("女")) {
			tv_user_sex.setText("女");
//			iv_sex.setImageResource(R.drawable.profile_girl);
		} else if (myapp.m_userInfo.m_strUserGender.equals("男")) {
			tv_user_sex.setText("男");
//			iv_sex.setImageResource(R.drawable.profile_boy);
		} 

		if (myapp.m_userInfo.verifyStatus == Constant.REVIEW_STATE_NOT_SUBMIT) {
			tv_auth_status.setText("未审核");
//			rl_auth.setClickable(true);
		} else if (myapp.m_userInfo.verifyStatus == Constant.REVIEW_STATE_SUBMITING) {
//			rl_auth.setClickable(false);
			tv_auth_status.setText("审核中");
		} else if (myapp.m_userInfo.verifyStatus == Constant.REVIEW_STATE_SUCCESS) {
//			rl_auth.setClickable(false);
			tv_auth_status.setText("审核通过");
			
		}

		if (myapp.m_userInfo.verifyStatus == Constant.REVIEW_STATE_FAIL) {
//			rl_auth.setClickable(true);
			tv_auth_status.setText("审核失败");
			
		}
	}
	
	@Override
	public void initView() {
		tv_restart_auth=(TextView) findViewById(R.id.tv_restart_auth);
//		tv_dormitry=(TextView) findViewById(R.id.tv_dormitry);
		tv_school_name=(TextView) findViewById(R.id.tv_school_name);
		tv_user_name=(TextView) findViewById(R.id.tv_user_name);
		tv_user_count=(TextView) findViewById(R.id.tv_user_count);
//		iv_sex = (ImageView) findViewById(R.id.iv_sex);
		tv_user_sex = (TextView) findViewById(R.id.tv_user_sex);
		tv_auth_status = (TextView) findViewById(R.id.tv_auth_status);
		layout = (RelativeLayout) findViewById(R.id.layout_user_info);
		rl_user_head = (RelativeLayout) findViewById(R.id.rl_user_head);
//		rl_auth = (RelativeLayout) findViewById(R.id.rl_auth);
		rl_school_data = (RelativeLayout) findViewById(R.id.rl_school_data);
		rl_select_sex = (RelativeLayout) findViewById(R.id.rl_select_sex);
		rl_change_psd = (RelativeLayout) findViewById(R.id.rl_change_psd);
		rl_user_name=(RelativeLayout) findViewById(R.id.rl_user_name);
		iv_user_icon = (CircularImage) findViewById(R.id.iv_user_icon);

		tv_restart_auth.setOnClickListener(this);
		rl_user_name.setOnClickListener(this);
		rl_school_data.setOnClickListener(this);
		rl_change_psd.setOnClickListener(this);
		rl_select_sex.setOnClickListener(this);
		rl_user_head.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getPersonalMessage();
		//友盟统计
		//		MobclickAgent.onPageStart(mPageName);
		//		MobclickAgent.onResume(this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		//友盟统计
		//		MobclickAgent.onPageEnd(mPageName);
		//		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		unregisterReceiver(receiver);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_user_name:
			startActivity(new Intent(context, ChangeNameActivity.class));
			break;
		case R.id.tv_restart_auth:
			Intent intent = new Intent(UserInfoActivity.this,
					SelectSchoolActivity.class);
			intent.putExtra("user", "userinfo");
			startActivityForResult(intent, SELECT_SCHOOL);
			break;
		case R.id.rl_school_data:
			//友盟统计
			MobclickAgent.onEvent(UserInfoActivity.this, "rl_school_data");
			startActivity(new Intent(context, SchoolDataActivity.class));
			break;
		case R.id.rl_change_psd:
			//友盟统计
			MobclickAgent.onEvent(UserInfoActivity.this, "rl_change_psd");
			startActivity(new Intent(context, UpdateUserPwdActivity.class));
			break;
		case R.id.rl_select_sex:
			new PopupWindows_UpdateaSex(context, rl_select_sex);
			break;
		case R.id.rl_user_head:
			//友盟统计
			MobclickAgent.onEvent(UserInfoActivity.this, "rl_user_head");

			/**
			 * 上传自拍照
			 * 
			 * @param v
			 */
			Constant.PHOTO_INDEX = 0;
			CommandTools.hidenKeyboars(context, v);
			new PopupWindows(context, layout);
			break;
		default:
			break;
		}

	}
	
	/**
	 * 查询学生完善资料
	 */
	private void get_schoolinfo() {
		JSONObject jsonObject = new JSONObject();
		RequestUtilNet.postDataToken(context, Constant.getSchoolData,
				jsonObject, new RequestCallback() {
					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						try {
							if (success == 0) {
								JSONObject object = jsonObject
										.optJSONObject("data");
								tv_school_name.setText(object
										.optString("collegeName"));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

	}
	/**
	 * 修改学校信息
	 * @param id
	 */
	public void UpdateShool(int id) {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("collegeId", id);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(mContext, Constant.updateschool_url,
				jsonObject, new RequestCallback() {

					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {

						CommandTools.showToast(remark);
						if (success == 0) {
							tv_auth_status.setText("审核中");
						}
					}
				});

	
	}
	/**
	 * 获取个人信息
	 */
	private void getPersonalMessage() {

		JSONObject jsonObject = new JSONObject();

		RequestUtilNet.postData(CommandTools.getGlobalActivity(), Constant.getUserInfo_url, jsonObject, new MyCallback() {

			@Override
			public void callback(JSONObject jsonObject) {
				try {
					jsonObject = jsonObject.optJSONObject("data");

					MyApplication myApp = MyApplication.getInstance();
					myApp.m_userInfo.m_strUserName = jsonObject.optString("realName");
					myApp.m_userInfo.m_strUserHeadPic = jsonObject.optString("icon");
					myApp.m_userInfo.m_user_income = jsonObject.optLong("totalIncome");
					myApp.m_userInfo.verifyStatus = jsonObject.optInt("state");
					myApp.m_userInfo.m_strUserSign = jsonObject.optString("signNumber");
					myApp.m_userInfo.m_strUserGender = jsonObject.optString("gender");
					myApp.m_userInfo.m_nick_name=jsonObject.optString("nickname");
					tv_user_name.setText(jsonObject.optString("nickname"));
					tv_user_count.setText(myApp.m_userInfo.m_strUserPhone);
					int state=jsonObject.optInt("state");
					if (state==1) {
						startActivity(new Intent(UserInfoActivity.this,MainActivity.class));
					}
					if (jsonObject.optString("gender").equals("男")) {
						tv_user_sex.setText(jsonObject.optString("gender"));
//						iv_sex.setImageResource(R.drawable.profile_boy);
					} else if (jsonObject.optString("gender").equals("女")) {
						tv_user_sex.setText(jsonObject.optString("gender"));
//						iv_sex.setImageResource(R.drawable.profile_girl);
					} 
					Logs.v("zdkj", "state = " + jsonObject.optInt("state"));

					//load_img(jsonObject.optString("icon"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 加载图片
	 * 
	 * @param url
	 */
	public void load_img( String url) {
		if (!"".equals(url) && null != url) {
			ImageLoader loader = ImageLoader.getInstance();
			loader.displayImage(url, iv_user_icon);
		} else {
			iv_user_icon.setBackgroundResource(R.drawable.personal_center_avatar);
		}
	}

	/**
	 * 上传头像
	 * 
	 * @param type
	 * @param bitmap
	 */
	private void uploadPic(Bitmap bitmap) {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("fileName", "head.JPEG");
			jsonObject.put("file", URLEncoder.encode(CommandTools.bitmap2String(bitmap, 80)));
			jsonObject.put("sourceId", "1");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(context, Constant.upload_userpic_url, jsonObject, new RequestCallback() {
			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				try {
					String strRemark = jsonObject.getString("message");
					CommandTools.showToast(strRemark);
					getPersonalMessage();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * 修改性别
	 */
	private LoadTextNetTask update_sex(String sexType, String sex) {

		//如果选择的是当前性别，则不需要请求接口
		if(MyApplication.getInstance().m_userInfo.m_strUserGender.equals(sex)){
			return null;
		}

		OnPostExecuteListener listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);
						Log.i("zdkj", jsonObj.toString() + "---");

						int res = jsonObj.optInt("success");

						if (res == 0) {
							String message = jsonObj.optString("message");

							Toast.makeText(UserInfoActivity.this, message, Toast.LENGTH_LONG).show();
							getPersonalMessage();// 查询用户信息
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(UserInfoActivity.this, result.m_nResultCode);
				}
				CustomProgress.dissDialog();
			}
		};
		CustomProgress.showDialog(UserInfoActivity.this, "", false, null);
		LoadTextNetTask task = UserService.updateSex(sexType, listener, null);
		return task;
	}

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			super(mContext);

			View view = View.inflate(mContext, R.layout.item_popupwindows, null);

			final LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
			getBackground().setAlpha(80);
			setAnimationStyle(R.style.AnimationFade);
			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setOutsideTouchable(false);
			setFocusable(false);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.layout_pop_window);
			Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);

			layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					ll_popup.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
					dismiss();
				}
			});
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
//					Intent intent=new Intent(UserInfoActivity.this.mContext,MyCameraActivity.class);
					Intent intent=new Intent(UserInfoActivity.this.mContext,MyCameraActivity.class);
					startActivityForResult(intent, Crop.TAKE_PHOTO);
					//					getFromCamera();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent=new Intent(UserInfoActivity.this.mContext,MyPhotoAlbumActivity.class);
					startActivityForResult(intent, 2);
					//					getFromFolder();
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}
	public class PopupWindows_UpdateaSex extends PopupWindow {

		public PopupWindows_UpdateaSex(Context mContext, View parent) {

			super(mContext);

			View view = View.inflate(mContext, R.layout.update_sex, null);

			final LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
			getBackground().setAlpha(80);
			setAnimationStyle(R.style.AnimationFade);
			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			ll_popup.getBackground().setAlpha(80);
			setOutsideTouchable(false);
			setFocusable(false);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.layout_pop_window);
			Button bt1 = (Button) view.findViewById(R.id.btn_gender_male);
			Button bt2 = (Button) view.findViewById(R.id.btn_gender_female);
			Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);

			layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					ll_popup.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
					dismiss();
				}
			});
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					update_sex("2", "男");
					tv_user_sex.setText("男");
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					update_sex("1", "女");
					tv_user_sex.setText("女");
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_SCHOOL && resultCode == 1111) {
			schoolInfo = (SchoolInfo) data.getSerializableExtra("schoolinfo");
			tv_school_name.setText(schoolInfo.getCOLLEGE_NAME());
			getPersonalMessage();
//			Exit_login(tv_school_name);
//			school_id = schoolInfo.getCOLLEGE_GCODE();
		}
		
		if (requestCode == Crop.TAKE_PHOTO && resultCode == RESULT_OK) {
			if (data != null) {
				String pathCamera = data.getStringExtra("pathCamera");
				if (!TextUtils.isEmpty(pathCamera)) {
					// main_id.setImageBitmap(getLoacalBitmap(pathCamera));
					Logs.e("wufuqi---", "拍照返回的路径:" + pathCamera);

					File out = new File(pathCamera);
					Uri uri = Uri.fromFile(out);
					beginCrop(uri);
				} else {
					Toast.makeText(this, "请重新拍照", 0);
				}
			}
			// beginCrop(Crop.getTakePhotoUri());
		} else if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
			// beginCrop(data.getData());
		} else if (requestCode == Crop.REQUEST_CROP) {

			handleCrop(resultCode, data);
		}

	}

	/**
	 * 裁剪图片
	 * 
	 * @param path图片路径
	 */
	private void startCropImageActivity(String path) {

		Intent intent = new Intent(this, CutPictureActivity.class);
		intent.putExtra("path", path);
		startActivityForResult(intent, FLAG_MODIFY_FINISH);
	}

	/**
	 * 开始裁剪图片
	 * @param source
	 */
	private void beginCrop(Uri source) {
		destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
		Crop.of(source, destination).asSquare().start(this);
	}

	private void handleCrop(int resultCode, Intent result) {

		if (resultCode == RESULT_OK) {
			iv_user_icon.setImageBitmap(mBitmap);
			uploadPic(mBitmap);
		} else if (resultCode == Crop.RESULT_ERROR) {
			Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	protected void getFromCamera() {

		Crop.startTakePhoto(UserInfoActivity.this);
	}

	/**
	 * 从本地手机中选择图片
	 */
	private void getFromFolder() {

		Crop.pickImage(this);
	}

	public static UserInfoActivity getActivity(){

		if(mActivity != null){
			return mActivity;
		}

		return null;
	} 

	/**
	 * 退出登录
	 */
	public void Exit_login(View v) {
		DialogUtils.showExitDialog(UserInfoActivity.this, new DialogCallback() {
			@Override
			public void callback(int position) {
				if (position == 1) {
					JSONObject jsonObject = new JSONObject();
					RequestUtilNet.postDataToken(UserInfoActivity.this, Constant.Exit_url, jsonObject, new RequestCallback() {
						@Override
						public void callback(int success, String remark, JSONObject jsonObject) {
							CommandTools.showToast(remark);
							if (success == 0) {
								DemoHelper.getInstance().logout(true, new EMCallBack() {
				                    
				                    @Override
				                    public void onSuccess() {
				                        runOnUiThread(new Runnable() {
				                            public void run() {
				                            	Log.i("hexiuhui---", "环信退出成功");
				                            }
				                        });
				                    }
				                    
				                    @Override
				                    public void onProgress(int progress, String status) {}
				                    
				                    @Override
				                    public void onError(int code, String message) {}
				                });
								
								
								SharedPreferencesUtils.setParam(Constant.SP_LOGIN_PSD, "");
								MyApplication myApplication = MyApplication.getInstance();
								myApplication.m_userInfo.toKen = "";
								startActivity(new Intent(UserInfoActivity.this, LoginActivity.class));
								Utils.finishAllActivities();
							}
						}
					});
				}
			}
		});
	}
}
