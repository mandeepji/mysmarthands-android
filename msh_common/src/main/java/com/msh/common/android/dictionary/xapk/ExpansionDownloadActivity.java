package com.msh.common.android.dictionary.xapk;

import android.app.DownloadManager.Request;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import com.common_lib.android.vending.billing.util.SkuDetails;
import com.common_lib.android.apkexpansion.APKDownloaderActivity;
import com.msh.common.android.dictionary.Constants;
import com.msh.common.android.dictionary.AppInstance;
import com.msh.common.android.dictionary.R;
import com.msh.common.android.dictionary.test.VideoTestActivity;
import com.msh.common.android.dictionary.view.LearnActivity;
import com.common_lib.android.storage.StorageHelper;
import com.common_lib.android.vending.billing.BillingHelper;
import com.common_lib.android.vending.billing.BillingHelper.RestoreDelegate;

public class ExpansionDownloadActivity extends APKDownloaderActivity implements RestoreDelegate {

	@Override
	protected Class<?> getDownloadServiceClass() {
		return ExpansionDownloadService.class;
	}
	
	@Override
	protected XAPKFile[] getExpansionFileDefinitions() {
		
		
		 XAPKFile[] xAPKS = { 
				 
				new XAPKFile(
				true, // main apk extension file
				Constants.getInteger(Constants.CONST_KEY_XAPK_MAIN_VERSION),
				Constants.getLong(Constants.CONST_KEY_XAPK_MAIN_SIZE)
				),
			};

		return xAPKS;
	}

	@Override
	protected void setBackgroundImage(ImageView backImage) {
		
		backImage.setImageResource(R.drawable.boot_screen);
	}
	
	@Override
	protected void validationStarted() {
	
		if(Constants.getBoolean(Constants.CONST_KEY_IAP_USES_IAP)){
			BillingHelper.restorePurchases(this.getApplicationContext(),
					Constants.getString(Constants.CONST_KEY_XAPK_PUBLIC_KEY),
					Constants.getString(Constants.CONST_KEY_IAP_SKU_FULL_UNLOCK),
					this);
		}
		
		this.hideAll(true);
	}
	
	@Override
	protected void validationSuccessful() {
		
		Intent i = null;
		switch (Constants.TESTING_ACTIVITY) {
		case 1:
			i = new Intent(this, VideoTestActivity.class);
			break;

		default:
			i = new Intent(this, LearnActivity.class);
			break;
		}
		
		startActivity(i);
		this.finish();
	}

	@Override
	protected void validationFailed() {
	
		this.hideAll(false);
	}
	
	@Override
	protected void validationFailedAcknoledged() {
		
		this.finish();
	}
	
	@Override
	protected int validationInterval() {
	
		return -1;
	}
	
	@Override
	protected boolean shouldValidate() {
		return false;
	}

	@Override
	protected boolean bypassXAPKProcedure() {
	
		return Constants.VIDEO_CONTENT_BUNDLED;
	}
	
	@Override
	protected boolean manualXAPKDownload() {
		return Constants.USE_EXTERNAL_XAPK_SOURCE;
	}
	
	@Override
	protected int manualXAPKVersion() {
	
		return Constants.getInteger(Constants.CONST_KEY_XAPK_MAIN_VERSION);
	}
	
	@Override
	protected String[] manualXAPKurls() {
		
		String apkFileName = 
				StorageHelper.getXAPKFile(this.getApplicationContext(),
						true,
						manualXAPKVersion()).getName();
		
		return new String[]{Constants.EXTERNAL_XAPK_SOURCE_URL+apkFileName};
	}
	
	@Override
	protected Request customXAPKDownloadRequest(String url) {
	
		Request request = new Request(Uri.parse(url));
        request.setTitle("My Smart Hands");
        request.setDescription("Downloading Video Resources");
        
        return request;
	}
	
	@Override
	protected boolean promptForManualXAPKNetworkPermission() {
		return true;
	}
	
	//----------------------------------------------------+
	// Restore Delegate
	@Override
	public boolean shouldStorePurchasesInPrefs() {
		
		return true;
	}

	@Override
	public void skuISPurchased(SkuDetails details) {
		
		AppInstance.getCastContext().reloadDatabase();
	}

	@Override
	public void restoreFinished() {
		
		
	}
	
	//----------------------------------------------------+
}
