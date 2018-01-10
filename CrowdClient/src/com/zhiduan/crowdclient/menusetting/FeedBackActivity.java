package com.zhiduan.crowdclient.menusetting;

import org.json.JSONException;
import org.json.JSONObject;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.CustomProgress;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 用户反馈
 * @author sun
 *
 */
public class FeedBackActivity extends BaseActivity {

	private Context mContext;
	private EditText mEtContent;
	private TextView mEtNumber;

	private final int maxLength = 250;//意见反馈字数限制
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = FeedBackActivity.this;
	}

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_feed_back, this);
	}

	@Override
	public void initView() {

		mEtContent = (EditText) findViewById(R.id.feedback_et_content);
		mEtNumber = (TextView) findViewById(R.id.feedback_tv_number);

		initListener();
	}

	private void initListener() {

		mEtContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				String strMsg = s.toString();
				Logs.v("zd", strMsg);
				if(strMsg.length() > maxLength){

					mHandler.sendEmptyMessage(0x0022);
					mEtNumber.setText(maxLength + "/" + maxLength);
				}else{
					mEtNumber.setText(strMsg.length() + "/" + maxLength);
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

	public Handler mHandler = new Handler(){

		public void handleMessage(Message msg){

			if(msg.what == 0x0022){

				String str = mEtContent.getText().toString();
				mEtContent.setText(str.substring(0, maxLength));
				mEtContent.setSelection(maxLength);

				CommandTools.showToast("输入字数达到上限");
			}
		} 
	};

	@Override
	public void initData() {

		setTitle("意见反馈");
	}

	/**
	 * 提交
	 * @param v
	 */
	public void submit(View v) {

		String strMsg = mEtContent.getText().toString();

		if(TextUtils.isEmpty(strMsg)){

			CommandTools.showToast("请输入反馈内容");
			return;
		}
		
		strMsg = CommandTools.escapeOtherChar(strMsg);//特殊字符过滤

		if(strMsg.length() > maxLength){

			CommandTools.showToast("反馈内容长度不能超过250位");
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("content", strMsg);
			jsonObject.put("submittedTime", CommandTools.getTime());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "user/feedback/addfeedback.htm";
		CustomProgress.showDialog(mContext, "数据提交中", false, null);
		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CustomProgress.dissDialog();
				
				CommandTools.showToast("用户反馈信息成功!");
				finish();
			}
		});

	}


}
