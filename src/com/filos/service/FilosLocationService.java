package com.filos.service;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.filos.utils.DataSender;

public class FilosLocationService extends Service {
	
	private static final String TAG = "FilosLocationService";
	private LocationManager mLocationManager = null;
	private static final int LOCATION_INTERVAL = 5 * 60 * 1000;
	private static final float LOCATION_DISTANCE = 10f;
	
	private class LocationListener implements android.location.LocationListener {
		
		Location mLastKnownLocation;
		
		public LocationListener(String provider) {
			mLastKnownLocation = new Location(provider);
		}

		@Override
		public void onLocationChanged(Location location) {
//			Log.d(TAG, "onLocationChanged: " + location);
			mLastKnownLocation.set(location);
			 SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(FilosLocationService.this);
			    String facebookId = sharedPref.getString("userFacebookId", "");
//			    Log.d(TAG, "FacebookID is: " + facebookId);
			    if (!facebookId.isEmpty()) {
			    	String gpsLat = Double.toString(mLastKnownLocation.getLatitude());
				    String gpsLong = Double.toString(mLastKnownLocation.getLongitude());;
				    ArrayList<NameValuePair> locationData = new ArrayList<NameValuePair>();
					locationData.add(new BasicNameValuePair("userFacebookId", facebookId));
					locationData.add(new BasicNameValuePair("gpsLat", gpsLat));
					locationData.add(new BasicNameValuePair("gpsLong", gpsLong));
		    		new DataSender(null, 3, null, locationData).execute(); // -> update user location
			    }
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}	
	}
	
	LocationListener[] mLocationListeners = new LocationListener[] {
	        new LocationListener(LocationManager.GPS_PROVIDER),
	        new LocationListener(LocationManager.NETWORK_PROVIDER)
	};
	
    private enum State {
        IDLE, WORKING;
    }

    private static State state;  
    private WakeLock wakeLock;

    static {
        state = State.IDLE;
    }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		Log.d(TAG, "Service started");
		if (state == State.IDLE) {
			state = State.WORKING;
			wakeLock.acquire();
			
			initializeLocationManager();
			
			try {
		        mLocationManager.requestLocationUpdates(
		                LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
		                mLocationListeners[1]);
		    } catch (java.lang.SecurityException ex) {
		        Log.i(TAG, "fail to request location update, ignore", ex);
		    } catch (IllegalArgumentException ex) {
		        Log.d(TAG, "network provider does not exist, " + ex.getMessage());
		    }
			
		    try {
		        mLocationManager.requestLocationUpdates(
		                LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
		                mLocationListeners[0]);
		    } catch (java.lang.SecurityException ex) {
		        Log.i(TAG, "fail to request location update, ignore", ex);
		    } catch (IllegalArgumentException ex) {
		        Log.d(TAG, "gps provider does not exist " + ex.getMessage());
		    }
			stopSelf();
		}
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
//		Log.d(TAG, "Service destroyed");
		super.onDestroy();
		state = State.IDLE;
		if (wakeLock.isHeld()) {
			wakeLock.release();
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
//		Log.d(TAG, "Service created");
        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "FilosLocationService");
	}
	
	private void initializeLocationManager() {
	    if (mLocationManager == null) {
	        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
	    }
	}
}
