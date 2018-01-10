package com.zhiduan.crowdclient.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.order.timecount.CountdownView;
import com.zhiduan.crowdclient.order.timecount.CountdownView.OnCountdownEndListener;
import com.zhiduan.crowdclient.util.BottomCallBackInterface.OnBottomClickListener;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtilTools;

/** 
 * 配送中适配器
 * 
 * @author yxx
 *
 * @date 2016-5-24 下午6:13:00
 * 
 */
public class DistributionAdapter extends BaseAdapter{

	private List<OrderInfo> dataList=new ArrayList<OrderInfo>();
	private Context mContext;
	private OnBottomClickListener mListener;

	private final SparseArray<MyViewHolder> mCountdownVHList;
	private Handler mHandler = new Handler();
	private Timer mTimer;
	private boolean isCancel = true;

	public DistributionAdapter(Context context, List<OrderInfo> dataList, OnBottomClickListener listener) {

		this.mContext = context;
		this.dataList = dataList;
		mListener = listener;

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
	public Object getItem(int arg0) {
		return dataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		MyViewHolder holder;

		if(convertView==null){
			convertView = View.inflate(mContext, R.layout.item_order_distribution, null);			
			holder = new MyViewHolder();

			holder.layoutState1 = (RelativeLayout) convertView.findViewById(R.id.include_order_dis_1);
			holder.layoutState2 = (RelativeLayout) convertView.findViewById(R.id.include_order_dis_2);
			holder.layoutState3 = (RelativeLayout) convertView.findViewById(R.id.include_order_dis_3);
			holder.layoutState4 = (RelativeLayout) convertView.findViewById(R.id.include_order_dis_4);

			holder.mCvCountdownView = (CountdownView) convertView.findViewById(R.id.item_order_wait_time_count);
			holder.layoutDetail = (LinearLayout) convertView.findViewById(R.id.item_order_dis_layout_detail);

			holder.icon = (ImageView) convertView.findViewById(R.id.item_order_dis_icon);
			holder.sex = (ImageView) convertView.findViewById(R.id.item_order_dis_sex);
			holder.imgTrans = (ImageView) convertView.findViewById(R.id.item_order_dis_img_trans);
			holder.imgSystem = (ImageView) convertView.findViewById(R.id.item_order_dis_img_system);
			holder.imgBuild = (ImageView) convertView.findViewById(R.id.item_order_dis_img_buildFlag);

			holder.tvTitle = (TextView) convertView.findViewById(R.id.item_order_dis_title);

			holder.tvTitle1 = (TextView) convertView.findViewById(R.id.item_order_dis_title_1);
			holder.tvTitle2 = (TextView) convertView.findViewById(R.id.item_order_dis_title_2);
			holder.tvTitle3 = (TextView) convertView.findViewById(R.id.item_order_dis_title_3);

			holder.tv1 = (TextView) convertView.findViewById(R.id.item_order_dis_1);
			holder.tv2 = (TextView) convertView.findViewById(R.id.item_order_dis_2);
			holder.tv3 = (TextView) convertView.findViewById(R.id.item_order_dis_3);

			holder.name = (TextView) convertView.findViewById(R.id.item_order_dis_name);
			holder.remark = (TextView) convertView.findViewById(R.id.item_order_dis_remark);
			holder.fee = (TextView) convertView.findViewById(R.id.item_order_dis_fee);

			holder.complain = (Button) convertView.findViewById(R.id.item_order_dis_complain);
			holder.backComplain = (Button) convertView.findViewById(R.id.include_order_wati_2_revoke);
			holder.trans = (Button) convertView.findViewById(R.id.item_order_dis_trans);
			holder.handle = (Button) convertView.findViewById(R.id.item_order_dis_handle);
			holder.complain_signed = (Button) convertView.findViewById(R.id.item_order_dis_complain_signed);

			convertView.setTag(holder);
		}else{
			holder = (MyViewHolder) convertView.getTag();
		}

		final OrderInfo info = dataList.get(position);
		holder.bindData(info);

		//处理倒计时
		if (info.getCountdown() > 0) {
			synchronized (mCountdownVHList) {
				mCountdownVHList.put(info.getTimeId(), holder);
			}
		}

//		holder.tvTitle.setText(info.getTheme());
//		holder.name.setText(info.getReceiveName());

//		if(!info.getCategoryId().equals(OrderUtil.ORDER_TYPE_COMMON)){
//			holder.fee.setText(OrderUtil.changeF2Y(info.getDeliveryFee(), 0));
//		}else{
//			holder.fee.setText(OrderUtil.changeF2Y(info.getDeliveryFee(), 1));
//		}
//
//		if(TextUtils.isEmpty(info.getPlusRemark())){
//			holder.remark.setVisibility(View.GONE);
//		}else{
//			holder.remark.setVisibility(View.VISIBLE);
//			holder.remark.setText(info.getPlusRemark());
//		}
//
//		holder.tv1.setText(info.getTakerId());
//		OrderUtilTools.setOrderContentByType(info, holder.tvTitle2, holder.tv2, holder.tvTitle3, holder.tv3);

		holder.mCvCountdownView.setOnCountdownEndListener(new OnCountdownEndListener() {

			@Override
			public void onEnd(CountdownView cv) {

				info.setLockState(0);
				notifyDataSetChanged();
			}
		});

		holder.imgTrans.setVisibility(View.GONE);
		if(info.getState() == 4 && info.getLockState() == 0){//订单完成
			holder.layoutState1.setVisibility(View.GONE);
			holder.layoutState2.setVisibility(View.GONE);
			holder.layoutState3.setVisibility(View.GONE);
			holder.layoutState4.setVisibility(View.VISIBLE);
		}else if(info.getLockState() == 0){//订单处于初始状态
			holder.layoutState1.setVisibility(View.VISIBLE);
			holder.layoutState2.setVisibility(View.GONE);
			holder.layoutState3.setVisibility(View.GONE);
			holder.layoutState4.setVisibility(View.GONE);
		}
		
		if(position == 0){//转单中
			holder.imgTrans.setVisibility(View.VISIBLE);
			holder.layoutState1.setVisibility(View.GONE);
			holder.layoutState2.setVisibility(View.GONE);
			holder.layoutState3.setVisibility(View.VISIBLE);
			holder.layoutState4.setVisibility(View.GONE);
		}
		
		if(position == 1){//提交申诉中
			holder.layoutState1.setVisibility(View.GONE);
			holder.layoutState2.setVisibility(View.VISIBLE);
			holder.layoutState3.setVisibility(View.GONE);
			holder.layoutState4.setVisibility(View.GONE);
			
//			if(info.getReportRole() == 1){
//				holder.backComplain.setVisibility(View.GONE);
//			}else{
				holder.backComplain.setVisibility(View.VISIBLE);
//			}
		}

		holder.imgSystem.setVisibility(View.GONE);
		if(info.getAssignCode() == 3){
			holder.imgTrans.setVisibility(View.VISIBLE);
		}else if(info.getAssignCode() == 2 || info.getAssignCode() == 4 || info.getAssignCode() == 5){
			holder.imgSystem.setVisibility(View.VISIBLE);
		}

		if(info.isBuildFlag()){
			holder.imgBuild.setVisibility(View.VISIBLE);
		}else{
			holder.imgBuild.setVisibility(View.GONE);
		}

		if(info.getGenderId().equals(OrderUtil.MALE)){
			holder.sex.setImageResource(R.drawable.profile_boy);
		}else{
			holder.sex.setImageResource(R.drawable.profile_girl);
		}

		if(!TextUtils.isEmpty(info.getReceiveIcon())){
			MyApplication.getImageLoader().displayImage(info.getReceiveIcon(), holder.icon, MyApplication.getOrderListOptions(), null);
		}

		holder.layoutDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});

		holder.complain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});

		holder.backComplain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});

		holder.trans.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});

		holder.handle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});

		holder.complain_signed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});

		return convertView;
	}

	class MyViewHolder {

		public OrderInfo mItemInfo;

		public RelativeLayout layoutState1;
		public RelativeLayout layoutState2;
		public RelativeLayout layoutState3;
		public RelativeLayout layoutState4;

		public LinearLayout layoutDetail;

		public ImageView icon;//头像
		public ImageView sex;//性别图标
		public ImageView imgTrans;//转单
		public ImageView imgSystem;//系统派单
		public ImageView imgBuild;//是否本楼层

		public TextView tvTitle;

		public TextView tvTitle1;
		public TextView tv1;

		public TextView tvTitle2;
		public TextView tv2;

		public TextView tvTitle3;
		public TextView tv3;

		public TextView name;
		public TextView remark;//备注
		public TextView fee;//赏金

		public Button complain;//提交申诉
		public Button backComplain;//撤销申诉
		public Button trans;//转单
		public Button handle;//订单处理
		public Button complain_signed;//提交申诉

		public CountdownView mCvCountdownView;

		public void bindData(OrderInfo itemInfo) {
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

		public OrderInfo getBean() {
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
