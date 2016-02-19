package com.huayu.lile.music_player;


import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class MusicInternet extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_internet);
		String str_url=
				"http://10.0.2.2:8080/web.com.huayu.lile.music_player/server_storage"
				+"/123.jpg";
		InputStream input;
		try {
			input=downloadFileFromServer(str_url);
			Bitmap bmp=BitmapFactory.decodeStream(input);
			ImageView iv1 = (ImageView)findViewById(R.id.iv1_music_internet);
			iv1.setImageBitmap(bmp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private InputStream downloadFileFromServer(String str_url) throws Exception{
		// TODO Auto-generated method stub
		URL url = new URL(str_url);
		URLConnection conn = url.openConnection();
		InputStream input = conn.getInputStream();
		return input;
	}
	
}
