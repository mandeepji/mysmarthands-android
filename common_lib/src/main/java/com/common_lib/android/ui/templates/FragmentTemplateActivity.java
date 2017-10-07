package com.common_lib.android.ui.templates;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.common_lib.R;
import com.common_lib.android.fragment.FragmentHelper;
import com.common_lib.android.fragment.nested.NestedFragment;
import com.common_lib.android.fragment.nested.NestedFragmentDelegate;

import java.util.ArrayList;
import java.util.List;

public abstract class FragmentTemplateActivity extends FragmentActivity
		implements NestedFragmentDelegate {

	// Nester
	private List<NestedFragment> nestedFrags;
	private int nestedFragReadyCount = 0;
	
	@Override
	protected void onCreate(Bundle instance) {
	
		super.onCreate(instance);
		
		this.setContentView();
		this.templateSetup(instance);
	}
	
	protected void templateSetup(Bundle instance){
		
		NestedFragment frag = this.getFragment();
		this.addFragment(frag, R.id.nestedFragement);
	}
	
	protected void addFragment(NestedFragment frag,int frameResID){
		
		frag.setNestedFragmentDelegate(this);
		FragmentHelper.addFragment(this,frag,frameResID);
	}
	
	public int getDefaultLayoutResID() {

		return R.layout.ui_templates_layout;
	}

	//---------------------------------------------------------------+
	// abstract for implementers
	public void setContentView(){
		
		setContentView(this.getDefaultLayoutResID());
		
	}

	//---------------------------------------------------------------+
	// abstract for further abstraction
	public void onCreateTemplate(){}
	
	public abstract NestedFragment getFragment();
	
	//---------------------------------------------------------------+
	// Nester
	@Override
	public synchronized void nestedFragmentAdded(NestedFragment frag) {

		++nestedFragReadyCount;
		if (nestedFrags == null) {
			nestedFrags = new ArrayList<NestedFragment>();
		}
		nestedFrags.add(frag);
		// Log.d("RBI",frag.toString());
		// Log.d("RBI",nestedFragCount+" add");
	}

	@Override
	public synchronized void nestedFragmentReady(NestedFragment frag, View view) {

		--nestedFragReadyCount;
		// Log.d("RBI",frag.toString());
		// Log.d("RBI",nestedFragCount+" sub");
		this.nestedFragmentCreated(frag, view);

		if (nestedFragReadyCount <= 0) {
			// Log.d("RBI","allFragsCreated");
			this.allNestedFragmentsCreated();
		}
	}

	// callbacks for implementing classes
	public void nestedFragmentCreated(NestedFragment frag, View view) {

	}

	public void allNestedFragmentsCreated() {}
	
	//---------------------------------------------------------------+
}
