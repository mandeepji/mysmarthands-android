package com.common_lib.android.apkexpansion;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.android.vending.expansion.zipfile.ZipResourceFile.ZipEntryRO;
import com.common_lib.android.Logging.Logger;

import java.util.Arrays;
import java.util.List;


public class XAPKHelper {

	Context appContext;
	int mainVersion;
	int patchVersion;
	
	
	public XAPKHelper(Context appContext, int mainVer, int patchVer){
		
		this.appContext = appContext;
		this.mainVersion = mainVer;
		this.patchVersion = patchVer;
	}
	
	//-------------------------------------------------------------------+
	public static Uri getURIFile(String authority,String fileName,String fileExtension){
	
		Uri contentUri = Uri.parse("content://" + authority);
		return Uri.parse(contentUri + "/" + fileName + fileExtension);
	}
	
	//-------------------------------------------------------------------+
	public ZipEntryRO[] getZipEntries(){
		
		ZipResourceFile expansionFile = null;
		try {
			expansionFile = getZipResourceFile();
			
		} catch (Exception e) {
			Logger.log(e);
		}
		
		return expansionFile.getAllEntries();
	}
	
	public List<ZipEntryRO> getZipEntriesList(){
		
		List<ZipEntryRO> ret = null;
		ZipEntryRO[] entries = getZipEntries();
		if(entries !=null){
			ret = Arrays.asList(entries);
		}
		
		return ret;
	}
	
	public ZipResourceFile getZipResourceFile(){
		
		ZipResourceFile expansionFile = null;
		try {
			 expansionFile = 
					APKExpansionSupport.getAPKExpansionZipFile(appContext,
							mainVersion,patchVersion);
		} catch (Exception e) {
			Logger.log(e);
		}
		
		return expansionFile;
	}
	
	public AssetFileDescriptor getUncompressedFile(String name){
		
		ZipResourceFile expansionFile = this.getZipResourceFile();
		
        return expansionFile.getAssetFileDescriptor(name);
	}
	
	public AssetFileDescriptor getUncompressedFile(String name,ZipResourceFile expFile){
		
        return expFile.getAssetFileDescriptor(name);
	}
	
	//-------------------------------------------------------------------+
}
