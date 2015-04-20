package com.filos.utils;

import java.util.ArrayList;

public class MatchedUser {
	private int mNumOfSharedContacts;
	private int mNumOfMutualFBFriends;
	private ArrayList<String> mSharedContacts;
	private ArrayList<String> mMutualFBFriends;
	private String mUserFBName;

	public MatchedUser() {
		mNumOfSharedContacts = 0;
		mNumOfMutualFBFriends = 0;
		mSharedContacts = new ArrayList<String>();
		mMutualFBFriends = new ArrayList<String>();
	}
	
	public void setUserName(String userName) {
		mUserFBName = userName;
	}
	
	public void addFBFriend(String friendName) {
		mNumOfMutualFBFriends++;
		mMutualFBFriends.add(friendName);
	}
	
	public void addSharedContact(String contactName) {
		mNumOfSharedContacts++;
		mSharedContacts.add(contactName);
	}
	
	public String getUserName() {
		return mUserFBName;
	}
	
	public int getNumOfMutualFriends() {
		return mNumOfMutualFBFriends;
	}
	
	public int getNumOfSharedContacts() {
		return mNumOfSharedContacts;
	}
	
	public ArrayList<String> getMutualFriends() {
		return mMutualFBFriends;
	}
	
	public ArrayList<String> getSharedContacts() {
		return mSharedContacts;
	}
}
