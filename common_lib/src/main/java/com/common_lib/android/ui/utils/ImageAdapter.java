package com.common_lib.android.ui.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.common_lib.R;

import java.util.ArrayList;
import java.util.List;


public class ImageAdapter extends BaseAdapter {
	
	public static final String IMAGE_VIEW_ID 	= "grid_item_image";
	public static final String TEXT_VIEW_ID 	= "grid_item_text";
	
	private Context context;
	private final List<Integer> imageIDs;

	private int cellResID = R.layout.grid_image_cell;
	List<String> titles = null;
	
	public ImageAdapter(Context context,int[] imageIDs) {
		
		this.context = context;
		this.imageIDs = new ArrayList<Integer>(imageIDs.length);
		for (int i : imageIDs) {
			this.imageIDs.add(i);
		}
	}
	
	public ImageAdapter(Context context,List<Integer> imageIDs) {
	
		this.context = context;
		this.imageIDs = imageIDs;
	}

	public void setCellResource(int restID){
		
		cellResID = restID;
	}
	
	public void setImageTitles(List<String> titles){
		
		this.titles = titles;
	}
	
	public void setImageTitles(String[] titles){
		
		this.titles = new ArrayList<String>(titles.length);
		for (String title : titles) {
			this.titles.add(title);
		}
	}
	
	//---------------------------------------------------------+
	// Base Adapter Methods
	@Override
	public int getCount() {
		
		return (titles !=null)
				? titles.size()
				: imageIDs.size();
	}

	@Override
	public Object getItem(int position) {
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}
	
	public View getView(int position, View gridCellView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (gridCellView == null) {
			gridCellView = inflater.inflate(cellResID, null);
			
			Holder h = new Holder();
			// set image based on selected text
			h.button = (ImageView) gridCellView
					.findViewById(R.id.grid_item_image);
			
			try{
				h.textView = (TextView) gridCellView
						.findViewById(R.id.grid_item_text);
			}
			catch(Exception e){}
			
			gridCellView.setTag(h);
		}

		int imgIndex = (imageIDs.size() == 1)
				? 0
				: position;
		Holder h = (Holder) gridCellView.getTag();
		h.button.setImageResource(imageIDs.get(imgIndex));
		if(h.textView !=null && titles !=null){
			h.textView.setText(titles.get(position));
		}
		
		return gridCellView;
	}
	
	//---------------------------------------------------------+
	private class Holder{
		
		ImageView button;
		TextView textView;
	}
	
	//---------------------------------------------------------+

}
