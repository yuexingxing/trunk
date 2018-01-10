package com.zhiduan.crowdclient.message;

import java.util.List;

import com.igexin.push.c.r;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.MessageInfo;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.view.BadgeView;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**  
 * <pre>
 * Description 消息列表适配器
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-7-21 下午5:25:02  
 * </pre>
 */

public class MessageAdapter extends BaseAdapter{
	private Context mContext;
	private List<MessageInfo> message_list;
	private int message_type;
	
	public MessageAdapter(Context context,List list,int type){
		this.mContext=context;
		this.message_list=list;
		this.message_type=type;
	}
	
	@Override
	public int getCount() {
		return message_list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return message_list.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView==null) {
			viewHolder=new ViewHolder();
			convertView=View.inflate(mContext,R.layout.item_message_center,null);
			viewHolder.message_title=(TextView) convertView.findViewById(R.id.tv_message_title);
			viewHolder.message_content=(TextView) convertView.findViewById(R.id.tv_message_content);
			viewHolder.message_time=(TextView) convertView.findViewById(R.id.tv_message_time);
			viewHolder.iv_message_icon=(ImageView) convertView.findViewById(R.id.iv_message_icon);
			viewHolder.tv_top_red=(TextView) convertView.findViewById(R.id.tv_top_red);
			convertView.setTag(viewHolder);
		}else {
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.message_content.setText(message_list.get(position).getMessageContent());
		viewHolder.message_title.setText(message_list.get(position).getMessageTitle());
		viewHolder.message_time.setText(message_list.get(position).getMessageTime());
		DownLoad_Img(message_list.get(position).getMessage_icon(), viewHolder.iv_message_icon);
		if (message_list.get(position).getUnread_num()!=0) {
		viewHolder.tv_top_red.setText(message_list.get(position).getUnread_num()+"");
		viewHolder.tv_top_red.setVisibility(View.VISIBLE);
		}
		return convertView;
	}
	class ViewHolder{
		public RelativeLayout deleteLayout;
		public RelativeLayout rl_system_update;
		TextView message_title,tv_top_red;
		TextView message_time;
		TextView message_content;
		ImageView iv_message_icon;
		
	}
	/**
	 * 下载服务器图片
	 */
	private void DownLoad_Img(String img_url,final ImageView imageView) {
		ImageLoader.getInstance().loadImage(img_url,new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
				imageView.setImageBitmap(bitmap);
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	}

}
