package com.huayu.lile.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.huayu.lile.music_player.R;

public class RegistFragment extends Fragment implements OnClickListener{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		Log.d("tag", "oncreate_RegistFragment");
	}
	@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO 自动生成的方法存根
		Log.d("tag", "oncreateView_RegistFragment");
		View view1=inflater.inflate(R.layout.fragment_regist, null);
		//view1.findViewById(R.)
			return view1;
		}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		Log.d("tag", "onViewcreate_RegistFragment");
		super.onViewCreated(view, savedInstanceState);
	}
	@Override
	public void onClick(View v) {
		// TODO 自动生成的方法存根
		
	}
}
