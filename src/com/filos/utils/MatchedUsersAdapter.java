package com.filos.utils;

import java.util.ArrayList;

import com.nadav.facebookintegrationapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This adapter will populate the items in the list view of the filos results fragment
 * @author Nadav
 *
 */
public class MatchedUsersAdapter extends ArrayAdapter<MatchedUser> {

	private Context context;
	private ArrayList<MatchedUser> mMatchedUsers;

	public MatchedUsersAdapter(Context context, ArrayList<MatchedUser> matchedUsers) {
		super(context, R.layout.filos_results_user_item, matchedUsers);
		
		this.context = context;
		mMatchedUsers = matchedUsers;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = null;
		
		view = inflater.inflate(R.layout.filos_results_user_item, parent, false);
		
		ImageView userFacebookPic = (ImageView) view.findViewById(R.id.user_profile_pic);		
		TextView userFacebookName = (TextView) view.findViewById(R.id.user_profile_name);	
		TextView numOfMatched = (TextView) view.findViewById(R.id.mutual_contacts_number);
		
		userFacebookName.setText(mMatchedUsers.get(position).getUserName());
		numOfMatched.setText(Integer.toString(mMatchedUsers.get(position).getNumOfSharedContacts()));
		
		return view;
	}

}
