package com.common_lib.android.maps;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link LocationSource} which reports a new location whenever a user long presses the map at
 * the point at which a user long pressed the map.
 */
public class LongPressLocationSource implements LocationSource, OnMapLongClickListener {
    
	public static final String PROVIDER_NAME = "LongPressLocationProvider";
	
	private OnLocationChangedListener mListener;
	
	private List<OnLocationChangedListener> otherListeners = 
			new ArrayList<LocationSource.OnLocationChangedListener>(3);
	
    /**
     * Flag to keep track of the activity's lifecycle. This is not strictly necessary in this
     * case because onMapLongPress events don't occur while the activity containing the map is
     * paused but is included to demonstrate best practices (e.g., if a background service were
     * to be used).
     */
    private boolean mPaused;
    public float accuracy = 10;

    @Override
    public void activate(OnLocationChangedListener listener) {
        
    	mListener = listener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onMapLongClick(LatLng point) {
        
    	if (mListener != null && !mPaused) {
            Location location = new Location("LongPressLocationProvider");
            location.setLatitude(point.latitude);
            location.setLongitude(point.longitude);
            location.setAccuracy(accuracy);
            mListener.onLocationChanged(location);
            for (OnLocationChangedListener l : otherListeners) {
				l.onLocationChanged(location);
			}
        }
    }

    public void onPause() {
        mPaused = true;
    }

    public void onResume() {
        mPaused = false;
    }

    //-----------------------------------------------+
    public void addListener(OnLocationChangedListener l){
    	
    	otherListeners.add(l);
    }
    
    public void removeListener(OnLocationChangedListener l){
    	
    	otherListeners.remove(l);
    }
    
    //-----------------------------------------------+
}