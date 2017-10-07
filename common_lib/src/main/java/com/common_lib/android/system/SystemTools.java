package com.common_lib.android.system;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.Display;
import android.view.WindowManager;

public class SystemTools {


	public static boolean apiCheck(int minApi){
		
		int currentapiVersion = VERSION.SDK_INT;
		 return (currentapiVersion >= minApi);
	}
	
	public static String getAppBundleID(Context context){
		
		return context.getPackageName();
	}
	
	public static long getMonotonicIntervalStamp(){
		
		return SystemClock.elapsedRealtime();
	}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static Point getScreenSize(Context c){
		
		WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		if(apiCheck(13)){
			display.getSize(size);
		}
		else{
			size.x = display.getWidth();  // deprecated
			size.y = display.getHeight();  // deprecated
		}
		
		return size;
	}

	@SuppressLint("NewApi")
	public static void setDeviceAwake(Context context,boolean awake){
		
		/*PowerManager powerManager =
				 (PowerManager) context.getSystemService(Activity.POWER_SERVICE);
		if(awake){
			powerManager.goToSleep(SystemClock.uptimeMillis());
		}
		else{
			powerManager.wakeUp(SystemClock.uptimeMillis());
		}*/
	}
	
}
