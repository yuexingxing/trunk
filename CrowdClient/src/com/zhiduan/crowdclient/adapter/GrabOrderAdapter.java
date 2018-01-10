package com.zhiduan.crowdclient.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.im.PersonalHomepageActivity;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.view.XCRoundRectImageView;

/**
 * Author: hexiuhui
 */
public class GrabOrderAdapter extends BaseAdapter {

	private Context mContext;
	private List<OrderInfo> dataList;

	private static final int TYPE_TASK = 0; // 任务
	private static final int TYPE_ORDER = 1; // 订单
	
	//任务订单等级
	private static final String TASK_LEVEL_1 = "1";
	private static final String TASK_LEVEL_2 = "2";
	private static final String TASK_LEVEL_3 = "3";
	private static final String TASK_LEVEL_4 = "4";

	/** 抢单 */
	onRobClickListener m_onRobClickListener = null;
	
	//接任务
	onAcceptInterface m_onAcceptClickListener = null;
	
	//查看详情
	onTaskDetailInterface m_onTaskDetailClickListener = null;

	public GrabOrderAdapter(Context context, List<OrderInfo> list) {
		this.dataList = list;
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int currentType = getItemViewType(position);

		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_index, null);
			holder = new ViewHolder();
			holder.theme_text = (TextView) convertView.findViewById(R.id.theme_text);
			holder.iv_sex = (ImageView) convertView.findViewById(R.id.iv_sex);
			holder.receive_icon = (ImageView) convertView.findViewById(R.id.receive_icon);
			holder.tv_require = (TextView) convertView.findViewById(R.id.tv_require);
			holder.tv_task_number = (TextView) convertView.findViewById(R.id.tv_task_number);
			holder.reward_money_index = (TextView) convertView.findViewById(R.id.reward_money_index);
			holder.tv_order_receiving = (TextView) convertView.findViewById(R.id.tv_order_receiving);
			holder.tv_delivery_time = (TextView) convertView.findViewById(R.id.tv_delivery_time);
			holder.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
			holder.tv_user_address = (TextView) convertView.findViewById(R.id.tv_user_address);
			holder.index_order_layout = (LinearLayout) convertView.findViewById(R.id.index_order_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final OrderInfo info = dataList.get(position);
		
		//容错处理,当数据未满一屏时，添加空数据使listview填充满一屏
		holder.index_order_layout.setVisibility(View.VISIBLE);
		if (TextUtils.isEmpty(info.getOrderNo())) {
			holder.index_order_layout.setVisibility(View.INVISIBLE);
			return convertView; 
		}
		
		holder.theme_text.setText(info.getOrder_theme());
		holder.tv_require.setText(info.getRemark());
			
		holder.tv_user_name.setText(info.getReceiveName());
		String persion = "";
		if (info.getCategoryId().equals(OrderUtil.ORDER_TYPE_TASK) || info.getCategoryId().equals(OrderUtil.ORDER_TYPE_COMMON)) {
			holder.tv_task_number.setVisibility(View.VISIBLE);
			holder.tv_user_address.setVisibility(View.GONE);
			holder.tv_task_number.setText("任务人数:" + info.getNeedPplQty() + "人");
			persion = "/人";
		} else {
			holder.tv_task_number.setVisibility(View.GONE);
			holder.tv_user_address.setVisibility(View.VISIBLE);
			holder.tv_user_address.setText("配送地址:" + info.getReceiveAddress());
			persion = "";
		}
		
		holder.tv_delivery_time.setText("送达时间:" + info.getServiceDate());
		try {
			holder.reward_money_index.setText("￥" + AmountUtils.changeF2Y(info.getDeliveryFee()) + persion);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (info.getDeliverySex().equals("p_gender_male")) {
			holder.iv_sex.setImageDrawable(mContext.getResources().getDrawable(R.drawable.profile_boy));
		} else if (info.getDeliverySex().equals("p_gender_female")) {
			holder.iv_sex.setImageDrawable(mContext.getResources().getDrawable(R.drawable.profile_girl));
		} else {
			holder.iv_sex.setVisibility(View.GONE);
		}
		
		if (info.getReceiveIcon().equals("")) {
			holder.receive_icon.setImageResource(R.drawable.icon_order_detail);
		} else {
			MyApplication.getImageLoader().displayImage(info.getReceiveIcon(), holder.receive_icon, MyApplication.getInstance().getOptions(), null);
		}

		holder.tv_order_receiving.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (m_onRobClickListener != null) {
					m_onRobClickListener.onRobClickClick(v, info.getOrderNo(), info.getOrder_type());
				}
			}
		});
		
//		holder.receive_icon.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(mContext, PersonalHomepageActivity.class);
//				intent.putExtra("appraiserOther", info.getReceiveId());
//				intent.putExtra("userIcon", info.getReceiveIcon());
//				intent.putExtra("userSex", info.getDeliverySex());
//				intent.putExtra("userName", info.getReceiveName());
//				mContext.startActivity(intent);
//			}
//		});

		return convertView;
	}

	class ViewHolder {
		TextView theme_text;
		ImageView iv_sex;
		ImageView receive_icon;
		TextView tv_task_number;
		TextView tv_require;
		TextView reward_money_index;
		TextView tv_order_receiving;
		TextView tv_delivery_time;
		TextView tv_user_name;
		TextView tv_user_address;
		LinearLayout index_order_layout;
	}

	/** 抢单 */
	public void setOnRobClickListener(onRobClickListener cl) {
		m_onRobClickListener = cl;
	}
	
	/** 接任务 */
	public void setOnAcceptClickListener(onAcceptInterface cl) {
		m_onAcceptClickListener = cl;
	}
	
	/** 查看任务详情 */
	public void setOnTaskDetailListener(onTaskDetailInterface cl) {
		m_onTaskDetailClickListener = cl;
	}
	
	public interface onRobClickListener {
		void onRobClickClick(View v, String orderId, String type);
	}
	
	public interface onAcceptInterface {
		void onAcceptClickClick(View v, String orderId, String type);
	}
	
	public interface onTaskDetailInterface {
		void onTaskDetailClick(View v, String orderId);
	}
}
