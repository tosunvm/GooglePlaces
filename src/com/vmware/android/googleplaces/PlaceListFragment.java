package com.vmware.android.googleplaces;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vmware.android.googleplaces.R;

/**
 * 
 * @author stosun
 * Google Maps Place API Documentation
 * https://developers.google.com/places/documentation/search

 * https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&types=food&name=cruise&key=AddYourOwnKeyHere
 * https://maps.googleapis.com/maps/api/place/textsearch/xml?query=restaurants+in+Belmont+MA&key=AddYourOwnKeyHere
 * 
 * "location, type, name" search
 * https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=42.380609,-71.175437&radius=1500&types=food&name=pizza&key=AIzaSyAfLfmLpEiyuSmZxgUvUaR34y5zC9FgISA
 * "location, keyword" search
 * https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=42.380609,-71.175437&radius=1500&keyword=pizza&key=AIzaSyAfLfmLpEiyuSmZxgUvUaR34y5zC9FgISA
 * https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=42.380609,-71.175437&radius=1500&keyword=liquor+store&key=AIzaSyAfLfmLpEiyuSmZxgUvUaR34y5zC9FgISA
 * 
 * TODO:
 * Features:
 * d- add search text box and search button on initial screen
 * - add location search box on initial screen
 * - store search results in a database on each search for persistence
 * d- add continuous scrolling for listing results
 * d- add caching for listing results
 * - Tie Done button on keyboard to search initiation
 * 
 * Code cleanup:
 * d- Pull hard coded text into resource files
 * d- Pull API_KEY to a single variable in a resource file
 * 
 * 
 */
public class PlaceListFragment extends ListFragment {
	
	private static final String TAG = "PlaceListFragment";
	// URL to get places JSON
    private static String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=42.380609,-71.175437&radius=1500&types=food&name=pizza&key=AIzaSyAfLfmLpEiyuSmZxgUvUaR34y5zC9FgISA";
    private ProgressDialog pDialog;

    private static final String TAG_NAME = "name";
	private static final String TAG_VICINITY = "vicinity";
	private static final String TAG_RATING = "rating";
	private static final String TAG_LAT = "lat";
	private static final String TAG_LON = "lng";
	private static final String TAG_PLACE_ID = "place_id";
	
	private static final int REQUEST_PLACE_DETAIL = 0;
 
    private Button mPlaceQueryButton;
	private EditText mQueryEditText;
    private Button mCreateCrimeButton;
	private TextView mEmptyListTextView;

    // Hashmap for ListView
    private ArrayList<HashMap<String, String>> mPlaceList;
    private PlaceDatabaseHelper mDbHelper;
    private StringBuilder mNextPage;
    private String mQueryText;
	private int mPages;
	private int mTotalCount = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.places_title);

		// Looks like retaining the fragment makes sure mPlaceList member variable is preserved
		// If you don't retain, even after page rotation, place list is reloaded from the server hinting "if" statement below is entered
		// Retaining and then checking mPlaceList existence prevents reload from server on screen orientation changes and when user exits the app
		//   via the home button and then comes back
		// If user exits the app via back button and enters the app again server is hit again though.
		// Also when you go to place details and come back via the back button place list is NOT reloaded from the server
		//   this was true even w/o the retain and if check below
		// If you go to place details and come back through the ancestral navigation on the menu bar server is hit again. I don't have a way to prevent this.
		//   actually I do through android:launchMode="singleTask" in the manifest file
		setRetainInstance(true);
		mNextPage = new StringBuilder();
		mQueryText = "pizza";
		mTotalCount = 0;
		// Best is to have a single instance of this database helper object for the application.
		// I might want to convert PlaceDatabaseHelper class to singleton in the future.
		mDbHelper = PlaceDatabaseHelper.get(getActivity());
		if (mPlaceList == null){
			mPlaceList = new ArrayList<HashMap<String, String>>();
			// Calling async task to get json
	        new GetPlaces().execute(new StringBuilder(mQueryText), mNextPage);		
		}
	}

	//@TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState) {
        
		View v = inflater.inflate(R.layout.fragment_generic_list, parent, false);

		mQueryEditText = (EditText) v.findViewById(R.id.place_query_edit_text);
		if (!mQueryEditText.getText().toString().trim().isEmpty()){
			mQueryText = mQueryEditText.getText().toString().trim();
		}
        mPlaceQueryButton = (Button)v.findViewById(R.id.place_query_button);
        mPlaceQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	// Do not change mPlaceList instance pointer otw. adapter will be pointing to old instance of mPlaceList.
            	// Just clear contents since this is a new search
            	// mPlaceList = new ArrayList<HashMap<String, String>>();
            	if (mPlaceList != null){
            		mPlaceList.clear();
            	}
            	// Get rid of the keyboard
            	InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
           		imm.hideSoftInputFromWindow(mQueryEditText.getWindowToken(), 0);
           		// 1/3 Get search text
    			mQueryText = mQueryEditText.getText().toString().trim();
    			// 2/3 Clear total item count you are keeping track of
    			mTotalCount = 0;
    			// 3/3 Clear nextPage value
    			mNextPage.setLength(0);
            	// Calling async task to get json
    	        new GetPlaces().execute(new StringBuilder(mQueryText), mNextPage);		
            }
        });
        
        // This causes a crash on first start since ListView member variable is not initialized yet for getListView().
        // getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
        ListView mListView = (ListView) v.findViewById(android.R.id.list);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// Log.d(TAG, String.format("firstVisibleItem: %1$d, visibleItemCount: %2$d, totalItemCount: %3$d", firstVisibleItem, visibleItemCount, totalItemCount));
				if ((visibleItemCount > 0)
						&& ((firstVisibleItem + visibleItemCount) == totalItemCount)
						&& (totalItemCount > mTotalCount)) {

					mTotalCount = totalItemCount;
					
					if (!mNextPage.toString().trim().isEmpty()){
						new GetPlaces().execute(new StringBuilder(mQueryText), mNextPage);
					}
					else{
				        String noNextPageText = getActivity().getString(R.string.no_next_page_text) + mTotalCount;
				        Toast.makeText(getActivity(), noNextPageText, Toast.LENGTH_LONG).show();						
					}
					
					Log.e(TAG, "firstVisibleItem: " + firstVisibleItem + " visibleItemCount: " + visibleItemCount +
							" totalItemCount: " + totalItemCount);
					Log.e(TAG, "Scrolled: current_page: " + mNextPage);
				}
			}

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}

		});
        
		mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text_view);
        mEmptyListTextView.setText(R.string.empty_list_text);
        mCreateCrimeButton = (Button)v.findViewById(R.id.create_crime_button);

        return v;
	}

	private class ContactAdapter extends ArrayAdapter<HashMap<String, String>> {

		public ContactAdapter(ArrayList<HashMap<String, String>> contacts) {
			super(getActivity(), 0, contacts);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// If we weren't given a view, inflate one
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.list_item_place, null);
			}
			// Configure the view for this Crime
			HashMap<String, String> c = getItem(position);

			TextView nameTextView = (TextView) convertView
					.findViewById(R.id.place_list_item_nameTextView);
			nameTextView.setText(c.get(TAG_NAME) + " - " + c.get(TAG_RATING) + " stars");
			TextView vicinityTextView = (TextView) convertView
					.findViewById(R.id.place_list_item_vicinityTextView);
			vicinityTextView.setText(c.get(TAG_VICINITY) + " - [" + c.get(TAG_LAT) + ", " + c.get(TAG_LON) + "]");

			return convertView;
		}

	}
	
	/**
     * Async task class to get places json by making HTTP call
     * */
    private class GetPlaces extends AsyncTask<StringBuilder, Void, ArrayList<HashMap<String, String>>> {
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getActivity().getString(R.string.please_wait_text));
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(StringBuilder... arg0) {
            return new PlaceFetcher().apacheDownloadPlaceItems(arg0[0].toString(), arg0[1], getActivity());
        }
 
        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            
            // UI should be updated on the main thread. onPostExecute is on the main thread
            // whereas doInBackground is on the spawned thread.
            if (mPlaceList != null){
        		mPlaceList.addAll(result);
        	}
        	else{
        		// This should never happen
        		mPlaceList = result;
        	}
            
            /**
             * Update parsed JSON data into ListView
             * */
            setupAdapter();
        }
 
    }
    
	void setupAdapter() {
		if (getActivity() == null)
			return;
		
		if (mPlaceList != null) {

			if (getListAdapter() != null) {
				// This makes sure newly added items to the data set get displayed.
				ArrayAdapter<HashMap<String, String>> listItemsAdapter = (ContactAdapter) this.getListAdapter();
				listItemsAdapter.notifyDataSetChanged();
			} else {
				// Adapter will be null only once on first execution
				// You need to make sure mPlaceList does not get nulled going forward, otw. adapter will be pointing to wrong mPlaceList.
	            ContactAdapter adapter = new ContactAdapter(mPlaceList);
	            setListAdapter(adapter);
			}

		} else {
    		// This should never happen
			setListAdapter(null);
		}
	}
    
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //Crime c = (Crime)(getListAdapter()).getItem(position);
		HashMap<String, String> c = ((ContactAdapter)getListAdapter()).getItem(position);
		Log.d(TAG, c.get(TAG_NAME) + " was clicked");
		
		String placeId = c.get(TAG_PLACE_ID);
		// Check in cache first
		// PlaceDetail placeDetail = PlaceDetailLab.get(getActivity()).getPlaceDetail(placeId);
		// Check in db first
		PlaceDetail placeDetail = mDbHelper.getPlaceDetail(placeId);

		if (placeDetail == null){
			// Network calls cannot be made on the main thread
	        new GetPlaceDetails().execute(c.get(TAG_PLACE_ID));		
		}
		else{
			// You save a network call if user clicks on a listing she visited before
            // Start PlaceDetailActivity with this Place
            Intent i = new Intent(getActivity(), PlaceDetailActivity.class);
            i.putExtra(PlaceDetailFragment.EXTRA_PLACE_ID, placeId);
            startActivityForResult(i, REQUEST_PLACE_DETAIL);
		}
		
    }
	
	/**
     * Async task class to get place details json by making HTTP call
     * */
    private class GetPlaceDetails extends AsyncTask<String, Void, String> {
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getActivity().getString(R.string.please_wait_text));
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected String doInBackground(String... arg0) {
        	// Make data call here to get the json result.
    		PlaceDetail pDetail = new PlaceDetailFetcher().apacheDownloadPlaceDetail(arg0[0], getActivity());

    		// Cache pDetail in singleton store of placeDetails
            // PlaceDetailLab.get(getActivity()).addPlaceDetail(pDetail);
            // Add pDetail to the database
            mDbHelper.insertPlace(pDetail);

            return arg0[0];
        }
 
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            // Start PlaceDetailActivity with this Place
            Intent i = new Intent(getActivity(), PlaceDetailActivity.class);
            i.putExtra(PlaceDetailFragment.EXTRA_PLACE_ID, result);
            startActivityForResult(i, REQUEST_PLACE_DETAIL);
        }
 
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE_DETAIL) {
            // Handle result
        }
    }

}
