package com.zhiduan.crowdclient.adapter;

import java.util.List;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.SchoolInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

/** 
 * 城市选择适配器
 * 
 * @author yxx
 *
 * @date 2016-5-4 下午2:18:08
 * 
 */
public class SelectCityAdapter extends BaseAdapter implements SectionIndexer {
	
	private Context ct;
	private List<SchoolInfo> data;

	public SelectCityAdapter(Context ct, List<SchoolInfo> datas) {
		this.ct = ct;
		this.data = datas;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<SchoolInfo> list) {
		this.data = list;
		notifyDataSetChanged();
	}

	public void remove(SchoolInfo user){
		this.data.remove(user);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(ct).inflate(R.layout.item_city, null);
			viewHolder = new ViewHolder();
			viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.layout_item_city);
			viewHolder.alpha = (TextView) convertView.findViewById(R.id.tv_item_city_alpha);
			viewHolder.name = (TextView) convertView.findViewById(R.id.tv_item_city);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		SchoolInfo friend = data.get(position);
		final String name = friend.getCOLLEGE_NAME();

		viewHolder.name.setText(name);

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.layout.setVisibility(View.VISIBLE);
			viewHolder.alpha.setText(friend.getPinYin().substring(0, 1));
		} else {
			viewHolder.layout.setVisibility(View.GONE);
		}

		return convertView;
	}

	static class ViewHolder {
		LinearLayout layout;
		TextView alpha;// 首字母提示
		TextView name;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return data.get(position).getPinYin().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	@SuppressLint("DefaultLocale")
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = data.get(i).getPinYin();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section){
				return i;
			}
		}

		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

}