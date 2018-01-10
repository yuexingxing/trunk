package com.zhiduan.crowdclient.net;

import java.io.File;

import android.os.Environment;

/**
 * @author HeXiuHui
 *
 */
public class DirSettings
{
	private static final String APP_BASE_DIR = "AiXuePai/"; 		// 在SD卡上存放文件的主目录
	private static final String APP_CACHE_DIR = "Cache/"; 			// 从服务器下载的图片存放的子目录	
	private static final String APP_CAMERA_DIR = "Photo/"; 	// 拍照存放的子目录	
	private static final String APP_AUDOI_DIR = "Audio/";

	private DirSettings()
	{

	}

	public static String getAppDir()
	{
		return getSDCardPath() + APP_BASE_DIR;
	}

	public static String getAppCacheDir()
	{
		return getAppDir() + APP_CACHE_DIR;
	}

	public static String getAppAudioDir()
	{
		return getAppDir() + APP_AUDOI_DIR;
	}
	
	public static String getAppCameraDir()
	{
		return getAppDir() + APP_CAMERA_DIR;
	}

	public static String getSDCardPath()
	{
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			File dir = Environment.getExternalStorageDirectory();
			return dir.getPath() + "/";
		}
		return "/mnt/sdcard/";
	}
}
