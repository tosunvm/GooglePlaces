package com.vmware.android.googleplaces;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/*
 * Datatypes In SQLite Version 3: https://www.sqlite.org/datatype3.html
 * You can use TEXT for your large blob of text.
 * 
 */
public class PlaceDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "places.sqlite";
    private static final int VERSION = 1;
    
    private static final String TABLE_PLACE = "place";
    private static final String COLUMN_PLACE_PLACEID = "place_id";
    private static final String COLUMN_PLACE_PLACENAME = "place_name";
    private static final String COLUMN_PLACE_FORMATTEDADDRESS = "formatted_address";
    private static final String COLUMN_PLACE_WEBSITEURL = "web_site_url";
    private static final String COLUMN_PLACE_RATING = "rating";
    private static final String COLUMN_PLACE_LATITUDE = "latitude";
    private static final String COLUMN_PLACE_LONGITUDE = "longitude";
    
    private static PlaceDatabaseHelper sPlaceDatabaseHelper;
    
    public static PlaceDatabaseHelper get(Context c) {
        if (sPlaceDatabaseHelper == null) {
            sPlaceDatabaseHelper = new PlaceDatabaseHelper(c);
        }
        return sPlaceDatabaseHelper;
    }

    private PlaceDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) {
        // create the "place" table
        db.execSQL("create table place (place_id varchar(100) primary key, place_name varchar(100)," +
        			" formatted_address varchar(100), web_site_url varchar(100)," + 
        			" rating real, latitude real, longitude real)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
    public long insertPlace(PlaceDetail pDetail) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PLACE_LATITUDE, pDetail.getLat());
        cv.put(COLUMN_PLACE_LONGITUDE, pDetail.getLon());
        cv.put(COLUMN_PLACE_PLACEID, pDetail.getId());
        cv.put(COLUMN_PLACE_PLACENAME, pDetail.getPlaceName());
        cv.put(COLUMN_PLACE_FORMATTEDADDRESS, pDetail.getFormattedAddress());
        cv.put(COLUMN_PLACE_WEBSITEURL, pDetail.getWebSiteUrl());
        cv.put(COLUMN_PLACE_RATING, pDetail.getRating());
        return getWritableDatabase().insert(TABLE_PLACE, null, cv);
    }
    
    /*
     * This is the actual db operation behind the convenience getPlaceDetail() below.
     */
    public PlaceCursor queryPlaceDetail(String placeId) {
        Cursor wrapped = getReadableDatabase().query(TABLE_PLACE, 
                null, // all columns 
                COLUMN_PLACE_PLACEID + " = ?", // limit to the given run
                new String[]{ String.valueOf(placeId) }, 
                null, // group by
                null, // having
                null, // order by
                "1"); // limit 1
        return new PlaceCursor(wrapped);
    }

    /*
     * This is a convenience method to call.
     * 
     * Actually you could have queried for the PlaceDetail from a single method
     * but I wanted to use the cursor implementation here. This will help me
     * return multiple place details in the future if need be.
     */
    public PlaceDetail getPlaceDetail(String placeId) {
    	PlaceDetail place = null;
        PlaceCursor cursor = queryPlaceDetail(placeId);
        cursor.moveToFirst();
        // if we got a row, get a location
        if (!cursor.isAfterLast())
            place = cursor.getPlace();
        cursor.close();
        return place;
    }

    public static class PlaceCursor extends CursorWrapper {
        
        public PlaceCursor(Cursor c) {
            super(c);
        }
        
        public PlaceDetail getPlace() {
            if (isBeforeFirst() || isAfterLast())
                return null;
            // first get the id out so we can use the constructor
            String pId = getString(getColumnIndex(COLUMN_PLACE_PLACEID));
            PlaceDetail pDetail = new PlaceDetail(pId);
            // populate the remaining properties
            pDetail.setLon(getDouble(getColumnIndex(COLUMN_PLACE_LONGITUDE)));
            pDetail.setLat(getDouble(getColumnIndex(COLUMN_PLACE_LATITUDE)));
            pDetail.setFormattedAddress(getString(getColumnIndex(COLUMN_PLACE_FORMATTEDADDRESS)));
            pDetail.setPlaceName(getString(getColumnIndex(COLUMN_PLACE_PLACENAME)));
            pDetail.setWebSiteUrl(getString(getColumnIndex(COLUMN_PLACE_WEBSITEURL)));
            pDetail.setRating(getDouble(getColumnIndex(COLUMN_PLACE_RATING)));
            return pDetail;
        }
    }

}
