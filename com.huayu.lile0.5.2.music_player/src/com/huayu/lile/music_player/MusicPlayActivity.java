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
	private List<LrcContent> lrcList = new ArrayList<LrcContent>(); // ��Ÿ���б����
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
				// ���MusicPlayActivity�����õ�ַ������������ظ�Service
				mysicPlayService.bind_MusicPlayActivity(MusicPlayActivity.this);
				(MusicPlayActivity.this).mediaPlayer = mysicPlayService.mediaPlayer;
				Log.d("tag",
						"ServiceConnection��󶨷���ɹ���" + mysicPlayService.toString());
				// ��service���ڲ������֣����û�����mainactivity�����ת�����activityʱ����һ��״̬���
				// reconnect_data��ͬʱ�������ڲ��ŵ�������ͣ��
				Log.e("---mysicPlayService.mediaPlayer", ""+mysicPlayService.mediaPlayer);
				if (mysicPlayService.mediaPlayer != null) {
					//8������play���룬0������listview����
					//��play������룬����Ҫreconnect
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
					//��mainactivity��bottombar���ֳ���
					sendBroadcast(new Intent(MusicPlayerUtils.main_bottombar_visible_ACTION));
					
					Log.d("tag", "execute---MusicPlayActivity__mysicPlayService.reconnect_data()");
				}
				//����musicplayactivity��ui������/��ͣͼ���seekbar��seekbar���Ͻ�ʱ�䣩
				sendMessageToupdate();
				
				
			} else {
				Log.e("ServiceConnection", "��󶨷���ʧ�ܣ�");
			}
		}

		/**
		 * �����ϴ���ʾʱ˵�ˣ��Ӵ��󶨵�ʱ�򣬲�������������������ServiceConnection�е�onServiceDisconnected
		 * ()����������������ǲ������õģ����ĵ���ʱ���ǵ�Service������������ʱ�������ڴ����Դ����ʱ���
		 * ��������Զ�̷�����������ԣ�����ڲ�ͬ�Ľ��������б���Ŀͻ��˷����ϵ��쳣ʧ�ܣ���ʧȥ�����ӣ�����ȡ�ص���
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO �Զ����ɵķ������
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_play);
				
		

		// ���˻�򿪵�ʱ�򣬰�Service
		bindService(new Intent(this, MusicPlayService.class), myServiceConn,
				Service.BIND_AUTO_CREATE);
		Log.d("tag", "---mysicPlayService" + mysicPlayService);
		
		// ע��㲥������_UI�ؼ�����
		registUIupdate_Receiver();
		// ע��LRC��ʼ��������
		registLRC_init_Receiver();
		

		bt_change_playmode = (Button) findViewById(R.id.bt_change_playmode);
		tv_music_play_wholetime = (TextView) findViewById(R.id.tv_music_play_wholetime);
		tv_music_play_currenttime = (TextView) findViewById(R.id.tv_music_play_currenttime);
		musicplay_pre = (ImageView) findViewById(R.id.musicplay_pre);
		musicplay_play_or_pause = (ImageView) findViewById(R.id.musicplay_play_or_pause);
		musicplay_next = (ImageView) findViewById(R.id.musicplay_next);
		activity_seekbar = (SeekBar) findViewById(R.id.activity_seekbar);
		lrcShowView = (LrcView) findViewById(R.id.lrcShowView);
		//��ʼ�������ͼ
		initLrc();
		
		handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				// ����activity���playpause��ťͼ��
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
						//ɸ�����ͬһ�׸�ʱ�����
						if(MusicPlayService.is_equals){
							//ʹ����󣬻ָ�is_equals��ȱʡֵ
							MusicPlayService.is_equals=false;
							return ;
						}
						// ע�⣡�������д�ǻ����0��progress/seekBar.getMax()*mediaPlayer.getDuration()
						int currentMusicTime = (progress
								* mediaPlayer.getDuration() / seekBar.getMax());

						//���������ֶ����ڵģ����ǻ����Զ����µ��ڵ�
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
	 * ��ʼ���������
	 */
	public void initLrc() {
		//new GetData_LrcList()��ͬʱ�� Ҳ��new lrclist
		GetData_LrcList getData_LrcList = new GetData_LrcList();
		// ��ȡ����ļ�������ļ��������Ƶ�ļ���ͬһ�ļ����£����ҳ��˺�׺��ͬ�⣬��ͬ��
		// ��������ҪUri.parse?
		try {
			getData_LrcList.readLRC(MusicPlayerUtils.MUSICLIST.get(
					MusicPlayService.CURRENT_MUSIC_ID).getData_Url());
		} catch (Exception e) {
			// TODO: handle exception
			getData_LrcList = new GetData_LrcList();
		}
			
			
		
		lrcList = getData_LrcList.getLrcList();
		if (lrcList.size() == 0) {
			Toast.makeText(this, "����û�и���ļ�", 1).show();
			
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
	 * ͨ����ǰmusic��currentTime��Ѱ�ҵ��˸����ʾ�ĸ�����ݵĽ��±�
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
				bt_change_playmode.setText("����ѭ��");
			} else if (MusicPlayerUtils.mediaPlayMode == 1) {
				MusicPlayerUtils.mediaPlayMode++;
				bt_change_playmode.setText("�������");
			} else {
				MusicPlayerUtils.mediaPlayMode = 0;
				bt_change_playmode.setText("˳�򲥷�");
			}
			break;
		case R.id.musicplay_pre:
			//��һ�׼���
			if(MusicPlayService.CURRENT_MUSIC_ID>0){
				MusicPlayService.CURRENT_MUSIC_ID--;
			}else if(MusicPlayService.CURRENT_MUSIC_ID==0){
				MusicPlayService.CURRENT_MUSIC_ID=MusicPlayerUtils.MUSICLIST.size()-1;
			}
			mysicPlayService.reconnect_data();
			mysicPlayService.play_music();
			
			// ����֪ͨ������Ϣ
			mysicPlayService.sendNotification();
			//����activity���play_or_pause
			sendMessageToupdate();

			break;
		case R.id.musicplay_play_or_pause:
			// ����/��ͣ��ť����
			if (mysicPlayService.isPlay_or_not == 0) {
				mysicPlayService.play_music();
				// ����activity���play_or_pause
				sendMessageToupdate();
			} else {
				mysicPlayService.pause_music();
				// ����activity���play_or_pause
				sendMessageToupdate();
			}
			// ����֪ͨ������Ϣ
			mysicPlayService.sendNotification();
			break;
		case R.id.musicplay_next:
			if(MusicPlayService.CURRENT_MUSIC_ID<MusicPlayerUtils.MUSICLIST.size()-1){
				//10-----9---8���²���++
				MusicPlayService.CURRENT_MUSIC_ID++;
			}else if(MusicPlayService.CURRENT_MUSIC_ID==MusicPlayerUtils.MUSICLIST.size()-1){
				MusicPlayService.CURRENT_MUSIC_ID=0;
			}
			mysicPlayService.reconnect_data();
			mysicPlayService.play_music();
			
			// ����֪ͨ������Ϣ
			mysicPlayService.sendNotification();
			//����activity���play_or_pause
			sendMessageToupdate();

			break;

		default:
			break;
		}
	}

	/**
	 ����musicplayActivity�����play_or_pauseͼ��
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
		//�ѵ���handler��ͨ���ر�
		handler_switch=false;
		// ���˻�رյ�ʱ�򣬽�����
		try {

			unbindService(myServiceConn);
			mysicPlayService = null;
			(MusicPlayActivity.this).mediaPlayer = null;
			mysicPlayService.unbind_MusicPlayActivity();
			
			unbindService(myServiceConn);

		} catch (Exception e) {
			Log.w("UnBindService", "�����쳣_������");
		}
		try {
			// ע��������
			unregisterReceiver(uiupdate_Receiver);
			unregisterReceiver(lrc_init_Receiver);
			Log.d("tag", "�ɹ�ע��ui���½�����");
		} catch (Exception e) {
			Log.d("UnregisterReceiver", "�����쳣_ע��ui���½�����");
		}
		
		Log.e("---MusicPlayActivityonDestroy", "success");

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	//ע��lrc��ʼ��������
	private void registLRC_init_Receiver() {
		IntentFilter filter = new IntentFilter(MusicPlayerUtils.lrc_init_ACTION);
		lrc_init_Receiver = new InitLRC_Receiver();
		registerReceiver(lrc_init_Receiver, filter);
	}
	//ע����¡�����/��ͣ���͡�seekbar���͡���ǰ������ʱ�䡱�ؼ��Ľ�����
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
				//����seekbar���Ͻǵ�ʱ��
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
