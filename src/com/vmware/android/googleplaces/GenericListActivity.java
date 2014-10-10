package com.vmware.android.googleplaces;

import android.support.v4.app.Fragment;

public class GenericListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		//return new ContactListFragment();
		return new PlaceListFragment();
	}

}
