package com.nadav.facebookintegrationapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.os.AsyncTask;

public class DataSender extends AsyncTask<Void, Void, Boolean> {


	// url to update user status
	private final String urlSendPhoneNumber = "http://liron.milab.idc.ac.il/php/bacon_send_phonenumber.php";
	private final String urlCreateTable = "http://liron.milab.idc.ac.il/php/bacon_create_new_table.php";
	private String url;
	
	private List<NameValuePair> postParams;
	private InputStream is = null;
	private String line = "";
	private String json = "";
	private JSONObject jObj = null;

	public DataSender(MainActivity activity, List<NameValuePair> params) {
		this.postParams = params;
	}
	
	public DataSender(int target, List<NameValuePair> params) {
		if (target == 0) {
			url = urlSendPhoneNumber;
		} else if (target == 1) {
			url = urlCreateTable;
		}
		this.postParams = params;
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
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(postParams, "utf-8"));
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse;

			// execute
			httpResponse = httpClient.execute(httpPost);

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
			} else {
				// failed to update product
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isSendOK;
	}
/*
	@Override
	protected void onPostExecute(Boolean isSendOK) {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		
		if (isSendOK) {
		//	parentActivity.saveUID(uid);
			CharSequence text = "Send Data Succeeded!";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(parentActivity, text, duration);
			toast.show();
		} else {
			CharSequence text = "Send Data Faild!";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(parentActivity, text, duration);
			toast.show();
		}
	}
	*/

}
