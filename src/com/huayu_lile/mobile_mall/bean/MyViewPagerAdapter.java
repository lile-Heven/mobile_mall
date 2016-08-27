package com.huayu_lile.mobile_mall.bean;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MyViewPagerAdapter extends PagerAdapter{
	private Context context;
	private List<View> list;

	/**
	 * @param context
	 * @param list
	 */
	public MyViewPagerAdapter(Context context, List<View> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(list.get(position));
	}

	@Override
	public int getCount() {
		// TODO �Զ����ɵķ������
		return list.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		
			container.addView(list.get(position),position);
			
		return list.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO �Զ����ɵķ������
		return arg0==arg1;
	}
	

}
