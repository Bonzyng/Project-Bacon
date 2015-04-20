package com.filos.utils;

import java.util.ArrayList;


import com.nadav.facebookintegrationapp.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

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
		
		//Gets the matched user profile picture
		
		String url = "https://graph.facebook.com/" + " " + "/picture?type=large";
		ImageView userFacebookPic = (ImageView) view.findViewById(R.id.user_profile_pic);
		Picasso.with(context)
		.load("https://graph.facebook.com/10205079633206382/picture?type=large")
		.transform(new CircleTransform())
		.into(userFacebookPic);
		
		TextView userFacebookName = (TextView) view.findViewById(R.id.user_profile_name);	
		TextView numOfMatched = (TextView) view.findViewById(R.id.mutual_contacts_number);
		
		userFacebookName.setText(mMatchedUsers.get(position).getUserName());
		numOfMatched.setText(Integer.toString(mMatchedUsers.get(position).getNumOfSharedContacts()));
		
		return view;
	}
	
	//sets the profile image to be circle
	public class CircleTransform implements Transformation {
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
