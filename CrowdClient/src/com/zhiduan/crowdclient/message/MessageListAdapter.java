package com.zhiduan.crowdclient.message;

import java.util.List;

import com.igexin.push.c.r;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.MessageInfo;
import com.zhiduan.crowdclient.view.BadgeView;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

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

public class MessageListAdapter extends BaseAdapter{
	private Context mContext;
	private List<MessageInfo> message_list;
	private int message_type;
	private int mRightWidth = 0;
	
	public MessageListAdapter(Context context,List list,int type){
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
			convertView=View.inflate(mContext,R.layout.item_activity_message_list,null);
			//viewHolder.deleteLayout=(RelativeLayout) convertView.findViewById(R.id.item_delete_layout_edit);
			//viewHolder.rl_system_update=(RelativeLayout) convertView.findViewById(R.id.rl_system_update);
			viewHolder.message_title=(TextView) convertView.findViewById(R.id.tv_message_title);
			viewHolder.message_content=(TextView) convertView.findViewById(R.id.tv_message_content);
			viewHolder.message_time=(TextView) convertView.findViewById(R.id.tv_message_time);
			viewHolder.iv_message_icon=(ImageView) convertView.findViewById(R.id.iv_message_icon);
			convertView.setTag(viewHolder);
		}else {
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.message_content.setText(message_list.get(position).getMessageContent());
		viewHolder.message_title.setText(message_list.get(position).getMessageTime());
		//viewHolder.message_time.setText(message_list.get(position).getMessageTime());
		viewHolder.message_time.setVisibility(View.GONE);
		viewHolder.iv_message_icon.setVisibility(View.GONE);
		BadgeView badge=new BadgeView(mContext, viewHolder.message_title);
		
		if (message_list.get(position).getReadState()==0) {
		badge.setWidth(10);
		viewHolder.message_time.setPadding(0, 15, 0, 0);
		badge.setHeight(10);
		badge.setBadgeMargin(0);
		badge.show();
		}else {
			badge.hide();
		}
		return convertView;
	}
	class ViewHolder{
		public RelativeLayout deleteLayout;
		public RelativeLayout rl_system_update;
		TextView message_title;
		TextView message_time;
		TextView message_content;
		ImageView iv_message_icon;
		
	}
	/**
	 * 单击事件监听器
	 */
	private onRightItemClickListener mListener = null;

	public void setOnRightItemClickListener(onRightItemClickListener listener) {
		mListener = listener;
	}

	public interface onRightItemClickListener {
		void onRightItemClick(View v, int position);
	}
	/**
	 * 设置外边距
	 */
	public static void setMargins(View v, int l, int t, int r, int b) {
		if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
			p.setMargins(l, t, r, b);
			v.requestLayout();
		}
	}
}
