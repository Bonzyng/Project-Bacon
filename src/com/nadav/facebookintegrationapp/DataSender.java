package com.nadav.facebookintegrationapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.nadav.facebookintegrationapp.MainActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class DataSender extends AsyncTask<Void, Void, Boolean> {
	private ProgressDialog dialog;

	// url to update user status
	private final String url = "http://liron.milab.idc.ac.il/php/bacon_send_phonenumber.php";
	
	private List<NameValuePair> postParams;
	private InputStream is = null;
	private String line = "";
	private String json = "";
	private JSONObject jObj = null;
//	private MainActivity parentActivity;
//	private int uid = -1;

	public DataSender(MainActivity activity, List<NameValuePair> params) {
//		parentActivity = activity;
//		dialog = new ProgressDialog(parentActivity);
		this.postParams = params;
	}
	
	public DataSender(List<NameValuePair> params) {
//		parentActivity = activity;
//		dialog = new ProgressDialog(parentActivity);
		this.postParams = params;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
//		dialog.setMessage("Sending data to server, please wait.");
//		dialog.show();
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
//				uid = jObj.getInt("uid");
				Log.d("MiLAB Class", "Data sender succeed: " + json.toString());
			} else {
				// failed to update product
				Log.d("MiLAB Class", "Data sender failed" + json.toString());
			}
		} catch (Exception e) {
			Log.d("MiLAB Class", "Data sender failed");
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
