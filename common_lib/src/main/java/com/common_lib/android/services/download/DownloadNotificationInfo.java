package com.common_lib.android.services.download;

import java.io.Serializable;


public class DownloadNotificationInfo implements Serializable{

	private static final long serialVersionUID = 1L;

	public int iconResID,largeIconResID = -1;
	public String title;
	public Class<?> resultActivity;
	
	public DownloadNotificationInfo(String title,int iconResID) {
		
		this.title = title;
		this.iconResID = iconResID;
	}
	
}
