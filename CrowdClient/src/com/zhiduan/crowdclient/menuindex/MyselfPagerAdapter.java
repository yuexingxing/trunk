package com.zhiduan.crowdclient.menuindex;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MyselfPagerAdapter extends PagerAdapter {

	private List<View> view;

	public MyselfPagerAdapter(List<View> view) {
		this.view = view;
	}

	@Override
	public int getCount() {
		if (view != null) {
			return view.size();
		}
		return 0;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(view.get(position));
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(view.get(position));
		return view.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

}