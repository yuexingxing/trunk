package com.zhiduan.crowdclient.data;

import java.io.Serializable;

/**
 * hexiuhui
 */
public class ChannelEntity implements Serializable {

    private String title;
    private int resId;
    public int getResId() {
		return resId;
	}
    
    public ChannelEntity() {
    }

    public ChannelEntity(String title, String image_url, int resId) {
        this.title = title;
        this.image_url = image_url;
        this.resId = resId;
    }

	public void setResId(int resId) {
		this.resId = resId;
	}

	private String image_url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
