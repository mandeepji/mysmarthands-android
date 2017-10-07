package com.common_lib.android.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;


public class ScreenOrientationController {

	Activity context;
	
	public ScreenOrientationController(Activity context){
		
		this.context = context;
//		UIHelper.adjustOrientation(context);
		
		int o = UIHelper.getCurrentOrientation(context);
//		Logger.log(o);
		switch (o) {
		case ActivityInfo.SCREEN_ORIENTATION_USER:
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;

		default:
			context.setRequestedOrientation(o);
			break;
		}
	}
	
	public int getCurrentOrientation(){
		
		return UIHelper.getCurrentOrientation(context);
	}
	
	public void flipOrientation(){
		
		int orientation = this.getCurrentOrientation();
//		Logger.log(orientation);
		
		switch (orientation) {
		case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
//			Logger.log("change to landscape");
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;

		case ActivityInfo.SCREEN_ORIENTATION_USER:
		case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
//			Logger.log("change to protrait");
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		}
	}

	
}
