package com.zhiduan.crowdclient.wallet;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.data.PayAccount;
import com.zhiduan.crowdclient.net.AsyncNetTask;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextResult;
import com.zhiduan.crowdclient.net.NetTaskResult;
import com.zhiduan.crowdclient.service.PayMentService;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.Util;
import com.zhiduan.crowdclient.view.CustomProgress;
/**
 * 添加支付宝
 * @author wfq
 *
 */
public class AddAlipayActivity extends BaseActivity {

	private EditText mEtName;
	private EditText mEtUser;
	private Button mBtOk;

	private Context context;
	private int isDiyi;
	
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_alipay, this);
		
		context = this;
	}

	@Override
	public void initView() {
		//姓名
		mEtName = (EditText) findViewById(R.id.alipay_et_name);
		//支付宝账号
		mEtUser = (EditText) findViewById(R.id.alipay_et_user);
		//确定
		mBtOk = (Button) findViewById(R.id.alipay_bt_ok);
		
		mBtOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String nume = mEtName.getText().toString();
				String NameNumber = mEtUser.getText().toString();
				if(!(TextUtils.isEmpty(nume)||TextUtils.isEmpty(NameNumber))){
					if(NameNumber.contains("@")){
						if(!CommandTools.isMailbox(NameNumber)){
							CommandTools.showToast( "邮箱不合法");
							return;
						}
					}else{
						
						if(CommandTools.isNumeric(NameNumber)){
							if(!CommandTools.isMobileNO(NameNumber)){
								CommandTools.showToast( "手机号不合法");
								return;
							}
						}else{
							CommandTools.showToast( "支付宝账号不合法");
							return;
						}
						
						
					}
					
					
					
					
					//					Intent intent = new Intent(AddAlipayActivity.this,WithdrawDepositTowActivity.class);
//					intent.putExtra("type", "支付宝");
//					intent.putExtra("number", NameNumber);
//					startActivity(intent);
					
					addAlipay(nume,NameNumber);
					
					
				}else{
					CommandTools.showToast( "您的姓名或账号未填写");
				}
				
			}
		});
		//hideUploadBtn();
		
		setTitle("添加支付宝");
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		isDiyi = intent.getIntExtra("isDiyi", 0);

	}
	
	
	/**
	 * 添加账户第三方支付账号
	 * @param nume
	 * @param nameNumber
	 */
	private void addAlipay(String nume,String nameNumber) {
		OnPostExecuteListener listener=new OnPostExecuteListener() {
			
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					JSONObject jsonObj;
					try {
						jsonObj = new JSONObject(
								mresult.m_strContent);
						Logs.i("zd", jsonObj.toString() + "---");
						int success = jsonObj.getInt("success");
						if(success==0){
							String message = jsonObj.getString("message");
							JSONObject data = jsonObj.getJSONObject("data");
							long thdId = data.getLong("thdId");
							String acctNo = data.getString("acctNo");
							String acctName = data.getString("acctName");
							int isDefault = data.getInt("isDefault");
							CommandTools.showToast( message);
							PayAccount account=new PayAccount(acctName, acctNo, isDefault==1?true:false,thdId);
							if(isDiyi==1){
								setdefault(account);
							}else{
								
								Intent intent=new Intent();
								intent.putExtra("data", account);
								setResult(1,intent);
								finish();
							}
							
						}else{
							String message = jsonObj.getString("message");
							CommandTools.showToast( message);
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
						Util.showJsonParseErrorMessage(AddAlipayActivity.this);
					}
				} else {
					Util.showNetErrorMessage(context, result.m_nResultCode);
				}
				CustomProgress.dissDialog();
				
			}
		};
		CustomProgress.showDialog(context, "", false, null);
		PayMentService.addAlipay(nume,nameNumber,"2",listener, null);
	}
	/**
	 * 设置默认支付账号
	 */
	private void setdefault(final PayAccount account) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					JSONObject jsonObj;
					try {
						jsonObj = new JSONObject(mresult.m_strContent);
						Logs.i("zd", jsonObj.toString() + "---");
						int success = jsonObj.getInt("success");
						if (success == 0) {
							Intent intent=new Intent();
							intent.putExtra("data", account);
							setResult(1,intent);
							finish();
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast( message);
						}

					} catch (JSONException e) {
						e.printStackTrace();
						Util.showJsonParseErrorMessage(mContext);
					}
				} else {
					Util.showNetErrorMessage(mContext, result.m_nResultCode);
				}
				CustomProgress.dissDialog();

			}
		};
		CustomProgress.showDialog(mContext, "查询中", false, null);

		PayMentService.setdefault(""+account.getThdId(), listener, null);
	}
}
