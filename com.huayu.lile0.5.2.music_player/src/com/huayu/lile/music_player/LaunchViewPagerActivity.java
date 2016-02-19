package com.huayu.lile.music_player;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.huayu_lile.bean.mViewPagerAdapter;

public class LaunchViewPagerActivity extends Activity {

	private ViewPager viewPager;
	private List<View> list = new ArrayList<View>();
	private int current_index = 0;
	private int[] points = new int[] { R.id.iv_viewpager_point1,
			R.id.iv_viewpager_point2, R.id.iv_viewpager_point3 };;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager_activity);
		ImageView iv1 = (ImageView) findViewById(points[current_index]);
		iv1.setImageResource(R.drawable.launcher_point_current);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		init_list();
		PagerAdapter arg0 = new mViewPagerAdapter(this, list);
		viewPager.setAdapter(arg0);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO 自动生成的方法存根
				ImageView iv1 = (ImageView) findViewById(points[current_index]);
				iv1.setImageResource(R.drawable.launcher_point_default);
				current_index = arg0;
				ImageView iv2 = (ImageView) findViewById(points[current_index]);
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

	}

	private void init_list() {

		ImageView imageView1 = new ImageView(this);
		imageView1.setBackgroundResource(R.drawable.viewpager_view1);
		list.add(imageView1);
		ImageView imageView2 = new ImageView(this);
		imageView2.setBackgroundResource(R.drawable.viewpager_view2);
		list.add(imageView2);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.viewpager_view3, null);
		Button bt = (Button) view.findViewById(R.id.bt_viewpager_view3);
		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				Intent intent = new Intent(LaunchViewPagerActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
		list.add(view);
	}
}
