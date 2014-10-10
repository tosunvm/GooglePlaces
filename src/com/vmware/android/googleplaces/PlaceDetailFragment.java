package com.vmware.android.googleplaces;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.vmware.android.googleplaces.R;

public class PlaceDetailFragment extends Fragment {
	private static final String TAG = "PlaceDetailFragment";
	public static final String EXTRA_PLACE_ID = "com.vmware.android.googleplaces.place_id";
	private PlaceDetail mPlaceDetail;
	private TextView mNameTextView;
	private TextView mAddressTextView;
	private Button mMapButton;
	private Button mWebSiteButton;
    
    public static PlaceDetailFragment newInstance(String placeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_PLACE_ID, placeId);

        PlaceDetailFragment fragment = new PlaceDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		String placeId = (String)getActivity().getIntent().getSerializableExtra(EXTRA_PLACE_ID);
		//String placeId = (String)getArguments().getSerializable(EXTRA_PLACE_ID);
		Log.i(TAG, "place detail fragment loaded with place id" + placeId);
		mPlaceDetail = PlaceDetailLab.get(getActivity()).getPlaceDetail(placeId);
		setHasOptionsMenu(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	if (NavUtils.getParentActivityName(getActivity()) != null) {
	                NavUtils.navigateUpFromSameTask(getActivity());
	            }

	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
    public void onPause() {
        super.onPause();
        //PlaceDetailLab.get(getActivity()).saveCrimes();
    }

	public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, null);
    }

	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_place_detail, parent, false);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		
		mNameTextView = (TextView) v.findViewById(R.id.place_detail_nameTextView);
		mNameTextView.setText(mPlaceDetail.getPlaceName());
		mAddressTextView = (TextView) v.findViewById(R.id.place_detail_formattedAddress_TextView);
		mAddressTextView.setText(mPlaceDetail.getFormattedAddress());

		mMapButton = (Button)v.findViewById(R.id.place_detail_map);
		mMapButton.setText("Map");
		mWebSiteButton = (Button)v.findViewById(R.id.place_detail_webSite);		
		mWebSiteButton.setText("WebSite");

/*
		mDateButton = (Button)v.findViewById(R.id.crime_date);		
		updateDate();
		mDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                // DatePickerFragment dialog = new DatePickerFragment();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(PlaceDetailFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

		mTimeButton = (Button)v.findViewById(R.id.crime_time);		
		updateTime();
		mTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                // DatePickerFragment dialog = new DatePickerFragment();
                TimePickerFragment dialog = TimePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(PlaceDetailFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

	    mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
	    mSolvedCheckBox.setChecked(mCrime.isSolved());
	    mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	        	// Set the crime's solved property
	            mCrime.setSolved(isChecked);
	        }
	    });
*/
		return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
	}

}
