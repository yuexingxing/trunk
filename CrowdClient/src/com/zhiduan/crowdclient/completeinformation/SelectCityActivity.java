package com.zhiduan.crowdclient.completeinformation;

import java.util.ArrayList;
import java.util.Collections;
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

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.adapter.SelectCityAdapter;
import com.zhiduan.crowdclient.authentication.CharacterParser;
import com.zhiduan.crowdclient.authentication.MyLetterSortView;
import com.zhiduan.crowdclient.authentication.MyLetterSortView.OnTouchingLetterChangedListener;
import com.zhiduan.crowdclient.authentication.SchoolComparators;
import com.zhiduan.crowdclient.data.SchoolInfo;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.MyCallback;

public class SelectCityActivity extends Activity {

	private Context mContext;

	private EditText edtSearch;

	private ListView listView;
	private List<SchoolInfo> dataList = new ArrayList<SchoolInfo>();
	private List<SchoolInfo> sortList = new ArrayList<SchoolInfo>();
	private SelectCityAdapter mAdapter;

	private TextView tv_mid_letter;
	private MyLetterSortView right_letter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_city);

		setImmerseLayout();
		findViewById();
		downloadData();
	}
	private void findViewById(){

		mContext = SelectCityActivity.this;
		edtSearch = (EditText) findViewById(R.id.select_city_edt_search);

		listView = (ListView) findViewById(R.id.listView_select_city);
		listView.setDividerHeight(0);

		mAdapter = new SelectCityAdapter(mContext, sortList);
		listView.setAdapter(mAdapter);

		tv_mid_letter = (TextView) findViewById(R.id.tv_mid_letter);
		right_letter = (MyLetterSortView) findViewById(R.id.right_letter);
		right_letter.setTextView(tv_mid_letter);

		right_letter.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = mAdapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					listView.setSelection(position );
				}

			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				Intent intent = new Intent();
				intent.putExtra("cityName", sortList.get(arg2).getCOLLEGE_NAME());
				intent.putExtra("cityId", sortList.get(arg2).getCOLLEGE_CODE());
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

				transList(arg0.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

	}

	/**
	 * 查询城市列表接口
	 */
	public void downloadData(){

		JSONObject jsonObject = new JSONObject();

		RequestUtilNet.postData(mContext, Constant.getCityData, jsonObject, new MyCallback() {

			@Override
			public void callback(JSONObject jsonObject) {

				try {
					CommandTools.showToast(jsonObject.optString("message"));
					int success=jsonObject.optInt("success");
					if (success==0) {
						JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
	
						dataList.clear();
						for(int i=0; i<jsonArray.length(); i++){
	
							jsonObject = jsonArray.getJSONObject(i);
	
							SchoolInfo info = new SchoolInfo();
							info.setCOLLEGE_NAME(jsonObject.getString("cityName"));//城市的名字
							info.setCOLLEGE_CODE(jsonObject.getString("cityId"));//序号
							String str1 = (CharacterParser.getInstance().getSelling(info.getCOLLEGE_NAME()).toUpperCase());
							info.setPinYin(str1);
	
							dataList.add(info);
						}
					} else {
						try {
							String remark = jsonObject.getString("message");
							CommandTools.showToast(remark);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					transList(null);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * 对数据集合进行按首字母排序
	 * @param str
	 */
	private void transList(String str){

		sortList.clear();
		if(TextUtils.isEmpty(str)){
			sortList.addAll(dataList);
		}else{

			int len = dataList.size();
			for(int i=0; i<len; i++){

				SchoolInfo info = dataList.get(i);

				if(info.getCOLLEGE_NAME().contains(str) || info.getPinYin().contains(str.toUpperCase())){
					sortList.add(info);
				}
			}
		}

		Collections.sort(sortList, new SchoolComparators());
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 取消
	 * @param v
	 */
	public void back(View v){

		finish();
	}
	@Override
	protected void onResume() {
		super.onResume();
		MyApplication.baseActivity=this;
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
