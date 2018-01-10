package com.zhiduan.crowdclient.util;

import java.io.File;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.utils.StorageUtils;
import com.zhiduan.crowdclient.MyApplication;

/**
 * 全局变量
 * 
 * @author yxx
 * 
 * @date 2016-5-25 上午11:42:46
 * 
 */
public class Constant {
	public static int PHOTO_INDEX = 0;// 记录当前的位置

	public static String SmsURL = "http://axd.shzhiduan.com:9650/Campusex/api/";// 短信url
	public static File cacheDir = StorageUtils.getOwnCacheDirectory(MyApplication.getInstance(), "AiXuePai/IconCache/");

	// 测试库--【1】
	public static String DEV_TagName = "Android_ZB_测试版_";
	public static String DEV_URL = "https://apidev.axpapp.com/axp-acl/"; // 测试库--外网地址
	//	public static String DEV_URL = "http://218.94.60.50:58080/axp-acl/";
	public static String DEV_QRCodePayURL = "https://dev.axpapp.com/axp/cashier/topay.htm?bizId=";// 测试版扫码支付， +订单号
	public static String DEV_UPDATEURL = "http://update.axpapp.com/appUpdate/checkNew?os=android&type=crowd&tag=dev&ver=";// 测试版本APP在线升级
	public static String DEV_AppKey = "56880eac0528297a62db334ea9516410";// 测试库APPKEY
	public static String DEV_InviteURL="http://dev.axpapp.com/receiveredpacket?spreadPhone=";//APP分享的链接
	public static String DEV_RULE_URL="http://dev.axpapp.com/activerule";//活动规则页面地址
	public static String DEV_QA_URL="http://dev.axpapp.com/newhelp";//QA页面地址
	public static String DEV_WEBVIEW_H5_URL = "http://dev.axpapp.com/index?";
	public static String DEV_PAY_APPID = "wx4e80aa722c6f00eb";//微信支付商户APPID
	public static String DEV_ABOUT_AXP = "http://dev.axpapp.com/apphelp?apper=";//关于爱学派

	// 预发布--【2】
	public static String PRE_TagName = "Android_ZB_预发布_";
	public static String PRE_URL = "http://apipre.axpapp.com/axp-acl/"; // 预发布
	public static String PRE_UPDATEURL = "http://update.axpapp.com/appUpdate/checkNew?os=android&type=crowd&tag=pre&ver=";// 测试版本APP在线升级
	public static String PRE_AppKey = "56880eac0528297a62db334ea9516410";// 测试库APPKEY
	public static String PRE_InviteURL="http://pre.axpapp.com/receiveredpacket?spreadPhone=";//APP分享的链接
	public static String PRE_RULE_URL="http://pre.axpapp.com/activerule";//活动规则页面地址
	public static String PRE_QA_URL="http://pre.axpapp.com/newhelp";//QA页面地址
	public static String PRE_WEBVIEW_H5_URL = "http://pre.axpapp.com/index?";
	public static String PRE_PAY_APPID = "wx4e80aa722c6f00eb";//微信支付商户APPID
	public static String PRE_ABOUT_AXP = "http://pre.axpapp.com/apphelp?apper=";//关于爱学派

	// 正式库--【3】
	public static String FORMAL_TagName = "Android_ZB_正式版_";
	public static String FORMAL_URL = "http://api.axpapp.com/axp-acl/"; // 正式库
	public static String FORMAL_UPDATEURL = "http://update.axpapp.com/appUpdate/checkNew?os=android&type=crowd&tag=normal&ver=";
	public static String FORMAL_AppKey = "t6880eac0528w97a62db334ea9v16g10";// 正式 APPKEY
	public static String FORMAL_InviteURL="http://axpapp.com/receiveredpacket?spreadPhone=";//APP分享的链接
	public static String FORMAL_RULE_URL="http://axpapp.com/activerule";//活动规则页面地址
	public static String FORMAL_QA_URL="http://axpapp.com/newhelp";//QA页面地址
	public static String FORMAL_WEBVIEW_H5_URL = "http://axpapp.com/index?";
	public static String FORMAL_PAY_APPID = "wxacac0cf7d181974c";//微信支付商户APPID--正式库
	public static String FORMAL_ABOUT_AXP = "http://axpapp.com/apphelp?apper=";//关于爱学派

	public static String TagName = FORMAL_TagName;
	public static String FormalURL = FORMAL_URL;
	public static String UPDATEURL = FORMAL_UPDATEURL;
	public static String appKey = FORMAL_AppKey;
	public static String INVITE_URL = FORMAL_InviteURL;
	public static String RULE_URL = FORMAL_RULE_URL;
	public static String QA_URL = FORMAL_QA_URL;
	public static String PAY_APPID = FORMAL_PAY_APPID;
	public static String ABOUT_AXP = FORMAL_ABOUT_AXP;
	public static String WEBVIEW_H5_URL = FORMAL_WEBVIEW_H5_URL;


	/******************************** 接口目录 ***********************************************/
	//收件人实名认证
	public static String Certification_url = "customeruser/certification.htm";
	// 用户登录
	public static String Login_url = "user/commonlogin.htm";
	// 退出登录
	public static String Exit_url = "user/loginout.htm";
	// 用户注册
	public static String register_url = "user/register.htm";// 注册接口和货源版一致
	// 修改登录密码
	public static String updatepwd_url = "user/changepwd.htm";
	// 修改性别
	public static String updatesex_url = "user/packet/changegender.htm";
	// 修改学校
	public static String updateschool_url = "user/packet/perfectcollegeinfo.htm";
	// 获取所有学校列表
	public static String getSchoolList_url = "COLLEGE/Post_GetCollegeList";
	// 获取验证码
	public static String getVerCode_url = "user/code/send.htm";
	// 校验验证码
	public static String validateCode_url = "user/code/validate.htm";
	// 找回密码
	public static String findpwd_url = "user/resetpwd.htm";
	// 用户上传头像
	public static String upload_userpic_url = "user/packet/uploadicon.htm";
	// 获取学校信息
	public static String getSchoolData = "user/packet/selectperfectinfo.htm";
	// 查询城市
	public static String getCityData = "manage/city/all.htm";
	// 提交学校信息
	public static String CommitSchoolData_url = "user/packet/perfectinfo.htm";
	// 提交学校,姓名，性别 信息
	public static String CommitNameSchool_url = "user/update/someuserinfo.htm";
	//完善用户名学校信息
	public static String PerfectSchoolData_url = "user/packet/perfectcollegeinfo.htm";
	//修改用户名
	public static String UpdateNickName_url = "user/update/nickname.htm";
	// 用户提交实名认证
	public static String CommitCertification = "user/packet/certification.htm";
	// 查询审核信息
	public static String SelectVerifyInfo_url = "user/packet/selectverifyinfo.htm";
	// 实名认证上传照片
	public static String Upload_AuthPhoto_url = "user/packet/uploadcredentials.htm";
	// 获取个人信息
	public static String getUserInfo_url = "user/packet/getuser.htm";
	//获取邀请人列表
	public static String getInvitePeople_url="user/invite/userlist.htm";
	//获取邀请收入列表
	public static String getInviteIncome_url="user/invite/incomelist.htm";
	//获取分享链接的配置信息
	public static String getInviteInfo="user/invite/getuserinviteinfo.htm";
	//获取完善信息界面的奖励规则
	public static String getRewardDetail="activity/promotionawardrule/getdetail.htm";
	public static String WEIXIN = "http://weixin.aixuepai.com.cn/axp-wx/index.jsp";// 绑定微信

	/**
	 * 这里是存储配置文件的key值，不许修改 SharedPreferencesUtils
	 */
	public static final String SP_LOGIN_NAME = "login_name";// 配置文件存储登录人姓名
	public static final String SP_LOGIN_PSD = "login_psd";// 配置文件存储登录人密码
	public static final String SP_LOGIN_TOKEN = "login_token";  // 登录token

	public static final String SP_DELIVERY_ORDER_SORT = "delivery_order_sort";// 订单数据排序
	public static final String SP_DELIVERY_SORT_TYPE = "delivery_sort_type";// 排序类型，0:距离、1:时间
	public static final String SP_DELIVERY_ORDER_TYPE = "delivery_order_type";// 排序类型

	public static final String SP_POST_ORDER_SORT = "post_order_sort";// 订单数据排序
	public static final String SP_POST_SORT_TYPE = "post_sort_type";// 排序类型，0:距离、1:时间
	public static final String SP_POST_ORDER_TYPE = "post_order_type";// 排序类型

	public static final String SP_TagName = "TagName";
	public static final String SP_FormalURL = "FormalURL";
	public static final String SP_QRCodePayURL = "QRCodePayURL";
	public static final String SP_UPDATEURL = "UPDATEURL";
	public static final String SP_AppKey = "appkey";

	public static final String SP_DELETE_FILE_DAY = "delete_day";// 上一次删除文件日期

	// 工作状态
	public static final String USER_STATE_REST = "0"; // 休息中
	public static final String USER_STATE_WORK = "1"; // 工作中
	public static boolean is_h5=false;
	// 推送类型
	public static final String PUSH_TYPE_LOGIN = "101"; // 登录踢出
	public static final String PUSH_TYPE_DISTRIBUTE_SINGLE = "104"; // 定向派单
	public static final String PUSH_TYPE_PAY = "201"; // 支付结果推送
	public static final String PUSH_TYPE_NEW_ORDER = "301";// 众包订单生成
	public static final String PUSH_TYPE_DELIVERY = "302";// 转单通知
	public static final String PUSH_TYPE_ACCEPT = "303";// 转单被接受通知
	public static final String PUSH_TYPE_REFUSE = "304";// 转单被拒绝通知
	public static final String PUSH_TYPE_OVERTIME = "305";// 转单超时通知
	public static final String PUSH_TYPE_ZD_STATE_SUCCESS = "401"; // 众包审核状态 成功
	public static final String PUSH_TYPE_ZD_STATE_FAIL = "402"; // 众包审核状态 失败
	public static final String PUSH_TYPE_HY_STATE = "501"; // 货源审核状态
	public static final String PUSH_TYPE_ASSIGNED = "503"; // 货源分单通知
	public static final String PUSH_TYPE_ZD_UPDATE = "601"; // 程序更新

	public static final String PUSH_MSG_AUDIT_TYPE = "1204"; //任务订单审核通过
	public static final String PUSH_MSG_TASK_CANCEL_TYPE = "1312"; //任务超时系统取消通知
	public static final String PUSH_MSG_TASK_USER_CANCEL_TYPE = "1313"; //用户取消任务通知
	public static final String PUSH_MSG_ORDER_TIMEOUT_TYPE_1 = "1315";   //已接未取，订单超时推送
	public static final String PUSH_MSG_ORDER_TIMEOUT_TYPE_2 = "1316";   //已接，已取，未派送，订单超时推送
	public static final String PUSH_MSG_SEND_TIMEOUT_TYPE = "1317";   //寄件订单超时未取件通知
	public static final String PUSH_MSG_ORDER_CANCEL_TYPE = "1318";   //用户取消订单(寄件，派件，代取)
	public static final String PUSH_MSG_SERVER_CANCEL_TYPE = "1319";   //管理端取消订单
	public static final String PUSH_MSG_SERVER_DISTRIBUTE_TYPE = "1320";   //管理端派单
	public static final String PUSH_MSG_SERVER_CANCEL_TYPE_2 = "1321";   //管理端派单 (已接单，未取件)
	public static final String PUSH_MSG_SERVER_ING = "1504";   //提交审核
	public static final String PUSH_MSG_NEW_VOUCHER = "5502";   //新用户代金卷

	// 任务状态常量
	public static final int TASK_MAIN_DETAIL = 0;
	public static final int TASK_UNDER_WAY_DETAIL = 1;
	public static final int TASK_AUDIT_DETAIL = 2;
	public static final int TASK_FINISH_DETAIL = 3;
	public static final int TASK_CANCEL_DETAIL = 4;
	// 审核结果
	public static final int REVIEW_STATE_NOT_SUBMIT = 0; // 未提交
	public static final int REVIEW_STATE_SUBMITING = 1; // 审核中
	public static final int REVIEW_STATE_SUCCESS = 2; // 审核成功
	public static final int REVIEW_STATE_FAIL = 3; // 审核失败
	public static final int REVIEW_STATE_USER_SUCCESS = 4; // 用户审核成功

	// 订单类型
	public static final String TYPE_PACKET = "packet";// 取件
	public static final String TYPE_AGENT_PACKET = "agent_packet";// 待取件
	public static final String TYPE_SEND = "send";// 寄件
	public static final String TYPE_AGENT_SYSTEM = "agent_system";// 系统任务
	public static final String TYPE_ERRANDS = "errands";   //跑腿

	public static final String PUSH_ASSIGNED_MESSAGE = "push_message"; // 保存新转单消息
	public static final String SAVE_NOT_ORDER_NUMBER = "not_number";   //保存未读订单
	public static final String SAVE_SHOW_DIALOG_TIME = "show_dialog_time";   //保存一天弹窗一次参数

	public static Bitmap mBitmap;

	/**
	 * 初始化服务器及其他地址
	 * type 1-测试库 2-pre 3-正式库
	 *  默认为正式库
	 * @param type
	 */
	public static void initURL(int type){

		if(type == 1){

			TagName = DEV_TagName;
			FormalURL = DEV_URL;
			UPDATEURL = DEV_UPDATEURL;
			appKey = DEV_AppKey;
			INVITE_URL = DEV_InviteURL;
			RULE_URL = DEV_RULE_URL;
			QA_URL = DEV_QA_URL;
			PAY_APPID = DEV_PAY_APPID;
			ABOUT_AXP = DEV_ABOUT_AXP;
			WEBVIEW_H5_URL = DEV_WEBVIEW_H5_URL;
		}else if(type == 2){

			TagName = PRE_TagName;
			FormalURL = PRE_URL;
			UPDATEURL = PRE_UPDATEURL;
			appKey = PRE_AppKey;
			INVITE_URL = PRE_InviteURL;
			RULE_URL = PRE_RULE_URL;
			QA_URL = PRE_QA_URL;
			PAY_APPID = PRE_PAY_APPID;
			ABOUT_AXP = PRE_ABOUT_AXP;
			WEBVIEW_H5_URL = PRE_WEBVIEW_H5_URL;
		}else if(type == 3){

			TagName = FORMAL_TagName;
			FormalURL = FORMAL_URL;
			UPDATEURL = FORMAL_UPDATEURL;
			appKey = FORMAL_AppKey;
			INVITE_URL = FORMAL_InviteURL;
			RULE_URL = FORMAL_RULE_URL;
			QA_URL = FORMAL_QA_URL;
			PAY_APPID = FORMAL_PAY_APPID;//微信支付商户号
			ABOUT_AXP = FORMAL_ABOUT_AXP;
			WEBVIEW_H5_URL = FORMAL_WEBVIEW_H5_URL;
		}
	}
}
