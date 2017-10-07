package com.common_lib.android.maps.path;

import com.common_lib.io.json.JsonArray;
import com.common_lib.io.json.JsonObject;
import com.common_lib.io.json.JsonValue;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

public class GoogleRoute {

	// Overall
	public LatLng start,end;
	public String routeDistance,routeDuration;
	
	// Steps
	List<String> duration;
	List<String> distance;
	List<String> instruction;
	List<LatLngBounds> bounds;
	
	public GoogleRoute(GoogleMapDirections dir, int index){
		
		JsonObject defLeg = dir.getDefaultLeg();
		start = GoogleMapDirections.getStartLatLng(defLeg);
		end = GoogleMapDirections.getEndLatLng(defLeg);
		routeDistance = GoogleMapDirections.getDistanceStr(defLeg);
		routeDuration = GoogleMapDirections.getDurationStr(defLeg);
		
		JsonArray steps = dir.getSteps(index);
		int size = steps.size();
		duration = new ArrayList<String>(size);
		distance = new ArrayList<String>(size);
		instruction = new ArrayList<String>(size);
		bounds = new ArrayList<LatLngBounds>(size);
		
		JsonObject step;
		for (JsonValue stepVal : steps) {
			step = stepVal.asObject();
			duration.add( GoogleMapDirections.getDurationStr(step) );
			distance.add( GoogleMapDirections.getDistanceStr(step) );
			instruction.add( GoogleMapDirections.getHTMLInstruction(step) );
			bounds.add( GoogleMapDirections.getBounds(step) );
		}
	}
	
	public int size(){
		
		return duration.size();
	}
	
	public String getDuration(int index){
		
		return duration.get(index);
	}
	
	public String getDistance(int index){
		
		return distance.get(index);
	}

	public String getInstruction(int index){
	
		return instruction.get(index);
	}

	public LatLngBounds getBounds(int index){
	
		return bounds.get(index);
	}
	
}
