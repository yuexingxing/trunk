package com.zhiduan.crowdclient.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.TaskOrderInfo;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.BottomCallBackInterface.OnBottomClickListener;
import com.zhiduan.crowdclient.util.OrderUtil;

/** 
 * 常规任务适配器
 * 
 * @author yxx
 *
 * @date 2016-5-24 下午6:13:00
 * 
 */
public class TaskOrderMenuFirstAdapter extends BaseAdapter{

	private List<TaskOrderInfo> dataList=new ArrayList<TaskOrderInfo>();
	private Context mContext;
	private ViewHolder holder;
	private OnBottomClickListener mListener;

	public TaskOrderMenuFirstAdapter(Context context, List<TaskOrderInfo> dataList, OnBottomClickListener listener) {

		this.mContext = context;
		this.dataList = dataList;
		mListener = listener;
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
			convertView = View.inflate(mContext, R.layout.item_task_order_first, null);			
			holder = new ViewHolder();

			holder.layoutDetail = (LinearLayout) convertView.findViewById(R.id.item_task_order_first_layout_detail);

			holder.icon = (ImageView) convertView.findViewById(R.id.item_task_order_first_icon);
			holder.level = (ImageView) convertView.findViewById(R.id.item_task_order_first_level);
			holder.grab = (ImageView) convertView.findViewById(R.id.item_task_order_first_grab);

			holder.name = (TextView) convertView.findViewById(R.id.item_task_order_first_name);
			holder.reward = (TextView) convertView.findViewById(R.id.item_task_order_first_reward);
			holder.time = (TextView) convertView.findViewById(R.id.item_task_order_first_time);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		final TaskOrderInfo info = dataList.get(position);

		holder.name.setText(info.getTheme());
		holder.time.setText(OrderUtil.getBetweenDate(info.getDeliveryStartDate(), info.getDeliveryEndDate()));
		
		holder.reward.setText(AmountUtils.changeF2Y(info.getIncome(), 0));

		if(info.getLevel().equals("1")){
			holder.level.setBackgroundResource(R.drawable.system_task_bgc);
		}else if(info.getLevel().equals("2")){
			holder.level.setBackgroundResource(R.drawable.system_task_bgb);
		}else if(info.getLevel().equals("3")){
			holder.level.setBackgroundResource(R.drawable.system_task_bgd);
		}else if(info.getLevel().equals("4")){
			holder.level.setBackgroundResource(R.drawable.system_task_bg);
		}

		MyApplication.getInstance().getImageLoader().displayImage(info.getIcon(), holder.icon, MyApplication.getInstance().getOptions(), null);

		holder.layoutDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});

		holder.grab.setOnClickListener(new OnClickListener() {

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

		public LinearLayout layoutDetail;

		public ImageView icon;//头像
		public ImageView level;//等级
		public ImageView grab;//接任务

		public TextView name;//任务名称
		public TextView reward;//打赏金额
		public TextView time;//打赏金额
	}	

}
