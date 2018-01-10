package com.zhiduan.crowdclient.completeinformation;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.CutPictureActivity;
import com.zhiduan.crowdclient.activity.MainActivity;
import com.zhiduan.crowdclient.data.RegisterInfo;
import com.zhiduan.crowdclient.menuindex.IndexActivity;
import com.zhiduan.crowdclient.menuindex.IndexActivity.OnTabActivityResultListener;
import com.zhiduan.crowdclient.photoalbum.MyPhotoAlbumActivity;
import com.zhiduan.crowdclient.photoalbum.PhotoAlbumReceiver;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.VoiceHint;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.ClearEditText;
import com.zhiduan.crowdclient.view.CustomProgress;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * <pre>
 * Description	完善信息
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2017-1-6 下午3:18:56
 * </pre>
 */
@SuppressLint("NewApi")
public class CompleteInformationActivity extends Activity implements
		OnTabActivityResultListener {

	private final int TAKE_PHOTO = 1;
	/** 拍照选取标志 */
	private final int GET_FROM_FOLDER = 2;
	/** 本地图片选取标志 */
	private final int FLAG_MODIFY_FINISH = 3;
	/** 截取结束标志 */
	private LinearLayout layout;
	private final String TMP_PATH = "temp.jpg";
	private static final String TAG = "zdkj";
	public static int PHOTO_INDEX = 0;// 记录当前的位置
	public static Bitmap mBitmap1;// 自拍照
	public static Bitmap mBitmap2;// 身份证正面照
	private ImageView iv_front_photo;// 自拍
	private ImageView iv_card_photo;// 身份证正面
	private ImageView iv_front_photo_error_img;// 审核失败的提示
	private ImageView iv_card_photo_error_img;
	private ImageView iv_sfz_step;
	private String auth = "";
	// private LinearLayout ll_sfz_layout;
	private ClearEditText edt_name_auth_manager;// 姓名
	private ClearEditText edt_id_auth_manager;// 身份证
	private TextView tv_id_auth_manager;
	private TextView tv_name_auth_manager;
	private TextView tv_choice_info;
	private RegisterInfo registerInfo;
	private String certificationType = "";
	private String status_name;
	private Button auth_manager_btn_next;
	private String status_id;
	private String status_hand_photo;
	private String status_front_photo;
	private String perfect_name = "";
	// private TextView tv_one_error, tv_two_error;
	private LinearLayout ll_complete_error;
	private TextView tv_complete_notice;
	private Context mContext;
	private LinearLayout ll_switch_btn;
	private MyApplication myapp = MyApplication.getInstance();
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg != null) {
				String path = (String) msg.obj;
				if (!TextUtils.isEmpty(path)) {
					startCropImageActivity(path);
				} else {
					CommandTools.showToast("请重新选择");
				}
			}

		};
	};
	private PhotoAlbumReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_complete_information);
		mContext = this;
		initView();
		initData();

	}

	/**
	 * 初始化控件
	 */
	public void initView() {
		// TODO Auto-generated method stub
		perfect_name = getIntent().getStringExtra("perfect");
		ll_switch_btn = (LinearLayout) findViewById(R.id.ll_switch_btn);
		tv_id_auth_manager = (TextView) findViewById(R.id.tv_id_auth_manager);
		tv_name_auth_manager = (TextView) findViewById(R.id.tv_name_auth_manager);
		iv_card_photo_error_img = (ImageView) findViewById(R.id.iv_card_photo_error_img);
		iv_front_photo_error_img = (ImageView) findViewById(R.id.iv_front_photo_error_img);
		tv_complete_notice = (TextView) findViewById(R.id.tv_complete_notice);
		ll_complete_error = (LinearLayout) findViewById(R.id.ll_complete_error);
		iv_front_photo = (ImageView) findViewById(R.id.iv_front_photo);
		iv_card_photo = (ImageView) findViewById(R.id.iv_card_photo);
		auth_manager_btn_next = (Button) findViewById(R.id.auth_manager_btn_next);
		// tv_one_error = (TextView) findViewById(R.id.tv_one_error);
		// tv_two_error = (TextView) findViewById(R.id.tv_two_error);
		tv_choice_info = (TextView) findViewById(R.id.tv_choice_info);
		edt_id_auth_manager = (ClearEditText) findViewById(R.id.edt_id_auth_manager);
		edt_name_auth_manager = (ClearEditText) findViewById(R.id.edt_name_auth_manager);
		iv_sfz_step = (ImageView) findViewById(R.id.iv_sfz_step);
		layout = (LinearLayout) findViewById(R.id.layout_auth);
		setEditableListener(edt_id_auth_manager);
		setEditableListener(edt_name_auth_manager);
		receiver = new PhotoAlbumReceiver(mHandler); // Create the receiver
		registerReceiver(receiver, new IntentFilter("photograph.photoalbum"));

	}

	public void initData() {
		if (myapp.m_userInfo.verifyStatus == 3) {
			select_verifyinfo();
			ll_complete_error.setVisibility(View.VISIBLE);

			tv_complete_notice.setVisibility(View.GONE);
		} 
		if (!TextUtils.isEmpty(perfect_name) && perfect_name.equals("name")) {
			ll_switch_btn.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 查询审核信息
	 */
	private void select_verifyinfo() {
		JSONObject jsonObject = new JSONObject();
		RequestUtilNet.postData(CommandTools.getGlobalActivity(),
				Constant.SelectVerifyInfo_url, jsonObject, new MyCallback() {

					@Override
					public void callback(JSONObject jsonObject) {

						try {
							if (jsonObject.optInt("success") == 0) {

								jsonObject = jsonObject.optJSONObject("data");
								edt_id_auth_manager.setText(jsonObject
										.optString("idNo"));
								edt_name_auth_manager.setText(jsonObject
										.optString("realName"));
								// 错误信息数组
								JSONObject verifyMap = jsonObject
										.optJSONObject("verifyMsg");
								// Gson gson = new Gson();
								// Map map =
								// gson.fromJson(jsonObject.toString(),
								// Map.class);
								// Map verifyMap =
								// gson.fromJson(map.get("verifyMsg")
								// .toString(), Map.class);
								// 照片集合
								JSONArray array = jsonObject
										.optJSONArray("photoList");
								for (int i = 0; i < array.length(); i++) {
									JSONObject arrayObject = (JSONObject) array
											.get(i);
									if (arrayObject.optInt("type") == 1) {
										// 手持身份证照
										load_img("hand",
												arrayObject.optString("url"),
												iv_front_photo);
									} else if (arrayObject.optInt("type") == 2) {
										// 身份证正面照
										load_img("front",
												arrayObject.optString("url"),
												iv_card_photo);
									}
								}
								status_id = verifyMap.optString("f5");
								status_name = verifyMap.optString("f4");
								status_hand_photo = verifyMap.optString("f6");
								status_front_photo = verifyMap.optString("f7");
								if (status_hand_photo.equals("-1")) {
									// tv_one_error.setVisibility(View.VISIBLE);

									iv_card_photo_error_img
											.setImageResource(R.drawable.failure);
								}
								if (status_front_photo.equals("-1")) {
									// tv_two_error.setVisibility(View.VISIBLE);
									iv_front_photo_error_img
											.setImageResource(R.drawable.failure);
								}
								if (status_name.equals("-1")) {
									draw_img(edt_name_auth_manager);
								}
								if (status_id.equals("-1")) {
									draw_img(edt_id_auth_manager);
								}
							} else {
								try {
									String remark = jsonObject
											.getString("message");
									CommandTools.showToast(remark);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					/**
					 * 显示错误图标
					 * 
					 * @param edt
					 */
					private void draw_img(ClearEditText edt) {
						Drawable drawable = new BitmapDrawable();
						drawable = CompleteInformationActivity.this
								.getResources().getDrawable(
										R.drawable.error_icon);
						edt.setCompoundDrawablesWithIntrinsicBounds(drawable,
								null, null, null);

					}
				});

	}

	@Override
	public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == GET_FROM_FOLDER) {
				if (data != null) {
					String path = data.getStringExtra("pathCamera");
					startCropImageActivity(path);
				}
			}
			if (requestCode == TAKE_PHOTO) {
				if (data != null) {
					String path = data.getStringExtra("pathCamera");
					startCropImageActivity(path);
				}
			} else if (requestCode == FLAG_MODIFY_FINISH
					&& resultCode == RESULT_OK) {
				if (data != null) {
					final String path = data.getStringExtra("path");

					Log.v("result", "PHOTO_INDEX = " + Constant.PHOTO_INDEX);
					if (Constant.PHOTO_INDEX == 0) {
						mBitmap1 = BitmapFactory.decodeFile(path);
						iv_front_photo.setImageBitmap(mBitmap1);
						// iv_front_photo.setBackground(new BitmapDrawable(
						// mBitmap1));
						uploadPic(1, mBitmap1);
					} else if (Constant.PHOTO_INDEX == 1) {
						mBitmap2 = BitmapFactory.decodeFile(path);
						// iv_card_photo
						// .setBackground(new BitmapDrawable(mBitmap2));
						iv_card_photo.setImageBitmap(mBitmap2);
						uploadPic(2, mBitmap2);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载图片
	 * 
	 * @param url
	 */
	public void load_img(final String type, String url, final ImageView layout) {
		ImageLoader loader = ImageLoader.getInstance();
		loader.loadImage(url, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
				if (type.equals("hand")) {

					mBitmap1 = bitmap;
					layout.setBackground(new BitmapDrawable(bitmap));

				} else if (type.equals("front")) {
					mBitmap2 = bitmap;
					layout.setBackground(new BitmapDrawable(bitmap));

				}

				checkStatus();

			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				// TODO Auto-generated method stub

			}
		});
		// loader.displayImage("http://zd-user-icon-dev.oss-cn-hangzhou.aliyuncs.com/"+url,
		// img);
	}

	/**
	 * 提交实名认证
	 * 
	 * @param v
	 */
	public void submit_Auth(View v) {
		String strName;
		String strId;
		RegisterInfo registerInfo = (RegisterInfo) getIntent()
				.getSerializableExtra("register");
		if (myapp.m_userInfo.verifyStatus == 4) {
			strName= tv_name_auth_manager.getText().toString();
			strId = tv_id_auth_manager.getText().toString();
		}else {
			strName= edt_name_auth_manager.getText().toString();
			strId = edt_id_auth_manager.getText().toString();
		}

		if (!CommandTools.personIdValidation(strId)) {
			VoiceHint.playErrorSounds();
			CommandTools.showToast("身份证无效");
			return;
		}
		if (mBitmap1 == null) {
			CommandTools.showToast("手持身份证照必须上传");
			return;
		}
		if (mBitmap2 == null) {
			CommandTools.showToast("身份证正面照必须上传");
			return;
		}
		JSONObject jsonObject = new JSONObject();
		try {

			jsonObject.put("idNo", strId);
			jsonObject.put("realName", strName);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		CustomProgress.showDialog(mContext, "数据提交中", false, null);
		RequestUtilNet.postDataToken(CompleteInformationActivity.this, Constant.CommitCertification, jsonObject,
				new RequestCallback() {
					@Override
					public void callback(int success, String remark, JSONObject jsonObject) {
						CustomProgress.dissDialog();
						try {
							Log.i(TAG, "=======" + jsonObject);
							CommandTools.showToast(remark);
							if (success == 0) {
								mBitmap1 = null;
								mBitmap2 = null;
								myapp.m_userInfo.verifyStatus = 1;
								IndexActivity.setTabXiaoPao();
//								startActivity(new Intent(CompleteInformationActivity.this, MainActivity.class));
//								finish();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});

	}

	/**
	 * 实名证件图片上传接口
	 */
	@SuppressWarnings("deprecation")
	private void uploadPic(final int type, Bitmap bitmap) {

		if (tv_choice_info.getText().toString().trim().equals("身份证")) {
			certificationType = "1";
		} else {
			certificationType = "2";
		}
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type + "");
			jsonObject.put("fileName", "certification.JPEG");
			jsonObject.put("sourceId", "1");
			jsonObject.put("certificationType", certificationType);
			jsonObject.put("file",
					URLEncoder.encode(CommandTools.bitmap2String(bitmap, 100)));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(mContext, Constant.Upload_AuthPhoto_url,
				jsonObject, new RequestCallback() {

					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						try {
							CommandTools.hidenKeyboars(mContext,
									edt_id_auth_manager);
							String strRemark = jsonObject.getString("message");
							CommandTools.showToast(strRemark);
						} catch (JSONException e) {
							e.printStackTrace();
						}

						initLeftImage();

					}
				});
	}

	@Override
	protected void onResume() {
		initLeftImage();
		getPersonalMessage();
		super.onResume();
	}

	/**
	 * 更新上传后照片显示
	 */
	@SuppressWarnings("deprecation")
	private void initLeftImage() {

		if (mBitmap1 != null) {
			iv_front_photo.setBackground(new BitmapDrawable(mBitmap1));
			iv_front_photo_error_img.setVisibility(View.GONE);
		} else if (mBitmap1 == null) {
			// img1.setImageResource(R.drawable.img_register_left);
			// layoutMyself_1.setVisibility(View.GONE);
		}

		if (mBitmap2 != null) {
			iv_card_photo.setBackground(new BitmapDrawable(mBitmap2));
			iv_card_photo_error_img.setVisibility(View.GONE);
		} else if (mBitmap2 == null) {
		}

		checkStatus();
	}

	/**
	 * 检查数据录入是否完整 修改注册、确定、下一步按钮状态
	 */
	private void checkStatus() {
		String tv_id;
		String tv_name;
		if (myapp.m_userInfo.verifyStatus == 4) {
			tv_id = tv_id_auth_manager.getText().toString();
			tv_name = tv_name_auth_manager.getText().toString();
		} else {
			tv_id = edt_id_auth_manager.getText().toString();
			tv_name = edt_name_auth_manager.getText().toString();
		}

		if (tv_name.length() == 0 || tv_id.length() == 0 || mBitmap1 == null
				|| mBitmap2 == null) {
			auth_manager_btn_next
					.setBackgroundResource(R.drawable.register_valid_gray);
			auth_manager_btn_next.setClickable(false);
		} else {
			auth_manager_btn_next
					.setBackgroundResource(R.drawable.register_valid);
			auth_manager_btn_next.setClickable(true);
		}
	}

	/**
	 * 对TextView进行录入监听
	 * 
	 * @param edt
	 */
	private void setEditableListener(EditText edt) {

		edt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

				checkStatus();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
	}

	/**
	 * 裁剪图片
	 * 
	 * @param path图片路径
	 */
	private void startCropImageActivity(String path) {

		Intent intent = new Intent(mContext, CutPictureActivity.class);
		intent.putExtra("path", path);
		getParent().startActivityForResult(intent, FLAG_MODIFY_FINISH);
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	protected void getFromCamera() {

		// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
		// Environment.getExternalStorageDirectory(), TMP_PATH)));
		// startActivityForResult(intent, TAKE_PHOTO);

		// Intent intent=new Intent(this,MyCameraActivity.class);
		Intent intent = new Intent(
				this,
				com.zhiduan.crowdclient.photoalbum.camera.MyCameraActivity.class);
		getParent().startActivityForResult(intent, TAKE_PHOTO);
	}

	/**
	 * 从本地手机中选择图片
	 */
	private void getFromFolder() {

		// Intent intent = new Intent();
		// intent.setAction(Intent.ACTION_PICK);
		// intent.setType("image/*");
		// startActivityForResult(intent, GET_FROM_FOLDER);

		Intent intent = new Intent(mContext, MyPhotoAlbumActivity.class);
		startActivityForResult(intent, GET_FROM_FOLDER);
	}

	/**
	 * 上传自拍照
	 * 
	 * @param v
	 */
	public void photoMySelf(View v) {

		Constant.PHOTO_INDEX = 0;
		CommandTools.hidenKeyboars(mContext, v);
		new PopupWindows(mContext, layout);
	}

	/**
	 * 上传正面照
	 * 
	 * @param v
	 */
	public void photoFront(View v) {

		Constant.PHOTO_INDEX = 1;
		CommandTools.hidenKeyboars(mContext, v);
		new PopupWindows(mContext, layout);
	}

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			super(mContext);

			View view = View
					.inflate(mContext, R.layout.item_popupwindows, null);
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			getBackground().setAlpha(80);
			setAnimationStyle(R.style.AnimationFade);
			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setFocusable(true);
			setOutsideTouchable(false);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			RelativeLayout layout = (RelativeLayout) view
					.findViewById(R.id.layout_pop_window);
			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);

			layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dismiss();
				}
			});
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					getFromCamera();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					getFromFolder();
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

	/**
	 * 获取个人信息
	 */
	private void getPersonalMessage() {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("userType", "1");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postData(CommandTools.getGlobalActivity(),
				Constant.getUserInfo_url, jsonObject, new MyCallback() {

					@Override
					public void callback(JSONObject jsonObject) {
						try {
							jsonObject = jsonObject.optJSONObject("data");

							MyApplication myApp = MyApplication.getInstance();
							myApp.m_userInfo.m_strUserName = jsonObject
									.optString("realName");
							myApp.m_userInfo.m_strUserHeadPic = jsonObject
									.optString("icon");
							myApp.m_userInfo.m_user_income = jsonObject
									.optLong("totalIncome");
							myApp.m_userInfo.verifyStatus = jsonObject
									.optInt("state");
							myApp.m_userInfo.m_strUserSign = jsonObject
									.optString("signNumber");
							myApp.m_userInfo.m_strUserGender = jsonObject
									.optString("gender");
							myApp.m_userInfo.m_nick_name = jsonObject
									.optString("nickname");
							Logs.i("zdkj--sfz---", "sfz = "
									+ jsonObject
									.optString("idNo"));
							int state=jsonObject.optInt("state");
							if (state==4) {
								edt_id_auth_manager.setVisibility(View.GONE);
								edt_name_auth_manager.setVisibility(View.GONE);
								tv_id_auth_manager.setVisibility(View.VISIBLE);
								tv_name_auth_manager.setVisibility(View.VISIBLE);
								tv_id_auth_manager.setText(jsonObject
										.optString("idNo"));
								tv_name_auth_manager.setText(jsonObject
										.optString("realName"));
							}
							
							// load_img(jsonObject.optString("icon"));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

}
