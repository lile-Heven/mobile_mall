package com.huayu_lile.bean;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class mViewPagerAdapter extends PagerAdapter{
	private Context context;
	private List<View> list;

	/**
	 * @param context
	 * @param list
	 */
	public mViewPagerAdapter(Context context, List<View> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO 自动生成的方法存根
		return arg0==arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(list.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
			container.addView(list.get(position),position);
			
		return list.get(position);
	}
	

}
