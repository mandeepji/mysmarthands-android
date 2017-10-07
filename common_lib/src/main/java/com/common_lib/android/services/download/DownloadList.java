package com.common_lib.android.services.download;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DownloadList extends HashMap<String,String> implements Serializable{

	private static final long serialVersionUID = 123L;

	public DownloadList() {
		
		super();
	}
	
	public DownloadList(Map<String, String> map){
		
		super(map);
	}
	
}
