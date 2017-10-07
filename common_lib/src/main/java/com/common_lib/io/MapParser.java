package com.common_lib.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MapParser extends GeneralFileParser {

	public Map<String, String> map;

	public MapParser(String path) {

		this.map = MapParser.fileToMap(path);
	}
	
	public MapParser(String path, Map<String,String> defaults){
		
		this.map = MapParser.fileToMap(path);
		
		this.addDefaults(defaults);
	}

	public MapParser(Map<String,String> map){
		
		this.map = map;
	}

	public MapParser(Map<String,String> map,Map<String,String> defaults){
	
		this.map = map;
		this.addDefaults(defaults);
	}
	
	public void addDefaults(Map<String,String> defaults){
		
		// Merge Default list with map from file
		for (Entry<String,String> def: defaults.entrySet()) {
			// add default value if not overriden(found in file)
			if( ! map.containsKey( def.getKey() )){
				map.put(def.getKey(),def.getValue());
			}
		}
	}
	
	public boolean hasKey(String key){
		
		return map.containsKey(key);
	}
	
	public void put(String key,String value){
		
		map.put(key, value);
	}
	
 	public int getInt(String key) {

 		//System.out.println(map.get(key));
		return (int)Double.valueOf(map.get(key)).doubleValue();
	}

 	public long getLong(String key) {

		return Long.valueOf(map.get(key));
	}
 	
	public boolean getBool(String key) {

		return Boolean.valueOf(map.get(key));
	}

	public double getDouble(String key) {

		return Double.valueOf(map.get(key));
	}

	public char getChar(String key) {

		return Character.valueOf(map.get(key).charAt(0));
	}

	public String getString(String key) {

		return map.get(key);
	}

	public List<Integer> getList_Integer(String key) {

		return stringToList_Integer(map.get(key));
	}

	public List<Double> getList_Double(String key) {

		return stringToList_Double(map.get(key));
	}

	public List<Boolean> getList_Boolean(String key) {

		return stringToList_Boolean(map.get(key));
	}

	public List<String> getList_String(String key) {

		return stringToList_String(map.get(key));
	}

	public static List<MapParser> fileToMapParserList(String path,String seperator){
		
		List<Map<String,String>> maps = GeneralFileParser.fileToMapList(path,seperator);
		List<MapParser> ret = new ArrayList<MapParser>(maps.size());
		
		for(Map<String,String> map : maps) {
			ret.add( new MapParser(map) );
		}
		
		return ret;
	}
}
