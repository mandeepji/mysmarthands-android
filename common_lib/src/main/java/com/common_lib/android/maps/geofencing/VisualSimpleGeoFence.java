package com.common_lib.android.maps.geofencing;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class VisualSimpleGeoFence extends SimpleGeofence {

	public Marker marker;
	public Circle circle;

	public VisualSimpleGeoFence(GoogleMap map, 
			String geofenceId, String name, String desc,
			LatLng latLng, float radius, 
			long expiration, int transition) {

		super(geofenceId, latLng, radius, expiration, transition);

		// <Marker Stop
		marker = map.addMarker(new MarkerOptions()
				.position(latLng)
				.title(name)
				.snippet(desc)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

		// Circle for the stop
		circle = map.addCircle(new CircleOptions()
				.center(latLng)
				.radius(radius)
				.fillColor(0x40ff0000)
				.strokeColor(Color.TRANSPARENT)
				.strokeWidth(2));
	}
	
	public VisualSimpleGeoFence(GoogleMap map, 
			String geofenceId,int iconResID,
			String name, String desc,
			LatLng latLng, float radius, 
			long expiration, int transition) {

		super(geofenceId, latLng, radius, expiration, transition);

		// <Marker Stop
		marker = map.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromResource(iconResID))
				.position(latLng)
				.title(name)
				.snippet(desc));

		// Circle for the stop
		circle = map.addCircle(new CircleOptions()
				.center(latLng)
				.radius(radius)
				.fillColor(0x40ff0000)
				.strokeColor(Color.TRANSPARENT)
				.strokeWidth(2));
	}
	
	public VisualSimpleGeoFence(GoogleMap map, 
			String geofenceId,Bitmap m,
			String name, String desc,
			LatLng latLng, float radius, 
			long expiration, int transition) {

		super(geofenceId, latLng, radius, expiration, transition);

		// <Marker Stop
		marker = map.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromBitmap(m))
				.position(latLng)
				.title(name)
				.snippet(desc));

		// Circle for the stop
		circle = map.addCircle(new CircleOptions()
				.center(latLng)
				.radius(radius)
				.fillColor(0x40ff0000)
				.strokeColor(Color.TRANSPARENT)
				.strokeWidth(2));
	}
	
	public void setVisible(boolean visible){
		
		marker.setVisible(visible);
		circle.setVisible(visible);
		
	}
	
	public void removeFromMap(){
		
		marker.remove();
		circle.remove();
	}
}
