package com.common_lib.android.ui.customfonts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.common_lib.R;

// put fonts in the assests folder ya dingus!!

public class CustomFontTextView extends TextView{

    public CustomFontTextView(Context context) {
        
    	super(context);
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        
    	super(context, attrs);
        setCustomFont(context, attrs);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        
    	super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        
    	TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
        String customFont = a.getString(R.styleable.CustomFontTextView_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        
    	
    	Typeface tf = null;
        tf = Typefaces.get(ctx, asset);
        
        try {
        	setTypeface(tf);
		} catch (Exception e) {
			Log.d("RBI",e.getLocalizedMessage());
			return false;
		}
          
        
        return true;
    }
	
}
