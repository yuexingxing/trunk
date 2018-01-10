package com.zhiduan.crowdclient.service;

import java.io.File;

import org.json.JSONObject;

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
public class TaskService {

	/** 获取风云榜**/
	public static LoadTextNetTask getPeak(OnPostExecuteListener listener, Object tag) {

		String url = "user/packet/querytopincome.htm";

		JSONObject object = new JSONObject();
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
	/** 获取进行中任务列表**/
	public static LoadTextNetTask getUnderWayTask(String beginTime,String endTime,int pageNumber,OnPostExecuteListener listener, Object tag) {

		String url = "order/systask/querygoing.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("beginTime", beginTime);
			object.put("endTime", endTime);
			object.put("pageNumber", pageNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
	/** 获取审核中任务列表**/
	public static LoadTextNetTask getAuditTask(String beginTime,String endTime,int pageNumber,OnPostExecuteListener listener, Object tag) {

		String url = "order/systask/queryauditing.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("beginTime", beginTime);
			object.put("endTime", endTime);
			object.put("pageNumber", pageNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
	/** 获取已结算任务列表**/
	public static LoadTextNetTask getPaymentTask(String beginTime, String endTime, int pageNumber, OnPostExecuteListener listener, Object tag) {
		String url = "order/systask/querypaid.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("beginTime", beginTime);
			object.put("endTime", endTime);
			object.put("pageNumber", pageNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
	/** 获取已取消任务列表**/
	public static LoadTextNetTask getCancelTask(String beginTime, String endTime, int pageNumber, OnPostExecuteListener listener, Object tag) {
		String url = "order/systask/querycanceled.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("beginTime", beginTime);
			object.put("endTime", endTime);
			object.put("pageNumber", pageNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
	/** 提交审核系统任务**/
	public static LoadTextNetTask SubmitTask(String orderid,OnPostExecuteListener listener, Object tag) {

		String url = "order/systask/submitaudit.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("orderId", orderid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
	/** 取消任务**/
	public static LoadTextNetTask CancelTask(String orderid,OnPostExecuteListener listener, Object tag) {

		String url = "order/systask/cancel.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("orderId", orderid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
	/** 查看任务详情**/
	public static LoadTextNetTask QueryTaskDetail(String orderid,OnPostExecuteListener listener, Object tag) {

		String url = "order/systask/querydetail.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("orderId", orderid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	/** 查看首页任务详情**/
	public static LoadTextNetTask QueryMianTaskDetail(String orderid,OnPostExecuteListener listener, Object tag) {

		String url = "order/systask/querygrabdetail.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("orderId", orderid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}
	/** 上传任务照片**/
	public static LoadTextNetTask UploadTaskImg(String file, String filename, String orderid, OnPostExecuteListener listener, Object tag) {
		String url = "order/systask/uploadimage.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("file", file);
			object.put("fileName", filename);
			object.put("orderId", orderid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
	/** 删除任务照片**/
	public static LoadTextNetTask DeleteTaskImg(int imgid,OnPostExecuteListener listener, Object tag) {

		String url = "order/systask/deleteimage.htm";

		JSONObject object = new JSONObject();
		try {
			object.put("id", imgid);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NetTaskUtil.makeTextNetTask(Constant.FormalURL + url, object.toString(), NetUtil.TOKEN, listener, tag);
	}
	
	
}
