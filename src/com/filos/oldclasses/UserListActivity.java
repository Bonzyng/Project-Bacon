package com.filos.oldclasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.filos.utils.DataGetter;
import com.filos.app.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class UserListActivity extends FragmentActivity {

	private static String user_ID = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_list);
		
//		user_ID = MainActivity.userFacebookId;
		user_ID = getIntent().getStringExtra("User_ID");
		
		if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
	}

	/**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private ListView userList;
        
        public PlaceholderFragment() {
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            // Set the userList value
            userList = (ListView) rootView.findViewById(R.id.listview_users);
            // Set a clickable event on each list item
            userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					String userName = userList.getItemAtPosition(position).toString();
					userName = userName.substring(6, userName.length() - 1);
					Log.i("This is the user clicked", userName);
					Intent intent = new Intent();
					intent.putExtra("userName", userName);
					intent.putExtra("myUserId", user_ID);
					intent.setClass(getActivity(), MatchedUserActivity.class);
					startActivity(intent);					
				}            	
            });
            
            // Add the user_ID into an array and send it to DataGetter to retrieve
            // all users from the DB.
            ArrayList<NameValuePair> user_ID_asArray = new ArrayList<NameValuePair>();
    		user_ID_asArray.add(new BasicNameValuePair("User_ID", user_ID));
            new DataGetter(0, this, user_ID_asArray).execute();

            return rootView;
        }
        
        
		public void setDataFromServer(JSONArray users) {
			try {
		        // create the grid item mapping
		        String[] from = new String[] {"User"};
		        int[] to = new int[] { R.id.list_item_users_textview};
		 
		        // looping through All Users prepare the list of all records
		        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		        for(int i = 0; i <  users.length(); i++){
		            HashMap<String, String> map = new HashMap<String, String>();
		            JSONObject row = users.getJSONObject(i);
		            map.put("User", row.getString("User_Profile"));
		            fillMaps.add(map);
		        }
		 
		        // fill in the grid_item layout
		        SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_users, from, to);
		        
	            userList.setAdapter(adapter);
			} catch (Exception e) {
				
			}			
		}  
    }
}
