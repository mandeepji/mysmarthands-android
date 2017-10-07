package com.common_lib.android.fragment.nested;

import android.annotation.TargetApi;
import android.os.Build;
import android.preference.PreferenceFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class NestedPreferenceFragment extends PreferenceFragment{

protected NestedFragmentDelegate nestedFragmentdelegate;
	
	//-------------------------------------+
	public void setNestedFragmentDelegate(
			NestedFragmentDelegate nestedFragmentdelegate) {
	
		this.nestedFragmentdelegate = nestedFragmentdelegate;
	}
	
	//-------------------------------------+
}
