package com.nadav.facebookintegrationapp;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

public class MainActivity extends FragmentActivity {
	private static final String TAG = "MainActivity";
	
	private static final int SPLASH = 0;
	private static final int SELECTION = 1;
	private static final int SETTINGS = 2;
	private static final int FRAGMENT_COUNT = SETTINGS + 1;
	
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	
	private boolean isResumed = false;
	
	private MenuItem settings;
	
	// UserID and ProfileName to be extracted in displayContacts
	private String userId = "";
	private String userProfileName = "";
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = 
			new Session.StatusCallback() {
				
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					onSessionStateChange(session, state, exception);
				}
			};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.activity_main);
	    
	    FragmentManager fm = getSupportFragmentManager();
	    fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
	    fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
	    fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
	    
	    FragmentTransaction transaction = fm.beginTransaction();
	    for (int i = 0; i < fragments.length; i++) {
	    	transaction.hide(fragments[i]);
	    }
	    transaction.commit();
	}
	
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			if (i == fragmentIndex) {
				transaction.show(fragments[i]);
			} else {
				transaction.hide(fragments[i]);
			}
		}
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (isResumed) {
			FragmentManager fm = getSupportFragmentManager();
			int backStackSize = fm.getBackStackEntryCount();
			for (int i = 0; i < backStackSize; i++) {
				fm.popBackStack();
			}
			
			if (state.isOpened()) {
				showFragment(SELECTION, false);
			} else if (state.isClosed()) {
				showFragment(SPLASH, false);
			}
		}
	}
	
	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		Session session = Session.getActiveSession();
		
		if (session != null && session.isOpened()) {
			showFragment(SELECTION, false);
		} else {
			showFragment(SPLASH, false);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	    isResumed = true;
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    isResumed = false;
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (fragments[SELECTION].isVisible()) {
			if (menu.size() == 0) {
				settings = menu.add(R.string.settings);
			}
			return true;
		} else {
			menu.clear();
			settings = null;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.equals(settings)) {
			showFragment(SETTINGS, true);
			return true;
		}
		return false;
	}
	
	public void showMenu(View view) {
		showFragment(SETTINGS, true);
	}

	// TODO: Move this to a separate class that will run once Login button is pressed
	private class SendContactsAsync extends AsyncTask<Void, Void, Void> {
		
		private String tableName;
		
		public SendContactsAsync(String tableName) {
			this.tableName = tableName;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CharSequence text = "Sending contacts to DB";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(MainActivity.this, text, duration);
			toast.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			ContentResolver cr = getContentResolver();
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
			Toast toast = Toast.makeText(MainActivity.this, text, duration);
			toast.show();
		}
	}
	public static String userFacebookId;
		
	public void displayContacts(View view) {
		Log.i("I AM IN DISPLAY CONTACTS", "IN THE MAIN ACTIVITY!!");
		final Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	        // If the session is open, make an API call to get user data
	        // and define a new callback to handle the response
	        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {

				@Override
				public void onCompleted(GraphUser user, Response response) {
					Log.i("HI", "I AM IN ONCOMPLETED");
					 // If the response is successful
	                if (session == Session.getActiveSession()) {
	                    if (user != null) {
	                        userId = user.getId(); //user id
	                        userProfileName = user.getName(); //user's profile name
	                        
	                		userFacebookId = "s" + userId; // Table name can't start with digit

	                		// Create a new table for this user's contact list
	                		ArrayList<NameValuePair> contactsData = new ArrayList<NameValuePair>();
	                		contactsData.add(new BasicNameValuePair("tableName", userFacebookId));
	                		new DataSender(1, contactsData).execute(); // 1 => create new table
	                		
	                		// Add this user to the users table
	                		ArrayList<NameValuePair> userData = new ArrayList<NameValuePair>();
	                		userData.add(new BasicNameValuePair("userFacebookId", userFacebookId));
	                		userData.add(new BasicNameValuePair("userProfileName", userProfileName));
	                		new DataSender(2, userData).execute(); // 2 => add user to user table
	                		
	                		
	                		
	                		// Fill phonenumber table with contact names + numbers
	                		new SendContactsAsync(userFacebookId).execute();
	                    }   
	                }   
				}   
	        }); 
	        request.executeAsync();
	    } else {
	    	Log.i("displayContacts", "Session is null or closed");
	    }
	}	
}
