package com.zhiduan.crowdclient.data;

/**
 * 描述：广告信息</br>
 */
public class ADInfo {
	
	String id = "";
	String url = "";//广告图片路径
	String url_link;//广告跳转链接
	String title;//广告标题
	String content = "";
	String type = "";
	int open_type; //跳转类型
	String open_url;  //内部跳转

	public int getOpen_type() {
		return open_type;
	}

	public void setOpen_type(int open_type) {
		this.open_type = open_type;
	}

	public String getOpen_url() {
		return open_url;
	}

	public void setOpen_url(String open_url) {
		this.open_url = open_url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl_link() {
		return url_link;
	}

	public void setUrl_link(String url_link) {
		this.url_link = url_link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
