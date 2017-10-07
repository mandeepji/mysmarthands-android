package com.msh.common.android.dictionary.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * The root element in the search bar layout. This is a custom view just to 
 * override the handling of the back button.
 * 
 */
public class SearchLayout extends RelativeLayout {

    //private static final String TAG = "SearchLayout";

    private Activity activity;

    public SearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchLayout(Context context) {
        super(context);
    }

    public void setSearchActivity(Activity searchActivity) {
    	activity = searchActivity;
    }

    /**
     * Overrides the handling of the back key to move back to the 
     * previous sources or dismiss the search dialog, instead of 
     * dismissing the input method.
     */
    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        //Log.d(TAG, "dispatchKeyEventPreIme(" + event + ")");
        if (activity != null && 
                    event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            KeyEvent.DispatcherState state = getKeyDispatcherState();
            if (state != null) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getRepeatCount() == 0) {
                    state.startTracking(event, this);
                    return true;
                } else if (event.getAction() == KeyEvent.ACTION_UP
                        && !event.isCanceled() && state.isTracking(event)) {
                	activity.onBackPressed();
                    return true;
                }
            }
        }

        return super.dispatchKeyEventPreIme(event);
    }
}
