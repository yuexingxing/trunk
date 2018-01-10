package com.zhiduan.crowdclient.menuorder.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.adapter.CommonAdapter;
import com.zhiduan.crowdclient.adapter.ViewHolder;
import com.zhiduan.crowdclient.menuorder.EditRemarkActivity;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.Res;
import com.zhiduan.crowdclient.util.OrderUtil.ObjectCallback;
import com.zhiduan.crowdclient.view.MyGridView;

/** 
 * 单人任务、多人任务
 * 
 * @author yxx
 *
 * @date 2017-1-5 下午4:40:57
 * 
 */
public class TaskOrderDetailActivity extends BaseActivity {

	private String orderId;//订单编号

	@ViewInject(R.id.include_6_gridview_picture) MyGridView mGridViewPicture;
	private List<HashMap<String, Object>> dataListPicture = new ArrayList<HashMap<String, Object>>();
	private CommonAdapter<HashMap<String, Object>> commonAdapterPicture;
	@ViewInject(R.id.task_order_detail_layout_gridview_picture) LinearLayout layoutViewPicture; 

	@ViewInject(R.id.include_6_gridview_icon) MyGridView mGridViewIcon;
	private List<HashMap<String, Object>> dataListIcon = new ArrayList<HashMap<String, Object>>();
	private CommonAdapter<HashMap<String, Object>> commonAdapterIcon;
	@ViewInject(R.id.task_order_detail_layout_gridview_icon) LinearLayout layoutViewIcon; 

	@ViewInject(R.id.task_order_detail_layout_mulity_icon) LinearLayout layoutMulity;
	@ViewInject(R.id.include_4_detail_layout) LinearLayout layoutRemark;
	@ViewInject(R.id.include_4_detail_1) TextView tvRemark;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_task_order_detail, this);

		ViewUtils.inject(this);
	}

	@Override
	public void initView() {

		initPicture();
		initIcon();
	}

	@Override
	public void initData() {
		setTitle("任务详情");

		setRightTitleText("备注");
		setRightTitleTextColor(Res.getColor(R.color.white));

		orderId = getIntent().getStringExtra("orderId");
		for(int i=0; i<10; i++){  

			String url = "http://www.yjz9.com/uploadfile/2014/0606/20140606111306232.jpg";
			HashMap<String, Object> map = new HashMap<String, Object>();    
			map.put("icon", url);
			dataListPicture.add(map);
		}  

		for(int i=0; i<10; i++){  

			String url = "http://img.7160.com/uploads/allimg/150313/9-150313112P3.jpg";
			HashMap<String, Object> map = new HashMap<String, Object>();    
			map.put("icon", url);
			dataListIcon.add(map);
		}  
	}

	protected void onResume(){
		super.onResume();

		OrderUtil.getRemark(mContext, orderId, new ObjectCallback() {

			@Override
			public void callback(Object remark) {

				tvRemark.setText((String)remark);
				if(TextUtils.isEmpty((String)remark)){
					layoutRemark.setVisibility(View.GONE);
				}else{
					layoutRemark.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void initPicture(){

		mGridViewPicture = new MyGridView(this);
		mGridViewPicture.setVerticalSpacing(10);
		mGridViewPicture.setHorizontalSpacing(15);
		mGridViewPicture.setNumColumns(5);
		mGridViewPicture.setSelector(new ColorDrawable(Color.TRANSPARENT));

		commonAdapterPicture = new CommonAdapter<HashMap<String, Object>>(mContext, dataListPicture, R.layout.item_task_order_detail_picture) {

			@Override
			public void convert(ViewHolder helper, HashMap<String, Object> item) {
				// TODO Auto-generated method stub

				MyApplication.getImageLoader().displayImage(item.get("icon").toString(), (ImageView)helper.getView(R.id.item_task_order_detail_picture), MyApplication.getInstance().getOptions(), null);
			}
		};

		mGridViewPicture.setAdapter(commonAdapterPicture);    

		LinearLayout layoutView_two = new LinearLayout(TaskOrderDetailActivity.this);  
		layoutView_two.setPadding(10, 0, 0, 0);  
		layoutView_two.addView(mGridViewPicture, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));  

		layoutViewPicture.addView(layoutView_two, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); 
	}

	private void initIcon(){

		mGridViewIcon = new MyGridView(this);
		mGridViewIcon.setVerticalSpacing(10);
		mGridViewIcon.setHorizontalSpacing(15);
		mGridViewIcon.setNumColumns(5);
		mGridViewIcon.setSelector(new ColorDrawable(Color.TRANSPARENT));

		commonAdapterIcon = new CommonAdapter<HashMap<String, Object>>(mContext, dataListIcon, R.layout.item_task_order_detail_icon) {

			@Override
			public void convert(ViewHolder helper, HashMap<String, Object> item) {
				// TODO Auto-generated method stub

				MyApplication.getImageLoader().displayImage(item.get("icon").toString(), (ImageView)helper.getView(R.id.item_task_order_detail_icon), MyApplication.getInstance().getOptions(), null);
			}
		};

		mGridViewIcon.setAdapter(commonAdapterIcon);    

		LinearLayout layoutView_two = new LinearLayout(TaskOrderDetailActivity.this);  
		layoutView_two.setPadding(10, 0, 0, 0);  
		layoutView_two.addView(mGridViewIcon, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));  

		layoutViewIcon.addView(layoutView_two, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); 
	}

	/**
	 * 拒绝
	 * @param v
	 */
	public void refuse(View v){

	}

	/**
	 * 接受
	 * @param v
	 */
	public void apply(View v){

	}

	/* 备注
	 * (non-Javadoc)
	 * @see com.zhiduan.crowdclient.activity.BaseActivity#rightClick()
	 */
	public void rightClick() {

		Intent intent = new Intent(mContext, EditRemarkActivity.class);
		intent.putExtra("orderId", orderId);
		intent.putExtra("remarkType", OrderUtil.REMARK_OTHER);
		intent.putExtra("content", tvRemark.getText().toString());
		startActivity(intent);
	}

	/**
	 * 调用系统发短信界面
	 * @param phone
	 */
	public void sendSms(View v){

		sendSms("123");
	}

	/**
	 * 打电话
	 * @param phone
	 */
	public void callPhone(View v){

		callPhone("123");
	}
}
