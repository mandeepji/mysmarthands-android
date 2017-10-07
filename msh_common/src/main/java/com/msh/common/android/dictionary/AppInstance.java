package com.msh.common.android.dictionary;

import com.common_lib.android.apkexpansion.XAPKHelper;
import com.msh.common.android.dictionary.database.MSHDatabaseAdapter;
import com.common_lib.android.storage.PreferencesHelper;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;


public class AppInstance extends Application {
	
	private static Context context;

	public DatabaseChangeListener dbChangeListener;

	public AppInstance() {

		super();
	}

	public void onCreate() {

		super.onCreate();
		AppInstance.context = getApplicationContext();
		Constants.init(AppInstance.context);
	}

	// --------------------------------------------------------+
	public static Context getContext() {

		return context;
	}

	public static AppInstance getCastContext() {

		return (AppInstance) context;
	}

	// --------------------------------------------------------+
	// static functionality
	public static void playVideo(VideoView vv, String videoName, boolean isQuiz) {

		vv.setVisibility(View.VISIBLE);

		videoName = applyVideoNamePathFilters(videoName);
		if (isQuiz) {
			videoName += "_quiz";
		}
		
		Uri vidUri;
		if(Constants.VIDEO_CONTENT_BUNDLED){
			vidUri = Uri.parse( getBundledVideoPath(videoName) );
		}
		else{
			vidUri = XAPKHelper.getURIFile(
					Constants.getString(Constants.CONST_KEY_XAPK_PROVIDER_AUTHORITY),
					videoName, Constants.getString(Constants.CONST_KEY_VIDEO_FORMAT_EXT));

		}
		// Log.d("RBI",vidUri.toString());
		vv.setVideoURI(vidUri);
	}

	public static void playDummyVid(VideoView vv) {

        vv.setVisibility(View.VISIBLE);

        String videoName = MSHDatabaseAdapter.getInstance().getFirstVid();
		videoName = applyVideoNamePathFilters(videoName);

		Uri vidUri;
		if(Constants.VIDEO_CONTENT_BUNDLED){
			vidUri = Uri.parse( getBundledVideoPath(videoName) );
		}
		else{
			vidUri = XAPKHelper.getURIFile(
					Constants.getString(Constants.CONST_KEY_XAPK_PROVIDER_AUTHORITY),
					videoName, Constants.getString(Constants.CONST_KEY_VIDEO_FORMAT_EXT));
		}
		// Log.d("RBI",vidUri.toString());
		vv.setVideoURI(vidUri);
		
	}
	
	public static void loadImage(ImageView iv,String imgName){
		
		Uri imgUri = XAPKHelper.getURIFile(
				Constants.getString(Constants.CONST_KEY_XAPK_PROVIDER_AUTHORITY),
				imgName, ".png");
		iv.setImageURI(imgUri);
	}

	public static boolean fullVersionPurchased(){
		
		return PreferencesHelper
				.get(context).getBoolean(Constants.getString(Constants.CONST_KEY_IAP_SKU_FULL_UNLOCK),false);
	}

	public void reloadDatabase() {

		MSHDatabaseAdapter newInstance = MSHDatabaseAdapter.resetInstance();

		if (dbChangeListener != null) {
			dbChangeListener.DatabaseDidReload(newInstance);
		}
	}

	public static String applyVideoNamePathFilters(String videoName){
		
		videoName = videoName.replace("/", "_");
		videoName = videoName.replace("'","");
		// videoName = videoName.replace(" ","%20");
		
		return videoName;
	}
	
	private static String getBundledVideoPath(String videoName){
		
		return "android.resource://com.rbi/"+
							getContext().getPackageName()+
							"/assets/videos/"+videoName;
		
	}
	
	public static boolean shouldShowAds(String adUnitID){
		
		if(adUnitID == null){
			return false;
		}
		
		if(Constants.getBoolean(Constants.CONST_KEY_IAP_USES_IAP)){
			if(Constants.OVERRIDE_IAP){
				return false;
			}
			else{
				return (Constants.getBoolean(Constants.CONST_KEY_USES_ADMOB) &&
						!fullVersionPurchased() );
			}
			
		}
		
		return Constants.getBoolean(Constants.CONST_KEY_USES_ADMOB);
	}
	
	public static void getAdRequest(){
		
		
	}
	
	// --------------------------------------------------------+
	// DatabaseChangeListener
	public interface DatabaseChangeListener {

		public void DatabaseDidReload(MSHDatabaseAdapter newInstance);
	}

	// --------------------------------------------------------+

}
