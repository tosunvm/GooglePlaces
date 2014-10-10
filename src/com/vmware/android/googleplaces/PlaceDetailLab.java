package com.vmware.android.googleplaces;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

public class PlaceDetailLab {
	private static final String TAG = "PlaceDetailLab";
    private static final String FILENAME = "placeDetails.json";

	private static PlaceDetailLab sPlaceDetailLab;
	private Context mAppContext;
	private ArrayList<PlaceDetail> mPlaceDetails;

	// TODO
	// When you get a chance implement a way to persist your place details
	// private CriminalIntentJSONSerializer mSerializer;

	private PlaceDetailLab(Context appContext){
		mAppContext = appContext;
		try {
            //mCrimes = mSerializer.loadCrimes();
			mPlaceDetails = new ArrayList<PlaceDetail>();
        } catch (Exception e) {
            //mCrimes = new ArrayList<Crime>();
            Log.e(TAG, "Error loading place details: ", e);
        }

	}
	
	public static PlaceDetailLab get(Context c){
		if (sPlaceDetailLab == null){
			sPlaceDetailLab = new PlaceDetailLab(c.getApplicationContext());
		}
		return sPlaceDetailLab;
	}
	
	public ArrayList<PlaceDetail> getPlaceDetails(){
		return mPlaceDetails;
	}

	public void addPlaceDetail(PlaceDetail c){
		mPlaceDetails.add(c);
	}

	public PlaceDetail getPlaceDetail(String id){
		for (PlaceDetail c : mPlaceDetails){
			if (c.getId().equals(id))
				return c;			
		}
		return null;
	}

}
