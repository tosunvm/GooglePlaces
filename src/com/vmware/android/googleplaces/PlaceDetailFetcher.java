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

public class PlaceDetailFetcher {

	private static final String TAG = "PlaceDetailFetcher";
	// Used to construct:
	// "https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4&key=AIzaSyAfLfmLpEiyuSmZxgUvUaR34y5zC9FgISA";
	private static final String ENDPOINT = "https://maps.googleapis.com/maps/api/place/details/json";
    //private static final String API_KEY = "AIzaSyAfLfmLpEiyuSmZxgUvUaR34y5zC9FgISA";
	// JSON Node names
	private static final String TAG_PLACE = "result";

	private PlaceDetail parsePlaceDetail(String jsonStr) {

		PlaceDetail pDetail = null;
		// place detail JSONObject
		JSONObject place = null;

		try {
			JSONObject jsonObj = new JSONObject(jsonStr);

			// Getting JSON Object node
			place = jsonObj.getJSONObject(TAG_PLACE);
			//pDetail = new PlaceDetail(place.getString(TAG_PLACE_ID));
			pDetail = new PlaceDetail(place);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return pDetail;
	}

	public PlaceDetail apacheDownloadPlaceDetail(String placeId, Context c) {

		Log.i(TAG, "place_id called: " + placeId);
		
		String url = Uri.parse(ENDPOINT).buildUpon()
				.appendQueryParameter("placeid", placeId)
				.appendQueryParameter("key", c.getString(R.string.GOOGLE_PLACES_API_KEY))
				.build().toString();

		Log.i(TAG, "URL called: " + url);
		// Creating service handler class instance
		HttpApache sh = new HttpApache();
		// Making a request to url and getting response
		String jsonStr = sh.makeHttpCall(url, HttpApache.GET);

		longInfo("Apache Response: > " + jsonStr);
		if (jsonStr != null) {
			return parsePlaceDetail(jsonStr);
		} else {
			Log.e(TAG, "Couldn't get place detail from the url");
		}
		return null;
	}
/*
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

				parseItems(items, jsonStr);
			} else {
				Log.e(TAG, "Couldn't get any data from the url");
			}

		} catch (IOException ioe) {
			Log.e(TAG, "Failed to fetch items", ioe);
		}

		return items;

	}
*/
	public static void longInfo(String str) {
		if (str.length() > 4000) {
			Log.i(TAG, str.substring(0, 4000));
			longInfo(str.substring(4000));
		} else
			Log.i(TAG, str);
	}
}
