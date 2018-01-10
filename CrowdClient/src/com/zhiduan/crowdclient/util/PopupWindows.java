package com.zhiduan.crowdclient.util;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.menucenter.UserInfoActivity;
import com.zhiduan.crowdclient.photoalbum.MyPhotoAlbumActivity;
import com.zhiduan.crowdclient.photoalbum.camera.MyCameraActivity;
import com.zhiduan.crowdclient.view.crop.Crop;

/** 
 * 弹窗选择拍照
 * 
 * @author yxx
 *
 * @date 2016-12-27 下午3:33:15
 * 
 */
public class PopupWindows extends PopupWindow {
	
	public static abstract class Callback {
		public abstract void callback(int pos);
	}

	public PopupWindows(final Context context, View parent, final Callback callback) {

		super(context);

		View view = View.inflate(context, R.layout.item_popupwindows, null);

		final LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		getBackground().setAlpha(80);
		setAnimationStyle(R.style.AnimationFade);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setOutsideTouchable(false);
		setFocusable(false);
		setContentView(view);
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		update();

		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.layout_pop_window);
		Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
		Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
		Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);

		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ll_popup.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
				dismiss();
			}
		});
		bt1.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				callback.callback(1);
				dismiss();
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				callback.callback(2);
				dismiss();
			}
		});
		bt3.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				dismiss();
			}
		});

	}
}