package com.zhiduan.crowdclient.message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.data.MessageInfo;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.menuindex.BannerDetailActivity;
import com.zhiduan.crowdclient.menuorder.DeliveryRemindActivity;
import com.zhiduan.crowdclient.menuorder.DeliveryRemindDetailActivity;
import com.zhiduan.crowdclient.swipemenulist.IXListViewListener;
import com.zhiduan.crowdclient.swipemenulist.OnMenuItemClickListener;
import com.zhiduan.crowdclient.swipemenulist.PullToRefreshSwipeMenuListView;
import com.zhiduan.crowdclient.swipemenulist.RefreshTime;
import com.zhiduan.crowdclient.swipemenulist.SwipeMenu;
import com.zhiduan.crowdclient.swipemenulist.SwipeMenuCreator;
import com.zhiduan.crowdclient.swipemenulist.SwipeMenuItem;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.OrderUtil.ObjectCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.zxing.view.TimerTextView;

/**
 * 
 * <pre>
 * Description	消息列表
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-7-21 下午5:17:50
 * </pre>
 */
public class MessageListActivity extends BaseActivity implements
		IXListViewListener {
	
	private int message_type;
	private PullToRefreshSwipeMenuListView mListView;
	private SlideAdapter adapter;
	private Context mContext;
	private List<MessageInfo> list = new ArrayList<MessageInfo>();
	private MessageInfo info;
	private int del_position = -1;
	private int PageNumber = 1;
	private int PageSize = 10;
	private int read_position = -1;
	// private SlideView mLastSlideViewWithStatusOn;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_message_list, this);
		mContext = this;
		setRightTitleText("一键已读");
		message_type = getIntent().getIntExtra("messagetype", 0);
		if (message_type > 0) {
			ShowType(message_type);
		}
	}

	@Override
	protected void onResume() {
		getMessage();
		super.onResume();
	}
	/**
	 * 一键已读
	 */
	@Override
	public void rightClick() {
		// TODO Auto-generated method stub
		super.rightClick();
		ReadAll(message_type);
	}
	@Override
	public void initView() {
		mListView = (PullToRefreshSwipeMenuListView) findViewById(R.id.lv_message_list);
		adapter = new SlideAdapter();
		mListView.setAdapter(adapter);
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);

		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				// SwipeMenuItem openItem = new
				// SwipeMenuItem(getApplicationContext());
				// // set item background
				// openItem.setBackground(new ColorDrawable(Color.rgb(0xC9,
				// 0xC9, 0xCE)));
				// // set item width
				// openItem.setWidth(dp2px(90));
				// // set item title
				// openItem.setTitle("Open");
				// // set item title fontsize
				// openItem.setTitleSize(18);
				// // set item title font color
				// openItem.setTitleColor(Color.WHITE);
				// // add to menu
				// menu.addMenuItem(openItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}

		};
		// set creator
		mListView.setMenuCreator(creator);

		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					Log.i("zdkj", "删除" + del_position + "个条" + "======list"
							+ list.size());

					DelMessage(list.get(position).getMsgId());
					list.remove(position);

					adapter.notifyDataSetChanged();
					// open
					break;
				case 1:

					// delete
					break;
				}
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				Intent intent;
				if (list.get(position - 1).getReadState() == 0) {
					ReadMessage(list.get(position - 1).getMsgId());
					read_position = position - 1;
					update_redstatus(position - 1);
				}
				switch (list.get(position - 1).getFirstMsgType()) {
				case 10:
					break;
				case 11:
					intent = new Intent(MessageListActivity.this,
							BannerDetailActivity.class);
					intent.putExtra("url", list.get(position - 1)
							.getRedirectURL());
					startActivity(intent);
					break;
				case 12:
					intent = new Intent(MessageListActivity.this,
							MessageDetailContentActivity.class);
					intent.putExtra("message_content", list.get(position - 1)
							.getMessageContent());
					startActivity(intent);
					break;
				case 13:

					break;
				case 14:
					if (list.get(position - 1).getMessageType() == 1402
							|| list.get(position - 1).getMessageType() == 1403) {
						Logs.i("zdkj", "message_biz"+list.get(position - 1)
								.getBizId());
						getOrderDetail(Long.parseLong(list.get(position - 1)
								.getBizId()));
					}
				default:
					break;
				}

			}
		});
	}

	@Override
	public void initData() {

	}

	public int dp2px(int dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}
	/**
	 * 获取订单详情
	 * 接口名称 获取订单详情接口
	 * 请求类型 post
	 * 请求Url  /order/packet/getorderdetail.htm
	 */
	private void getOrderDetail(final Long assignId){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("assignId", assignId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OrderUtil.getAssignOrderDetail(mContext, assignId, new ObjectCallback(){

			@Override
			public void callback(Object object) {

				OrderInfo info = (OrderInfo) object;
				Intent intent=new Intent(MessageListActivity.this,DeliveryRemindDetailActivity.class);
				intent.putExtra("orderType", info.getCategoryId());
				intent.putExtra("assignId", assignId);
				startActivity(intent);
			}
		});

	}
	/**
	 * 根据不同消息类型展示对应消息列表
	 */
	private void ShowType(int type) {
		switch (type) {
		case 10:
			setTitle("系统消息");
			break;
		case 12:
			setTitle("交易记录");
			break;
		case 11:
			setTitle("系统公告");
			break;
		case 13:
			setTitle("业务通知");
			break;
		case 14:
			setTitle("订单推送通知");
			break;
		default:
			break;
		}

	}

	public void getMessage() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("firstMsgType", message_type);
			jsonObject.put("pageNumber", PageNumber);
			jsonObject.put("pageSize", PageSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		CustomProgress.showDialog(mContext, "数据获取中", true, null);
		String strUrl = "msg/ps/sublist.htm";

		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject,
				new RequestCallback() {

					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						CustomProgress.dissDialog();
						if (success == 0) {
							mListView.setRefreshTime(RefreshTime
									.getRefreshTime(getApplicationContext()));
							SimpleDateFormat df = new SimpleDateFormat(
									"MM-dd HH:mm", Locale.getDefault());
							RefreshTime.setRefreshTime(getApplicationContext(),
									df.format(new Date()));
							onLoad();
							if (PageNumber == 1) {
								list.clear();
							}
							jsonObject = jsonObject.optJSONObject("data");
							JSONArray messageArray = jsonObject
									.optJSONArray("responseDto");
							for (int i = 0; i < messageArray.length(); i++) {
								JSONObject messageoObject = messageArray
										.optJSONObject(i);
								info = new MessageInfo();
								JSONObject redirectURL = messageoObject
										.optJSONObject("extendContentMap");
								if (redirectURL != null
										&& !redirectURL.equals("")) {
									
									info.setBeginDate(redirectURL.optString("beginDate"));
									info.setExpiryDate(redirectURL.optString("expiryDate"));
									info.setRedirectURL(redirectURL
											.optString("redirectURL"));
								}
								info.setFirstMsgType(messageoObject
										.optInt("firstMsgType"));
								info.setMessageContent(messageoObject
										.optString("content"));
								info.setMessageTime(messageoObject
										.optString("createTime"));
								info.setReadState(messageoObject
										.optInt("readState"));
								info.setBizId(messageoObject.optString("bizId"));
								info.setMessageType(messageoObject
										.optInt("msgType"));
								info.setMsgId(messageoObject.optInt("msgId"));
								
								list.add(info);
							}
							// mListView.setResultSize(messageArray.length());
							adapter.notifyDataSetChanged();

						}

					}
				});

	}

	/**
	 * 阅读消息
	 * 
	 * @param msgId
	 */
	private void ReadMessage(int msgId) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("msgId", msgId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		// CustomProgress.showDialog(mContext, "数据获取中", true, null);
		String strUrl = "msg/ps/read.htm";

		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject,
				new RequestCallback() {

					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						// CustomProgress.dissDialog();

					}
				});

	}

	/**
	 * 删除消息
	 * 
	 * @param msgId
	 */
	private void DelMessage(int msgId) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("msgId", msgId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		CustomProgress.showDialog(mContext, "正在删除", true, null);
		String strUrl = "msg/ps/del.htm";

		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject,
				new RequestCallback() {

					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						CustomProgress.dissDialog();
						CommandTools.showToast(remark);
					}
				});

	}
	/**
	 * 一键已读
	 * @param msgtype 一级消息类型
	 */
	public void ReadAll(int msgtype) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("firstMsgType", msgtype);

		} catch (Exception e) {
			e.printStackTrace();
		}
		CustomProgress.showDialog(mContext, "正在操作", true, null);
		String strUrl = "msg/ps/changeallread.htm";
		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject,
				new RequestCallback() {

					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						CustomProgress.dissDialog();
						if (success==0) {
							getMessage();
						}
						CommandTools.showToast(remark);
					}
				});

	}
	private class SlideAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		

		SlideAdapter() {
			super();
			mInflater = getLayoutInflater();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			 //开始计时，性能测试用nanoTime会更精确，因为它是纳秒级的
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.item_activity_message_list, null);
				// icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.msg = (TextView) convertView.findViewById(R.id.msg);
				// holder.time = (TextView) convertView.findViewById(R.id.time);
				holder.tv_red_circle = (TextView) convertView.findViewById(R.id.tv_red_circle);
				holder.deleteHolder = (TextView) convertView.findViewById(R.id.delete);
				// edit = (TextView) convertView.findViewById(R.id.edit);
				holder.tv_valid_time=(TimerTextView) convertView.findViewById(R.id.tv_valid_time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			MessageInfo item = list.get(position);
			// 当天时间显示年月日,时分秒 以前的只显示年月日
			String time = item.getMessageTime().substring(0, 10);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String today = sdf.format(new Date());
			if (!time.equals(today)) {
				holder.msg.setText(time);
			} else {
				holder.msg.setText(item.getMessageTime().substring(10,
						item.getMessageTime().length()));
			}
//			if (item.getMessageType() == 1402
//					|| item.getMessageType() == 1403) {
//			if (!TextUtils.isEmpty(item.getExpiryDate())) {
//				
//			//计算倒计时时间
//	        long valid_time=Utils.getValidTime(item.getBeginDate(),item.getExpiryDate());
//			
//	        holder.tv_valid_time.setTimes(0);
//	        if (valid_time==0) {
//	        	//holder.tv_valid_time.setVisibility(View.INVISIBLE);
//	        	holder.tv_valid_time.setTextStr("订单已失效");
//			}else {
//				holder.tv_valid_time.setTimes(valid_time);
//				/** 
//		         * 开始倒计时 
//		         */  
//		        if(!holder.tv_valid_time.isRun()){  
//		        	holder.tv_valid_time.start();  
//		        }  
//			}
//			}
//			}
			Log.i("zdkj", "position---"+position+"===time====="+item.getMessageTime());
			holder.title.setText(item.getMessageContent());
			//holder.time.setText(item.getMessageTime());
			if (list.get(position).getReadState() == 0) {
				holder.tv_red_circle.setVisibility(View.VISIBLE);
			} else {
				holder.tv_red_circle.setVisibility(View.GONE);
			}
			return convertView;
		}

	}

	private static class ViewHolder {
		// public ImageView icon;
		public TextView title;
		public TextView msg;
		public TextView tv_red_circle;
		public  TimerTextView tv_valid_time;
		public TextView deleteHolder;
		
	}

	public void update_redstatus(int position) {
		list.get(position).setReadState(1);
		// mListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		mListView.smoothScrollToPosition(position);
	}

	private void onLoad() {
		mListView.stopRefresh();

		mListView.stopLoadMore();

	}

	public void onRefresh() {
		PageNumber = 1;
		getMessage();
	}
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		++PageNumber;
		getMessage();
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	

}
