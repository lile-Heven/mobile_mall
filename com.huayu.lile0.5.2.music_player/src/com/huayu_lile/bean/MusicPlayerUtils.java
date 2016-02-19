package com.huayu_lile.bean;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.huayu.lile.music_player.MainActivity;
import com.huayu.lile.music_player.R;

public class MusicPlayerUtils {
	/** 点击开始播放后发送通知的广播action */
	public static String play_player_ACTION = "com.huayu.lile.sendNotification";
	public static String ui_update_ACTION = "com.huayu.lile.ui_update";
	public static String ui_update_time_left_of_seekbar_ACTION = "com.huayu.lile.ui_update_time_left_of_seekbar";
	public static String lrc_init_ACTION = "com.huayu.lile.lrc_init";
	public static String main_bottombar_init_ACTION = "com.huayu.lile.main_bottombar_init";
	public static String  main_bottombar_play_init_ACTION = "com.huayu.lile.main_bottombar_play_init";  
	public static String main_bottombar_visible_ACTION = "com.huayu.lile.main_bottombar_visible";  
	
	
	public static float tempY = 0;
	// 设置播放模式：0--循环顺序播放, 1--单曲循环 ,2--随机播放
	public static int mediaPlayMode = 0;
	public static ArrayList<MusicInfo> MUSICLIST;
	

	public static ArrayList<MusicInfo> getMusicList(Context context) {
		MUSICLIST = new ArrayList<MusicInfo>();
		ContentResolver contentResolver = context.getContentResolver();
		if (contentResolver != null) {
			Cursor cursor = contentResolver.query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
					null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

			while (cursor.moveToNext()) {

				String _id = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media._ID));
				int _count = cursor.getCount();
				Log.d("tag", "_count: "+_count);
				String album_id = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
				String title = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.TITLE));
				String singer = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.ARTIST));
				if ("<unknown>".equals(singer)) {
					singer = "未知艺术家";
				}
				String album = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.ALBUM));
				long size = cursor.getLong(cursor
						.getColumnIndex(MediaStore.Audio.Media.SIZE));
				long time = cursor.getLong(cursor
						.getColumnIndex(MediaStore.Audio.Media.DURATION));
				String data_url = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.DATA));
				String name = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
				//获得专辑图片
				Bitmap bmp=getALBUM_ART1(context, album_id);
				if(bmp==null){
					Log.d("tag", "MusicPlayerUtils没有获得相应的专辑图片");
					bmp=BitmapFactory.decodeResource(context.getResources(), R.drawable.item_imageicon);
				}
				String sbr = name.substring(name.length() - 3, name.length());

				if (sbr.equals("mp3")) {
					MusicInfo m = new MusicInfo(_id,_count,album_id,title, singer, album, data_url,
							size, time, name, bmp);

					MUSICLIST.add(m);
					Log.d("___Success_MusciList", "添加一个音频文件成功");
				}

			}
			cursor.close();
			Log.d("___Success_cursor", "cursor成功关闭");
		}

		if (context instanceof MainActivity) {
			((MainActivity) context).handler_updatelv.sendEmptyMessage(0);
		}
		Log.d("tag", "执行一次：MusicPlayerUtils.getMusicList(MainActivity.this);");
		//催促一次垃圾回收
		System.gc();
		return MUSICLIST;
	}

	/**
	 * 使时间的long毫秒形式转变成"mm:ss"
	 * @param l
	 * @return
	 */
	public static String millisecondFormat(long l) {
		SimpleDateFormat a = new SimpleDateFormat("mm:ss");
		return a.format(l - TimeZone.getDefault().getRawOffset());
	}
	/**
	 * 获取专辑图片的方法1
	 * @param mainActivity
	 * @param album_id
	 * @return
	 */
	private static Bitmap getALBUM_ART1(Context context,String album_id){
		
		Cursor cursor=context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, 
				new String[]{MediaStore.Audio.Albums._ID,MediaStore.Audio.Albums.ALBUM_ART}, 
				MediaStore.Audio.Albums._ID+"=?", 
				new String[]{album_id},
				null);
		//无论有没找到信息，cursor都不是空的。所以不能用==null来作为条件，而应该是moveToFirst
		if(cursor.moveToFirst()){
			Log.d("tag", "execute_cursor.moveToFirst()");
			String str_uri=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
			
			
			Bitmap bmp=BitmapFactory.decodeFile(str_uri);
			if(bmp!=null){
				Log.d("tag", "bmp!=null");
				return bmp;
			}else{
				return null;
			}
		}
		
		return null;
		
	}
	/**
	 * 获取专辑图片方法二
	 * @param mainActivity
	 * @param album_id
	 * @return
	 */
	private static Bitmap getALBUM_ART2(Context context,String album_id){
	Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),
			(long)Integer.valueOf(album_id));
	ParcelFileDescriptor pfd;
	FileDescriptor fd = null;
	try {
		pfd = context.getContentResolver().openFileDescriptor(uri, "r");
		if(pfd != null) {
			fd = pfd.getFileDescriptor();
		}
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	Bitmap bmp=BitmapFactory.decodeFileDescriptor(fd);
	
	return bmp;
	
	}
	/**
	 * 当获取不到album_id时
	 * @param mainActivity
	 * @param album_id
	 * @return
	 */
	private static Bitmap getALBUM_ART3(Context context,String _id){
		
		FileDescriptor fd = null;
			Uri uri = Uri.parse("content://media/external/audio/media/"
					+ _id + "/albumart");
			ParcelFileDescriptor pfd;
			try {
				pfd = context.getContentResolver().openFileDescriptor(uri, "r");
				if(pfd != null) {
					fd= pfd.getFileDescriptor();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			Bitmap bmp=BitmapFactory.decodeFileDescriptor(fd);
		return bmp;

	}
	/**
	 * 获取专辑图片方法4
	 * @param mainActivity
	 * @param _id
	 * @return
	 */
	/**
	 * 获取专辑图片方法4
	 * @param mainActivity
	 * @param _id
	 * @return
	 */
	private static Bitmap getALBUM_ART4(Context context,String _id){
		FileDescriptor fd = null;
		Uri uri = Uri.parse("content://media/external/audio/media/"
				+ _id + "/albumart");
		Bitmap bmp = null;
		try {
			InputStream in = context.getContentResolver().openInputStream(uri);
			 bmp=BitmapFactory.decodeStream(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			 
		}
		
		return bmp;
	
	}
	 /** 
     * 获取专辑封面位图对象 
     * @param context 
     * @param song_id 
     * @param album_id 
     * @param allowdefalut 
     * @return 
     */  
    public static Bitmap getArtwork(Context context, long song_id, long album_id, boolean allowdefalut, boolean small){  
        
    	if(album_id < 0) {  
            if(song_id < 0) {  
                Bitmap bm = getArtworkFromFile(context, song_id, -1);  
                if(bm != null) {  
                    return bm;  
                }  
            }  
            if(allowdefalut) {  
                return null;  
            }  
            return null;  
        }  
        ContentResolver res = context.getContentResolver();  
        Uri uri = ContentUris.withAppendedId(albumArtUri, album_id);  
        if(uri != null) {  
            InputStream in = null;  
            try {  
                in = res.openInputStream(uri);  
                BitmapFactory.Options options = new BitmapFactory.Options();  
                //先制定原始大小  
                options.inSampleSize = 1;  
                //只进行大小判断  
                options.inJustDecodeBounds = true;  
                //调用此方法得到options得到图片的大小  
                BitmapFactory.decodeStream(in, null, options);  
                /** 我们的目标是在你N pixel的画面上显示。 所以需要调用computeSampleSize得到图片缩放的比例 **/  
                /** 这里的target为800是根据默认专辑图片大小决定的，800只是测试数字但是试验后发现完美的结合 **/  
                if(small){  
                    options.inSampleSize = computeSampleSize(options, 40);  
                } else{  
                    options.inSampleSize = computeSampleSize(options, 600);  
                }  
                // 我们得到了缩放比例，现在开始正式读入Bitmap数据  
                options.inJustDecodeBounds = false;  
                options.inDither = false;  
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;  
                in = res.openInputStream(uri);  
                return BitmapFactory.decodeStream(in, null, options);  
            } catch (FileNotFoundException e) {  
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);  
                if(bm != null) {  
                    if(bm.getConfig() == null) {  
                        bm = bm.copy(Bitmap.Config.RGB_565, false);  
                        if(bm == null && allowdefalut) {  
                            return null;  
                        }  
                    }  
                } else if(allowdefalut) {  
                    bm = null;  
                }  
                return bm;  
            } finally {  
                try {  
                    if(in != null) {  
                        in.close();  
                    }  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return null;  
    }  
    /** 
     * 对图片进行合适的缩放 
     * @param options 
     * @param target 
     * @return 
     */  
    public static int computeSampleSize(Options options, int target) {  
        int w = options.outWidth;  
        int h = options.outHeight;  
        int candidateW = w / target;  
        int candidateH = h / target;  
        int candidate = Math.max(candidateW, candidateH);  
        if(candidate == 0) {  
            return 1;  
        }  
        if(candidate > 1) {  
            if((w > target) && (w / candidate) < target) {  
                candidate -= 1;  
            }  
        }  
        if(candidate > 1) {  
            if((h > target) && (h / candidate) < target) {  
                candidate -= 1;  
            }  
        }  
        return candidate;  
    }  
    /** 
     * 从文件当中获取专辑封面位图 
     * @param context 
     * @param songid 
     * @param albumid 
     * @return 
     */  
    private static Bitmap getArtworkFromFile(Context context, long songid, long albumid){  
        Bitmap bm = null;  
        if(albumid < 0 && songid < 0) {  
            throw new IllegalArgumentException("Must specify an album or a song id");  
        }  
        try {  
            BitmapFactory.Options options = new BitmapFactory.Options();  
            FileDescriptor fd = null;  
            if(albumid < 0){  
                Uri uri = Uri.parse("content://media/external/audio/media/"  
                        + songid + "/albumart");  
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");  
                if(pfd != null) {  
                    fd = pfd.getFileDescriptor();  
                }  
            } else {  
                Uri uri = ContentUris.withAppendedId(albumArtUri, albumid);  
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");  
                if(pfd != null) {  
                    fd = pfd.getFileDescriptor();  
                }  
            }  
            options.inSampleSize = 1;  
            // 只进行大小判断  
            options.inJustDecodeBounds = true;  
            // 调用此方法得到options得到图片大小  
            BitmapFactory.decodeFileDescriptor(fd, null, options);  
            // 我们的目标是在800pixel的画面上显示  
            // 所以需要调用computeSampleSize得到图片缩放的比例  
            options.inSampleSize = 100;  
            // 我们得到了缩放的比例，现在开始正式读入Bitmap数据  
            options.inJustDecodeBounds = false;  
            options.inDither = false;  
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;  
              
            //根据options参数，减少所需要的内存  
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        }  
        return bm;  
    }  
  //获取专辑封面的Uri  
    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
	

}
