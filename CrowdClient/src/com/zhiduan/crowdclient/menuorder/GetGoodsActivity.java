package com.zhiduan.crowdclient.menuorder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.GetGoodsAdapter;
import com.zhiduan.crowdclient.adapter.GetGoodsAdapter.OnBottomClickListener;
import com.zhiduan.crowdclient.data.GetGoodsInfo;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.GlobalInstanceStateHelper;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.ScreenUtil;
import com.zhiduan.crowdclient.util.VoiceHint;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;
import com.zhiduan.crowdclient.zxing.camera.CameraManagerGoods;
import com.zhiduan.crowdclient.zxing.decoding.CaptureActivityHandlerGoods;
import com.zhiduan.crowdclient.zxing.decoding.InactivityTimer;
import com.zhiduan.crowdclient.zxing.view.ViewfinderViewGoods;
import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.EditText;

/**
 * 扫描取件
 * 
 * @author yxx
 * 
 * @date 2016-1-8 下午3:06:11
 * 
 */
public class GetGoodsActivity extends Activity implements Callback, IXListViewListener {

	private Context mContext;
	private CaptureActivityHandlerGoods handler;
	private ViewfinderViewGoods viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;

	private DropDownListView listView;
	private List<GetGoodsInfo> dataList = new ArrayList<GetGoodsInfo>();//所有数据
	private GetGoodsAdapter mAdapter;

	private EditText edtBillcode;
	private String strBillcode;

	private Timer timer;
	private int orderType = 0;//默认为派送 0：派件1：代取件

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_get_goods);

		if (savedInstanceState != null) {
			GlobalInstanceStateHelper.restoreInstanceState(this, savedInstanceState);
		}

		findViewById();
	}

	@SuppressWarnings("deprecation")
	protected void onResume() {
		super.onResume();

		MyApplication.baseActivity=this;
		setImmerseLayout(findViewById(R.id.layout_camera_top));
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;
	}

	@SuppressWarnings("unchecked")
	private void findViewById(){

		CameraManagerGoods.init(getApplication());
		viewfinderView = (ViewfinderViewGoods) findViewById(R.id.viewfinder_view);

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		timer = new Timer(true);
		timer.schedule(task, 1000, 3000); //延时1000ms后执行，3000ms执行一次

		if(getIntent() != null){
			dataList = (List<GetGoodsInfo>) getIntent().getSerializableExtra("dataList");
			orderType = getIntent().getIntExtra("type", 0);
		}

		mContext = GetGoodsActivity.this;

		edtBillcode = (EditText) findViewById(R.id.edt_get_goods_billcode);
		listView = (DropDownListView) findViewById(R.id.lv_public_dropdown);

		listView.setDivider(new ColorDrawable(Color.TRANSPARENT)); 
		//		listView.setDividerHeight(20);
		listView.setPullRefreshEnable(false);
		//		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);

		mAdapter = new GetGoodsAdapter(mContext, dataList);
		listView.setAdapter(mAdapter);

		mAdapter.setOnBottomClickListener(new OnBottomClickListener() {

			@Override
			public void onBottomClick(View v, int position) {
				// TODO Auto-generated method stub
				if(v.getId() == R.id.layout_item_getgoods){

					GetGoodsInfo info = dataList.get(position);
					if(info.isScaned()){
						info.setScaned(false);
					}else{
						info.setScaned(true);
					}

					mAdapter.notifyDataSetChanged();
				}
			}
		});
	}
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		timer.cancel(); //退出计时器
		CameraManagerGoods.get().closeDriver();
	}

	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	public void sure(View v){

		strBillcode = edtBillcode.getText().toString();
		checkStatus(strBillcode);
	}

	protected void setImmerseLayout(View view) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			/*
			 * window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
			 * , WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			 */
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

			int statusBarHeight = ScreenUtil.getStatusBarHeight(this.getBaseContext());
			view.setPadding(0, statusBarHeight, 0, 0);
		}else{
			view.setVisibility(View.GONE);
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManagerGoods.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandlerGoods(GetGoodsActivity.this, decodeFormats,
					characterSet);
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {

		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public ViewfinderViewGoods getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	/**
	 * 扫描到结果
	 * @param obj
	 */
	public void handleDecode(Result obj) {
		inactivityTimer.onActivity();

		strBillcode = obj.getText();

		if(TextUtils.isEmpty(strBillcode)){
			CommandTools.showToast("扫描结果错误");
			return;
		}

		checkStatus(strBillcode);
	}

	/**
	 * 取到单号后和本地数据比较
	 * 单号相同则更新状态
	 * @param billcode
	 */
	private void checkStatus(String billcode){

		boolean flag = false;
		int len = dataList.size();
		for(int i=0; i<len; i++){

			GetGoodsInfo info = dataList.get(i);
			if(info.getWaybillCode().equals(billcode)){

				flag = true;
				if(info.isScaned()){

					CommandTools.showToast("单号已扫描");
					VoiceHint.playErrorSounds();
				}else{
					VoiceHint.playRightSounds();
					info.setScaned(true);
				}
				break;
			}
		}

		if(flag == false){

			CommandTools.showToast("未发现匹配运单");
			VoiceHint.playErrorSounds();
		}else{
			mAdapter.notifyDataSetChanged();
		}

	}

	/**
	 * 提交
	 * @param v
	 */
	public void submit(View v){

		int count = 0;
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {

			for(int i=0; i<dataList.size(); i++){

				GetGoodsInfo info = dataList.get(i);
				if(info.isScaned()){
					count++;

					jsonArray.put(info.getOrderId());
				}
			}

			//这里做了修改，之前是传运单号ID，现在增加了新的接口，为了统一传递订单ID即可
			jsonObject.put("orderIdList", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if(count == 0){
			CommandTools.showToast("请至少选择一条订单");
			return;
		}

		String strUrl;
		if(orderType == 0){//默认为派送 0：派件1：代取件
			strUrl = "order/packet/updatedelivery.htm";
		}else{
			strUrl = "order/agent/deliveryorder.htm";
		}
		CustomProgress.showDialog(mContext,"确认收件中", false, null);
		RequestUtilNet.postDataToken(mContext, strUrl, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CustomProgress.dissDialog();
				CommandTools.showToast(remark);
				if(success != 0){
					return;
				}

				EventBus mEventBus = EventBus.getDefault();
				Message msg = new Message();

				//通知刷新待取件数据
				msg.what = OrderUtil.REFRESH_WAIT_DATA;
				mEventBus.post(msg);

				//通知刷新配送中数据
				msg.what = OrderUtil.REFRESH_DISTRIBUTION_DATA;
				mEventBus.post(msg);

				finish();
			}
		});
	}

	private Handler mHandler = new Handler(){

		public void handleMessage(Message msg){

			if(mHandler == null || msg == null){
				return;
			}

			msg.what = R.id.restart_preview;
			if(handler != null){
				handler.dispatchMessage(msg);
			}
		}
	};

	/**
	 * 返回
	 * 
	 * @param v
	 */
	public void back(View v) {

		finish();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	TimerTask task = new TimerTask(){  

		public void run() {  

			Message message = new Message();      
			message.what = 1;      
			mHandler.sendMessage(message);    
		}  
	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		GlobalInstanceStateHelper.saveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		GlobalInstanceStateHelper.restoreInstanceState(this, savedInstanceState);
		findViewById();
	}
}