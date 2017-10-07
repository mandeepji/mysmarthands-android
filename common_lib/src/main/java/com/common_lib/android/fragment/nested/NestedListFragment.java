package com.common_lib.android.fragment.nested;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.common_lib.R;

public abstract class NestedListFragment extends NestedFragment {

	ListView listView;
	TextView noItemsTextView;
	BaseAdapter listAdapter;
	int overrideListRes = -1;

	
	public NestedListFragment(){}
	
	public NestedListFragment(int listResID){
		
		overrideListRes = listResID;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View ret = inflater.inflate(getContentView(), container, false);

		if (listView == null) {
			listView = (ListView) ret.findViewById(android.R.id.list);
			noItemsTextView = (TextView) ret.findViewById(android.R.id.empty);
			tableSetup();
		}

		if (nestedFragmentdelegate != null) {
			nestedFragmentdelegate.nestedFragmentReady(this, ret);
		}

		return ret;
	}

	protected void tableSetup() {

		if (listAdapter == null) {
			listAdapter = this.getListAdapter();
			listView.setAdapter(listAdapter);
			listView.setOnItemClickListener(this.getOnItemClickListener());
			listView.setOnItemLongClickListener(this.getOnLongItemClickListener());
		} else {
			listAdapter.notifyDataSetInvalidated();
		}
	}

	//----------------------------------------------------+
	public ListView getListView(){
		
		return listView;
	}
	
	//----------------------------------------------------+
	// Abstract Methods and overrides
	public abstract BaseAdapter getListAdapter();
	
	public abstract OnItemClickListener getOnItemClickListener();
	
	public OnItemLongClickListener getOnLongItemClickListener() {
	
		return null;
	}
	
	public int getContentView(){
		
		return (overrideListRes != -1) 
				? overrideListRes
				: R.layout.list_menu_fragment;
	}
	
//	public abstract void configureListView();
	
	//----------------------------------------------------+
	
	
	
}
