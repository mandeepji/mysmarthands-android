package com.msh.common.android.dictionary.xapk;

import com.common_lib.android.apkexpansion.APKDownloaderService;
import com.msh.common.android.dictionary.Constants;

public class ExpansionDownloadService extends APKDownloaderService {

	
	@Override
	public String getPublicKey() {
		
		return Constants.getString(Constants.CONST_KEY_XAPK_PUBLIC_KEY);
	}

	
	@Override
	public byte[] getSALT() {
		
		return new byte[]{32,-23,54,-54,23,12,4,5,-1,-2,-3};
	}

	
	
}
