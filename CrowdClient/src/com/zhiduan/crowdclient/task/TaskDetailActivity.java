package com.zhiduan.crowdclient.task;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.CutPictureActivity;
import com.zhiduan.crowdclient.activity.LoginActivity;
import com.zhiduan.crowdclient.activity.MainActivity;
import com.zhiduan.crowdclient.completeinformation.SchoolDataActivity;
import com.zhiduan.crowdclient.data.TaskDetailInfo;
import com.zhiduan.crowdclient.data.TaskImg;
import com.zhiduan.crowdclient.menucenter.UserInfoActivity;
import com.zhiduan.crowdclient.menuindex.IndexActivity;
import com.zhiduan.crowdclient.menuorder.EditRemarkActivity;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.OrderService;
import com.zhiduan.crowdclient.service.TaskService;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.ScreenUtil;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CallPhoneDialog;
import com.zhiduan.crowdclient.view.CallPhoneDialog.ResultCallback;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.GeneralDialog.TwoButtonListener;
import com.zhiduan.crowdclient.view.ObservableScrollView;
import com.zhiduan.crowdclient.view.ObservableScrollView.ScrollViewListener;
import com.zhiduan.crowdclient.view.TaskRemarkDialog;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;
import com.zhiduan.crowdclient.view.dialog.DialogUtils.DialogCallback;

/**
 * 
 * <pre>
 * Description	任务详情
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-17 下午3:08:05
 * </pre>
 */
public class TaskDetailActivity extends Activity implements OnClickListener, ScrollViewListener {
	private HorizontalListView horizon_listview;
	HorizontalListViewAdapter hListViewAdapter;
	private RelativeLayout rl_task_bottom, rl_album;
	private LinearLayout rl_task_photo, rl_remark, rl_grade;
	private LinearLayout rl_task_content;
	private LinearLayout detail_title;
	private RatingBar payment_grade;
	private TextView review_photo, tv_btn_cancel, tv_btn_commit, tv_task_time,
	tv_grade_accrued, tv_grade_standard, tv_end_time;

	View olderSelectView = null;
	// private DetailWebView web_task;
	private Context mContext;
	private LinearLayout iv_back_img;
	private final int TAKE_PHOTO = 1;
	/** 拍照选取标志 */
	private final int GET_FROM_FOLDER = 2;
	/** 本地图片选取标志 */
	private final int FLAG_MODIFY_FINISH = 3;
	/** 截取结束标志 */
	private final String TMP_PATH = "temp.jpg";
	public static Bitmap mBitmap;// 图片
	private String path_img;// 图片路径
	private List<Bitmap> imgs = new ArrayList<Bitmap>();
	private List<TaskImg> task_imgs = new ArrayList<TaskImg>();
	private TaskImg taskImg;
	private int task_status;
	public static int task_pager_type;
	private ObservableScrollView slidingMenu;
	// private TextView tv_review_detail;
	// public static MyHandler handler;
	// private ListView list_task;
	private LoadTextNetTask mTaskAssigned;
	private String task_orderid;
	private Intent task_Intent;
	private TaskDetailInfo detailInfo;
	private TextView tv_task_title, tv_task_num, tv_task_boss, tv_task_orderID,
	tv_task_money, tv_task_require, tv_start_task_time, tv_descirbe;
	private ImageView iv_photo, iv_task_logo, iv_task_sex, title_right,
	iv_task_example;
	private TextView edt_task_rule;
	private TextView task_image_num;
	public static List<TaskImg> taskImgs = new ArrayList<TaskImg>();
	private TextView tv_task_remark;
	private Bitmap downBitmap;
	private LoadTextNetTask mTaskRequestRobOrder;
	private TaskRemarkDialog remarkDialog;
	private View view_grade_bottom;

	private int imageHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_task_detail);
		mContext = this;
		setImmerseLayout();
		initView();
		initData();
		// handler=new MyHandler();
		initListeners();
	}

	private void initListeners() {
		// 获取顶部图片高度后，设置滚动监听
		ViewTreeObserver vto = rl_task_content.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				rl_task_content.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				imageHeight = rl_task_content.getHeight();

				slidingMenu.setScrollViewListener(TaskDetailActivity.this);
			}
		});
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		view_grade_bottom=findViewById(R.id.view_grade_bottom);
		tv_grade_standard = (TextView) findViewById(R.id.tv_grade_standard);
		tv_grade_accrued = (TextView) findViewById(R.id.tv_grade_accrued);
		iv_task_logo = (ImageView) findViewById(R.id.iv_task_logo);
		tv_task_remark = (TextView) findViewById(R.id.tv_task_remark);
		payment_grade = (RatingBar) findViewById(R.id.payment_grade);
		rl_album = (RelativeLayout) findViewById(R.id.rl_album);
		rl_grade = (LinearLayout) findViewById(R.id.rl_grade);
		rl_task_photo = (LinearLayout) findViewById(R.id.rl_task_photo);
		title_right = (ImageView) findViewById(R.id.title_right);
		task_image_num = (TextView) findViewById(R.id.task_image_num);
		edt_task_rule = (TextView) findViewById(R.id.edt_task_rule);
		tv_descirbe = (TextView) findViewById(R.id.tv_descirbe);
		tv_task_title = (TextView) findViewById(R.id.tv_task_title);
		tv_task_num = (TextView) findViewById(R.id.tv_task_num);
		tv_task_boss = (TextView) findViewById(R.id.tv_task_boss);
		tv_task_orderID = (TextView) findViewById(R.id.tv_task_orderID);
		tv_task_money = (TextView) findViewById(R.id.tv_task_money);
		tv_task_require = (TextView) findViewById(R.id.tv_task_require);
		tv_start_task_time = (TextView) findViewById(R.id.tv_start_task_time);
		iv_task_sex = (ImageView) findViewById(R.id.iv_task_sex);
		iv_task_example = (ImageView) findViewById(R.id.iv_task_example);
		// tv_review_detail = (TextView) findViewById(R.id.tv_review_detail);
		slidingMenu = (ObservableScrollView) findViewById(R.id.expanded_menu);
		tv_end_time = (TextView) findViewById(R.id.tv_end_time);
		tv_task_time = (TextView) findViewById(R.id.tv_task_time);
		rl_task_bottom = (RelativeLayout) findViewById(R.id.rl_task_bottom);
		detail_title = (LinearLayout) findViewById(R.id.detail_title);
		rl_task_content = (LinearLayout) findViewById(R.id.rl_task_content);
		iv_back_img = (LinearLayout) findViewById(R.id.iv_back_img);
		tv_btn_commit = (TextView) findViewById(R.id.tv_btn_commit);
		tv_btn_cancel = (TextView) findViewById(R.id.tv_btn_cancel);
		rl_remark = (LinearLayout) findViewById(R.id.rl_remark);
		review_photo = (TextView) findViewById(R.id.review_photo);
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		// web_task = (DetailWebView) findViewById(R.id.web_task);
		edt_task_rule.setMovementMethod(new ScrollingMovementMethod());
		title_right.setOnClickListener(this);
		iv_back_img.setOnClickListener(this);
		iv_photo.setOnClickListener(this);
		tv_btn_cancel.setOnClickListener(this);
		tv_btn_commit.setOnClickListener(this);
		rl_remark.setOnClickListener(this);
		review_photo.setOnClickListener(this);
		slidingMenu.smoothScrollTo(0, 20);
		horizon_listview = (HorizontalListView) findViewById(R.id.horizon_listview);
		hListViewAdapter = new HorizontalListViewAdapter(getApplicationContext(), imgs);
		horizon_listview.setAdapter(hListViewAdapter);

		horizon_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(mContext, TaskAlbumActivity.class);
				intent.putExtra("orderid", task_orderid);
				intent.putExtra("task_status", task_status);
				startActivity(intent);

			}
		});

		tv_task_require.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				CopyDialog(tv_task_require);
				return false;
			}
		});
		edt_task_rule.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				CopyDialog(edt_task_rule);
				return false;
			}
		});
	}

	public void CopyDialog(final TextView textView) {
		GeneralDialog.showTwoButtonDialog(mContext, GeneralDialog.DIALOG_ICON_TYPE_8, "", "你确定要复制这条内容吗?", "取消", "确定", new TwoButtonListener() {

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

	private void initData() {
		if (getIntent() != null) {
			task_Intent = getIntent();
			task_status = task_Intent.getIntExtra("task_status", 8);
			task_orderid = task_Intent.getStringExtra("orderid");
		}
		if (task_status == Constant.TASK_AUDIT_DETAIL) {
			rl_task_bottom.setVisibility(View.GONE);
			iv_photo.setVisibility(View.GONE);
			tv_end_time.setText("审核中");
			tv_task_time.setVisibility(View.GONE);
		} else if (task_status == Constant.TASK_MAIN_DETAIL) {
			tv_btn_commit.setText("接任务");
			tv_btn_cancel.setVisibility(View.GONE);
			rl_task_photo.setVisibility(View.GONE);
			rl_album.setVisibility(View.GONE);
			rl_remark.setVisibility(View.GONE);
			title_right.setVisibility(View.GONE);
			// title_right.setImageResource(R.drawable.task_profile_a);
		} else if (task_status == Constant.TASK_FINISH_DETAIL) {
			rl_grade.setVisibility(View.VISIBLE);
			view_grade_bottom.setVisibility(View.VISIBLE);
			rl_task_bottom.setVisibility(View.GONE);
			iv_photo.setVisibility(View.GONE);
			tv_end_time.setText("已结算");
			tv_task_time.setVisibility(View.GONE);
		}
		task_pager_type=task_status;

	}

	public LoadTextNetTask GetMainTaskDetail(String orderid) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				CustomProgress.dissDialog();
				mTaskAssigned = null;
				imgs.clear();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);
						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONObject detailObject = jsonObj.optJSONObject("data");
							detailInfo = new TaskDetailInfo();
							detailInfo.setAuditRemark(detailObject.optString("auditRemark"));
							detailInfo.setOrderId(detailObject.optString("orderId") + "");
							detailInfo.setClaim(detailObject.optString("claim"));
							detailInfo.setCoverImage(detailObject.optString("coverImage"));
							detailInfo.setWinnerRemark(detailObject.optString("winnerRemark"));
							detailInfo.setCreateUserName(detailObject.optString("createUserName"));
							detailInfo.setDeadline(detailObject.optString("deadline"));
							detailInfo.setDescript(detailObject.optString("descript"));
							detailInfo.setEffectiveEndDate(detailObject.optString("effectiveEndDate"));
							detailInfo.setEffectiveStartDate(detailObject.optString("effectiveStartDate"));
							detailInfo.setIsImg(detailObject.optInt("isImg"));
							detailInfo.setOverallScore(detailObject.optString("overallScore"));
							detailInfo.setRule(detailObject.optString("rule"));
							detailInfo.setSex(detailObject.optString("sex"));
							detailInfo.setState(detailObject.optInt("state"));
							detailInfo.setTheme(detailObject.optString("theme"));

							if (detailObject.optString("sex").contains("female")) {
								iv_task_sex.setBackgroundResource(R.drawable.profile_girl);
							} else {
								iv_task_sex.setBackgroundResource(R.drawable.profile_boy);
							}

							if (detailObject.optString("coverImage") != null) {
								DownLoad_Img(detailObject.optString("coverImage"));
							}

							if (!TextUtils.isEmpty(detailObject.optString("winnerRemark"))) {
								tv_task_remark.setText(detailObject.optString("winnerRemark"));
							}

							JSONArray example_array = detailObject.optJSONArray("exampleImage");
							for (int i = 0; i < example_array.length(); i++) {
								JSONObject exampleObject = example_array.optJSONObject(i);
								if (!TextUtils.isEmpty(exampleObject.optString("normalUrl"))) {
									MyApplication.getInstance().getImageLoader().displayImage(exampleObject
											.optString("normalUrl"),
											iv_task_example);
								}
							}

							// 提取日期中的小时分钟
							SimpleDateFormat OldDateFormat = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat NewFormat = new SimpleDateFormat("MM-dd");
							String startDate = "";
							String endDate = "";
							try {
								startDate = OldDateFormat
										.format(OldDateFormat.parse(detailObject
												.optString("effectiveStartDate")));
								endDate = NewFormat
										.format(OldDateFormat.parse(detailObject
												.optString("effectiveEndDate")));
							} catch (java.text.ParseException e) {
								e.printStackTrace();
							}
							tv_start_task_time.setText(startDate + "~"
									+ endDate);
							try {
								String endtime = "";
								endtime = OldDateFormat
										.format(OldDateFormat.parse(detailObject
												.optString("effectiveEndDate")));
								if (task_status == Constant.TASK_MAIN_DETAIL
										|| task_status == Constant.TASK_UNDER_WAY_DETAIL) {
									tv_end_time.setText(endtime);
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
							edt_task_rule.setText(detailObject
									.optString("rule"));
							if (task_status == Constant.TASK_FINISH_DETAIL) {
								payment_grade
								.setRating(Float.parseFloat(detailObject
										.optString("overallScore")) / 2);
							}
							tv_task_title.setText(detailObject
									.optString("theme"));
							tv_task_num.setText(detailObject
									.optString("taskUnitNum"));
							tv_task_boss.setText(detailObject
									.optString("createUserName"));
							try {

								tv_task_money.setText(AmountUtils
										.changeF2Y(detailObject
												.optLong("reward")));
							} catch (Exception e) {
								// TODO: handle exception
							}
							tv_task_orderID.setText(detailObject
									.optString("orderId"));
							tv_task_require.setText(detailObject
									.optString("claim"));

							tv_descirbe.setText(detailObject
									.optString("descript"));
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(TaskDetailActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(TaskDetailActivity.this,
							result.m_nResultCode);
				}
			}
		};

		CustomProgress
		.showDialog(TaskDetailActivity.this, "数据获取中", false, null);
		LoadTextNetTask task = TaskService.QueryMianTaskDetail(orderid,
				listener, null);
		return task;
	}
	public LoadTextNetTask GetTaskDetail(String orderid) {
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
						imgs.clear();
						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONObject detailObject = jsonObj
									.optJSONObject("data");
							detailInfo = new TaskDetailInfo();
							detailInfo.setAuditRemark(detailObject
									.optString("auditRemark"));
							detailInfo.setOrderId(detailObject
									.optString("orderId") + "");
							detailInfo
							.setClaim(detailObject.optString("claim"));
							detailInfo.setCoverImage(detailObject
									.optString("coverImage"));
							detailInfo.setWinnerRemark(detailObject
									.optString("winnerRemark"));
							detailInfo.setCreateUserName(detailObject
									.optString("createUserName"));
							detailInfo.setDeadline(detailObject
									.optString("deadline"));
							detailInfo.setDescript(detailObject
									.optString("descript"));
							detailInfo.setEffectiveEndDate(detailObject
									.optString("effectiveEndDate"));
							detailInfo.setEffectiveStartDate(detailObject
									.optString("effectiveStartDate"));
							detailInfo.setIsImg(detailObject.optInt("isImg"));
							detailInfo.setOverallScore(detailObject
									.optString("overallScore"));
							detailInfo.setRule(detailObject.optString("rule"));
							detailInfo.setSex(detailObject.optString("sex"));
							detailInfo.setState(detailObject.optInt("state"));
							detailInfo
							.setTheme(detailObject.optString("theme"));
							if (detailObject.getInt("isImg")==0) {
								rl_task_photo.setVisibility(View.GONE);
								rl_album.setVisibility(View.GONE);
							}
							if (detailObject.optString("sex").contains("female")) {
								iv_task_sex.setBackgroundResource(R.drawable.profile_girl);
							}else {
								iv_task_sex.setBackgroundResource(R.drawable.profile_boy);
							}
							if (detailObject.optString("coverImage") != null) {
								DownLoad_Img(detailObject
										.optString("coverImage"));
							}
							if (!TextUtils.isEmpty(detailObject
									.optString("winnerRemark"))) {
								tv_task_remark.setText(detailObject
										.optString("winnerRemark"));
							}
							if (!TextUtils.isEmpty(detailObject
									.optString("finalMoney"))) {
								try {
									tv_grade_accrued
									.setText("¥"
											+ AmountUtils
											.changeF2Y(detailObject
													.optLong("finalMoney")));
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
							tv_grade_standard.setText(detailObject
									.optString("auditRemark") == null ? ""
											: detailObject.optString("auditRemark"));
							edt_task_rule.setText(detailObject
									.optString("rule"));
							// 提取日期中的小时分钟
							SimpleDateFormat OldDateFormat = new SimpleDateFormat(
									"yyyy-MM-dd");
							SimpleDateFormat NewFormat = new SimpleDateFormat(
									"MM-dd");
							String startDate = "";
							String endDate = "";
							try {
								startDate = OldDateFormat
										.format(OldDateFormat.parse(detailObject
												.optString("effectiveStartDate")));
								endDate = NewFormat
										.format(OldDateFormat.parse(detailObject
												.optString("effectiveEndDate")));
							} catch (java.text.ParseException e) {
								e.printStackTrace();
							}
							tv_start_task_time.setText(startDate + "~"
									+ endDate);
							try {
								String endtime = "";
								endtime = OldDateFormat
										.format(OldDateFormat.parse(detailObject
												.optString("effectiveEndDate")));
								if (task_status == Constant.TASK_MAIN_DETAIL
										|| task_status == Constant.TASK_UNDER_WAY_DETAIL) {
									tv_end_time.setText(endtime);
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
							if (task_status == Constant.TASK_FINISH_DETAIL) {
								payment_grade
								.setRating(Float.parseFloat(detailObject
										.optString("overallScore")) / 2);
							}
							JSONArray example_array = detailObject
									.optJSONArray("exampleImage");
							for (int i = 0; i < example_array.length(); i++) {
								JSONObject exampleObject = example_array
										.optJSONObject(i);
								if (!TextUtils.isEmpty(exampleObject
										.optString("normalUrl"))) {
									MyApplication
									.getInstance()
									.getImageLoader()
									.displayImage(
											exampleObject
											.optString("normalUrl"),
											iv_task_example);
								}
							}
							JSONArray image_array = detailObject
									.optJSONArray("submitImage");
							if (image_array != null
									&& image_array.length() != 0) {
								Log.i("zdkj", "图片张数===" + image_array.length());
								task_image_num.setText("("
										+ image_array.length() + "张" + ")");
								for (int i = 0; i < image_array.length(); i++) {
									JSONObject imageObject = image_array
											.getJSONObject(i);
									if (!TextUtils.isEmpty(imageObject
											.optString("thumbnailUrl"))) {
										MyApplication
										.getInstance()
										.getImageLoader()
										.loadImage(
												imageObject
												.optString("thumbnailUrl"),
												MyApplication
												.getInstance()
												.getOptions(),
												new ImageLoadingListener() {

													@Override
													public void onLoadingStarted(
															String arg0,
															View arg1) {
														// TODO
														// Auto-generated
														// method stub

													}

													@Override
													public void onLoadingFailed(
															String arg0,
															View arg1,
															FailReason arg2) {
														// TODO
														// Auto-generated
														// method stub

													}

													@Override
													public void onLoadingComplete(
															String arg0,
															View arg1,
															Bitmap arg2) {
														int w = mContext
																.getResources()
																.getDimensionPixelOffset(
																		R.dimen.thumnail_default_width);
														int h = mContext
																.getResources()
																.getDimensionPixelSize(
																		R.dimen.thumnail_default_height);
														Bitmap thumBitmap = ThumbnailUtils
																.extractThumbnail(
																		arg2,
																		w,
																		h);
														imgs.add(thumBitmap);
														hListViewAdapter
														.notifyDataSetChanged();
													}

													@Override
													public void onLoadingCancelled(
															String arg0,
															View arg1) {
														// TODO
														// Auto-generated
														// method stub

													}
												});
									}
								}

							} else {
								task_image_num.setText("(" + 0 + "张" + ")");
							}
							hListViewAdapter.notifyDataSetChanged();
							try {

								tv_task_money.setText(AmountUtils
										.changeF2Y(detailObject
												.optLong("reward")));
							} catch (Exception e) {
								// TODO: handle exception
							}
							tv_task_title.setText(detailObject
									.optString("theme"));
							tv_task_num.setText(detailObject
									.optString("taskUnitNum"));
							tv_task_boss.setText(detailObject
									.optString("createUserName"));
							tv_task_orderID.setText(detailObject
									.optString("orderId"));
							// tv_task_money.setText(detailObject.optString("taskUnitNum"));
							tv_task_require.setText(detailObject
									.optString("claim"));
							tv_descirbe.setText(detailObject
									.optString("descript"));
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(TaskDetailActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(TaskDetailActivity.this,
							result.m_nResultCode);
				}
			}
		};

		CustomProgress
		.showDialog(TaskDetailActivity.this, "数据获取中", false, null);
		LoadTextNetTask task = TaskService.QueryTaskDetail(orderid, listener,
				null);
		return task;
	}

	public LoadTextNetTask CommitTask(String orderid) {
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
							String message = jsonObj.getString("message");
							DialogUtils.showOneButtonDialog(mContext, GeneralDialog.DIALOG_ICON_TYPE_7, "", message,"确定", new DialogCallback() {

								@Override
								public void callback(int position) {
									// TODO Auto-generated method stub
									finish();
								}
							});
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(TaskDetailActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(TaskDetailActivity.this,
							result.m_nResultCode);
				}
			}
		};

		CustomProgress.showDialog(TaskDetailActivity.this, "正在提交", false, null);
		LoadTextNetTask task = TaskService.SubmitTask(orderid, listener, null);
		return task;
	}

	public LoadTextNetTask CancelTask(String orderid) {
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
							JSONObject detailObject = jsonObj
									.optJSONObject("data");
							finish();
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(TaskDetailActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(TaskDetailActivity.this,
							result.m_nResultCode);
				}
			}
		};

		CustomProgress.showDialog(TaskDetailActivity.this, "正在提交", false, null);
		LoadTextNetTask task = TaskService.CancelTask(orderid, listener, null);
		return task;
	}

	/**
	 * 弹备注信息对话框
	 */
	public void showRemarkDialog(String msg) {
		remarkDialog = new TaskRemarkDialog(mContext);
		remarkDialog.setMessage(msg);
		remarkDialog.setSingleOKButton("点击关闭", new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				remarkDialog.dismiss();
			}
		});
	}

	/**
	 * 给页面头部设置毛玻璃背景
	 */
	private void SetBlurBack(Bitmap blur_bitmap) {
		// Drawable d = mContext.getResources().getDrawable(R.drawable.female);
		// Bitmap blurBitmap = drawableToBitmap(d);
		blur_bitmap = CommandTools.fastblur(mContext, blur_bitmap, 100);
		Drawable drawable = new BitmapDrawable(blur_bitmap);
		rl_task_content.setBackgroundDrawable(drawable);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MyApplication.baseActivity = this;
		if (task_orderid != null && !task_orderid.equals("")) {
			if (task_status == Constant.TASK_MAIN_DETAIL) {
				mTaskAssigned = GetMainTaskDetail(task_orderid);
			} else {

				mTaskAssigned = GetTaskDetail(task_orderid);
			}
		}
	}

	protected void setImmerseLayout(View view) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			int statusBarHeight = ScreenUtil.getStatusBarHeight(this
					.getBaseContext());
			view.setPadding(0, statusBarHeight, 0, 0);
		}
	}

	protected void setImmerseLayout() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
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
					// int w = mContext.getResources().getDimensionPixelOffset(
					// R.dimen.thumnail_default_width);
					// int h = mContext.getResources().getDimensionPixelSize(
					// R.dimen.thumnail_default_height);
					// Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(
					// mBitmap, w, h);
					// imgs.add(thumBitmap);
					// hListViewAdapter.notifyDataSetChanged();

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
			mBitmap = CommandTools.ratio(mBitmap, 500, 500);
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

	protected LoadTextNetTask requestGobOrder(String orderId, final String type) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {
				mTaskRequestRobOrder = null;
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;

					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							CommandTools.showToast("抢单成功");
							finish();
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);

							String code = jsonObj.getString("code");

						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(TaskDetailActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(TaskDetailActivity.this,
							result.m_nResultCode);
				}
			}

		};

		CustomProgress.showDialog(TaskDetailActivity.this, "获取数据中...", false,
				null);
		LoadTextNetTask task = OrderService.grabOrder(orderId, type, listener,
				null);
		return task;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.review_photo:
			intent = new Intent(mContext, TaskAlbumActivity.class);
			intent.putExtra("orderid", task_orderid);
			intent.putExtra("task_status", task_status);
			startActivity(intent);
			break;
		case R.id.rl_remark:
			if (task_status == Constant.TASK_AUDIT_DETAIL || task_status == Constant.TASK_FINISH_DETAIL) {
				showRemarkDialog(tv_task_remark.getText().toString());
				return;
			} else {
				intent = new Intent(mContext, EditRemarkActivity.class);
				intent.putExtra("orderId", task_orderid);
				intent.putExtra("remarkType", OrderUtil.REMARK_TASK);
				if (!TextUtils.isEmpty(detailInfo.getWinnerRemark())) {
					intent.putExtra("content", tv_task_remark.getText().toString());
				} else {
					intent.putExtra("content", "");
				}
				startActivity(intent);
			}
			break;
		case R.id.tv_btn_cancel:
			if (task_orderid != null && !task_orderid.equals("")) {
				mTaskAssigned = CancelTask(task_orderid);
			}
			break;
		case R.id.tv_btn_commit:
			if (task_status == Constant.TASK_MAIN_DETAIL) {

				// 是否登录
				if (TextUtils.isEmpty(MyApplication.getInstance().m_userInfo.toKen)) {
					DialogUtils.showLoginDialog(TaskDetailActivity.this);
					return;
				} else if (MyApplication.getInstance().m_userInfo.verifyStatus == Constant.REVIEW_STATE_SUCCESS) {
					mTaskRequestRobOrder = requestGobOrder(task_orderid,
							Constant.TYPE_AGENT_SYSTEM);
				} else if (MyApplication.getInstance().m_userInfo.verifyStatus == Constant.REVIEW_STATE_NOT_SUBMIT) {
					DialogUtils.showAuthDialog(TaskDetailActivity.this);
					return;
				} else if (MyApplication.getInstance().m_userInfo.verifyStatus == Constant.REVIEW_STATE_SUBMITING) {
					DialogUtils.showReviewingDialog(TaskDetailActivity.this);
					return;
				} else {
					DialogUtils.showReviewingFailed(TaskDetailActivity.this, "");
				}
			} else {
				if (task_orderid != null && !task_orderid.equals("")) {
					mTaskAssigned = CommitTask(task_orderid);
				}
			}

			break;
		case R.id.iv_photo:
			CommandTools.hidenKeyboars(mContext, v);
			new PopupWindows(mContext, iv_photo);
			break;
		case R.id.iv_back_img:
			finish();
			break;
		case R.id.title_right:
			if (task_status == Constant.TASK_MAIN_DETAIL) {
				intent = new Intent(mContext, TaskUnderWayActivity.class);
				startActivity(intent);
				finish();
			} else {
				callPhone();
			}
			break;
		default:
			break;
		}
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

	public LoadTextNetTask UploadImg(String file, String filename,
			String orderid) {
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
							// JSONObject jsonArray =
							// jsonObj.getJSONObject("data");
							mTaskAssigned = GetTaskDetail(task_orderid);

						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(TaskDetailActivity.this);
						e.printStackTrace();
					}
				} else {
					Util.showNetErrorMessage(TaskDetailActivity.this,
							result.m_nResultCode);
				}
			}
		};

		CustomProgress.showDialog(TaskDetailActivity.this, "图片正在上传", false,
				null);
		LoadTextNetTask task = TaskService.UploadTaskImg(file, filename,
				orderid, listener, null);
		return task;
	}

	/**
	 * 下载服务器图片
	 */
	private void DownLoad_Img(String img_url) {

		if(TextUtils.isEmpty(img_url)){
			iv_task_logo.setImageResource(R.drawable.app_logo);
			return;
		}

		ImageLoader.getInstance().loadImage(img_url, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			}

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
				iv_task_logo.setImageBitmap(bitmap);
				//SetBlurBack(bitmap);
			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
			}
		});
	}

	/**
	 * 打电话
	 * 
	 * @param phone
	 */
	private void callPhone() {

		DialogUtils.showCallPhoneDialog(mContext, "400-166-1098", null);
	}

	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
		if (y <= 0) {  
			detail_title.setBackgroundColor(Color.argb((int) 0, 227, 29, 26));//AGB由相关工具获得，或者美工提供  
		} else if (y > 0 && y <= imageHeight) {  
			float scale = (float) y / imageHeight;  
			float alpha = (255 * scale);  
			// 只是layout背景透明(仿知乎滑动效果)  
			detail_title.setBackgroundColor(Color.argb((int) alpha, 227, 29, 26));  
		} else {  
			detail_title.setBackgroundColor(Color.argb((int) 255, 227, 29, 26));  
		}  
	}

	// class MyHandler extends Handler {
	// public MyHandler() {
	// }
	//
	// public MyHandler(Looper L) {
	// super(L);
	// }
	//
	// // 子类必须重写此方法，接受数据
	// @Override
	// public void handleMessage(Message msg) {
	// // TODO Auto-generated method stub
	// super.handleMessage(msg);
	// // 此处可以更新UI
	// int page = msg.what;
	// if (page == 1) {
	// tv_review_detail.setText("上拉查看图文详情");
	// } else if (page == 2) {
	// tv_review_detail.setText("下拉收起图文详情");
	// }
	// }
	// }
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//在继承BaseActivity的销毁方法中使用销毁Dialog
		//这样跨线程销毁Dialog，就不会出现not attached to window manager的异常

		GeneralDialog.dismiss();
	}
}
