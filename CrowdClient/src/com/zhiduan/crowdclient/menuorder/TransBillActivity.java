package com.zhiduan.crowdclient.menuorder;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.adapter.TransBillAdapter;
import com.zhiduan.crowdclient.adapter.TransBillAdapter.OnBottomClickListener;
import com.zhiduan.crowdclient.data.CrowedUserInfo;
import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.OrderUtil;
import com.zhiduan.crowdclient.util.OrderUtilTools;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.OrderUtil.RefreshCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.CustomProgress;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.GeneralDialog;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;
import com.zhiduan.crowdclient.view.dialog.DialogUtils;
import com.zhiduan.crowdclient.view.dialog.DialogUtils.DialogCallback;
import android.os.Bundle;
import android.os.Message;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/** 
 * 转单
 * 
 * @author yxx
 *
 * @date 2016-7-26 下午3:56:04
 * 
 */
public class TransBillActivity extends BaseActivity implements IXListViewListener{

	private Context mContext;
	private DropDownListView listView;
	private TransBillAdapter mAdapter;

	private EditText edtSearch;
	private List<CrowedUserInfo> dataList = new ArrayList<CrowedUserInfo>();
	private List<CrowedUserInfo> sortList = new ArrayList<CrowedUserInfo>();
	private int pageNumber = 1;//当前页

	private boolean isOffice = false;//是否是楼长，楼长可以无限转单
	private int transSize = 1;//转单数，用来控制全部转单后关闭界面
	private int transOverplus = 0;//转单剩余次数

	private TextView tvNoData;
	private ImageView imgNoData;
	private LinearLayout layoutNoData;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_trans_bill, this);

		findViewById();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		setTitle("转单");
		if(TextUtils.isEmpty(MyApplication.getInstance().m_userInfo.m_strUserOffice)){
			isOffice = false;
		}else{
			isOffice = true;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void findViewById(){

		mContext = TransBillActivity.this;

		edtSearch = (EditText) findViewById(R.id.edt_order_billcode);
		listView = (DropDownListView) findViewById(R.id.lv_public_dropdown);

		tvNoData = (TextView) findViewById(R.id.tv_no_data);
		imgNoData = (ImageView) findViewById(R.id.img_no_data);
		layoutNoData = (LinearLayout) findViewById(R.id.layout_no_data);
		tvNoData.setText("暂无该学校小派");

		mAdapter = new TransBillAdapter(mContext, sortList);
		listView.setAdapter(mAdapter);

		listView.setDivider(new ColorDrawable(Color.TRANSPARENT)); 
		listView.setPullRefreshEnable(true);
		listView.setPullLoadEnable(false);
		//		listView.setDividerHeight(20);
		listView.setXListViewListener(this);

		mAdapter.setOnBottomClickListener(new OnBottomClickListener() {

			@Override
			public void onBottomClick(View v, int position) {

				CrowedUserInfo info = sortList.get(position);
				if(v.getId() == R.id.item_transbill_detail){

					checkTransDialog(info.getRealName(), info.getUserId());
				}else if(v.getId() == R.id.layout_transbill_call){

					DialogUtils.showCallPhoneDialog(TransBillActivity.this, info.getPhone(), null);
				}
			}
		});

		edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {

				if(arg1 == EditorInfo.IME_ACTION_SEARCH){
					CommandTools.hidenKeyboars(mContext, edtSearch);
					searchData(null);
				}else if(arg1 == EditorInfo.IME_ACTION_NEXT){
					CommandTools.hidenKeyboars(mContext, edtSearch);
					searchData(null);
				}
				return false;
			}
		});

		if(!isOffice){
			getTransOverplus();
		}

		onRefresh();
	}

	public void onEventMainThread(Message msg) {

		if(msg.what == OrderUtil.NO_NET_CONNECTION){
			OrderUtilTools.setNoNetChangedByTransBill(listView, dataList, sortList, mAdapter, tvNoData, imgNoData, layoutNoData);
		}
	}

	/**
	 * 弹出转单确认对话框
	 * 楼长可以无限转单
	 * @param name
	 * @param userId接收人ID
	 */
	private void checkTransDialog(String name, final String userId){

		//判断当日转单次数是否用完
		if(transOverplus <= 0 && !isOffice){

			DialogUtils.showOneButtonDialog(mContext, GeneralDialog.DIALOG_ICON_TYPE_5, "提示", "今日转单次数已用完！", "我知道了", new DialogCallback() {

				@Override
				public void callback(int position) {
					// TODO Auto-generated method stub
					if(position == 0){

						finish();
					}
				}
			});

			return;
		}

		DialogUtils.showTransBillDialog(TransBillActivity.this, isOffice,  MyApplication.getInstance().m_userInfo.m_strUserName, name, transOverplus, new DialogCallback() {

			@Override
			public void callback(int position) {
				// TODO Auto-generated method stub
				if(position == 1){

					try {
						JSONArray jsonArray = new JSONArray(getIntent().getStringExtra("orderIdList"));
						transBill(jsonArray.get(0).toString(), userId);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

	/**
	 * 接口名称 (730)确认转单
	 * 请求类型 post
	 * 请求Url  /packet/assign/confirm.htm
	 */
	private void transBill(String orderId, String userId){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("takerId", orderId);
			jsonObject.put("receiveUser", userId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		CustomProgress.showDialog(TransBillActivity.this, "转单中", false, null);
		RequestUtilNet.postDataToken(mContext, OrderUtil.assign_confirm, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {

				CommandTools.showToast(remark);
				if(success != 0){
					return;
				}

				--transSize;
				if(transSize <= 0){

					OrderUtilTools.refreshDataList(mEventBus, 1);

					finish();
				}
			}
		});
	}

	/**
	 * 取消
	 * @param v
	 */
	public void back(View v){

		dataList.clear();
		sortList.clear();
		finish();
	}

	public void testData(){

		sortList.clear();
		for(int i=0; i<5; i++){

			CrowedUserInfo info = new CrowedUserInfo();

			sortList.add(info);
		}

		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onRefresh() {
		
		testData();

//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject.put("pageNumber", 1);//当前页
//			jsonObject.put("qp", "");//查询所有
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		CustomProgress.showDialog(mContext, "资料加载中", true, null);
//		OrderUtil.getCrowedUserList(mContext, jsonObject, 0, mAdapter, listView, dataList, sortList, new RefreshCallback() {
//
//			@Override
//			public void callback(int success, int totalCount, int pageSize) {
//
//				OrderUtilTools.setNoData(sortList.size(), 2, tvNoData, imgNoData, layoutNoData);
//
//				if(success != 0){
//					return;
//				}
//
//				if(sortList.size() >= pageSize){
//
//					pageNumber = 2;
//					listView.setLoadShow();
//					listView.setPullLoadEnable(true);
//				}
//			}
//		});
	}

	@Override
	public void onLoadMore() {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("pageNumber", pageNumber);//当前页
			jsonObject.put("qp", "");//
		} catch (JSONException e) {
			e.printStackTrace();
		}

		OrderUtil.getCrowedUserList(mContext, jsonObject, 1, mAdapter, listView, dataList, sortList, new RefreshCallback() {

			@Override
			public void callback(int success, int totalCount, int pageSize) {

				if(success != 0){
					listView.setLoadShow();
					listView.setLoadFinished();
					return;
				}

				++pageNumber;
			}
		});
	}

	/**
	 * 按照小派姓名或电话搜索
	 * @param v
	 */
	public void searchData(View v){

		String strData = edtSearch.getText().toString();

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("pageNumber", 1);//当前页
			jsonObject.put("qp", strData);//查询所有
		} catch (JSONException e) {
			e.printStackTrace();
		}

		CustomProgress.showDialog(mContext, "资料加载中", true, null);
		OrderUtil.getCrowedUserList(mContext, jsonObject, 0, mAdapter, listView, dataList, sortList, new RefreshCallback() {

			@Override
			public void callback(int success, int totalCount, int pageSize) {

				pageNumber = 1;
				OrderUtilTools.setNoData(sortList.size(), 2, tvNoData, imgNoData, layoutNoData);

				CustomProgress.dissDialog();
				if(success != 0){
					return;
				}

				listView.setPullLoadEnable(false);
				listView.setLoadHide();
			}
		});
	}

	/**
	 * 查询转单剩余次数
	 */
	private void getTransOverplus(){

		JSONObject jsonObject = new JSONObject();

		CustomProgress.showDialog(mContext, "资料加载中", true, null);
		RequestUtilNet.postDataToken(mContext, OrderUtil.assign_surplusnum, jsonObject, new RequestCallback() {

			@Override
			public void callback(int success, String remark, JSONObject jsonObject) {
				// TODO Auto-generated method stub

				if(success != 0){
					CommandTools.showToast(remark);
					return;
				}

				transOverplus = jsonObject.optInt("data", 0);
			}
		});
	}

}
