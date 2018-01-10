package com.zhiduan.crowdclient.menuorder;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import android.os.Bundle;
import android.widget.ImageView;

/** 
 * 图片预览
 * 
 * @author yxx
 *
 * @date 2016-8-4 下午7:15:47
 * 
 */
public class ZoomImageActivity extends BaseActivity{

	private ImageView imgView;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentViewId(R.layout.activity_zoom_image, this);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		setTitle("图片预览");
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		findViewById();
	}

	private void findViewById(){

		imgView = (ImageView) findViewById(R.id.img_zoom);// 获取控件
		MyApplication.getInstance().getImageLoader().displayImage(getIntent().getStringExtra("imgUrl"), imgView, MyApplication.getInstance().getOptions(), null);
//		ImageLoader.getInstance().loadImage(getIntent().getStringExtra("imgUrl"), new ImageLoadingListener() {
//
//			@Override
//			public void onLoadingStarted(String arg0, View arg1) {
//				// TODO Auto-generated method stub
////				CustomProgress.showDialog(ZoomImageActivity.this, "图片加载中", true, null);
//			}
//
//			@Override
//			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
//				// TODO Auto-generated method stub
//				CustomProgress.dissDialog();
//				CommandTools.showToast("图片加载失败");
//				finish();
//			}
//
//			@Override
//			public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
//				CustomProgress.dissDialog();
//				
////				imgView.setImageBitmap(bitmap);
//				imgView.setImage(bitmap);
//			}
//
//			@Override
//			public void onLoadingCancelled(String arg0, View arg1) {
//				// TODO Auto-generated method stub
//				CustomProgress.dissDialog();
//				CommandTools.showToast("图片加载失败");
//				finish();
//			}
//
//		});
	}

}