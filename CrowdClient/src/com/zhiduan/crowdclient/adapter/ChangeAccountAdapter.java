package com.zhiduan.crowdclient.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.PayAccount;
/**
 * <pre>
 * Description 切换账号 adapter
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-5-20 下午5:09:57
 * </pre>
 */
public class ChangeAccountAdapter extends BaseAdapter {

	private Context mContext;
	private List<PayAccount> dataList;
	private int mRightWidth = 0;

	public ChangeAccountAdapter(Context context, List<PayAccount> list, int rightWidth) {
		this.dataList = list;
		this.mContext = context;
		this.mRightWidth = rightWidth;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.changeaccount_item, null);
			holder = new ViewHolder();
			holder.leftLayout = (LinearLayout) convertView.findViewById(R.id.item_left_layout_account);
			holder.deleteLayout = (RelativeLayout) convertView.findViewById(R.id.item_delete_layout_account);
			holder.tv_nick = (TextView) convertView.findViewById(R.id.tv_nick);
			holder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
			holder.iv_check_status = (ImageView) convertView.findViewById(R.id.iv_check_status);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		LinearLayout.LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		holder.leftLayout.setLayoutParams(lp1);

		LinearLayout.LayoutParams lp2 = new LayoutParams(mRightWidth, LayoutParams.MATCH_PARENT);
		holder.deleteLayout.setLayoutParams(lp2);

		PayAccount info = dataList.get(position);

		holder.tv_nick.setText(info.getAccountName());
		holder.tv_username.setText(info.getAccountNumber());
		
		if (info.isPitchOn()) {
			holder.iv_check_status.setImageResource(R.drawable.msg_status_select);
		} else {
			holder.iv_check_status.setImageResource(R.drawable.msg_status);
		}
		
		holder.leftLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onRightItemClick(v, position);
				}

			}
		});
		
		holder.deleteLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onRightItemClick(v, position);
				}
			}
		});
		return convertView;
	}

	class ViewHolder {
		public ImageView iv_check_status;
		public TextView tv_nick;
		public TextView tv_username;
		public LinearLayout leftLayout;
		public RelativeLayout deleteLayout;
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
}
