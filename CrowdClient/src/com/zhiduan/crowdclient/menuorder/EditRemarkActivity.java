package com.zhiduan.crowdclient.menuorder;

import org.json.JSONException;
import org.json.JSONObject;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

/** 
 * 订单备注编辑
 * 
 * 需要传入orderId
 * 
 * @author yxx
 *
 * @date 2016-7-25 下午2:04:39
 * 
 */
public class EditRemarkActivity extends BaseActivity {

	private TextView tvCount;
	private EditText edtContent;

	private String oldContent;
	private int maxLength = 50;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_edit_remark, this);

	}

	@Override
	public void initView() {

		findViewById();
	}

	@Override
	public void initData() {

		LayoutParams lp = edtContent.getLayoutParams();

		//任务备注字数限制
		if(getIntent().getIntExtra("remarkType", 0) == OrderUtil.REMARK_TASK){
			setTitle("任务报告");
			maxLength = 500;
			edtContent.setHint("请填写任务报告，最多不超过" + maxLength + "个文字");
			lp.height = CommandTools.dip2px(EditRemarkActivity.this, 300);//设置编辑框高度
		}else{
			setTitle("备注");
			maxLength = 50;
			edtContent.setHint("请填写备注，最多不超过" + maxLength + "个文字");
			lp.height = CommandTools.dip2px(EditRemarkActivity.this, 150);
		}

		oldContent = getIntent().getStringExtra("content");
		edtContent.setText(oldContent);
		edtContent.setSelection(oldContent.length());

		tvCount.setText(oldContent.length() + "/" + maxLength);
	}

	private void findViewById(){

		tvCount = (TextView) findViewById(R.id.tv_remark_number);
		edtContent = (EditText) findViewById(R.id.edt_remark_content);

		edtContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				String strMsg = s.toString();
				if(strMsg.length() > maxLength){

					mHandler.sendEmptyMessage(0x0022);
					tvCount.setText(maxLength + "/" + maxLength);
				}else{
					tvCount.setText(strMsg.length() + "/" + maxLength);
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

	/**
	 *接口名称 (730)更新订单备注信息
	 *请求类型 post
	 *请求Url  /order/packet/updateremark.htm
	 */
	public void submit(View v){

		String strContent = edtContent.getText().toString();
		if(TextUtils.isEmpty(strContent) && TextUtils.isEmpty(oldContent)){
			CommandTools.showToast("输入内容不能为空");
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("takerId", getIntent().getStringExtra("takerId"));
			jsonObject.put("remark", strContent);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(mContext, OrderUtil.packet_updateremark, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CommandTools.showToast(remark);

				if(success == 0){
					finish();
				}

			}
		});
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
}
