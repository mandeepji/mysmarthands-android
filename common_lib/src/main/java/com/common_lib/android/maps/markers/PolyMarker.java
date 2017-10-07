package com.common_lib.android.maps.markers;

import com.common_lib.android.maps.GoogleMapsHelper.IMarker;
import com.common_lib.android.maps.location.LocationHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;


public class PolyMarker implements IMarker{

	public Marker marker;
	public Polygon poly;
	public LatLngBounds bounds;
	
	Object tag;
	
	public PolyMarker(String title,List<LatLng> outline,GoogleMap map,int resID){
		
		poly = map.addPolygon(new PolygonOptions()
								.addAll(outline)
							);
		
		marker = map.addMarker(new MarkerOptions()
								.position(LocationHelper.centroid(poly.getPoints()))
								.title(title)
								.icon(BitmapDescriptorFactory.fromResource(resID))
							);
		
		bounds = LocationHelper.latLngsToBounds(poly.getPoints());
	}
	
	public PolyMarker(String title,List<LatLng> outline,
				List<? extends List<LatLng>> holes,GoogleMap map,int resID){
		
		this(title, outline, map,resID);
		if(holes !=null){
			poly.setHoles(holes);
		}
	}
	
	public PolyMarker(String title,List<LatLng> outline,GoogleMap map){
		
		poly = map.addPolygon(new PolygonOptions()
								.addAll(outline)
							);
		
		marker = map.addMarker(new MarkerOptions()
								.position(LocationHelper.centroid(poly.getPoints()))
								.title(title)
							);
		
		bounds = LocationHelper.latLngsToBounds(poly.getPoints());
	}
	
	public PolyMarker(String title,List<LatLng> outline,
				List<? extends List<LatLng>> holes,GoogleMap map){
		
		this(title, outline, map);
		if(holes !=null){
			poly.setHoles(holes);
		}
	}
	
	public boolean contains(LatLng point){
		
		// check holes
		if(poly.getHoles().size() >0){
			return(
				LocationHelper.outlineContainsPoint(point, poly.getPoints()) &&
				(this.containedInHole(point) ==null));
		}
		
		return LocationHelper.outlineContainsPoint(point, poly.getPoints());
	}
	
	public List<LatLng> containedInHole(LatLng point){
		
		for (List<LatLng> hole : poly.getHoles()) {
			if( LocationHelper.outlineContainsPoint(point,hole) ){
				return hole;
			}
		}
		return null;
	}
	
	public LatLng getCenter(){
		
		return marker.getPosition();
	}

	public void setColor(String hexColor){
		
		poly.setFillColor((int) Long.parseLong(hexColor, 16));
		poly.setStrokeColor((int) Long.parseLong(hexColor, 16));
	}
	
	public void setColor(int color){
		
		poly.setFillColor(color );
		poly.setStrokeColor( color );
	}
	
	//--------------------------------------------+
	// IMarker Methods
	@Override
	public LatLng getPosition() {
		
		return marker.getPosition();
	}

	@Override
	public void setVisible(boolean visible) {
		
		marker.setVisible(visible);
		poly.setVisible(visible);
	}

	@Override
	public void setDraggable(boolean draggable) {
		
		// false for now
		// change to implementing on drag and 
		// move all points in poly to delta (old marker - new marker)
		marker.setVisible(false);
		poly.setVisible(false);
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
