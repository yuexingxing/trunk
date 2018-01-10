package com.zhiduan.crowdclient.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.CrowedUserInfo;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.CommandTools;
/**
 * 龙虎榜
 * 
 * @author hexiuhui
 * 
 */
@SuppressLint("NewApi")
public class PeakAdapter extends BaseAdapter {

	private Context mContext;
	private List<CrowedUserInfo> mData;
	
	public PeakAdapter(Context context, List<CrowedUserInfo> data) {
		mContext = context;
		mData = data;
		
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {

		return mData.get(position);

	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.peak_listview_item, null);
			holder = new ViewHolder();

			holder.peak_name = (TextView) convertView.findViewById(R.id.peak_name);
			holder.peak_phone = (TextView) convertView.findViewById(R.id.peak_phone);
			holder.peak_money = (TextView) convertView.findViewById(R.id.peak_money);
			holder.peak_icon = (ImageView) convertView.findViewById(R.id.peak_icon);
			holder.peak_iv_sex = (ImageView) convertView.findViewById(R.id.peak_iv_sex);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		CrowedUserInfo info = mData.get(position);
		
		holder.peak_name.setText(info.getRealName());
		holder.peak_phone.setText(CommandTools.replacePhone(info.getPhone()));
		try {
			holder.peak_money.setText("￥" + AmountUtils.changeF2Y(info.getMoney()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		NetImageUtil.setImageViewBitmap(mContext, holder.peak_icon, info.getIcon(), R.drawable.app_logo, -1, -1);
		
		if (info.getIcon().equals("")) {
//			holder.peak_icon.setBackground(mContext.getResources().getDrawable(R.drawable.app_logo));
			holder.peak_icon.setImageResource(info.getGender().contains("female") ?R.drawable.female:R.drawable.male);
		} else {
			MyApplication.getInstance().getImageLoader().displayImage(info.getIcon(), holder.peak_icon, MyApplication.getInstance().getOptions(), null);
		}
		
		if (info.getGender().equals("p_gender_male")) {
			holder.peak_iv_sex.setImageDrawable(mContext.getResources().getDrawable(R.drawable.profile_boy));
		} else if (info.getGender().equals("p_gender_female")) {
			holder.peak_iv_sex.setImageDrawable(mContext.getResources().getDrawable(R.drawable.profile_girl));
		} else {
			holder.peak_iv_sex.setVisibility(View.GONE);
		}
		
		return convertView;
	}

	class ViewHolder {
		public TextView peak_name;
		public TextView peak_phone;
		public TextView peak_money;
		public ImageView peak_iv_sex;
		public ImageView peak_icon;
	}
}
