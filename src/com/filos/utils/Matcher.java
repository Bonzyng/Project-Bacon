package com.filos.utils;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.filos.app.FilosResultsActivity;
import com.filos.app.MainActivity;

public class Matcher {
	
	public MainActivity mCallerActivity;
	private String mUserId;
	private String mOtherUserId;
	private String mUserName;
	private ArrayList<MatchedUser> mMatchedUsers;
	public int numOfUsers;
	private FilosResultsActivity mCaller;
	
	public Matcher(String userId, FilosResultsActivity caller) {
//		mCallerActivity = caller;
		mUserId = "s" + userId;
//		mUserName = userName;
		mMatchedUsers = new ArrayList<MatchedUser>();
		mCaller = caller;
	}
	
	public ArrayList<MatchedUser> getMatchedUsers() {
		return mMatchedUsers;
	}
	
	// Match this user with all other users in the db and return an array of the results
	public void match() {
		ArrayList<NameValuePair> userIdAsArray = new ArrayList<NameValuePair>();
		userIdAsArray.add(new BasicNameValuePair("User_ID", mUserId));
		new DataGetter(0, this, null, 0, userIdAsArray).execute();
	}
	
	// TODO: Receive the matched array and return the array sorted by number of mutual contacts
	public void sortMatchedUsers(ArrayList<MatchedUser> matchedUsers) {}
	
	private void matchUsers(String userName, MatchedUser matchedUser, int index) {
		ArrayList<NameValuePair> userNameAsArray = new ArrayList<NameValuePair>();
 		userNameAsArray.add(new BasicNameValuePair("userName", userName));
 		// Get the other user's facebook ID and send them to matchContacts
		new DataGetter(1, this, matchedUser, index, userNameAsArray).execute();
	}
	
	protected void matchContacts(JSONArray jsonArray, MatchedUser matchedUser, int index) {
		String jsonArrayString = jsonArray.toString();
		// Extract the other user's facebook ID from the returned JSON string.
		// Keep the original version to match the tables
		String userId = jsonArrayString.substring(jsonArrayString.indexOf(':') + 2,jsonArrayString.length() - 3);
		mOtherUserId = userId;
		// Drop the leading 's' and save the true ID to match mutual friends
		matchedUser.setFacebookId(mOtherUserId.substring(1));

		ArrayList<NameValuePair> userIdsArray = new ArrayList<NameValuePair>();
 		userIdsArray.add(new BasicNameValuePair("myUserId", mUserId));
 		userIdsArray.add(new BasicNameValuePair("otherUserId", mOtherUserId));
 		// Match the two users
 		new DataGetter(2, this, matchedUser, index, userIdsArray).execute();
	}

	protected void populateMatchedUserSharedContacts(JSONArray matchedUsers, MatchedUser matchedUser, int index) {
		int inputLength = matchedUsers.length();
		try {
			for(int i = 0; i < inputLength; i++) {
				JSONObject row = matchedUsers.getJSONObject(i);
				matchedUser.addSharedContact(row.getString("contactName"));
			}
//			Log.i("Matcher Class - Shared contacts", matchedUser.getSharedContacts().toString());
//			Log.i("Matcher Class - Number of shared contacts", Integer.toString(matchedUser.getNumOfSharedContacts()));
			
			// If it's the last called match operation, populate the list view
			// to show the results
			if (index == numOfUsers - 1) {
				mCaller.setAdapter();
			}
		} catch (Exception e) {
		}
	}

	protected void findMatchedUserMutualFriends(MatchedUser matchedUser) {
		// TODO Send a GraphAPI request similar to that in PickerActivity	
	}

	public void matchAllUsers(JSONArray allUsersJson) {
		
		String[] allUsers = new String[allUsersJson.length()];
		numOfUsers = allUsers.length;
		for(int i = 0; i < numOfUsers; i++) {
			try {
				JSONObject row = allUsersJson.getJSONObject(i);
				allUsers[i] = row.getString("User_Profile");
			} catch (JSONException e) {
			}
		}
		
		for(int i = 0; i < numOfUsers; i++) {
			MatchedUser matchedUser = new MatchedUser();
			matchedUser.setUserName(allUsers[i]);
//			matchedUser.setFacebookId(mUserId.substring(1));
			// Starts the matching sequence between two users
			matchUsers(allUsers[i], matchedUser, i);
			mMatchedUsers.add(matchedUser);
		}
	}
}
