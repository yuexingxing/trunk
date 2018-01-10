package com.zhiduan.crowdclient.completeinformation;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.CommonAdapter;
import com.zhiduan.crowdclient.adapter.ViewHolder;
import com.zhiduan.crowdclient.authentication.Trans2PinYin;
import com.zhiduan.crowdclient.data.SchoolInfo;
import com.zhiduan.crowdclient.menucenter.UserInfoActivity;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.util.VoiceHint;

/**
 * <pre>
 * Description   选择学校界面
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-5-24 下午8:07:36
 * </pre>
 */
public class SelectSchoolActivity extends Activity {
	private static final String TAG = "zdkj";
	private Context mContext;

	private ListView listView;
	private List<SchoolInfo> dataList = new ArrayList<SchoolInfo>();//存放下载的总数据
	private List<SchoolInfo> sortList = new ArrayList<SchoolInfo>();//存放筛选出的数据，显示在列表里
	private CommonAdapter<SchoolInfo> commonAdapter;

	private EditText edtSearch;
	private TextView tvCity;
	private final int SELECT_CITY = 0x0021;
	private String userinfo;
	public AMapLocationClient mLocationClient = null;//声明AMapLocationClient类对象
	public AMapLocationClientOption mLocationOption = null;	//声明mLocationOption对象
	private SchoolInfo info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_school);

		setImmerseLayout();
		findViewById();
		initPostion();
	}

	private void findViewById(){

		mContext = SelectSchoolActivity.this;
		tvCity = (TextView) findViewById(R.id.tv_select_school_city);

		edtSearch = (EditText) findViewById(R.id.select_school_edt_search);

		listView = (ListView) findViewById(R.id.listView_select_school);
		listView.setDividerHeight(0);
		listView.setAdapter(commonAdapter = new CommonAdapter<SchoolInfo>(mContext, sortList, R.layout.item_school) {

			@Override
			public void convert(ViewHolder helper, SchoolInfo item) {

				helper.setText(R.id.tv_item_school, item.getCOLLEGE_NAME());
				if(item.isSelect()){
					helper.setImageView(R.id.img_item_school, R.drawable.order_selected);
				}else{
					helper.setImageView(R.id.img_item_school, R.drawable.order_notselected);
				}
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				info = sortList.get(arg2);
				if (!TextUtils.isEmpty(info.getCOLLEGE_GCODE())) {
				if (!TextUtils.isEmpty(userinfo)) {
						UpdateShool(Integer.parseInt(info.getCOLLEGE_GCODE()));
					}else {
						Intent intent=new Intent(SelectSchoolActivity.this,SchoolDataActivity.class);
						Bundle bundle=new Bundle();
						bundle.putSerializable("schoolinfo", info);
						intent.putExtras(bundle);
						setResult(1111,intent);
						finish();
				}
				}
			}
		});

		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

				checkList(arg0.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

		userinfo=getIntent().getStringExtra("user");
	}

	/**
	 * 根据拼音查询，过滤相关数据
	 * @param str
	 */
	private void checkList(String str){

		sortList.clear();
		if(TextUtils.isEmpty(str)){
			sortList.addAll(dataList);
		}else{

			int len = dataList.size();
			for(int i=0; i<len; i++){

				SchoolInfo info = dataList.get(i);
				if(info.getCOLLEGE_NAME().contains(str) || info.getPinYin().contains(str)){
					sortList.add(info);
				}
			}
		}

		commonAdapter.notifyDataSetChanged();
	}

	protected void onResume(){
		super.onResume();
		MyApplication.baseActivity=this;
	}
	/**
	 * 修改学校信息
	 * @param id
	 */
	public void UpdateShool(int id) {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("collegeId", id);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		RequestUtilNet.postDataToken(mContext, Constant.updateschool_url,
				jsonObject, new RequestCallback() {

					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {

						CommandTools.showToast(remark);
						if (success == 0) {
							Intent intent=new Intent(SelectSchoolActivity.this,UserInfoActivity.class);
							Bundle bundle=new Bundle();
							bundle.putSerializable("schoolinfo", info);
							intent.putExtras(bundle);
							setResult(1111,intent);
							finish();
						}else {
							return;
						}
					}
				});

	
	}
	/**
	 * 根据城市查询学校
	 */
	public void downloadSchoolData(String cityId){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("cityName", cityId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "manage/college/allbycityname.htm";
		RequestUtilNet.postData(mContext, strUrl, jsonObject, new MyCallback() {

			@Override
			public void callback(JSONObject jsonObject) {
				try {
					JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));

					dataList.clear();
					for(int i=0; i<jsonArray.length(); i++){

						jsonObject = jsonArray.getJSONObject(i);

						SchoolInfo info = new SchoolInfo();
						info.setCOLLEGE_NAME(jsonObject.getString("fullName"));//首都师范大学
						info.setCOLLEGE_CODE(jsonObject.getString("collegeCode"));//10028
						info.setCOLLEGE_GCODE(jsonObject.getString("collegeId"));//
						info.setCOUNTYNAME(jsonObject.getString("countyName"));//海淀区
						info.setPinYin(Trans2PinYin.trans(info.getCOLLEGE_NAME()));
						info.setSelect(false);

						int len =SelectSchoolActivity.this.dataList.size();
						for(int k=0; k<len; k++){
							if(info.getCOLLEGE_CODE().equals(SelectSchoolActivity.this.dataList.get(k).getCOLLEGE_CODE())){
								info.setSelect(true);
								break;
							}
						}

						dataList.add(info);
					}

					checkList(null);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SELECT_CITY) {

			if(resultCode == RESULT_OK){

				String strCityName = data.getStringExtra("cityName");
				String strCityId = data.getStringExtra("cityId");
				tvCity.setText(strCityName);
				downloadSchoolData(strCityName);
			}
		}
	}

	/**
	 * 重新加载数据
	 * @param v
	 */
	public void refreshData(View v){

		//		downloadSchoolData("51");
	}

	/**
	 * 确认
	 * @param v
	 */
	public void nextStep(View v){

		int len = SelectSchoolActivity.this.dataList.size();
		if(len == 0){
			VoiceHint.playErrorSounds();
			CommandTools.showToast("请选择至少一条数据");
			return;
		}
		
		finish();
	}

	/**
	 * 选择城市
	 * @param v
	 */
	public void selectCity(View v){

		Intent intent = new Intent(this, SelectCityActivity.class);
		startActivityForResult(intent, SELECT_CITY);
	}

	/**
	 * 取消
	 * @param v
	 */
	public void back(View v){

		finish();
	}

	/**
	 * 初始化定位
	 */
	private void initPostion(){

		mLocationClient = new AMapLocationClient(getApplicationContext());
		//设置定位回调监听
		mLocationClient.setLocationListener(mLocationListener);

		//初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		//设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		//设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(true);
		//设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		//设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		//设置定位间隔,单位毫秒,默认为2000ms
		mLocationOption.setInterval(1000 * 3);
		//给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		//启动定位
		mLocationClient.startLocation();
	}

	//声明定位回调监听器
	public AMapLocationListener mLocationListener = new AMapLocationListener() {

		@Override
		public void onLocationChanged(AMapLocation amapLocation) {
			if (amapLocation != null) {
				if (amapLocation.getErrorCode() == 0) {
					//定位成功回调信息，设置相关消息
					amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
					amapLocation.getLatitude();//获取纬度
					amapLocation.getLongitude();//获取经度
					amapLocation.getAccuracy();//获取精度信息
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = new Date(amapLocation.getTime());
					df.format(date);//定位时间
					amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
					amapLocation.getCountry();//国家信息
					amapLocation.getProvince();//省信息
					amapLocation.getCity();//城市信息
					amapLocation.getDistrict();//城区信息
					amapLocation.getStreet();//街道信息
					amapLocation.getStreetNum();//街道门牌号信息
					amapLocation.getCityCode();//城市编码
					amapLocation.getAdCode();//地区编码

					tvCity.setText(amapLocation.getCity());
					downloadSchoolData(amapLocation.getCity());//定位成功后下载该城市对应的校区
					//针对上海地区下载校区列表
					/*if(amapLocation.getCity().contains("上海")){
						downloadSchoolData("51");//定位成功后下载该城市对应的校区
					}*/
				} else {
					//显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
					CommandTools.showToast("定位失败" + amapLocation.getErrorCode());
				}
			}

		}
	};

	/**
	 * 判断列表是否存在
	 * @param info
	 * @return
	 */
	private int checkSchoolList(SchoolInfo info){

		int len = SelectSchoolActivity.this.dataList.size();
		for(int i=0; i<len; i++){

			SchoolInfo extra = SelectSchoolActivity.this.dataList.get(i);
			if(extra.getCOLLEGE_CODE().equals(info.getCOLLEGE_CODE())){

				return i;
			}
		}

		return -1;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//被系统回收时保存全局变量值
		//GlobalInstanceStateHelper.saveInstanceState(outState);

		outState.putSerializable("dataList", (Serializable) dataList);
		outState.putSerializable("sortList", (Serializable) sortList);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		//恢复被系统回收后的值
		//GlobalInstanceStateHelper.restoreInstanceState(this, savedInstanceState);

		dataList = (List<SchoolInfo>) savedInstanceState.getSerializable("dataList");
		sortList = (List<SchoolInfo>) savedInstanceState.getSerializable("sortList");
	}
	
	protected void setImmerseLayout() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}
}
