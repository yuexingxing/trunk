package com.zhiduan.crowdclient.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.AbnormalInfo;
import com.zhiduan.crowdclient.util.BottomCallBackInterface.OnBottomClickListener;
import com.zhiduan.crowdclient.util.OrderUtil;

/** 
 * 异常件适配器
 * 
 * @author yxx
 *
 * @date 2016-5-24 下午6:13:00
 * 
 */
public class AbnormalAdapter extends BaseAdapter{

	private List<AbnormalInfo> dataList=new ArrayList<AbnormalInfo>();
	private Context mContext;
	private ViewHolder holder;
	private OnBottomClickListener mListener;
	
	public AbnormalAdapter(Context mContext, List<AbnormalInfo> dataList, OnBottomClickListener listener) {

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
			convertView = View.inflate(mContext, R.layout.item_order_abnormal, null);			
			holder = new ViewHolder();

			holder.layoutDetail = (LinearLayout) convertView.findViewById(R.id.item_order_abnormal_layout_detail);
			
			holder.icon = (ImageView) convertView.findViewById(R.id.item_order_abnormal_icon);
			holder.name = (TextView) convertView.findViewById(R.id.item_order_abnormal_name);
			holder.sex = (ImageView) convertView.findViewById(R.id.item_order_abnormal_sex);
			holder.type = (ImageView) convertView.findViewById(R.id.item_order_abnormal_type);
			holder.phone = (TextView) convertView.findViewById(R.id.item_order_abnormal_phone);

			holder.status = (TextView) convertView.findViewById(R.id.item_order_abnormal_status);
			holder.reason = (TextView) convertView.findViewById(R.id.item_order_abnormal_typename);
			holder.time = (TextView) convertView.findViewById(R.id.item_order_abnormal_time);
			
			holder.back = (Button) convertView.findViewById(R.id.item_order_abnormal_back);
			holder.edit = (Button) convertView.findViewById(R.id.item_order_abnormal_edit);
			holder.cancel = (Button) convertView.findViewById(R.id.item_order_abnormal_cancel);
			
			holder.layoutBottom = (LinearLayout) convertView.findViewById(R.id.layout_order_abnormal_bottom);
			holder.assignType = (ImageView) convertView.findViewById(R.id.item_order_abnormal_assign_type);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		AbnormalInfo info = dataList.get(position);
		if(info.getSex().equals("男") || info.getSex().equals(OrderUtil.MALE)){
			holder.sex.setBackgroundResource(R.drawable.profile_boy);
			if(!TextUtils.isEmpty(info.getIcon())){
				MyApplication.getInstance().getImageLoader().displayImage(info.getIcon(), holder.icon, MyApplication.getInstance().getOptions(), null);
			}else{
				holder.icon.setImageResource(R.drawable.male);
			}
		}else{
			holder.sex.setBackgroundResource(R.drawable.profile_girl);
			if(!TextUtils.isEmpty(info.getIcon())){
				MyApplication.getInstance().getImageLoader().displayImage(info.getIcon(), holder.icon, MyApplication.getInstance().getOptions(), null);
			}else{
				holder.icon.setImageResource(R.drawable.female);
			}
		}
		
		if(info.getType().equals(OrderUtil.PACKET)){
			holder.type.setImageResource(R.drawable.homepage_generation);
		}else{
			holder.type.setImageResource(R.drawable.homepage_generation);
		}
		
		//分单状态,3 ：众包转单， 4：定向派单
		if(info.getAssignCode().equals("3")){
			holder.assignType.setBackgroundResource(R.drawable.homepage_assign_type);
		}else if(info.getAssignCode().equals("2") || info.getAssignCode().equals("4") || info.getAssignCode().equals("5")){
			holder.assignType.setBackgroundResource(R.drawable.homepage_system_dis);
		}else{
			holder.assignType.setBackgroundResource(0);
		}
		
		holder.name.setText(info.getName());
		holder.phone.setText(info.getPhone());
		
		holder.time.setText(info.getCreateTime());
		holder.status.setText(info.getState());
		holder.reason.setText(info.getProblemTypeName());
		
		if(info.getState().equals("已退件")){
			holder.layoutBottom.setVisibility(View.GONE);
		}else{
			holder.layoutBottom.setVisibility(View.VISIBLE);
		}

		holder.layoutDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});
		
		holder.back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});
		
		holder.edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});
		
		holder.cancel.setOnClickListener(new OnClickListener() {

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
		public TextView name;//姓名
		public ImageView sex;//性别图标
		public ImageView type;//类型
		public TextView phone;//手机号

		public TextView status;//状态
		public TextView reason;//异常原因
		public TextView time;//申请
		
		public Button back;//退件
		public Button edit;//编辑
		public Button cancel;//取消
		
		public LinearLayout layoutBottom;
		public ImageView assignType;//分单类型
	}	

}
