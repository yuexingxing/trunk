package com.zhiduan.crowdclient.completeinformation;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.R.layout;
import com.zhiduan.crowdclient.activity.BaseActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 * <pre>
 * Description  宿舍地址
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-5-24 下午8:13:37
 * </pre>
 */
public class DormitoryAddressActivity extends BaseActivity {
	private Context context;
	private static final String TAG = "zdkj";
	private EditText edt_dormitory_address;
	private String dormitory_address;
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_dormitory_address,
				DormitoryAddressActivity.this);
		context = this;
		setTitle("宿舍地址");
		setRightTitleText("完成");

	}

	@Override
	public void initView() {
		edt_dormitory_address = (EditText) findViewById(R.id.edt_dormitory_address);
		clickRightTitle(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(context, SchoolDataActivity.class);
				intent.putExtra("dormitory", edt_dormitory_address.getText()
						.toString());
				setResult(2222, intent);
				finish();

			}
		});
	}

	@Override
	public void initData() {
		dormitory_address=getIntent().getStringExtra("dormitory_add");
		if (!TextUtils.isEmpty(dormitory_address)) {
			edt_dormitory_address.setText(dormitory_address);
		}
	}

}
