package com.filos.app;

import org.json.JSONException;
import org.json.JSONObject;

import com.nadav.facebookintegrationapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SelectedUserActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selected_user);

		Intent intent = getIntent();
		try {			
			JSONObject obj = new JSONObject(intent.getStringExtra("responseObj"));
			//GraphUser gObj = GraphObject.Factory.create(obj, GraphUser.class);
			
			//Log.i("SelectedUserActivity", gObj.toString());
			String userName = intent.getStringExtra("userName");
			

			//JSONObject context = obj.getJSONObject("context");
			//JSONObject mutual_friends = context.getJSONObject("mutual_friends");
			//JSONObject summary = mutual_friends.getJSONObject("summary");
			String mfriends_count = obj.getJSONObject("context")
					.getJSONObject("mutual_friends")
					.getJSONObject("summary")
					.getString("total_count");
			TextView textView = (TextView) findViewById(R.id.mutual_friends);
			textView.setText("You have " + mfriends_count + " mutual friends with " + userName + ".\n\n");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
