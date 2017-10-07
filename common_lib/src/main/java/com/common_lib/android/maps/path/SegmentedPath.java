package com.common_lib.android.maps.path;

import com.common_lib.io.json.JsonArray;
import com.common_lib.io.json.JsonValue;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SegmentedPath extends ArrayList<Path>{

	private static final long serialVersionUID = 1L;

	Map<GoogleMap,Map<Path,Polyline>> mapLines = 
			new HashMap<GoogleMap, Map<Path,Polyline>>(2);
	
	
	public SegmentedPath(){
		
		super();
	}
	
	public SegmentedPath(List<Path> paths){
		
		super(paths);
	}
	
	public SegmentedPath(JsonArray paths){
		
		super(paths.size());

		for (JsonValue path : paths) {
			this.add( new Path(path.asArray()) );
		}
	}
	
	public Path getAsPath(){
		
		Path combined = new Path();
		for (Path p: this) {
			combined.addAll(p);
		}
		return combined;
	}
	
	//--------------------------------------------+
	// Drawing
	public void draw(GoogleMap map,int start,int end,int color){
		
		Map<Path,Polyline> lines = 
				new HashMap<Path, Polyline>(this.size());
		
		boolean drawbetween = true;
		
		Path path;
		for (int i=start; i<end; ++i) {
			if(i>= this.size()) break;
			path = this.get(i);
			if(drawbetween && i<end-1){
				path.add( this.get(i+1).get(0) ); // add first point from next path
			}
			lines.put(path, path.draw(map,color));
		}
		
		mapLines.put(map,lines);
	}
	
	public void drawAll(GoogleMap map,int color){
		
		this.draw(map, 0, this.size(), color);
	}
	
	public void setVissible(GoogleMap map,boolean visible,int start, int end){
		
		Map<Path,Polyline> lines = mapLines.get(map);
		
		for (int i=start; i<end; ++i) {
			if(i>= this.size()) break;
			lines.get(this.get(i)).setVisible(visible);
		}
	}
	
	public void setColor(GoogleMap map,int start, int end, int color){
		
		Map<Path,Polyline> lines = mapLines.get(map);
		
		for (int i=start; i<end; ++i) {
			if(i>= this.size()) break;
			lines.get(this.get(i)).setColor(color);
		}
	}
	
	public void removeAll(GoogleMap map){
		
		Map<Path,Polyline> lines = mapLines.get(map);
		if(lines ==null) return;
		
		for (Path path : this) {
			lines.get(path).remove();
		}
	}
	
	//--------------------------------------------+
}
