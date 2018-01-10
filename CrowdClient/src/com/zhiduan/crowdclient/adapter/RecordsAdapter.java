package com.zhiduan.crowdclient.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.CrowedUserInfo;
import com.zhiduan.crowdclient.data.RecordSDetail;
import com.zhiduan.crowdclient.data.RecordsInfo;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Logs;
/**
 * 龙虎榜
 * 
 * @author hexiuhui
 * 
 */
@SuppressLint("NewApi")
public class RecordsAdapter extends BaseAdapter {

	private Context mContext;
	private List<RecordsInfo> mData;
	private Map<Integer, List<RecordSDetail>>decsMap=new HashMap<Integer, List<RecordSDetail>>();
	private List<RecordSDetail> recordsDetails;
	public RecordsAdapter(Context context, List<RecordsInfo> data,Map<Integer, List<RecordSDetail>> map) {
		mContext = context;
		mData = data;
		decsMap=map;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {

		return mData.get(position);

	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_wallet_record, null);
			holder = new ViewHolder();
			
			holder.records_money = (TextView) convertView.findViewById(R.id.item_wallet_record_tv_money);
			holder.records_name = (TextView) convertView.findViewById(R.id.item_wallet_record_tv_type);
			holder.records_time = (TextView) convertView.findViewById(R.id.item_wallet_record_tv_time);

			holder.tv_record_one=(TextView) convertView.findViewById(R.id.tv_record_one);
			holder.tv_money_one=(TextView) convertView.findViewById(R.id.tv_money_one);
			holder.tv_record_two=(TextView) convertView.findViewById(R.id.tv_record_two);
			holder.tv_money_two=(TextView) convertView.findViewById(R.id.tv_money_two);
			holder.tv_record_three=(TextView) convertView.findViewById(R.id.tv_record_three);
			holder.tv_money_three=(TextView) convertView.findViewById(R.id.tv_money_three);
			holder.tv_record_four=(TextView) convertView.findViewById(R.id.tv_record_four);
			holder.tv_money_four=(TextView) convertView.findViewById(R.id.tv_money_four);

			holder.ll_income_details=(LinearLayout) convertView.findViewById(R.id.ll_income_details);
			holder.ll_record_two=(LinearLayout) convertView.findViewById(R.id.ll_record_two);
			holder.ll_record_three=(LinearLayout) convertView.findViewById(R.id.ll_record_three);
			holder.ll_record_four=(LinearLayout) convertView.findViewById(R.id.ll_record_four);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		RecordsInfo info = mData.get(position);
		if (info.isIs_open()) {
			holder.ll_income_details.setVisibility(View.VISIBLE);
		}else {
			holder.ll_income_details.setVisibility(View.GONE);
		}

		holder.records_time.setText(info.getCreateTime());
		String payType = "充值";
		String type = "+";
		int rgb = Color.rgb(51, 51, 51);
		switch (info.getPayType()) {
		case 1:
			payType = "充值";
			type = "";
			rgb = Color.rgb(238, 176, 38);
			break;
		case 2:
			payType = "提现";
			type = "";
			rgb = Color.rgb(238, 176, 38);
			break;
		case 3:
			payType = "订单收入";
			type = "+";
			rgb = Color.rgb(51, 51, 51);
			break;
		case 4:
			payType = "提成收入";
			type = "+";
			rgb = Color.rgb(51, 51, 51);
			break;
		case 5:
			payType = "奖励收入";
			type = "+";
			rgb = Color.rgb(51, 51, 51);
			break;
		case 6:
			payType = "钱包支付";
			type = "";
			rgb = Color.rgb(238, 176, 38);
			break;
		case 7:
			payType = "退款";
			type = "+";
			rgb = Color.rgb(51, 51, 51);

			break;

		default:
			break;
		}
//		recordsDetails=decsMap.get(position);
		recordsDetails = mData.get(position).getDescList();
		
		if (recordsDetails!=null&&recordsDetails.size()>0) {
			
		switch (recordsDetails.size()) {
		case 1:
			holder.ll_record_two.setVisibility(View.INVISIBLE);
			holder.ll_record_three.setVisibility(View.INVISIBLE);
			holder.ll_record_four.setVisibility(View.INVISIBLE);


			if (!TextUtils.isEmpty(recordsDetails.get(0)
					.getRemark())) {
				holder.tv_record_one.setVisibility(View.VISIBLE);
				holder.tv_record_one.setText(
						recordsDetails.get(0).getRemark());
			}
			if (recordsDetails.get(0).getOptAmount() != 0) {
				holder.tv_money_one.setVisibility(View.VISIBLE);
				holder.tv_money_one.setText(CommandTools.longTOString(
						recordsDetails.get(0)
						.getOptAmount()) + "");
			}
			break;
		case 2:
			holder.ll_record_three.setVisibility(View.INVISIBLE);
			holder.ll_record_four.setVisibility(View.INVISIBLE);

			if (!TextUtils.isEmpty(recordsDetails.get(0)
					.getRemark())) {
				holder.tv_record_one.setVisibility(View.VISIBLE);
				holder.tv_record_one.setText(
						recordsDetails.get(0).getRemark());
			}
			if (recordsDetails.get(0).getOptAmount() != 0) {
				holder.tv_money_one.setVisibility(View.VISIBLE);
				holder.tv_money_one.setText(CommandTools.longTOString(
						recordsDetails.get(0)
						.getOptAmount() )+ "");
			}
			if (!TextUtils.isEmpty(recordsDetails.get(1)
					.getRemark())) {
				holder.tv_record_two.setVisibility(View.VISIBLE);
				holder.tv_record_two.setText(
						recordsDetails.get(1).getRemark());
			}
			if (recordsDetails.get(1).getOptAmount() != 0) {
				holder.tv_money_two.setVisibility(View.VISIBLE);
				holder.tv_money_two.setText(CommandTools.longTOString(
						recordsDetails.get(1)
						.getOptAmount()) + "");
			}
			break;
		case 3:
			holder.ll_record_four.setVisibility(View.INVISIBLE);
			if (!TextUtils.isEmpty(recordsDetails.get(0)
					.getRemark())) {
				holder.tv_record_one.setVisibility(View.VISIBLE);
				holder.tv_record_one.setText(
						recordsDetails.get(0).getRemark());
			}
			if (recordsDetails.get(0).getOptAmount() != 0) {
				holder.tv_money_one.setVisibility(View.VISIBLE);
				holder.tv_money_one.setText(CommandTools.longTOString(
						recordsDetails.get(0)
						.getOptAmount()) + "");
			}
			if (!TextUtils.isEmpty(recordsDetails.get(1)
					.getRemark())) {
				holder.tv_record_two.setVisibility(View.VISIBLE);
				holder.tv_record_two.setText(
						recordsDetails.get(1).getRemark());
			}
			if (recordsDetails.get(1).getOptAmount() != 0) {
				holder.tv_money_two.setVisibility(View.VISIBLE);
				holder.tv_money_two.setText(CommandTools.longTOString(
						recordsDetails.get(1)
						.getOptAmount()) + "");
			}
			if (!TextUtils.isEmpty(recordsDetails.get(2)
					.getRemark())) {
				holder.tv_record_three.setVisibility(View.VISIBLE);
				holder.tv_record_three.setText(
						recordsDetails.get(2).getRemark());
			}
			if (recordsDetails.get(2).getOptAmount() != 0) {
				holder.tv_money_three.setVisibility(View.VISIBLE);
				holder.tv_money_three.setText(CommandTools.longTOString(
						recordsDetails.get(2)
						.getOptAmount() )+ "");
			}
			break;
		case 4:
			if (!TextUtils.isEmpty(recordsDetails.get(0)
					.getRemark())) {
				holder.tv_record_one.setVisibility(View.VISIBLE);
				holder.tv_record_one.setText(
						recordsDetails.get(0).getRemark());
			}
			if (recordsDetails.get(0).getOptAmount() != 0) {
				holder.tv_money_one.setVisibility(View.VISIBLE);
				holder.tv_money_one.setText(CommandTools.longTOString(
						recordsDetails.get(0)
						.getOptAmount() )+ "");
			}
			if (!TextUtils.isEmpty(recordsDetails.get(1)
					.getRemark())) {
				holder.tv_record_two.setVisibility(View.VISIBLE);
				holder.tv_record_two.setText(
						recordsDetails.get(1).getRemark());
			}
			if (recordsDetails.get(1).getOptAmount() != 0) {
				holder.tv_money_two.setVisibility(View.VISIBLE);
				holder.tv_money_two.setText(CommandTools.longTOString(
						recordsDetails.get(1)
						.getOptAmount() )+ "");
			}
			if (!TextUtils.isEmpty(recordsDetails.get(2)
					.getRemark())) {
				holder.tv_record_three.setVisibility(View.VISIBLE);
				holder.tv_record_three.setText(
						recordsDetails.get(2).getRemark());
			}
			if (recordsDetails.get(2).getOptAmount() != 0) {
				holder.tv_money_three.setVisibility(View.VISIBLE);
				holder.tv_money_three.setText(CommandTools.longTOString(
						recordsDetails.get(2)
						.getOptAmount()) + "");
			}
			if (!TextUtils.isEmpty(recordsDetails.get(3)
					.getRemark())) {
				holder.tv_record_four.setVisibility(View.VISIBLE);
				holder.tv_record_four.setText(
						recordsDetails.get(3).getRemark());
			}
			if (recordsDetails.get(3).getOptAmount() != 0) {
				holder.tv_money_four.setVisibility(View.VISIBLE);
				holder.tv_money_four.setText(CommandTools.longTOString(
						recordsDetails.get(3)
						.getOptAmount() )+ "");
			}
			break;

		default:
			break;
		}
		}
		holder.records_name.setText(payType);
		holder.records_money.setTextColor(rgb);
		holder.records_money.setText(type+CommandTools.longTOString(info.getOptAmount()));
		return convertView;
	}

	class ViewHolder {
		public TextView records_money;
		public TextView records_name;
		public TextView records_time;

		public TextView tv_record_one;
		public TextView tv_money_one;
		public TextView tv_record_two;
		public TextView tv_money_two;
		public TextView tv_record_three;
		public TextView tv_money_three;
		public TextView tv_record_four;
		public TextView tv_money_four;

		public LinearLayout ll_income_details;
		public LinearLayout ll_record_two;
		public LinearLayout ll_record_three;
		public LinearLayout ll_record_four;

	}
}
