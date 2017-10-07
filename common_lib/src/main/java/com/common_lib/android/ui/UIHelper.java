package com.common_lib.android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class UIHelper {

	static Map<Activity, AlertDialog> backBtnBlockDialogs;
	
	// ----------------------------------------------------+
	// Activity Configurations
	public static void removeTitleBar(Activity activity) {

		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	public static void removeNotificationBar(Activity activity) {

		activity.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/*
	 * has to call setContentView(int resID) for you since request window and
	 * getWindow calls have to wrap setContentView
	 */
	public static void setCustomTitleBar(Activity activity, int contentResID,
			int customTitleResID) {

		activity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		activity.setContentView(contentResID);
		activity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				customTitleResID);
	}
	
	public static void enableLockScreen(Activity activity,boolean enabled){
		
		if(enabled){
			activity.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
					WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		}
		else{
			activity.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
					WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		}
	}
	
	public static boolean isScreenOn(Activity activity){
		
		 PowerManager powerManager = 
				 (PowerManager) activity.getSystemService(Activity.POWER_SERVICE);
		 
		 return powerManager.isScreenOn();
	}
	
	public static void physicalMenuBtnHide(Activity activity){
		
		try {
	        ViewConfiguration config = ViewConfiguration.get(activity);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception ex) {
	        // Ignore
	    }
	}
	
	// ----------------------------------------------------+
	public static void setOrientationManually(Activity activity,int orientation){
		
		activity.setRequestedOrientation(orientation);
	}
	
	public static void adjustOrientation(Activity activity){
		
		int o = getCurrentOrientation(activity);
		activity.setRequestedOrientation(o);
	}
	
	public static int getCurrentOrientation(Activity activity){
		
		return activity.getResources().getConfiguration().orientation;
	}
	
	// ----------------------------------------------------+
	// Orientation Locking
	public static void lockOrientationPortrait(Activity activity){
		
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	public static void lockOrientationLandscape(Activity activity){
		
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	public static void unlockOrientation(Activity activity){
		
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}
	
	public static void setAutoOrientationEnabled(Activity activity, boolean enabled){
	  
		Settings.System.putInt(
			  activity.getContentResolver(), 
			  Settings.System.ACCELEROMETER_ROTATION, 
			  enabled ? 1 : 0);
	}
	
	// ----------------------------------------------------+
	// Back Button Exit Warning
	public static void blockBackActionWithWarning(
			final Activity activity, 
			String title, String msg,
			final BlockBackActionListener l){
		
		if(backBtnBlockDialogs ==null){
			backBtnBlockDialogs = new HashMap<Activity, AlertDialog>(3);
		}
//		Log.d("RBI", backBtnBlockDialogs.size()+"");
		
		if( !backBtnBlockDialogs.containsKey(activity) ){
			AlertDialog a = DialogHelper.simpleAlertDialog(activity, 
					title, 
					msg, 
					"Exit", 
					"Cancel", 
					null, 
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							switch (which) {
							// Exit
							case AlertDialog.BUTTON_POSITIVE:
								if(l ==null || l.shouldFinish()){
									activity.finish();
									backBtnBlockDialogs.remove(activity);
								}
								break;
							// cancel
							default:
								if(l !=null){
									l.cancelPressed();
								}
								break;
							}
						}
					});
			a.show();
		}
		else{
			backBtnBlockDialogs.remove(activity);
		}
		
	}
	
	public static void popToActivity(Context context,Class<?> activityClass){
		
		Intent intent = new Intent(context, activityClass);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
		context.startActivity(intent);
	}
	
	// ----------------------------------------------------+
	public interface BlockBackActionListener{
		
		public boolean shouldFinish();
		public void cancelPressed();
	}
	
	// ----------------------------------------------------+
}
