package com.zhiduan.crowdclient.share;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.AbnormalInfo;
import com.zhiduan.crowdclient.data.InviteInfo;
import com.zhiduan.crowdclient.data.TaskInfo;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.Constant;

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
 * <pre>
 * Description	邀请列表适配器
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-11-26 下午3:26:00  
 * </pre>
 */
public class InviteAdapter extends BaseAdapter{

	private List<InviteInfo> dataList=new ArrayList<InviteInfo>();
	private Context mContext;
	private ViewHolder holder;
	private int invite_type;//type：1是邀请人数列表,2邀请收入列表
	private InviteInfo info;

	public InviteAdapter(Context mContext, List<InviteInfo> dataList,int type) {

		this.mContext = mContext;
		this.dataList = dataList;
		this.invite_type=type;
		
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
			convertView = View.inflate(mContext, R.layout.item_invite, null);			
			holder = new ViewHolder();
			holder.tv_invite_xiaopai_grade=(TextView) convertView.findViewById(R.id.tv_invite_xiaopai_grade);
//			holder.tv_invite_user_income=(TextView) convertView.findViewById(R.id.tv_invite_user_income);
			holder.tv_invite_xiaopai_income=(TextView) convertView.findViewById(R.id.tv_invite_xiaopai_income);
			holder.iv_invite_icon=(ImageView) convertView.findViewById(R.id.iv_invite_icon);
			holder.tv_invite_date=(TextView) convertView.findViewById(R.id.tv_invite_date);
			holder.tv_invite_name=(TextView) convertView.findViewById(R.id.tv_invite_name);
			holder.ll_invite_list=(LinearLayout) convertView.findViewById(R.id.ll_invite_list);
			holder.ll_invite_user=(LinearLayout) convertView.findViewById(R.id.ll_invite_user);
			holder.ll_invite_xiaopai=(LinearLayout) convertView.findViewById(R.id.ll_invite_xiaopai);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		info=dataList.get(position);
		
		
		if (invite_type==1) {
			//日期显示:邀请人数显示年月日，邀请收入显示年月日时分秒
			//性别只有邀请人列表才显示
//			holder.tv_invite_income.setVisibility(View.GONE);
			holder.ll_invite_list.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(info.getInvite_date())) {
				holder.tv_invite_date.setText(info.getInvite_date().substring(0, 10)+"  加入");
			}
			
		}else {
			
			if (info.getExpPoint()==0) {
				holder.ll_invite_user.setVisibility(View.VISIBLE);
			}else {
				holder.ll_invite_xiaopai.setVisibility(View.VISIBLE);
				holder.tv_invite_xiaopai_grade.setText(info.getExpPoint()+"积分");
				try {
					holder.tv_invite_xiaopai_income.setText(AmountUtils.changeF2Y(info.getInvite_income())==null?"":"￥"+AmountUtils.changeF2Y(info.getInvite_income()));
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
			if (!TextUtils.isEmpty(info.getInvite_date())) {
				holder.tv_invite_date.setText(info.getInvite_date().substring(0, 10)+"  加入");
			}
			
		}
		
		holder.tv_invite_name.setText(info.getInvite_name());
//		holder.tv_invite_user_income.setText(info.getInvite_phone());
		if (!TextUtils.isEmpty(info.getInvite_icon())) {
				MyApplication.getInstance().getImageLoader().displayImage(info.getInvite_icon(), holder.iv_invite_icon, MyApplication.getInstance().getOptions(), null);
		}
		return convertView;
	}

	class ViewHolder {
		public TextView tv_invite_xiaopai_income;//邀请小派获取的奖励
		public TextView tv_invite_user_income;//邀请用户获取的奖励
		public TextView tv_invite_xiaopai_grade;//邀请小派获取的积分
		public LinearLayout ll_invite_xiaopai;
		public LinearLayout ll_invite_user;
		public LinearLayout ll_invite_list;//邀请人列表
		public ImageView iv_invite_icon;//头像
		public TextView tv_invite_name;//用户名
		public TextView tv_invite_date;//邀请日期
	}	

	/**
	 * 单击事件监听器
	 */
//	private OnBottomClickListener mListener = null;
//
//	public void setOnBottomClickListener(OnBottomClickListener listener) {
//		mListener = listener;
//	}
//
//	public interface OnBottomClickListener {
//		void onBottomClick(View v, int position);
//	}
}
