package com.huayu.lile.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huayu.lile.music_player.R;

public class FragmentTest extends Fragment{
         
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO �Զ����ɵķ������
		super.onCreate(savedInstanceState);
	}
	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
        		Bundle savedInstanceState) {
        	// TODO �Զ����ɵķ������
        	return inflater.inflate(R.layout.music_play, null, false);
        }
         @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO �Զ����ɵķ������
        super.onViewCreated(view, savedInstanceState);
        }
}
