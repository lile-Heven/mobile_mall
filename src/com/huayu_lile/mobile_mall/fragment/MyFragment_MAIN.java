package com.huayu_lile.mobile_mall.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.com.huayu.lile.mobile_mall.R;
import com.huayu_lile.mobile_mall.bean.MyViewPagerAdapter;

public class MyFragment_MAIN extends Fragment{
	private int pageId;
	private ViewPager viewPager;
	private List<View> list = new ArrayList<View>();
	private int current_index = 0;
	private int[] points = new int[] { R.id.iv_viewpager_point1,
			R.id.iv_viewpager_point2, R.id.iv_viewpager_point3,R.id.iv_viewpager_point4,
			R.id.iv_viewpager_point5,R.id.iv_viewpager_point6
	};
	private View view_base;
	private Activity activity_base;;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activity_base=getActivity();
		
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// TODO 自动生成的方法存根
    	view_base=inflater.inflate(R.layout.fragment_main, null, false);
    	init_viewpager();
    	return view_base;
    }

	@SuppressWarnings("deprecation")
	private void init_viewpager() {
		// TODO Auto-generated method stub
		ImageView iv1 = (ImageView) view_base.findViewById(points[current_index]);
		//iv1.setBackgroundResource(R.drawable.launcher_point_current);
		iv1.setImageResource(R.drawable.launcher_point_current);
		viewPager = (ViewPager) view_base.findViewById(R.id.viewpager);
		/*
		 * iv_viewpager_point1=(ImageView)findViewById(R.id.iv_viewpager_point1);
		 * iv_viewpager_point2
		 * =(ImageView)findViewById(R.id.iv_viewpager_point2);
		 * iv_viewpager_point3
		 * =(ImageView)findViewById(R.id.iv_viewpager_point3);
		 */

		// 初始化list
		init_viewpager_list();
		PagerAdapter arg0 = new MyViewPagerAdapter(activity_base, list);
		viewPager.setAdapter(arg0);
		viewPager.setOffscreenPageLimit(list.size());
		AutoThread autoThread=new AutoThread();
		autoThread.start();
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			/**页面结束以后位于哪个位置*/
			@Override
			public void onPageSelected(int arg0) {
				pageId=arg0;
				// TODO 自动生成的方法存根
				ImageView iv1 = (ImageView) view_base.findViewById(points[current_index]);
				iv1.setImageResource(R.drawable.launcher_point_default);
				current_index = arg0;
				ImageView iv2 = (ImageView) view_base.findViewById(points[current_index]);
				iv2.setImageResource(R.drawable.launcher_point_current);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO 自动生成的方法存根

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO 自动生成的方法存根

			}
		});

		// 给view3的button设置点击监听
		// Button bt = (Button)findViewById(R.id.bt_viewpager_view3);

		
	}

	private void init_viewpager_list() {

		ImageView imageView1 = new ImageView(activity_base);
		imageView1.setBackgroundResource(R.drawable.viewpager1);
		list.add(imageView1);
		
		ImageView imageView2 = new ImageView(activity_base);
		imageView2.setBackgroundResource(R.drawable.viewpager2);
		list.add(imageView2);
		
		ImageView imageView3 = new ImageView(activity_base);
		imageView3.setBackgroundResource(R.drawable.viewpager3);
		list.add(imageView3);
		
		ImageView imageView4 = new ImageView(activity_base);
		imageView4.setBackgroundResource(R.drawable.viewpager4);
		list.add(imageView4);
		
		ImageView imageView5 = new ImageView(activity_base);
		imageView5.setBackgroundResource(R.drawable.viewpager5);
		list.add(imageView5);
		
		ImageView imageView6 = new ImageView(activity_base);
		imageView6.setBackgroundResource(R.drawable.viewpager6);
		list.add(imageView6);
		
	}
	public Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (0 == msg.what) {
				int id = Integer.valueOf(msg.obj.toString());
				viewPager.setCurrentItem(id);
			}
			return true;
		}
	});
	/**线程的两种实现方式*/
	class AutoThread extends Thread{
		

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message msg=new Message();
				msg.what=0;//标志：哪种控件、类别更新
				
				msg.obj=pageId%list.size();//值
				handler.sendMessage(msg);
				pageId++;
			}
		}
	}
}

