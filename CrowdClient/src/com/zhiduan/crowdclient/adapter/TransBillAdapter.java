package com.zhiduan.crowdclient.adapter;

import java.util.ArrayList;
import java.util.List;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.CrowedUserInfo;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * 待取件适配器
 * 
 * @author yxx
 *
 * @date 2016-5-24 下午6:13:00
 * 
 */
public class TransBillAdapter extends BaseAdapter{

	private List<CrowedUserInfo> dataList=new ArrayList<CrowedUserInfo>();
	private Context mContext;
	private ViewHolder holder;

	public TransBillAdapter(Context mContext, List<CrowedUserInfo> dataList) {

		this.mContext = mContext;
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
			convertView = View.inflate(mContext, R.layout.item_transbill, null);			
			holder = new ViewHolder();

			holder.layoutDetail = (LinearLayout) convertView.findViewById(R.id.item_transbill_detail);

			holder.icon = (ImageView) convertView.findViewById(R.id.item_tranlbill_icon);
			holder.name = (TextView) convertView.findViewById(R.id.item_tranlbill_name);
			holder.sex = (ImageView) convertView.findViewById(R.id.item_tranlbill_sex);
			holder.phone = (TextView) convertView.findViewById(R.id.item_tranlbill_phone);

			holder.layoutCall = (LinearLayout) convertView.findViewById(R.id.layout_transbill_call);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		CrowedUserInfo info = dataList.get(position);

//		holder.phone.setText(info.getPhone());
//		holder.name.setText(info.getRealName());

//		if(info.getGender().equals("男")){
//			holder.sex.setBackgroundResource(R.drawable.profile_boy);
//			if(!TextUtils.isEmpty(info.getIcon())){
//				MyApplication.getImageLoader().displayImage(info.getIcon(), holder.icon, MyApplication.getInstance().getOptions(), null);
//			}else{
//				holder.icon.setImageResource(R.drawable.head_portrait);
//			}
//		}else{
//			holder.sex.setBackgroundResource(R.drawable.profile_girl);
//			if(!TextUtils.isEmpty(info.getIcon())){
//				MyApplication.getImageLoader().displayImage(info.getIcon(), holder.icon, MyApplication.getInstance().getOptions(), null);
//			}else{
//				holder.icon.setImageResource(R.drawable.head_portrait);
//			}
//		}

		holder.layoutDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});

		holder.layoutCall.setOnClickListener(new OnClickListener() {

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
		public TextView phone;//手机号

		public LinearLayout layoutCall;//拨打电话
	}	

	/**
	 * 单击事件监听器
	 */
	private OnBottomClickListener mListener = null;

	public void setOnBottomClickListener(OnBottomClickListener listener) {
		mListener = listener;
	}

	public interface OnBottomClickListener {
		void onBottomClick(View v, int position);
	}

}
