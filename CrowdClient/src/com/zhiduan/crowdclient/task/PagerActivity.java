package com.zhiduan.crowdclient.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.TaskImg;
import com.zhiduan.crowdclient.task.PinchImageViewPager.OnPageChangeListener;
import com.zhiduan.crowdclient.util.Constant;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * <pre>
 * Description	图片浏览器
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-22 下午3:11:48  
 * </pre>
 */
public class PagerActivity extends Activity {
	private Context mContext;
	//装点点的ImageView数组
		private ImageView[] tips;
		private LinearLayout group;
		final LinkedList<PinchImageView> viewCache = new LinkedList<PinchImageView>();
		private  PinchImageViewPager pager;
		private Intent intent;
		private int img_index=-1;
//	private int[] imgs={R.drawable.alipay_icon,R.drawable.app_logo,R.drawable.wechat_payment,R.drawable.app_logo};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pager);
		
		mContext=this;
		group=(LinearLayout) findViewById(R.id.index_ad_radio);
		intent=getIntent();
		img_index=intent.getIntExtra("index", -1);
		pager=(PinchImageViewPager) findViewById(R.id.pager);
//		final DisplayImageOptions thumbOptions = new DisplayImageOptions.Builder()
//				.resetViewBeforeLoading(true).cacheInMemory(true).build();
//		final DisplayImageOptions originOptions = new DisplayImageOptions.Builder()
//				.build();
		
		// 将点点加入到ViewGroup中
		tips = new ImageView[TaskAlbumActivity.pager_task.size()];
		for (int i = 0; i < TaskAlbumActivity.pager_task.size(); i++) {
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			tips[i] = imageView;
			if (i == 0) {
				tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
			group.addView(imageView);
		}
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				setImageBackground(position % TaskAlbumActivity.pager_task.size());
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				
			}
		});
		
		pager.setAdapter(new PagerAdapter() {
			@Override
			public int getCount() {
				return TaskAlbumActivity.pager_task.size();
			}

			@Override
			public boolean isViewFromObject(View view, Object o) {
				return view == o;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				PinchImageView piv;
				if (viewCache.size() > 0) {
					piv = viewCache.remove();
					piv.reset();
				} else {
					piv = new PinchImageView(PagerActivity.this);
				}
//				ImageSource image = Global.getTestImage(position);
//				Global.getImageLoader(getApplicationContext()).displayImage(
//						image.getUrl(100, 100), piv, thumbOptions);
				
				DownLoad_Img(TaskAlbumActivity.pager_task.get(position).getNormalUrl(), piv);
				container.addView(piv);
				
				piv.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						finish();
					}
				});
				return piv;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				PinchImageView piv = (PinchImageView) object;
				container.removeView(piv);
				viewCache.add(piv);
			}

			@Override
			public void setPrimaryItem(ViewGroup container, int position,
					Object object) {
				PinchImageView piv = (PinchImageView) object;
//				ImageSource image = Global.getTestImage(position);
//				Global.getImageLoader(getApplicationContext()).displayImage(
//						image.getUrl(image.getOriginWidth(),
//								image.getOriginHeight()), piv, originOptions);
				//piv.setImageResource(imgs[position]);
				
				DownLoad_Img(TaskAlbumActivity.pager_task.get(position).getNormalUrl(), piv);
				pager.setMainPinchImageView(piv);
			}
			
		});
		if (img_index!=-1) {
			if (TaskDetailActivity.task_pager_type==Constant.TASK_UNDER_WAY_DETAIL) {
				setImageBackground(img_index-1);
				pager.setCurrentItem(img_index-1);
			}else {
				setImageBackground(img_index);
				pager.setCurrentItem(img_index);
			}
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		MyApplication.baseActivity = this;
	}
	/**
	 * 下载服务器图片
	 */
	private void DownLoad_Img(String img_url, final ImageView imageView) {
		ImageLoader.getInstance().loadImage(img_url,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {

					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap bitmap) {
						imageView.setImageBitmap(bitmap);
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {

					}

				});

	}
	/**
	 * 设置选中的tip的背景
	 * 
	 * @param selectItems
	 */
	private void setImageBackground(int selectItems) {
		for (int i = 0; i < tips.length; i++) {
			if (i == selectItems) {
				tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
			setMargins(tips[i], 5, 0, 0, 0);
		}
	}
	/**
	 * 设置外边距
	 */
	public static void setMargins(View v, int l, int t, int r, int b) {
		if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
			p.setMargins(l, t, r, b);
			v.requestLayout();
		}
	}
}