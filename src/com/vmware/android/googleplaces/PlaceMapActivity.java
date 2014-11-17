package com.vmware.android.googleplaces;

import android.support.v4.app.Fragment;

public class PlaceMapActivity extends SingleFragmentActivity {

    /** A key for passing a run ID as a long */
    public static final String EXTRA_LAT = "com.vmware.android.googleplaces.lat";
    public static final String EXTRA_LON = "com.vmware.android.googleplaces.lon";
    
    @Override
    protected Fragment createFragment() {
        double lat = getIntent().getDoubleExtra(EXTRA_LAT, 0);
        double lon = getIntent().getDoubleExtra(EXTRA_LON, 0);
        
        if (true) {
            return PlaceMapFragment.newInstance(lat, lon);
        } else {
            return new PlaceMapFragment();
        }
    }

}
