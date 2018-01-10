package com.zhiduan.crowdclient.adapter;

import java.util.ArrayList;
import java.util.List;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtilTools;
import com.zhiduan.crowdclient.util.BottomCallBackInterface.OnBottomClickListener;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/** 
 * 已签收适配器
 * 
 * @author yxx
 *
 * @date 2016-5-24 下午6:13:00
 * 
 */
public class SignedAdapter extends BaseAdapter{

	private List<OrderInfo> dataList=new ArrayList<OrderInfo>();
	private Context mContext;
	private ViewHolder holder;
	private OnBottomClickListener mListener;

	public SignedAdapter(Context mContext, List<OrderInfo> dataList, OnBottomClickListener listener) {

		this.mContext = mContext;
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
			convertView = View.inflate(mContext, R.layout.item_order_signed, null);			
			holder = new ViewHolder();

			holder.layoutState1 = (RelativeLayout) convertView.findViewById(R.id.include_order_signed_1);
			holder.layoutState2 = (RelativeLayout) convertView.findViewById(R.id.include_order_signed_2);

			holder.layoutDetail = (LinearLayout) convertView.findViewById(R.id.item_order_sign_layout_detail);

			holder.icon = (ImageView) convertView.findViewById(R.id.item_order_sign_icon);
			holder.sex = (ImageView) convertView.findViewById(R.id.item_order_sign_sex);
			holder.imgTrans = (ImageView) convertView.findViewById(R.id.item_order_signed_img_trans);
			holder.imgSystem = (ImageView) convertView.findViewById(R.id.item_order_signed_img_system);
			holder.imgBuild = (ImageView) convertView.findViewById(R.id.item_order_sign_img_buildFlag);

			holder.status = (TextView) convertView.findViewById(R.id.item_order_sign_status);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.item_order_sign_title);

			holder.tvTitle1 = (TextView) convertView.findViewById(R.id.item_order_sign_title_1);
			holder.tvTitle2 = (TextView) convertView.findViewById(R.id.item_order_sign_title_2);
			holder.tvTitle3 = (TextView) convertView.findViewById(R.id.item_order_sign_title_3);

			holder.tv1 = (TextView) convertView.findViewById(R.id.item_order_sign_1);
			holder.tv2 = (TextView) convertView.findViewById(R.id.item_order_sign_2);
			holder.tv3 = (TextView) convertView.findViewById(R.id.item_order_sign_3);

			holder.name = (TextView) convertView.findViewById(R.id.item_order_sign_name);
			holder.remark = (TextView) convertView.findViewById(R.id.item_order_sign_remark);
			holder.fee = (TextView) convertView.findViewById(R.id.item_order_sign_fee);

			holder.complain = (Button) convertView.findViewById(R.id.item_order_signed_complain);
			holder.backComplain = (Button) convertView.findViewById(R.id.include_order_wati_2_revoke);
			holder.handle = (Button) convertView.findViewById(R.id.item_order_signed_thought);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		OrderInfo info = dataList.get(position);

//		holder.tvTitle.setText(info.getTheme());
//		holder.name.setText(info.getReceiveName());

//		if(!info.getCategoryId().equals(OrderUtil.ORDER_TYPE_COMMON)){
//			holder.fee.setText(OrderUtil.changeF2Y(info.getDeliveryFee(), 0));
//		}else{
//			holder.fee.setText(OrderUtil.changeF2Y(info.getDeliveryFee(), 1));
//		}

		if(TextUtils.isEmpty(info.getPlusRemark())){
			holder.remark.setVisibility(View.GONE);
		}else{
			holder.remark.setVisibility(View.VISIBLE);
			holder.remark.setText(info.getPlusRemark());
		}

//		holder.tv1.setText(info.getTakerId());
//		OrderUtilTools.setOrderContentByType(info, holder.tvTitle2, holder.tv2, holder.tvTitle3, holder.tv3);

		if(info.isBuildFlag()){
			holder.imgBuild.setVisibility(View.VISIBLE);
		}else{
			holder.imgBuild.setVisibility(View.GONE);
		}

		if(info.isBeEvaluated()){
			holder.status.setVisibility(View.VISIBLE);
			holder.layoutState1.setVisibility(View.GONE);
			holder.layoutState2.setVisibility(View.GONE);
		}else{
			holder.status.setVisibility(View.INVISIBLE);

			if(info.getLockState() == 0){//订单处于初始状态
				holder.layoutState1.setVisibility(View.VISIBLE);
				holder.layoutState2.setVisibility(View.GONE);
			}else if(info.getLockState() == 2){//提交申诉中
				holder.layoutState1.setVisibility(View.GONE);
				holder.layoutState2.setVisibility(View.VISIBLE);
				
				if(info.getReportRole() == 1){
					holder.backComplain.setVisibility(View.GONE);
				}else{
					holder.backComplain.setVisibility(View.VISIBLE);
				}
			}
		}

		holder.imgTrans.setVisibility(View.GONE);
		holder.imgSystem.setVisibility(View.GONE);
		if(info.getAssignCode() == 3){
			holder.imgTrans.setVisibility(View.VISIBLE);
		}else if(info.getAssignCode() == 2 || info.getAssignCode() == 4 || info.getAssignCode() == 5){
			holder.imgSystem.setVisibility(View.VISIBLE);
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

		holder.handle.setOnClickListener(new OnClickListener() {

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

		public RelativeLayout layoutState1;
		public RelativeLayout layoutState2;

		public LinearLayout layoutDetail;

		public TextView status;
		public ImageView icon;//头像
		public ImageView sex;//性别图标
		public ImageView imgTrans;//转单标记
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
		public TextView fee;

		public Button complain;//提交申诉
		public Button backComplain;//撤销申诉
		public Button handle;//订单处理
	}	

}
