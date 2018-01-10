package com.zhiduan.crowdclient.adapter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.TransOrderInfo;
import com.zhiduan.crowdclient.order.timecount.CountdownView;
import com.zhiduan.crowdclient.order.timecount.CountdownView.OnCountdownEndListener;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.BottomCallBackInterface.OnBottomClickListener;

/**
 * 转单列表
 * Author:	hexiuhui
 */
public class DeliveryRemindAdapter extends BaseAdapter {

	private Context mContext;
	private List<TransOrderInfo> dataList;
	private OnBottomClickListener mListener;

	private final SparseArray<MyViewHolder> mCountdownVHList;
	private Handler mHandler = new Handler();
	private Timer mTimer;
	private boolean isCancel = true;

	public DeliveryRemindAdapter(Context context, List<TransOrderInfo> list, OnBottomClickListener listener) {

		this.dataList = list;
		this.mContext = context;
		this.mListener = listener;

		mCountdownVHList = new SparseArray<MyViewHolder>();
		startRefreshTime();
	}

	public void startRefreshTime() {
		if (!isCancel) return;

		if (null != mTimer) {
			mTimer.cancel();
		}

		isCancel = false;
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				mHandler.post(mRefreshTimeRunnable);
			}
		}, 0, 10);
	}

	public void cancelRefreshTime() {
		isCancel = true;
		if (null != mTimer) {
			mTimer.cancel();
		}
		mHandler.removeCallbacks(mRefreshTimeRunnable);
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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		MyViewHolder holder;

		if (convertView == null) {
			//			convertView = View.inflate(R.layout.delivery_remind_list_item, parent, false);
			convertView = LayoutInflater.from(mContext).inflate(R.layout.delivery_remind_list_item, parent, false);
			holder = new MyViewHolder();
			holder.initView(convertView);

			holder.layoutDetail = (LinearLayout) convertView.findViewById(R.id.item_remind_layout_detail);
			holder.layoutCheck = (LinearLayout) convertView.findViewById(R.id.item_remind_layout_check);

			holder.title = (TextView) convertView.findViewById(R.id.item_remind_title);
			holder.name = (TextView) convertView.findViewById(R.id.item_remind_name);
			holder.time = (TextView) convertView.findViewById(R.id.item_remind_time);
			holder.tips = (TextView) convertView.findViewById(R.id.item_remind_tips);
			holder.fee = (TextView) convertView.findViewById(R.id.item_remind_fee);

			holder.check = (ImageView) convertView.findViewById(R.id.item_remind_check);
			holder.icon = (ImageView) convertView.findViewById(R.id.item_remind_icon);
			holder.sex = (ImageView) convertView.findViewById(R.id.item_remind_sex);

			holder.refuse = (TextView) convertView.findViewById(R.id.item_remind_refuse);
			holder.ok = (TextView) convertView.findViewById(R.id.item_remind_ok);

			convertView.setTag(holder);
		} else {
			holder = (MyViewHolder) convertView.getTag();
		}

		final TransOrderInfo info = dataList.get(position);
//		holder.title.setText(info.getTheme());
//		holder.name.setText(info.getAssignUser());
//		holder.fee.setText(OrderUtil.changeF2Y(info.getReward(), 0));
//		holder.time.setText(OrderUtil.getBetweenDate(info.getServiceStartDate(), info.getServiceEndDate()));

		String strTrans = "%s %s请您帮忙处理订单";
//		holder.tips.setText(String.format(strTrans, info.getAssignUser(), info.getAssignMobile()));

		holder.bindData(info);

		//处理倒计时
		if (info.getCountdown() > 0) {
			synchronized (mCountdownVHList) {
				mCountdownVHList.put(info.getTimeId(), holder);
			}
		}

		if(info.getChecked().equals("1")){
			holder.check.setBackgroundResource(R.drawable.orders_select);
		}else{
			holder.check.setBackgroundResource(R.drawable.orders_not_selected);
		}

		if(!TextUtils.isEmpty(info.getAssignIcon())){
			MyApplication.getImageLoader().displayImage(info.getAssignIcon(), holder.icon, MyApplication.getOrderListOptions(), null);
		}

		holder.mCvCountdownView.setOnCountdownEndListener(new OnCountdownEndListener() {

			@Override
			public void onEnd(CountdownView cv) {

				if(mListener != null){
					mListener.onBottomClick(cv, 0);
				}
			}
		});

		holder.layoutDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});

		holder.layoutCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});

		holder.layoutCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});

		holder.refuse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});

		holder.ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});

		return convertView;
	}

	static class MyViewHolder {

		private CountdownView mCvCountdownView;
		private TransOrderInfo mItemInfo;

		public LinearLayout layoutDetail;
		public LinearLayout layoutCheck;

		public TextView title;
		public TextView name;
		public TextView time;
		public TextView tips;
		public TextView fee;

		public ImageView check;
		public ImageView icon;
		public ImageView sex;

		public TextView refuse;
		public TextView ok;

		public void initView(View convertView) {

			mCvCountdownView = (CountdownView) convertView.findViewById(R.id.item_remind_timecount);
		}

		public void bindData(TransOrderInfo itemInfo) {
			mItemInfo = itemInfo;

			if (itemInfo.getCountdown() > 0) {
				refreshTime(System.currentTimeMillis());
			} else {
				mCvCountdownView.allShowZero();
			}

		}

		public void refreshTime(long curTimeMillis) {
			if (null == mItemInfo || mItemInfo.getCountdown() <= 0) return;

			mCvCountdownView.updateShow(mItemInfo.getEndTime() - curTimeMillis);
		}

		public TransOrderInfo getBean() {
			return mItemInfo;
		}
	}

	private Runnable mRefreshTimeRunnable = new Runnable() {

		@Override
		public void run() {
			if (mCountdownVHList.size() == 0) return;

			synchronized (mCountdownVHList) {
				long currentTime = System.currentTimeMillis();
				int key;
				for (int i = 0; i < mCountdownVHList.size(); i++) {
					key = mCountdownVHList.keyAt(i);
					MyViewHolder curMyViewHolder = mCountdownVHList.get(key);
					if (currentTime >= curMyViewHolder.getBean().getEndTime()) {
						// 倒计时结束
						curMyViewHolder.getBean().setCountdown(0);
						mCountdownVHList.remove(key);
						notifyDataSetChanged();
					} else {
						curMyViewHolder.refreshTime(currentTime);
					}

				}
			}
		}
	};

}
