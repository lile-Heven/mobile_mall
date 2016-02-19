package com.huayu.lile.fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.huayu.lile.music_player.R;
import com.huayu.lile.net.NetUtils;

public class LoginFragment extends Fragment implements OnClickListener {

	private Button bt_login_regist;
	private EditText et_login_name;
	private Button bt_login_login;
	private Handler handler=new Handler(new Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			
			
			et_login_name.setText(msg.obj.toString());
			Log.d("tag", "success-et_login_name---change");
			return true;
		}
	});

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		Log.d("tag", "oncreate_LoginFragment");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		Log.d("tag", "oncreateView_LoginFragment");
		View view1 = inflater.inflate(R.layout.fragment_login, null);
		bt_login_regist = (Button) view1.findViewById(R.id.bt_login_regist);
		bt_login_regist.setOnClickListener(this);
		bt_login_login = (Button)view1.findViewById(R.id.bt_login_login);
		bt_login_login.setOnClickListener(this);
		
		
		et_login_name=(EditText)view1.findViewById(R.id.et_login_name);
		return view1;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		Log.d("tag", "onViewcreate_LoginFragment");
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_login_regist:
			FragmentTransaction fragmentTransaction = getActivity()
					.getFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.fl_login, new RegistFragment());
			fragmentTransaction.addToBackStack("RegistFragment");
			fragmentTransaction.commit();
			break;
		case R.id.bt_login_login:
			//网络操作不允许在主线程进行
			new Thread(){
				public void run() {
					String str_entity = NetUtils.easyLinkInternet("/RequestLogin");
					Log.d("tag", "success-str_entity: "+str_entity);
					Message msg=new Message();
					msg.obj=str_entity;
					handler.sendMessage(msg);
					
				};
			}.start();
			
			break;
		

		default:
			break;
		}

	}
}
