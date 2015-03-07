package com.vmware.android.googleplaces;

public class SearchQuery {

	private String mQueryText;
	private String mQueryLocation;
	
	private StringBuilder mNextPage;
	private double mLat;
	private double mLon;
	
	public SearchQuery(String queryText, String queryLocation){
		mQueryText = queryText;
		mQueryLocation = queryLocation;
		mNextPage = new StringBuilder();
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

	public String getQueryText() {
		return mQueryText;
	}

	public void setQueryText(String queryText) {
		mQueryText = queryText;
	}

	public String getQueryLocation() {
		return mQueryLocation;
	}

	public void setQueryLocation(String queryLocation) {
		mQueryLocation = queryLocation;
	}

	public StringBuilder getNextPage() {
		return mNextPage;
	}

	public void setNextPage(StringBuilder nextPage) {
		mNextPage = nextPage;
	}
	
	public void clearNextPage(){
		mNextPage.setLength(0);
	}

	@Override
    public String toString() {
        return mQueryText;
    }

}
