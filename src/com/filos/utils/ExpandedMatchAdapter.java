package com.filos.utils;

import java.util.ArrayList;

import com.filos.app.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ExpandedMatchAdapter extends ArrayAdapter<String> {
	
	private Context context;
	private ArrayList<String> sharedContacts;
	
	static class ViewHolder {
		TextView contact;
	}
	
	public ExpandedMatchAdapter(Context context, ArrayList<String> sharedContacts) {
		super(context, R.layout.list_item_users, sharedContacts);

		this.context = context;
		this.sharedContacts = sharedContacts;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item_users, parent, false);
			
			viewHolder = new ViewHolder();
			
			viewHolder.contact = (TextView) convertView.findViewById(R.id.list_item_users_textview);
			
			convertView.setTag(viewHolder);
		}
		
		viewHolder = (ViewHolder) convertView.getTag();
		
		String contactName = sharedContacts.get(position);
		
		Log.i("contacts at " + position + ": ", contactName);
		
		viewHolder.contact.setText(contactName);
		
		return convertView;
		
//		if (convertView == null) {
//		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		
//		convertView = inflater.inflate(R.layout.filos_results_user_item, parent, false);
//		
//		viewHolder = new ViewHolder();
//		
//		viewHolder.userFacebookPic = (ImageView) convertView.findViewById(R.id.user_profile_pic);			
//		viewHolder.userFacebookName = (TextView) convertView.findViewById(R.id.user_profile_name);	
//		viewHolder.numOfMatched = (TextView) convertView.findViewById(R.id.mutual_contacts_number);
//		
//		convertView.setTag(viewHolder);			
//	}
//	
//	viewHolder = (ViewHolder) convertView.getTag();
//
//	MatchedUser matchedUser = mMatchedUsers.get(position);
//	
//	// Gets the matched user profile picture		
//	String urlBeginning = "https://graph.facebook.com/"; 
//	String urlEnd = "/picture?type=large";
//	String userFacebookId = matchedUser.getFacebookId();
//	
//	String url = urlBeginning + userFacebookId + urlEnd;
//	
//	// Set the image as the profile pic
//	Picasso.with(context).load(url).transform(new CircleTransform())
//		.into(viewHolder.userFacebookPic);
//	
//	viewHolder.userFacebookName.setText(matchedUser.getUserName());
//	viewHolder.numOfMatched.setText(Integer.toString(matchedUser.getNumOfSharedContacts()));
//	
//	return convertView;
	}
	
}
