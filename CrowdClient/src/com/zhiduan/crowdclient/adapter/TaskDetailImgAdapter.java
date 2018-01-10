package com.zhiduan.crowdclient.adapter;

import java.util.ArrayList;
import java.util.List;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.AbnormalInfo;
import com.zhiduan.crowdclient.data.TaskInfo;

import android.content.Context;
import android.graphics.Bitmap;
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
 * Description	任务详情图片列表适配器
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-17 上午10:27:08  
 * </pre>
 */
public class TaskDetailImgAdapter extends BaseAdapter{

	//private List<TaskInfo> dataList=new ArrayList<TaskInfo>();
//	private int []ids;
	private List<Bitmap>datas=new ArrayList<Bitmap>();
	private Context mContext;
	private ViewHolder holder;
	private int task_type;

	//public TaskDetailImgAdapter(Context mContext, List<TaskInfo> dataList,int type) {
	public TaskDetailImgAdapter(Context mContext, List<Bitmap> maps,int type) {
		this.mContext = mContext;
		//this.dataList = dataList;
		this.datas=maps;
		this.task_type=type;
	}

	@Override
	public int getCount() {
		return datas.size();
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
			convertView = View.inflate(mContext, R.layout.item_taskdetail_img, null);			
			holder = new ViewHolder();

			holder.iv_detail_img=(ImageView) convertView.findViewById(R.id.iv_detail_img);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		holder.iv_detail_img.setImageBitmap(datas.get(position));
//		holder.layoutDetail.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//
//				if(mListener != null){
//					mListener.onBottomClick(arg0, position);
//				}
//			}
//		});
		
		return convertView;
	}

	class ViewHolder {

		public ImageView iv_detail_img;
		
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
