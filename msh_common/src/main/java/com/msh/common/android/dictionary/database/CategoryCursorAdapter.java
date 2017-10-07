package com.msh.common.android.dictionary.database;

import com.msh.common.android.dictionary.R;
import com.msh.common.android.dictionary.SingleSelectionManager;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CategoryCursorAdapter extends CursorAdapter {

	private SingleSelectionManager sm = null;
	private final LayoutInflater mInflater;

	public CategoryCursorAdapter(SingleSelectionManager sm, Context context,
			Cursor cursor) {

		super(context, cursor, true);
		this.sm = sm;
		mInflater = LayoutInflater.from(context);
		// mContext = context;
	}

	public CategoryCursorAdapter(Context context,Cursor cursor) {

		super(context, cursor, true);
		mInflater = LayoutInflater.from(context);
		// mContext = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		Holder holder = (Holder) view.getTag();
		holder.catText.setText(cursor.getString(0));
		holder.countText.setText(cursor.getInt(1) + " Words");
		
		if(sm != null)
			view.setSelected( (sm.selectedRow() == cursor.getPosition()) );
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		final View view = mInflater.inflate(R.layout.category_list_item,
				parent, false);

		Holder holder = new Holder();
		holder.catText = (TextView) view.findViewWithTag("catTxt");
		holder.countText = (TextView) view.findViewWithTag("countTxt");
		view.setTag(holder);
		
		return view;
	}
	
	public String getName(View row){
		
		Holder holder = (Holder) row.getTag();
		return holder.catText.getText().toString();
	}

	public int getCountForIndex(int index) {

		Cursor c = this.getCursor();
		int prev = c.getPosition();
		c.moveToPosition(index);

		int ret = c.getInt(1);
		c.moveToPosition(prev);
		return ret;
	}

	public String getNameForIndex(int index) {

		Cursor c = this.getCursor();
		int prev = c.getPosition();
		c.moveToPosition(index);

		String ret = c.getString(0);
		c.moveToPosition(prev);
		return ret;
	}
	
	private class Holder{
		
		TextView catText;
		TextView countText;
	}
}
