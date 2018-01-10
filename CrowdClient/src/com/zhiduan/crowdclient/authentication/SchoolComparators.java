package com.zhiduan.crowdclient.authentication;

import java.util.Comparator;

import com.zhiduan.crowdclient.data.SchoolInfo;

/** 
 * 学校排序
 * 
 * @author yxx
 *
 * @date 2016-5-4 下午1:42:51
 * 
 */
public class SchoolComparators implements Comparator<SchoolInfo> {

	public int compare(SchoolInfo o1, SchoolInfo o2) {
		if (o1.getPinYin().equals("@")
				|| o2.getPinYin().equals("#")) {
			return -1;
		} else if (o1.getPinYin().equals("#")
				|| o2.getPinYin().equals("@")) {
			return 1;
		} else {
			return o1.getPinYin().compareTo(o2.getPinYin());
		}
	}

}
