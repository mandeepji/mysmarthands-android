package com.common_lib.android.ui.utils;

import android.content.Context;

import com.common_lib.android.storage.StorageHelper;
import com.common_lib.io.GeneralFileParser;
import com.common_lib.io.json.JsonArray;
import com.common_lib.io.json.JsonObject;
import com.common_lib.io.json.JsonValue;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JsonMediaScanner {

	Context context;
	String host;
	String rootDir;
	
	ScannerListener scanListener = null;
	public Map<String,String> scannedFiles;
	
	boolean tst = false;

	boolean testRan = false;
	int testCount;
	
	public JsonMediaScanner(Context context,String host,String rootDir){
		
		this.context = context;
		this.host = host;
		this.rootDir = rootDir;
		scannedFiles = new HashMap<String,String>();
		
	}
	
	public JsonMediaScanner(Context context,String host,String rootDir,
							ScannerListener scanListener){
		
		this(context, host, rootDir);
		this.scanListener = scanListener;
	}
	
	public void scan(JsonValue jVal){
		
		convertAndDownloadMediaLinks(jVal);
	}
	
	public void scanAsync(final JsonValue jVal){
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(scanListener !=null){
					scanListener.scannerDidBeginScanning(JsonMediaScanner.this,jVal);
				}
				convertAndDownloadMediaLinks(jVal);
				if(scanListener !=null){
					scanListener.scannerDidEndScanning(JsonMediaScanner.this,jVal);
				}
			}
		}).start();
	}
	
	public void convertAndDownloadMediaLinks(JsonValue jVal){
		
		JsonValue val;
		if(jVal instanceof JsonObject){
			JsonObject obj = (JsonObject) jVal;
			List<String> keys = obj.getKeys(); 
			String key;
			for (int i=0 ;i<keys.size() ;++i ){
				key = keys.get(i);
				val = obj.get(key);	
				
				if(val.isObject() || val.isArray()){
					convertAndDownloadMediaLinks(val);
				}
				else{
					URL url;
					if(val.isString() && 
							(url = GeneralFileParser.stringToURL(val.asString())) !=null ){
						String localUri = convertToLocalLinkAndDownload(url.toExternalForm());
						
						obj.replace(key, localUri);
					}
				}
			}
		}
		else if(jVal instanceof JsonArray){
			JsonArray list = (JsonArray) jVal;
			for (int index = 0; index<list.size(); ++index) {
				val = list.get(index);
				if(val.isObject() || val.isArray()){
					convertAndDownloadMediaLinks(val);
				}
				else{
					URL url;
					if(val.isString() && 
							(url = GeneralFileParser.stringToURL(val.asString())) !=null ){
						String localUri = convertToLocalLinkAndDownload(url.toExternalForm());
						list.replace(index,localUri);
					}
				}
			}
		}
	}

	// change this to exclude android libraries... make it pure java
	public String convertToLocalLinkAndDownload(String url){
		
		String dir = (host.endsWith("/"))
						? url.replace(host.substring(0,host.length()-1),"")
						: url.replace(host,"");
		
		dir = rootDir + dir;
		File savePath = StorageHelper.getExternalStorageDir(context,dir);
		
		scannedFiles.put(savePath.getAbsolutePath(),url);
//		StorageHelper.downloadFile(context,url,savePath.toURI().toString());
		
//		Logger.log(url);
//		Logger.log(dir);
//		Logger.log(savePath.toURI().toString());
		
		return savePath.getAbsolutePath();
	}

	//---------------------------------------------------------+
	public interface ScannerListener{
	
		public void scannerDidBeginScanning(JsonMediaScanner scanner, JsonValue jVal);
		
		public void scannerDidEndScanning(JsonMediaScanner scanner, JsonValue jVal);
	}
	
	//---------------------------------------------------------+
}
