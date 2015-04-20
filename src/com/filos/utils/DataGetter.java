package com.filos.utils;

import java.io.BufferedReader;
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

import com.filos.app.FilosResultsActivity;
import com.filos.app.MatchedUserActivity;
import com.filos.app.UserListActivity.PlaceholderFragment;

public class DataGetter extends AsyncTask<Void, Void, Boolean> {

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
		if (requestCode == 0) {
			url = urlGetUsers;
		} else if (requestCode == 1) {
			url = urlGetUserId;
		} else if (requestCode == 2) {
			url = urlMatchContacts;
		}
		
		this.requestCode = requestCode;

		this.requestParams = params;
		mMatchedUser = matchedUser;
		mMatcher = caller;
		this.index = index;
	}
	
	public DataGetter(int requestCode, PlaceholderFragment caller, List<NameValuePair> params) {
		if (requestCode == 0) {
			url = urlGetUsers;
		} else if (requestCode == 1) {
			url = urlGetUserId;
		} else if (requestCode == 2) {
			url = urlMatchContacts;
		}
		
		this.requestCode = requestCode;
		this.fragmentCaller = caller;
		this.requestParams = params;
	}
	
	public DataGetter(int requestCode, MatchedUserActivity caller, List<NameValuePair> params) {
		if (requestCode == 0) {
			url = urlGetUsers;
		} else if (requestCode == 1) {
			url = urlGetUserId;
		} else if (requestCode == 2) {
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

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
			StringBuilder sb = new StringBuilder();
			line = reader.readLine();
			while (line != null) {
				sb.append(line + "\n");
				line = reader.readLine();
			}

			is.close();
			json = sb.toString();

			// try parse the string to a JSON object
			jObj = new JSONObject(json);

			// check json success tag
			int success = jObj.getInt("success");

			if (success == 1) {
				isSendOK = true;
//				Log.d("MiLAB Class", "Data sender succeed: " + json.toString());
			} else {
				// failed to update product
//				Log.d("MiLAB Class", "Data sender failed" + json.toString());
			}
		} catch (Exception e) {
			Log.d("MiLAB Class", "Data sender failed");
			e.printStackTrace();
		}
//		Log.i("In doInBackground", "Response is: " + json.toString());
		return isSendOK;
	}

	@Override
	protected void onPostExecute(Boolean isSendOK) {
		if (isSendOK) {
			try {
				if (mMatcher != null) {
					if (requestCode == 1) {
						mMatcher.matchContacts(jObj.getJSONArray("users"), mMatchedUser, index);
					} else if (requestCode == 2) {
						mMatcher.populateMatchedUserSharedContacts(jObj.getJSONArray("users"), mMatchedUser, index);
						mMatcher.findMatchedUserMutualFriends(mMatchedUser);
					} else if (requestCode == 0) {
						mMatcher.matchAllUsers(jObj.getJSONArray("users"));
					}
				// TODO: Still need all this?
				} else if (fragmentCaller != null) {
					fragmentCaller.setDataFromServer(jObj.getJSONArray("users"));

				} else if (activityCaller != null) {
					if (requestCode == 1) {
//						activityCaller.matchContacts(jObj.getJSONArray("users"));
					} else if (requestCode == 2) {
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
