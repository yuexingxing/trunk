package com.zhiduan.crowdclient.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.TaskInfo;
import com.zhiduan.crowdclient.util.AmountUtils;
/**
 * 已结算。已取消
 * 
 * @author hexiuhui
 * 
 */
@SuppressLint("NewApi")
public class PaymentAdapter extends BaseAdapter {

	private Context mContext;
	private List<TaskInfo> mData;
	private int mType;
	
	public final static int TASK_PAYMENT_TYPE = 2;
	public final static int TASK_CANCEL_TYPE = 3;
	
	public PaymentAdapter(Context context, List<TaskInfo> data, int type) {
		mContext = context;
		mData = data;
		mType = type;
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
			convertView = View.inflate(mContext, R.layout.payment_listview_item, null);
			holder = new ViewHolder();

			holder.task_payment_name = (TextView) convertView.findViewById(R.id.task_payment_name);
			holder.reward_money_task = (TextView) convertView.findViewById(R.id.reward_money_task);
			holder.payment_tv_require = (TextView) convertView.findViewById(R.id.payment_tv_require);
			holder.payment_actual_text = (TextView) convertView.findViewById(R.id.payment_actual_text);
			holder.cancel_time_text = (TextView) convertView.findViewById(R.id.cancel_time_text);
			holder.payment_grade = (RatingBar) convertView.findViewById(R.id.payment_grade);
			holder.receive_icon = (ImageView) convertView.findViewById(R.id.receive_icon);
			holder.task_send_sex = (ImageView) convertView.findViewById(R.id.task_send_sex);
			holder.payment_actual_layout = (LinearLayout) convertView.findViewById(R.id.payment_actual_layout);
			holder.payment_grade_layout = (LinearLayout) convertView.findViewById(R.id.payment_grade_layout);
			holder.cancel_time_layout = (RelativeLayout) convertView.findViewById(R.id.cancel_time_layout);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (mType == PaymentAdapter.TASK_PAYMENT_TYPE) {
			holder.payment_actual_layout.setVisibility(View.VISIBLE);
			holder.payment_grade_layout.setVisibility(View.VISIBLE);
			holder.cancel_time_layout.setVisibility(View.GONE);
		} else if (mType == PaymentAdapter.TASK_CANCEL_TYPE) {
			holder.payment_actual_layout.setVisibility(View.GONE);
			holder.payment_grade_layout.setVisibility(View.GONE);
			holder.cancel_time_layout.setVisibility(View.VISIBLE);
		}
		
		TaskInfo taskInfo = mData.get(position);
		holder.task_payment_name.setText(taskInfo.getTask_title());
		try {
			holder.reward_money_task.setText(AmountUtils.changeF2Y(taskInfo.getTask_money()));
			holder.payment_actual_text.setText(AmountUtils.changeF2Y(taskInfo.getTask_actual_money()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (taskInfo.getTask_people_sex().equals("p_gender_male")) {
			holder.task_send_sex.setImageDrawable(mContext.getResources().getDrawable(R.drawable.profile_boy));
		} else if (taskInfo.getTask_people_sex().equals("p_gender_female")) {
			holder.task_send_sex.setImageDrawable(mContext.getResources().getDrawable(R.drawable.profile_girl));
		} else {
			holder.task_send_sex.setVisibility(View.GONE);
		}
		
		if (taskInfo.getTask_logo().equals("")) {
			holder.receive_icon.setBackground(mContext.getResources().getDrawable(R.drawable.task_default));
		} else {
			MyApplication.getInstance().getImageLoader().displayImage(taskInfo.getTask_logo(), holder.receive_icon, MyApplication.getInstance().getOptions(), null);
		}
		
		holder.payment_tv_require.setText(taskInfo.getTask_require());
		holder.payment_grade.setRating(Float.parseFloat(taskInfo.getTask_grade() / 2 + ""));
		holder.cancel_time_text.setText(taskInfo.getTask_time());
		
		return convertView;
	}

	class ViewHolder {
		public TextView task_payment_name;
		public TextView reward_money_task;
		public TextView payment_tv_require;
		public TextView payment_actual_text;
		public TextView cancel_time_text;
		public RatingBar payment_grade;
		public ImageView receive_icon;
		public ImageView task_send_sex;
		public LinearLayout payment_actual_layout;
		public LinearLayout payment_grade_layout;
		public RelativeLayout cancel_time_layout;
	}
}
