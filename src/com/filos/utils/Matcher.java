package com.filos.utils;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.filos.app.FilosResultsActivity;

public class Matcher {
	
	private FilosResultsActivity mCallerActivity;
	private String mUserId;
	private String mOtherUserId;
	private String mUserName;
	private ArrayList<MatchedUser> mMatchedUsers;
	
	public Matcher(String userId) {
//		mCallerActivity = caller;
		mUserId = "s" + userId;
//		mUserName = userName;
		mMatchedUsers = new ArrayList<MatchedUser>();
	}	
	
	// Match this user with all other users in the db and return an array of the results
	public void match() {
		ArrayList<NameValuePair> userIdAsArray = new ArrayList<NameValuePair>();
		userIdAsArray.add(new BasicNameValuePair("User_ID", mUserId));
		new DataGetter(0, this, null, userIdAsArray).execute();
	}
	
	// TODO: Receive the matched array and return the array sorted by number of mutual contacts
	public void sortMatchedUsers(ArrayList<MatchedUser> matchedUsers) {}
	
	private void matchUsers(String userName, MatchedUser matchedUser) {
		ArrayList<NameValuePair> userNameAsArray = new ArrayList<NameValuePair>();
 		userNameAsArray.add(new BasicNameValuePair("userName", userName));
		new DataGetter(1, this, matchedUser, userNameAsArray).execute();
	}
	
	protected void matchContacts(JSONArray jsonArray, MatchedUser matchedUser) {
		String jsonArrayString = jsonArray.toString();
		String userId = jsonArrayString.substring(jsonArrayString.indexOf(':') + 2,jsonArrayString.length() - 3);
		mOtherUserId = userId;
		Log.i("other used id: ", mOtherUserId);
		Log.i("my user id: ", mUserId);

		ArrayList<NameValuePair> userIdsArray = new ArrayList<NameValuePair>();
 		userIdsArray.add(new BasicNameValuePair("myUserId", mUserId));
 		userIdsArray.add(new BasicNameValuePair("otherUserId", mOtherUserId));
 		new DataGetter(2, this, matchedUser, userIdsArray).execute();
	}

	protected void populateMatchedUserSharedContacts(JSONArray matchedUsers, MatchedUser matchedUser) {
		int inputLength = matchedUsers.length();
		try {
			for(int i = 0; i < inputLength; i++) {
				JSONObject row = matchedUsers.getJSONObject(i);
				matchedUser.addSharedContact(row.getString("contactName"));
			}
			Log.i("Shared contacts", matchedUser.getSharedContacts().toString());
		} catch (Exception e) {
		}
	}


	protected void findMatchedUserMutualFriends(MatchedUser matchedUser) {
		// TODO Send a GraphAPI request similar to that in PickerActivity	
	}

	public void matchAllUsers(JSONArray allUsersJson) {
		
		String[] allUsers = new String[allUsersJson.length()];
		for(int i = 0; i < allUsers.length; i++) {
			try {
				JSONObject row = allUsersJson.getJSONObject(i);
				allUsers[i] = row.getString("User_Profile");
			} catch (JSONException e) {
			}
		}
		for(int i = 0; i < allUsers.length; i++) {
			MatchedUser matchedUser = new MatchedUser();
			matchedUser.setUserName(allUsers[i]);
			matchedUser.setFacebookId(mUserId.substring(1));
			matchUsers(allUsers[i], matchedUser);
			mMatchedUsers.add(matchedUser);
		}
	}
}
