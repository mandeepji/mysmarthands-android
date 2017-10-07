package com.common_lib.android.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class ServiceStarterReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Intent service = new Intent(context, getServiceClass());
		context.startService(service);
	}

	public abstract Class<?> getServiceClass();

}
