package com.zhiduan.crowdclient.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.NetTaskUtil;
import com.zhiduan.crowdclient.net.NetUtil;
import com.zhiduan.crowdclient.util.AES;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Util;

/**
 * 
 * @author HeXiuHui
 * 
 */
public class UserService {

	/** 登陆接口 **/
	public static void login(String userName, String accountPwd, String clientId, OnPostExecuteListener listener, Object tag) {

		MyApplication.getInstance().sendTime = CommandTools.getTimes();
		MyApplication.getInstance().mRandom = CommandTools.CeShi();

		JSONObject object = new JSONObject();
		try {
			object.put("userName", AES.encrypt(userName));
			object.put("accountPwd", AES.encrypt(accountPwd));
			object.put("imei", CommandTools.getMIME(MyApplication.getInstance()));//
			object.put("clientId", clientId + "");
		} catch (Exception e) {
			e.printStackTrace();
		}

//		CommandTools.showDialog(CommandTools.getGlobalActivity(), object.toString());
		NetTaskUtil.makeTextNetTask(Constant.FormalURL + Constant.Login_url, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}
	
	/** 快速登陆接口 
	 * @return **/
	public static LoadTextNetTask quickLogin(String userName, String userCode, String clientId, OnPostExecuteListener listener, Object tag) {
		String quicklogon = "user/quicklogin.htm";
		MyApplication.getInstance().sendTime = CommandTools.getTimes();
		MyApplication.getInstance().mRandom = CommandTools.CeShi();

		JSONObject object = new JSONObject();
		try {
			object.put("userName", AES.encrypt(userName));
			object.put("code", userCode);
			object.put("imei", CommandTools.getMIME(MyApplication.getInstance()));//
			object.put("clientId", clientId + "");
		} catch (Exception e) {
			e.printStackTrace();
		}

//		CommandTools.showDialog(CommandTools.getGlobalActivity(), object.toString());
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + quicklogon, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}
	
	/** 是否绑定手机
	 * @return **/
	public static LoadTextNetTask isBind(String source, String thirdpartyToken, OnPostExecuteListener listener, Object tag) {
		String quicklogon = "user/isbind.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("source", source);
			object.put("thirdpartyToken", thirdpartyToken);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		CommandTools.showDialog(CommandTools.getGlobalActivity(), object.toString());
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + quicklogon, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}
	
	/** 绑定手机
	 * @return **/
	public static LoadTextNetTask bind(String clientId, String code, String imei, String phone, String source, String thirdpartyToken, OnPostExecuteListener listener, Object tag) {
		String quicklogon = "user/thirdpartybind.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("clientId", clientId);
			object.put("code", code);
			object.put("imei", imei);
			object.put("phone", phone);
			object.put("source", source);
			object.put("thirdpartyToken", thirdpartyToken);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		CommandTools.showDialog(CommandTools.getGlobalActivity(), object.toString());
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + quicklogon, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}
	
	/** 第三方登录
	 * @return **/
	public static LoadTextNetTask geveLogin(String clientId, String imei, String source, String thirdpartyToken, OnPostExecuteListener listener, Object tag) {
		String quicklogon = "user/thirdpartylogin.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("clientId", clientId);
			object.put("imei", imei);
			object.put("source", source);
			object.put("thirdpartyToken", thirdpartyToken);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		CommandTools.showDialog(CommandTools.getGlobalActivity(), object.toString());
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + quicklogon, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 修改登录密码 **/
	public static LoadTextNetTask updatePassword(String oldPassWord, String newPassWord, OnPostExecuteListener listener, Object tag) {
		JSONObject object = new JSONObject();
		try {
			MyApplication.getInstance().sendTime = CommandTools.getTimes();
			MyApplication.getInstance().mRandom = CommandTools.CeShi();
			
			object.put("accountPwd", AES.encrypt(oldPassWord));
			object.put("newPwd", AES.encrypt(newPassWord));
			object.put("token", MyApplication.getInstance().m_userInfo.toKen);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + Constant.updatepwd_url, object.toString(), NetUtil.TOKEN, listener, tag);
	}

	/** 修改性别 **/
	public static LoadTextNetTask updateSex( String sex, OnPostExecuteListener listener, Object tag) {

		JSONObject object = new JSONObject();
		try {
			
			MyApplication.getInstance().sendTime = CommandTools.getTimes();
			MyApplication.getInstance().mRandom = CommandTools.CeShi();
			
			object.put("gender", sex);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + Constant.updatesex_url, object.toString(), NetUtil.TOKEN, listener, tag);
	}

	/** 获取所有学校列表 **/
	public static LoadTextNetTask getSchool(OnPostExecuteListener listener, Object tag) {
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + Constant.getSchoolList_url,
				object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 查询短信价格 */
	public static LoadTextNetTask inquireSmsPrice(OnPostExecuteListener listener, Object tag) {

		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		try {
			object.put("SITE_GCODE", "S1");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		// "http://axd.shzhiduan.com:9650/Campusex/api/SMSBought/PostGet_SMSPrice"
		return NetTaskUtil.makeTextNetTask(Constant.SmsURL + "SMSBought/PostGet_SMSPrice", object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}


	/** 获取快递公司列表 */
	public static LoadTextNetTask getExpressageLists(OnPostExecuteListener listener, Object tag) {
		String getExpressageLists = "ExpressComPany/PostInfoList";
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		// "http://axd.shzhiduan.com:9650/Campusex/api/ExpressComPany/PostInfoList"
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + getExpressageLists, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 更新推送ID */
	public static LoadTextNetTask channelId(String phone, String channelid, String userid, OnPostExecuteListener listener, Object tag) {

		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		try {
			object.put("ver", "1.0");
			object.put("sign", "D8DCB7A5E6F2DF3E844DDA39629F4521");
			object.put("phone_number", phone);
			object.put("pay_type", "wx_push_setid");
			object.put("channelid", channelid);
			object.put("userid", userid);
			object.put("dervice_type", 1);
			// 获取当前时间

		} catch (JSONException e) {
			e.printStackTrace();
		}

		System.out.println("-----------" + object.toString());
		return NetTaskUtil.makeTextNetTask("", Util.getEncryptionData(object.toString()), NetUtil.NOT_TOKEN, listener, tag);
	}
	
	/** 获取来自用户的评价列表 */
	public static LoadTextNetTask getEvaluateLists(int pageNumber, OnPostExecuteListener listener, Object tag) {
		String getExpressageLists = "appraise/fromuser/list.htm";
		JSONObject object = new JSONObject();
		try {
			object.put("pageNumber", pageNumber);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// "http://axd.shzhiduan.com:9650/Campusex/api/ExpressComPany/PostInfoList"
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + getExpressageLists, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
	/** 获取用户评价列表 */
	public static LoadTextNetTask getEvaluate(int appraiserOther, int pageNumber, OnPostExecuteListener listener, Object tag) {
		String getExpressageLists = "appraise/fromtaker/list.htm";
		JSONObject object = new JSONObject();
		try {
			object.put("appraiserOther", appraiserOther);
			object.put("pageNumber", pageNumber);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// "http://axd.shzhiduan.com:9650/Campusex/api/ExpressComPany/PostInfoList"
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + getExpressageLists, object.toString(), NetUtil.TOKEN, listener, tag);
	}
}
