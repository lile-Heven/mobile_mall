package com.huayu.lile.service;

import java.io.IOException;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.huayu.lile.broadcastreceiver.MusicNotification_Receive;
import com.huayu.lile.music_player.MainActivity;
import com.huayu.lile.music_player.MusicPlayActivity;
import com.huayu.lile.music_player.R;
import com.huayu_lile.bean.MusicPlayerUtils;

/**
 * 本地服务（Local Service）:MusicPlayService
 * 
 * @author EDUASK
 * 
 */
public class MusicPlayService extends Service {
	// 通知-对象
	Builder builder;
	/** 当前音乐索引 */
	// -1代表打开app最开始初始化时
	public static int CURRENT_MUSIC_ID = -1;
	// 8代表点击play进入，0代表点击listview进入
	public static int flags = -1;
	// 是否点进的是同一首歌
	public static boolean is_equals = false;
	public MediaPlayer mediaPlayer;
	public static MusicNotification_Receive broadCastReceive;
	// 初始化为-1，0代表暂停，1代表播放
	public static int isPlay_or_not = -1;
	// 1代表正用着未释放，0代表释放
	public static int isRelease_or_not = 1;

	// public ArrayList<MusicInfo> musicList = MusicPlayerUtils.MUSICLIST;
	public MyBinder myBinder = new MyBinder();

	/** 更新seekbar的子线程 */
	public MyThread thread_seekbar;

	public class MyThread extends Thread {
		public boolean flag = true;

		@Override
		public void run() {
			// 更改播放/暂停按钮的图标
			sendMessageToupdate();
			while (flag) {
				sendMessageToupdate_seekbar_lrc();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void init_thread_seekbar() {
		// 当给mediaplayer传新数据的同时，刷新thread
		if (thread_seekbar != null) {
			thread_seekbar.flag = false;
			thread_seekbar = new MyThread();

		} else {
			thread_seekbar = new MyThread();
		}

		// 开始刷新seekbar
		if (thread_seekbar != null) {
			thread_seekbar.start();
			Log.e("thread_seekbar", "success_thread_seekbar!=null");
		}

	}

	// 自定义一个Binder
	public class MyBinder extends Binder {
		public MusicPlayService getService() {
			return MusicPlayService.this;
		}

	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 实例化一个通知的builder
		init_builder();
		regist();
		init_music_one();

		Log.e("---ServiceOnCreate", "success");
		TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telManager.listen(new MobliePhoneStateListener(),
				PhoneStateListener.LISTEN_CALL_STATE);

	}

	private class MobliePhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: /* 无任何状态时(挂断) */
				// 筛剔之前正在播放的状态
				if (isPlay_or_not != 0) {
					return;
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: /* 接起电话时 */

				break;
			case TelephonyManager.CALL_STATE_RINGING: /* 电话进来时 */
				// 筛剔之前正在暂停的状态
				if (isPlay_or_not != 1) {
					return;
				}
				pause_music();
				break;
			default:
				break;

			}

		}

	}

	// 发送通知
	public void sendNotification() {

		// 获得通知管理器
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// 通过NotificationManager来发送一条通知
		manager.notify("一号通知", 0, notification_create());
		// 音乐播放或暂停的切换

	}

	/** android4.1之后 */
	public void init_builder() {

		builder = new Notification.Builder(this);
		builder.setSmallIcon(R.drawable.icon_app2);
		builder.setContentTitle("music");
		builder.setContentText("musiccontent");
		builder.setAutoCancel(false);
		// builder.setDefaults(Notification.FLAG_NO_CLEAR);
		builder.setTicker("Music coming...");
		builder.setPriority(1000);// -1000-1000
		// builder.setPriority(Notification.PRIORITY_MAX);
		//点击通知栏跳转到MainActivity
		Intent intent = new Intent(this,MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		builder.setContentIntent(pendingIntent);
		
		builder.setLights(0xff889900, 500, 300);

	}

	// 创建一个通知
	private Notification notification_create() {
		/*
		 * ????布局-RemoteViews--Linearlayout/Relaytivilayout...--不能使用复杂控件-ListView
		 * -SurfaceView...
		 */
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notificatin_player);

		contentView.setImageViewBitmap(R.id.notification_music_img,
				MusicPlayerUtils.MUSICLIST.get(CURRENT_MUSIC_ID).getBmp());
		contentView.setTextViewText(R.id.notification_music_title_left,
				MusicPlayerUtils.MUSICLIST.get(CURRENT_MUSIC_ID).getTitle()
						+ "-"
						+ MusicPlayerUtils.MUSICLIST.get(CURRENT_MUSIC_ID)
								.getSinger());
		contentView.setTextViewText(R.id.notification_music_title_right,
				MusicPlayerUtils.millisecondFormat(MusicPlayerUtils.MUSICLIST
						.get(CURRENT_MUSIC_ID).getTime()));
		contentView.setImageViewResource(R.id.notification_music_pre,
				R.drawable.player_pre);
		if (isPlay_or_not == 0) {
			contentView.setImageViewResource(
					R.id.notification_music_play_or_pause,
					R.drawable.player_play);
		} else {
			contentView.setImageViewResource(
					R.id.notification_music_play_or_pause,
					R.drawable.player_pause);
		}
		contentView.setImageViewResource(R.id.notification_music_next,
				R.drawable.player_next);

		// 给通知栏里的控件添加监听
		addListener(contentView);

		// 通过builder.build()来创建一个notification
		builder.setContent(contentView);
		Notification notification = builder.build();
		//notification.flags = Notification.FLAG_NO_CLEAR;
		return notification;

	}

	// 初始化一个mediaPlayer并给以联系一个音频文件
	private void init_music_one() {
		// TODO 自动生成的方法存根
		mediaPlayer = new MediaPlayer();
		// reconnect_data();
	}

	public void reconnect_data() {

		// 筛剔musiclist.size为0的状态
		if (MusicPlayerUtils.MUSICLIST == null) {
			Toast.makeText(this, "musicList为空，可能还未初始化", 1).show();
			return;
		} else if (MusicPlayerUtils.MUSICLIST.size() == 0) {
			Toast.makeText(this, "musicList.size()==0", 1).show();
			return;
		}
		// 当给mediaplayer传新数据的同时，刷新thread
		if (thread_seekbar != null) {
			thread_seekbar.flag = false;
			thread_seekbar = new MyThread();

		} else {
			thread_seekbar = new MyThread();
		}
		// 好像是无论mediaplayer处于什么状态，都可以强制reset的，好像是。。。
		mediaPlayer.reset();
		isPlay_or_not = 0;
		try {
			mediaPlayer.setDataSource(this, Uri
					.parse(MusicPlayerUtils.MUSICLIST.get(CURRENT_MUSIC_ID)
							.getData_Url()));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}

		// 初始化播放模式
		mediaPlayer.setLooping(false);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		mediaPlayer.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				if (MusicPlayService.CURRENT_MUSIC_ID < MusicPlayerUtils.MUSICLIST
						.size() - 1) {
					// 10-----9---8以下才能++
					MusicPlayService.CURRENT_MUSIC_ID++;
				} else if (MusicPlayService.CURRENT_MUSIC_ID == MusicPlayerUtils.MUSICLIST
						.size() - 1) {
					MusicPlayService.CURRENT_MUSIC_ID = 0;
				}
				reconnect_data();
				play_music();
				// 更新歌词视图
				sendBroadcast(new Intent(MusicPlayerUtils.lrc_init_ACTION));
				// 更新通知栏的信息
				sendNotification();
				// 更新activity里的play_or_pause
				sendMessageToupdate();
				// 更新MainActivity里面的MainBottomBar
				sendBC_updateMainBottomBar();
				// mp.start();
				// isPlay_or_not = 1;
				Toast.makeText(getApplicationContext(), "播放过程出现了错误，播放下一首...", 1)
						.show();
				Log.e("OnErrorListener", "触发了OnErrorListener");
				return true;
			}
		});
		// 设置单曲正常播放结束后的监听
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// 单曲循环播放的情况
				if (MusicPlayerUtils.mediaPlayMode == 1) {
					// 设置循环播放
					mediaPlayer.setLooping(true);
					mediaPlayer.start();
					// 恢复播放模式，以便每次播放完毕后都能执行OnCompletionListener
					mediaPlayer.setLooping(false);
				}

				// 随机循环播放的情况
				if (MusicPlayerUtils.mediaPlayMode == 2) {
					isPlay_or_not = 0;
					sendMessageToupdate();
					double a = MusicPlayerUtils.MUSICLIST.size()
							* Math.random();
					int b = (int) a;
					MusicPlayService.CURRENT_MUSIC_ID = b;
					// *****在reconnect之前，先reset，避免了递归*****
					mediaPlayer.reset();
					reconnect_data();
					play_music();
					// 更新歌词视图
					sendBroadcast(new Intent(MusicPlayerUtils.lrc_init_ACTION));
					// 更新通知栏的信息
					sendNotification();
					// 更新activity里的play_or_pause
					sendMessageToupdate();
					// 更新MainActivity里面的MainBottomBar
					sendBC_updateMainBottomBar();

				}

				// 顺序循环播放的情况
				if (MusicPlayerUtils.mediaPlayMode == 0) {
					isPlay_or_not = 0;
					sendMessageToupdate();
					// Toast.makeText(context, "下一首监听", 1).show();
					if (MusicPlayService.CURRENT_MUSIC_ID < MusicPlayerUtils.MUSICLIST
							.size() - 1) {
						// 10-----9---8以下才能++
						MusicPlayService.CURRENT_MUSIC_ID++;
					} else if (MusicPlayService.CURRENT_MUSIC_ID == MusicPlayerUtils.MUSICLIST
							.size() - 1) {
						MusicPlayService.CURRENT_MUSIC_ID = 0;
					}
					// *****在reconnect之前，先reset，避免了递归*****
					mediaPlayer.reset();
					reconnect_data();
					play_music();
					// 更新歌词视图
					sendBroadcast(new Intent(MusicPlayerUtils.lrc_init_ACTION));
					// 更新通知栏的信息
					sendNotification();
					// 更新activity里的play_or_pause
					sendMessageToupdate();
					// 更新MainActivity里面的MainBottomBar
					sendBC_updateMainBottomBar();

				}

			}
		});

		// 初始化歌词视图
		sendBroadcast(new Intent(MusicPlayerUtils.lrc_init_ACTION));
		// 更新通知栏的信息
		sendNotification();
		// 更新MusicPlayactivity里的play_or_pause
		sendMessageToupdate();
		// 更新MainActivity里面的整个MainBottomBar
		sendBC_updateMainBottomBar();

	}

	private void mediaplayer_release() {
		// TODO 自动生成的方法存根
		if (isRelease_or_not == 1) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			isRelease_or_not = 0;
			Log.e("---mediaplayer_release", "success");
		}
	}

	/** 添加通知栏按钮的监听 */
	private void addListener(RemoteViews contentView) {

		/** 返回相应的app */
		Intent appIntent = new Intent(Intent.ACTION_MAIN);
		// appIntent.setAction(Intent.ACTION_MAIN);
		appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// appIntent.setComponent(new ComponentName(this.getPackageName(),
		// this.getPackageName() + "." + this.getLocalClassName()));
		appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// 关键的一步，设置启动模式
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				appIntent, 0);
		contentView.setOnClickPendingIntent(R.id.notification_layout,
				contentIntent);

		/** 上一首监听 */
		Intent music_pre_intent = new Intent(
				MusicPlayerUtils.play_player_ACTION);
		music_pre_intent.putExtra("flag", 0);// 上一首按钮监听-flag
		PendingIntent music_pre_pendingIntent = PendingIntent.getBroadcast(
				this, 0, music_pre_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(R.id.notification_music_pre,
				music_pre_pendingIntent);

		/** 播放按钮的监听 */
		/* 意图 */
		Intent music_player_intent = new Intent(
				MusicPlayerUtils.play_player_ACTION);
		music_player_intent.putExtra("flag", 1);// 播放按钮
		PendingIntent music_player_pendingIntent = PendingIntent
				.getBroadcast(this, 1, music_player_intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(
				R.id.notification_music_play_or_pause,
				music_player_pendingIntent);

		/** 下一首监听 */
		Intent music_next_intnet = new Intent(
				MusicPlayerUtils.play_player_ACTION);
		music_next_intnet.putExtra("flag", 2);// 下一首按钮监听-flag
		PendingIntent music_next_pendingIntent = PendingIntent.getBroadcast(
				this, 2, music_next_intnet, PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(R.id.notification_music_next,
				music_next_pendingIntent);

	}

	// 播放音乐
	public void play_music() {
		// 筛剔之前正在播放的状态
		if (isPlay_or_not != 0) {
			return;
		}
		// 之前处于暂停
		Log.e("error38", "before");
		try {
			mediaPlayer.start();
		} catch (Throwable e) {
			e.printStackTrace();
			// Toast.makeText(context, "下一首监听", 1).show();
			if (MusicPlayService.CURRENT_MUSIC_ID < MusicPlayerUtils.MUSICLIST
					.size() - 1) {
				// 10-----9---8以下才能++
				MusicPlayService.CURRENT_MUSIC_ID++;
			} else if (MusicPlayService.CURRENT_MUSIC_ID == MusicPlayerUtils.MUSICLIST
					.size() - 1) {
				MusicPlayService.CURRENT_MUSIC_ID = 0;
			}
			reconnect_data();
			// 更新通知栏信息（更新歌曲图标以及歌名等歌曲信息）
			sendNotification();
			play_music();
			// 好像捕获不了哦。。。
			Log.e("Throwablecatch", "success");
			return;
		}
		Log.e("error38", "after");
		// 开始刷新seekbar
		if (thread_seekbar != null) {
			thread_seekbar.start();
			Log.e("thread_seekbar", "success_thread_seekbar!=null");
		}
		isPlay_or_not = 1;
		// 更改播放/暂停按钮的图标
		sendMessageToupdate();
		// 更新通知栏信息（更新播放，暂停按钮的图标）
		sendNotification();
		// 仅更新MainActivity里面的MainBottomBar的play和动画
		sendBC_updateMainBottomBar_Play();
	}

	private void sendBC_updateMainBottomBar_Play() {
		// TODO 自动生成的方法存根
		sendBroadcast(new Intent(
				MusicPlayerUtils.main_bottombar_play_init_ACTION));

	}

	// 暂停音乐
	public void pause_music() {
		// 筛剔之前正在暂停的状态
		if (isPlay_or_not != 1) {
			return;
		}
		// 之前处于播放
		Log.e("---isplaying_pause", "" + mediaPlayer.isPlaying());
		mediaPlayer.pause();
		if (thread_seekbar != null) {
			thread_seekbar.flag = false;
			// 这里虽改变了flag的值但此时的线程仍然是already started状态
			// 看来线程不存在关闭线程的概念，线程运行完run后就相当于自动结束了。
			thread_seekbar = null;
			thread_seekbar = new MyThread();
		}
		isPlay_or_not = 0;
		// 更改播放/暂停按钮的图标
		sendMessageToupdate();
		// 更新通知栏信息（更新播放，暂停按钮的图标）
		sendNotification();

		// 仅更新MainActivity里面的MainBottomBar的play和动画
		sendBC_updateMainBottomBar_Play();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.e("---ServiceonStartCommand", "success");
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 注意！！！已过时。。。已替换为onStartCommand
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		Log.e("---ServiceonStart", "success");
		super.onStart(intent, startId);
	}

	/**
	 * 绑定服务
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		Log.e("---MyBinder", myBinder.toString());
		return myBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO 自动生成的方法存根
		/*
		 * if(thread_seekbar!=null){ thread_seekbar.flag=false;
		 * thread_seekbar.interrupt(); thread_seekbar=null; }
		 */
		Log.e("---ServiceonUnbind", "success");
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO 自动生成的方法存根
		Log.e("---ServiceonRebind", "success");
		super.onRebind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 注销通知栏的通知:TAG处的值必须跟之前发通知时的Tag相同；id也要相同；否则都清不掉通知栏；至于第二个参数意义，目前未知；
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.cancel("一号通知", 0);
		// 释放mediaplayer
		mediaplayer_release();
		isPlay_or_not = -1;
		// 注销之前动态注册广播接收器
		if (broadCastReceive != null) {
			unregisterReceiver(broadCastReceive);
		}
		// 注销ui更新子线程
		if (thread_seekbar != null) {
			thread_seekbar.flag = false;
			thread_seekbar.destroy();
		}
		Log.e("ServiceonDestroy", "success");
	}

	public void sendMessageToupdate() {
		if (musicPlayActivity != null) {
			// 更新播放/暂停图标
			musicPlayActivity.sendMessageToupdate();
		}
	}

	public void sendBC_updateMainBottomBar() {
		sendBroadcast(new Intent(MusicPlayerUtils.main_bottombar_init_ACTION));
	}

	/** 动态注册notification发送的广播的接收器 */
	private void regist() {
		broadCastReceive = new MusicNotification_Receive();
		IntentFilter filter = new IntentFilter(
				MusicPlayerUtils.play_player_ACTION);
		registerReceiver(broadCastReceive, filter);

	}

	public void sendMessageToupdate_seekbar_lrc() {
		// TODO Auto-generated method stub
		if (musicPlayActivity != null) {

			// ui_update_action，是更新seekbar和lrcshowview
			Intent intent = new Intent(MusicPlayerUtils.ui_update_ACTION);
			intent.putExtra("SeekBar_currentPosition",
					mediaPlayer.getCurrentPosition());
			sendBroadcast(intent);
			// 这个是更新seekbar左上角的时间
			Intent intent2 = new Intent(
					MusicPlayerUtils.ui_update_time_left_of_seekbar_ACTION);

			sendBroadcast(intent2);
		}
	}

	MusicPlayActivity musicPlayActivity;

	public void bind_MusicPlayActivity(MusicPlayActivity musicPlayActivity) {
		// TODO Auto-generated method stub
		this.musicPlayActivity = musicPlayActivity;
	}

	public void unbind_MusicPlayActivity() {
		// TODO Auto-generated method stub
		this.musicPlayActivity = null;
	}

}
