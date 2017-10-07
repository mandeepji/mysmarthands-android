package com.msh.common.android.dictionary.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import com.msh.common.android.dictionary.R;
import com.msh.common.android.dictionary.database.MSHDatabaseAdapter;
import com.common_lib.android.storage.StorageHelper;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;


public class DownloadIntentService extends IntentService {
	
	public enum Directory{
		
		INTERNAL_DIRECTORY,
		EXTERNAL_DIRECTORY
	};

	private static final int DOWNLOAD_NOTIF_ID = 1;
	
	private static Directory storageDir;
	private static String videoServerPath;
	private static String videoServerPath_quiz;
	private static int pixelHeight;
	private static boolean seperateQuizVideos = false; // set and read from database
	
	public static final String SERVICE_CHECK_FILENAME = "check";
	public static final String SERVICE_INCOMPLETE_FILENAME = "incomplete";
	public static final String SERVICE_PROGRESS_FILENAME = "progress";
	public static final String SERVICE_PROGRESS_DIV = "/";
	
	public static final String SERVER_SCRIPT_VIDEOGET = "videoGet.php";
	public static final String SERVER_SCRIPT_VIDEOGET_QUIZ = "videoGet_quiz.php";
	public static final String QUIZ_FILENAME_EXT = "_quiz";
	public static final String SERVER_SCRIPT_VIDEOUPDATE = "updateCheck.php";
	public static final String SERVER_SCRIPT_DOWNLSIZE = "sizeGet.php";

	private static boolean RUNNING = false;

	public DownloadIntentService() {

		super("DownloadIntentService");
	}

	@Override
	public void onDestroy() {

		RUNNING = false;
		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		if (RUNNING) {
			return;
		}
		RUNNING = true;
		this.deleteCheckFile(SERVICE_CHECK_FILENAME);
		Bundle bundle = intent.getExtras();
		pixelHeight = bundle.getInt( getString(R.string.resHeight) );
		videoServerPath = this.stringFromURL(bundle.getString(getString(R.string.urlKEY)));
		videoServerPath_quiz = videoServerPath + SERVER_SCRIPT_VIDEOGET_QUIZ;
		videoServerPath += SERVER_SCRIPT_VIDEOGET;
		
		storageDir = Directory.valueOf( bundle.getString(getString(R.string.storageKey)) );
		seperateQuizVideos = bundle.getBoolean( getString(R.string.seperateQuizKey) );
		
		//Log.d("RBI",videoServerPath);
		boolean err = bundle.getBoolean(getString(R.string.serviceErrKey));
		ArrayList<String> words = 
				bundle.getStringArrayList(getString(R.string.quizSetKEY));
		fireStartedNotification();
		
		// CREATE EXTERNAL VIDEOS FOLDER
		try {
			String packageName = this.getApplicationContext().getPackageName();
			File externalPath = Environment.getExternalStorageDirectory();
			File extenalVidFolder = new File(externalPath.getAbsolutePath() +
			                         "/Android/data/" + packageName + "/videos");
			extenalVidFolder.mkdirs();
			File noMedia = new File(extenalVidFolder,".nomedia");
			noMedia.createNewFile();
			
			// cleans external folder old videos
			StorageHelper.cleanDirectoryOfFileExtension(extenalVidFolder,".3gp");
			
		} catch (Exception e) {}
		
		if (err)
			downloadIncomplete(words);
		else
			this.downloadAll(words);

		//this.printFiles();
	}

	public void killService() {

		this.stopSelf();
	}

	// -----------------------DOWNLOADING------------------------+
	private void downloadAll(ArrayList<String> words) {

		int count = 0;
		int numOfWords = words.size();
		ArrayList<String> failed = new ArrayList<String>(words.size());		

		for (String word : words) {
			if(!downloadFile(word)) {
				//Log.d("RBI", word);
				failed.add(word);
			}
			
			if(seperateQuizVideos  && 
					!downloadFile_Quiz(word+QUIZ_FILENAME_EXT)) {
				//Log.d("RBI quiz", word);
				failed.add(word+QUIZ_FILENAME_EXT);
			}
			
			count++;
			this.writeProgressFile( count,numOfWords );
			// Progress bar - future
			// Log.d("RBI", "" + count);
		}

		// Retry
		if (!failed.isEmpty()) {
			count = 0;
			numOfWords = failed.size();
			// Log.d("RBI", "FAILED:");
			for (String string : failed) {
				// Log.d("RBI", string);
				downloadFile(string);
				count++;
				this.writeProgressFile( count,numOfWords );
			}
			if (!this.errorCheck(words).isEmpty()){
				writeCheckFile(SERVICE_INCOMPLETE_FILENAME);
				fireErrorNotification();
			}
			else
				writeCompletion();
		}
		// Completion
		else
			writeCompletion();

	}

	private void downloadIncomplete(ArrayList<String> words) {

		ArrayList<String> task = this.errorCheck(words);
		ArrayList<String> failed = new ArrayList<String>(task.size());

		int count = 0;
		int numOfWords = task.size();
		
		for (String word : task) {
			if (!downloadFile(word)) {
				// Log.d("RBI", word);
				failed.add(word);
			}
			count++;
			this.writeProgressFile( count,numOfWords );
			// Progress bar - future
			// Log.d("RBI", "" + ++count);
		}

		if (failed.isEmpty())
			writeCompletion();
		else{
			writeCheckFile(SERVICE_INCOMPLETE_FILENAME);
			fireErrorNotification();
		}
	}

	private boolean downloadFile(String name) {

		// URL ENCODING
		name = name.replace("/", "_")+MSHDatabaseAdapter.VID_EXT;
		//Log.d("RBI", name);
		//name = name.replace("/", ":")+MSHDatabaseAdapter.VID_EXT; // FORCE ERROR EXTERNAL
		//Log.d("RBI", "Saved As:"+name);
		String serverFileName = name.replace(" ", "%20");
		//serverFileName = serverFileName.replace("_", ":");
		serverFileName = serverFileName.replace("'","");
		FileOutputStream fos;

		//Log.d("RBI", serverFileName);
		
		// INTERNAL
		if(storageDir == Directory.INTERNAL_DIRECTORY){
			try {
				fos = openFileOutput(name, Context.MODE_WORLD_READABLE);
				new DefaultHttpClient()
					.execute(new HttpGet(fillHttpParams(videoServerPath,serverFileName)))
					.getEntity().writeTo(fos);
				fos.close();
				
				//Log.d("RBI",""+getFileStreamPath(name));
				// Doesn't Exist
				if(getFileStreamPath(name).length()==0){
					//Log.d("RBI", "ERROR: " + name+" Does Not Exist");
					deleteFile(name);
					return false;
				}
			}catch(Exception e){
				//Log.d("RBI", "ERROR: " + e.toString());
				deleteFile(name);
				return false;
			}
		}
		// EXTERNAL
		else{
			try{
				String packageName = this.getApplicationContext().getPackageName();
				File externalPath = Environment.getExternalStorageDirectory();
				File vidFile = new File(externalPath.getAbsolutePath() +
				                         "/Android/data/" + packageName + "/videos/" + name);
				fos = new FileOutputStream(vidFile);
				
				new DefaultHttpClient()
					.execute(new HttpGet(fillHttpParams(videoServerPath,serverFileName)))
					.getEntity().writeTo(fos);
				fos.close();
				
				//Log.d("RBI",""+vidFile);
				// Doesn't Exist
				if(vidFile.length()==0){
					//Log.d("RBI", "ERROR: " + name+" Does Not Exist");
					deleteFile(name);
					return false;
				}
			}catch(Exception e){
				//Log.d("RBI", "ERROR: " + e.toString());
				deleteFile(name);
				return false;
			}
		}
		return true;
	}

	private boolean downloadFile_Quiz(String name) {

		// URL ENCODING
		name = name.replace("/", "_")+MSHDatabaseAdapter.VID_EXT;
		//name = name.replace("/", ":")+MSHDatabaseAdapter.VID_EXT; // FORCE ERROR EXTERNAL
		//Log.d("RBI", "Saved As:"+name);
		String serverFileName = name.replace(" ", "%20");
		//serverFileName = serverFileName.replace("_", ":");
		serverFileName = serverFileName.replace("'","");
		FileOutputStream fos;

		//Log.d("RBI", serverFileName);
		
		// INTERNAL
		if(storageDir == Directory.INTERNAL_DIRECTORY){
			try {
				fos = openFileOutput(name, Context.MODE_WORLD_READABLE);
				new DefaultHttpClient()
					.execute(new HttpGet(fillHttpParams(videoServerPath_quiz,serverFileName)))
					.getEntity().writeTo(fos);
				fos.close();
				
				//Log.d("RBI",""+getFileStreamPath(name));
				// Doesn't Exist
				if(getFileStreamPath(name).length()==0){
					//Log.d("RBI", "ERROR: " + name+" Does Not Exist");
					deleteFile(name);
					return false;
				}
			}catch(Exception e){
				//Log.d("RBI", "ERROR: " + e.toString());
				deleteFile(name);
				return false;
			}
		}
		// EXTERNAL
		else{
			try{
				String packageName = this.getApplicationContext().getPackageName();
				File externalPath = Environment.getExternalStorageDirectory();
				File vidFile = new File(externalPath.getAbsolutePath() +
				                         "/Android/data/" + packageName + "/videos/" + name);
				fos = new FileOutputStream(vidFile);
				
				new DefaultHttpClient()
					.execute(new HttpGet(fillHttpParams(videoServerPath_quiz,serverFileName)))
					.getEntity().writeTo(fos);
				fos.close();
				
				//Log.d("RBI",""+vidFile);
				// Doesn't Exist
				if(vidFile.length()==0){
					//Log.d("RBI", "ERROR: " + name+" Does Not Exist");
					deleteFile(name);
					return false;
				}
			}catch(Exception e){
				//Log.d("RBI", "ERROR: " + e.toString());
				deleteFile(name);
				return false;
			}
		}
		return true;
	}
	
	private String fillHttpParams(String serverPath, String name) {

		//Log.d("RBI", videoServerPath + "?code=hello&height=" + pixelHeight + "&filename="
			//	+ name + "");
		
		return serverPath + "?code=hello&height=" + pixelHeight + "&filename="
				+ name;
	}
	
	public ArrayList<String> errorCheck(ArrayList<String> words) {

		String[] files;
		if(storageDir == Directory.INTERNAL_DIRECTORY)
			files = fileList();
		else{
			String packageName = this.getApplicationContext().getPackageName();
			File externalPath = Environment.getExternalStorageDirectory();
			File extenalVidFolder = new File(externalPath.getAbsolutePath() +
			                         "/Android/data/" + packageName + "/videos");
			files = extenalVidFolder.list();
		}
			
		// EXISTING VIDEOS
		Set<String> fileSet = new HashSet<String>(files.length);
		for (String string : files)
			fileSet.add(string.replace(":", "/").replace(MSHDatabaseAdapter.VID_EXT,""));

		//Log.d("RBI", "ERROR TEST: "+files[0]+" "+words.get(0));

		// ALL NAMES IN DB
		Set<String> dbSet = new HashSet<String>(words);

		// SET DIFFERENCE AKA MISSING FROM VIDEO COLLECTION
		// Log.d("RBI", "err: "+dbSet.size());
		dbSet.removeAll(fileSet);
		
		//Log.d("RBI", "err: "+dbSet.size());
		return new ArrayList<String>(dbSet);
	}

	public void writeCompletion() {

		deleteCheckFile(SERVICE_INCOMPLETE_FILENAME);
		writeCheckFile(SERVICE_CHECK_FILENAME);
		fireCompleteNotification();
	}

	// -----------------------Networking_Helper----------------------------+
	public String stringFromURL(String urlStr){

		String ret = "";
		
		try {
			URL url = new URL(urlStr);
			BufferedReader in = new BufferedReader(
			            new InputStreamReader( url.openStream() ));

			String inputLine;
			
			while ((inputLine = in.readLine()) != null)
			    ret += inputLine;

			in.close();
			
		} catch (Exception e) {}
		
		return ret;
	}

	// -----------------------SERVICE_CHECKS------------------------+
	public static boolean isRunning() {

		return RUNNING;
	}

	private void writeCheckFile(String name) {

		try {
			FileOutputStream fos = openFileOutput(name,
					Context.MODE_WORLD_READABLE);
			fos.write(name.getBytes());
			fos.close();
		} catch (Exception e) {
		}
	}

	private void writeProgressFile(int currentIndex, int total) {
		
		try {
			FileOutputStream fos = openFileOutput(SERVICE_PROGRESS_FILENAME,
					Context.MODE_WORLD_READABLE);
			fos.write(String.valueOf(currentIndex).getBytes());
			fos.write(SERVICE_PROGRESS_DIV.getBytes());
			fos.write(String.valueOf(total).getBytes());
			fos.close();
		} catch (Exception e) {
		}
	}
	
	private void deleteCheckFile(String name) {

		deleteFile(name);
	}

	/*
	private boolean fileCheck(String name) {

		File file = getFileStreamPath(name);
		return file.exists();
	}

	private void printFiles() {

		String[] files = fileList();
		Log.d("RBI", getFilesDir().getAbsolutePath());

		for (String string : files) {
			Log.d("RBI", string);
		}
	}
	 */
	// ----------------------------Notifications-----------------------------------+
	private void createNotification(Context context, PendingIntent contentIntent, CharSequence title, int icon, CharSequence tickerText, CharSequence text, long when)
	{
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		Notification notification = builder.setContentIntent(contentIntent)
				.setSmallIcon(icon).setTicker(tickerText).setWhen(when)
				.setAutoCancel(true).setContentTitle(title)
				.setContentText(text).build();
		mNotificationManager.notify(DOWNLOAD_NOTIF_ID, notification);
	}

	public void fireCompleteNotification() {

		int icon = R.drawable.icon;
		CharSequence tickerText = "MSH Download Complete";
		long when = System.currentTimeMillis();

		Context context = getApplicationContext();
		CharSequence contentTitle = "MSH";
		CharSequence contentText = "MSH Download Complete";
		//Intent notificationIntent = new Intent(this, SplashActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(), 0);
		createNotification(context, contentIntent, contentTitle, icon, tickerText, contentText, when);
	}
	
	public void fireErrorNotification(){

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.icon;
		CharSequence tickerText = "MSH Download Error";
		long when = System.currentTimeMillis();

		Context context = getApplicationContext();
		CharSequence contentTitle = "MSH Download Error";
		CharSequence contentText = "Open app for details";
		//Intent notificationIntent = new Intent(this, SplashActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(), 0);
		createNotification(context, contentIntent, contentTitle, icon, tickerText, contentText, when);
	}

	public void fireStartedNotification() {

		int icon = R.drawable.icon;
		CharSequence tickerText = "MSH Download Started";
		long when = System.currentTimeMillis();

		Context context = getApplicationContext();
		CharSequence contentTitle = "MSH Download Started";
		CharSequence contentText = "You will be notified upon completion";
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(), 0);
		createNotification(context, contentIntent, contentTitle, icon, tickerText, contentText, when);
	}

	// ---------------------------------------------------------------+
}
