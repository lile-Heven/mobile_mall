package com.huayu_lile.bean;

import java.util.ArrayList;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huayu.lile.music_player.MainActivity;
import com.huayu.lile.music_player.R;

public class MusicShowAdapter extends BaseAdapter {

	MainActivity mainActivity;
	ArrayList<MusicInfo> musicList;
	
	
	private int noALBUM_ART=-1;
	public MusicShowAdapter(MainActivity mainActivity,
			ArrayList<MusicInfo> musicList) {
		this.mainActivity=mainActivity;
		this.musicList=musicList;
	}

	@Override
	public int getCount() {
		//if(musicList==null){return 0;}
		return musicList.size();
	}

	@Override
	public Object getItem(int position) {
		return musicList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView=LayoutInflater.from(mainActivity).inflate(R.layout.item_music_listview, null);
		}
		Holder holder=new Holder();
		holder.iv_item_music_pic=(ImageView) convertView.findViewById(R.id.iv_item_music_pic);
		holder.tv_item_music_name=(TextView) convertView.findViewById(R.id.tv_item_music_name);
		holder.tv_item_music_album=(TextView)convertView.findViewById(R.id.tv_item_music_album);
		holder.tv_item_music_duration=(TextView) convertView.findViewById(R.id.tv_item_music_duration);
		holder.tv_item_music_name.setText(musicList.get(position).getTitle()+"-"+musicList.get(position).getSinger());
		holder.tv_item_music_album.setText("专辑-"+musicList.get(position).getAlbum());
		holder.tv_item_music_duration.setText(MusicPlayerUtils.millisecondFormat(musicList.get(position).getTime()));
		
		
		//getView还未执行时，先赋予默认图片
		if(position==-1){
			holder.iv_item_music_pic.setImageResource(R.drawable.item_imageicon);
		}else{
			holder.iv_item_music_pic.setImageBitmap(musicList.get(position).getBmp());
			
		}
		Log.e("---AdapterGetView", "1");
		
		return convertView;
	}
	
	class Holder{
		ImageView iv_item_music_pic;
		TextView tv_item_music_name;
		TextView tv_item_music_duration;
		TextView tv_item_music_album;
	}
	
}
