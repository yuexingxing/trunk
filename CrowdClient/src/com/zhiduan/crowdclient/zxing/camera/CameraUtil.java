package com.zhiduan.crowdclient.zxing.camera;

import com.zhiduan.crowdclient.MyApplication;
import com.zhiduan.crowdclient.util.CommandTools;

/** 
 * 自定义条形码、二维码扫描区域
 * 
 * @author yxx
 *
 * @date 2016-7-15 上午9:42:54
 * 
 */
public class CameraUtil {

	public static final int marginTop = CommandTools.dip2px(MyApplication.getInstance(), 20);//顶部距离
	public static final int marginLeft = CommandTools.dip2px(MyApplication.getInstance(), 20);//左侧距离
	public static final int marginRight = CommandTools.dip2px(MyApplication.getInstance(), 20);//右侧距离
	public static final int cameraHeight = CommandTools.dip2px(MyApplication.getInstance(), 100);//扫描高度
}
