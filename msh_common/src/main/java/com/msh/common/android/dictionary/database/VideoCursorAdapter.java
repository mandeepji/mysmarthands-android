package com.msh.common.android.dictionary.database;

import com.msh.common.android.dictionary.Constants;
import com.msh.common.android.dictionary.AppInstance;
import com.msh.common.android.dictionary.R;
import com.msh.common.android.dictionary.SingleSelectionManager;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


public class VideoCursorAdapter extends CursorAdapter implements Filterable {

	public SingleSelectionManager sm;
	// private Context mContext;
	private final LayoutInflater mInflater;
	
	private Integer NOT_FAV = 0;
	private Integer FAV = 1;

	// ------------------------CURSOR_ADAPTER_OVERRIDES-------------------------+
	public VideoCursorAdapter(SingleSelectionManager ssm, Context context,
			Cursor cursor) {

		super(context, cursor, true);
		this.sm = ssm;
		mInflater = LayoutInflater.from(context);
		// mContext = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		Holder holder = (Holder) view.getTag();
		
		String word = cursor.getString(0);
		holder.text.setText( word );
		
		if(Constants.getBoolean(Constants.CONST_KEY_USES_WORD_IMAGERY)){
			AppInstance.loadImage(holder.icn,"icn_"+word);
		}
		
		if(cursor.getColumnCount() > 1){
			holder.image.setVisibility(View.VISIBLE);
			boolean isFav = (cursor.getInt(1) != 0);
			if( isFav ){
				holder.image.setImageResource(R.drawable.fav_pressed);
				holder.image.setTag(FAV);
			}
			else{
				holder.image.setImageResource(R.drawable.fav_normal);
				holder.image.setTag(NOT_FAV);
			}
		}
		else{
			holder.image.setVisibility(View.INVISIBLE);
		}
		
		if(sm != null)
			view.setSelected( (sm.selectedRow() == cursor.getPosition()) );
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		final View view = mInflater.inflate(R.layout.video_list_item, parent,
				false);

		Holder holder = new Holder();
		
		holder.text = (TextView) view.findViewWithTag("nameTxt");
		holder.image = (ImageView) view.findViewWithTag("favBtn");
		holder.icn = (ImageView) view.findViewWithTag("icn");
		
		if(!Constants.getBoolean(Constants.CONST_KEY_USES_WORD_IMAGERY)){
			holder.icn.setVisibility(View.GONE);
		}
		
		view.setTag(holder);

		return view;
	}

	@Override
	public CharSequence convertToString(Cursor cursor) {

		return cursor.getString(0);
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {

		return MSHDatabaseAdapter.getInstance().videoNamesStartingWith(
					(String) constraint);
	}

	// ----------------------------------------------------------------------------+
	public String getTitle(View row) {

		Holder holder = (Holder) row.getTag();
		return "" + holder.text.getText();
	}

	public boolean getFavValue(View row) {

		Holder h = (Holder) row.getTag();
		return h.image.getTag() == FAV;
	}

	public boolean invertFavValue(View row) {

		MSHDatabaseAdapter dbAdapter = MSHDatabaseAdapter.getInstance();
		
		Holder holder = (Holder) row.getTag();
		boolean fav = !(this.getFavValue(row));
		//Log.d("RBI", ""+fav);
		if (fav){
			holder.image.setImageResource(R.drawable.fav_pressed);
			dbAdapter.setFavorite(holder.text.getText().toString());
		}
		else{
			holder.image.setImageResource(R.drawable.fav_normal);
			dbAdapter.removeFavorite(holder.text.getText().toString());
		}
		return fav;
	}
	
	public int getPosition(String f){
		
		Cursor mCursor = this.getCursor();
		int cPos = mCursor.getPosition();
		int ret = -1;
		for (boolean hasItem = mCursor.moveToFirst(); hasItem; hasItem = mCursor.moveToNext()) {
			if( mCursor.getString(0).equalsIgnoreCase(f) ){
				ret = mCursor.getPosition(); 
				break;
			}
		}
		mCursor.moveToPosition(cPos);
		return ret;
	}
	
	// ----------------------------------------------------------------------------+
	public void change(int pos) {

		Cursor mCursor = this.getCursor();
		int last = mCursor.getPosition();
		mCursor.moveToPosition(pos);
		mCursor.moveToPosition(last);
	}
	
	// ----------------------------------------------------------------------------+
	private class Holder{
		
		TextView text;
		ImageView image;
		ImageView icn;
	}
	
	// ----------------------------------------------------------------------------+
}
