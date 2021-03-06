package com.zhiduan.crowdclient.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.RewardRuleInfo;
import com.zhiduan.crowdclient.data.UserInfo;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Res;
import com.zhiduan.crowdclient.util.TaskRule;

/** 
 * 奖励规则适配器
 * 
 * @author yxx
 *
 * @date 2016-5-24 下午6:13:00
 * 
 */
public class TaskOrderMenuSecondAdapter extends BaseAdapter{

	private List<RewardRuleInfo> dataList = new ArrayList<RewardRuleInfo>();
	private Context mContext;
	private ViewHolder holder;

	public TaskOrderMenuSecondAdapter(Context context, List<RewardRuleInfo> dataList) {

		this.mContext = context;
		this.dataList = dataList;
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
			convertView = View.inflate(mContext, R.layout.item_task_order_second, null);			
			holder = new ViewHolder();

			holder.icon = (ImageView) convertView.findViewById(R.id.item_task_order_second_icon);

			holder.title = (TextView) convertView.findViewById(R.id.item_task_order_second_title);
			holder.status = (TextView) convertView.findViewById(R.id.item_task_order_second_status);
			holder.content = (TextView) convertView.findViewById(R.id.item_task_order_second_content);
			holder.type1 = (TextView) convertView.findViewById(R.id.item_task_order_second_type1);
			holder.type1_content = (TextView) convertView.findViewById(R.id.item_task_order_second_type1_content);
			holder.experence = (TextView) convertView.findViewById(R.id.item_task_order_second_experence);
			holder.cash = (TextView) convertView.findViewById(R.id.item_task_order_second_cash);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		final RewardRuleInfo info = dataList.get(position);

		Log.v("result", info.getType());
		if(info.getType().equals(TaskRule.daily_order)){//每日订单

			holder.icon.setBackgroundResource(R.drawable.img_taskorder_rule_icon_1);
		}else if(info.getType().equals(TaskRule.group_order)){//组合订单

			holder.icon.setBackgroundResource(R.drawable.img_taskorder_rule_icon_2);
		}else if(info.getType().equals(TaskRule.errands_order)){//代跑腿订单

			holder.icon.setBackgroundResource(R.drawable.img_taskorder_rule_icon_3);
		}else if(info.getType().equals(TaskRule.routine_tasks)){//常规任务

			holder.icon.setBackgroundResource(R.drawable.img_taskorder_rule_icon_4);
		}else if(info.getType().equals(TaskRule.grab_aging)){//接单时效

			holder.icon.setBackgroundResource(R.drawable.img_taskorder_rule_icon_5);
		}else if(info.getType().equals(TaskRule.assign_orderes)){//派单完成数

			holder.icon.setBackgroundResource(R.drawable.img_taskorder_rule_icon_6);
		}else if(info.getType().equals(TaskRule.positive_orderes)){//好评数

			holder.icon.setBackgroundResource(R.drawable.img_taskorder_rule_icon_7);
		}

		holder.type1.setText(info.getItemDesc());
		holder.type1_content.setText("(" + info.getCompletedNum() + "/" + info.getNeedCompleteNumber() + ")");
	
		/**
		 * 任务状态判断
		 * 已登录未认证、未登录，不显示状态
		 * */
		UserInfo userInfo = MyApplication.getInstance().m_userInfo;
		if(!TextUtils.isEmpty(userInfo.toKen) && userInfo.verifyStatus == Constant.REVIEW_STATE_NOT_SUBMIT){
			holder.status.setText("");
		}else if(TextUtils.isEmpty(userInfo.toKen)){
			holder.status.setText("");
		}else if(info.getFlowState().equals("1")){
			holder.status.setText("进行中");
			holder.status.setTextColor(Res.getColor(R.color.text_gray));
		}else if(info.getFlowState().equals("2")){
			holder.status.setText("已完成");
			holder.status.setTextColor(Res.getColor(R.color.main_color));
		}

		holder.title.setText(info.getTitle());
		holder.content.setText(info.getContent());
		holder.experence.setText(info.getExperence());
		
		String strFee = Res.getString(R.string.fee); 
		holder.cash.setText(String.format(strFee, AmountUtils.changeF2Y(info.getCash(), 0)));

		return convertView;
	}

	class ViewHolder {

		public ImageView icon;
		public TextView title;
		public TextView status;
		public TextView content;
		public TextView type1;
		public TextView type1_content;
		public TextView experence;
		public TextView cash;
	}	

}
