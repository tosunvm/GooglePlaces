package com.vmware.android.googleplaces.test;

import java.util.HashMap;

import com.vmware.android.googleplaces.GenericListActivity;
import com.vmware.android.googleplaces.PlaceListFragment;
import com.vmware.android.googleplaces.R;

import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class GenericListActivityTest extends
		ActivityInstrumentationTestCase2<GenericListActivity> {

	private static final String TAG = "GenericListActivityTest";
	
	private static final int TIMEOUT_IN_MS = 5000;
    private static final String TEST_QUERY = "plumbers";
    private static final String TEST_LOCATION = "355 Main Street, Cambridge MA";
    private static final String TEST_BUSINESS_NAME = "Plumbing Prime";
    
	private GenericListActivity mGenericListActivity;
	private PlaceListFragment mPlaceListFragment;
	
    public GenericListActivityTest() {
        super(GenericListActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mGenericListActivity = (GenericListActivity) getActivity();
        mPlaceListFragment = (PlaceListFragment) mGenericListActivity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
    }
    
    public void testPreConditions() {
        assertNotNull(mGenericListActivity);
        assertNotNull(mPlaceListFragment);
    }
    
    @MediumTest
    public void testSearchButton_LabelText() {

        final Button placeQueryButton = (Button) mPlaceListFragment.getView()
                .findViewById(R.id.place_query_button);
        final String expectedButtonText = mGenericListActivity.getString(R.string.place_query_button);
        assertEquals("Unexpected button label text", expectedButtonText, placeQueryButton.getText());
    }

    @LargeTest
    public void testExecuteSearch() {

        final Button placeQueryButton = (Button) mPlaceListFragment.getView()
                .findViewById(R.id.place_query_button);
        final EditText queryEditText = (EditText) mPlaceListFragment.getView()
                .findViewById(R.id.place_query_edit_text);
        final EditText queryLocationEditText = (EditText) mPlaceListFragment.getView()
                .findViewById(R.id.place_location_edit_text);
        final ListView resultListView = (ListView) mPlaceListFragment.getView().findViewById(android.R.id.list);

        //1/3 Request focus on the queryEditText field. This must be done on the UiThread
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            	queryEditText.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Send the text message
        getInstrumentation().sendStringSync(TEST_QUERY);
        getInstrumentation().waitForIdleSync();

        //2/3 Request focus on the queryLocationEditText field. This must be done on the UiThread
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            	queryLocationEditText.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Send the text message
        getInstrumentation().sendStringSync(TEST_LOCATION);
        getInstrumentation().waitForIdleSync();
        
        //3/3 Click on the placeQueryButton to execute a search
        TouchUtils.clickView(this, placeQueryButton);
        
        // I feel we need to wait for search results but this works 
        HashMap<String, String> c = (HashMap<String, String>) (mPlaceListFragment.getListAdapter()).getItem(1);
        Log.d(TAG, c.get("name") + " is at position 1");
        assertEquals("Business name at position 1 does not match.", TEST_BUSINESS_NAME, c.get("name"));
    }

}
