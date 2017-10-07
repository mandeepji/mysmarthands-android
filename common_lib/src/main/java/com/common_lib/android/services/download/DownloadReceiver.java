package com.common_lib.android.services.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class DownloadReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		
	}
	
	//-------------------------------------------------+
	// abstract interface
	public abstract void resultReceived(Context context,Intent intent);
	
	//-------------------------------------------------+
	// manifest example
	{
//	<receiver android:name="com.rbi.android.services.download.DownloadReciever" >
//        <intent-filter>
//            <action android:name="de.vogella.android.mybroadcast" />
//        </intent-filter>
//      <intent-filter>
//      	<action android:name="..." />
//  	</intent-filter>
//  </receiver>
	}
	//-------------------------------------------------+

}
