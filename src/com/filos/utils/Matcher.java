package com.filos.utils;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.filos.app.FilosResultsActivity;
import com.filos.app.MainActivity;

public class Matcher {
	
	private static final int CONTACTS_MATCH = 0;
	private static final int MUTUAL_FRIENDS_MATCH = 1;
	
	public MainActivity mCallerActivity;
	private String mUserId;
	private String mOtherUserId;
	private ArrayList<MatchedUser> mMatchedUsers;
	public int numOfUsers;
	private FilosResultsActivity mCaller;
	
	private boolean contactsMatchComplete = false;
	private boolean mutualFriendsMatchComplete = false;
	
	public Matcher(String userId, FilosResultsActivity caller) {
		mUserId = "s" + userId;
		mMatchedUsers = new ArrayList<MatchedUser>();
		mCaller = caller;
	}
	
	/*
	 * Each matching sequence calls this method at the end. Once all matching
	 * sequences are complete, the adapter will be populated
	 */
	public void checkMatchComplete(int matchCode) {

		
		if (matchCode == CONTACTS_MATCH) {
			contactsMatchComplete = true;
		} else if (matchCode == MUTUAL_FRIENDS_MATCH) {
			mutualFriendsMatchComplete = true;
		}
		
		if (contactsMatchComplete && mutualFriendsMatchComplete) {
			mCaller.setAdapter();
		}
	}
	
	/*
	 * Collect all the users from the db and send the results to matchAllUsers()
	 */
	public void match() {
		ArrayList<NameValuePair> userIdAsArray = new ArrayList<NameValuePair>();
		userIdAsArray.add(new BasicNameValuePair("User_ID", mUserId));
		new DataGetter(0, this, null, 0, userIdAsArray).execute();
	}
	
	/*
	 * Start the matching sequence vs each user in the db
	 */
	protected void matchAllUsers(JSONArray allUsersJson) {
		
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
	

	
	public ArrayList<MatchedUser> getMatchedUsers() {
		return mMatchedUsers;
	}
	
	
	// TODO: Receive the matched array and return the array sorted by number of mutual contacts
	public void sortMatchedUsers(ArrayList<MatchedUser> matchedUsers) {}
	
	/*
	 * ************************************************************************
	 * *********************** MATCH CONTACTS LOGIC ***************************
	 * ************************************************************************
	 */

	/*
	 * Match two users' contact lists
	 */
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
				checkMatchComplete(CONTACTS_MATCH);
			}
		} catch (Exception e) {
		}
	}
	
	/*
	 * ************************************************************************
	 * ********************** MATCH FB FRIENDS LOGIC **************************
	 * ************************************************************************
	 */

	protected void findMatchedUserMutualFriends(final MatchedUser matchedUser, final int index) {
		Bundle params = new Bundle();
		params.putString("fields", "context.fields(mutual_friends)");
		
		new Request(Session.getActiveSession(), 
				matchedUser.getFacebookId(),
				params, 
				HttpMethod.GET,
				new Request.Callback() {
					
					@Override
					public void onCompleted(Response response) {
						String message = "Request received";
						String mutualFriendsCount = "0";
						JSONArray mutualFriendsData = null;

						GraphObject responseGraphObject = response.getGraphObject();
						FacebookRequestError error = response.getError();

						if (responseGraphObject != null) {
							JSONObject userObj = responseGraphObject.getInnerJSONObject();
							
							try {
								if (userObj.has("context")) {
									mutualFriendsCount = userObj.getJSONObject("context")
											.getJSONObject("mutual_friends")
											.getJSONObject("summary")
											.getString("total_count");
									
									mutualFriendsData = userObj.getJSONObject("context")
											.getJSONObject("mutual_friends")
											.getJSONArray("data");
								}							
							} catch (JSONException e) {
								// TODO Add error screen?		
								e.printStackTrace();
							}
						} else if (error != null) {
							message = "Error getting request info";
						}
						
						arrangeResults(matchedUser, mutualFriendsCount, mutualFriendsData, index);
					}			
				}).executeAsync();
	}
	
	private void arrangeResults(MatchedUser matchedUser, String mutualFriendsCount,
			JSONArray mutualFriendsData, int index) {
		
		if (mutualFriendsData != null) {
			int size = mutualFriendsData.length();
			
			for(int i = 0; i < size; i++) {
				JSONObject friend;
				try {
					friend = mutualFriendsData.getJSONObject(i);
					String friendName = friend.getString("name");

					matchedUser.addFBFriend(friendName);
				} catch (JSONException e) {
					// TODO Add error screen?
					e.printStackTrace();
				}

			}
		}
		
		if (index == numOfUsers - 1) {
			checkMatchComplete(MUTUAL_FRIENDS_MATCH);
		}
	}
}
