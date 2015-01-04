package com.nadav.facebookintegrationapp;

import java.util.Arrays;

import com.facebook.widget.LoginButton;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SplashFragment extends Fragment {

	private static final MainActivity Context = null;

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		
		//calls the DisplayContacts from the main activity
		((MainActivity)Context).displayContacts();
		
		
		View view = inflater.inflate(R.layout.splash, 
				container, false); 
		LoginButton login = (LoginButton) view.findViewById(R.id.login_button);
		login.setReadPermissions(Arrays.asList("user_friends"));
		return view;
	}



}
