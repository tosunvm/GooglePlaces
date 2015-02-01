package com.vmware.android.googleplaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class PlaceFetcher {

	private static final String TAG = "PlaceFetcher";
	// JSON Node names
	private static final String TAG_PLACES = "results";
	private static final String TAG_NEXT_PAGE_TOKEN = "next_page_token";
	private static final String TAG_PLACE_ID = "place_id";
	private static final String TAG_NAME = "name";
	private static final String TAG_VICINITY = "vicinity";
	private static final String TAG_RATING = "rating";
	private static final String TAG_GEOMETRY = "geometry";
	private static final String TAG_LOCATION = "location";
	private static final String TAG_LAT = "lat";
	private static final String TAG_LON = "lng";
	
	private static final String ENDPOINT = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    //private static final String API_KEY = "AIzaSyAfLfmLpEiyuSmZxgUvUaR34y5zC9FgISA";
    private static final String LOCATION = "42.380609,-71.175437";
    private static final String RADIUS = "3000"; // this is in meters

    // Note that nextPage is being used as an in/out parameter
	void parseItems(ArrayList<HashMap<String, String>> items, String jsonStr, StringBuilder nextPage) {

		// places JSONArray
		JSONArray places = null;

		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			// Get next_page_token
			nextPage.append(jsonObj.optString(TAG_NEXT_PAGE_TOKEN));
			// Getting JSON Array node
			places = jsonObj.getJSONArray(TAG_PLACES);

			// looping through All Contacts
			for (int i = 0; i < places.length(); i++) {
				JSONObject p = places.getJSONObject(i);

				String id = p.getString(TAG_PLACE_ID);
				String name = p.getString(TAG_NAME);
				String vicinity = p.getString(TAG_VICINITY);
				//double rating = p.getDouble(TAG_RATING);
				double rating = p.optDouble(TAG_RATING);

				// Geometry node is JSON Object
				JSONObject geometry = p.getJSONObject(TAG_GEOMETRY);
				// Location node is JSON Object
				JSONObject location = geometry.getJSONObject(TAG_LOCATION);
				double lat = location.getDouble(TAG_LAT);
				double lon = location.getDouble(TAG_LON);

				// tmp hashmap for single place
				HashMap<String, String> place = new HashMap<String, String>();

				// adding each child node to HashMap key => value
				place.put(TAG_PLACE_ID, id);
				place.put(TAG_NAME, name);
				place.put(TAG_VICINITY, vicinity);
				place.put(TAG_RATING, String.valueOf(rating));
				place.put(TAG_LAT, Double.toString(lat));
				place.put(TAG_LON, String.valueOf(lon));

				// adding contact to contact list
				items.add(place);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<HashMap<String, String>> apacheDownloadPlaceItems(
			String query, StringBuilder nextPage, Context c) {
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
		
		Log.i(TAG, "query called: " + query);
		
		String url = Uri.parse(ENDPOINT).buildUpon()
				.appendQueryParameter("location", LOCATION)
				.appendQueryParameter("radius", RADIUS)
				.appendQueryParameter("keyword", query)
				.appendQueryParameter("pagetoken", nextPage.toString())
				.appendQueryParameter("key", c.getString(R.string.GOOGLE_PLACES_API_KEY))
				.build().toString();

		Log.i(TAG, "URL called: " + url);
		
		// Creating service handler class instance
		HttpApache sh = new HttpApache();
		// Making a request to url and getting response
		String jsonStr = sh.makeHttpCall(url, HttpApache.GET);

		longInfo("Apache Response: > " + jsonStr);
		if (jsonStr != null) {
			// Clear nextPage value
			//nextPage.delete(0, nextPage.length());
			nextPage.setLength(0);
			parseItems(items, jsonStr, nextPage);
		} else {
			Log.e(TAG, "Couldn't get any data from the url");
		}

		return items;

	}

	public ArrayList<HashMap<String, String>> javaDownloadContactItems(
			String url) {
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
		Log.i(TAG, "URL called: " + url);
		// Creating service handler class instance
		HttpJava jh = new HttpJava();
		// Making a request to url and getting response
		try {
			String jsonStr = jh.getUrl(url);
			longInfo("Java Response: > " + jsonStr);

			if (jsonStr != null) {
				
				parseItems(items, jsonStr, new StringBuilder());
			} else {
				Log.e(TAG, "Couldn't get any data from the url");
			}

		} catch (IOException ioe) {
			Log.e(TAG, "Failed to fetch items", ioe);
		}

		return items;

	}

	public static void longInfo(String str) {
		if (str.length() > 4000) {
			Log.i(TAG, str.substring(0, 4000));
			longInfo(str.substring(4000));
		} else
			Log.i(TAG, str);
	}
}
