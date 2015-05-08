package com.filos.utils;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.filos.app.FilosResultsActivity.MatchedUserDataHolder;
import com.filos.app.R;

public class PopupListAdapter extends ArrayAdapter<MatchedUserDataHolder> {
	
	private Context context;
	private ArrayList<MatchedUserDataHolder> dataHolder;
	
	static class ViewHolder {
		TextView matchedItemName;
		ImageView matchedItemTypeImage;
	}

	public PopupListAdapter(Context context, ArrayList<MatchedUserDataHolder> contactsAndFriendsDataHolder) {
		super(context, R.layout.filos_popup_matched_list_item, contactsAndFriendsDataHolder);
		
		this.context = context;
		dataHolder = contactsAndFriendsDataHolder;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder;
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			convertView = inflater.inflate(R.layout.filos_popup_matched_list_item, parent, false);
			
			viewHolder = new ViewHolder();
			
			viewHolder.matchedItemTypeImage = (ImageView) convertView.findViewById(R.id.popup_matched_list_item_type_image);			
			viewHolder.matchedItemName = (TextView) convertView.findViewById(R.id.popup_matched_list_item_name);	
			
			convertView.setTag(viewHolder);			
		}
		
		viewHolder = (ViewHolder) convertView.getTag();

		MatchedUserDataHolder data = dataHolder.get(position);
		
		viewHolder.matchedItemName.setText(data.getName());
		if (data.getType() == 0) {
			viewHolder.matchedItemTypeImage.setImageResource(R.drawable.filos_contacts_icon);
		} else {
			viewHolder.matchedItemTypeImage.setImageResource(R.drawable.filos_fb_icon);
		}
		
		return convertView;
	}

}
