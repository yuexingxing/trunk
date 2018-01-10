package com.zhiduan.crowdclient.share;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.R.layout;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.data.InviteInfo;
import com.zhiduan.crowdclient.data.TaskInfo;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
/**
 * 
 * <pre>
 * Description	邀请收入
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-11-24 下午9:49:03  
 * </pre>
 */
public class InviteIncomeActivity extends BaseActivity implements IXListViewListener{

	private DropDownListView list_invite_income;
	private List<InviteInfo> datas = new ArrayList<InviteInfo>();
	private InviteAdapter adapter;
	private InviteInfo inviteInfo;
	private Context mContext;
	private int PageNumber = 1;
	private int PageSize = 10;
	private int PageCount;
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_invite_income, this);
		mContext=this;
		setTitle("邀请收入");
	}

	@Override
	public void initView() {
		list_invite_income=(DropDownListView) findViewById(R.id.list_invite_income);
		list_invite_income.setPullLoadEnable(true);
		list_invite_income.setPullRefreshEnable(true);
		list_invite_income.setXListViewListener(this);
	}

	
	@Override
	public void initData() {
		adapter=new InviteAdapter(mContext, datas, 2);
		list_invite_income.setAdapter(adapter);
		
		get_InviteData(PageNumber);
	}
	/**
	 * 获取邀请收入列表
	 */
	private void get_InviteData(int pagenumber) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("pageNumber", pagenumber);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		RequestUtilNet.postDataToken(mContext, Constant.getInviteIncome_url,
				jsonObject, new RequestCallback() {
					@Override
					public void callback(int success, String remark,
							JSONObject jsonObject) {
						try {
							if (success == 0) {
								jsonObject=jsonObject.optJSONObject("data");
								if (PageNumber == 1) {
									datas.clear();
								}
								PageCount=jsonObject.optInt("totalPageCount");
								if (PageNumber>=PageCount) {
									list_invite_income.setLoadHide();
								}
								list_invite_income.setRefreshTime(CommandTools.getTime());
								JSONArray array_income=jsonObject.optJSONArray("responseDto");
								for (int i = 0; i < array_income.length(); i++) {
									JSONObject object_income=array_income.optJSONObject(i);
									inviteInfo=new InviteInfo();
									inviteInfo.setInvite_name(object_income.optString("realName"));
									inviteInfo.setInvite_date(object_income.optString("arriveDate"));
									inviteInfo.setInvite_phone(object_income.optString("phone"));
									inviteInfo.setInvite_income(object_income.optLong("income"));
									inviteInfo.setInvite_icon(object_income.optString("icon"));
									inviteInfo.setInvite_sex(object_income.optString("gender"));
									inviteInfo.setExpPoint(object_income.optInt("expPoint"));
									datas.add(inviteInfo);
								}
								if (array_income.length() < 10) {
									list_invite_income.setLoadHide();
								} else {
									list_invite_income.setLoadShow();
								}
								adapter.notifyDataSetChanged();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

	}
	@Override
	public void onRefresh() {
		PageNumber=1;
		get_InviteData(PageNumber);
		list_invite_income.stopRefresh();
		list_invite_income.stopLoadMore();
		
	}

	@Override
	public void onLoadMore() {
		++PageNumber;
		get_InviteData(PageNumber);
		list_invite_income.stopRefresh();
		list_invite_income.stopLoadMore();
	
		
	}

	

}
