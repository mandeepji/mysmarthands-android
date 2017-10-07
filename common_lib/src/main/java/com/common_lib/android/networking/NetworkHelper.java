package com.common_lib.android.networking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.common_lib.android.ui.DialogHelper;

public class NetworkHelper {
	
	//--------------------------------------------------+
	// Wifi
	public static boolean isWifiEnabled(Context context){
		
		WifiManager man = 
				(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	
		return man.isWifiEnabled();
	}
	
	public static boolean isWifiConnected(Context context){
		
		ConnectivityManager man = 
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = man.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return mWifi.isConnected();
	}
	
	public static void setWifiEnabled(Context context,boolean enabled){
		
		WifiManager man = 
				(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		
		man.setWifiEnabled(enabled);
	}
	
	public static void openWifiSettings(Context context){
		
		context.startActivity(
				new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
	}
	
	//--------------------------------------------------+
	// General Connectivity
	public static boolean isNetworkAvailable(Context context) {
		
	    ConnectivityManager cm = (ConnectivityManager) 
	      context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    // if no network is available networkInfo will be null
	    // otherwise check if we are connected
	    if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	
	public static void openNetworkSettings(Context context){
		
		context.startActivity(
				new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
	}
	
	//--------------------------------------------------+
	// Messages
	public static void downloadOverWifiDialogs(Context context,
			WifiDownloadDialogDelegate delegate){
		
		if( !isNetworkAvailable(context) ){
			showConnectionUnavailableDialog(context,delegate);
		}
		else if( !isWifiEnabled(context) ){
			showWifiRecomendedDialog(context,delegate);
		}
		else{ // ready to download over wifi
			showDownloadDialog(context,delegate);
		}
		
	}
	
	public static void showWifiRecomendedDialog(final Context context,
										final WifiDownloadDialogDelegate delegate){
		
		String msg = "A Wifi connection is recomended in order to download the app's content.\nWhat would you like to do?";
		
		DialogHelper.simpleAlertDialog(context, 
				"Content Download", msg, 
				"Enable Wifi", "Download Later", "Download anyway", 
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							// Enable Wifi
							openWifiSettings(context);
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							// Download Anyway
							delegate.downloadNowSelected();
							break;
						default: // DialogInterface.BUTTON_NEUTRAL
							// Download Later
							delegate.downloadLaterSelected();
							break;
						}
					}
				}).show();
	}
	
	public static void showConnectionUnavailableDialog(final Context context,
			final WifiDownloadDialogDelegate delegate){
		
		String msg = "No connection found. additional content is required to use the app.\nWhat would you like to do?";
		
		DialogHelper.simpleAlertDialog(context, 
				"Content Download", msg, 
				"Enable Wifi", "Download Later", "Network Settings", 
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							// Enable Wifi
							openWifiSettings(context);
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							// other network settings
							openNetworkSettings(context);
							break;
						default: // DialogInterface.BUTTON_NEUTRAL
							// Download Later
							delegate.downloadLaterSelected();
							break;
						}
					}
				}).show();
	}
	
	public static void showDownloadDialog(final Context context,
			final WifiDownloadDialogDelegate delegate){
		
		String msg = "Additional content is required to use the app.\nWhat would you like to do?";
		
		DialogHelper.simpleAlertDialog(context, 
				"Content Download", msg, 
				"Download over Wifi", "Download Later", null, 
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							// Download over Wifi
							delegate.downloadNowSelected();
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							// none
							break;
						default: // DialogInterface.BUTTON_NEUTRAL
							delegate.downloadLaterSelected();
							break;
						}
					}
				}).show();
	}
	
	public interface WifiDownloadDialogDelegate{
		
		public void downloadNowSelected();
		
		public void downloadLaterSelected();
	}
	
	//--------------------------------------------------+
}
