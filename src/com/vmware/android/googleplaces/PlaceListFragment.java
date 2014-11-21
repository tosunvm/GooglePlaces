package com.vmware.android.googleplaces;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.vmware.android.googleplaces.R;

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
 
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> contactList;
    
    private Button mCreateCrimeButton;
	private TextView mEmptyListTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.places_title);

		// Looks like retaining the fragment makes sure contactList member variable is preserved
		// If you don't retain even after page rotation place list is reloaded from the server hinting if statement below is entered
		// Retaining and then checking contactList existence prevents reload from server on screen orientation changes and when user exits the app
		//   via the home button and then comes back
		// If user exits the app via back button and enters the app again server is hit again though.
		// Also when you go to place details and come back via the back button place list is NOT reloaded from the server
		//   this was true even w/o the retain and if check below
		// If you go to place details and come back through the ancestral navigation on the menu bar server is hit again. I don't have a way to prevent this.
		//   actually I do through android:launchMode="singleTask" in the manifest file
		setRetainInstance(true);
		if (contactList == null){
			contactList = new ArrayList<HashMap<String, String>>();
			// Calling async task to get json
	        new GetContacts().execute();		
		}
	}

	//@TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState) {
        
		View v = inflater.inflate(R.layout.fragment_generic_list, parent, false);

		mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text_view);
        mEmptyListTextView.setText("There are no crimes, please add one.");
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
    private class GetContacts extends AsyncTask<Void, Void, Void> {
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            //contactList = new ContactsFetcher().javaDownloadContactItems(url);
            contactList = new PlaceFetcher().apacheDownloadPlaceItems(url);
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            //ContactAdapter adapter = new ContactAdapter(contactList);
            //setListAdapter(adapter);
            setupAdapter();
        }
 
    }
    
	void setupAdapter() {
		if (getActivity() == null)
			return;
		
		if (contactList != null) {

			if (getListAdapter() == null) {
	            ContactAdapter adapter = new ContactAdapter(contactList);
	            setListAdapter(adapter);
			} else {
				// This makes sure newly added items to the data set get displayed.
				ArrayAdapter<HashMap<String, String>> listItemsDapter = (ContactAdapter) this.getListAdapter();
				listItemsDapter.notifyDataSetChanged();
			}

		} else {
			setListAdapter(null);
		}
	}
    
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //Crime c = (Crime)(getListAdapter()).getItem(position);
		HashMap<String, String> c = ((ContactAdapter)getListAdapter()).getItem(position);
		Log.d(TAG, c.get(TAG_NAME) + " was clicked");
		
		String placeId = c.get(TAG_PLACE_ID);
		PlaceDetail placeDetail = PlaceDetailLab.get(getActivity()).getPlaceDetail(placeId);
		
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
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected String doInBackground(String... arg0) {
        	// Make data call here to get the json result.
    		PlaceDetail pDetail = new PlaceDetailFetcher().apacheDownloadPlaceDetail(arg0[0]);

    		// TODO
    		// Add pDetail to singleton store of placeDetails
            PlaceDetailLab.get(getActivity()).addPlaceDetail(pDetail);

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
