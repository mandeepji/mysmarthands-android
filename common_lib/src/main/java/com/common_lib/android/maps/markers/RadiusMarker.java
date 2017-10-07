package com.common_lib.android.maps.markers;


import android.graphics.Color;

import com.common_lib.android.maps.GoogleMapsHelper.IMarker;
import com.common_lib.android.maps.location.RadialLatLng;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RadiusMarker implements OnMarkerDragListener,IMarker{

	public Marker marker;
//	public Marker radiusWidget;
	public Circle circle;
	
	RadialLatLng rLatLng;
	
	Object tag;
	
	public RadiusMarker(LatLng latLng){
		
		rLatLng = new RadialLatLng(latLng);
	}
	
	public RadiusMarker(RadialLatLng latLng){
		
		this.rLatLng = latLng;
	}
	
	public RadiusMarker(LatLng latLng,GoogleMap map){
		
		this.rLatLng = new RadialLatLng(latLng);
		this.addToMap(map);
	}
	
	public RadiusMarker(LatLng latLng,double radius,GoogleMap map){
		
		this.rLatLng = new RadialLatLng(latLng,radius);
		this.addToMap(map);
	}

	public void setRadius(double radius){
		
		if(circle !=null){
			circle.setRadius(radius);
		}
		rLatLng.radius = radius;
	}
	
	public double getRadius(){
		
		return rLatLng.radius;
	}
	
	public RadialLatLng getRadialLatLng(){
		
		return rLatLng;
	}
	
	//--------------------------------------------+
	// Map functions
	public void addToMap(GoogleMap map){
		
		marker = map.addMarker(new MarkerOptions()
		.position(rLatLng.latLng)
//		.title(name)
//		.snippet(desc)
		.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
		
//		radiusWidget = map.addMarker(new MarkerOptions()
//		.position(latLng)
//		.icon(BitmapDescriptorFactory
//				.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

		// Circle for the stop
		circle = map.addCircle(new CircleOptions()
		.center(rLatLng.latLng)
		.radius(rLatLng.radius)
		.fillColor(0x40ff0000)
		.strokeColor(Color.TRANSPARENT)
		.strokeWidth(2));
	}

	@Override
	public LatLng getPosition() {
		
		return rLatLng.latLng;
	}
	
	@Override
	public void setVisible(boolean visible){
		
		marker.setVisible(visible);
		circle.setVisible(visible);
		
	}

	@Override
	public void setDraggable(boolean draggable) {
	
		marker.setDraggable(draggable);
	}
	
	public void removeFromMap(){
		
		marker.remove();
		circle.remove();
	}

	//--------------------------------------------+
	// Marker Drag Event
	@Override
	public void onMarkerDrag(Marker m) {
		
		
	}

	@Override
	public void onMarkerDragEnd(Marker m) {
		
		rLatLng.latLng = m.getPosition();
		circle.setCenter(m.getPosition());
	}

	@Override
	public void onMarkerDragStart(Marker m) {
		
		
	}

	
	@Override
	public Object getTag() {
		
		return tag;
	}

	
	@Override
	public void setTag(Object obj) {
		
		this.tag = obj;
	}
	
	//--------------------------------------------+
}
