package com.zhiduan.crowdclient.completeinformation;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.data.SchoolInfo;
import com.zhiduan.crowdclient.menucenter.UserInfoActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;

/**
 * <pre>
 * Description  学校信息界面
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-5-24 下午6:26:31
 * </pre>
 */
public class SchoolDataActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "zdkj";
	private Context context;
	private RelativeLayout rl_dormitory_address, rl_school_address,
			rl_university;
	private Button btn_next;
	private TextView tv_dormitory_address, tv_school_address, tv_school_area,tv_reward_rule;
	private ImageView iv_school_step;
	private String school_id;
	public final int SELECT_SCHOOL = 1001;
	public final int SCHOOL_ADDRESS = 1002;
	public final int DORMITORY_ADDRESS = 1003;
	String school_area, dormitory_address, school_address;
	private SchoolInfo schoolInfo;
	private ImageView iv_area_line, iv_address_line, iv_dormitory_line;
	private String auth = "";
	private String register_str;
	private EditText edt_invite_code;
	private String reward_rule="";
	private ImageView iv_error_dormitory, iv_error_address, iv_error_area;
	MyApplication myapp = MyApplication.getInstance();

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_school_data, SchoolDataActivity.this);
		context = this;
		setTitle("学校信息");
	}

	@Override
	public void initView() {
		tv_reward_rule=(TextView) findViewById(R.id.tv_reward_rule);
		edt_invite_code = (EditText) findViewById(R.id.edt_invite_code);
		iv_dormitory_line = (ImageView) findViewById(R.id.iv_dormitory_line);
		iv_area_line = (ImageView) findViewById(R.id.iv_area_line);
		iv_address_line = (ImageView) findViewById(R.id.iv_address_line);
		iv_error_dormitory = (ImageView) findViewById(R.id.iv_error_dormitory);
		iv_error_address = (ImageView) findViewById(R.id.iv_error_address);
		iv_error_area = (ImageView) findViewById(R.id.iv_error_area);
		tv_dormitory_address = (TextView) findViewById(R.id.tv_dormitory_address);
		tv_school_address = (TextView) findViewById(R.id.tv_school_address);
		tv_school_area = (TextView) findViewById(R.id.tv_school_area);
		iv_school_step = (ImageView) findViewById(R.id.iv_school_step);
		rl_dormitory_address = (RelativeLayout) findViewById(R.id.rl_dormitory_address);
		rl_school_address = (RelativeLayout) findViewById(R.id.rl_school_address);
		rl_university = (RelativeLayout) findViewById(R.id.rl_university);
		btn_next = (Button) findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);
		rl_dormitory_address.setOnClickListener(this);
		rl_university.setOnClickListener(this);
		rl_school_address.setOnClickListener(this);
		setEditableListener(tv_dormitory_address);
		setEditableListener(tv_school_address);
		setEditableListener(tv_school_area);

	}

	@Override
	public void initData() {
		auth = getIntent().getStringExtra(UserInfoActivity.AUTH);
		register_str = getIntent().getStringExtra("register");
		if (TextUtils.isEmpty(auth)) {
			get_schoolinfo();
			iv_school_step.setVisibility(View.GONE);
			edt_invite_code.setVisibility(View.GONE);
			btn_next.setText("提交");
		} else {
			getRewardinfo();
			iv_school_step.setVisibility(View.VISIBLE);
			btn_next.setText("下一步");
		}
		if (myapp.m_userInfo.verifyStatus == 3) {
			select_verifyinfo();
		}
		if (myapp.m_userInfo.verifyStatus == 0) {
			getRewardinfo();
		}
		// 认证失败
		if (myapp.m_userInfo.verifyStatus == Constant.REVIEW_STATE_SUCCESS
				|| myapp.m_userInfo.verifyStatus == Constant.REVIEW_STATE_SUBMITING) {
			rl_dormitory_address.setClickable(false);
			rl_school_address.setClickable(false);
			rl_university.setClickable(false);
			btn_next.setVisibility(View.GONE);
			iv_address_line.setVisibility(View.INVISIBLE);
			iv_area_line.setVisibility(View.INVISIBLE);
			iv_dormitory_line.setVisibility(View.INVISIBLE);
		}
		if (!TextUtils.isEmpty(register_str)) {
			rl_dormitory_address.setClickable(true);
			rl_school_address.setClickable(true);
			rl_university.setClickable(true);
			btn_next.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 检查数据录入是否完整 修改注册、确定、下一步按钮状态
	 */
	private void checkStatus() {

		school_area = tv_school_area.getText().toString();
		school_address = tv_school_address.getText().toString();
		dormitory_address = tv_dormitory_address.getText().toString();

		if (school_area.length() == 0 || school_address.length() == 0
				|| dormitory_address.length() == 0) {
			btn_next.setBackgroundResource(R.drawable.register_valid_gray);
			btn_next.setClickable(false);
		} else {
			btn_next.setBackgroundResource(R.drawable.register_valid);
			btn_next.setClickable(true);
		}
	}

	/**
	 * 对TextView进行录入监听
	 * 
	 * @param edt
	 */
	private void setEditableListener(TextView textView) {

		textView.addTextChangedListener(new TextWatcher() {

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
								Log.e(TAG, "==============" + jsonObject);
								// // 错误信息数组
								JSONObject verifyMap = jsonObject
										.optJSONObject("verifyMsg");
								// 显示错误图标
								if (verifyMap.optString("f1").equals("-1")) {
									iv_error_area.setVisibility(View.VISIBLE);
								}
								if (verifyMap.optString("f2").equals("-1")) {
									iv_error_address
											.setVisibility(View.VISIBLE);
								}
								if (verifyMap.optString("f3").equals("-1")) {
									iv_error_dormitory
											.setVisibility(View.VISIBLE);
								}
							} else {
								try {
									String remark = jsonObject.getString("message");
									CommandTools.showToast(remark);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				});

	}
	/**
	 * 查询审核信息
	 */
	private void getRewardinfo() {

		JSONObject jsonObject = new JSONObject();
		RequestUtilNet.postDataNoToken(CommandTools.getGlobalActivity(), Constant.getRewardDetail, jsonObject,new RequestCallback() {
			
			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {
				
				try {
					if (success == 0) {
						jsonObject = jsonObject.optJSONObject("data");
						reward_rule=jsonObject.optString("showContent");
						tv_reward_rule.setText(reward_rule);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

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
								tv_school_address.setText(object
										.optString("collegeAddress"));
								tv_dormitory_address.setText(object
										.optString("dormitoryAddress"));
								tv_school_area.setText(object
										.optString("collegeName"));
								school_id = object.optString("collegeId");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == DORMITORY_ADDRESS && resultCode == 2222) {
			dormitory_address = data.getStringExtra("dormitory");
			tv_dormitory_address.setText(dormitory_address);
		} else if (requestCode == SELECT_SCHOOL && resultCode == 1111) {
			schoolInfo = (SchoolInfo) data.getSerializableExtra("schoolinfo");
			tv_school_area.setText(schoolInfo.getCOLLEGE_NAME());
			school_id = schoolInfo.getCOLLEGE_GCODE();
		} else if (requestCode == SCHOOL_ADDRESS && resultCode == 3333) {
			school_address = data.getStringExtra("schooladdress");
			tv_school_address.setText(school_address);
		}
	}

	/**
	 * 提交信息
	 */
	private void finish_info() {

		JSONObject jsonObject = new JSONObject();
		try {
			if (!TextUtils.isEmpty(edt_invite_code.getText())) {
				jsonObject.put("inviteCode", edt_invite_code.getText());
			}
			jsonObject.put("collegeAddress", tv_school_address.getText()
					.toString());
			jsonObject.put("collegeId", school_id);
			jsonObject.put("dormitoryAddress", tv_dormitory_address.getText()
					.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(context, Constant.CommitSchoolData_url,
				jsonObject, new RequestCallback() {

					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {

						CommandTools.showToast(remark);
						if (success != 0) {
							return;
						}

						if (!TextUtils.isEmpty(auth)) {
							Intent intent = new Intent(context,
									CompleteInformationActivity.class);
							intent.putExtra(UserInfoActivity.AUTH, "auth");
							startActivity(intent);
						} else {
							finish();
						}
					}
				});

	}

	/*
	 * 事件监听
	 */
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {

		case R.id.btn_next:

			if (TextUtils.isEmpty(tv_school_area.getText().toString())
					|| TextUtils
							.isEmpty(tv_school_address.getText().toString())
					|| TextUtils.isEmpty(tv_dormitory_address.getText()
							.toString())) {
				CommandTools.showToast("资料不能为空");
				return;
			}

			finish_info();
			break;
		case R.id.rl_dormitory_address:
			intent = new Intent(context, DormitoryAddressActivity.class);
			if (!TextUtils.isEmpty(dormitory_address)) {
				intent.putExtra("dormitory_add", dormitory_address);
			}
			startActivityForResult(intent, DORMITORY_ADDRESS);
			break;
		case R.id.rl_school_address:
			intent = new Intent(context, SchoolAddressActivity.class);
			if (!TextUtils.isEmpty(school_address)) {
				intent.putExtra("school_address", school_address);
			}
			startActivityForResult(intent, SCHOOL_ADDRESS);
			break;
		case R.id.rl_university:
			intent = new Intent(context, SelectSchoolActivity.class);
			startActivityForResult(intent, SELECT_SCHOOL);
			break;
		default:
			break;
		}

	}

}
