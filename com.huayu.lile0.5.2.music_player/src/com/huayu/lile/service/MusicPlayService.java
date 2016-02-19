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
 * ���ط���Local Service��:MusicPlayService
 * 
 * @author EDUASK
 * 
 */
public class MusicPlayService extends Service {
	// ֪ͨ-����
	Builder builder;
	/** ��ǰ�������� */
	// -1�����app�ʼ��ʼ��ʱ
	public static int CURRENT_MUSIC_ID = -1;
	// 8������play���룬0������listview����
	public static int flags = -1;
	// �Ƿ�������ͬһ�׸�
	public static boolean is_equals = false;
	public MediaPlayer mediaPlayer;
	public static MusicNotification_Receive broadCastReceive;
	// ��ʼ��Ϊ-1��0������ͣ��1������
	public static int isPlay_or_not = -1;
	// 1����������δ�ͷţ�0�����ͷ�
	public static int isRelease_or_not = 1;

	// public ArrayList<MusicInfo> musicList = MusicPlayerUtils.MUSICLIST;
	public MyBinder myBinder = new MyBinder();

	/** ����seekbar�����߳� */
	public MyThread thread_seekbar;

	public class MyThread extends Thread {
		public boolean flag = true;

		@Override
		public void run() {
			// ���Ĳ���/��ͣ��ť��ͼ��
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
		// ����mediaplayer�������ݵ�ͬʱ��ˢ��thread
		if (thread_seekbar != null) {
			thread_seekbar.flag = false;
			thread_seekbar = new MyThread();

		} else {
			thread_seekbar = new MyThread();
		}

		// ��ʼˢ��seekbar
		if (thread_seekbar != null) {
			thread_seekbar.start();
			Log.e("thread_seekbar", "success_thread_seekbar!=null");
		}

	}

	// �Զ���һ��Binder
	public class MyBinder extends Binder {
		public MusicPlayService getService() {
			return MusicPlayService.this;
		}

	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// ʵ����һ��֪ͨ��builder
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
			case TelephonyManager.CALL_STATE_IDLE: /* ���κ�״̬ʱ(�Ҷ�) */
				// ɸ��֮ǰ���ڲ��ŵ�״̬
				if (isPlay_or_not != 0) {
					return;
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: /* ����绰ʱ */

				break;
			case TelephonyManager.CALL_STATE_RINGING: /* �绰����ʱ */
				// ɸ��֮ǰ������ͣ��״̬
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

	// ����֪ͨ
	public void sendNotification() {

		// ���֪ͨ������
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// ͨ��NotificationManager������һ��֪ͨ
		manager.notify("һ��֪ͨ", 0, notification_create());
		// ���ֲ��Ż���ͣ���л�

	}

	/** android4.1֮�� */
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
		//���֪ͨ����ת��MainActivity
		Intent intent = new Intent(this,MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		builder.setContentIntent(pendingIntent);
		
		builder.setLights(0xff889900, 500, 300);

	}

	// ����һ��֪ͨ
	private Notification notification_create() {
		/*
		 * ????����-RemoteViews--Linearlayout/Relaytivilayout...--����ʹ�ø��ӿؼ�-ListView
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

		// ��֪ͨ����Ŀؼ���Ӽ���
		addListener(contentView);

		// ͨ��builder.build()������һ��notification
		builder.setContent(contentView);
		Notification notification = builder.build();
		//notification.flags = Notification.FLAG_NO_CLEAR;
		return notification;

	}

	// ��ʼ��һ��mediaPlayer��������ϵһ����Ƶ�ļ�
	private void init_music_one() {
		// TODO �Զ����ɵķ������
		mediaPlayer = new MediaPlayer();
		// reconnect_data();
	}

	public void reconnect_data() {

		// ɸ��musiclist.sizeΪ0��״̬
		if (MusicPlayerUtils.MUSICLIST == null) {
			Toast.makeText(this, "musicListΪ�գ����ܻ�δ��ʼ��", 1).show();
			return;
		} else if (MusicPlayerUtils.MUSICLIST.size() == 0) {
			Toast.makeText(this, "musicList.size()==0", 1).show();
			return;
		}
		// ����mediaplayer�������ݵ�ͬʱ��ˢ��thread
		if (thread_seekbar != null) {
			thread_seekbar.flag = false;
			thread_seekbar = new MyThread();

		} else {
			thread_seekbar = new MyThread();
		}
		// ����������mediaplayer����ʲô״̬��������ǿ��reset�ģ������ǡ�����
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
			// TODO �Զ����ɵ� catch ��
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO �Զ����ɵ� catch ��
			e1.printStackTrace();
		}

		// ��ʼ������ģʽ
		mediaPlayer.setLooping(false);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		mediaPlayer.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				if (MusicPlayService.CURRENT_MUSIC_ID < MusicPlayerUtils.MUSICLIST
						.size() - 1) {
					// 10-----9---8���²���++
					MusicPlayService.CURRENT_MUSIC_ID++;
				} else if (MusicPlayService.CURRENT_MUSIC_ID == MusicPlayerUtils.MUSICLIST
						.size() - 1) {
					MusicPlayService.CURRENT_MUSIC_ID = 0;
				}
				reconnect_data();
				play_music();
				// ���¸����ͼ
				sendBroadcast(new Intent(MusicPlayerUtils.lrc_init_ACTION));
				// ����֪ͨ������Ϣ
				sendNotification();
				// ����activity���play_or_pause
				sendMessageToupdate();
				// ����MainActivity�����MainBottomBar
				sendBC_updateMainBottomBar();
				// mp.start();
				// isPlay_or_not = 1;
				Toast.makeText(getApplicationContext(), "���Ź��̳����˴��󣬲�����һ��...", 1)
						.show();
				Log.e("OnErrorListener", "������OnErrorListener");
				return true;
			}
		});
		// ���õ����������Ž�����ļ���
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// ����ѭ�����ŵ����
				if (MusicPlayerUtils.mediaPlayMode == 1) {
					// ����ѭ������
					mediaPlayer.setLooping(true);
					mediaPlayer.start();
					// �ָ�����ģʽ���Ա�ÿ�β�����Ϻ���ִ��OnCompletionListener
					mediaPlayer.setLooping(false);
				}

				// ���ѭ�����ŵ����
				if (MusicPlayerUtils.mediaPlayMode == 2) {
					isPlay_or_not = 0;
					sendMessageToupdate();
					double a = MusicPlayerUtils.MUSICLIST.size()
							* Math.random();
					int b = (int) a;
					MusicPlayService.CURRENT_MUSIC_ID = b;
					// *****��reconnect֮ǰ����reset�������˵ݹ�*****
					mediaPlayer.reset();
					reconnect_data();
					play_music();
					// ���¸����ͼ
					sendBroadcast(new Intent(MusicPlayerUtils.lrc_init_ACTION));
					// ����֪ͨ������Ϣ
					sendNotification();
					// ����activity���play_or_pause
					sendMessageToupdate();
					// ����MainActivity�����MainBottomBar
					sendBC_updateMainBottomBar();

				}

				// ˳��ѭ�����ŵ����
				if (MusicPlayerUtils.mediaPlayMode == 0) {
					isPlay_or_not = 0;
					sendMessageToupdate();
					// Toast.makeText(context, "��һ�׼���", 1).show();
					if (MusicPlayService.CURRENT_MUSIC_ID < MusicPlayerUtils.MUSICLIST
							.size() - 1) {
						// 10-----9---8���²���++
						MusicPlayService.CURRENT_MUSIC_ID++;
					} else if (MusicPlayService.CURRENT_MUSIC_ID == MusicPlayerUtils.MUSICLIST
							.size() - 1) {
						MusicPlayService.CURRENT_MUSIC_ID = 0;
					}
					// *****��reconnect֮ǰ����reset�������˵ݹ�*****
					mediaPlayer.reset();
					reconnect_data();
					play_music();
					// ���¸����ͼ
					sendBroadcast(new Intent(MusicPlayerUtils.lrc_init_ACTION));
					// ����֪ͨ������Ϣ
					sendNotification();
					// ����activity���play_or_pause
					sendMessageToupdate();
					// ����MainActivity�����MainBottomBar
					sendBC_updateMainBottomBar();

				}

			}
		});

		// ��ʼ�������ͼ
		sendBroadcast(new Intent(MusicPlayerUtils.lrc_init_ACTION));
		// ����֪ͨ������Ϣ
		sendNotification();
		// ����MusicPlayactivity���play_or_pause
		sendMessageToupdate();
		// ����MainActivity���������MainBottomBar
		sendBC_updateMainBottomBar();

	}

	private void mediaplayer_release() {
		// TODO �Զ����ɵķ������
		if (isRelease_or_not == 1) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			isRelease_or_not = 0;
			Log.e("---mediaplayer_release", "success");
		}
	}

	/** ���֪ͨ����ť�ļ��� */
	private void addListener(RemoteViews contentView) {

		/** ������Ӧ��app */
		Intent appIntent = new Intent(Intent.ACTION_MAIN);
		// appIntent.setAction(Intent.ACTION_MAIN);
		appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// appIntent.setComponent(new ComponentName(this.getPackageName(),
		// this.getPackageName() + "." + this.getLocalClassName()));
		appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// �ؼ���һ������������ģʽ
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				appIntent, 0);
		contentView.setOnClickPendingIntent(R.id.notification_layout,
				contentIntent);

		/** ��һ�׼��� */
		Intent music_pre_intent = new Intent(
				MusicPlayerUtils.play_player_ACTION);
		music_pre_intent.putExtra("flag", 0);// ��һ�װ�ť����-flag
		PendingIntent music_pre_pendingIntent = PendingIntent.getBroadcast(
				this, 0, music_pre_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(R.id.notification_music_pre,
				music_pre_pendingIntent);

		/** ���Ű�ť�ļ��� */
		/* ��ͼ */
		Intent music_player_intent = new Intent(
				MusicPlayerUtils.play_player_ACTION);
		music_player_intent.putExtra("flag", 1);// ���Ű�ť
		PendingIntent music_player_pendingIntent = PendingIntent
				.getBroadcast(this, 1, music_player_intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(
				R.id.notification_music_play_or_pause,
				music_player_pendingIntent);

		/** ��һ�׼��� */
		Intent music_next_intnet = new Intent(
				MusicPlayerUtils.play_player_ACTION);
		music_next_intnet.putExtra("flag", 2);// ��һ�װ�ť����-flag
		PendingIntent music_next_pendingIntent = PendingIntent.getBroadcast(
				this, 2, music_next_intnet, PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(R.id.notification_music_next,
				music_next_pendingIntent);

	}

	// ��������
	public void play_music() {
		// ɸ��֮ǰ���ڲ��ŵ�״̬
		if (isPlay_or_not != 0) {
			return;
		}
		// ֮ǰ������ͣ
		Log.e("error38", "before");
		try {
			mediaPlayer.start();
		} catch (Throwable e) {
			e.printStackTrace();
			// Toast.makeText(context, "��һ�׼���", 1).show();
			if (MusicPlayService.CURRENT_MUSIC_ID < MusicPlayerUtils.MUSICLIST
					.size() - 1) {
				// 10-----9---8���²���++
				MusicPlayService.CURRENT_MUSIC_ID++;
			} else if (MusicPlayService.CURRENT_MUSIC_ID == MusicPlayerUtils.MUSICLIST
					.size() - 1) {
				MusicPlayService.CURRENT_MUSIC_ID = 0;
			}
			reconnect_data();
			// ����֪ͨ����Ϣ�����¸���ͼ���Լ������ȸ�����Ϣ��
			sendNotification();
			play_music();
			// ���񲶻���Ŷ������
			Log.e("Throwablecatch", "success");
			return;
		}
		Log.e("error38", "after");
		// ��ʼˢ��seekbar
		if (thread_seekbar != null) {
			thread_seekbar.start();
			Log.e("thread_seekbar", "success_thread_seekbar!=null");
		}
		isPlay_or_not = 1;
		// ���Ĳ���/��ͣ��ť��ͼ��
		sendMessageToupdate();
		// ����֪ͨ����Ϣ�����²��ţ���ͣ��ť��ͼ�꣩
		sendNotification();
		// ������MainActivity�����MainBottomBar��play�Ͷ���
		sendBC_updateMainBottomBar_Play();
	}

	private void sendBC_updateMainBottomBar_Play() {
		// TODO �Զ����ɵķ������
		sendBroadcast(new Intent(
				MusicPlayerUtils.main_bottombar_play_init_ACTION));

	}

	// ��ͣ����
	public void pause_music() {
		// ɸ��֮ǰ������ͣ��״̬
		if (isPlay_or_not != 1) {
			return;
		}
		// ֮ǰ���ڲ���
		Log.e("---isplaying_pause", "" + mediaPlayer.isPlaying());
		mediaPlayer.pause();
		if (thread_seekbar != null) {
			thread_seekbar.flag = false;
			// ������ı���flag��ֵ����ʱ���߳���Ȼ��already started״̬
			// �����̲߳����ڹر��̵߳ĸ���߳�������run����൱���Զ������ˡ�
			thread_seekbar = null;
			thread_seekbar = new MyThread();
		}
		isPlay_or_not = 0;
		// ���Ĳ���/��ͣ��ť��ͼ��
		sendMessageToupdate();
		// ����֪ͨ����Ϣ�����²��ţ���ͣ��ť��ͼ�꣩
		sendNotification();

		// ������MainActivity�����MainBottomBar��play�Ͷ���
		sendBC_updateMainBottomBar_Play();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.e("---ServiceonStartCommand", "success");
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * ע�⣡�����ѹ�ʱ���������滻ΪonStartCommand
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		Log.e("---ServiceonStart", "success");
		super.onStart(intent, startId);
	}

	/**
	 * �󶨷���
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		Log.e("---MyBinder", myBinder.toString());
		return myBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO �Զ����ɵķ������
		/*
		 * if(thread_seekbar!=null){ thread_seekbar.flag=false;
		 * thread_seekbar.interrupt(); thread_seekbar=null; }
		 */
		Log.e("---ServiceonUnbind", "success");
		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO �Զ����ɵķ������
		Log.e("---ServiceonRebind", "success");
		super.onRebind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// ע��֪ͨ����֪ͨ:TAG����ֵ�����֮ǰ��֪ͨʱ��Tag��ͬ��idҲҪ��ͬ�������岻��֪ͨ�������ڵڶ����������壬Ŀǰδ֪��
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.cancel("һ��֪ͨ", 0);
		// �ͷ�mediaplayer
		mediaplayer_release();
		isPlay_or_not = -1;
		// ע��֮ǰ��̬ע��㲥������
		if (broadCastReceive != null) {
			unregisterReceiver(broadCastReceive);
		}
		// ע��ui�������߳�
		if (thread_seekbar != null) {
			thread_seekbar.flag = false;
			thread_seekbar.destroy();
		}
		Log.e("ServiceonDestroy", "success");
	}

	public void sendMessageToupdate() {
		if (musicPlayActivity != null) {
			// ���²���/��ͣͼ��
			musicPlayActivity.sendMessageToupdate();
		}
	}

	public void sendBC_updateMainBottomBar() {
		sendBroadcast(new Intent(MusicPlayerUtils.main_bottombar_init_ACTION));
	}

	/** ��̬ע��notification���͵Ĺ㲥�Ľ����� */
	private void regist() {
		broadCastReceive = new MusicNotification_Receive();
		IntentFilter filter = new IntentFilter(
				MusicPlayerUtils.play_player_ACTION);
		registerReceiver(broadCastReceive, filter);

	}

	public void sendMessageToupdate_seekbar_lrc() {
		// TODO Auto-generated method stub
		if (musicPlayActivity != null) {

			// ui_update_action���Ǹ���seekbar��lrcshowview
			Intent intent = new Intent(MusicPlayerUtils.ui_update_ACTION);
			intent.putExtra("SeekBar_currentPosition",
					mediaPlayer.getCurrentPosition());
			sendBroadcast(intent);
			// ����Ǹ���seekbar���Ͻǵ�ʱ��
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
