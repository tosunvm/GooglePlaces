package com.vmware.android.googleplaces;

import java.util.Date;

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
    // private static final int VERSION = 1;
    private static final int VERSION = 3;
    
    private static final String TABLE_PLACE = "place";
    private static final String COLUMN_PLACE_PLACEID = "place_id";
    private static final String COLUMN_PLACE_PLACENAME = "place_name";
    private static final String COLUMN_PLACE_FORMATTEDADDRESS = "formatted_address";
    private static final String COLUMN_PLACE_WEBSITEURL = "web_site_url";
    private static final String COLUMN_PLACE_RATING = "rating";
    private static final String COLUMN_PLACE_LATITUDE = "latitude";
    private static final String COLUMN_PLACE_LONGITUDE = "longitude";

    private static final String TABLE_LISTINGRESULTS = "listing_results";
    private static final String COLUMN_LISTINGRESULTS_TIMESTAMP = "keyword";
    private static final String COLUMN_LISTINGRESULTS_KEYWORD = "keyword";
    private static final String COLUMN_LISTINGRESULTS_RADIUS = "radius";
    private static final String COLUMN_LISTINGRESULTS_LOCATION = "location";
    private static final String COLUMN_LISTINGRESULTS_PAGETOKEN = "page_token";
    private static final String COLUMN_LISTINGRESULTS_LISTINGRESULT = "listing_result";
    
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
        db.execSQL("create table place (_id integer primary key autoincrement, place_id varchar(100) unique, place_name varchar(100)," +
    			" formatted_address varchar(100), web_site_url varchar(100)," + 
    			" rating real, latitude real, longitude real)");
        // keep last 6 rows to avoid ballooning of table
        db.execSQL(
        		"CREATE TRIGGER place_delete_till_5 INSERT ON place WHEN (select count(*) from place)>10" +
        			" BEGIN" +
        				" DELETE FROM place WHERE place._id IN  (SELECT _id FROM place ORDER BY _id limit (select count(*) -5 from place ));" +
        			" END;"
        		);
        // create "ListingResults" table.
        db.execSQL("create table listing_results (_id integer primary key autoincrement," + 
        		"  timestamp integer, keyword varchar(100), radius varchar(10)," +
    			" location varchar(30), page_token varchar(100)," + 
    			" listing_result text)");
        // keep last 6 rows to avoid ballooning of table
        db.execSQL(
        		"CREATE TRIGGER listing_results_delete_till_5 INSERT ON listing_results WHEN (select count(*) from listing_results)>10" +
        			" BEGIN" +
        				" DELETE FROM listing_results WHERE listing_results._id IN  (SELECT _id FROM listing_results ORDER BY _id limit (select count(*) -5 from listing_results ));" +
        			" END;"
        		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {

		case 1:
			// upgrade logic from version 1 to 2

		case 2:
			// upgrade logic from version 2 to 3
			/*
			db.execSQL("DROP TABLE place");
	        db.execSQL("create table place (_id integer primary key autoincrement, place_id varchar(100) unique, place_name varchar(100)," +
        			" formatted_address varchar(100), web_site_url varchar(100)," + 
        			" rating real, latitude real, longitude real)");
	        // keep last 6 rows to avoid ballooning of table
	        db.execSQL(
	        		"CREATE TRIGGER place_delete_till_5 INSERT ON place WHEN (select count(*) from place)>10" +
	        			" BEGIN" +
	        				" DELETE FROM place WHERE place._id IN  (SELECT _id FROM place ORDER BY _id limit (select count(*) -5 from place ));" +
	        			" END;"
	        		);
	        */
		case 3:
			// upgrade logic from version 3 to 4
			break;
		default:
			throw new IllegalStateException(
					"onUpgrade() with unknown newVersion: " + newVersion);
		}

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

    public long insertListingResults(String query, String radius, String location, String nextPage, String listingResultsJson) {
    	// TODO: 
    	// 1. There is nothing avoiding multiple rows to be inserted for the same <query, radius, location, nextPage> combination
    	//    at this point.
    	// 2. You can also add logic here to limit the table size to 10 rows or smth. This is achieved by triggers above.
    	Date insertDate = new Date();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LISTINGRESULTS_TIMESTAMP, insertDate.getTime());
        cv.put(COLUMN_LISTINGRESULTS_KEYWORD, query);
        cv.put(COLUMN_LISTINGRESULTS_RADIUS, radius);
        cv.put(COLUMN_LISTINGRESULTS_LOCATION, location);
        cv.put(COLUMN_LISTINGRESULTS_PAGETOKEN, nextPage);
        cv.put(COLUMN_LISTINGRESULTS_LISTINGRESULT, listingResultsJson);
        return getWritableDatabase().insert(TABLE_LISTINGRESULTS, null, cv);
    }
    
    /*
     * This is the actual db operation behind the convenience getListingResults() below.
     */
    public ListingResultsCursor queryListingResults(String query, String radius, String location, String nextPage) {
    	String selection = COLUMN_LISTINGRESULTS_KEYWORD + " = ? AND " +
    						COLUMN_LISTINGRESULTS_RADIUS + " = ? AND " +
    						COLUMN_LISTINGRESULTS_LOCATION + " = ? AND " +
    						COLUMN_LISTINGRESULTS_PAGETOKEN + " = ?";
        Cursor wrapped = getReadableDatabase().query(TABLE_LISTINGRESULTS, 
                null, // all columns 
                selection, // where
                new String[]{ query, radius, location, nextPage }, 
                null, // group by
                null, // having
                null, // order by
                "1"); // limit 1
        return new ListingResultsCursor(wrapped);
    }

    public String getListingResults(String query, String radius, String location, String nextPage) {
    	String places = null;
        ListingResultsCursor cursor = queryListingResults(query, radius, location, nextPage);
        cursor.moveToFirst();
        // if we got a row, get a location
        if (!cursor.isAfterLast())
            places = cursor.getListingResults();
        cursor.close();
        return places;
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

    public static class ListingResultsCursor extends CursorWrapper {
        
        public ListingResultsCursor(Cursor c) {
            super(c);
        }
        
        public String getListingResults() {
            if (isBeforeFirst() || isAfterLast())
                return null;
            String listingResults = getString(getColumnIndex(COLUMN_LISTINGRESULTS_LISTINGRESULT));
            return listingResults;
        }
    }

}
