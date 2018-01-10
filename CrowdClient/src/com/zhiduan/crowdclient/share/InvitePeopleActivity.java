package com.zhiduan.crowdclient.share;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.zhiduan.crowdclient.R;
import com.zhiduan.crowdclient.activity.BaseActivity;
import com.zhiduan.crowdclient.data.InviteInfo;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.RequestUtilNet;
import com.zhiduan.crowdclient.util.RequestUtilNet.RequestCallback;
import com.zhiduan.crowdclient.view.DropDownListView;
import com.zhiduan.crowdclient.view.DropDownListView.IXListViewListener;
/**
 * 
 * <pre>
 * Description	邀请人数
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-11-24 下午9:48:17  
 * </pre>
 */
public class InvitePeopleActivity extends BaseActivity implements IXListViewListener{

	private DropDownListView list_invite_people;
	private List<InviteInfo> datas = new ArrayList<InviteInfo>();
	private InviteAdapter adapter;
	private InviteInfo inviteInfo;
	private Context mContext;
	private int PageNumber = 1;
	private int PageSize = 10;
	private int PageCount;
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_share_people, this);
		mContext=this;
		setTitle("邀请人数");
	}

	@Override
	public void initView() {
		list_invite_people=(DropDownListView) findViewById(R.id.list_invite_people);
		list_invite_people.setPullLoadEnable(true);
		list_invite_people.setPullRefreshEnable(true);
		list_invite_people.setXListViewListener(this);
	}

	@Override
	public void initData() {

		adapter=new InviteAdapter(mContext, datas, 1);
		list_invite_people.setAdapter(adapter);
	
		get_InviteData(PageNumber);
	}
	/**
	 * 获取邀请人列表
	 */
	/**
	 * 获取邀请人列表
	 */
	private void get_InviteData(int pagenumber) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("pageNumber", pagenumber);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		RequestUtilNet.postDataToken(mContext, Constant.getInvitePeople_url,
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
									list_invite_people.setLoadHide();
								}
								list_invite_people.setRefreshTime(CommandTools.getTime());
								JSONArray array_people=jsonObject.optJSONArray("responseDto");
								for (int i = 0; i < array_people.length(); i++) {
									JSONObject object_people=array_people.optJSONObject(i);
									inviteInfo=new InviteInfo();
									inviteInfo.setInvite_name(object_people.optString("realName"));
									inviteInfo.setInvite_date(object_people.optString("auditDate"));
									inviteInfo.setInvite_phone(object_people.optString("phone"));
									//inviteInfo.setInvite_income(object_people.optString("income"));
									inviteInfo.setInvite_icon(object_people.optString("icon"));
									inviteInfo.setInvite_sex(object_people.optString("gender"));
									inviteInfo.setExpPoint(object_people.optInt("expPoint"));
									datas.add(inviteInfo);
								}
								if (array_people.length() < 10) {
									list_invite_people.setLoadHide();
								} else {
									list_invite_people.setLoadShow();
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
		list_invite_people.stopRefresh();
		list_invite_people.stopLoadMore();
		
	}

	@Override
	public void onLoadMore() {
		++PageNumber;
		get_InviteData(PageNumber);
		list_invite_people.stopRefresh();
		list_invite_people.stopLoadMore();
	
		
	}
	

}
