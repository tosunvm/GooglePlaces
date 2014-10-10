package com.vmware.android.googleplaces;

import android.support.v4.app.Fragment;

public class PlaceDetailActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new PlaceDetailFragment();
		//String placeId = (String)getIntent().getSerializableExtra(PlaceDetailFragment.EXTRA_PLACE_ID);
		//return PlaceDetailFragment.newInstance(placeId);

	}

}
