package com.zhiduan.crowdclient.menucenter;

import org.json.JSONObject;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.R.id;
import com.zhiduan.crowdclient.R.layout;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.ClearEditText;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * <pre>
 * Description	修改用户名
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2017-1-6 下午5:21:54
 * </pre>
 */
public class ChangeNameActivity extends BaseActivity {
	private ClearEditText user_name;
	private Context mContext;
	private Button btn_change_name;
	private MyApplication myapp = MyApplication.getInstance();
	
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentViewId(R.layout.activity_change_name, this);
		mContext=this;
		setTitle("用户名");
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		btn_change_name=(Button) findViewById(R.id.btn_change_name);
		user_name = (ClearEditText) findViewById(R.id.user_name);
		setEditableListener(user_name);
		user_name.setText(myapp.m_userInfo.m_nick_name);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

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
	 * 检查数据录入是否完整 修改注册、确定、下一步按钮状态
	 */
	private void checkStatus() {

		String str_name = user_name.getText().toString();

		if (str_name.length() == 0 
				) {
			btn_change_name
					.setBackgroundResource(R.drawable.register_valid_gray);
			btn_change_name.setClickable(false);
		} else {
			btn_change_name
					.setBackgroundResource(R.drawable.register_valid);
			btn_change_name.setClickable(true);
		}
	}
	/**
	 * 确认修改
	 * 
	 * @param v
	 */
	public void submit_change(View v) {

		if (TextUtils.isEmpty(user_name.getText().toString())) {
			CommandTools.showToast("请填写你的用户名");
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			
			jsonObject.put("nickname", user_name.getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(mContext, Constant.UpdateNickName_url,
				jsonObject, new RequestCallback() {

					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {

						CommandTools.showToast(remark);
						if (success != 0) {
							return;
						}
						finish();
					}
				});

	
	}
	
}
