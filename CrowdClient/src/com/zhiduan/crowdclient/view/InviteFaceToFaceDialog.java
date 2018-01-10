package com.zhiduan.crowdclient.view;

import com.google.zxing.WriterException;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Logs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * <pre>
 * Description	面对面邀请二维码对话框
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2017-1-5 上午10:50:47  
 * </pre>
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class InviteFaceToFaceDialog{

	private static Context mContext;
	public static boolean isShow = false;
	static Dialog dialog;
	private static ImageView imgCode;
	
	public static abstract class SavePicCallback {
		public abstract void callback(int pos);
	}

	public InviteFaceToFaceDialog(Context context){

	}

	/**
	 * @param context
	 * @param strMessage
	 */
	public static void showMyDialog(Context context, String title, Bitmap mBitmap, final SavePicCallback callback){

//		Logs.v("result", url);
		mContext = context;

		if(dialog != null){
			dialog.dismiss();
		}

		Message msg = new Message();
		msg.obj = mBitmap;
		msg.what = 0;
		myHandler.sendMessage(msg);

		dialog = new Dialog(context, R.style.dialog);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.invite_face_to_face, null);

		TextView tv_qrcode_invite_title = (TextView) layout.findViewById(R.id.tv_qrcode_invite_title);
		tv_qrcode_invite_title.setText("我是    "+title+",    爱学派帮我省了好多力，    快扫码加入爱学派吧");
		imgCode = (ImageView) layout.findViewById(R.id.iv_invite_qrcode);
		LinearLayout ll_copy_img=(LinearLayout) layout.findViewById(R.id.ll_copy_img);
		ImageView img = (ImageView) layout.findViewById(R.id.img_invite_close);
		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				dialog.dismiss();
			}
		});
		Button btn_save_pic = (Button) layout.findViewById(R.id.btn_save_pic);
		ll_copy_img.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				callback.callback(1);
				return false;
			}
		});

		dialog.setContentView(layout);

		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);

		dialog.show();
		setDialogWindowAttr(dialog, context);
		isShow = true;
	}

	/**
	 * 关闭窗口
	 */
	public static void closeDialog(){

		if(dialog != null){
			dialog.dismiss();
		}
	}

	//在dialog.show()之后调用
	public static void setDialogWindowAttr(Dialog dlg,Context ctx){

		WindowManager wm = ((Activity) ctx).getWindowManager();
		Display display = wm.getDefaultDisplay(); // 为获取屏幕宽、高

		Window window = dlg.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.width = (int) (display.getWidth()/1.2);
		lp.height = (int) (display.getHeight()/1.5);
		dlg.getWindow().setAttributes(lp);
	}

	private static float imagt_halfWidth = 20;
	static Handler myHandler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				Bitmap bitmap=(Bitmap) msg.obj;
				imgCode.setBackground(new BitmapDrawable(bitmap));
//				Bitmap mBitmap = BitmapFactory.decodeResource(mContext.getResources(),
//						R.drawable.login_logo);
//
//				Matrix m = new Matrix();
//				float sx = (float) 2 * imagt_halfWidth / mBitmap.getWidth();
//				float sy = (float) 2 * imagt_halfWidth / mBitmap.getHeight();
//				m.setScale(sx, sy);
//
//				mBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
//						mBitmap.getWidth(), mBitmap.getHeight(), m, false);
//
//				try {
//					Bitmap creatTwoBitMap = CommandTools.creatTwoBitMap(
//							(String)msg.obj, mBitmap);
//					imgCode.setBackground(new BitmapDrawable(creatTwoBitMap));
//				} catch (WriterException e) {
//					e.printStackTrace();
//				}
			}
		}
	};
}
