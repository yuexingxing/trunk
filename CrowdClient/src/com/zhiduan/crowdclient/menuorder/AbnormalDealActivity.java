package com.zhiduan.crowdclient.menuorder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.activity.CutPictureActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.ImageActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * <pre>
 * Description	异常件处理
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-7-25 下午5:11:46
 * </pre>
 */
public class AbnormalDealActivity extends BaseActivity implements
		OnClickListener {
	private FrameLayout fl_abnormal_img;
	private Context mContext;
	private TextView tv_express_number, tv_express_company;
	private ImageView iv_abnormal, iv_abnormal_delete;
	private EditText edt_abnormal_reason;// 异常件原因编辑框
	private final String TMP_PATH = "temp.jpg";
	private GridView gridview;
	private String abnormal_type[];// 异常件原因
	private String abnormal_code[];// 异常件原因代码
	private final int TAKE_PHOTO = 1;
	/** 拍照选取标志 */
	private final int GET_FROM_FOLDER = 2;
	/** 本地图片选取标志 */
	private final int FLAG_MODIFY_FINISH = 3;
	/** 截取结束标志 */
	public static Bitmap mBitmap;// 异常图片
	private String path_img;// 异常图片路径
	private String express_name = "";// 快递公司
	private String orderId = "";// 运单号
	private String waybillId = "";// 运单Id
	private int check_position = -1;// 当前选中的异常原因
	MyGridAdapter adapter;// 异常件原因适配器
	private boolean is_selelect = false;
	private String abnormaltype = "";
	private LinearLayout ll_abnormal_menu;// 异常件类型布局
	private long problemId;// 异常件Id
	private String problemTypeCode = "";
	private Intent abnormal_intent;
	private String abnormal_url;// 原图路径
	private String problem_reason;
	private String waybillNo = "";
	private String thumbnailUrl = "";// 缩略图路径
	private LinearLayout ll_photo;
	private String order_type;
	private int maxLength = 50;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_abnormal_deal, this);
		mContext = this;
		setTitle("异常件处理");
		setRightTitleText("提交");
		getAbnormalType(3);// 获取异常件类型
		abnormal_intent = getIntent();
		if (abnormal_intent != null) {
			order_type = abnormal_intent.getStringExtra("orderType");
			waybillNo = abnormal_intent.getStringExtra("waybillNo");
			problem_reason = abnormal_intent.getStringExtra("problemReason");
			abnormal_url = abnormal_intent.getStringExtra("imgUrl");
			abnormaltype = abnormal_intent.getStringExtra("abnormaltype");
			orderId = abnormal_intent.getStringExtra("orderId");
			express_name = abnormal_intent.getStringExtra("expressName");
			waybillId = abnormal_intent.getStringExtra("waybillId");
			problemTypeCode = abnormal_intent.getStringExtra("problemTypeCode");
			problemId = abnormal_intent.getLongExtra("problemId", 0);
			thumbnailUrl = abnormal_intent.getStringExtra("thumbnailUrl");
		}
	}

	/**
	 * 异常件提交
	 */
	@Override
	public void rightClick() {
		Log.i("zdkj", "==============点击提交");
		CustomProgress.showDialog(mContext, "正在提交", true, null);
		setButtonFalse();
		if (abnormaltype != null && abnormaltype.equals("return")) {
			Abnormal_Return();
		} else if (abnormaltype != null && abnormaltype.equals("edit")) {
			if (is_selelect == false) {
				Toast.makeText(mContext, "请选择异常情况", 0).show();
				setButtonTrue();
				CustomProgress.dissDialog();
				return;
			}
			Abnormal_Edit();
		} else {
			if (is_selelect == false) {
				Toast.makeText(mContext, "请选择异常情况", 0).show();
				setButtonTrue();
				CustomProgress.dissDialog();
				return;
			}
			Abnormal_Sign();
		}
	}

	@Override
	public void initView() {
		tvCount = (TextView) findViewById(R.id.tv_remark_number);
		ll_photo = (LinearLayout) findViewById(R.id.ll_photo);
		ll_abnormal_menu = (LinearLayout) findViewById(R.id.ll_abnormal_menu);
		gridview = (GridView) findViewById(R.id.gridview);
		fl_abnormal_img = (FrameLayout) findViewById(R.id.fl_abnormal_img);
		iv_abnormal_delete = (ImageView) findViewById(R.id.iv_abnormal_delete);
		tv_express_number = (TextView) findViewById(R.id.tv_express_number);
		tv_express_company = (TextView) findViewById(R.id.tv_express_company);
		iv_abnormal = (ImageView) findViewById(R.id.iv_abnormal);
		edt_abnormal_reason = (EditText) findViewById(R.id.edt_abnormal_reason);
		tv_express_number.isFocused();
		iv_abnormal_delete.setOnClickListener(this);

		edt_abnormal_reason.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				String strMsg = s.toString();
				if (strMsg.length() > maxLength) {

					mHandler.sendEmptyMessage(0x0022);
					tvCount.setText(maxLength + "/" + maxLength);
				} else {
					tvCount.setText(strMsg.length() + "/" + maxLength);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	public Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.what == 0x0022) {

				String str = edt_abnormal_reason.getText().toString();
				edt_abnormal_reason.setText(str.substring(0, maxLength));
				edt_abnormal_reason.setSelection(maxLength);

				CommandTools.showToast("输入字数达到上限");
			}
		}
	};
	private TextView tvCount;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("zdkj", "onresume");
		// if (mBitmap != null) {
		// iv_abnormal.setImageBitmap(mBitmap);
		// }

		if (Constant.mBitmap != null) {
			fl_abnormal_img.setVisibility(View.VISIBLE);
			iv_abnormal.setImageBitmap(Constant.mBitmap);
		}
	}

	/**
	 * 点击看大图
	 * 
	 * @param view
	 */
	public void ReciewPic(View view) {
		Intent intent = new Intent(mContext, ImageActivity.class);
		intent.putExtra("path", path_img);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == GET_FROM_FOLDER && resultCode == RESULT_OK) {
			if (data != null) {
				Uri uri = data.getData();
				uri = CommandTools.geturi(data, mContext);// 解决方案

				if (!TextUtils.isEmpty(uri.getAuthority())) {
					Cursor cursor = getContentResolver().query(uri,
							new String[] { MediaStore.Images.Media.DATA },
							null, null, null);
					if (null == cursor) {
						CommandTools.showToast("图片没找到");
						return;
					}
					cursor.moveToFirst();
					path_img = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();
					fl_abnormal_img.setVisibility(View.VISIBLE);
					if (mBitmap != null) {
						mBitmap = null;
					}
					BitmapFactory.Options options=new BitmapFactory.Options();
					options.inSampleSize=2;
					mBitmap = BitmapFactory.decodeFile(path_img,options);
					Constant.mBitmap = CommandTools
							.zoomImage(mBitmap, 200, 200);
					iv_abnormal.setImageBitmap(CommandTools.zoomImage(mBitmap,
							200, 200));
				}
			} else {
				CommandTools.showToast("data == null");
			}
		}
		if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
			path_img = Environment.getExternalStorageDirectory() + "/"
					+ TMP_PATH;
			fl_abnormal_img.setVisibility(View.VISIBLE);
			if (mBitmap != null) {
				mBitmap = null;
			}
			// startCropImageActivity(path_img);
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inSampleSize=2;
			mBitmap = BitmapFactory.decodeFile(path_img,options);
			Constant.mBitmap = CommandTools.zoomImage(mBitmap, 200, 200);
			iv_abnormal.setImageBitmap(CommandTools
					.zoomImage(mBitmap, 200, 200));
		} else if (requestCode == FLAG_MODIFY_FINISH && resultCode == RESULT_OK) {

			if (data != null) {
				path_img = data.getStringExtra("path");
				fl_abnormal_img.setVisibility(View.VISIBLE);
				BitmapFactory.Options options=new BitmapFactory.Options();
				options.inSampleSize=2;
				mBitmap = BitmapFactory.decodeFile(path_img,options);
				iv_abnormal.setImageBitmap(mBitmap);
				// uploadPic(1, mBitmap);

			}
		}

	}

	@Override
	public void initData() {
		if (abnormaltype != null && abnormaltype.equals("return")) {
			setTitle("异常件退件");
			iv_abnormal_delete.setVisibility(View.GONE);
			ll_photo.setVisibility(View.GONE);
			down_img(abnormal_url, 1);
			down_img(thumbnailUrl, 0);
			edt_abnormal_reason.setHint("请说明退件原因");
			ll_abnormal_menu.setVisibility(View.GONE);
		} else if (abnormaltype != null && abnormaltype.equals("edit")) {
			setTitle("异常件编辑");
			down_img(abnormal_url, 1);
			down_img(thumbnailUrl, 0);
			ll_abnormal_menu.setVisibility(View.VISIBLE);
			if (problem_reason.equals("") || problem_reason == null) {
				edt_abnormal_reason.setHint("请说明退件原因");
			} else {
				edt_abnormal_reason.setText(problem_reason);
			}
			gridview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View v,
						int position, long id) {

					is_selelect = true;
					check_position = position;
					problemTypeCode = abnormal_code[position];
					adapter.notifyDataSetChanged();
				}
			});
		} else {
			ll_abnormal_menu.setVisibility(View.VISIBLE);
			gridview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View v,
						int position, long id) {
					is_selelect = true;
					check_position = position;
					problemTypeCode = abnormal_code[position];
					adapter.notifyDataSetChanged();
				}
			});

		}

		tv_express_company.setText(express_name);
		tv_express_number.setText(waybillNo);
	}

	/**
	 * 下载图片
	 */
	private void down_img(String url, final int type) {
		if (thumbnailUrl == null || thumbnailUrl.equals("")) {
			return;
		}
		ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {

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
				fl_abnormal_img.setVisibility(View.VISIBLE);
				if (type == 0) {
					iv_abnormal.setScaleType(ScaleType.FIT_XY);
					iv_abnormal.setImageBitmap(bitmap);
				}
				if (type == 1) {
					mBitmap = bitmap;
					saveBitmapFile(mBitmap);
				}
			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * 将图片保存本地
	 * 
	 * @param bitmap
	 */
	public void saveBitmapFile(Bitmap bitmap) {
		// 得到外部存储卡的路径
		String path = Environment.getExternalStorageDirectory().toString();
		File file = new File(path, "abnormal.jpg");// 将要保存图片的路径
		path_img = file.getAbsolutePath();
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 拍照
	 * 
	 * @param v
	 */
	public void TakePtoto(View v) {
		CommandTools.hidenKeyboars(mContext, v);
		new PopupWindows(mContext, iv_abnormal);
	}

	/**
	 * 异常件登记接口
	 */
	@SuppressWarnings("deprecation")
	private void Abnormal_Sign() {

		if (mBitmap == null) {
			CommandTools.showToast("照片不能为空");
			setButtonTrue();
			CustomProgress.dissDialog();
			return;
		}
		String strUrl = "";
		// file 图片base64编码 string 必填
		// fileName 文件名称 string 必填
		// orderId 订单号 number 必填
		// problemReason 异常件原因 string
		// problemTypeCode 问题件类型编码 string 必填
		// sourceId 来源id
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("fileName", "abnormal.jpeg");
			jsonObject.put("orderId", orderId);
			jsonObject.put("problemReason", edt_abnormal_reason.getText()
					.toString());
			jsonObject.put("problemTypeCode", problemTypeCode);
			jsonObject.put("sourceId", 1);
			jsonObject.put("file",
					URLEncoder.encode(CommandTools.bitmapToBase64(mBitmap)));

			if (!order_type.equals("") && order_type.equals(OrderUtil.PACKET)) {
				jsonObject.put("waybillId", waybillId);
				jsonObject.put("bizId", 11);
				strUrl = "waybill/problem/sign.htm";
			} else {
				strUrl = "waybill/problem/agentsign.htm";
			}
		} catch (JSONException e) {
			CustomProgress.dissDialog();
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject,
				new RequestCallback() {

					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						setButtonTrue();
						CustomProgress.dissDialog();
						if (success == 0) {
							CommandTools.showToast(remark);
							setResult(RESULT_OK);
							finish();
						} else {
							CommandTools.showToast(remark);

						}
					}
				});

	}

	/**
	 * 异常件退件接口
	 */
	private void Abnormal_Return() {
		String strUrl = "";
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("returnReason", edt_abnormal_reason.getText()
					.toString());
			jsonObject.put("problemId", problemId);
			if (!order_type.equals("") && order_type.equals(OrderUtil.PACKET)) {
				strUrl = "waybill/problem/return.htm";
			} else {
				strUrl = "waybill/problem/agentreturn.htm";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject,
				new RequestCallback() {

					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						setButtonTrue();
						CustomProgress.dissDialog();
						CommandTools.showToast(remark);
						if (success == 0) {
							finish();
						}
					}
				});

	}

	/**
	 * 异常件修改
	 */
	private void Abnormal_Edit() {
		if (mBitmap == null) {
			CommandTools.showToast("照片不能为空");
			return;
		}
		String strUrl = "";
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("fileName", "abnormal.jpeg");
			jsonObject.put("problemId", problemId);
			jsonObject.put("problemReason", edt_abnormal_reason.getText()
					.toString());
			jsonObject.put("problemTypeCode", problemTypeCode);
			jsonObject.put("sourceId", 1);
			if (mBitmap != null) {
				jsonObject
						.put("file", URLEncoder.encode(CommandTools
								.bitmapToBase64(mBitmap)));
			} else {
				jsonObject.put("file", "");
			}
			if (!order_type.equals("") && order_type.equals(OrderUtil.PACKET)) {
				strUrl = "waybill/problem/edit.htm";
			} else {
				strUrl = "waybill/problem/agentedit.htm";
			}

		} catch (JSONException e) {
			CustomProgress.dissDialog();
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject,
				new RequestCallback() {

					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						CustomProgress.dissDialog();
						setButtonTrue();
						CommandTools.showToast(remark);
						if (success == 0) {
							finish();
						}
					}
				});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}

		if (Constant.mBitmap != null) {
			Constant.mBitmap.recycle();
			Constant.mBitmap = null;
		}
	}

	/**
	 * 异常件类型查询接口
	 */
	public void getAbnormalType(int type) {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("problemTypeGroup", 3);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "waybill/problem/typequery.htm";
		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject,
				new RequestCallback() {

					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						if (success != 0) {
							CommandTools.showToast(remark);
							return;
						}
						JSONArray typeArray = jsonObject.optJSONArray("data");
						abnormal_type = new String[typeArray.length()];
						abnormal_code = new String[typeArray.length()];
						for (int i = 0; i < typeArray.length(); i++) {
							JSONObject typeObject = typeArray.optJSONObject(i);
							abnormal_type[i] = typeObject
									.optString("problemTypeName");
							abnormal_code[i] = typeObject
									.optString("problemTypeCode");
							Log.i("zdkj", "=====type===" + abnormal_type[i]);
							Log.i("zdkj", "=====type=abposition----=="
									+ abnormal_type[i]);
						}
						adapter = new MyGridAdapter(abnormal_type);
						gridview.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					}
				});

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
	 * 从本地手机中选择图片
	 */
	private void getFromFolder() {

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, GET_FROM_FOLDER);
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	protected void getFromCamera() {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
				Environment.getExternalStorageDirectory(), TMP_PATH)));
		startActivityForResult(intent, TAKE_PHOTO);
	}

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			super(mContext);

			View view = View
					.inflate(mContext, R.layout.item_popupwindows, null);
			setAnimationStyle(R.style.AnimationFade);
			getBackground().setAlpha(80);

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
	 * gridview适配器
	 * 
	 */
	class MyGridAdapter extends BaseAdapter {
		private String type[];

		public MyGridAdapter(String abnormal[]) {
			this.type = abnormal;
		}

		@Override
		public int getCount() {
			return type.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup group) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mContext,
						R.layout.item_abnormal_grid, null);
				holder = new ViewHolder();
				holder.tv_abnormal_type = (TextView) convertView
						.findViewById(R.id.tv_abnormal_type);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (problemTypeCode != null && problemTypeCode != "") {
				if (problemTypeCode.equals(abnormal_code[position])) {
					holder.tv_abnormal_type
							.setBackgroundResource(R.drawable.shape_radius_abnormal_red);
					holder.tv_abnormal_type.setTextColor(getResources()
							.getColor(R.color.red));
					is_selelect = true;
				}
			}
			if (check_position != -1) {
				if (position == check_position) {
					holder.tv_abnormal_type
							.setBackgroundResource(R.drawable.shape_radius_abnormal_red);
					holder.tv_abnormal_type.setTextColor(getResources()
							.getColor(R.color.red));
				} else {
					holder.tv_abnormal_type
							.setBackgroundResource(R.drawable.shape_radius_abnormal_gray);
					holder.tv_abnormal_type.setTextColor(getResources()
							.getColor(R.color.text_black));
				}
			}
			holder.tv_abnormal_type.setText(type[position]);

			return convertView;
		}

		class ViewHolder {
			TextView tv_abnormal_type;

		}
	}

	/**
	 * 图片删除
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_abnormal_delete:
			fl_abnormal_img.setVisibility(View.GONE);
			iv_abnormal.setImageBitmap(null);
			path_img = "";
			mBitmap = null;
			break;
		default:
			break;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}

}
