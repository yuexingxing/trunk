package com.zhiduan.crowdclient.adapter;

import java.util.ArrayList;
import java.util.List;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.GetGoodsInfo;
import com.zhiduan.crowdclient.util.OrderUtil;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * 确认取件适配器
 * 
 * @author yxx
 *
 * @date 2016-5-24 下午6:13:00
 * 
 */
public class GetGoodsAdapter extends BaseAdapter{

	private List<GetGoodsInfo> dataList=new ArrayList<GetGoodsInfo>();
	private Context mContext;
	private ViewHolder holder;

	public GetGoodsAdapter(Context mContext, List<GetGoodsInfo> dataList) {

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
			convertView = View.inflate(mContext, R.layout.item_get_goods, null);			
			holder = new ViewHolder();

			holder.billocde = (TextView) convertView.findViewById(R.id.tv_get_goods_billcode);
			holder.expressName = (TextView) convertView.findViewById(R.id.tv_get_goods_express_name);
			holder.stack = (TextView) convertView.findViewById(R.id.tv_get_goods_stack);
			
			holder.billocdeTitle = (TextView) convertView.findViewById(R.id.tv_get_goods_billcode_title);
			holder.expressNameTitle = (TextView) convertView.findViewById(R.id.tv_get_goods_express_title);
			holder.stackTitle = (TextView) convertView.findViewById(R.id.tv_get_goods_stack_title);
			
			holder.layout = (LinearLayout) convertView.findViewById(R.id.layout_item_getgoods);
			holder.check = (ImageView) convertView.findViewById(R.id.tv_get_goods_4);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		GetGoodsInfo info = dataList.get(position);
		
		if(info.getType().equals(OrderUtil.PACKET)){
			
			holder.billocdeTitle.setText(mContext.getResources().getString(R.string.billcode));
			holder.expressNameTitle.setText(mContext.getResources().getString(R.string.express_name));
			holder.stackTitle.setText(mContext.getResources().getString(R.string.stack_no));
			
			holder.billocde.setText(info.getWaybillCode());
			holder.expressName.setText(info.getExpressName());
			holder.stack.setText(info.getStack());
		}else{
			
			holder.billocdeTitle.setText(mContext.getResources().getString(R.string.phone_num));
			holder.expressNameTitle.setText(mContext.getResources().getString(R.string.store_name));
			holder.stackTitle.setText(mContext.getResources().getString(R.string.get_no));
			
			holder.billocde.setText(info.getPhone());
			holder.expressName.setText(info.getStoreName());
			holder.stack.setText(info.getPickupCode());
		}
		
		if(info.isScaned()){
			holder.check.setImageResource(R.drawable.garden_pigeon);
		}else{
			holder.check.setImageResource(R.drawable.garden_heart);
		}

		holder.layout.setOnClickListener(new OnClickListener() {

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
		
		public TextView billocdeTitle;
		public TextView expressNameTitle;
		public TextView stackTitle;

		public TextView billocde;
		public TextView expressName;
		public TextView stack;

		public LinearLayout layout;
		public ImageView check;
		
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
