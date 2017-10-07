package com.common_lib.android.maps;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;

public class MapCameraBounds implements OnCameraChangeListener{

	GoogleMap map;
	LatLngBounds bounds;
	float maxZoom;
	float minZoom;
	CameraPosition oldPosition;
	
	public MapCameraBounds(GoogleMap map,LatLngBounds bounds,float minZoom,float maxZoom){
		
		this.bounds = bounds;
		this.map = map;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		oldPosition = map.getCameraPosition();
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		
//		Log.d("RBI",position.toString());
		if(bounds.contains(position.target) && 
				position.zoom <= maxZoom &&
				position.zoom >= minZoom){
			oldPosition = position;
		}
		else{
			map.animateCamera(CameraUpdateFactory.newCameraPosition(oldPosition));
		}
	}
	
}
