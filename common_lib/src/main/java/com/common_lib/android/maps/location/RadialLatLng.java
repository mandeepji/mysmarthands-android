package com.common_lib.android.maps.location;

import com.google.android.gms.maps.model.LatLng;

public class RadialLatLng{

	public LatLng latLng;
	public double radius = 0;
	
	public RadialLatLng(LatLng latLng){
		this.latLng = latLng;
	}
	
	public RadialLatLng(LatLng latLng, double radius){
		
		this.latLng = latLng;
		this.radius = radius;
	}

	public RadialLatLng(double lat,double lng, double radius){
		
		this.latLng = new LatLng(lat, lng);
		this.radius = radius;
	}

}
