package com.zhiduan.crowdclient.menuorder.task;

import org.json.JSONException;
import org.json.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.socialize.utils.Log;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtilTools;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.OrderUtil.ObjectCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

/** 
 * 订单评价
 * 
 * @author yxx
 *
 * @date 2017-1-4 上午11:32:00
 * 
 */
public class ThoughtActivity extends BaseActivity {

	private String takerId;//订单编号

	@ViewInject(R.id.thought_icon) ImageView icon;
	@ViewInject(R.id.thought_name) TextView tvName;
	@ViewInject(R.id.thought_number) TextView tvNumber;
	@ViewInject(R.id.thought_phone) TextView tvPhone;
	@ViewInject(R.id.thought_content) EditText edtContent;
	@ViewInject(R.id.thought_sex) ImageView sex;

	@ViewInject(R.id.thought_tatingbar) RatingBar ratingBar;

	private final int maxLength = 200;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_thought, this);

		ViewUtils.inject(this);
	}

	@Override
	public void initView() {

		ratingBar.setMax(5);
		ratingBar.setStepSize(1);
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
				// TODO Auto-generated method stub
				Log.v("zd", arg1 + "");
			}
		});

		edtContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				String strMsg = s.toString();
				if(strMsg.length() > maxLength){

					mHandler.sendEmptyMessage(0x0022);
					tvNumber.setText(maxLength + "/" + maxLength);
				}else{
					tvNumber.setText(strMsg.length() + "/" + maxLength);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void initData() {

		setTitle("评价");
		takerId = getIntent().getStringExtra("takerId");
		getOrderDetail(takerId);
	}

	/**
	 * 提交评价
	 * @param v
	 */
	public void submit(View v){

		String content = edtContent.getText().toString();
		if(TextUtils.isEmpty(content)){
			CommandTools.showToast("评价内容不能为空");
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("images", "");//图片列表,逗号隔开:例如:"1.jpg,2.jpg"
			jsonObject.put("overallDesc", content);//评价内容
			jsonObject.put("overallScore", ratingBar.getNumStars());//评分number
			jsonObject.put("takerId", takerId);//接单号string
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(mContext, OrderUtil.taker_add, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CommandTools.showToast(remark);
				if(success != 0){
					return;
				}

				OrderUtilTools.refreshDataList(mEventBus, 3);
				Intent intent = new Intent(ThoughtActivity.this, ThoughtCompleteActivity.class);
				startActivityForResult(intent, 0x0011);
			}
		});

	}

	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  

		if(requestCode == 0x0011 && resultCode == RESULT_OK){
			setResult(RESULT_OK);
			finish();
		}
	}

	public Handler mHandler = new Handler(){

		public void handleMessage(Message msg){

			if(msg.what == 0x0022){

				String str = edtContent.getText().toString();
				edtContent.setText(str.substring(0, maxLength));
				edtContent.setSelection(maxLength);

				CommandTools.showToast("输入字数达到上限");
			}
		} 
	};

	/**
	 * 获取订单详情
	 * 接口名称 获取订单详情接口
	 * 请求类型 post
	 * 请求Url  /order/packet/getorderdetail.htm
	 */
	private void getOrderDetail(String takerId){

		OrderUtil.getOrderDetail(mContext, takerId, new ObjectCallback() {

			@Override
			public void callback(Object object) {
				// TODO Auto-generated method stub

				OrderInfo info = (OrderInfo) object;

				tvName.setText(info.getReceiveName());
				tvPhone.setText(info.getReceivePhone());

				if(info.getGenderId().equals(OrderUtil.MALE)){
					sex.setBackgroundResource(R.drawable.profile_boy);
				}else{
					sex.setBackgroundResource(R.drawable.profile_girl);
				}

				if(!TextUtils.isEmpty(info.getReceiveIcon())){
					MyApplication.getImageLoader().displayImage(info.getReceiveIcon(), icon, MyApplication.getInstance().getOptions(), null);
				}

			}
		});
	}
}
