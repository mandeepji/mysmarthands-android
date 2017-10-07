package com.common_lib.android.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class SchedulingTools {

public static void startRepeating(Context context, Intent intent, int repeatMillis,boolean useInexact){
		
		AlarmManager alarmService = (AlarmManager) 
				context.getSystemService(Context.ALARM_SERVICE);

		PendingIntent pending = PendingIntent.getBroadcast(context,0,intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, repeatMillis);

		// Fetch every X milliseconds
		// InexactRepeating allows Android to optimize the energy consumption
		if( useInexact ){
			alarmService.setInexactRepeating(AlarmManager.RTC_WAKEUP,
					cal.getTimeInMillis(), repeatMillis, pending);
		}
		else{
			alarmService.setRepeating(AlarmManager.RTC_WAKEUP, 
					cal.getTimeInMillis(),repeatMillis, pending);
		}
	}
	
	public static void stopRepeating(Context context,Intent intent){

		PendingIntent pending = PendingIntent.getBroadcast(context,0,intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		
		AlarmManager alarmService = (AlarmManager) 
				context.getSystemService(Context.ALARM_SERVICE);
		
		// Should cancel based on intent.filterEquals(otherIntent)
		alarmService.cancel(pending);
	}
	
	
	public static void startRepeating(Context context, Class<?> intervalReciever, int repeatMillis,boolean useInexact){
		
		AlarmManager alarmService = (AlarmManager) 
				context.getSystemService(Context.ALARM_SERVICE);

		Intent i = new Intent(context,intervalReciever);

		PendingIntent pending = PendingIntent.getBroadcast(context,0,i,
				PendingIntent.FLAG_CANCEL_CURRENT);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, repeatMillis);

		// Fetch every X milliseconds
		// InexactRepeating allows Android to optimize the energy consumption
		if( useInexact ){
			alarmService.setInexactRepeating(AlarmManager.RTC_WAKEUP,
					cal.getTimeInMillis(), repeatMillis, pending);
		}
		else{
			alarmService.setRepeating(AlarmManager.RTC_WAKEUP, 
					cal.getTimeInMillis(),repeatMillis, pending);
		}
	}
	
	public static void stopRepeating(Context context,Class<?> intervalReciever){
		
		Intent i = new Intent(context,intervalReciever);

		PendingIntent pending = PendingIntent.getBroadcast(context,0,i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		
		AlarmManager alarmService = (AlarmManager) 
				context.getSystemService(Context.ALARM_SERVICE);
		
		// Should cancel based on intent.filterEquals(otherIntent)
		alarmService.cancel(pending);
	}

}
