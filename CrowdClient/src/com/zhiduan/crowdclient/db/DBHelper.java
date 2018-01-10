package com.zhiduan.crowdclient.db;

import com.zhiduan.crowdclient.util.Logs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** 
 * 
 * 
 * @author yxx
 *
 * @date 2016-5-28 下午2:55:05
 * 
 */
public final class DBHelper {
	
	public static final int DATABASE_VERSION = 1;// 数据库版本号

	public static SQLiteDBOpenHelper SQLiteDBHelper;
	public static final String DATABASE_NAME = "ZDCrowd.db";// 数据库名

	public static final String TABLE_ORDER = "ORDERDATA";//order表

	/**
	 * 数据库版本号
	 * 每次修改表中字段时，该版本号自动+1
	 * 避免PDA上程序卸载重装
	 * 修改后表被删除(包括表中数据)，创建一个新表
	 * */

	/**
	 * 数据扫描表
	 */
	public static final String ORDER_CACHE_ID = "cache_id";//唯一标识
	public static final String ORDER_BILLCODE = "billcode";//订单号

	public static final String ORDER_DIST_MAN = "dist_man";//派件人
	public static final String ORDER_DIST_PHONE = "dist_phone";//派件人联系方式
	public static final String ORDER_DIST_ADDRESS = "dist_address";//派件地址
	public static final String ORDER_DIST_TIME = "dist_time";//派件时间
	public static final String ORDER_DIST_FEE = "dist_fee";//派件小费

	public static final String ORDER_REC_MAN = "rec_man";//收件人
	public static final String ORDER_REC_PHONE = "rec_phone";//收件人联系方式
	public static final String ORDER_REC_ADDRESS = "rec_address";//收件地址
	public static final String ORDER_REC_TIME = "rec_time";//收件地址

	public static final String ORDER_GRAB_TIME = "grab_time";//抢单时间
	public static final String ORDER_ARRIVE_FEE = "arrive_fee";//到付金额

	public static final String ORDER_STACK = "stack";//货位号
	public static final String ORDER_DISTANCE = "distance";//距离
	public static final String ORDER_REMARK = "remark";//说明
	
	public static final String ORDER_STATUS = "status";//订单状态

	public static final String CREATE_ORDER_TABLE = "create table "
			+ TABLE_ORDER + " (" 
			+ ORDER_CACHE_ID + "  nvarchar(50) null default(''), "
			+ ORDER_BILLCODE + "  nvarchar(50) null default(''), "
			+ ORDER_DIST_MAN + "  nvarchar(50) null default(''), "
			+ ORDER_DIST_PHONE + "  nvarchar(50) null default(''), "
			+ ORDER_DIST_ADDRESS + "  nvarchar(50) null default(''), "
			+ ORDER_DIST_TIME + "  nvarchar(50) null default(''), "
			+ ORDER_DIST_FEE + "  nvarchar(50) null default(''), "
			+ ORDER_REC_MAN + "  nvarchar(50) null default(''), "
			+ ORDER_REC_PHONE + "  nvarchar(50) null default(''), "
			+ ORDER_REC_ADDRESS + "  nvarchar(50) null default(''), "
			+ ORDER_REC_TIME + "  nvarchar(50) null default(''), "
			+ ORDER_GRAB_TIME + "  nvarchar(50) null default(''), "
			+ ORDER_ARRIVE_FEE + "  nvarchar(50) null default(''), "
			+ ORDER_STACK + "  nvarchar(50) null default(''), "
			+ ORDER_DISTANCE + "  nvarchar(50) null default(''), "
			+ ORDER_STATUS + "  nvarchar(50) null default(''), "
			+ ORDER_REMARK + " nvarchar(50) null default(''));";

	public DBHelper(Context context) {
		SQLiteDBHelper = new SQLiteDBOpenHelper(context);

	}

	public class SQLiteDBOpenHelper extends SQLiteOpenHelper {

		public SQLiteDBOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			Logs.v("upload", "onCreate");

			db.execSQL(CREATE_ORDER_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			Logs.v("upload", "onUpgrade");
			try {

				//快递公司表增加--修改时间字段
				//				checkColumnExist(db, DBHelper.EXPRESS_TABLE, DBHelper.EXPRESS_MODIFY_TIME);
				//				db.execSQL("DROP TABLE IF EXISTS " + SCANDATA_TABLE);
				onCreate(db);

				if(oldVersion != newVersion) {
					Logs.v("zd", "数据库升级成功！");
				} else {
					Logs.v("zd", "数据库升级失败！");
				}

			} catch(Exception e) {
				e.printStackTrace();
				Logs.v("zd", "数据库升级失败！");
			}
		}

	}

	/**
	 * 方法1：检查某表列是否存在
	 * @param db
	 * @param tableName 表名
	 * @param columnName 列名
	 * @return
	 */
	private void checkColumnExist(SQLiteDatabase db, String tableName, String columnName) {

		boolean result = false ;
		Cursor cursor = null ;
		try{
			cursor = db.rawQuery( "SELECT * FROM " + tableName + " LIMIT 0", null );
			result = cursor != null && cursor.getColumnIndex(columnName) != -1 ;
			Logs.v("sql", "快递公司表已存在该字段");
		}catch (Exception e){
			Logs.e("sql", e.getMessage()) ;
		}finally{
			if(null != cursor && !cursor.isClosed()){
				cursor.close() ;
			}
		}

		if(result == false){
			addColumn(db, tableName, columnName);
		}
	}

	/**
	 * 数据库升级
	 * 表增加字段
	 * @param db
	 * @param tableName
	 * @param columnName
	 */
	private void addColumn(SQLiteDatabase db, String tableName, String columnName){

		try{

			String sql = "Alter Table " + tableName + " ADD COLUMN " + columnName;
			db.execSQL(sql);
			Logs.v("sql", "表增加字段成功");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
