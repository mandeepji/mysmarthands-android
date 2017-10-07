package com.common_lib.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


public abstract class GeneralService extends Service {
	

	private final IBinder mBinder = new GeneralBinder();

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		run(intent, flags, startId);
		return serviceMode();
		
	}

	@Override
	public IBinder onBind(Intent arg0) {

		return mBinder;
	}

	// -------------------------------------------------------------------+
	public abstract void run(Intent intent, int flags, int startId);

	public abstract int serviceMode();

	// -------------------------------------------------------------------+
	public class GeneralBinder extends Binder {

		public GeneralService getGeneralService() {

			return GeneralService.this;
		}
	}

	// -------------------------------------------------------------------+
}
