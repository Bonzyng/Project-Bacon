package com.filos.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.nadav.facebookintegrationapp.R;

/*
 * TODO: Turn this into the main app screen, with the scan button and everything.
 */
public class SelectionFragment extends Fragment {
	private static final String TAG = "SelectionFragment";
	
	private ProfilePictureView profilePictureView;
	private TextView userNameView;
	
	private String user_ID;
	
	private ListView listView;
	private List<BaseListElement> listElements;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	private static final int REAUTH_ACTIVITY_CODE = 100;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.filoser_fragment_layout, 
	            container, false);
	    
//	    listView = (ListView) view.findViewById(R.id.selection_list);
//	    listElements = new ArrayList<BaseListElement>();
////	    listElements.add(new PeopleListElement(0));
////	    listView.setAdapter(new ActionListAdapter(getActivity(),
////	    		R.id.selection_list, listElements));
//	    
//	    profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
//	    profilePictureView.setCropped(true);
//	    
//	    userNameView = (TextView) view.findViewById(R.id.selection_user_name);
//	    
//	    Session session = Session.getActiveSession();
//	    if (session != null && session.isOpened()) {
//	    	makeMeRequest(session);
//	    }
	    return view;
	}
	
	private void makeMeRequest(final Session session) {
		Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
			
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (session == Session.getActiveSession()) {
					if (user != null) {
//						profilePictureView.setProfileId(user.getId());
//						
//						userNameView.setText(user.getName());
//						
//						user_ID = "s" + user.getId();
					}
				}
				if (response.getError() != null) {
					// Handle error, will do later. Or never, probably.
				}
				
			}
		});
		request.executeAsync();
	}
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
//			makeMeRequest(session);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REAUTH_ACTIVITY_CODE) { 
			uiHelper.onActivityResult(requestCode, resultCode, data);
		} else if (resultCode == Activity.RESULT_OK) {
			// Do nothing for now.
		}
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	/*
	private void startPickerActivity(Uri data, int requestCode) {
		String test = MainActivity.userFacebookId;
		Intent intent = new Intent();
		intent.setData(data);
		intent.setClass(getActivity(), PickerActivity.class);
		startActivityForResult(intent, requestCode);
	}
	*/
	
	private void startUserListActivity() {
		Intent intent = new Intent();
		intent.putExtra("User_ID", user_ID);
		intent.setClass(getActivity(), UserListActivity.class);
		startActivity(intent);
	}
	
//	private class ActionListAdapter extends ArrayAdapter<BaseListElement> {
//		private List<BaseListElement> listElements;
//		
//		public ActionListAdapter(Context context, int resourceId,
//				List<BaseListElement> listElements) {
//			super(context, resourceId, listElements);
//			
//			this.listElements = listElements;
//			
//			for (int i = 0; i < listElements.size(); i++) {
//				listElements.get(i).setAdapter(this);
//			}
//		}
//		
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View view = convertView;
//			if (view == null) {
//				LayoutInflater inflater = (LayoutInflater) getActivity()
//						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				view = inflater.inflate(R.layout.listitem, null);
//						
//			}
//			
//			BaseListElement listElement = listElements.get(position);
//			if (listElement != null) {
//				view.setOnClickListener(listElement.getOnClickListener());
//				ImageView icon = (ImageView) view.findViewById(R.id.icon);
//				TextView text1 = (TextView) view.findViewById(R.id.text1);
//				TextView text2 = (TextView) view.findViewById(R.id.text2);
//				
//				if (icon != null) {
//					icon.setImageDrawable(listElement.getIcon());
//				}
//				
//				if (text1 != null) {
//					text1.setText(listElement.getText1());
//				}
//				
//				if (text2 != null) {
//					text2.setText(listElement.getText2());
//				}
//			}
//			
//			return view;
//		}
//	}
//	
//	private class PeopleListElement extends BaseListElement {
//		
//		public PeopleListElement(int requestCode) {
//			super(getActivity().getResources().getDrawable(R.drawable.add_friends), 
//					getActivity().getResources().getString(R.string.action_people),
//					getActivity().getResources().getString(R.string.action_people_default), 
//					requestCode);
//		}
//		
//		@Override
//		protected View.OnClickListener getOnClickListener() {
//			return new View.OnClickListener() {
//				@Override
//				public void onClick(View view) {
////					startPickerActivity(PickerActivity.FRIEND_PICKER, getRequestCode());
//					startUserListActivity();
//				}
//			};
//		}
//	}
	

}
