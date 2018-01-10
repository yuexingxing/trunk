package com.zhiduan.crowdclient.menuindex.filterlistview;

import java.util.ArrayList;
import java.util.List;


/**
 * hexiuhui
 */
public class ModelUtil {

    // 广告
    public static List<String> getAdData() {
        List<String> adList = new ArrayList<String>();
        adList.add("http://img0.imgtn.bdimg.com/it/u=1270781761,1881354959&fm=21&gp=0.jpg");
        adList.add("http://img0.imgtn.bdimg.com/it/u=2138116966,3662367390&fm=21&gp=0.jpg");
        adList.add("http://img0.imgtn.bdimg.com/it/u=1296117362,655885600&fm=21&gp=0.jpg");
        return adList;
    }


    // 设置订单类型
    public static List<FilterEntity> getCategoryData() {
    	List<FilterEntity> list = new ArrayList<FilterEntity>();
        list.add(new FilterEntity("全部", "", false));
        list.add(new FilterEntity("跑腿订单", "errands", false));
        list.add(new FilterEntity("快递订单", "express", false));
        return list;
    }

    // 设置时间排序
    public static List<FilterEntity> getSortData() {
        List<FilterEntity> list = new ArrayList<FilterEntity>();
        list.add(new FilterEntity("下单时间优先", "0", false));
        list.add(new FilterEntity("服务时间优先", "1", false));
        return list;
    }

    // 筛选性别排序
    public static List<FilterEntity> getFilterData() {
        List<FilterEntity> list = new ArrayList<FilterEntity>();
        list.add(new FilterEntity("所有性别", "0", false));
        list.add(new FilterEntity("男", "1", false));
        list.add(new FilterEntity("女", "2", false));
        return list;
    }
}
