package com.filos.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ListView;

import com.filos.utils.MatchedUsersAdapter;
import com.filos.utils.Matcher;
//import com.nadav.facebookintegrationapp.R;

public class FilosResultsActivity extends Activity {
	private ListView mMatchedContacts;
	public ProgressDialog pd;
	private Matcher mMatcher;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filos_results_fragment);
		
		pd = ProgressDialog.show(this, "Working...", "Matching contacts...", true, false);
		
		mMatchedContacts = (ListView) findViewById(R.id.matched_users_list);
		Bundle bundle = getIntent().getExtras();

		mMatcher = new Matcher(bundle.get("userId").toString(), this);
		mMatcher.match();
	}
	
	public void setAdapter() {
		pd.dismiss();
		MatchedUsersAdapter adapter = new MatchedUsersAdapter(this, mMatcher.getMatchedUsers());
		mMatchedContacts.setAdapter(adapter);
	}
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.filos_results_fragment, container, false);
//		
//		mMatchedContacts = (ListView) view.findViewById(R.id.matched_users_list);
//		
////		MatchedUsersAdapter adapter = new MatchedUsersAdapter(getActivity(), MainActivity.mMatcher.getMatchedUsers());
////		mMatchedContacts.setAdapter(adapter);
//		return view;
//	}
}
