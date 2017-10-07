package com.msh.common.android.dictionary;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class QuizSetAdapter extends ArrayAdapter<QuestionSet> {

	QuizSet qSet;
	
	public QuizSetAdapter(Context context,QuizSet qSet) {

		super(context, R.layout.result_list_item,qSet.qSet);
		
		this.qSet = qSet;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = ( (Activity) this.getContext() ).getLayoutInflater();
			rowView = inflater.inflate(R.layout.result_list_item, null);
			Holder holder = new Holder();
			holder.text = (TextView) rowView.findViewWithTag("answer");
			holder.image = (ImageView) rowView.findViewWithTag("correct");
			
			holder.icn = (ImageView) rowView.findViewWithTag("icn");
			
			if(!Constants.getBoolean(Constants.CONST_KEY_USES_WORD_IMAGERY)){
				holder.icn.setVisibility(View.GONE);
			}
			
			rowView.setTag(holder);
		}
		
		Holder holder = (Holder) rowView.getTag();
		QuestionSet s = qSet.qSet.get( position );
		
		String word = s.getAnswer();
		
		holder.text.setText( word );
		
		if(Constants.getBoolean(Constants.CONST_KEY_USES_WORD_IMAGERY)){
			AppInstance.loadImage(holder.icn,"icn_"+word);
		}
		
		if(!s.answeredCorrect())
			holder.image.setImageResource(R.drawable.incorrect);
		else
			holder.image.setImageResource(R.drawable.correct);

		return rowView;
	}
	
	public String name(View row){
		
		Holder h = (Holder) row.getTag();
		return (String) h.text.getText();
	}
	
	private class Holder{
		
		TextView text;
		ImageView image;
		ImageView icn;
	}
	
	
}
