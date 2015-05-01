package com.filos.app;

import java.util.Arrays;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.widget.LoginButton;
//import com.nadav.facebookintegrationapp.R;

public class SplashFragment extends Fragment {


	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.splash, 
				container, false); 
		LoginButton login = (LoginButton) view.findViewById(R.id.login_button);
		
		// Added onClick event to call displayContacts
//		login.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View view) {
//				Log.i("I AM IN THE ONCLICK", "OF LOGIN BUTTON");
//				Session session = Session.getActiveSession();
//				while(session.isClosed() || session == null) {
//					Log.i("onClick", "in the while");
//				}
//				((MainActivity) getActivity()).displayContacts(view);
//			}
//			
//		});
		login.setReadPermissions(Arrays.asList("user_friends"));
		return view;
	}



}
