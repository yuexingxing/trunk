package com.zhiduan.crowdclient.message;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.R.layout;
import com.zhiduan.crowdclient.R.menu;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.data.MessageInfo;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 
 * <pre>
 * Description	消息中心
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-7-21 下午5:17:29
 * </pre>
 */
public class MessageActivity extends BaseActivity implements IXListViewListener {
	private Context mContext;
	private List<MessageInfo> messageList = new ArrayList<MessageInfo>();
	private MessageInfo messageInfo;
	private DropDownListView listView;
	private MessageAdapter adapter;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_message, this);
		mContext = this;
		setTitle("消息");

	}

	@Override
	public void initView() {
		listView = (DropDownListView) findViewById(R.id.lv_message_list);
		listView.setPullRefreshEnable(true);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);

	}
	@Override
	protected void onResume() {
		getMessage();
		super.onResume();
	}


	@Override
	public void initData() {

	}

	/**
	 * 跳转到消息列表
	 * 
	 * @param type
	 */
	public void go_messsage_list(int type) {
		Intent intent = new Intent(mContext, MessageListActivity.class);
		intent.putExtra("messagetype", type);
		startActivity(intent);
	}

	public void getMessage() {

		JSONObject jsonObject = new JSONObject();

		CustomProgress.showDialog(mContext, "数据获取中", true, null);
		String strUrl = "msg/ps/list.htm";
		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject,
				new RequestCallback() {

					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						CustomProgress.dissDialog();

						if (success == 0) {
							messageList.clear();
							listView.stopRefresh();
							JSONArray messageArray = jsonObject
									.optJSONArray("data");
							for (int i = 0; i < messageArray.length(); i++) {
								JSONObject messageoObject = messageArray
										.optJSONObject(i);
								messageInfo = new MessageInfo();
								messageInfo.setMessageTitle(messageoObject
										.optString("title"));
								messageInfo.setMessageContent(messageoObject
										.optString("content"));
								messageInfo.setMessageTime(messageoObject
										.optString("createTime"));
								messageInfo.setMessageType(messageoObject
										.optInt("firstMsgType"));
								messageInfo.setUnread_num(messageoObject
										.optInt("total"));
								messageInfo.setMessage_icon(messageoObject
										.optString("picUrl"));
								messageList.add(messageInfo);
							}
							adapter = new MessageAdapter(mContext,
									messageList, 0);
							if (messageList.size() > 0) {
								
								listView.setAdapter(adapter);
								adapter.notifyDataSetChanged();
								listView.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> arg0, View v,
											int position, long arg3) {
										go_messsage_list(messageList.get(
												position-1).getMessageType());
										
									}
								});
							}
							adapter.notifyDataSetChanged();

						}
						listView.stopLoadMore();
					}
				});

	}

	/**
	 * 下载服务器图片
	 */
	private void DownLoad_Img(String img_url, final ImageView imageView) {
		ImageLoader.getInstance().loadImage(img_url,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap bitmap) {
						imageView.setImageBitmap(bitmap);
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}

				});

	}

	@Override
	public void onRefresh() {
		getMessage();
	}

	@Override
	public void onLoadMore() {
		
	}

	
}
