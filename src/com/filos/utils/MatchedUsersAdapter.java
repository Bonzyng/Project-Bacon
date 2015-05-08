package com.filos.utils;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.filos.app.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * This adapter will populate the items in the list view of the filos results fragment
 * @author Nadav
 *
 */
//public class MatchedUsersAdapter extends BaseExpandableListAdapter {
//	
//	private Context context;
//	private ArrayList<MatchedUser> mMatchedUsers;
//	
//	// A view holder to save calls to findViewById
//	static class ViewHolder {
//		TextView userFacebookName;
//		TextView numOfMatched;
//		ImageView userFacebookPic;
//	}
//	
//	static class ExpandedViewHolder {
//		TextView contact;
//	}
//
//	public MatchedUsersAdapter(Context context, ArrayList<MatchedUser> matchedUsers) {
//		this.context = context;
//		mMatchedUsers = matchedUsers;
//	}
//
//	@Override
//	public int getGroupCount() {
//		return mMatchedUsers.size();
//	}
//
//	@Override
//	public int getChildrenCount(int groupPosition) {
//		return mMatchedUsers.get(groupPosition).getNumOfSharedContacts();
//	}
//
//	@Override
//	public Object getGroup(int groupPosition) {
//		return mMatchedUsers.get(groupPosition);
//	}
//
//	@Override
//	public Object getChild(int groupPosition, int childPosition) {
//		// Get the list of shared contacts from a matched user, and get the
//		// contact from that list
//		return mMatchedUsers.get(groupPosition).getSharedContacts().get(childPosition);
//	}
//	
//	public ArrayList<String> getContacts(int groupPosition) {
//		return mMatchedUsers.get(groupPosition).getSharedContacts();
//	}
//
//	@Override
//	public long getGroupId(int groupPosition) {
//		return groupPosition;
//	}
//
//	@Override
//	public long getChildId(int groupPosition, int childPosition) {
//		return childPosition;
//	}
//
//	@Override
//	public boolean hasStableIds() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public View getGroupView(int groupPosition, boolean isExpanded,
//			View convertView, ViewGroup parent) {
//		ViewHolder viewHolder;
//
//		if (convertView == null) {
//			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//			convertView = inflater.inflate(R.layout.filos_results_user_item, parent, false);
//
//			viewHolder = new ViewHolder();
//
//			viewHolder.userFacebookPic = (ImageView) convertView.findViewById(R.id.user_profile_pic);			
//			viewHolder.userFacebookName = (TextView) convertView.findViewById(R.id.user_profile_name);	
//			viewHolder.numOfMatched = (TextView) convertView.findViewById(R.id.mutual_contacts_number);
//
//			convertView.setTag(viewHolder);			
//		}
//
//		viewHolder = (ViewHolder) convertView.getTag();
//
//		MatchedUser matchedUser = mMatchedUsers.get(groupPosition);
//
//		// Gets the matched user profile picture		
//		String urlBeginning = "https://graph.facebook.com/"; 
//		String urlEnd = "/picture?type=large";
//		String userFacebookId = matchedUser.getFacebookId();
//
//		String url = urlBeginning + userFacebookId + urlEnd;
//
//		// Set the image as the profile pic
//		Picasso.with(context).load(url).transform(new CircleTransform())
//		.into(viewHolder.userFacebookPic);
//
//		viewHolder.userFacebookName.setText(matchedUser.getUserName());
//		viewHolder.numOfMatched.setText(Integer.toString(matchedUser.getNumOfSharedContacts()));
//		
//		return convertView;
//	}
//
//	@Override
//	public View getChildView(int groupPosition, int childPosition,
//			boolean isLastChild, View convertView, ViewGroup parent) {
//
//		ExpandedViewHolder viewHolder;
//		
//		if (convertView == null) {
//			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//			convertView = inflater.inflate(R.layout.filos_app_results_list_item_expanded, parent, false);
//
//			viewHolder = new ExpandedViewHolder();
//			
//			viewHolder.contact = (TextView) convertView.findViewById(R.id.expanded_contact);
//			
//			convertView.setTag(viewHolder);
//		}
//		
//		viewHolder = (ExpandedViewHolder) convertView.getTag();
//				
//		viewHolder.contact.setText((String) getChild(groupPosition, childPosition));
//		
//		return convertView;
//	}
//
//	@Override
//	public boolean isChildSelectable(int groupPosition, int childPosition) {
//		// TODO Auto-generated method stub
//		return false;
//	}
	


public class MatchedUsersAdapter extends ArrayAdapter<MatchedUser> {

	private Context context;
	private ArrayList<MatchedUser> mMatchedUsers;
	
	// A view holder to save calls to findViewById
	static class ViewHolder {
		TextView userFacebookName;
		TextView numOfMatched;
		ImageView userFacebookPic;
	}

	public MatchedUsersAdapter(Context context, ArrayList<MatchedUser> matchedUsers) {
		super(context, R.layout.filos_results_user_item, matchedUsers);
		
		this.context = context;
		mMatchedUsers = matchedUsers;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder;
				
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			convertView = inflater.inflate(R.layout.filos_results_user_item, parent, false);
			
			viewHolder = new ViewHolder();
			
			viewHolder.userFacebookPic = (ImageView) convertView.findViewById(R.id.user_profile_pic);			
			viewHolder.userFacebookName = (TextView) convertView.findViewById(R.id.user_profile_name);	
			viewHolder.numOfMatched = (TextView) convertView.findViewById(R.id.mutual_contacts_number);
			
			convertView.setTag(viewHolder);			
		}
		
		viewHolder = (ViewHolder) convertView.getTag();

		MatchedUser matchedUser = mMatchedUsers.get(position);
		
		// Gets the matched user profile picture		
		String urlBeginning = "https://graph.facebook.com/"; 
		String urlEnd = "/picture?type=large";
		String userFacebookId = matchedUser.getFacebookId();
		
		String url = urlBeginning + userFacebookId + urlEnd;
		
		// Set the image as the profile pic
		Picasso.with(context).load(url).transform(new CircleTransform())
			.into(viewHolder.userFacebookPic);
		
		viewHolder.userFacebookName.setText(matchedUser.getUserName());
		int numOfMatched = matchedUser.getNumOfMutualFriends() + matchedUser.getNumOfSharedContacts();
		viewHolder.numOfMatched.setText(Integer.toString(numOfMatched));
		
		return convertView;
	}
	
	// Sets the profile image to be circle
	private class CircleTransform implements Transformation {
		@Override
		public Bitmap transform(Bitmap source) {
			int size = Math.min(source.getWidth(), source.getHeight());

			int x = (source.getWidth() - size) / 2;
			int y = (source.getHeight() - size) / 2;

			Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
			if (squaredBitmap != source) {
				source.recycle();
			}

			Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();
			BitmapShader shader = new BitmapShader(squaredBitmap,
					BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
			paint.setShader(shader);
			paint.setAntiAlias(true);

			float r = size / 2f;
			canvas.drawCircle(r, r, r, paint);

			squaredBitmap.recycle();
			return bitmap;
		}

		@Override
		public String key() {
			return "circle";
		}
	}
}
