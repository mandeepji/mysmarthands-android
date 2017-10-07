package com.common_lib.android.fragment.nested;

import android.view.View;

public interface NestedFragmentDelegate{
	
	public void nestedFragmentAdded(NestedFragment frag);
	public void nestedFragmentReady(NestedFragment frag, View view);
}

