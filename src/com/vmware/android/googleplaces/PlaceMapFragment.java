package com.vmware.android.googleplaces;

import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlaceMapFragment extends SupportMapFragment {
	private static final String TAG = "PlaceMapFragment";
    private static final String ARG_LAT = "LAT";
    private static final String ARG_LON = "LON";
    
    private GoogleMap mGoogleMap;
    private double mLat;
    private double mLon;

    public static PlaceMapFragment newInstance(double lat, double lon) {
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, lat);
        args.putDouble(ARG_LON, lon);

        PlaceMapFragment pf = new PlaceMapFragment();
        pf.setArguments(args);
        return pf;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check for a Run ID as an argument, and find the run
        Bundle args = getArguments();
        if (args != null) {
            mLat = args.getDouble(ARG_LAT, 0);
            mLon = args.getDouble(ARG_LON, 0);
        }
    }
        
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
        
        // stash a reference to the GoogleMap
        mGoogleMap = getMap();
        // show the user's location
        //mGoogleMap.setMyLocationEnabled(true);
        updateUI();
        return v;
    }
    
    private void updateUI() {
        if (mGoogleMap == null)
            return;
        
        // also create a LatLngBounds so we can zoom to fit
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        LatLng latLng = new LatLng(mLat, mLon);
        MarkerOptions startMarkerOptions = new MarkerOptions()
                    .position(latLng)
                    .title("Business");
        mGoogleMap.addMarker(startMarkerOptions);
        latLngBuilder.include(latLng);
        // make the map zoom to show the track, with some padding
        // use the size of the current display in pixels as a bounding box
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        // construct a movement instruction for the map camera
        CameraUpdate movement = CameraUpdateFactory.newLatLngBounds(latLngBuilder.build(),
                display.getWidth(), display.getHeight(), 15);
        mGoogleMap.moveCamera(movement);
    }
}
