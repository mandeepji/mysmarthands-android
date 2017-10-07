package com.msh.common.android.dictionary.view;

import com.msh.common.android.dictionary.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;


public class ToolBarView extends LinearLayout implements OnClickListener{
	
	public Button alphaBtn;
	public Button catBtn;
	public Button favBtn;
	public Button rightBtn;
	
	int selectedIndex = -1;
	
	private boolean momentary = false;
	
	public SegementListner segListner = null;

	public ToolBarView(Context context) {

		super(context);
		this.viewSetup();
	}

	public ToolBarView(Context context, AttributeSet attrs) {

		super(context, attrs);
		this.viewSetup();
	}

	private void viewSetup() {

		LayoutInflater layoutInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.tool_bar_layout,this,true);

		alphaBtn = (Button) findViewWithTag("alphaButton");
		catBtn = (Button) findViewWithTag("categoryButton");
		favBtn = (Button) findViewWithTag("favoriteButton");
		rightBtn = (Button) findViewWithTag("rightButton");
		
		alphaBtn.setText("A-Z");
		catBtn.setText("Categories");
		favBtn.setText("Favorites");
		rightBtn.setText("Search");
		
		alphaBtn.setOnClickListener(this);
		catBtn.setOnClickListener(this);
		favBtn.setOnClickListener(this);
		rightBtn.setOnClickListener(this);
	}

	public void onClick(View v) {
		
		if(v == rightBtn){
			if(segListner != null)
				this.segListner.searchBtnPressed((Button)v);
			return;
		}
		
		int pressed = getButtonIndex( (Button)v );
		
		if(segListner != null)
			segListner.segmentPressed(pressed);
		
		int result;
		if(pressed == selectedIndex){
			if(momentary)
				return;
			else
				result = -1;
		}
			
		else
			result = pressed;
		
		this.setSelected(result);
		if(segListner != null)
			this.segListner.indexChanged(result);
	}
	
	private int getButtonIndex(Button b){
		
		if(b == alphaBtn) return 0;
		if(b == catBtn) return 1;
		else return 2;
		
	}
	
	private Button getSegment(int index){
		
		switch (index) {
		case 0: return alphaBtn;
		case 1: return catBtn;
		default: return favBtn;
		}
	}
	
	public void setSelected(int index){	
		
		if(index < 0){
			if(momentary)
				return;
			else if(selectedIndex >= 0){
				this.getSegment(index).setSelected(false);
				selectedIndex = -1;
			}
		}
		else{
			if(selectedIndex != index){
				this.getSegment(selectedIndex).setSelected(false);
				this.getSegment(index).setSelected(true);
				selectedIndex = index;
			}
		}
	}
	
	public void setMomentary(boolean b){
		
		if( b == true && selectedIndex < 0 )
			this.setSelected(0);
		
		momentary = b;
	}
	
	public boolean getMomentary(){
		
		return momentary;
	}
	
	public interface SegementListner{
		
		public void indexChanged(int index);
		public void segmentPressed(int index);
		public void searchBtnPressed(Button b);
	}
}
