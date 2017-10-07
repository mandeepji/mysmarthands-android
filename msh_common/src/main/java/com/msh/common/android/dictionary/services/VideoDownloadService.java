package com.msh.common.android.dictionary.services;

import android.app.Service;
import android.content.Intent;

import com.common_lib.android.services.GeneralService;

public class VideoDownloadService extends GeneralService {

	
	
	@Override
	public void run(Intent intent, int flags, int startId) {
		
		
	}

	@Override
	public int serviceMode() {
		
		
		return Service.START_STICKY;
	}

}
 