package com.huayu.lile.music_player;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.huayu_lile.bean.MusicPlayerUtils;

public class LaunchActivity extends Activity {
	
	private ImageView iv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.launch);
		iv=(ImageView)findViewById(R.id.iv_launch_activity);
		iv.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_launch_pic));
		new Thread(){
			public void run() {
				
				initMusicList();
			};
		}.start();
		new Thread(){
			public void run() {
				try {
					sleep(3500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//判断是否为第一次启动
				if(first_start()){
					Intent intent=new Intent(LaunchActivity.this,LaunchViewPagerActivity.class);
					startActivity(intent);
					finish();
					
				}else{
					Intent intent=new Intent(LaunchActivity.this,MainActivity.class);
					startActivity(intent);
					finish();
				}
				
				
			};
		}.start();
	}
	private boolean first_start(){
		SharedPreferences preferences=getSharedPreferences("SHARE_APP_TAG", 0);
		boolean isFirst=preferences.getBoolean("IS_FIRST", true);
		Log.d("tag", "LaunchActivty-IS_FIRST:"+isFirst);
		if(isFirst){
			preferences.edit().putBoolean("IS_FIRST", false).commit();
			Log.d("tag", "LaunchActivty-firststart---true");
			return true;
		}
		
		Log.d("tag", "LaunchActivty-firststart---false");
		return false;
		
	}
	/** 扫描SD卡并添加歌曲信息 */
	private void initMusicList() {
		MusicPlayerUtils.getMusicList(this);
	}
}
