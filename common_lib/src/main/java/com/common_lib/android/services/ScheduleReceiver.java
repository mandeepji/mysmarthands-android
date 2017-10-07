package com.common_lib.android.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public abstract class ScheduleReceiver extends BroadcastReceiver {
	
	//public static final String SET_SCHEDULE_ACTION = 
		//	"com.rbi.android.general.services.ScheduleReceiver.SetSchedule";
	public static final String EXECUTE_ACTION = 
			"com.rbi.android.general.services.ScheduleReceiver.Execute";
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		
		// execute iteration
		if(action != null){
			if(action.equals(EXECUTE_ACTION) ){
				execute(context,intent);
			}
			else if(action.equals(getStartScheduleAction())){
				startRepeating(context);
			}
			else if(action.equals(getStopScheduleAction())){
				stopRepeating(context);
			}
			else{
				executeUnkownAction(context,intent);
			}
		}
	}
	
	public void startRepeating(Context context){
		
		//Log.d("RBI", "Schecdule started");
		Intent intervalIntent = new Intent(context, this.getClass());
		intervalIntent.setAction(EXECUTE_ACTION);
		SchedulingTools.startRepeating(context,
				intervalIntent,
				repeatIntervalMillis(),
				useInexactRepeating());
	}
	
	public void stopRepeating(Context context){
		
		//Log.d("RBI", "Schecdule stopped");
		Intent intervalIntent = new Intent(context, this.getClass());
		intervalIntent.setAction(EXECUTE_ACTION);
		SchedulingTools.stopRepeating(context,intervalIntent);
	}
	
	public abstract String getStartScheduleAction();
	public abstract String getStopScheduleAction();
	
	public abstract void execute(Context context, Intent intent);
	public abstract void executeUnkownAction(Context context,Intent intent);
	
	public abstract int repeatIntervalMillis();
	public abstract boolean useInexactRepeating();
}
