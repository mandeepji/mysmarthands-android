package com.common_lib.android.fragment.nested;

import android.support.v4.app.DialogFragment;

public class NestedDialogFragment extends DialogFragment{

	protected NestedFragmentDelegate nestedFragmentdelegate;
	
	//-------------------------------------+
	public void setNestedFragmentDelegate(
			NestedFragmentDelegate nestedFragmentdelegate) {
	
		this.nestedFragmentdelegate = nestedFragmentdelegate;
	}
	
	//-------------------------------------+
	
}
