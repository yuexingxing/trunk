package com.zhiduan.crowdclient.dao;

import java.util.ArrayList;
import java.util.List;

import com.zhiduan.crowdclient.data.OrderInfo;
import com.zhiduan.crowdclient.db.DBHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/** 
 * 订单信息dao
 * 
 * @author yxx
 *
 * @date 2016-2-24 上午10:46:07
 * 
 */
public class OrderDao {

	public OrderDao(){

	}

	private SQLiteDatabase db = null;

	/**
	 * 单条插入
	 * @param table
	 * @param info
	 * @return
	 */
	public boolean addData(OrderInfo info) {

		boolean flag = false;
		try {
			db = DBHelper.SQLiteDBHelper.getWritableDatabase();
			db.beginTransaction();

			ContentValues cv = new ContentValues();

			cv.put(DBHelper.ORDER_ARRIVE_FEE, info.getArriveFee());
			cv.put(DBHelper.ORDER_CACHE_ID, info.getCacheid());
			cv.put(DBHelper.ORDER_BILLCODE, info.getOrderNo());
			
			cv.put(DBHelper.ORDER_STATUS, info.getOrderStatus());

			db.setTransactionSuccessful();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			db.endTransaction();
		}
		return flag;
	}

	/**
	 * 设置快递公司的常用与不常用
	 * @param info(0、1)不常用、常用
	 * @param n
	 */
	public boolean updateExpressUse(OrderInfo info, int n){

		boolean flag = false;
		//		String sql = "update " + DBHelper.TABLE_ORDER + " set express_use = '" + n + "'"
		//				+" where express_name = '" + info.getName() + "'";
		//		try{
		//			db = DBHelper.SQLiteDBHelper.getWritableDatabase();
		//			db.beginTransaction();
		//			db.execSQL(sql);
		//			db.setTransactionSuccessful();
		//			flag = true;
		//		}catch(Exception e){
		//			e.printStackTrace();
		//		}finally{
		//			db.endTransaction();
		//		}

		return flag;
	}

	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public int selectData(String id) {

		int count = -1;
		Cursor cursor = null;
		String sql = "select * from " + DBHelper.TABLE_ORDER + " where express_id = '" + id + "'";
		try{

			db = DBHelper.SQLiteDBHelper.getWritableDatabase();
			cursor = db.rawQuery(sql, null);
			count = cursor.getCount();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}

		return count;
	}

	//根据快递公司编号获取快递公司的url
	public String getSelectURL(String expressCode) {
		
		db = DBHelper.SQLiteDBHelper.getWritableDatabase();
		
		String sql = "select * from " + DBHelper.TABLE_ORDER
				+ " where express_code = '" + expressCode + "'";
		Cursor cursor = db.rawQuery(sql, null);
		String imagerUrl = "";
		while (cursor.moveToNext()) {
			imagerUrl = cursor.getString(cursor.getColumnIndex("express_url"));
		}

		if(cursor != null){
			cursor.close();
		}

		return imagerUrl;
	}


	/**
	 * 查询所有快递公司
	 * @param n(0、1、2)所有、常用、不常用
	 * @return
	 */
	public List<OrderInfo> selectDataList(int n) {

		List<OrderInfo> list = new ArrayList<OrderInfo>();
		db = DBHelper.SQLiteDBHelper.getWritableDatabase();
		String sql = "select * from " + DBHelper.TABLE_ORDER + " order by express_use DESC";

		Cursor cursor = db.rawQuery(sql, null);

		while (cursor.moveToNext()) {
			//			OrderInfo info = new OrderInfo();
			//			info.setId(cursor.getString(cursor.getColumnIndex("express_id")));
			//			info.setName(cursor.getString(cursor.getColumnIndex("express_name")));
			//			info.setCode(cursor.getString(cursor.getColumnIndex("express_code")));
			//			info.setUrl(cursor.getString(cursor.getColumnIndex("express_url")));
			//			info.setUse(cursor.getString(cursor.getColumnIndex("express_use")));
			//			info.setGCode(cursor.getString(cursor.getColumnIndex("express_gcode")));
			//			info.setModifyTime(cursor.getString(cursor.getColumnIndex("modify_time")));
			//			list.add(info);
		}

		if(cursor != null){
			cursor.close();
		}
		return list;
	}

	/**
	 * 根据常用不常用
	 * 获取快递公司列表
	 * @param n
	 * @return
	 */
	public List<OrderInfo> selectExpressByUse(int n) {

		List<OrderInfo> list = new ArrayList<OrderInfo>();
		db = DBHelper.SQLiteDBHelper.getWritableDatabase();
		String sql = "select * from " + DBHelper.TABLE_ORDER + " where express_use = '" + n + "'" + " order by express_use DESC";

		Cursor cursor = db.rawQuery(sql, null);

		while (cursor.moveToNext()) {
			//			OrderInfo info = new OrderInfo();
			//			info.setId(cursor.getString(cursor.getColumnIndex("express_id")));
			//			info.setName(cursor.getString(cursor.getColumnIndex("express_name")));
			//			info.setCode(cursor.getString(cursor.getColumnIndex("express_code")));
			//			info.setUrl(cursor.getString(cursor.getColumnIndex("express_url")));
			//			info.setUse(cursor.getString(cursor.getColumnIndex("express_use")));
			//			info.setGCode(cursor.getString(cursor.getColumnIndex("express_gcode")));
			//			info.setModifyTime(cursor.getString(cursor.getColumnIndex("modify_time")));
			//			list.add(info);
		}
		if(cursor != null){
			cursor.close();
		}
		return list;
	}

	/**
	 * 获取当前最大的修改时间
	 * @return
	 */
	public String getMaxTime(){

		String strMaxTime = "1979-01-01 00:00:00";
		db = DBHelper.SQLiteDBHelper.getWritableDatabase();

		String sql = "select * from " + DBHelper.TABLE_ORDER + " order by modify_time DESC";

		Cursor cursor = null;
		try{

			cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToNext()) {

				strMaxTime = cursor.getString(cursor.getColumnIndex("modify_time"));
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}

		return strMaxTime;
	}

	/**
	 * 删除数据
	 * @param info
	 */
	public void deleteById(OrderInfo info){

		//		String sql = " delete from " + DBHelper.TABLE_ORDER
		//				+ " where " 
		//				+ DBHelper.KEY_EXPRESS_ID + " = '" + info.getId() + "'";
		//
		//		db = DBHelper.SQLiteDBHelper.getWritableDatabase();
		//		try{
		//			db.execSQL(sql);
		//		}catch(Exception e){
		//			e.printStackTrace();
		//		}	
	}

	/**
	 * 更新快递公司信息
	 * @param info
	 */
	public void updateData(OrderInfo info){

		//		String sql = " UPDATE " + DBHelper.TABLE_ORDER 
		//				+ " SET "
		//				+ DBHelper.KEY_EXPRESS_CODE +  " = '" + info.getCode() + "'" + ","
		//				+ DBHelper.KEY_EXPRESS_NAME +  " = '" + info.getName() + "'" + ","
		//				+ DBHelper.KEY_EXPRESS_URL +  " = '" + info.getUrl() + "'" + ","
		//				+ DBHelper.EXPRESS_MODIFY_TIME +  " = '" + info.getModifyTime() + "'" + ","
		//				+ DBHelper.KEY_EXPRESS_GCODE +  " = '" + info.getGCode() + "'"
		//				+ " where " + DBHelper.KEY_EXPRESS_ID + " = '" + info.getId() + "'";
		//
		//		db = DBHelper.SQLiteDBHelper.getWritableDatabase();
		//		try{
		//			db.execSQL(sql);
		//		}catch(Exception e){
		//			e.printStackTrace();
		//		}	

	}

	/**
	 * 根据类型排序(时间、距离)
	 * @param id
	 * @return
	 */
	public List<OrderInfo> selectDataByType(int type) {

		List<OrderInfo> list = new ArrayList<OrderInfo>();

		db = DBHelper.SQLiteDBHelper.getWritableDatabase();
		String sql;
		if(type == 0){
			sql = "select * from " + DBHelper.TABLE_ORDER + " order by " + DBHelper.ORDER_DISTANCE + " ASC";
		}else{
			sql = "select * from " + DBHelper.TABLE_ORDER + " order by " + DBHelper.ORDER_DIST_TIME + " ASC";
		}

		Cursor cursor = db.rawQuery(sql, null);

		while(cursor.moveToNext()) {

			OrderInfo info = new OrderInfo();
			info.setCacheid(cursor.getString(cursor.getColumnIndex(DBHelper.ORDER_CACHE_ID)));

			list.add(info);
		}

		if(cursor != null){
			cursor.close();
		}

		return list;
	}

	/**
	 * 清空表
	 */
	public void clearTable(){

		db = DBHelper.SQLiteDBHelper.getWritableDatabase();

		String sql = "delete from " + DBHelper.TABLE_ORDER;
		db.execSQL(sql);
	}
}
