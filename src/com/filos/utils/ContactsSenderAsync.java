package com.filos.utils;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.filos.app.DataSender;
import com.filos.app.MainActivity;

public class ContactsSenderAsync extends AsyncTask<Void, Void, Void> {
	
	private String tableName;
	private MainActivity activity;
	
	public ContactsSenderAsync(String tableName, MainActivity activity) {
		this.tableName = tableName;
		this.activity = activity;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		CharSequence text = "Sending contacts to DB";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(activity, text, duration);
		toast.show();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		ContentResolver cr = activity.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);
		

		
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
			String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
			String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			if (Integer.parseInt(cur.getString(
					cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
							null, 
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
									new String[]{id}, null);
					while (pCur.moveToNext()) {
						String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						
						phoneNo = phoneNo.replaceAll("-", "");
						// removes dummy phone numbers 
						if (phoneNo.length() < 9) {
							continue;
						}
						phoneNo = phoneNo.substring((phoneNo.length()-9), phoneNo.length());
												
						// Create a params array for this contact and send to DB
						ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();
						data.add(new BasicNameValuePair("phoneNumber", phoneNo));
						data.add(new BasicNameValuePair("contactName", name));
						data.add(new BasicNameValuePair("tableName", this.tableName));
						new DataSender(0, data).execute(); // add rows to tableName
						
					} 
					pCur.close();
				}
			}
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		CharSequence text = "Finished sending all contacts to DB";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(activity, text, duration);
		toast.show();
	}
}