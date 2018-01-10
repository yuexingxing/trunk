package com.zhiduan.crowdclient.data;

/** 
 * 已抢单页数
 * 
 * @author yxx
 *
 * @date 2016-6-4 下午1:45:04
 * 
 */
public class PageInfo {

	private int totalCount = 0;//总条数
	private int totalPageCount = 0;//总页数
	private int pageNumber = 0;//当前页
	private int pageSize = 0;//每页条数
	private String orderBy = "";//排序规则


	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPageCount() {
		return totalPageCount;
	}

	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
}
