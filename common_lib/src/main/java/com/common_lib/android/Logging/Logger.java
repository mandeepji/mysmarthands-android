package com.common_lib.android.Logging;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {

	private static String LOG_TAG = "RBI";
	
	// add log to server or to file functionality
	
	public static void Toast(final Activity activity,final String message){
		
		activity.runOnUiThread(new Runnable() {
			
			public void run() {
				Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public static String stackTraceToString(Throwable t){
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		
		return sw.toString();
	}

	//----------------------------------------------------+
	public static void log(String tag,String msg){
		
		if(msg == null){
			msg = "null";
		}
		Log.d(tag, msg);
	}
	
	public static void log(String tag,int msg){
		
		Log.d(tag, msg+"");
	}
	
	public static void log(String tag,double msg){
		
		Log.d(tag, msg+"");
	}
	
	public static void log(String tag,float msg){
		
		Log.d(tag, msg+"");
	}
	
	public static void log(String tag,long msg){
		
		Log.d(tag, msg+"");
	}
	
	public static void log(String tag,boolean msg){
		
		Log.d(tag, msg+"");
	}
	
	public static void log(String tag,Object msg){
		
		if(msg == null){
			msg = "null";
		}
		Log.d(tag, msg.toString());
	}
	
	public static void log(String msg){
		
		if(msg == null){
			msg = "null";
		}
		Log.d(LOG_TAG, msg);
	}
	
	public static void log(int msg){
		
		Log.d(LOG_TAG, msg+"");
	}
	
	public static void log(double msg){
		
		Log.d(LOG_TAG, msg+"");
	}
	
	public static void log(float msg){
		
		Log.d(LOG_TAG, msg+"");
	}
	
	public static void log(long msg){
		
		Log.d(LOG_TAG, msg+"");
	}
	
	public static void log(boolean msg){
		
		Log.d(LOG_TAG, msg+"");
	}
	
	public static void log(Object msg){
		
		if(msg == null){
			msg = "null";
		}
		Log.d(LOG_TAG, msg.toString());
	}
	
	public static void log(){
		
		Log.d(LOG_TAG, "\n");
	}
	
	//----------------------------------------------------+
}



