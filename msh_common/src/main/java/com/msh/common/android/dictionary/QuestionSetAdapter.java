package com.msh.common.android.dictionary;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class QuestionSetAdapter extends ArrayAdapter<String> {

	public QuestionSet qSet;
	//private View correctView;
	private int correct = -1;
	private int answered = -1;
	Handler handler;

	public QuestionSetAdapter(Context context, QuestionSet qSet) {

		super(context, R.layout.result_list_item_small, qSet.getSet());
		this.qSet = qSet;
		this.correct = qSet.getAnswerIndex();
	}
	
	public QuestionSetAdapter(Context context, QuestionSet qSet, int answered) {

		super(context, R.layout.result_list_item_small, qSet.getSet());
		this.qSet = qSet;
		this.correct = qSet.getAnswerIndex();
		this.answered = answered;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = ((Activity) this.getContext())
					.getLayoutInflater();
			rowView = inflater.inflate(R.layout.result_list_item_small, null);
			Holder holder = new Holder();
			holder.text = (TextView) rowView.findViewWithTag("answer");
			holder.image = (ImageView) rowView.findViewWithTag("correct");
			
			holder.icn = (ImageView) rowView.findViewWithTag("icn");
			
			if(!Constants.getBoolean(Constants.CONST_KEY_USES_WORD_IMAGERY)){
				holder.icn.setVisibility(View.GONE);
			}
			
			rowView.setTag(holder);
			
			//if (position == qSet.getAnswerIndex()){
				//correctView = rowView;
				//Log.d("RBI", ""+position);
			//}
		}

		Holder holder = (Holder) rowView.getTag();
		
		String word = qSet.wordAtIndex(position);
		
		holder.text.setText(word);
		
		if(Constants.getBoolean(Constants.CONST_KEY_USES_WORD_IMAGERY)){
			AppInstance.loadImage(holder.icn,"icn_"+word);
		}
		
		//holder.image.setVisibility(View.INVISIBLE);
		
		if(answered<0)
			holder.image.setVisibility(View.INVISIBLE);
		else{
			holder.image.setVisibility(View.VISIBLE);
			if(correct == position)
				holder.image.setImageResource(R.drawable.correct);
			else if(answered == position)
				holder.image.setImageResource(R.drawable.incorrect);
			else
				holder.image.setVisibility(View.INVISIBLE);
		}

		return rowView;
	}
	
	/*
	public void animateAnsweredEvent(View answeredView,int pos) {

		
		Holder h = (Holder) correctView.getTag();
		h.image.setVisibility(View.VISIBLE);

		if (correctView != answeredView) {
			h = (Holder) answeredView.getTag();
			h.image.setSelected(true);
			h.image.setVisibility(View.VISIBLE);
		}
		
		
		this.answered = pos;
		
		try {
			Thread n = new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(ANIMATION_DURATION);
					} catch (Exception e) {}
				}
			});
			n.start();
		} catch (Exception e) {}
	}
	*/
	
	private class Holder {

		TextView text;
		ImageView image;
		ImageView icn;
	}


}
