package com.zhiduan.crowdclient.menuindex;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.data.ADInfo;
import com.zhiduan.crowdclient.share.InviteCenter_Activity;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.view.XCRoundRectImageView;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;

public class AdvertisementPop extends PopupWindow implements OnPageChangeListener, OnClickListener{
	private View mMenuView;
	private Context context;
	private ViewPager vp;
	private MyselfPagerAdapter myselfPagerAdapter;
	private List<View> listView;
	private ImageView[] round;

	public int currentIndex = 0;
	public boolean flag = false;
	
	private String[] imageList;
	private List<ADInfo> dialogAdInfos;
	
	public AdvertisementPop(final Activity context, String[] imageList, List<ADInfo> dialogAdInfos) {
		super(context);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.activity_advertisement, null);
		
		this.imageList = imageList;
		this.dialogAdInfos = dialogAdInfos;
		
		init();

		// 从布局文件中获取ViewPager父容器
		final LinearLayout pagerLayout = (LinearLayout) mMenuView.findViewById(R.id.viewPager_layout);
		
		// 创建ViewPager
		vp = new ViewPager(context);
		// 获取屏幕像素相关信息
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);

		int widthPixels = dm.widthPixels - dm.widthPixels / 4;
		int heightPixels = dm.heightPixels - dm.heightPixels / 3;
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(widthPixels, heightPixels);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		// 根据屏幕信息设置ViewPager广告容器的宽高
		pagerLayout.setLayoutParams(layoutParams);

		// 将ViewPager容器设置到布局文件父容器中
		pagerLayout.addView(vp);

		myselfPagerAdapter = new MyselfPagerAdapter(listView);
		vp.setAdapter(myselfPagerAdapter);
		vp.setOnPageChangeListener(this);

		// 初始化底部小点
		initRound();
		
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		
		this.setOutsideTouchable(true); 
		
		// 设置SelectPicPopupWindow弹出窗体动画效果
		// this.setAnimationStyle(R.style.AnimBottom);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				int height = pagerLayout.getTop();
				int bottom = pagerLayout.getBottom();
				int left = pagerLayout.getLeft();
				int right = pagerLayout.getRight();
				int y = (int) event.getY();
				int x = (int) event.getX();
				
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height || y > bottom || x < left || x > right) {
						dismiss();
					}
				}
				return true;
			}
		});
		
	}
	
	private void init() {
		listView = new ArrayList<View>();
		for (int i = 0; i < imageList.length; i++) {
			XCRoundRectImageView imageView = new XCRoundRectImageView(context);
			imageView.setScaleType(ScaleType.FIT_CENTER);
			setImageToImageView(imageView, imageList[i]);
			listView.add(imageView);
			
			final int position = i; 
			imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					ADInfo adInfo = dialogAdInfos.get(position);
					
					if (adInfo.getUrl_link() == null || adInfo.getUrl_link().equals("")) {
						return;
					}
					if (adInfo.getOpen_type() == 1) {
						Intent intent = new Intent(context, BannerDetailActivity.class);
						intent.putExtra("url", dialogAdInfos.get(position).getUrl_link());
						intent.putExtra("title", dialogAdInfos.get(position).getTitle());
						context.startActivity(intent);
					} else {
						if(adInfo.getOpen_url().equals("1001")) {
							Intent taskIntent = new Intent(context, TaskOrderMenuActivity.class);
							taskIntent.putExtra("currIndex", 0);
							context.startActivity(taskIntent);
						} else if (adInfo.getOpen_url().equals("1002")) {
							Intent intent2 = new Intent(context, BannerDetailActivity.class);
							intent2.putExtra("title", "帮助与反馈");
							intent2.putExtra("url", "http://dev.axpapp.com/question/toqalist.htm");
							intent2.putExtra("type", "question");
							context.startActivity(intent2);
						} else if (adInfo.getOpen_url().equals("1003")) {
							context.startActivity(new Intent(context, InviteCenter_Activity.class));
						}
					}
					dismiss();
				}
			});
		}
	}
	
	public static void setImageToImageView(final ImageView imageView ,final String imgURL){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Log.e("HAHAHA", "设置图片成功");
                super.handleMessage(msg);
                Bitmap bitmap = (Bitmap)msg.obj;
                imageView.setImageBitmap(bitmap);
            }
        };
        new Thread(new Runnable() {

            @Override
            public void run() {
                Bitmap bitmap = getBitmap(imgURL);//这是我封装的获取Bitmap的工具
                Message msg = new Message();
                msg.obj = bitmap;
                handler.sendMessage(msg);
            }
        }).start();
    }
	
	public static Bitmap getBitmap(String url) {
        URL imageURL = null;
        Bitmap bitmap = null;
        Log.e("inuni","URL = "+url);
        try {
            imageURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) imageURL.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

	private void initRound() {
		LinearLayout ll = (LinearLayout) mMenuView.findViewById(R.id.ll);
		round = new ImageView[imageList.length];
		for (int i = 0; i < imageList.length; i++) {
			ImageView img = new ImageView(context);
			img.setBackgroundResource(R.drawable.round_dialog_index);
			
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);  
			params.setMargins(5, 5, 5, 5);  
			img.setLayoutParams(params);
			ll.addView(img);
			
			round[i] = img;
			round[i].setVisibility(View.VISIBLE);
			round[i].setOnClickListener(this);
			round[i].setSelected(false);
			round[i].setTag(i);
		}
		round[currentIndex].setSelected(true);
	}

	private void setCurView(int position) {
		if (position < 0 || position >= imageList.length) {
			return;
		}
		vp.setCurrentItem(position);
	}

	private void setRoundView(int position) {
		if (position < 0 || position >= imageList.length || currentIndex == position) {
			return;
		}
		round[position].setSelected(true);
		round[currentIndex].setSelected(false);
		currentIndex = position;
	}

	@Override
	// 当滑动状态改变时调用
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	// 当前页面被滑动时调用
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	// 当新的页面被选中时调用
	public void onPageSelected(int arg0) {
		setRoundView(arg0);
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		setCurView(position);
		setRoundView(position);
	}
}