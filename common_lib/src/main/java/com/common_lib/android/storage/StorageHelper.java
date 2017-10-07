package com.common_lib.android.storage;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.common_lib.android.storage.sqlite.CursorHelper;
import com.common_lib.io.GeneralFileParser;
import com.google.android.vending.expansion.downloader.Helpers;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.UUID;

public class StorageHelper {

	public static long BYTES_MB = 1048576l;
	
	// ------------------------------------------------------+
	// ANDROID INTERNAL STORAGE
	public static Object readFile_internal(Context context, String filePath)
			throws IOException, StreamCorruptedException,
			FileNotFoundException, ClassNotFoundException {

		Object ret = null;

		FileInputStream fis = context.openFileInput(filePath);
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
				fis));
		ret = in.readObject();
		in.close();

		return ret;
	}

	public static void writeFile_internal(Context context, String filePath,
			Serializable ser, int fileContextPrivelages) throws IOException,
			FileNotFoundException {

		FileOutputStream fos = context.openFileOutput(filePath,
				fileContextPrivelages);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(ser);
		out.flush();
		out.close();
	}

	public static boolean deleteFile_internal(Context context, String filePath) {

		File dir = context.getFilesDir();
		File file = new File(dir, filePath);
		return file.delete();
	}

	public static boolean filePathExists(Context context, String filePath) {

		File dir = context.getFilesDir();
		File file = new File(dir, filePath);
		return file.exists();
	}

	public static String getAppFilesDir(Context context) {

		String path = context.getFilesDir().getPath();
		// removes the "files" sub dir (6 = "/files".length())
		return path.substring(0, path.length() - 6);
	}

	// ------------------------------------------------------+
	// ANDROID EXTERNAL STORAGE
	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	public static File getExternalStorageDir(Context context, String env,
			String dir, boolean isPublic) {

		File file = (isPublic) ? new File(
				Environment.getExternalStoragePublicDirectory(env), dir)
				: new File(context.getExternalFilesDir(env), dir);

		return file;
	}

	public static File getExternalStorageDir(Context context, String dir) {

		return getExternalStorageDir(context, null, dir, false);
	}

	public static void stringToFile_external(Context context, String envDir,
			String dir, String content, boolean overwrite) throws IOException,
			FileNotFoundException {

		String path = getExternalStorageDir(context, envDir, dir, true)
				.getPath();
		GeneralFileParser.stringToFile(path, content, overwrite);
	}

	public static String fileToString_external(Context context, String envDir,
			String dir) {

		String path = getExternalStorageDir(context, envDir, dir, true)
				.getPath();
		return GeneralFileParser.fileToString(path);
	}

	// ------------------------------------------------------+
	// Storage Stats
	public static long getInternalDiskSpace() {

		return getDiskSpace(Environment.getDataDirectory().getPath());
	}

	public static long getExternalDiskSpace() {

		return getDiskSpace(Environment.getExternalStorageDirectory().getPath());
	}

	public static long getInternalDiskSpaceAvailable() {

		return getDiskSpaceAvailable(Environment.getDataDirectory().getPath());
	}

	public static long getExternalDiskSpaceAvailable() {

		return getDiskSpaceAvailable(Environment.getExternalStorageDirectory().getPath());
	}

	public static long getDiskSpace(String path){
		
		StatFs stat = new StatFs(path);
		long bytesAvailable = (long)stat.getBlockCount() * (long)stat.getBlockSize();
		return bytesAvailable;
	}
	
	public static long getDiskSpaceAvailable(String path){
		
		StatFs stat = new StatFs(path);
		long bytesAvailable = (long)stat.getFreeBlocks() * (long)stat.getBlockSize();
		return bytesAvailable;
	}
	
	// ------------------------------------------------------+
	// from old library - to be revised
	public static boolean addNoMedia(Context context, String path) {

		try {
			File noMedia = new File(path, ".nomedia");
			noMedia.createNewFile();
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static void downloadFile(Context context, String url,
			FileOutputStream fos) {

		try {
			new DefaultHttpClient().execute(new HttpGet(url)).getEntity()
					.writeTo(fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void cleanDirectoryOfFileExtension(File dir, String extension) {

		File[] listing = dir.listFiles();

		for (File file : listing) {
			if (file.getName().endsWith(extension)) {
				file.delete();
			}
		}
	}

	public static File[] getDirectoryListing(String dirPath) {

		return new File(dirPath).listFiles();
	}

	public static void printDirectory(File dir) {

		File[] listing = dir.listFiles();

		for (File file : listing) {
			Log.d("RBI", file.getName());
		}
	}

	public static void printDirectory(String dirPath) {

		File[] listing = new File(dirPath).listFiles();

		for (File file : listing) {
			Log.d("RBI", file.getName());
		}
	}

	// ------------------------------------------------------+
	// XAPK Manual Downloading
	public static File getXAPKFile(Context appContext, boolean isMain,
			int xapkVersion) {

		return new File(Helpers.generateSaveFileName(appContext, Helpers
				.getExpansionAPKFileName(appContext, isMain, xapkVersion)));

	}

	public static long downloadXAPK(Context appContext, String url,
			boolean isMain, int xapkVersion,boolean allowMobileNetwork) {

		DownloadManager dm = (DownloadManager) appContext
				.getSystemService(Context.DOWNLOAD_SERVICE);

		File savePath = getXAPKFile(appContext, isMain, xapkVersion);

		Request request = new Request(Uri.parse(url));
		request.setTitle(appContext.getPackageName());
		request.setDescription("Downloading Resources " + xapkVersion);

		Uri spUri = Uri.fromFile(savePath);
		// Logger.log(spUri.toString());
		// Logger.log(GeneralFileParser.createPath(spUri.getPath())+"");
		GeneralFileParser.createPath(spUri.getPath());
		request.setDestinationUri(spUri);

		if(!allowMobileNetwork){
			request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
		}
		
		long downloadID = dm.enqueue(request);
		PreferencesHelper.set(appContext, savePath.getName(), downloadID);

		return downloadID;
	}

	public static long downloadXAPK(Context appContext, Request request,
			boolean isMain, int xapkVersion,boolean allowMobileNetwork) {

		DownloadManager dm = (DownloadManager) appContext
				.getSystemService(Context.DOWNLOAD_SERVICE);

		File savePath = getXAPKFile(appContext, isMain, xapkVersion);

		Uri spUri = Uri.fromFile(savePath);
		// Logger.log(spUri.toString());
		// Logger.log(GeneralFileParser.createPath(spUri.getPath())+"");
		GeneralFileParser.createPath(spUri.getPath());
		request.setDestinationUri(spUri);
		
		if(!allowMobileNetwork){
			request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
		}

		long downloadID = dm.enqueue(request);
		PreferencesHelper.set(appContext, savePath.getName(), downloadID);

		return downloadID;
	}

	public static long getStoredXAPKDownloadID(Context appContext,
			boolean isMain, int xapkVersion, boolean excludeCompleted) {

		String key = getXAPKFile(appContext, isMain, xapkVersion).getName();
		long id = PreferencesHelper.get(appContext).getLong(key, -1l);

		if (excludeCompleted && id > -1) {

			int status = CursorHelper.getInt(getDownloadInfo(appContext, id),
					DownloadManager.COLUMN_STATUS);
			// Logger.log("status "+status);
			if (status == DownloadManager.STATUS_FAILED
					|| status == DownloadManager.STATUS_SUCCESSFUL) {
				PreferencesHelper.remove(appContext, key);
				return -1;
			}
		}

		return id;
	}

	public static int cancelXAPKDownload(Context appContext, boolean isMain, int xapkVersion){
		
		DownloadManager dm = (DownloadManager) appContext
				.getSystemService(Context.DOWNLOAD_SERVICE);
		
		String key = getXAPKFile(appContext, isMain, xapkVersion).getName();
		int ret = dm.remove(PreferencesHelper.get(appContext).getLong(key, -1));
		PreferencesHelper.remove(appContext, key);
		
		return ret;
	}
	
	public static Cursor getXapkDownloadInfo(Context appContext,
			boolean isMain, int xapkVersion) {

		return getDownloadInfo(appContext,
				getStoredXAPKDownloadID(appContext, isMain, xapkVersion, false));
	}

	public static Cursor getDownloadInfo(Context appContext, long downloadID) {

		DownloadManager dm = (DownloadManager) appContext
				.getSystemService(Context.DOWNLOAD_SERVICE);

		Query query = new Query();
		query.setFilterById(downloadID);
		Cursor c = dm.query(query);
		if (c.moveToFirst()) {
			return c;
		}

		return null;
	}

	// ------------------------------------------------------+
	// Downloading
	@SuppressWarnings("deprecation")
	public static void downloadFile(Context context, String url, String savePath) {

		Request request = new Request(
				Uri.parse(url));
		// request.setDescription("Some description");
		request.setTitle(url);
		request.setShowRunningNotification(false);

		// in order for this if to run, you must use the android 3.2 to compile
		// your app
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// request.allowScanningByMediaScanner();
		// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		// }

		// Log.d("RBI",savePath);
		// Log.d("RBI",""+GeneralFileParser.createPath(savePath));
		Uri spUri = Uri.parse(savePath);
		// Log.d("RBI",GeneralFileParser.createPath(spUri.getPath())+"");
		GeneralFileParser.createPath(spUri.getPath());
		request.setDestinationUri(spUri);

		// get download service and enqueue file
		DownloadManager manager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		manager.enqueue(request);
	}

	public static void downloadFile(Context context, String url, String envDir,
			String dir, boolean isPublic) {

		// Log.d("RBI", getExternalStorageDir(context, envDir, dir,
		// isPublic).toURI().toString());
		downloadFile(context, url,
				getExternalStorageDir(context, envDir, dir, isPublic).toURI()
						.toString());
	}

	public static boolean isDownloadManagerAvailable(Context context) {
		try {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setClassName("com.android.providers.downloads.ui",
					"com.android.providers.downloads.ui.DownloadList");
			List<ResolveInfo> list = context.getPackageManager()
					.queryIntentActivities(intent,
							PackageManager.MATCH_DEFAULT_ONLY);
			return list.size() > 0;
		} catch (Exception e) {
			return false;
		}
	}

	// ------------------------------------------------------+
	// Temporary
	public static UUID getPhoneID(Context context) {

		// Get the phones ID
		final TelephonyManager tm = (TelephonyManager) context
				./* getBaseContext(). */getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		return new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
	}

	/*
	 * // Register for the battery changed event IntentFilter filter = new
	 * IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	 * 
	 * / Intent is sticky so using null as receiver works fine // return value
	 * contains the status Intent batteryStatus = this.registerReceiver(null,
	 * filter);
	 * 
	 * // Are we charging / charged? int status =
	 * batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1); boolean
	 * isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status
	 * == BatteryManager.BATTERY_STATUS_FULL;
	 * 
	 * boolean isFull = status == BatteryManager.BATTERY_STATUS_FULL;
	 * 
	 * // How are we charging? int chargePlug =
	 * batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1); boolean
	 * usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB; boolean
	 * acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
	 */
	// ------------------------------------------------------+

}