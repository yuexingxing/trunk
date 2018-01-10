package com.zhiduan.crowdclient.task;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.activity.CutPictureActivity;
import com.zhiduan.crowdclient.adapter.TaskAlbumAdapter;
import com.zhiduan.crowdclient.adapter.TaskAlbumAdapter.OnBottomClickListener;
import com.zhiduan.crowdclient.data.TaskDetailInfo;
import com.zhiduan.crowdclient.data.TaskImg;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.service.TaskService;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.ImageActivity;

import android.R.integer;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * <pre>
 * Description	任务相册
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-18 上午10:44:14
 * </pre>
 */
public class TaskAlbumActivity extends BaseActivity {
	private Context mContext;
	private GridView task_gridview;
	private  List<TaskImg> imgs_task = new ArrayList<TaskImg>();
	public static List<TaskImg>pager_task=new ArrayList<TaskImg>();
	private TaskImg taskImg;
	private TaskAlbumAdapter adapter;
	private final int TAKE_PHOTO = 1;
	/** 拍照选取标志 */
	private final int GET_FROM_FOLDER = 2;
	/** 本地图片选取标志 */
	private final int FLAG_MODIFY_FINISH = 3;
	/** 截取结束标志 */
	private final String TMP_PATH = "temp.jpg";
	public static Bitmap mBitmap;// 图片
	private String path_img;// 图片路径
	public static int axp_i = 0;
	private LoadTextNetTask mTaskAssigned;
	private String task_orderid;
	private Intent task_intent;
	private int task_status; 
	
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_task_album, this);
		mContext = this;
		setTitle("相册库");
		hidenRightTitle();

	}

	@Override
	public void initView() {
		task_gridview = (GridView) findViewById(R.id.task_gridview);
		if (getIntent() != null) {
			task_intent = getIntent();
			task_status=task_intent.getIntExtra("task_status", 8);
			task_orderid = task_intent.getStringExtra("orderid");
		}
		if (task_orderid != null && !task_orderid.equals("")) {
			mTaskAssigned = GetTaskDetail(task_orderid);
		}
		adapter = new TaskAlbumAdapter(mContext, imgs_task,task_status);
		task_gridview.setAdapter(adapter);
//		if (axp_path != null) {
//			getFiles(axp_path);
//		}
		

		
	}

	private void AddFirstPhoto() {
		Drawable d = mContext.getResources().getDrawable(R.drawable.list_photo);
		mBitmap = drawableToBitmap(d);
		int w = mContext.getResources().getDimensionPixelOffset(
				R.dimen.thumnail_default_width);
		int h = mContext.getResources().getDimensionPixelSize(
				R.dimen.thumnail_default_height);
		Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(mBitmap, w, h);
		taskImg = new TaskImg();
		taskImg.setTaskBitmap(thumBitmap);
		imgs_task.add(taskImg);
	}
	public LoadTextNetTask GetTaskDetail(String orderid) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {
				CustomProgress.dissDialog();
				mTaskAssigned = null;
				imgs_task.clear();
				pager_task.clear();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(
								mresult.m_strContent);
						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONObject detailObject = jsonObj
									.optJSONObject("data");
					
							JSONArray image_array = detailObject
									.optJSONArray("submitImage");
							if (task_status==Constant.TASK_UNDER_WAY_DETAIL) {
								AddFirstPhoto();
							}
							if (image_array.length() != 0) {
								Log.i("zdkj","图片张数==="+image_array.length());
								for (int i = 0; i < image_array.length(); i++) {
									JSONObject imageObject = image_array
											.getJSONObject(i);
									
									taskImg=new TaskImg();
									taskImg.setId(imageObject.optInt("id"));
									taskImg.setNormalUrl(imageObject.optString("normalUrl"));
									taskImg.setThumbnailUrl(imageObject.optString("thumbnailUrl"));
									taskImg.setIspass(imageObject.optInt("isPass"));
									imgs_task.add(taskImg);
									pager_task.add(taskImg);
								}
							}
							adapter.notifyDataSetChanged();
				
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(TaskAlbumActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(TaskAlbumActivity.this,
							result.m_nResultCode);
				}
			}
		};

		CustomProgress
				.showDialog(TaskAlbumActivity.this, "数据获取中", false, null);
		LoadTextNetTask task = TaskService.QueryTaskDetail(orderid, listener,
				null);
		return task;
	}
	@Override
	public void initData() {
		adapter.setOnBottomClickListener(new OnBottomClickListener() {

			@Override
			public void onBottomClick(View v, int position) {
				mTaskAssigned=DeleteTaskImg(imgs_task.get(position).getId());

			}
		});
		task_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				if (task_status==Constant.TASK_AUDIT_DETAIL||task_status==Constant.TASK_FINISH_DETAIL) {
					ReciewPic(imgs_task.get(position).getNormalUrl(),position);
					return;
				}
				if (position == 0) {
					CommandTools.hidenKeyboars(mContext, view);
					new PopupWindows(mContext, view);
				}else {
					ReciewPic(imgs_task.get(position).getNormalUrl(),position);
				}
				

			}
		});

	}
	public LoadTextNetTask DeleteTaskImg(int imageid) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {
				CustomProgress.dissDialog();
				mTaskAssigned = null;

				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(
								mresult.m_strContent);

						Log.i("zdkj", jsonObj + "--");

						int success = jsonObj.getInt("success");
						if (success == 0) {
							mTaskAssigned=GetTaskDetail(task_orderid);
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(TaskAlbumActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(TaskAlbumActivity.this,
							result.m_nResultCode);
				}
			}
		};

		CustomProgress.showDialog(TaskAlbumActivity.this, "正在提交", false, null);
		LoadTextNetTask task = TaskService.DeleteTaskImg(imageid, listener, null);
		return task;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == GET_FROM_FOLDER && resultCode == RESULT_OK) {
		      
			if (data != null) {
				Uri uri = data.getData();
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
					startCropImageActivity(path_img);

				} else {
					startCropImageActivity(uri.getPath());
				}
			}
	           
			}
		
		if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
			path_img = Environment.getExternalStorageDirectory() + "/"
					+ TMP_PATH;
			if (mBitmap != null) {
				mBitmap = null;
			}
			mBitmap = BitmapFactory.decodeFile(path_img);
			mBitmap=CommandTools.ratio(mBitmap, 500, 500);
			mTaskAssigned = UploadImg(
					URLEncoder.encode(CommandTools.bitmapToBase64(mBitmap)),
					"zdkj.JPEG", task_orderid);
		} else if (requestCode == FLAG_MODIFY_FINISH && resultCode == RESULT_OK) {

			if (data != null) {
				path_img = data.getStringExtra("path");
				mBitmap = BitmapFactory.decodeFile(path_img);
				if (mBitmap != null) {
					mBitmap = null;
				}
				mBitmap = BitmapFactory.decodeFile(path_img);
				mTaskAssigned = UploadImg(
						URLEncoder.encode(CommandTools.bitmapToBase64(mBitmap)),
						"zdkj.JPEG", task_orderid);
			}
		}

	}
	/**
	 * 点击看大图
	 * 
	 * @param view
	 */
	public void ReciewPic(String path_img,int position) {
		Intent intent = new Intent(mContext, PagerActivity.class);
		//intent.putExtra("path", path_img);
		intent.putExtra("index", position);
		startActivity(intent);
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

	public static Bitmap drawableToBitmap(Drawable drawable) {
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
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

	public LoadTextNetTask GetTaskDetail(int imgid) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				CustomProgress.dissDialog();
				mTaskAssigned = null;

				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Log.i("zdkj", jsonObj + "--");

						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONArray jsonArray = jsonObj.getJSONArray("data");
							
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(TaskAlbumActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(TaskAlbumActivity.this, result.m_nResultCode);
				}
			}
		};

		CustomProgress.showDialog(CommandTools.getGlobalActivity(), "数据获取中", false, null);
		LoadTextNetTask task = TaskService.DeleteTaskImg(imgid,listener, null);
		return task;
	}
	public LoadTextNetTask UploadImg(String file,String filename,String orderid) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				CustomProgress.dissDialog();
				mTaskAssigned = null;

				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Log.i("zdkj", jsonObj + "--");

						int success = jsonObj.getInt("success");
						if (success == 0) {
							mTaskAssigned = GetTaskDetail(task_orderid);
							
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(TaskAlbumActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(TaskAlbumActivity.this, result.m_nResultCode);
				}
			}
		};

		CustomProgress.showDialog(TaskAlbumActivity.this, "图片正在上传", false, null);
		LoadTextNetTask task = TaskService.UploadTaskImg(file, filename, orderid,listener, null);
		return task;
	}
	
}
