package com.vmware.android.googleplaces;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceDetail {

	private static final String TAG_PLACE_ID = "place_id";
    private static final String TAG_FORMATTED_ADDRESS ="formatted_address";
	private static final String TAG_NAME = "name";
	private static final String TAG_RATING = "rating";
	private static final String TAG_WEBSITE = "website";
	private static final String TAG_GEOMETRY = "geometry";
	private static final String TAG_LOCATION = "location";
	private static final String TAG_LAT = "lat";
	private static final String TAG_LON = "lng";
    
	//private UUID mId;
	private String mPlaceName;
	private String mPlaceId;
	private String mFormattedAddress;
	private String mFormattedPhone;
	private String mGoogleUrl;
	private String mWebSiteUrl;
	private double mRating;
	private double mLat;
	private double mLon;
	
	public PlaceDetail(String placeId){
		mPlaceId = placeId;
	}
	
	public PlaceDetail(JSONObject placeDetail) throws JSONException {
		mPlaceId = placeDetail.getString(TAG_PLACE_ID);
		mFormattedAddress = placeDetail.getString(TAG_FORMATTED_ADDRESS);
		mPlaceName = placeDetail.getString(TAG_NAME);
		mRating = placeDetail.optDouble(TAG_RATING);
		mWebSiteUrl = placeDetail.optString(TAG_WEBSITE);
		
		// Geometry node is JSON Object
		JSONObject geometry = placeDetail.getJSONObject(TAG_GEOMETRY);
		// Location node is JSON Object
		JSONObject location = geometry.getJSONObject(TAG_LOCATION);
		mLat = location.optDouble(TAG_LAT);
		mLon = location.optDouble(TAG_LON);

    }

	public String getPlaceName() {
		return mPlaceName;
	}

	public void setPlaceName(String name) {
		mPlaceName = name;
	}

	public String getId() {
		return mPlaceId;
	}

	public String getFormattedAddress() {
		return mFormattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		mFormattedAddress = formattedAddress;
	}

	public String getFormattedPhone() {
		return mFormattedPhone;
	}

	public String getWebSiteUrl() {
		return mWebSiteUrl;
	}

	public void setWebSiteUrl(String webSiteUrl) {
		mWebSiteUrl = webSiteUrl;
	}

	public double getLat() {
		return mLat;
	}

	public void setLat(double lat) {
		mLat = lat;
	}

	public double getLon() {
		return mLon;
	}

	public void setLon(double lon) {
		mLon = lon;
	}

	public double getRating() {
		return mRating;
	}

	public void setRating(double rating) {
		mRating = rating;
	}

	@Override
    public String toString() {
        return mPlaceName;
    }

}
