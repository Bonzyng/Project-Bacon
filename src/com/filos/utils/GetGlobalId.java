package com.filos.utils;

import java.io.BufferedReader;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Attempt to get the absolute url name of a person's profile page (e.g for Nadav Glickman, 
 * www.facebook.com/nadav.glickman - to get the nadav.glickman part).
 * It's under the 'location' field in the url response. But it doesn't show up when
 * we receive the response on Android. Gotta trick it somehow that we're a browser maybe.
 * @author Nadav
 *
 */
public class GetGlobalId extends AsyncTask<Void, Void, Void> {
	
	private static final String fbUrl = "http://facebook.com/";
	private long appScopeId;
	private String url;
	
	public GetGlobalId(long appScopeId) {
		this.appScopeId = appScopeId;
		url = fbUrl + appScopeId;
	}

	@Override
	protected Void doInBackground(Void... params) {
		BufferedReader reader = null;
		
		try {
			HttpGet httpGet = new HttpGet(url);

			HttpContext httpContext = new BasicHttpContext();
			httpContext.setAttribute(CoreProtocolPNames.USER_AGENT, 
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.65 Safari/537.36");
			httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.65 Safari/537.36");
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse;
			
			httpResponse = httpClient.execute(httpGet, httpContext);
			
			Header[] headers = httpResponse.getHeaders("Location");
			
			Log.d("Http response 'location' header:", headers[0].toString());
			
		} catch (Exception e) {}
		
		return null;
	}

}
