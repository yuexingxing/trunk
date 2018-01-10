package com.zhiduan.crowdclient.menuorder.task;

import android.os.Bundle;
import android.view.View;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;

/** 
 * 完成评价
 * 
 * @author yxx
 *
 * @date 2017-1-4 下午2:19:54
 * 
 */
public class ThoughtCompleteActivity extends BaseActivity {

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_thought_complete, this);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		setTitle("已完成评价");
	}

	/**
	 * 返回订单管理
	 * @param v
	 */
	public void backMenu(View v){

		setResult(RESULT_OK);
		finish();
	}
}
