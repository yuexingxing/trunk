package com.zhiduan.crowdclient.util;

import java.util.Comparator;

import com.zhiduan.crowdclient.data.OrderInfo;

/** 
 * 按时间进行排序
 * 
 * @author yxx
 *
 * @date 2016-5-30 下午4:19:41
 * 
 */
public class SortGrabTimeComparator implements Comparator<OrderInfo> {
	
	@Override
	public int compare(OrderInfo info1, OrderInfo info2) {
		
		try{
			return info1.getGrabTime().compareTo(info2.getGrabTime());
		}catch(Exception e){
			e.printStackTrace();
		}

		return 0;
	}

}