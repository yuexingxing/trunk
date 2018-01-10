package com.zhiduan.crowdclient.service;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.NetTaskUtil;
import com.zhiduan.crowdclient.net.NetUtil;
import com.zhiduan.crowdclient.util.AES;
import com.zhiduan.crowdclient.util.CommandTools;
import com.zhiduan.crowdclient.util.Constant;
import com.zhiduan.crowdclient.util.Logs;
import com.zhiduan.crowdclient.util.MCrypt;
import com.zhiduan.crowdclient.util.Util;

/**
 * 
 * @author HeXiuHui
 * 
 */
public class PayMentService {

	public static LoadTextNetTask setPassWord(String veri_code, String id_number, String new_password, String mac_code,
			OnPostExecuteListener listener, Object tag) {

		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		String setPassWord = "UserAccount/Post_PayPassSet";
		MCrypt mCrypt = new MCrypt();
		try {
			new_password = Base64.encodeToString(mCrypt.encrypt(new_password), Base64.NO_WRAP) + MyApplication.getInstance().mRandom;
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		JSONObject object = new JSONObject();
		try {
			object.put("VERI_CODE", veri_code);
			object.put("ID_NUM", id_number);
			object.put("OLD_PASSWORD", "");
			object.put("NEW_PASSWORD", new_password);
			object.put("MAC_CODE", mac_code);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Logs.i("hexiuhui==", "" + object.toString());

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + setPassWord, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}


	public static LoadTextNetTask updatePassWord(String veri_code,
			String id_number, String old_password, String new_password,
			String mac_code, OnPostExecuteListener listener, Object tag) {

		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		String setPassWord = "UserAccount/Post_PayPassSet";
		MCrypt mCrypt = new MCrypt();
		try {
			old_password = Base64.encodeToString(mCrypt.encrypt(old_password),
					Base64.NO_WRAP) + MyApplication.getInstance().mRandom;
			new_password = Base64.encodeToString(mCrypt.encrypt(new_password),
					Base64.NO_WRAP) + MyApplication.getInstance().mRandom;
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		JSONObject object = new JSONObject();
		try {

			object.put("VERI_CODE", veri_code);
			object.put("ID_NUM", id_number);
			object.put("OLD_PASSWORD", old_password);
			object.put("NEW_PASSWORD", new_password);
			object.put("MAC_CODE", mac_code);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Logs.i("hexiuhui==", "" + object.toString());

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + setPassWord,
				object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	public static LoadTextNetTask getBalanceDetail(Context context,
			int currentPage, int pageCount, OnPostExecuteListener listener,
			Object tag) {
		String getBalanceDetail = "UserAccount/Post_GetUserBalanceDetailList";
		JSONObject jsonObject = new JSONObject();

		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();

		try {
			jsonObject.put("currentPage", currentPage + "");
			jsonObject.put("pageCount", pageCount + "");
			jsonObject.put("AuthSign",
					MyApplication.getInstance().m_userInfo.m_strUserAuthSign);
			jsonObject.put("MachineSystem", "Android");
			jsonObject.put("MachineCode", CommandTools.getMIME(context));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Logs.i("hexiuhui==", "" + jsonObject.toString());

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL
				+ getBalanceDetail, jsonObject.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}


	/** 查看石否激活钱包 */
	public static LoadTextNetTask isWxapi( OnPostExecuteListener listener, Object tag) {

		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		
		
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"acct/actv/ifactivate.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}

	
	/** 获取账户信息 */
	public static LoadTextNetTask getUserInfo( OnPostExecuteListener listener,
			Object tag) {
		
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"acct/uinfo.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}
	/** 获取账户信息 */
	public static LoadTextNetTask getUserStatistics(String userId,OnPostExecuteListener listener,
			Object tag) {
		
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		try {
			object.put("userId", userId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"user/querystatistics.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}
	/** 激活已认证用户钱包 */
	public static LoadTextNetTask activatereal(String idNo,String pwd,String phone,String code, OnPostExecuteListener listener,
			Object tag) {
		
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		try {
			object.put("idNo", idNo);
			object.put("pwd", AES.encrypt(pwd));
			object.put("phone", phone);
			object.put("code", code);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"acct/actv/activatereal.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}
	/** 重置钱包交易密码 */
	public static LoadTextNetTask resetpwd(String idNo,String pwd,String code, OnPostExecuteListener listener,
			Object tag) {
		
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		try {
			object.put("id", idNo);
			object.put("newPwd", AES.encrypt(pwd));
			object.put("code", code);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"acct/resetpwd.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}
	/** 获取用户最多可提现金额 */
	public static LoadTextNetTask getPayNumber( OnPostExecuteListener listener,
			Object tag) {
		
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"acct/drawamount.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}
	
	
	/** 添加账户第三方支付账号 */
	public static LoadTextNetTask addAlipay(String acctName,String acctNo,String thdType, OnPostExecuteListener listener,
			Object tag) {
		
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		try {
			object.put("acctName", acctName);
			
			object.put("acctNo", AES.encrypt(acctNo));
			
			object.put("deviceId", CommandTools.getMIME(MyApplication.getInstance()));
			object.put("thdType", thdType);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"acct/thd/add.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}
	/** 删除账户第三方支付账号 */
	public static LoadTextNetTask deleteAlipay(long thdId, OnPostExecuteListener listener,
			Object tag) {
		
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		try {
			object.put("thdId", thdId);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"acct/thd/del.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}
	/** 修改钱包交易密码 */
	public static LoadTextNetTask alterPassWord(String newPwd,String oldPwd, OnPostExecuteListener listener,
			Object tag) {
		
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		try {
			object.put("newPwd", AES.encrypt(newPwd));
			object.put("oldPwd", AES.encrypt(oldPwd));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"acct/modpwd.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}
	/** 账单记录 */
	public static LoadTextNetTask billRecord(String lastTime,String nextTime,int index,int num,int payType, OnPostExecuteListener listener,
			Object tag) {
		
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		try {
			object.put("startDate", lastTime+" 00:00:00");
			object.put("endDate", nextTime+" 23:59:59");
			object.put("index", index);
			object.put("num", num);
			object.put("payType", payType);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"acct/bill/all.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}
	/** 收入详情 */
	public static LoadTextNetTask income(String lastTime,String nextTime,int index,int num, OnPostExecuteListener listener,
			Object tag) {
		
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		try {
			object.put("startDate", lastTime+" 00:00:00");
			object.put("endDate", nextTime+" 23:59:59");
			object.put("index", index);
			object.put("num", num);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"acct/bill/income.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}
	
	/** 账单明细 */
	public static LoadTextNetTask withdrawDepositSchedule(long detailId,int payType, OnPostExecuteListener listener,
			Object tag) {
		
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		
		try {
			
			object.put("detailId", detailId);
			object.put("payType",payType);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"acct/bill/detail.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}
	/** 钱包提现 */
	public static LoadTextNetTask withdrawDeposit(String IdentifyCode,long balance,String payPwd,String phone,long thdId, OnPostExecuteListener listener,
			Object tag) {
		
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		try {
			object.put("IdentifyCode", IdentifyCode);
			object.put("balance", balance);
			object.put("devId", CommandTools.getDevId());
			object.put("payPwd", AES.encrypt(payPwd));
			object.put("phone", phone);
			object.put("thdId", thdId);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"acct/user/exchangebalance.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}
	
	/** 查询账户第三方支付账号 */
	public static LoadTextNetTask selectNumbet(OnPostExecuteListener listener,
			Object tag) {
		
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"acct/thd/all.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}
	
	/** 更新微信支付信息 */
	public static LoadTextNetTask getWeiXin( OnPostExecuteListener listener,
			Object tag) {
		
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		try {
			object.put("deviceId", CommandTools.getMIME(MyApplication.baseActivity));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"acct/thd/updatewxinfo.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}
	/** 设置默认支付账号 */
	public static LoadTextNetTask setdefault( String thdId, OnPostExecuteListener listener,
			Object tag) {
		
		MyApplication.getInstance().mRandom = CommandTools.CeShi();
		MyApplication.getInstance().sendTime = CommandTools.initDataTime();
		JSONObject object = new JSONObject();
		try {
			object.put("thdId", thdId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL+"acct/thd/setdefault.htm", Util.getEncryptionData(object.toString()), NetUtil.TOKEN, listener, tag);
	}
}
