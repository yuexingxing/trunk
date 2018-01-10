package com.zhiduan.crowdclient.completeinformation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;

/**
 * <pre>
 * Description  校区地址
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-5-24 下午8:13:16
 * </pre>
 */
public class SchoolAddressActivity extends BaseActivity {

	private EditText edt_school_address;
	private Context context;
	private static final String TAG = "zdkj";
	private String school_add;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_school_address,
				SchoolAddressActivity.this);
		context = this;
		setTitle("校区地址");
		setRightTitleText("完成");
	}

	@Override
	public void initView() {
		edt_school_address = (EditText) findViewById(R.id.edt_school_address);
		clickRightTitle(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, SchoolDataActivity.class);
				intent.putExtra("schooladdress", edt_school_address.getText()
						.toString());
				setResult(3333, intent);
				finish();

			}
		});
	}

	@Override
	public void initData() {
		school_add=getIntent().getStringExtra("school_address");
		if (!TextUtils.isEmpty(school_add)) {
			edt_school_address.setText(school_add);
		}
	}

}
