package com.zhiduan.crowdclient.im;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.activity.MainActivity;
import com.zhiduan.crowdclient.data.EvaluateInfo;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.OrderService;
import com.zhiduan.crowdclient.service.UserService;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.GlobalInstanceStateHelper;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;
import com.zhiduan.crowdclient.view.XCRoundRectImageView;

/**
 * Description 个人主页
 * Author: hexiuhui
 */
public class PersonalHomepageActivity extends BaseActivity implements IXListViewListener{

	private XCRoundRectImageView mUserIcon;
	private TextView mUserNameTxt;
	private ImageView mUserSexImg;
	private TextView evaluate_count_txt;
	private RatingBar evaluate_grade;
	
	private int refresh = 0;
	private int task_pageNumber = 1;
	private LoadTextNetTask mTaskRequestGetOrderTask;
	
	private DropDownListView mEvaluateListView;
	private EvaluateAdapter mEvaluateAdapter;
	private List<EvaluateInfo> mEvaluateData = new ArrayList<EvaluateInfo>();
	
	private int appraiserOther;
	
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_personal_homepage, PersonalHomepageActivity.this);
		setTitle("个人主页");
	}

	@Override
	public void initView() {
		mUserIcon = (XCRoundRectImageView) findViewById(R.id.user_icon);
		mUserNameTxt = (TextView) findViewById(R.id.user_name);
		mUserSexImg = (ImageView) findViewById(R.id.user_sex);
		evaluate_grade = (RatingBar) findViewById(R.id.evaluate_grade);
		evaluate_count_txt = (TextView) findViewById(R.id.evaluate_count_txt);
		
		mEvaluateListView = (DropDownListView) findViewById(R.id.lv_evaluate_list);
		mEvaluateListView.setPullLoadEnable(true);
		mEvaluateListView.setPullRefreshEnable(true);
		mEvaluateListView.setXListViewListener(this);
	}

	@Override
	public void initData() {
		appraiserOther = getIntent().getIntExtra("appraiserOther", 0);
		String userIconUrl = getIntent().getStringExtra("userIcon");
		String userSex = getIntent().getStringExtra("userSex");
		String userName = getIntent().getStringExtra("userName");
		
		mUserNameTxt.setText(userName);
		
		if (userSex.equals("p_gender_male")) {
			mUserSexImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.profile_boy));
		} else if (userSex.equals("p_gender_female")) {
			mUserSexImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.profile_girl));
		} else {
			mUserSexImg.setVisibility(View.GONE);
		}
		
		if (userIconUrl.equals("")) {
			if (userSex.equals("p_gender_female")) {
				mUserIcon.setImageResource(R.drawable.female);
			} else{
				mUserIcon.setImageResource(R.drawable.male);
			}
		} else {
			MyApplication.getInstance().getImageLoader().displayImage(userIconUrl, mUserIcon, MyApplication.getInstance().getOptions(), null);
		}
		
		mEvaluateAdapter = new EvaluateAdapter(PersonalHomepageActivity.this, mEvaluateData);
		mEvaluateListView.setAdapter(mEvaluateAdapter);
	}
	
	public void onResume() {
		super.onResume();
		
		getOneData();
	}
	
	protected LoadTextNetTask requestGetOrderData(int pageNumber) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestGetOrderTask = null;

				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONObject dataObj = jsonObj.getJSONObject("data");
							JSONArray jsonArray = dataObj.getJSONArray("responseDto");

							JSONObject jsonObject2 = dataObj.getJSONObject("orderAppraiseSummary");
							int appraiseNums = jsonObject2.getInt("appraiseNums");
							int avgScore = jsonObject2.getInt("avgScore");
							
							evaluate_count_txt.setText("全部评价(" + appraiseNums + "次)");
							evaluate_grade.setRating(Float.parseFloat(avgScore/2 + ""));
							
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								String appraiserName = jsonObject.getString("appraiserName");
								String appraiserGender = jsonObject.getString("appraiserGender");
								double overallScore = jsonObject.getDouble("overallScore");
								String overallDesc = jsonObject.getString("overallDesc");
								String appraiserIcon = jsonObject.getString("icon");
								
								EvaluateInfo info = new EvaluateInfo();
								info.setAppraiserName(appraiserName);
								info.setAppraiserGender(appraiserGender);
								info.setOverallScore(overallScore);
								info.setEvaluateUrl(appraiserIcon);
								info.setEvaluateContent(overallDesc);
								
								mEvaluateData.add(info);
							}

							mEvaluateAdapter.notifyDataSetChanged();

							refreshInit();

							if (jsonArray.length() < 10) {
								mEvaluateListView.setLoadHide();
							} else {
								mEvaluateListView.setLoadShow();
							}
						} else {
							String message = jsonObj.getString("message");
							// CommandTools.showToast(message);
							refreshInit();

							String code = jsonObj.getString("code");
							if (code.equals("105")) {
								mEvaluateData.clear();
								mEvaluateAdapter.notifyDataSetChanged();
								mEvaluateListView.setPullLoadEnable(false);
							}
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(PersonalHomepageActivity.this);
						e.printStackTrace();

						refreshInit();
					}
				} else {
					Util.showNetErrorMessage(PersonalHomepageActivity.this, result.m_nResultCode);
					refreshInit();
				}
			}
		};

		LoadTextNetTask task = UserService.getEvaluate(appraiserOther, pageNumber, listener, null);
		return task;
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (mTaskRequestGetOrderTask != null) {
			mTaskRequestGetOrderTask.cancel(true);
			mTaskRequestGetOrderTask = null;
		}
	}
	
	// 下拉刷新
	private void refreshInit() {
		mEvaluateListView.stopRefresh();
		mEvaluateListView.stopLoadMore();
		// 也可以用系统时间
		mEvaluateListView.setRefreshTime("刚刚");

		refresh = 0;
	}
	
	private void getOneData() {
		mEvaluateData.clear();
		task_pageNumber = 1;
		mTaskRequestGetOrderTask = requestGetOrderData(task_pageNumber);
	}
	
	@Override
	public void onRefresh() {
		if (refresh == 0) {
			refresh++;
			getOneData();
		}
	}

	@Override
	public void onLoadMore() {
		task_pageNumber++;

		mTaskRequestGetOrderTask = requestGetOrderData(task_pageNumber);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 被系统回收时保存myapplication全局变量值
		GlobalInstanceStateHelper.saveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// 恢复被系统回收后的myapplication值
		GlobalInstanceStateHelper.restoreInstanceState(this, savedInstanceState);

		initView();
	}
	
	class EvaluateAdapter extends BaseAdapter {

		private Context context;
		private List<EvaluateInfo> list;
		
		public EvaluateAdapter(Context context, List<EvaluateInfo> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.item_user_evaluate, null);
				holder = new ViewHolder();
				
				holder.tv_evaluate_icon = (XCRoundRectImageView) convertView.findViewById(R.id.tv_evaluate_icon);
				holder.tv_evaluate_name = (TextView) convertView.findViewById(R.id.tv_evaluate_name);
				holder.iv_evaluate_sex = (ImageView) convertView.findViewById(R.id.iv_evaluate_sex);
				holder.rb_evaluate_grade = (RatingBar) convertView.findViewById(R.id.rb_evaluate_grade);
				holder.tv_evaluate_content = (TextView) convertView.findViewById(R.id.tv_evaluate_content);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			EvaluateInfo evaluateInfo = list.get(position);
			
			if (evaluateInfo.getEvaluateUrl().equals("")) {
				if (evaluateInfo.getAppraiserGender().equals("p_gender_female")) {
					holder.tv_evaluate_icon.setImageResource(R.drawable.female);
				} else{
					holder.tv_evaluate_icon.setImageResource(R.drawable.male);
				}
			} else {
				MyApplication.getInstance().getImageLoader().displayImage(evaluateInfo.getEvaluateUrl(), holder.tv_evaluate_icon, MyApplication.getInstance().getOptions(), null);
			}
			
			holder.tv_evaluate_name.setText(evaluateInfo.getAppraiserName());
			
			if (evaluateInfo.getAppraiserGender().equals("p_gender_male")) {
				holder.iv_evaluate_sex.setImageDrawable(mContext.getResources().getDrawable(R.drawable.profile_boy));
			} else if (evaluateInfo.getAppraiserGender().equals("p_gender_female")) {
				holder.iv_evaluate_sex.setImageDrawable(mContext.getResources().getDrawable(R.drawable.profile_girl));
			} else {
				holder.iv_evaluate_sex.setVisibility(View.GONE);
			}
			
			holder.rb_evaluate_grade.setRating(Float.parseFloat(evaluateInfo.getOverallScore() + ""));
			holder.tv_evaluate_content.setText(evaluateInfo.getEvaluateContent());
			
			return convertView;
		}

		class ViewHolder {
			XCRoundRectImageView tv_evaluate_icon;
			TextView tv_evaluate_name;
			ImageView iv_evaluate_sex;
			RatingBar rb_evaluate_grade;
			TextView tv_evaluate_content;
		}
	}
}
