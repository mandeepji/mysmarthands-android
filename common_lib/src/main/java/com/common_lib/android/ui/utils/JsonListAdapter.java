package com.common_lib.android.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.common_lib.io.json.JsonArray;
import com.common_lib.io.json.JsonValue;

public class JsonListAdapter extends BaseAdapter {

	protected Context context;
	protected JsonArray list;
	
	AdapterDelegate<JsonValue> delegate = null; 
	
	public int rowViewResID = android.R.layout.simple_list_item_1;

	public JsonListAdapter(Context context, JsonArray list) {

		this.context = context;
		this.list = list;
	}
	
	public JsonListAdapter(Context context, JsonArray list, int rowViewResID) {

		this.context = context;
		this.list = list;
		this.rowViewResID = rowViewResID;
	}
	
	public JsonListAdapter(Context context, JsonArray list, int rowViewResID,
			AdapterDelegate<JsonValue> delegate) {

		this.context = context;
		this.list = list;
		this.rowViewResID = rowViewResID;
		this.delegate = delegate;
	}

	@Override
	public int getCount() {

		return list.size();
	}

	@Override
	public Object getItem(int position) {

		return list.get(position);
	}

	@Override
	public long getItemId(int position) {

		return (long) position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			
			if(delegate ==null){
				rowView = inflater.inflate(rowViewResID, null);
				Holder holder = new Holder();
				holder.titleTV = (TextView) rowView.findViewById(android.R.id.text1);
				rowView.setTag(holder);
			}
			else{
				rowView = inflater.inflate(rowViewResID, null);
				rowView.setTag( delegate.bindHolder(rowView) );
			}
		}

		if(delegate ==null){
			Holder holder = (Holder) rowView.getTag();
			holder.titleTV.setText(list.get(position).asString());
		}
		else{
			delegate.setContent(
					position,
					list.get(position),
					rowView.getTag()
				);
		}

		return rowView;
	}

	// ----------------------------------------+
	private class Holder {

		TextView titleTV;
	}
	
	// ----------------------------------------+
}
