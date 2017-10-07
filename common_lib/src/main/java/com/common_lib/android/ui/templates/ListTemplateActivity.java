package com.common_lib.android.ui.templates;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.common_lib.R;
import com.common_lib.android.fragment.FragmentHelper;
import com.common_lib.android.fragment.nested.NestedFragment;
import com.common_lib.android.fragment.nested.NestedListFragment;


@SuppressLint("ValidFragment")
public abstract class ListTemplateActivity extends FragmentTemplateActivity implements OnItemClickListener{

	NestedListFragment frag;
	
	@Override
	protected void templateSetup(Bundle instance){
		
		frag = new NestedListFragment(this.getListResID()) {
			
			@Override
			public OnItemClickListener getOnItemClickListener() {
				
				return ListTemplateActivity.this.getOnItemClickListener();
			}
			
			@Override
			public BaseAdapter getListAdapter() {
				
				return ListTemplateActivity.this.getListAdapter();
			}
		};
		frag.setNestedFragmentDelegate(this);
		FragmentHelper.addFragment(this,frag, R.id.nestedFragement);
	}


	@Override
	public NestedFragment getFragment() {
	
		NestedFragment f = new NestedListFragment() {
			
			@Override
			public OnItemClickListener getOnItemClickListener() {
				
				return ListTemplateActivity.this.getOnItemClickListener();
			}
			
			@Override
			public BaseAdapter getListAdapter() {
				
				return ListTemplateActivity.this.getListAdapter();
			}
		};
		
		return f;
	}
	
	// ----------------------------------------------------+
	// Abstract Methods
	public abstract BaseAdapter getListAdapter();

	public OnItemClickListener getOnItemClickListener(){
		
		return this;
	}
	
	protected int getListResID() {
	
		return -1;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
		
	}

//	public abstract void configureListView();

	// ----------------------------------------------------+
}
