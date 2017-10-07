package com.common_lib.android.fragment.nested;

import android.support.v4.app.Fragment;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public  abstract class NestedFragment extends Fragment
	implements NestedFragmentDelegate{
	
	// Nested
	protected NestedFragmentDelegate nestedFragmentdelegate;
	
	// Nester
	private List<NestedFragment> nestedFrags;
	private int nestedFragReadyCount = 0;
	public boolean pauseResumeOnHiddenChanged = false;
	
	//-------------------------------------+
	// Nested
	public void setNestedFragmentDelegate(
			NestedFragmentDelegate nestedFragmentdelegate) {
	
		nestedFragmentdelegate.nestedFragmentAdded(this);
		this.nestedFragmentdelegate = nestedFragmentdelegate;
	}
	
	//-------------------------------------+
	// Nester
	@Override
	public synchronized void nestedFragmentAdded(NestedFragment frag) {
		
		++nestedFragReadyCount;
		if(nestedFrags == null){
			nestedFrags = new ArrayList<NestedFragment>();
		}
		nestedFrags.add(frag);
//		Log.d("RBI",frag.toString());
//		Log.d("RBI",nestedFragCount+" add");
	}

	@Override
	public synchronized void nestedFragmentReady(NestedFragment frag, View view) {
		
		--nestedFragReadyCount;
//		Log.d("RBI",frag.toString());
//		Log.d("RBI",nestedFragCount+" sub");
		this.nestedFragmentCreated(frag,view);
		
		if(nestedFragReadyCount <=0){
//			Log.d("RBI","allFragsCreated");
			this.allNestedFragmentsCreated();
		}
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
	
		if(pauseResumeOnHiddenChanged){
			if(hidden){
				this.onPause();
			}
			else{
				this.onResume();
			}
		}
		
		if(nestedFrags !=null){
			for (NestedFragment nf : nestedFrags) {
				nf.onHiddenChanged(hidden);
			}
		}
		super.onHiddenChanged(hidden);
	}

	// callbacks for implementing classes
	public void nestedFragmentCreated(NestedFragment frag, View view){
		
	}
	
	public void allNestedFragmentsCreated(){
		
	}
	
	//-------------------------------------+
}
