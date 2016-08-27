package com.huayu_lile.mobile_mall.app;

import cn.jpush.android.api.JPushInterface;

import com.activeandroid.ActiveAndroid;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.content.Context;

/**
 * 用于初始化ImageLoader。若不执行初始化，下载图片前清除缓存，存在潜在空指针问题
 * @author Administrator
 *
 */
public class MyApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoader(this);
		ActiveAndroid.initialize(this);
		//初始化百度地图
		SDKInitializer.initialize(this);
		initJPush(); 
		
	}

	private void initJPush() {
//		JPushInterface.setDebugMode(true);	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
	}

	public void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}
