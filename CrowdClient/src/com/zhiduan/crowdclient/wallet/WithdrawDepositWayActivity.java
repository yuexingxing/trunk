package com.zhiduan.crowdclient.wallet;

import java.io.Serializable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.data.PayAccount;
/**
 * 提现方式
 * @author wfq
 *
 */
public class WithdrawDepositWayActivity extends BaseActivity implements OnClickListener {

	private TextView mTvAlipay;
	private ImageView mIvAlipay;
	private TextView mTvWeChat;
	private ImageView mIvWeChat;
	private RelativeLayout mRlAlipay;
	private RelativeLayout mRlWeChat;
	private boolean isAlipay=false;
	private boolean isWeChat=false;
	private int isDefaultPay=0;
	private String name;
	private String number;
	private PayAccount alipay;
	private PayAccount weiXin;
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_withdraw_deposit_way, this);
	}

	@Override
	public void initView() {
		mTvAlipay = (TextView) findViewById(R.id.withdraw_deposit_way_tv_zhifubao);
		
		mIvAlipay = (ImageView) findViewById(R.id.withdraw_deposit_way_iv_zhifubaoselect);
		mTvWeChat = (TextView) findViewById(R.id.withdraw_deposit_way_tv_weixin);
		mIvWeChat = (ImageView) findViewById(R.id.withdraw_deposit_way_iv_weixinselect);
		
		mRlAlipay = (RelativeLayout) findViewById(R.id.withdraw_deposit_way_rl_zhifubao);
		mRlWeChat = (RelativeLayout) findViewById(R.id.withdraw_deposit_way_rl_weixin);
		initListener();
		
	}


	@Override
	public void initData() {
		setTitle("提现方式");
		Intent intent = getIntent();
		alipay = (PayAccount) intent.getSerializableExtra("alipay");
		weiXin = (PayAccount) intent.getSerializableExtra("weiXin");
		if(alipay!=null){
			mTvAlipay.setText(alipay.getAccountName()+" "+alipay.getAccountNumber());
			isAlipay=true;
		}
		if(weiXin!=null){
			mTvWeChat.setText(weiXin.getAccountName()+" "+weiXin.getAccountNumber());
			isWeChat=true;
		}
		switch (isDefaultPay) {
		case 0:
			//无默认
			mIvAlipay.setImageResource(R.drawable.order_notselected);
			mIvWeChat.setImageResource(R.drawable.order_notselected);
			break;
		case 1:
			//支付宝
			mIvAlipay.setImageResource(R.drawable.order_selected);
			mIvWeChat.setImageResource(R.drawable.order_notselected);
			
			break;
		case 2:
			//微信
			mIvAlipay.setImageResource(R.drawable.order_notselected);
			mIvWeChat.setImageResource(R.drawable.order_selected);
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.withdraw_deposit_way_rl_zhifubao:
			
			mIvAlipay.setImageResource(R.drawable.order_selected);
			mIvWeChat.setImageResource(R.drawable.order_notselected);
			if(isAlipay){
				Intent intent=new Intent();
				intent.putExtra("pay", alipay);
				setResult(0,intent);
				finish();
			}else{
				Intent way = new Intent(this, AddAlipayActivity.class);
				startActivityForResult(way, 1);
			}
			break;
		case R.id.withdraw_deposit_way_rl_weixin:
			
			mIvAlipay.setImageResource(R.drawable.order_notselected);
			mIvWeChat.setImageResource(R.drawable.order_selected);
			if(isWeChat){
				Intent intent=new Intent();
				intent.putExtra("pay", weiXin);
				setResult(1,intent);
				finish();
			}else{
				Intent way = new Intent(this, AddWeChatActivity.class);
				startActivityForResult(way, 1);
			}
			break;

		default:
			break;
		}
	}
	private void initListener() {
		mRlAlipay.setOnClickListener(this);
		mRlWeChat.setOnClickListener(this);
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data!=null){
			if(requestCode==1){
				alipay = (PayAccount) data.getSerializableExtra("data");
				if(alipay!=null){
					isAlipay=true;
					mTvAlipay.setText(alipay.getAccountName()+" "+alipay.getAccountNumber());
				}
				
			}
		}
		
		
		
	}
	

}
