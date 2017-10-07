package com.common_lib.android.services;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;


public abstract class GeneralServiceActivity extends Activity {

	private GeneralServiceConnection serviceConnection = new GeneralServiceConnection();
	protected GeneralService connectedService = null;
	
	public void bindService(Class<? extends GeneralService> serviceClass) {

		bindService(new Intent(this, serviceClass), serviceConnection,
	        Context.BIND_AUTO_CREATE);
	}
	
	public void bindServiceFromApplication(Class<? extends GeneralService> serviceClass){
		
		getApplicationContext().bindService(new Intent(this, serviceClass), serviceConnection,
		        Context.BIND_AUTO_CREATE);
	}
	
	public void unbindService(){
		
		unbindService(serviceConnection);
		connectedService = null;
	}
	
	public void unbindServiceFromApplication(){
		
		getApplicationContext().unbindService(serviceConnection);
		connectedService = null;
	}
	
	@Override
	public void finish() {
		
		if(connectedService != null)
			this.unbindService();
		super.finish();
	}
	
	public GeneralServiceConnection getServiceConnection(){
		
		return serviceConnection;
	}
	
	public GeneralService getconnectedService(){
		
		return connectedService;
	}
	
	//-------------------------------------------------------------+
	// Service Connection absorbed interface via abstract methods
	public abstract void onGeneralServiceConnected(ComponentName name, GeneralService serv);
	
	public abstract void onGeneralServiceConnectionLost(ComponentName name);
	
	//-------------------------------------------------------------+
	// Service Connection
	public class GeneralServiceConnection implements ServiceConnection{

		public void onServiceConnected(ComponentName name, IBinder binder) {

			//Log.d( "RBI", "onConnected" );
			connectedService = 
					((GeneralService.GeneralBinder) binder).getGeneralService();
			
			onGeneralServiceConnected(name, connectedService);
		}

		public void onServiceDisconnected(ComponentName name) {
			
			connectedService = null; // not sure if the connection is unregestered on erroneous disconnect
			onGeneralServiceConnectionLost(name);
		}
	}
	
	//-------------------------------------------------------------+
}
