package com.zhiduan.crowdclient.photoalbum;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.photoalbum.MyImageView.OnMeasureListener;
import com.zhiduan.crowdclient.photoalbum.NativeImageLoader.NativeImageCallBack;
/**
 * 相册展示
 * @author wfq
 *
 */
public class ShowImageActivity extends Activity {
	private GridView mGridView;
	private List<String> list;
	private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_image_activity);
		mContext=this;
		mGridView = (GridView) findViewById(R.id.child_grid);
		list = getIntent().getStringArrayListExtra("data");
		
		mGridView.setAdapter(new BaseAdapter() {
			
			private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
			/**
			 * 用来存储图片的选中情况
			 */
			private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
			
			@Override
			public int getCount() {
				return list.size();
			}

			@Override
			public Object getItem(int position) {
				return list.get(position);
			}


			@Override
			public long getItemId(int position) {
				return position;
			}
			
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				final ViewHolder viewHolder;
				 String path = list.get(position);
				
				if(convertView == null){
					convertView = View.inflate(mContext, R.layout.grid_child_item, null);
					viewHolder = new ViewHolder();
					viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
					
					//用来监听ImageView的宽和高
					viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {
						
						@Override
						public void onMeasureSize(int width, int height) {
							mPoint.set(width, height);
						}
					});
					
					convertView.setTag(viewHolder);
				}else{
					viewHolder = (ViewHolder) convertView.getTag();
					viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
				}
				viewHolder.mImageView.setTag(path);
				
				
				//利用NativeImageLoader类加载本地图片
				Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageCallBack() {
					
					@Override
					public void onImageLoader(Bitmap bitmap, String path) {
						ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
						if(bitmap != null && mImageView != null){
							mImageView.setImageBitmap(bitmap);
						}
					}
				});
				
				if(bitmap != null){
					viewHolder.mImageView.setImageBitmap(bitmap);
				}else{
					viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
				}
				viewHolder.mImageView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
//						Toast.makeText(mContext, "请稍等", 0);
						String path = list.get(position);
						Intent intent = new Intent("photograph.photoalbum");
						intent.putExtra("path", path);
						sendBroadcast(intent); 
						finish();
						
					}
				});
				return convertView;
			}
		});
		
//		mGridView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//					long arg3) {
//				Toast.makeText(mContext, "请稍等", 0);
//				String path = list.get(position);
//				Intent data=new Intent();
//				data.putExtra("path", path);
//				setResult(2, data);
//				finish();
//			}
//		});
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		MyApplication.baseActivity = this;
	}
	
	static class ViewHolder{
		public MyImageView mImageView;
	}

	
}