package com.common_lib.android.maps.path;

import com.common_lib.io.json.JsonArray;
import com.common_lib.io.json.JsonObject;
import com.common_lib.io.json.JsonValue;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


public class Path extends ArrayList<LatLng>{

	private static final long serialVersionUID = 1L;
	
	
	public Path(){
		
		super();
	}
	
	public Path(List<LatLng> points){
		
		super(points);
	}
	
	public Path(JsonArray points){
		
		super(points.size());
		for (JsonValue val : points) {
			JsonObject latLng = val.asObject();
//			Log.d("RBI", latLng.toString());
			this.add( new LatLng(
						latLng.get("lattitude").asDouble(),
						latLng.get("longitude").asDouble()) );
		}
	}

	//--------------------------------------------+
	// Drawing
	public Polyline draw(GoogleMap map, int color){
		
		PolylineOptions plOptions = new PolylineOptions()
		.width(5)
     	.color(color)
		.addAll(this);
		
		// Get back the mutable Polyline
		return map.addPolyline(plOptions);
	}
	
	public static Polyline drawBetween(GoogleMap map,Path p1, Path p2, int color){
		
		PolylineOptions plOptions = new PolylineOptions()
		.width(5)
     	.color(color)
		.add(p1.get(0),p2.get(0));
		
		// Get back the mutable Polyline
		return map.addPolyline(plOptions);
	}
	
	//--------------------------------------------+
}
