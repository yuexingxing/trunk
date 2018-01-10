package com.zhiduan.crowdclient.menusetting;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;

import android.os.Bundle;
import android.widget.TextView;

/**
 * 关于爱学派
 * @author sun
 *
 */
public class AboutActivity extends BaseActivity {

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {

		setContentViewId(R.layout.activity_about, this);
	}

	@Override
	public void initView() {
		setTitle("关于爱学派");

		String version = CommandTools.getVersionName(AboutActivity.this);

		String ver_title = Constant.FormalURL.contains("dev") ? "【测试】" : "";
		((TextView) findViewById(R.id.tv_about_version)).setText(ver_title + "版本号: " + version);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}
}
