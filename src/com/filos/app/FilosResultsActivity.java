package com.filos.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.filos.utils.MatchedUsersAdapter;
import com.filos.utils.Matcher;

public class FilosResultsActivity extends Activity {
	private ListView mMatchedContacts;
	public ProgressDialog pd;
	private Matcher mMatcher;
	private PopupWindow popupWindow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filos_results_fragment);
		
		ActionBar bar = getActionBar();
	    bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.filos_blue)));
		bar.setTitle("");
		bar.setIcon(R.drawable.ic_launcher);
		
		pd = ProgressDialog.show(this, "Working...", "Matching contacts...", true, false);
		pd.setCancelable(true);
		
		mMatchedContacts = (ListView) findViewById(R.id.matched_users_list);
		Bundle bundle = getIntent().getExtras();

		mMatcher = new Matcher(bundle.get("userId").toString(), this);
		mMatcher.match();
		
		mMatchedContacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
				View popupView = inflater.inflate(R.layout.filos_app_results_list_item_expanded, parent, false);
				
				TextView userProfileName = (TextView) popupView.findViewById(R.id.popup_user_profile_name);
				userProfileName.setText(mMatcher.getMatchedUsers().get(position).getUserName());
				
				ImageView userProfilePic = (ImageView) view.findViewById(R.id.user_profile_pic);
				ImageView popupProfilePic = (ImageView) popupView.findViewById(R.id.popup_user_profile_pic);
				popupProfilePic.setImageDrawable(userProfilePic.getDrawable());

				ListView matchedContacts = (ListView) popupView.findViewById(R.id.popup_shared_contacts_list_expanded);
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(FilosResultsActivity.this, R.layout.list_item_users, 
						mMatcher.getMatchedUsers().get(position).getSharedContacts());
				
				matchedContacts.setAdapter(adapter);
				
				popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
				popupWindow.setOutsideTouchable(true);
				popupWindow.setFocusable(false);
			}
			
		});
	}
	
	public void dismissPopup(View v) {
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
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
