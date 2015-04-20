package com.filos.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.nadav.facebookintegrationapp.R;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MatchedUserActivity extends ActionBarActivity {

	private ListView mMatchedContacts;
	private String myUserId;
	private String mOtherUserId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matched_user);
		String userName = getIntent().getStringExtra("userName");
		myUserId = getIntent().getStringExtra("myUserId");
		
		mMatchedContacts = (ListView) findViewById(R.id.listview_matched_results);
		
		matchUsers(userName); // and call the matching method.
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.matched_user, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void matchUsers(String userName) {
		ArrayList<NameValuePair> userNameAsArray = new ArrayList<NameValuePair>();
 		userNameAsArray.add(new BasicNameValuePair("userName", userName));
		new DataGetter(1, this, userNameAsArray).execute();
	}
	
	public void matchContacts(JSONArray jsonArray) {
		String jsonArrayString = jsonArray.toString();
		String userId = jsonArrayString.substring(jsonArrayString.indexOf(':') + 2,jsonArrayString.length() - 3);
		mOtherUserId = userId;

		String test = myUserId;
		ArrayList<NameValuePair> userIdsArray = new ArrayList<NameValuePair>();
 		userIdsArray.add(new BasicNameValuePair("myUserId", myUserId));
 		userIdsArray.add(new BasicNameValuePair("otherUserId", mOtherUserId));
// 		Log.i("myUserId is: ", myUserId);
// 		Log.i("otherUserId is: ", mOtherUserId);
 		new DataGetter(2, this, userIdsArray).execute();
	}

	public void setDataFromServer(JSONArray matchedUsers) {
		try {
	        // create the grid item mapping
	        String[] from = new String[] {"Contact"};
	        int[] to = new int[] { R.id.list_item_users_textview};
	 
	        // looping through All Users prepare the list of all records
	        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	        for(int i = 0; i <  matchedUsers.length(); i++){
	            HashMap<String, String> map = new HashMap<String, String>();
	            JSONObject row = matchedUsers.getJSONObject(i);
	            map.put("Contact", row.getString("contactName"));
	            fillMaps.add(map);
	        }
	 
	        // fill in the grid_item layout
	        SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.list_item_users, from, to);
	        
            mMatchedContacts.setAdapter(adapter);
		} catch (Exception e) {
			
		}		
		
	}

//	public void setTextView(JSONArray jsonArray) {
//		String jsonArrayString = jsonArray.toString();
//		String userId = jsonArrayString.substring(jsonArrayString.indexOf(':') + 2,jsonArrayString.length() - 3);
//		mOtherUserId = userId;
//
//		setMatchedContacts(myUserId, mOtherUserId);
//	}
}
