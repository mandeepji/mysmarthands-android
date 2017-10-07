package com.msh.common.android.dictionary.view;

import com.msh.common.android.dictionary.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TitleBarView extends LinearLayout {

	public Button rightButton;
	public Button leftButton;
	public TextView titleView;

	public TitleBarView(Context context) {

		super(context);
		this.viewSetup();
	}

	public TitleBarView(Context context, AttributeSet attrs) {

		super(context, attrs);
		this.viewSetup();

	}

	private void viewSetup() {

		LayoutInflater layoutInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.title_bar_layout, this, true);

		leftButton = (Button) findViewWithTag("leftButton");
		rightButton = (Button) findViewWithTag("rightButton");
		titleView = (TextView) findViewWithTag("titleView");
	}

	// ----------------------- TITLE VIEW -----------------------------+
	public void setTitle(String title) {

		titleView.setText(title);
	}

	// ----------------------- RIGHT BUTTON -----------------------------+
	public void setRightButtonTitle(String title) {

		rightButton.setText(title);
	}

	public void setRightButtonListner(OnClickListener listner) {

		rightButton.setOnClickListener(listner);
	}

	public void setHiddenRightButton(boolean hidden) {

		int visible = (hidden) 
				? View.INVISIBLE
				: View.VISIBLE;
					
		rightButton.setVisibility(visible);
	}

	// ----------------------- LEFT BUTTON -----------------------------+
	public void setLeftButtonTitle(String title) {

		leftButton.setText(title);
	}

	public void setLeftButtonListner(OnClickListener listner) {

		leftButton.setOnClickListener(listner);
	}

	public void setHiddenLeftButton(boolean hidden) {

		int visible = (hidden) 
				? View.INVISIBLE
				: View.VISIBLE;
					
		leftButton.setVisibility(visible);
	}
}
