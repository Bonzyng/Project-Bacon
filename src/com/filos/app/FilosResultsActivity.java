package com.filos.app;

import java.util.ArrayList;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.filos.utils.MatchedUser;
import com.filos.utils.MatchedUsersAdapter;
import com.filos.utils.Matcher;
import com.filos.utils.PopupListAdapter;

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

				// Set the name and profile pic for the selected matched user
				TextView userProfileName = (TextView) popupView.findViewById(R.id.popup_user_profile_name);
				userProfileName.setText(mMatcher.getMatchedUsers().get(position).getUserName());

				ImageView userProfilePic = (ImageView) view.findViewById(R.id.user_profile_pic);
				ImageView popupProfilePic = (ImageView) popupView.findViewById(R.id.popup_user_profile_pic);
				popupProfilePic.setImageDrawable(userProfilePic.getDrawable());

				ListView matchedContacts = (ListView) popupView.findViewById(R.id.popup_shared_contacts_list_expanded);

				MatchedUser matchedUser = mMatcher.getMatchedUsers().get(position);
				
				// Collect the matched results with this user and populate the list of results
				ArrayList<MatchedUserDataHolder> dataHolder = new ArrayList<MatchedUserDataHolder>();
				populateDataHolderArray(dataHolder, matchedUser.getSharedContacts(), 0);
				populateDataHolderArray(dataHolder, matchedUser.getMutualFriends(), 1);
				
				PopupListAdapter adapter = new PopupListAdapter(FilosResultsActivity.this, dataHolder);

				matchedContacts.setAdapter(adapter);

				popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
				popupWindow.setOutsideTouchable(true);
				popupWindow.setFocusable(true);
			}

		});
	}

	public void dismissPopup(View v) {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}

	public void setAdapter() {
		pd.dismiss();
		MatchedUsersAdapter adapter = new MatchedUsersAdapter(this, mMatcher.getMatchedUsers());
		mMatchedContacts.setAdapter(adapter);
	}

	@Override
	public void onBackPressed() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			super.onBackPressed();
		}
	}
	
	/*
	 * A holder class to hold each match by name and type (where the match came from)
	 */
	public class MatchedUserDataHolder {		
		private String mName;
		private int mType; // 0 for contact, 1 for fb friend
		
		public MatchedUserDataHolder(String name, int type) {
			mName = name;
			mType = type;
		}
		
		public String getName() {
			return mName;
		}
		
		public int getType() {
			return mType;
		}
	}
	
	private void populateDataHolderArray(ArrayList<MatchedUserDataHolder> dataHolder, 
			ArrayList<String> data, int type) {
		if (dataHolder == null) {
			dataHolder = new ArrayList<MatchedUserDataHolder>();
		}	
		int size = data.size();
		
		for (int i = 0; i < size; i++) {
			dataHolder.add(new MatchedUserDataHolder(data.get(i), type));
		}
	}
}
