package com.common_lib.android.maps.location;

import android.location.Location;

import com.common_lib.android.maps.GoogleMapsHelper;
import com.common_lib.android.maps.location.LocationHelper.LocationHelperLifeCycle;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;


public class LocationTracker implements LocationListener,LocationHelperLifeCycle{

	public final LocationHelper locationHelper;
	public final String id;
	boolean isTracking;
	private List<Location> track;
	private List<LatLng> mapPath;
	
	GoogleMap map;
	Polyline visualPath;
	
	
	public LocationTracker(LocationHelper helper, String id, GoogleMap map) {
		
		this.locationHelper = helper;
		this.locationHelper.addListener(this);
		
		this.id = id;
		this.map = map;
		track = new ArrayList<Location>();
		isTracking = false;
		
		if(this.map != null){
			mapPath = new ArrayList<LatLng>();
			visualPath = GoogleMapsHelper.addPolyline(map, mapPath);
		}
	}
	
	public void startTracking(){
		
		isTracking = true;
	}
	
	public void pauseTracking(){
		
		isTracking = false;
	}
	
	public List<Location> stopTracking(){
		
		isTracking = false;
		locationHelper.removeListener(this);
		visualPath.remove();
		return track;
	}

	public void setPolylineVisible(boolean visible){
		
		visualPath.setVisible(visible);
	}
	
	// -------------------------------------------------------------------+
	// LocationListner Delegate
	@Override
	public void onLocationChanged(Location location){
		
		if(isTracking){
			track.add(location);
			
			if(map != null){
				mapPath.add( LocationHelper.locationToLatLng(location) );
				visualPath.setPoints(mapPath);
			}
		}
	}

	// -------------------------------------------------------------------+
	// LocationHelperLifeCycle Delegate
	@Override
	public void onStart() {
		
		locationHelper.onStart();
	}

	@Override
	public void onStop() {
		
		locationHelper.onStop();
	}

	@Override
	public void onResume() {
		
		locationHelper.onResume();
	}
	
	@Override
	public void onPause(boolean startRequestsOnResume) {
		
		locationHelper.onPause(startRequestsOnResume);
	}
	
	// -------------------------------------------------------------------+
	
}
