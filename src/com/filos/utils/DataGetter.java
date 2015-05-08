package com.filos.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.filos.oldclasses.MatchedUserActivity;
import com.filos.oldclasses.UserListActivity.PlaceholderFragment;

public class DataGetter extends AsyncTask<Void, Void, Boolean> {
	
	// Request codes to send the HTTP request to the matching php page
	private static final int GET_ALL_USERS = 0;
	private static final int GET_USER_ID = 1;
	private static final int MATCH_TWO_USERS = 2;

	// url to update user status
	private String url;
	private final String urlGetUsers = "http://liron.milab.idc.ac.il/php/bacon_get_all_users.php";
	private final String urlGetUserId = "http://liron.milab.idc.ac.il/php/bacon_get_user_id.php";
	private final String urlMatchContacts = "http://liron.milab.idc.ac.il/php/bacon_match_two_users.php";
	
	private List<NameValuePair> requestParams;
	private InputStream is = null;
	private String line = "";
	private String json = "";
	private JSONObject jObj = null;
	private PlaceholderFragment fragmentCaller;
	private MatchedUserActivity activityCaller;
	
	private Matcher mMatcher;
	private MatchedUser mMatchedUser;
	
	private int requestCode;
	private int index;


	public DataGetter(int requestCode, Matcher caller, MatchedUser matchedUser, int index, List<NameValuePair> params) {
		if (requestCode == GET_ALL_USERS) {
			url = urlGetUsers;
		} else if (requestCode == GET_USER_ID) {
			url = urlGetUserId;
		} else if (requestCode == MATCH_TWO_USERS) {
			url = urlMatchContacts;
		}
		
		this.requestCode = requestCode;

		this.requestParams = params;
		mMatchedUser = matchedUser;
		mMatcher = caller;
		this.index = index;
	}
	
	public DataGetter(int requestCode, PlaceholderFragment caller, List<NameValuePair> params) {
		if (requestCode == GET_ALL_USERS) {
			url = urlGetUsers;
		} else if (requestCode == GET_USER_ID) {
			url = urlGetUserId;
		} else if (requestCode == MATCH_TWO_USERS) {
			url = urlMatchContacts;
		}
		
		this.requestCode = requestCode;
		this.fragmentCaller = caller;
		this.requestParams = params;
	}
	
	public DataGetter(int requestCode, MatchedUserActivity caller, List<NameValuePair> params) {
		if (requestCode == GET_ALL_USERS) {
			url = urlGetUsers;
		} else if (requestCode == GET_USER_ID) {
			url = urlGetUserId;
		} else if (requestCode == MATCH_TWO_USERS) {
			url = urlMatchContacts;
		}
		
		this.requestCode = requestCode;
		this.activityCaller = caller;
		this.requestParams = params;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		boolean isSendOK = false;
		BufferedReader reader = null;
		try {

			// create http request
			String paramString = URLEncodedUtils.format(requestParams, "utf-8");
			HttpGet httpGet = new HttpGet(url + "?" + paramString);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse;

			// execute
			httpResponse = httpClient.execute(httpGet);

			// get response from server and parse it to json
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

			reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
			StringBuilder sb = new StringBuilder();
			line = reader.readLine();
			while (line != null) {
				sb.append(line + "\n");
				line = reader.readLine();
			}

			
			json = sb.toString();

			// try parse the string to a JSON object
			jObj = new JSONObject(json);

			// check json success tag
			int success = jObj.getInt("success");

			if (success == 1) {
				isSendOK = true;
			} else {
				// failed to update product
			}
		} catch (Exception e) {
			Log.d("MiLAB Class", "Data sender failed");
			e.printStackTrace();
		
		// Close the streams securely
		} finally {
			try {
				if (is != null) {
					is.close();	
				}
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
//		Log.i("In doInBackground", "Response is: " + json.toString());
		return isSendOK;
	}

	@Override
	protected void onPostExecute(Boolean isSendOK) {
		if (isSendOK) {
			try {
				if (mMatcher != null) {
					if (requestCode == GET_USER_ID) {
						mMatcher.matchContacts(jObj.getJSONArray("users"), mMatchedUser, index);
					} else if (requestCode == MATCH_TWO_USERS) {
						mMatcher.populateMatchedUserSharedContacts(jObj.getJSONArray("users"), mMatchedUser, index);
						mMatcher.findMatchedUserMutualFriends(mMatchedUser, index);
					} else if (requestCode == GET_ALL_USERS) {
						mMatcher.matchAllUsers(jObj.getJSONArray("users"));
					}
				// TODO: Still need all this?
				} else if (fragmentCaller != null) {
					fragmentCaller.setDataFromServer(jObj.getJSONArray("users"));

				} else if (activityCaller != null) {
					if (requestCode == GET_USER_ID) {
//						activityCaller.matchContacts(jObj.getJSONArray("users"));
					} else if (requestCode == MATCH_TWO_USERS) {
						activityCaller.setDataFromServer(jObj.getJSONArray("users"));
					}			
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
//			CharSequence text = "Get Data Faild!";
//			int duration = Toast.LENGTH_SHORT;
//			Toast toast = Toast.makeText(parentCaller, text, duration);
//			toast.show();
		}
	}

}
