package com.zhiduan.crowdclient.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.ChannelEntity;
import com.zhiduan.crowdclient.menuindex.filterlistview.ImageManager;

/**
 * 首页频道数据
 * 
 * hexiuhui
 */
public class HeaderChannelAdapter extends BaseAdapter {

	private Context context;
	private List<ChannelEntity> list;

	public HeaderChannelAdapter(Context context, List<ChannelEntity> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_channel, null);
			holder = new ViewHolder();

			holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ChannelEntity entity = list.get(position);
		ImageManager mImageManager = new ImageManager(context);
		
		holder.tv_title.setText(entity.getTitle());
		
		mImageManager.loadCircleResImage(entity.getResId(), holder.iv_image);

		return convertView;
	}

	class ViewHolder {
		public ImageView iv_image;
		public TextView tv_title;
	}
}
