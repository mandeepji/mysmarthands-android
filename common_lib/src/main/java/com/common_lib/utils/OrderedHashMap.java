package com.common_lib.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class OrderedHashMap<K,V> extends HashMap<K, V>{

	private static final long serialVersionUID = 1L;

	List<K> orderedKeys = new ArrayList<K>();
	
	public OrderedHashMap(){
		
		super();
	}
	
	public OrderedHashMap(int initialCapacity){
	
		super(initialCapacity);
	}
	
	@Override
	public V put(K key, V value) {
	
		if(orderedKeys !=null){
			orderedKeys.add( key );
		}
		return super.put(key, value);
	}
	
	@Override
	public V remove(Object key) {
	
		if(orderedKeys !=null){
			orderedKeys.remove( key );
		}
		return super.remove(key);
	}
	
	public void sort(Comparator<K> comparator){
		
		Collections.sort(orderedKeys, comparator);
	}
	
	//------------------------------------------+
	public K getKey(int index){
		
		return orderedKeys.get(index);
	}
	
	public V getValue(int index){
		
		return this.get(this.getKey(index));
	}
	
	public List<K> getOrderedKeys() {
		
		return new ArrayList<K>(orderedKeys);
	}
	
	//------------------------------------------+
}
