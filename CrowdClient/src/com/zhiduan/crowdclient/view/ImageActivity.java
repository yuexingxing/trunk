package com.zhiduan.crowdclient.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.task.PinchImageView;
/**
 * 
 * <pre>
 * Description	图片缩放浏览
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-22 下午3:59:32  
 * </pre>
 */
public class ImageActivity extends BaseActivity{
	

	private Bitmap bmp;
	private int window_width, window_height;// 控件宽度
	private PinchImageView dragImageView;// 自定义控件
	private int state_height;// 状态栏的高度
	private ViewTreeObserver viewTreeObserver;


	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_image,this);

		Intent intent = getIntent();
		String path = intent.getStringExtra("path");
		
		/** 获取可見区域高度 **/
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();
		dragImageView = (PinchImageView) findViewById(R.id.div_main);
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		bmp = BitmapFactory.decodeFile(path, options);
		dragImageView.setImageBitmap(bmp);
//		if(!TextUtils.isEmpty(path)){
//			MyApplication.getInstance().getImageLoader().displayImage(path, dragImageView, MyApplication.getInstance().getOptions(), null);
//		}else{
////			((ImageView)findViewById(R.id.img_abnormal_detail_icon)).setImageBitmap(null);
//		}
		//dragImageView.setmActivity(this);//注入Activity.
		/** 测量状态栏高度 **/
		viewTreeObserver = dragImageView.getViewTreeObserver();
		viewTreeObserver
		.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				if (state_height == 0) {
					// 获取状况栏高度
					Rect frame = new Rect();
					getWindow().getDecorView()
					.getWindowVisibleDisplayFrame(frame);
					state_height = frame.top;
					//dragImageView.setScreen_H(window_height-state_height);
					//dragImageView.setScreen_W(window_width);
				}

			}
		});


	}

	@Override
	public void initView() {

	}

	@Override
	public void initData() {
		setTitle("图片预览");
		hidenRightTitle();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(bmp != null && !bmp.isRecycled()){ 
			// 回收并且置为null
			bmp.recycle(); 
			bmp = null; 
		}
	}
	
	/**
	 * 相片按相框的比例动态缩放
	 * @param context 
	 * @param 要缩放的图片
	 * @param width 模板宽度
	 * @param height 模板高度
	 * @return
	 */
	public static Bitmap upImageSize(Bitmap bmp, int width,int height) {
	    if(bmp==null){
	        return null;
	    }
	    // 计算比例
	    float scaleX = (float)width / bmp.getWidth();// 宽的比例
	    float scaleY = (float)height / bmp.getHeight();// 高的比例
	    //新的宽高
	    int newW = 0;
	    int newH = 0;
	    if(scaleX > scaleY){
	        newW = (int) (bmp.getWidth() * scaleX);
	        newH = (int) (bmp.getHeight() * scaleX);
	    }else if(scaleX <= scaleY){
	        newW = (int) (bmp.getWidth() * scaleY);
	        newH = (int) (bmp.getHeight() * scaleY);
	    }
	    return Bitmap.createScaledBitmap(bmp, newW, newH, true);
	}
	
	protected void onStop(){
		super.onStop();
		
		if(bmp != null && !bmp.isRecycled()){ 
			// 回收并且置为null
			bmp.recycle(); 
			bmp = null; 
		}
	}
}
