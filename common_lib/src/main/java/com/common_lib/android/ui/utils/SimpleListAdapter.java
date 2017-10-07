package com.common_lib.android.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;


public class SimpleListAdapter<E> extends BaseAdapter{

	protected List<E> objects;
	protected AdapterDelegate<E> delegate;
	protected Context context;
	protected int rowResID;
	
	public SimpleListAdapter(Context context,int rowViewResID) {

		this.context = context;
		this.rowResID = rowViewResID;
	}
	
	public SimpleListAdapter(Context context, List<E> objects, int rowViewResID,AdapterDelegate<E> delegate) {

		this.context = context;
		this.objects = objects;
		this.rowResID = rowViewResID;
		this.delegate = delegate;
	}
	
	//------------------------------------------------------+
	// Base adapter
	@Override
	public int getCount() {
		
		return objects.size();
	}

	@Override
	public Object getItem(int position) {
		
		return objects.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View row, ViewGroup parent) {
		
		if(row ==null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(rowResID, null);
			row.setTag( delegate.bindHolder(row) );
		}
		delegate.setContent(
				position,
				objects.get(position),
				row.getTag()
			);
		
		return row;
	}

	//------------------------------------------------------+
	
}
