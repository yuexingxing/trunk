package com.zhiduan.crowdclient.completeinformation;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.R.drawable;
import com.zhiduan.crowdclient.R.id;
import com.zhiduan.crowdclient.R.layout;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.activity.LoginActivity;
import com.zhiduan.crowdclient.activity.MainActivity;
import com.zhiduan.crowdclient.data.SchoolInfo;
import com.zhiduan.crowdclient.data.UserInfo;
import com.zhiduan.crowdclient.menucenter.UserInfoActivity;
import com.zhiduan.crowdclient.menuindex.IndexActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.LoginUtil;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.ClearEditText;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;
import com.zhiduan.crowdclient.view.dialog.DialogUtils.DialogCallback;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * <pre>
 * Description	完善资料-姓名-性别
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2017-1-7 下午1:53:34
 * </pre>
 */
public class PerfectNameActivity extends BaseActivity {

	private ClearEditText edt_user_name;
	private LinearLayout ll_sex_male, ll_sex_female;
	private boolean is_male = true;
	private ImageView iv_sex_male;
	private ImageView iv_sex_female;
	private Context mContext;
	private int school_id = 0;
	public final int SELECT_SCHOOL = 1001;
	public final int SCHOOL_ADDRESS = 1002;
	private SchoolInfo schoolInfo;
	private TextView tv_school_name;
	private int select_sex = 1;
	private UserInfo m_userInfo;
	MyApplication myApp = MyApplication.getInstance();

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentViewId(R.layout.activity_perfect_name, this);
		mContext = this;
		setTitle("完善信息");
	}

	/*	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		LoginUtil.getPersonalMessage();
		super.onResume();
	}*/

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		m_userInfo = MyApplication.getInstance().m_userInfo;
		tv_school_name = (TextView) findViewById(R.id.tv_school_name);
		edt_user_name = (ClearEditText) findViewById(R.id.edt_user_name);
		ll_sex_female = (LinearLayout) findViewById(R.id.ll_sex_female);
		ll_sex_male = (LinearLayout) findViewById(R.id.ll_sex_male);
		iv_sex_female = (ImageView) findViewById(R.id.iv_sex_female);
		iv_sex_male = (ImageView) findViewById(R.id.iv_sex_male);

	}

	@Override
	public void back(View v) {
		// TODO Auto-generated method stub
		startActivity(new Intent(PerfectNameActivity.this, LoginActivity.class));
		finish();
		super.back(v);
	}

	@Override
	public void initData() {
		edt_user_name.setText(m_userInfo.m_nick_name);
		if (m_userInfo.m_strUserGender.equals("1")) {
			iv_sex_female.setImageResource(R.drawable.garden_heart);
			iv_sex_male.setImageResource(R.drawable.garden_pigeon);
			is_male = true;
			select_sex = 1;
		} else if (m_userInfo.m_strUserGender.equals("2")) {
			iv_sex_female.setImageResource(R.drawable.garden_pigeon);
			iv_sex_male.setImageResource(R.drawable.garden_heart);
			is_male = false;
			select_sex = 0;
		}
		tv_school_name.setText(m_userInfo.m_strUserSchool);
		school_id = m_userInfo.m_strUserSchoolId;
		tv_school_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PerfectNameActivity.this,
						SelectSchoolActivity.class);
				startActivityForResult(intent, SELECT_SCHOOL);
			}
		});
		// TODO Auto-generated method stub
		ll_sex_male.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!is_male) {
					iv_sex_female.setImageResource(R.drawable.garden_heart);
					iv_sex_male.setImageResource(R.drawable.garden_pigeon);
					is_male = true;
					select_sex = 1;
				}
			}
		});
		ll_sex_female.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (is_male) {
					iv_sex_female.setImageResource(R.drawable.garden_pigeon);
					iv_sex_male.setImageResource(R.drawable.garden_heart);
					is_male = false;
					select_sex = 0;
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == SELECT_SCHOOL && resultCode == 1111) {
			schoolInfo = (SchoolInfo) data.getSerializableExtra("schoolinfo");
			tv_school_name.setText(schoolInfo.getCOLLEGE_NAME());
			school_id = Integer.parseInt(schoolInfo.getCOLLEGE_GCODE());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 提交
	 * 
	 * @param view
	 */
	public void submit(View view) {

		if (TextUtils.isEmpty(edt_user_name.getText().toString())) {
			CommandTools.showToast("请填写你的用户名");
			return;
		}
		
		if (school_id == 0) {
			CommandTools.showToast("请选择你的学校");
			return;
		}

		DialogUtils.showTwoButtonDialog(mContext, GeneralDialog.DIALOG_ICON_TYPE_6, "", "宝宝，确认学校选对了吗？提交了就不可更改了喔！", "取消", "确认", new DialogCallback() {

			@Override
			public void callback(int position) {

				if(position == 0){

					finish_info();
				}
			}
		});

	}

	/**
	 * 选择学校
	 * 
	 * @param view
	 */
	public void SelectSchool(View view) {

	}

	/**
	 * 提交信息
	 */
	private void finish_info() {


		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("collegeId", school_id);
			jsonObject.put("gender", select_sex);
			jsonObject.put("nickname", edt_user_name.getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(mContext, Constant.CommitNameSchool_url,
				jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark,
					JSONObject jsonObject) {

				CommandTools.showToast(remark);
				if (success != 0) {
					return;
				}
				// 新用户继续完善信息
				if (m_userInfo.verifyStatus == Constant.REVIEW_STATE_NOT_SUBMIT) {
					Intent intent = new Intent(
							PerfectNameActivity.this,
							MainActivity.class);
					startActivity(intent);
					finish();
				} else {
					// 老用户跳转到首页
					startActivity(new Intent(PerfectNameActivity.this,
							MainActivity.class));
					LoginUtil.getPersonalMessage();
					finish();

				}
				// if (!TextUtils.isEmpty(auth)) {
				// Intent intent = new Intent(mContext,
				// CertificationActivity.class);
				// intent.putExtra(UserInfoActivity.AUTH, "auth");
				// startActivity(intent);
				// } else {
				// finish();
				// }
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			startActivity(new Intent(PerfectNameActivity.this,
					LoginActivity.class));
			finish();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
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
					myApp.m_userInfo.m_nick_name = jsonObject
							.optString("nickname");
					myApp.m_userInfo.m_strUserSchool = jsonObject
							.optString("collegeName");
					myApp.m_userInfo.m_strUserSchoolId = jsonObject
							.optInt("collegeId");
					myApp.m_userInfo.m_strUserGender = jsonObject
							.optString("gender");
					Logs.i("zdkj--sfz---",
							"sfz = " + jsonObject.optString("idNo"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
