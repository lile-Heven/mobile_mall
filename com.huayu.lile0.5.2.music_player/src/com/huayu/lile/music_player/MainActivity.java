package com.huayu.lile.music_player;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huayu.lile.broadcastreceiver.Receiver_MainBottomBar;
import com.huayu.lile.login.LoginRegistActivity;
import com.huayu.lile.service.MusicPlayService;
import com.huayu_lile.bean.MusicPlayerUtils;
import com.huayu_lile.bean.MusicShowAdapter;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;



/**
 * �㲥��֪ͨû�б�Ȼ��ϵ 1.0���԰� MediaPlayer-ͨ��֪ͨ���������ֵĲ��� ���-����
 * 
 * @author pzp
 * 
 */
public class MainActivity extends Activity implements OnClickListener {
	TextView jump_to_musicplay,tv_tab1,tv_tab3,tv_tab4;
	 private static boolean isExit = false;

	private ListView listview_musiclist;

	private Button bt_scan_mediafile;

	private MusicShowAdapter adapter;
	Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };
	
	public Handler handler_updatelv=new Handler(new Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			Log.e("---handler_updatelv", "execute");
			adapter=new MusicShowAdapter(MainActivity.this,MusicPlayerUtils.MUSICLIST);
    		listview_musiclist.setAdapter(adapter);
			return true;
		}
	});
    	public Handler handler_animation=new Handler(new Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			Log.e("---handler_animation", "execute");
			
			return true;
		}
	});

		private View aaa;

		private TextView unlogin;
		public ImageView main_bottom_image;
		public TextView main_bottom_tv_name;
		public TextView main_bottom_tv_singer;
		public ImageView main_bottom_pre;
		public ImageView main_bottom_next;
		public ImageView main_bottom_play;
		private Receiver_MainBottomBar receiver_MainBottomBar;
		public  LinearLayout layout_bottombar;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		// ��ʼ��slidingmenu  
        SlidingMenu menu = new SlidingMenu(this);  
        menu.setMode(SlidingMenu.LEFT);  
        // ���ô�����Ļ��ģʽ  
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);  
        menu.setShadowWidthRes(R.dimen.shadow_width);  
        menu.setShadowDrawable(R.drawable.shadow);  
  
        // ���û����˵���ͼ�Ŀ��  
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);  
        // ���ý��뽥��Ч����ֵ  
        menu.setFadeDegree(0.35f);  
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);  
        //Ϊ�໬�˵����ò���  
        menu.setMenu(R.layout.layout_menu); 
       
        unlogin = (TextView)menu.findViewById(R.id.unlogin);
        unlogin.setOnClickListener(this);
        //��ʼ��activity
        init();
		
	}

	private void init() {

		startService(new Intent(this,MusicPlayService.class));
		jump_to_musicplay=(TextView) findViewById(R.id.jump_to_musicplay);
		jump_to_musicplay.setOnClickListener(this);
		tv_tab1=(TextView)findViewById(R.id.tv_tab1);
		tv_tab1.setOnClickListener(this);
		//tv_tab1.requestFocus();
		tv_tab3=(TextView)findViewById(R.id.tv_tab3);
		tv_tab3.setOnClickListener(this);
		tv_tab4=(TextView)findViewById(R.id.tv_tab4);
		tv_tab4.setOnClickListener(this);
		
		//����mainbottombar�����Ԫ��
		layout_bottombar=(LinearLayout)findViewById(R.id.layout_bottombar);;
		//�����gone�����޷��ָ���
		if(MusicPlayService.CURRENT_MUSIC_ID==-1){
			layout_bottombar.setVisibility(android.view.View.INVISIBLE);
		}
		main_bottom_image=(ImageView)findViewById(R.id.main_bottom_image);
		main_bottom_tv_name=(TextView)findViewById(R.id.main_bottom_tv_name);
		main_bottom_tv_singer=(TextView)findViewById(R.id.main_bottom_tv_singer);
		main_bottom_next=(ImageView)findViewById(R.id.main_bottom_next);
		main_bottom_next.setOnClickListener(this);
		main_bottom_play=(ImageView)findViewById(R.id.main_bottom_play);
		main_bottom_play.setOnClickListener(this);
		main_bottom_pre=(ImageView)findViewById(R.id.main_bottom_pre);
		main_bottom_pre.setOnClickListener(this);
		regist_Receiver_MainBottomBar();
		
		bt_scan_mediafile=(Button)findViewById(R.id.bt_scan_mediafile);
		bt_scan_mediafile.setOnClickListener(this);
		listview_musiclist=(ListView)findViewById(R.id.listview_musiclist);
		
		
		
		if(MusicPlayerUtils.MUSICLIST!=null){
		adapter=new MusicShowAdapter(this,MusicPlayerUtils.MUSICLIST);
		listview_musiclist.setAdapter(adapter);
		}
		listview_musiclist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(MusicPlayService.CURRENT_MUSIC_ID==position){
					MusicPlayService.is_equals=true;
					
				}else{
					
					MusicPlayService.CURRENT_MUSIC_ID=position;
					MusicPlayService.is_equals=false;
					
				}
				// 8������play���룬0������listview����
				MusicPlayService.flags=0;
				Log.e("---MusicPlayService.CURRENT_MUSIC_ID=position", "position"+position);
				
				Intent intent=new Intent(MainActivity.this, MusicPlayActivity.class);
				startActivity(intent);
				
			}
		});
		listview_musiclist.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				return false;
			}
		});

	}


	private void regist_Receiver_MainBottomBar() {
		// TODO �Զ����ɵķ������
		receiver_MainBottomBar=new Receiver_MainBottomBar();
		IntentFilter filter=new IntentFilter(MusicPlayerUtils.main_bottombar_init_ACTION);
		filter.addAction((MusicPlayerUtils.main_bottombar_play_init_ACTION));
		filter.addAction((MusicPlayerUtils.main_bottombar_visible_ACTION));
		
		registerReceiver(receiver_MainBottomBar, filter);
		
		if(MusicPlayerUtils.MUSICLIST.size()==0){
			return ;
		}
		//��ʼ����ʱmainActivity��bottombar��ͼƬ���������ƣ�������
		//���ר��ͼƬ��Ϊ�գ�����ר��ͼƬ
		
		if(MusicPlayerUtils.MUSICLIST.
				get(0).getBmp()!=null){
			main_bottom_image.setImageBitmap(MusicPlayerUtils.MUSICLIST.
				get(0).getBmp());
			}
		//������������
		if(MusicPlayerUtils.MUSICLIST.
				get(0).getTitle()!=null){
			main_bottom_tv_name.setText(MusicPlayerUtils.MUSICLIST.
				get(0).getTitle());
		}
		//���ô����ֵĸ���
		if(MusicPlayerUtils.MUSICLIST.
				get(0).getSinger()!=null){
			main_bottom_tv_singer.setText(MusicPlayerUtils.MUSICLIST.
				get(0).getSinger());
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_bottom_play:
			Intent music_player_intent = new Intent(
					MusicPlayerUtils.play_player_ACTION);
			music_player_intent.putExtra("flag", 1);// ���Ű�ť
			sendBroadcast(music_player_intent);
			break;
		case R.id.main_bottom_pre:
			Intent music_pre_intent = new Intent(
					MusicPlayerUtils.play_player_ACTION);
			music_pre_intent.putExtra("flag", 0);// ��һ�װ�ť����-flag
			sendBroadcast(music_pre_intent);
			break;
		case R.id.main_bottom_next:
			Intent music_next_intent = new Intent(
					MusicPlayerUtils.play_player_ACTION);
			music_next_intent.putExtra("flag", 2);// ���Ű�ť
			sendBroadcast(music_next_intent);
			break;
		case R.id.jump_to_musicplay:
			Intent intent=new Intent(this,MusicPlayActivity.class);
			MusicPlayService.flags=8;
			startActivity(intent);
			
			break;
		case R.id.tv_tab3:
			Toast.makeText(this, "�ܱ�Ǹ����ģ����δ����", 0).show();
			break;
		case R.id.tv_tab4:
			Toast.makeText(this, "�ܱ�Ǹ����ģ����δ����", 0).show();
			break;
		case R.id.unlogin:
			Toast.makeText(this, "��½tv��Ч", 0).show();
			Intent intent_unlogin=new Intent(this, LoginRegistActivity.class);
			startActivity(intent_unlogin);
			break;
		case R.id.bt_scan_mediafile:
			
			final Builder alertDialog_buider=new AlertDialog.Builder(this);
			alertDialog_buider.setIcon(R.drawable.scanfile);
			
			alertDialog_buider.setTitle("ɨ���ڲ�����");
			alertDialog_buider.setMessage("ȷ��Ҫ���²����б���");
			alertDialog_buider.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO �Զ����ɵķ������
					bt_scan_mediafile.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_scan));
					bt_scan_mediafile.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_scan));
					
					//�����׳����⣬��ʱ����
					scanfile(Environment.getExternalStorageDirectory());
					/*new Thread(){
						public void run()   {
							scanfile(Environment.getExternalStorageDirectory());
						};
					}.start();*/
				}

			});
			alertDialog_buider.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO �Զ����ɵķ������
					
				}
			});
			alertDialog_buider.create().show();
			break;
		
		default:
			break;
		}
	}
	static int scanfile_number=0;
	private void scanfile(File f){
		
        if(f.isDirectory()){
            File[] files=f.listFiles();//��ָ���ļ���������ļ�ȫ���г���
            if(files!=null){
                for(int i=0;i<files.length;i++){
                	//������ļ���Ŀ¼�Ļ�
                    if(files[i].isDirectory())
                    	if(files[i].length()>5000){
                        scanfile(files[i]);
                    	}
                    else
                    {
                    	MediaScannerConnection.scanFile(this,
                    			new String[]{files[i].getAbsolutePath()},
                    			new String[]{"audio/mp3"}, new OnScanCompletedListener() {
									//û����һ�ξ͵�����һ��getmusiclist���������Ӱ������
									@Override
									public void onScanCompleted(String path, Uri uri) {
										Log.d("tag", "uri->"+uri+","+"path->"+path);
										Log.d("tag", "scanfile_number: "+(++scanfile_number));
									}
								});
                    }
                }
            }
        }
        Log.d("tag", "originalThread_finish_scanfile");
        scanfile_number=0;
        
        
        
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
		stopService(new Intent(this,MusicPlayService.class));
		Log.e("---MainActivityonDestroy", "success");
		if(receiver_MainBottomBar!=null){
			unregisterReceiver(receiver_MainBottomBar);
		}
		super.onDestroy();
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO �Զ����ɵķ������
		if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
		
		return super.onKeyDown(keyCode, event);
	}
	private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "�ٰ�һ�η��ؼ��˳�����",
            		Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "��̨�����밴Home��",
                    Toast.LENGTH_SHORT).show();
            // ����handler�ӳٷ��͸���״̬��Ϣ
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
        	onDestroy();
        	finish();
        	
            System.exit(0);
        }
    }
	
}
