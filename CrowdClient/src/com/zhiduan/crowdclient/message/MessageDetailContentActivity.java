package com.zhiduan.crowdclient.message;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.R.id;
import com.zhiduan.crowdclient.R.layout;
import com.zhiduan.crowdclient.activity.BaseActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.EditText;
/**
 * 
 * <pre>
 * Description 	内容详情
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-6 下午5:22:38  
 * </pre>
 */
public class MessageDetailContentActivity extends BaseActivity {
	private EditText edt_message;
	private Intent intent;
	private String message_content;
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_message_detail_content, this);
		setTitle("内容详情");
		hidenRightTitle();
		intent=getIntent();
		if (intent!=null) {
			message_content=intent.getStringExtra("message_content");
		}
	}

	@Override
	public void initView() {
		edt_message=(EditText) findViewById(R.id.edt_message);
		if (message_content!=null) {
			edt_message.setText(message_content);
		}
	}
	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}


}
