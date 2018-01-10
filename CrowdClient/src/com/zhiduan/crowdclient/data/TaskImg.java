package com.zhiduan.crowdclient.data;

import java.io.File;
import java.io.Serializable;

import android.graphics.Bitmap;

/**  
 * <pre>
 * Description	任务拍照图片
 * Copyright:	Copyright (c)2016 
 * Company:		Z D technology
 * Author:		Huang hua
 * Version:		1.0  
 * Created at:	2016-8-19 下午4:55:52  
 * </pre>
 */

public class TaskImg implements Serializable{

	private static final long serialVersionUID = 1L;
	private Bitmap taskBitmap;
	private String taskimg_path;
	private int id;//图片ID
	private int ispass;//图片是否符合要求
	private String normalUrl;//	正常图片
	private String thumbnailUrl;//	缩略图片
	
	
	/**
	 * @return the {@link #id}
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the {@link #id} to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the {@link #ispass}
	 */
	public int getIspass() {
		return ispass;
	}
	/**
	 * @param ispass the {@link #ispass} to set
	 */
	public void setIspass(int ispass) {
		this.ispass = ispass;
	}
	/**
	 * @return the {@link #normalUrl}
	 */
	public String getNormalUrl() {
		return normalUrl;
	}
	/**
	 * @param normalUrl the {@link #normalUrl} to set
	 */
	public void setNormalUrl(String normalUrl) {
		this.normalUrl = normalUrl;
	}
	/**
	 * @return the {@link #thumbnailUrl}
	 */
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	/**
	 * @param thumbnailUrl the {@link #thumbnailUrl} to set
	 */
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public Bitmap getTaskBitmap() {
		return taskBitmap;
	}
	public void setTaskBitmap(Bitmap taskBitmap) {
		this.taskBitmap = taskBitmap;
	}
	public String getTaskimg_path() {
		return taskimg_path;
	}
	public void setTaskimg_path(String taskimg_path) {
		this.taskimg_path = taskimg_path;
	}

	
}
