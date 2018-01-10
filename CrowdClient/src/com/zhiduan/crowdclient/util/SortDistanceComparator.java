package com.zhiduan.crowdclient.util;

import java.util.Comparator;

import com.zhiduan.crowdclient.data.OrderInfo;

/** 
 * 按距离进行排序
 * 
 * @author yxx
 *
 * @date 2016-5-30 下午4:19:41
 * 
 */
public class SortDistanceComparator implements Comparator<OrderInfo> {
	
	@Override
	public int compare(OrderInfo info1, OrderInfo info2) {

		try{
			double dis1 = Double.parseDouble(info1.getDistance());
			double dis2 = Double.parseDouble(info2.getDistance());
			
			return (int) (dis1 - dis2);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return 0;
	}

}