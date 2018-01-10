package com.zhiduan.crowdclient.menuindex;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.activity.MainActivity;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.menuorder.OrderDetailGrabActivity;
import com.zhiduan.crowdclient.menuorder.RunningDetailGrabActivity;
import com.zhiduan.crowdclient.menuorder.task.TaskDetailGrabActivity;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.OrderService;
import com.zhiduan.crowdclient.util.AmountUtils;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.GlobalInstanceStateHelper;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;
import com.zhiduan.crowdclient.view.XCRoundRectImageView;

/**
 * Description 经验加成
 * Author: hexiuhui
 */
public class ExperienceAdditionActivity extends BaseActivity implements IXListViewListener{

	private int refresh = 0;
	private int task_pageNumber = 1;
	private LoadTextNetTask mTaskRequestRobOrder;
	private LoadTextNetTask mTaskRequestGetOrderTask;
	
	private LinearLayout mNotOrderLayout;
	private DropDownListView mExperienceListView;
	private ExperienceAdapter mExperienceAdapter;
	private List<OrderInfo> mExperienceData = new ArrayList<OrderInfo>();
	
	private int categoryId;
	private String ruleCode;
	
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_experience_additionl, ExperienceAdditionActivity.this);
	}

	@Override
	public void initView() {
		mNotOrderLayout = (LinearLayout) findViewById(R.id.no_order_layout);
		mExperienceListView = (DropDownListView) findViewById(R.id.lv_experience_list);
		mExperienceListView.setPullLoadEnable(true);
		mExperienceListView.setPullRefreshEnable(true);
		mExperienceListView.setXListViewListener(this);
	}

	@Override
	public void initData() {
		categoryId = getIntent().getIntExtra("categoryId", 0);
		ruleCode = getIntent().getStringExtra("ruleCode");
		if (!"".equals(ruleCode)) {
			setTitle("经验加成");
		} else {
			if (categoryId == OrderUtil.EXPRESS_TYPE) {
				setTitle("快递");
			} else if (categoryId == OrderUtil.ERRANDS_TYPE) {
				setTitle("跑腿");
			} else if (categoryId == OrderUtil.TASK_TYPE) {
				setTitle("任务");
			} else if (categoryId == OrderUtil.PRODUCT_TYPE) {
				setTitle("商品");
			}
		}
		
		mExperienceAdapter = new ExperienceAdapter(ExperienceAdditionActivity.this, mExperienceData);
		mExperienceListView.setAdapter(mExperienceAdapter);
		
		mExperienceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				OrderInfo orderInfo = mExperienceData.get(position - 1);

				Intent intent = null;
				if (orderInfo.getCategoryId().equals(OrderUtil.ORDER_TYPE_PACKET)) {
					intent = new Intent(ExperienceAdditionActivity.this, OrderDetailGrabActivity.class);
				} else if (orderInfo.getCategoryId().equals(OrderUtil.ORDER_TYPE_AGENT)) {
					intent = new Intent(ExperienceAdditionActivity.this, RunningDetailGrabActivity.class);
				} else if (orderInfo.getCategoryId().startsWith(OrderUtil.ORDER_TYPE_RUN)) {
					intent = new Intent(ExperienceAdditionActivity.this, RunningDetailGrabActivity.class);
				} else if (orderInfo.getCategoryId().startsWith(OrderUtil.ORDER_TYPE_RUN)) {
					intent = new Intent(ExperienceAdditionActivity.this, RunningDetailGrabActivity.class);
				} else if (orderInfo.getCategoryId().equals(OrderUtil.ORDER_TYPE_COMMON)) {
					intent = new Intent(ExperienceAdditionActivity.this, TaskDetailGrabActivity.class);
				} else {
					intent = new Intent(ExperienceAdditionActivity.this, RunningDetailGrabActivity.class);
				}

				intent.putExtra("orderType", orderInfo.getCategoryId());
				intent.putExtra("orderId", orderInfo.getOrderNo());
				startActivity(intent);
			}
		});
		
		mExperienceAdapter.setOnRobClickListener(new onRobClickListener() {
			@Override
			public void onRobClickClick(View v, String orderId, String type) {
				mTaskRequestRobOrder = requestGobOrder(orderId, type);
			}
		});
	}
	
	public void onResume() {
		super.onResume();
		getOneData();
	}
	
	protected LoadTextNetTask requestGetOrderData(int categoryId, String ruleCode, int pageNumber) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestGetOrderTask = null;

				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONObject dataObj = jsonObj.getJSONObject("data");
							JSONArray jsonArray = dataObj.getJSONArray("responseDto");

							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								String theme = jsonObject.getString("theme");
								String icon = jsonObject.getString("icon");
								String startDeliverTime = jsonObject.getString("serviceStartDate");
								String endDeliverTime = jsonObject.getString("serviceEndDate");
								String orderId = jsonObject.getString("orderId");
								String sex = jsonObject.getString("sex");
								String address = jsonObject.getString("address");
								String remark = jsonObject.getString("remark");
								String name = jsonObject.getString("name");
								int categoryId = jsonObject.getInt("categoryId");
								long income = jsonObject.getLong("income");

								// 提取日期中的小时分钟
								OrderInfo info = new OrderInfo();
								info.setOrder_theme(theme);
								info.setDeliverySex(sex);
								info.setDeliveryFee(income);
								info.setReceiveIcon(icon);
								info.setReceiveName(name);
								info.setRemark(remark); 
								info.setCategoryId(categoryId+"");
								info.setReceiveAddress(address);
								info.setOrderNo(orderId);
								info.setDeliveryTime(OrderUtil.getBetweenTime(startDeliverTime, endDeliverTime));

								mExperienceData.add(info);
							}

							mExperienceAdapter.notifyDataSetChanged();

							refreshInit();

							if (jsonArray.length() < 10) {
								mExperienceListView.setLoadHide();
							} else {
								mExperienceListView.setLoadShow();
							}
						} else {
							String message = jsonObj.getString("message");
							// CommandTools.showToast(message);
							refreshInit();

							String code = jsonObj.getString("code");
							if (code.equals("105")) {
								mNotOrderLayout.setVisibility(View.VISIBLE);
								mExperienceListView.setVisibility(View.GONE);
								mExperienceData.clear();
								mExperienceAdapter.notifyDataSetChanged();
								mExperienceListView.setPullLoadEnable(false);
							}
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(ExperienceAdditionActivity.this);
						e.printStackTrace();

						refreshInit();
					}
				} else {
					Util.showNetErrorMessage(ExperienceAdditionActivity.this, result.m_nResultCode);
					refreshInit();
				}
			}
		};

		LoadTextNetTask task = OrderService.getExperience(categoryId, ruleCode, pageNumber, listener, null);
		return task;
	}
	
	protected LoadTextNetTask requestGobOrder(String orderId, final String type) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestRobOrder = null;
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							CommandTools.showToast("抢单成功");
							getOneData();

							// 接单成功后设置未读消息数量
							SharedPreferences sp = ExperienceAdditionActivity.this.getSharedPreferences(Constant.SAVE_NOT_ORDER_NUMBER, Context.MODE_PRIVATE);
							int number = sp.getInt("OrderNumber", 0);
							Util.saveNotNumber(ExperienceAdditionActivity.this,number + 1, Util.order_type);
							MainActivity.mNotOrderNum.setText(number + 1 + "");
							MainActivity.mNotOrderNum.setVisibility(View.VISIBLE);

							// if (type.equals(Constant.TYPE_AGENT_SYSTEM)) {
							// startAnim(v);
							// }
						} else {
							String message = jsonObj.getString("message");

							String code = jsonObj.getString("code");
							if (code.equals("010011")) {
								CommandTools.showToast(message);
								getOneData();
							}

							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Util.showJsonParseErrorMessage(ExperienceAdditionActivity.this);
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(ExperienceAdditionActivity.this, "获取数据中...", false, null);
		LoadTextNetTask task = OrderService.grabOrder(orderId, type, listener, null);
		return task;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	// 下拉刷新
	private void refreshInit() {
		mExperienceListView.stopRefresh();
		mExperienceListView.stopLoadMore();
		// 也可以用系统时间
		mExperienceListView.setRefreshTime("刚刚");

		refresh = 0;
	}
	
	private void getOneData() {
		mExperienceData.clear();
		task_pageNumber = 1;
		mTaskRequestGetOrderTask = requestGetOrderData(categoryId, ruleCode, task_pageNumber);
	}
	
	@Override
	public void onRefresh() {
		if (refresh == 0) {
			refresh++;
			getOneData();
		}
	}

	@Override
	public void onLoadMore() {
		task_pageNumber++;

		mTaskRequestGetOrderTask = requestGetOrderData(categoryId, ruleCode, task_pageNumber);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 被系统回收时保存myapplication全局变量值
		GlobalInstanceStateHelper.saveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// 恢复被系统回收后的myapplication值
		GlobalInstanceStateHelper.restoreInstanceState(this, savedInstanceState);

		initView();
	}
	
	class ExperienceAdapter extends BaseAdapter {

		private Context context;
		private List<OrderInfo> list;
		
		public ExperienceAdapter(Context context, List<OrderInfo> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.item_experience, null);
				holder = new ViewHolder();
				holder.theme_text = (TextView) convertView.findViewById(R.id.theme_text);
				holder.iv_sex = (ImageView) convertView.findViewById(R.id.iv_sex);
				holder.receive_icon = (XCRoundRectImageView) convertView.findViewById(R.id.receive_icon);
				holder.tv_require = (TextView) convertView.findViewById(R.id.tv_require);
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

			final OrderInfo info = list.get(position);
			
			holder.theme_text.setText(info.getOrder_theme());
			holder.tv_require.setText(info.getRemark());
				
			holder.tv_user_name.setText(info.getReceiveName());
			holder.tv_user_address.setText("用户地址:" + info.getReceiveAddress());
			
			holder.tv_delivery_time.setText("时间:" + info.getDeliveryTime());
			try {
				holder.reward_money_index.setText("Y" + AmountUtils.changeF2Y(info.getDeliveryFee()));
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
				if (info.getDeliverySex().equals("p_gender_female")) {
					holder.receive_icon.setImageResource(R.drawable.female);
				} else{
					holder.receive_icon.setImageResource(R.drawable.male);
				}
			} else {
				MyApplication.getInstance().getImageLoader().displayImage(info.getReceiveIcon(), holder.receive_icon, MyApplication.getInstance().getOptions(), null);
			}

			holder.tv_order_receiving.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (m_onRobClickListener != null) {
						m_onRobClickListener.onRobClickClick(v, info.getOrderNo(), info.getOrder_type());
					}
				}
			});

			return convertView;
		}

		class ViewHolder {
			TextView theme_text;
			ImageView iv_sex;
			XCRoundRectImageView receive_icon;
			TextView tv_require;
			TextView reward_money_index;
			TextView tv_order_receiving;
			TextView tv_delivery_time;
			TextView tv_user_name;
			TextView tv_user_address;
			LinearLayout index_order_layout;
		}
		
		/** 抢单 */
		onRobClickListener m_onRobClickListener = null;
		
		/** 抢单 */
		public void setOnRobClickListener(onRobClickListener cl) {
			m_onRobClickListener = cl;
		}
	}
	
	public interface onRobClickListener {
		void onRobClickClick(View v, String orderId, String type);
	}
}
