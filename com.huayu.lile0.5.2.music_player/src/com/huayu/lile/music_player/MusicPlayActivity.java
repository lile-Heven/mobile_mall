package com.huayu.lile.music_player;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.huayu.lile.broadcastreceiver.InitLRC_Receiver;
import com.huayu.lile.service.MusicPlayService;
import com.huayu.lile.service.MusicPlayService.MyBinder;
import com.huayu_lile.bean.GetData_LrcList;
import com.huayu_lile.bean.LrcContent;
import com.huayu_lile.bean.LrcView;
import com.huayu_lile.bean.MusicPlayerUtils;

public class MusicPlayActivity extends Activity implements OnClickListener {
	private boolean handler_switch;
	Button send, clear, bt_change_playmode;
	ImageView musicplay_pre, musicplay_play_or_pause, musicplay_next;
	private SeekBar activity_seekbar;
	private MusicPlayService mysicPlayService;
	private MyServiceConnection myServiceConn = new MyServiceConnection();
	private MediaPlayer mediaPlayer;
	private UIupdate_Receiver uiupdate_Receiver;
	private LrcView lrcShowView;
	private List<LrcContent> lrcList = new ArrayList<LrcContent>(); // 存放歌词列表对象
	private Handler handler;
	private TextView tv_music_play_wholetime;
	private TextView tv_music_play_currenttime;

	private int index;

	private InitLRC_Receiver lrc_init_Receiver;

	class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			handler_switch=true;
			// TODO Auto-generated method stub
			if (mysicPlayService == null) {
				mysicPlayService = ((MyBinder) service).getService();
				// 获得MusicPlayActivity的引用地址（句柄）并传回给Service
				mysicPlayService.bind_MusicPlayActivity(MusicPlayActivity.this);
				(MusicPlayActivity.this).mediaPlayer = mysicPlayService.mediaPlayer;
				Log.d("tag",
						"ServiceConnection活动绑定服务成功！" + mysicPlayService.toString());
				// 当service正在播放音乐，而用户又在mainactivity点击跳转到这个activity时，做一个状态监测
				// reconnect_data的同时，把正在播放的音乐暂停了
				Log.e("---mysicPlayService.mediaPlayer", ""+mysicPlayService.mediaPlayer);
				if (mysicPlayService.mediaPlayer != null) {
					//8代表点击play进入，0代表点击listview进入
					//从play点击进入，则不需要reconnect
					if(MusicPlayService.flags==8){
						sendMessageToupdate();
						return ;
					}
					if(MusicPlayService.is_equals){
						sendMessageToupdate();
						return;
					}
					mysicPlayService.reconnect_data();
					mysicPlayService.play_music();
					//把mainactivity的bottombar浮现出来
					sendBroadcast(new Intent(MusicPlayerUtils.main_bottombar_visible_ACTION));
					
					Log.d("tag", "execute---MusicPlayActivity__mysicPlayService.reconnect_data()");
				}
				//更新musicplayactivity的ui（播放/暂停图标和seekbar，seekbar左上角时间）
				sendMessageToupdate();
				
				
			} else {
				Log.e("ServiceConnection", "活动绑定服务失败！");
			}
		}

		/**
		 * 好像上次演示时说了，接触绑定的时候，不会调用以下这个方法？ServiceConnection中的onServiceDisconnected
		 * ()方法在正常情况下是不被调用的，它的调用时机是当Service服务被异外销毁时，例如内存的资源不足时这个
		 * 它发生在远程服务崩溃。所以，如果在不同的进程中运行比你的客户端服务上的异常失败，你失去了连接，并获取回调。
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_play);
				
		

		// 当此活动打开的时候，绑定Service
		bindService(new Intent(this, MusicPlayService.class), myServiceConn,
				Service.BIND_AUTO_CREATE);
		Log.d("tag", "---mysicPlayService" + mysicPlayService);
		
		// 注册广播接收器_UI控件更新
		registUIupdate_Receiver();
		// 注册LRC初始化接收器
		registLRC_init_Receiver();
		

		bt_change_playmode = (Button) findViewById(R.id.bt_change_playmode);
		tv_music_play_wholetime = (TextView) findViewById(R.id.tv_music_play_wholetime);
		tv_music_play_currenttime = (TextView) findViewById(R.id.tv_music_play_currenttime);
		musicplay_pre = (ImageView) findViewById(R.id.musicplay_pre);
		musicplay_play_or_pause = (ImageView) findViewById(R.id.musicplay_play_or_pause);
		musicplay_next = (ImageView) findViewById(R.id.musicplay_next);
		activity_seekbar = (SeekBar) findViewById(R.id.activity_seekbar);
		lrcShowView = (LrcView) findViewById(R.id.lrcShowView);
		//初始化歌词视图
		initLrc();
		
		handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				// 更新activity里的playpause按钮图标
				switch (msg.what) {
				case 0:
					if (mysicPlayService.isPlay_or_not == 0) {
						musicplay_play_or_pause
								.setImageResource(R.drawable.musicplay_play);
					} else {
						musicplay_play_or_pause
								.setImageResource(R.drawable.musicplay_pause);
						tv_music_play_wholetime.setText(MusicPlayerUtils
								.millisecondFormat(mediaPlayer.getDuration()));
					}

					break;
				case 1:
					int a = ((Integer) msg.obj) * activity_seekbar.getMax()
							/ mediaPlayer.getDuration();
					activity_seekbar.setProgress(a);
					break;
				default:
					break;
				}

				return false;
			}
		});
		activity_seekbar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						//筛除点进同一首歌时的情况
						if(MusicPlayService.is_equals){
							//使用完后，恢复is_equals的缺省值
							MusicPlayService.is_equals=false;
							return ;
						}
						// 注意！如果这样写是会输出0：progress/seekBar.getMax()*mediaPlayer.getDuration()
						int currentMusicTime = (progress
								* mediaPlayer.getDuration() / seekBar.getMax());

						//区分是人手动调节的，还是机器自动更新调节的
						if (mediaPlayer.getCurrentPosition() - currentMusicTime > 2000
								|| currentMusicTime
										- mediaPlayer.getCurrentPosition() > 2000) {

							mediaPlayer.seekTo(currentMusicTime);
						}
					}
				});
		musicplay_pre.setOnClickListener(this);
		musicplay_play_or_pause.setOnClickListener(this);
		musicplay_next.setOnClickListener(this);
		bt_change_playmode.setOnClickListener(this);
		
		
		
	}

	/**
	 * 初始化歌词配置
	 */
	public void initLrc() {
		//new GetData_LrcList()的同时， 也在new lrclist
		GetData_LrcList getData_LrcList = new GetData_LrcList();
		// 读取歌词文件：歌词文件必须和音频文件在同一文件夹下，并且除了后缀不同外，需同名
		// 看来不需要Uri.parse?
		try {
			getData_LrcList.readLRC(MusicPlayerUtils.MUSICLIST.get(
					MusicPlayService.CURRENT_MUSIC_ID).getData_Url());
		} catch (Exception e) {
			// TODO: handle exception
			getData_LrcList = new GetData_LrcList();
		}
			
			
		
		lrcList = getData_LrcList.getLrcList();
		if (lrcList.size() == 0) {
			Toast.makeText(this, "此曲没有歌词文件", 1).show();
			
			//return;
		}
		Log.d("tag", "lrcList.size() : "+lrcList.size());
		lrcShowView.setmLrcList(lrcList);
		lrcShowView.invalidate();
	}


	Handler handler2 = new Handler();
	Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			lrcShowView.setIndex(getLrcIndex());
			lrcShowView.invalidate();
		}
	};

	/**
	 * 通过当前music的currentTime，寻找到此歌该显示的歌词内容的角下标
	 * 
	 * @return
	 */
	public int getLrcIndex(){
		int currentTime = 0;
		int duration = 0;
		if (mediaPlayer.isPlaying()) {
			currentTime = mediaPlayer.getCurrentPosition();
			duration = mediaPlayer.getDuration();
		}
		if (currentTime < duration) {
			for (int i = 0; i < lrcList.size(); i++) {
				if (i < lrcList.size() - 1) {
					if (currentTime < lrcList.get(i).getLrcTime() && i == 0) {
						index = i;
					} else if (currentTime > lrcList.get(i).getLrcTime()
							&& currentTime < lrcList.get(i + 1).getLrcTime()) {
						index = i;
					}

				} else if (i == lrcList.size() - 1
						&& currentTime > lrcList.get(i).getLrcTime()) {
					index = i;
				}
			}
		}
		return index;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_change_playmode:
			if (MusicPlayerUtils.mediaPlayMode == 0) {
				MusicPlayerUtils.mediaPlayMode++;
				bt_change_playmode.setText("单曲循环");
			} else if (MusicPlayerUtils.mediaPlayMode == 1) {
				MusicPlayerUtils.mediaPlayMode++;
				bt_change_playmode.setText("随机播放");
			} else {
				MusicPlayerUtils.mediaPlayMode = 0;
				bt_change_playmode.setText("顺序播放");
			}
			break;
		case R.id.musicplay_pre:
			//上一首监听
			if(MusicPlayService.CURRENT_MUSIC_ID>0){
				MusicPlayService.CURRENT_MUSIC_ID--;
			}else if(MusicPlayService.CURRENT_MUSIC_ID==0){
				MusicPlayService.CURRENT_MUSIC_ID=MusicPlayerUtils.MUSICLIST.size()-1;
			}
			mysicPlayService.reconnect_data();
			mysicPlayService.play_music();
			
			// 更新通知栏的信息
			mysicPlayService.sendNotification();
			//更新activity里的play_or_pause
			sendMessageToupdate();

			break;
		case R.id.musicplay_play_or_pause:
			// 播放/暂停按钮监听
			if (mysicPlayService.isPlay_or_not == 0) {
				mysicPlayService.play_music();
				// 更新activity里的play_or_pause
				sendMessageToupdate();
			} else {
				mysicPlayService.pause_music();
				// 更新activity里的play_or_pause
				sendMessageToupdate();
			}
			// 更新通知栏的信息
			mysicPlayService.sendNotification();
			break;
		case R.id.musicplay_next:
			if(MusicPlayService.CURRENT_MUSIC_ID<MusicPlayerUtils.MUSICLIST.size()-1){
				//10-----9---8以下才能++
				MusicPlayService.CURRENT_MUSIC_ID++;
			}else if(MusicPlayService.CURRENT_MUSIC_ID==MusicPlayerUtils.MUSICLIST.size()-1){
				MusicPlayService.CURRENT_MUSIC_ID=0;
			}
			mysicPlayService.reconnect_data();
			mysicPlayService.play_music();
			
			// 更新通知栏的信息
			mysicPlayService.sendNotification();
			//更新activity里的play_or_pause
			sendMessageToupdate();

			break;

		default:
			break;
		}
	}

	/**
	 更新musicplayActivity里面的play_or_pause图标
	 */
	public void sendMessageToupdate() {
		// Message msg=new Message();
		if(handler_switch){
			handler.sendEmptyMessage(0);
		}
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		//把调用handler的通道关闭
		handler_switch=false;
		// 当此活动关闭的时候，解绑服务
		try {

			unbindService(myServiceConn);
			mysicPlayService = null;
			(MusicPlayActivity.this).mediaPlayer = null;
			mysicPlayService.unbind_MusicPlayActivity();
			
			unbindService(myServiceConn);

		} catch (Exception e) {
			Log.w("UnBindService", "出现异常_解绑服务");
		}
		try {
			// 注销接收器
			unregisterReceiver(uiupdate_Receiver);
			unregisterReceiver(lrc_init_Receiver);
			Log.d("tag", "成功注销ui更新接收器");
		} catch (Exception e) {
			Log.d("UnregisterReceiver", "出现异常_注销ui更新接收器");
		}
		
		Log.e("---MusicPlayActivityonDestroy", "success");

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	//注册lrc初始化接收器
	private void registLRC_init_Receiver() {
		IntentFilter filter = new IntentFilter(MusicPlayerUtils.lrc_init_ACTION);
		lrc_init_Receiver = new InitLRC_Receiver();
		registerReceiver(lrc_init_Receiver, filter);
	}
	//注册更新“播放/暂停”和“seekbar”和“当前播放至时间”控件的接收器
	private void registUIupdate_Receiver() {
		// TODO Auto-generated method stub
		uiupdate_Receiver = new UIupdate_Receiver();
		IntentFilter filter = new IntentFilter(
				MusicPlayerUtils.ui_update_ACTION);
		filter.addAction(MusicPlayerUtils.ui_update_time_left_of_seekbar_ACTION);
		registerReceiver(uiupdate_Receiver, filter);
	}

	class UIupdate_Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action
					.equals(MusicPlayerUtils.ui_update_time_left_of_seekbar_ACTION)) {
				//更新seekbar左上角的时间
				tv_music_play_currenttime.setText(MusicPlayerUtils
						.millisecondFormat(mediaPlayer.getCurrentPosition()));
			} else if (action.equals(MusicPlayerUtils.ui_update_ACTION)) {
				lrcShowView.setIndex(getLrcIndex());
				lrcShowView.invalidate();
				int a = mediaPlayer.getCurrentPosition()
						* activity_seekbar.getMax() / mediaPlayer.getDuration();
				activity_seekbar.setProgress(a);
			}
		}

	}

}
