package com.huayu_lile.bean;

import android.graphics.Bitmap;
import android.media.MediaPlayer;

/**
 单个音频的信息
 */
public class MusicInfo {
	private String title;
	private String singer;
	private String album;
	private String data_url;
	private long size;
	private long time;
	private String name;
	private String album_id;
	private String _id;
	private int _count;
	private Bitmap bmp;
	
	/**
	 * @param title
	 * @param singer
	 * @param album
	 * @param url
	 * @param size
	 * @param time
	 * @param name
	 * @param bmp 
	 */
	public MusicInfo(String _id,int _count,String album_id,String title, String singer, String album, String data_url,
			long size, long time, String name, Bitmap bmp) {
		super();
		this._id=_id;
		this._count=_count;
		this.title = title;
		this.singer = singer;
		this.album = album;
		this.data_url = data_url;
		this.size = size;
		this.time = time;
		this.name = name;
		this.album_id=album_id;
		this.bmp=bmp;
	}
	public Bitmap getBmp() {
		return bmp;
	}
	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public int get_count() {
		return _count;
	}
	public void set_count(int _count) {
		this._count = _count;
	}
	public String getAlbum_id() {
		return album_id;
	}
	public void setAlbum_id(String album_id) {
		this.album_id = album_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}
	
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	
	public String getData_Url() {
		return data_url;
	}
	public void setData_Url(String data_url) {
		this.data_url = data_url;
	}
	
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	
}
