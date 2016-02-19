package com.huayu.lile.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.animation.AnimationUtils;

import com.huayu.lile.music_player.MainActivity;
import com.huayu.lile.music_player.R;
import com.huayu.lile.service.MusicPlayService;
import com.huayu_lile.bean.MusicPlayerUtils;

public class Receiver_MainBottomBar extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		
		MainActivity mainActivity=(MainActivity)context;
		String action = intent.getAction();
		int action_number=0;
		if(action.endsWith(MusicPlayerUtils.main_bottombar_visible_ACTION)){
			mainActivity.layout_bottombar.setVisibility(android.view.View.VISIBLE);
			return ;
		}
		if(action==MusicPlayerUtils.main_bottombar_play_init_ACTION){
			//������play����pause��ͼƬ����
			action_number=1;
		}else{
			//��������bottombar
			action_number=2;
		}
		switch (action_number) {
		case 1:
			//==0Ϊ��ͣ������Ϊͼ��play
			if(MusicPlayService.isPlay_or_not==0){
				//������ͣ
				mainActivity.main_bottom_play.setImageResource(R.drawable.main_play);
				mainActivity.main_bottom_image.clearAnimation();
			}else if(MusicPlayService.isPlay_or_not==1){
				//���ڲ���
				mainActivity.main_bottom_play.setImageResource(R.drawable.main_pause);
				mainActivity.main_bottom_image.setAnimation(
						AnimationUtils.loadAnimation(mainActivity, R.anim.rotate_music_icon));
			
			}
			
			break;
		case 2:
			//���ר��ͼƬ��Ϊ�գ�����ר��ͼƬ
			if(MusicPlayerUtils.MUSICLIST.
					get(MusicPlayService.CURRENT_MUSIC_ID).getBmp()!=null){
				mainActivity.main_bottom_image.setImageBitmap(MusicPlayerUtils.MUSICLIST.
					get(MusicPlayService.CURRENT_MUSIC_ID).getBmp());
				mainActivity.main_bottom_image.setAnimation(
						AnimationUtils.loadAnimation(mainActivity, R.anim.rotate_music_icon));
			}else{
				mainActivity.main_bottom_image.setAnimation(
						AnimationUtils.loadAnimation(mainActivity, R.anim.rotate_music_icon));
			}
			
			//������������
			if(MusicPlayerUtils.MUSICLIST.
					get(MusicPlayService.CURRENT_MUSIC_ID).getTitle()!=null){
				mainActivity.main_bottom_tv_name.setText(MusicPlayerUtils.MUSICLIST.
					get(MusicPlayService.CURRENT_MUSIC_ID).getTitle());
			}
			//���ô����ֵĸ���
			if(MusicPlayerUtils.MUSICLIST.
					get(MusicPlayService.CURRENT_MUSIC_ID).getSinger()!=null){
				mainActivity.main_bottom_tv_singer.setText(MusicPlayerUtils.MUSICLIST.
					get(MusicPlayService.CURRENT_MUSIC_ID).getSinger());
			}
			
			break;

		default:
			break;
		}
		
		
		
		
		
	}

}
