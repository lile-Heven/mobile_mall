package com.huayu.lile.mobile_mall;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Movie;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.example.com.huayu.lile.mobile_mall.R;
import com.huayu_lile.mobile_mall.fragment.MyFragment_FL;
import com.huayu_lile.mobile_mall.fragment.MyFragment_FX;
import com.huayu_lile.mobile_mall.fragment.MyFragment_GWC;
import com.huayu_lile.mobile_mall.fragment.MyFragment_MAIN;
import com.huayu_lile.mobile_mall.fragment.MyFragment_WD;

public class MainActivity extends Activity implements OnClickListener {
	private Movie movie;

	private LinearLayout linearlayout;

	private int screenWidth = 0;

	private FragmentManager fragmentManager;

	private ImageView iv_mainbottombar_1;

	private ImageView iv_mainbottombar_2;

	private ImageView iv_mainbottombar_3;

	private ImageView iv_mainbottombar_4;

	private ImageView iv_mainbottombar_5;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*
		 * linearlayout=(LinearLayout)findViewById(R.id.linearlayout);
		 * 
		 * GifImageView gifview=new GifImageView(this);
		 * 
		 * gifview.setImageResource( R.drawable.pangzi1 );
		 * linearlayout.addView(gifview); final MediaController mc = new
		 * MediaController( this ); mc.setMediaPlayer( ( GifDrawable )
		 * gifview.getDrawable() ); mc.setAnchorView( gifview );
		 * gifview.setOnClickListener( new OnClickListener() {
		 * 
		 * @Override public void onClick ( View v ) { mc.show(); } } );
		 */

		init_bottombar_height();
		init_bottombar_listener();

		init_framelayout();

	}

	private void init_bottombar_listener() {
		// TODO Auto-generated method stub
		iv_mainbottombar_1 = (ImageView) findViewById(R.id.iv_mainbottombar_1);
		iv_mainbottombar_1.setOnClickListener(this);

		iv_mainbottombar_2 = (ImageView) findViewById(R.id.iv_mainbottombar_2);
		iv_mainbottombar_2.setOnClickListener(this);

		iv_mainbottombar_3 = (ImageView) findViewById(R.id.iv_mainbottombar_3);
		iv_mainbottombar_3.setOnClickListener(this);

		iv_mainbottombar_4 = (ImageView) findViewById(R.id.iv_mainbottombar_4);
		iv_mainbottombar_4.setOnClickListener(this);

		iv_mainbottombar_5 = (ImageView) findViewById(R.id.iv_mainbottombar_5);
		iv_mainbottombar_5.setOnClickListener(this);
	}

	private void init_framelayout() {
		// TODO Auto-generated method stub
		MyFragment_MAIN fragment_main = new MyFragment_MAIN();
		fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().add(R.id.frameLayout, fragment_main)
				.commit();
	}

	private void init_bottombar_height() {
		// TODO Auto-generated method stub
		/*
		 * 利用getLayoutParams()方法 和 setLayoutParams()方法 重新 设置 控件的 布局，
		 * 1、首先利用getLayoutParams()方法，获取控件的LayoutParams。 eg:LayoutParams
		 * laParams=(LayoutParams)imageView.getLayoutParams();
		 * 2、设置该控件的layoutParams参数 eg: laParams.height=200; laParams.width=100;
		 * 3、将修改好的layoutParams设置为该控件的layoutParams.
		 * eg：imageView.setLayoutParams(laParams);
		 */
		getScreenMetrics();
		linearlayout = (LinearLayout) findViewById(R.id.ll_bottombar);
		LayoutParams laParams = (LayoutParams) linearlayout.getLayoutParams();
		laParams.height = (int) (screenWidth / 5.0);
		linearlayout.setLayoutParams(laParams);

	}

	private void getScreenMetrics() {
		// TODO Auto-generated method stub
		// 获取整个屏幕的宽度、高度
		DisplayMetrics screenMetrics = new DisplayMetrics();
		// 将屏幕的宽、高放入displayMetrics中
		getWindowManager().getDefaultDisplay().getMetrics(screenMetrics);
		screenWidth = screenMetrics.widthPixels;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_mainbottombar_1:

			iv_mainbottombar_1.setImageResource(R.drawable.shouye_0);
			iv_mainbottombar_2.setImageResource(R.drawable.fenlei_0);
			iv_mainbottombar_3.setImageResource(R.drawable.faxian_0);
			iv_mainbottombar_4.setImageResource(R.drawable.gouwuche_0);
			iv_mainbottombar_5.setImageResource(R.drawable.wode_0);
			//
			iv_mainbottombar_1.setImageResource(R.drawable.shouye_1);
			//更新fragment
			MyFragment_MAIN fragment_main = new MyFragment_MAIN();
			fragmentManager.beginTransaction().replace(R.id.frameLayout,fragment_main ).commit();
			break;
		case R.id.iv_mainbottombar_2:
			// fragmentManager.beginTransaction().replace(R.id., fragment)
			iv_mainbottombar_1.setImageResource(R.drawable.shouye_0);
			iv_mainbottombar_2.setImageResource(R.drawable.fenlei_0);
			iv_mainbottombar_3.setImageResource(R.drawable.faxian_0);
			iv_mainbottombar_4.setImageResource(R.drawable.gouwuche_0);
			iv_mainbottombar_5.setImageResource(R.drawable.wode_0);
			//
			iv_mainbottombar_2.setImageResource(R.drawable.fenlei_1);
			//更新fragment
			MyFragment_FL fragment_fl = new MyFragment_FL();
			fragmentManager.beginTransaction().replace(R.id.frameLayout,fragment_fl ).commit();
			break;
		case R.id.iv_mainbottombar_3:
			iv_mainbottombar_1.setImageResource(R.drawable.shouye_0);
			iv_mainbottombar_2.setImageResource(R.drawable.fenlei_0);
			iv_mainbottombar_3.setImageResource(R.drawable.faxian_0);
			iv_mainbottombar_4.setImageResource(R.drawable.gouwuche_0);
			iv_mainbottombar_5.setImageResource(R.drawable.wode_0);
			//
			iv_mainbottombar_3.setImageResource(R.drawable.faxian1);
			//更新fragment
			MyFragment_FX fragment_fx = new MyFragment_FX();
			fragmentManager.beginTransaction().replace(R.id.frameLayout,fragment_fx ).commit();
			break;
		case R.id.iv_mainbottombar_4:
			iv_mainbottombar_1.setImageResource(R.drawable.shouye_0);
			iv_mainbottombar_2.setImageResource(R.drawable.fenlei_0);
			iv_mainbottombar_3.setImageResource(R.drawable.faxian_0);
			iv_mainbottombar_4.setImageResource(R.drawable.gouwuche_0);
			iv_mainbottombar_5.setImageResource(R.drawable.wode_0);
			//
			iv_mainbottombar_4.setImageResource(R.drawable.gouwuche_1);
			//更新fragment
			MyFragment_GWC fragment_gwc = new MyFragment_GWC();
			fragmentManager.beginTransaction().replace(R.id.frameLayout,fragment_gwc).commit();
			break;
		case R.id.iv_mainbottombar_5:
			iv_mainbottombar_1.setImageResource(R.drawable.shouye_0);
			iv_mainbottombar_2.setImageResource(R.drawable.fenlei_0);
			iv_mainbottombar_3.setImageResource(R.drawable.faxian_0);
			iv_mainbottombar_4.setImageResource(R.drawable.gouwuche_0);
			iv_mainbottombar_5.setImageResource(R.drawable.wode_0);
			//
			iv_mainbottombar_5.setImageResource(R.drawable.wode_1);
			//更新fragment
			MyFragment_WD fragment_wd = new MyFragment_WD();
			fragmentManager.beginTransaction().replace(R.id.frameLayout,fragment_wd ).commit();
			break;

		default:
			break;
		}

	}

}
