package com.nadav.facebookintegrationapp;

import java.util.List;

import org.json.JSONObject;

import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

/*
 * TODO: Remove this activity. No need for it. Scan button should launch the compare +
 * new activity that will show the results.
 */
public class PickerActivity extends FragmentActivity {
	private static final String TAG = "PickerActivity";
	
	public static final Uri FRIEND_PICKER = Uri.parse("picker://friend");
	private FriendPickerFragment friendPickerFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pickers);
		
		Bundle args = getIntent().getExtras();
		FragmentManager manager = getSupportFragmentManager();
		Fragment fragmentToShow = null;
		Uri intentUri = getIntent().getData();
		
		if (FRIEND_PICKER.equals(intentUri)) {
			if (savedInstanceState == null) {
				friendPickerFragment = new FriendPickerFragment(args);
			} else {
				friendPickerFragment =
						(FriendPickerFragment) manager.findFragmentById(R.id.picker_fragment);
			}
			
			friendPickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {

				@Override
				public void onError(PickerFragment<?> fragment,
						FacebookException error) {
					PickerActivity.this.onError(error);
				}
			});
			
			friendPickerFragment.setOnDoneButtonClickedListener(
					new PickerFragment.OnDoneButtonClickedListener() {

						@Override
						public void onDoneButtonClicked(
								PickerFragment<?> fragment) {
							//startSelectedUserActivity(friendPickerFragment.getSelection()); // *****************************************
							//finishActivity();
							startActivity(new Intent().setClass(PickerActivity.this, DummyForDemo.class));
						}
					});
			friendPickerFragment.setMultiSelect(false);
			fragmentToShow = friendPickerFragment;
		} else {
			// Nothing to do, finish.
			setResult(RESULT_CANCELED);
			finish();
			return;
		}
		
		manager.beginTransaction()
			.replace(R.id.picker_fragment, fragmentToShow).commit();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (FRIEND_PICKER.equals(getIntent().getData())) {
			try {
				friendPickerFragment.loadData(false);
			} catch (Exception ex) {
				onError(ex);
			}
		}
	}
	
	private void onError(Exception error) {
		onError(error.getLocalizedMessage(), false);
	}
	
	private void onError(String error, final boolean finishActivity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.error_dialog_title)
			.setMessage(error)
			.setPositiveButton(R.string.error_dialog_button_text,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (finishActivity) {
								finishActivity();
							}						
						}
			});
	}
	
	private void finishActivity() {
		setResult(RESULT_OK, null);
		finish();
	}
	
	private void startSelectedUserActivity(List<GraphUser> selection) {
		// Make the API request, put extra into intent and start activity with it
		GraphUser user = selection.get(0);
		Log.i(TAG, user.toString());
		
		//String graphPath = "/" + user.getId().toString() + "?fields=context.friends(mutual_friends)";
		String userId = "/" + user.getId().toString();
		final String userName = user.getName();
		//Log.i(TAG, userId);
		Bundle params = new Bundle();
		params.putString("fields", "context.fields(mutual_friends)");
		
		new Request(Session.getActiveSession(), 
				userId,
				params, 
				HttpMethod.GET,
				new Request.Callback() {
					
					@Override
					public void onCompleted(Response response) {
						//Log.i(TAG, response.toString());
						Intent intent = new Intent(PickerActivity.this, SelectedUserActivity.class);
						String message = "Request received";
						
						GraphObject responseGraphObject = response.getGraphObject();
						FacebookRequestError error = response.getError();
						
						if (responseGraphObject != null) {
						//Log.i(TAG, responseGraphObject.toString());
						
						JSONObject userObj = responseGraphObject.getInnerJSONObject();
						
						intent.putExtra("responseObj", userObj.toString());
						intent.putExtra("userName", userName);
						startActivity(intent);
						setResult(RESULT_OK, null);
						finish();
						} else if (error != null) {
							message = "Error getting request info";
						}
						
						Toast.makeText(PickerActivity.this.getApplicationContext(),
	                            message,
	                            Toast.LENGTH_LONG).show();
					}
					
					
				}).executeAsync();
				
	}
}
