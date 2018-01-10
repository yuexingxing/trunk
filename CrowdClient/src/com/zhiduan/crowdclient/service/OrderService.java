package com.zhiduan.crowdclient.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.net.AsyncNetTask.OnPostExecuteListener;
import com.zhiduan.crowdclient.net.LoadTextNetTask;
import com.zhiduan.crowdclient.net.NetTaskUtil;
import com.zhiduan.crowdclient.net.NetUtil;
import com.zhiduan.crowdclient.util.Constant;

/**
 * 
 * @author HeXiuHui
 * 
 */
public class OrderService {

	/** 查询任务列表接口 **/
	public static LoadTextNetTask getOrdertask(String sortBy, String orderBy, int pageNumber, String sex, boolean lookFloor, int number, OnPostExecuteListener listener, Object tag) {
		String url = "order/pool/list.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("pageNumber", pageNumber); //
			if (!"".equals(orderBy)) {
				object.put("orderBy", orderBy);
				object.put("sortBy", sortBy);
			}
			if (!"".equals(sex)) {
				object.put("sex", sex);
			}
			if (lookFloor) {
				object.put("lookFloor", lookFloor);
			}
			if (number != 0) {
				object.put("number", number);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean isToken = NetUtil.NOT_TOKEN;
		if (!"".equals(MyApplication.getInstance().m_userInfo.toKen) && MyApplication.getInstance().m_userInfo.toKen != null) {
			isToken = NetUtil.TOKEN;
		}

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), isToken, listener, tag);
	}
	
	/** 抢单 **/
	public static LoadTextNetTask grabOrder(String orderId, String type, OnPostExecuteListener listener, Object tag) {
		String url = "taker/grab.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("orderId", orderId);
//			object.put("type", type);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
	/** 获取订单详情 **/
	public static LoadTextNetTask getOrderDetail(String orderId, OnPostExecuteListener listener, Object tag) {

		String url = "order/packet/getorderdetail.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("orderId", orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}
	
	/** 获取跑腿订单详情 **/
	public static LoadTextNetTask getErrandsDetail(String orderId, OnPostExecuteListener listener, Object tag) {

		String url = "order/errands/getorderdetail.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("orderId", orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
	/** 获取转单列表详情**/
	public static LoadTextNetTask getDeliveryDetail(String orderId, OnPostExecuteListener listener, Object tag) {
		String url = "packet/assign/detail.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("orderId", orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
	/**拒绝转单**/
	public static LoadTextNetTask refuseDelivery(String assignId, OnPostExecuteListener listener, Object tag) {
		String url = "packet/assign/refuse.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("assignId", assignId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
	/**同意转单**/
	public static LoadTextNetTask agreeDelivery(String assignId, OnPostExecuteListener listener, Object tag) {
		String url = "packet/assign/agree.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("assignId", assignId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
	public static final int ADTYPE_INDEX = 1;  //首页广告
	public static final int ADTYPE_DIALOG = 2; //弹窗广告
	
	/**获取广告图**/
	public static LoadTextNetTask getIndexAdv(int adtype, OnPostExecuteListener listener, Object tag) {
		String url = "manage/ad/list.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("adType", adtype);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}
	
	/** 查询经验加成接口 **/
	public static LoadTextNetTask getExperience(int categoryId, String ruleCode, int pageNumber, OnPostExecuteListener listener, Object tag) {
		String url = "order/pool/list.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("pageNumber", pageNumber); //
			if (categoryId != 0) {
				object.put("categoryId", categoryId); //
			}
			
			if (!ruleCode.equals("")) {
				object.put("ruleCode", ruleCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean isToken = NetUtil.NOT_TOKEN;
		if (!"".equals(MyApplication.getInstance().m_userInfo.toKen) && MyApplication.getInstance().m_userInfo.toKen != null) {
			isToken = NetUtil.TOKEN;
		}

		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), isToken, listener, tag);
	}
	
	/** 判断用户所在学校是否开启经验/悬赏加成**/
	public static LoadTextNetTask getExperienceType(OnPostExecuteListener listener, Object tag) {
		String url = "user/ifplusopen.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("token", MyApplication.getInstance().m_userInfo.toKen); //
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
//	/**获取第一次启动广告图**/
//	public static LoadTextNetTask getDialogAdv(int adtype, OnPostExecuteListener listener, Object tag) {
//		String url = "manage/ad/list.htm";
//
//		JSONObject object = new JSONObject();
//		try {
//			object.put("adType", adtype);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
//	}
}
