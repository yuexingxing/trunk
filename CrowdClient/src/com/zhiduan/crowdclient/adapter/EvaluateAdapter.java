package com.zhiduan.crowdclient.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.EvaluateInfo;
import com.zhiduan.crowdclient.util.Constant;
/**
 * 订单评价
 * 
 * @author hexiuhui
 * 
 */
@SuppressLint("NewApi")
public class EvaluateAdapter extends BaseAdapter {

	private Context mContext;
	private List<EvaluateInfo> mData;
	
	public EvaluateAdapter(Context context, List<EvaluateInfo> data) {
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
			convertView = View.inflate(mContext, R.layout.evaluate_listview_item, null);
			holder = new ViewHolder();
			holder.evaluate_icon = (ImageView) convertView.findViewById(R.id.evaluate_icon);
			holder.evaluate_content = (TextView) convertView.findViewById(R.id.evaluate_content);
			holder.evaluate_time = (TextView) convertView.findViewById(R.id.evaluate_time);
			holder.evaluate_grade = (RatingBar) convertView.findViewById(R.id.evaluate_grade);
			holder.evaluate_gender=(ImageView) convertView.findViewById(R.id.evaluate_gender);
			holder.evaluate_name=(TextView) convertView.findViewById(R.id.evaluate_name);
			holder.rating_sum=(TextView) convertView.findViewById(R.id.rating_sum);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		EvaluateInfo evaluateInfo = mData.get(position);
		
		holder.evaluate_icon.setBackgroundResource(R.drawable.profile_headportrait);
		MyApplication.getInstance().getImageLoader().displayImage(evaluateInfo.getEvaluateUrl(), holder.evaluate_icon, MyApplication.getInstance().getOptions(), null);
		holder.evaluate_content.setText(evaluateInfo.getEvaluateContent());
		holder.evaluate_name.setText(evaluateInfo.getAppraiserName());
		if (evaluateInfo.getAppraiserGender().equals("p_gender_male")) {
			holder.evaluate_gender.setImageResource(R.drawable.profile_boy);
		}else if (evaluateInfo.getAppraiserGender().equals("p_gender_female")) {
			holder.evaluate_gender.setImageResource(R.drawable.profile_girl);
		}else if (evaluateInfo.getAppraiserGender().equals("p_gender_secret")) {
			holder.evaluate_gender.setVisibility(View.GONE);
		}
		
		// 提取日期中的小时分钟
		SimpleDateFormat OldDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat startFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (!TextUtils.isEmpty(evaluateInfo.getOverallScore()+"")) {
			double rat=evaluateInfo.getOverallScore();
			int fenshu=(int)rat;
			holder.rating_sum.setText(fenshu+"星");
		}
		String startDate = "";
		try {
			startDate = startFormat.format(OldDateFormat.parse(evaluateInfo.getEvaluateTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		holder.evaluate_time.setText(startDate);
		if (!TextUtils.isEmpty(evaluateInfo.getOverallScore()+"")) {
			holder.evaluate_grade.setRating(Float.parseFloat(evaluateInfo.getOverallScore() + ""));
		}
		
		return convertView;
	}

	class ViewHolder {
		private TextView rating_sum;
		public TextView evaluate_name;
		public ImageView evaluate_gender;
		public ImageView evaluate_icon;
		public TextView evaluate_content;
		public TextView evaluate_time;
		public RatingBar evaluate_grade;
	}
}
