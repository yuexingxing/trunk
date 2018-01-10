package com.zhiduan.crowdclient.util;

import java.util.Comparator;

import com.zhiduan.crowdclient.data.OrderInfo;

/** 
 * 按签收时间进行排序
 * 
 * @author yxx
 *
 * @date 2016-5-30 下午4:19:41
 * 
 */
public class SortSignTimeComparator implements Comparator<OrderInfo> {
	
	@Override
	public int compare(OrderInfo info1, OrderInfo info2) {

		try{
			return info1.getSignTime().compareTo(info2.getSignTime());
		}catch(Exception e){
			e.printStackTrace();
		}
	
		return -1;
	}

}