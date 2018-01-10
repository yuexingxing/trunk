package com.zhiduan.crowdclient.menuindex.filterlistview;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhiduan.crowdclient.R;

/**
 * hexiuhui
 */
public class FilterOneAdapter extends BaseAdapter {

    private Context mContext;
    private List<FilterEntity> mList;

    public FilterOneAdapter(Context context) {
    }

    public FilterOneAdapter(Context context, List<FilterEntity> list) {
    	this.mContext = context;
    	this.mList = list;
    }

    public void setSelectedEntity(String value) {
        for (int i = 0; i < mList.size(); i++) {
        	FilterEntity entity = mList.get(i);
        	entity.setSelected(entity.getValue().equals(value));
		}
        
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_filter_one, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FilterEntity entity = mList.get(position);

        holder.tvTitle.setText(entity.getKey());
        if (entity.isSelected()) {
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.orange));
        } else {
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.font_black_3));
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView ivImage;
        TextView tvTitle;

        ViewHolder(View view) {
            ivImage = (ImageView) view.findViewById(R.id.iv_image);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
        }
    }

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
}
