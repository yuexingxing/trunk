package com.zhiduan.crowdclient.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.TaskImg;
import com.zhiduan.crowdclient.util.Constant;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * <pre>
 * Description	任务相册适配器
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-18 下午3:39:21
 * </pre>
 */

public class TaskAlbumAdapter extends BaseAdapter {
	private List<TaskImg> imgs;
	private Context mContext;
	private int type;

	public TaskAlbumAdapter(Context mContext, List<TaskImg> bitmaps,int task_type) {
		this.imgs = bitmaps;
		this.mContext = mContext;
		this.type=task_type;
	}

	@Override
	public int getCount() {
		return imgs.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup group) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = View
					.inflate(mContext, R.layout.item_task_album, null);
			holder = new ViewHolder();
			holder.iv_task_album = (ImageView) convertView
					.findViewById(R.id.iv_task_album);

			holder.iv_task_delete = (ImageView) convertView
					.findViewById(R.id.iv_task_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == 0||type==Constant.TASK_AUDIT_DETAIL||type==Constant.TASK_FINISH_DETAIL) {
			holder.iv_task_delete.setVisibility(View.GONE);
		} else {
			holder.iv_task_delete.setVisibility(View.VISIBLE);
		}
		if (imgs.get(position).getTaskBitmap() != null) {
			holder.iv_task_album.setImageBitmap(imgs.get(position)
					.getTaskBitmap());
		} else {

			if (!TextUtils.isEmpty(imgs.get(position).getThumbnailUrl())) {
				MyApplication
						.getInstance()
						.getImageLoader()
						.loadImage(imgs.get(position).getThumbnailUrl(),
								MyApplication.getInstance().getOptions(),
								new ImageLoadingListener() {

									@Override
									public void onLoadingStarted(String arg0,
											View arg1) {
										// TODO
										// Auto-generated
										// method stub

									}

									@Override
									public void onLoadingFailed(String arg0,
											View arg1, FailReason arg2) {
										holder.iv_task_delete.setVisibility(View.GONE);
										// TODO
										// Auto-generated
										// method stub

									}

									@Override
									public void onLoadingComplete(String arg0,
											View arg1, Bitmap arg2) {
										int w = mContext
												.getResources()
												.getDimensionPixelOffset(
														R.dimen.thumnail_default_width);
										int h = mContext
												.getResources()
												.getDimensionPixelSize(
														R.dimen.thumnail_default_height);
										Bitmap thumBitmap = ThumbnailUtils
												.extractThumbnail(arg2, w, h);
										holder.iv_task_album
												.setImageBitmap(thumBitmap);

									}

									@Override
									public void onLoadingCancelled(String arg0,
											View arg1) {
										// TODO
										// Auto-generated
										// method stub

									}
								});
			} else {
				// ((ImageView)findViewById(R.id.img_abnormal_detail_icon)).setImageBitmap(null);
			}
		}
		// holder.iv_task_album.setImageBitmap(imgs.get(position).getTaskBitmap());
		
		holder.iv_task_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mListener != null) {
					mListener.onBottomClick(arg0, position);
				}

			}
		});
		return convertView;
	}

	class ViewHolder {
		ImageView iv_task_album;
		ImageView iv_task_delete;

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
