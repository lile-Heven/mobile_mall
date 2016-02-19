package com.huayu.lile.login;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.huayu.lile.fragment.LoginFragment;
import com.huayu.lile.music_player.R;

public class LoginRegistActivity extends Activity{
	
		private LoginFragment loginFragment;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.login);
			init_login_fragment();
			init();
			
		}

		private void init_login_fragment() {
			 loginFragment = new LoginFragment(); 
		}

		private void init() {
			FragmentManager fragmentManager=getFragmentManager();
			FragmentTransaction transcation = fragmentManager.beginTransaction();
			transcation.add(R.id.fl_login, loginFragment);
			transcation.commit();
		}
		
}
