package com.huayu.lile.broadcastreceiver;

import com.huayu.lile.music_player.MusicPlayActivity;
import com.huayu.lile.service.MusicPlayService;
import com.huayu_lile.bean.MusicPlayerUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MusicNotification_Receive extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		MusicPlayService main=(MusicPlayService)context;
		int flag=intent.getIntExtra("flag", -1);
		//System.out.println("flag"+flag);
		
		switch (flag) {
		case 0://��һ�׼���
			//��һ�׼���
			if(MusicPlayService.CURRENT_MUSIC_ID>0){
				MusicPlayService.CURRENT_MUSIC_ID--;
			}else if(MusicPlayService.CURRENT_MUSIC_ID==0){
				MusicPlayService.CURRENT_MUSIC_ID=MusicPlayerUtils.MUSICLIST.size()-1;
			}
			//reconnectʱ�������˸��
			main.reconnect_data();
			main.play_music();
			
			break;
			
		case 1://���Ű�ť����
			//����/��ͣ��ť����
			if(MusicPlayService.isPlay_or_not==0){
				main.play_music();
				
			}else{
				main.pause_music();
				
			}
			
			break;
			
		case 2:
			//Toast.makeText(context, "��һ�׼���", 1).show();
			if(MusicPlayService.CURRENT_MUSIC_ID<MusicPlayerUtils.MUSICLIST.size()-1){
				//10-----9---8���²���++
				MusicPlayService.CURRENT_MUSIC_ID++;
			}else if(MusicPlayService.CURRENT_MUSIC_ID==MusicPlayerUtils.MUSICLIST.size()-1){
				MusicPlayService.CURRENT_MUSIC_ID=0;
			}
			//reconnectʱ�������˸��
			main.reconnect_data();
			main.play_music();
			
			
			break;

		default:
			break;
		}
		main.sendNotification();
	}
}
