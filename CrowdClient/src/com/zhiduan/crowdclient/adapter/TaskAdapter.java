package com.zhiduan.crowdclient.adapter;

import java.util.ArrayList;
import java.util.List;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.AbnormalInfo;
import com.zhiduan.crowdclient.data.TaskInfo;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.Constant;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * <pre>
 * Description	任务适配器
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-17 上午10:27:08  
 * </pre>
 */
public class TaskAdapter extends BaseAdapter{

	private List<TaskInfo> dataList=new ArrayList<TaskInfo>();
	private Context mContext;
	private ViewHolder holder;
	private int task_type;

	public TaskAdapter(Context mContext, List<TaskInfo> dataList,int type) {

		this.mContext = mContext;
		this.dataList = dataList;
		this.task_type=type;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if(convertView==null){
			convertView = View.inflate(mContext, R.layout.item_task, null);			
			holder = new ViewHolder();
			holder.layoutDetail = (RelativeLayout) convertView.findViewById(R.id.rl_task_adapter_detail);
			holder.iv_task_logo=(ImageView) convertView.findViewById(R.id.iv_task_adapter_logo);
			holder.iv_task_sex=(ImageView) convertView.findViewById(R.id.iv_task_adapter_sex);
			holder.tv_task_money=(TextView) convertView.findViewById(R.id.tv_task_adapter_money);
			holder.tv_task_require=(TextView) convertView.findViewById(R.id.tv_task_adapter_require);
			holder.tv_item_task_time=(TextView) convertView.findViewById(R.id.tv_item_task_adapter_time);
			holder.tv_task_title=(TextView) convertView.findViewById(R.id.tv_task_adapter_title);
			holder.task_time=(TextView) convertView.findViewById(R.id.task_adapter_time);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		TaskInfo info = dataList.get(position);
		if (info.getTask_people_sex().contains("female")) {
			holder.iv_task_sex.setBackgroundResource(R.drawable.profile_girl);
		}else {
			holder.iv_task_sex.setBackgroundResource(R.drawable.profile_boy);
		}
		
		if(!TextUtils.isEmpty(info.getTask_logo())){
			MyApplication.getInstance().getImageLoader().displayImage(info.getTask_logo(), holder.iv_task_logo, MyApplication.getInstance().getOptions(), null);
		}
		
		holder.tv_task_title.setText(info.getTask_title()==null?"":info.getTask_title());
		holder.tv_task_require.setText(info.getTask_require()==null?"":info.getTask_require());
		try {
			
		holder.tv_task_money.setText(AmountUtils.changeF2Y(info.getTask_money())==null?"":AmountUtils.changeF2Y(info.getTask_money()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		holder.tv_item_task_time.setText(info.getTask_time()==null?"":info.getTask_time());
		
		if (task_type==Constant.TASK_AUDIT_DETAIL) {
			holder.task_time.setVisibility(View.GONE);
			holder.tv_item_task_time.setText("审核中");
		}

		holder.layoutDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});
		
		return convertView;
	}

	class ViewHolder {

		public RelativeLayout layoutDetail;
		public ImageView iv_task_sex;
		public TextView tv_task_title;
		public TextView tv_task_require;
		public ImageView iv_task_logo;
		public TextView tv_item_task_time;
		public TextView tv_task_money;
		public TextView task_time;
	}	

	/**
	 * 单击事件监听器
	 */
	private OnBottomClickListener mListener = null;

	public void setOnBottomClickListener(OnBottomClickListener listener) {
		mListener = listener;
	}

	public interface OnBottomClickListener {
		void onBottomClick(View v, int position);
	}
}
