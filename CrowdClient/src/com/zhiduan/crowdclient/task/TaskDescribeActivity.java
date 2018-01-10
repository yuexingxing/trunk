package com.zhiduan.crowdclient.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
/**
 * 
 * <pre>
 * Description	任务详细描述
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-18 上午10:42:38  
 * </pre>
 */
public class TaskDescribeActivity extends BaseActivity {
	private Context mContext;
	private EditText edt_task_describe;
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_task_describe, this);
		mContext=this;
		setTitle("任务描述");
		setRightTitleText("提交");
	}

	@Override
	public void rightClick() {
		if (edt_task_describe.getText().toString().equals("")) {
			Toast.makeText(mContext, "请输入你的任务详细描述", 0).show();
		}
	}
	
	@Override
	public void initView() {
		edt_task_describe=(EditText) findViewById(R.id.edt_task_describe);
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 获取订单备注信息
	 * 请求类型 post
	 * 请求Url  /order/packet/selectremark.htm
	 */
	private void selectremark(String orderId){

		if(TextUtils.isEmpty(orderId)){
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("orderId", orderId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "order/packet/selectremark.htm";
		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				if(success != 0){
					CommandTools.showToast(remark);
					return;
				}

				jsonObject = jsonObject.optJSONObject("data");
				remark = jsonObject.optString("remark");
//				((TextView)findViewById(R.id.tv_dis_detail_note)).setText(remark+"");
			}
		});
	}

}
