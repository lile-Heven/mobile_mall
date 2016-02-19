package com.huayu.lile.broadcastreceiver;

import com.huayu.lile.music_player.MusicPlayActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InitLRC_Receiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		MusicPlayActivity musicPlayActivity=(MusicPlayActivity)context;
		if(musicPlayActivity==null){return ;}
		musicPlayActivity.initLrc();
		Log.d("tag", "execute---InitLRC_Receiver");
	}

}
