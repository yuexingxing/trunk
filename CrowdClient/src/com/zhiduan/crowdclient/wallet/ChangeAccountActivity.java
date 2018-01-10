package com.zhiduan.crowdclient.wallet;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.adapter.ChangeAccountAdapter;
import com.zhiduan.crowdclient.adapter.ChangeAccountAdapter.onRightItemClickListener;
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
import com.zhiduan.crowdclient.view.MyListView;
import com.zhiduan.crowdclient.view.WalletDialong;
/**
 * 更改账户
 * 
 * @author wfq
 * 
 */
public class ChangeAccountActivity extends BaseActivity implements OnClickListener {

	private MyListView mLvPay;
	private ArrayList<PayAccount> listAlipay = new ArrayList<PayAccount>();
	private ArrayList<PayAccount> listWeChat = new ArrayList<PayAccount>();
	private ArrayList<PayAccount> lists = new ArrayList<PayAccount>();
	private PayAccount data = null;
	private ChangeAccountAdapter mAdapter;
	private Button bt_change_ok;
	private TextView tv_tail_number;
	private Context context;
	private TextView tv_tail_type;
	private int type;
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		context = this;
		setContentViewId(R.layout.activity_change_account, this);
	}

	@Override
	public void initView() {
		// 支付方式显示
		mLvPay = (MyListView) findViewById(R.id.change_account_lv_pay);
		bt_change_ok = (Button) findViewById(R.id.bt_change_ok);
		tv_tail_number = (TextView) findViewById(R.id.tv_tail_number);
		tv_tail_type = (TextView) findViewById(R.id.tv_tail_type);
		setTitle("更换账户");
	
		setRightTitleText("添加");
		//添加的点击事件
		clickRightTitle(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent;
				if (type == 1) {

					intent = new Intent(mContext, AddAlipayActivity.class);
				} else {
					intent = new Intent(mContext, AddWeChatActivity.class);

				}
				startActivityForResult(intent, type);
				
			}
		});
		
		bt_change_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (data != null) {
					setdefault();
					
				}else{
					if(lists.size()!=0){
						int m=0;
							for(int i=0;i<lists.size();i++){
								PayAccount payAccount = lists.get(i);
								if(payAccount.isPitchOn()){
									data=payAccount;
									m=1;
								}
							}	
							if(m==1){
								
								setdefault();
							}else{
								
								CommandTools.showToast("请选择默认帐号");
							}
					}else{
						
						CommandTools.showToast("请选择默认帐号");
					}
				}
//				Toast.makeText(context, "选择确认", 0).show();
//				finish();
			}

			
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.zbclient.BaseActivity#onClickRight()
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			default :
				break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.zbclient.BaseActivity#initData()
	 */
	@Override
	public void initData() {
		selectNumbet();
		Intent intent = getIntent();
		type = intent.getIntExtra("type", 1);

		if (type == 1) {
			tv_tail_type.setText("支付宝   尾号：");
		} else {
			tv_tail_type.setText("微信     ：");

		}
		mAdapter = new ChangeAccountAdapter(context, lists, mLvPay.getRightViewWidth());
		mLvPay.setAdapter(mAdapter);
		mAdapter.setOnRightItemClickListener(new onRightItemClickListener() {

			@Override
			public void onRightItemClick(View v, int position) {
				switch (v.getId()) {
					case R.id.item_delete_layout_account :
						// Toast.makeText(context, "shanchu"+position,
						// 0).show();
						if(type==2){
							WalletDialong.show(context, "无法删除微信账号");
							
						}else{
							
							deleteAlipay(position);
						}

						break;
					case R.id.item_left_layout_account :
						//
						// if (lists.get(position).isPitchOn()) {
						// lists.get(position).setPitchOn(false);
						// mAdapter.notifyDataSetChanged();
						// }else {
						// lists.get(position).setPitchOn(true);
						// mAdapter.notifyDataSetChanged();
						// }
						for (int i = 0; i < lists.size(); i++) {
							if (i == position) {
								lists.get(position).setPitchOn(true);
								data = lists.get(position);
							} else {
								lists.get(i).setPitchOn(false);
							}
						}
						mAdapter.notifyDataSetChanged();
					default :
						break;
				}

			}

		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}
		PayAccount payAccount = (PayAccount) data.getSerializableExtra("data");
		if (payAccount != null) {
			lists.add(payAccount);
			mAdapter.notifyDataSetChanged();

		}

	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int measuredHeight = mLvPay.getMeasuredHeight();
		if (event.getRawY() > measuredHeight) {
			mLvPay.clearAllSlideItemState();
		}
		return super.onTouchEvent(event);

	}
	private int positions;
	/**
	 * 删除账户第三方支付账号
	 * 
	 * @param position
	 */
	private void deleteAlipay(int position) {

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
							//空指针错误
							PayAccount payAccount = lists.get(positions);
							if(payAccount != null && data != null){
								if(payAccount.getThdId() == data.getThdId()){
									data=null;
								}
							}
							
							lists.remove(positions);
							mAdapter.notifyDataSetChanged();
							mLvPay.hiddenRight(mLvPay.getRightView());
							String message = jsonObj.getString("message");
							CommandTools.showToast( message);
							
							
						} else {
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);
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
		CustomProgress.showDialog(mContext, "删除中", false, null);
		PayAccount payAccount = lists.get(position);
		positions = position;
		PayMentService.deleteAlipay(payAccount.getThdId(), listener, null);

	}
	/**
	 * 查询账户第三方支付账号
	 */
	private void selectNumbet() {
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
							lists.clear();
							JSONArray datsa = jsonObj.getJSONArray("data");
							for (int i = 0; i < datsa.length(); i++) {
								JSONObject jsonObject = datsa.getJSONObject(i);
								int thdType = jsonObject.getInt("thdType");
								String acctName = jsonObject.getString("acctName");
								String acctNo = jsonObject.getString("acctNo");
								int isDefault = jsonObject.getInt("isDefault");
								int thdId = jsonObject.getInt("thdId");
								if (thdType == 1) {
									// 微信
									listWeChat.add(new PayAccount(acctName, acctNo, isDefault == 1 ? true : false,
											thdId));
								} else {
									// 支付宝
									listAlipay.add(new PayAccount(acctName, acctNo, isDefault == 1 ? true : false,
											thdId));

								}
								if (isDefault != 1) {
									continue;
								}
								if (type == 1) {
									// 支付宝
									if(thdType!=2){
										continue;
									}
									String s = "";
									if (acctNo.contains("@")) {
										String[] split = acctNo.split("@");
										if (split[0].length() < 4) {
											s = split[0] + "@***";
										} else {
											s = split[0].substring(split[0].length() - 4, split[0].length()) + "@***";
										}
									} else {
										s = acctNo.substring(acctNo.length() - 4, acctNo.length());
									}
									tv_tail_number.setText(s);
									data=new PayAccount(acctName, acctNo, isDefault == 1 ? true : false,
											thdId);
								} else {
									// 微信
									if(thdType!=1){
										continue;
									}
									tv_tail_number.setText(acctNo);
									data=new PayAccount(acctName, acctNo, isDefault == 1 ? true : false,
											thdId);
								}
							}

							if (type == 1) {
								// 支付宝
								lists.addAll(listAlipay);
							} else {
								// 微信
								lists.addAll(listWeChat);
							}
							mAdapter.notifyDataSetChanged();

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

		PayMentService.selectNumbet(listener, null);
	}
	
	/**
	 * 设置默认支付账号
	 */
	private void setdefault() {
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
						
							Intent intent = new Intent();
							intent.putExtra("data", data);
							setResult(119, intent);
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

		PayMentService.setdefault(""+data.getThdId(),  listener, null);
	}
	
}
