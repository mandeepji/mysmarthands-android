package com.common_lib.android.services.download;

import android.annotation.SuppressLint;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map.Entry;


public class DownloadTask implements Runnable {

	public static final int BYTES_BUFFER_SIZE 	= 32 * 1024;
	
	public static final int RESULT_WAITING 		= -1;
	public static final int RESULT_COMPLETE 		= 0;
	public static final int RESULT_FAILED 		= 1;

	private int attempts;
	private int result;
	private Exception downloadException = null;
	private String filename, url;

	public DownloadTask(String filename, String url) {

		result = RESULT_WAITING;
		attempts = 0;
		this.filename = filename;
		this.url = url;
	}

	public DownloadTask(Entry<String, String> filenameAndURL) {

		result = RESULT_WAITING;
		attempts = 0;
		this.filename = filenameAndURL.getKey();
		this.url = filenameAndURL.getValue();
	}

	public void run() {

		downloadFile(url, filename);

	}

	// -------------------------------------------------------+
	// downloading
	protected void downloadFile(String urlStr, String fileName) {

//		Logger.log("url:"+urlStr);
//		Logger.log("fn:"+fileName);
		
		result = RESULT_WAITING;
		attempts++;
		
		File output = new File(fileName);
		if (!isDirectory(output)) {
			File fileDir = output.getParentFile();
			if(!fileDir.exists()){
//				Logger.log( fileDir.mkdirs() );
				fileDir.mkdirs();
			}
		} 
		else {
			throw new RuntimeException(output.getAbsolutePath()
					+ " is a directory (should be a file).");
		}

		try {
			URL url = urlify(urlStr);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(1000);
			connection.setReadTimeout(1000);

			BufferedInputStream bis = new BufferedInputStream(
					connection.getInputStream());
			FileOutputStream fos = new FileOutputStream(fileName);
			int bytesRead, totalBytesRead = 0;
			byte[] bytes = new byte[BYTES_BUFFER_SIZE];

			// String progress, kbytes;
			// kbytes = String.format("%s / %s",
			// getStringByteSize(totalBytesRead),
			// getStringByteSize(filesize));

			while ((bytesRead = bis.read(bytes)) != -1) {
				totalBytesRead += bytesRead;
				fos.write(bytes, 0, bytesRead);
			}

			if (fos != null) {
				fos.close();
			}
			if (bis != null) {
				bis.close();
			}
			// Successful finished
			result = RESULT_COMPLETE;
		} catch (Exception e) {
//			Logger.log(e);
			downloadException = e;
			result = RESULT_FAILED;
		}
	}

	// -------------------------------------------------------+
	// getters
	public String getFileName() {

		return filename;
	}

	public String getURL() {

		return url;
	}

	public int getResult() {

		return result;
	}

	public Exception getException(){
		
		return downloadException;
	}
	
	public int getAttempts(){
		
		return attempts;
	}
	
	// -------------------------------------------------------+
	// utility methods
	public static URL urlify(String urlStr) throws URISyntaxException,
			MalformedURLException {

		URL url = new URL(urlStr);
		URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(),
				url.getPort(), url.getPath(), url.getQuery(), url.getRef());

		return uri.toURL();
	}

	public static boolean isDirectory(File test) {

		// check if the file/directory is already there
		if (!test.exists()) {
			// check for extension extension
			return test.getName().lastIndexOf('.') == -1;
		} else {
			// see if the path that's already in place is a file or directory
			return test.isDirectory();
		}
	}

	public static int getFileSizeAtURL(URL url) {

		int filesize = -1;
		try {
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			filesize = http.getContentLength();
			http.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filesize;
	}

	@SuppressLint("DefaultLocale")
	public static String getStringByteSize(int size) {

		if (size > 1024 * 1024) { // mega
			return String.format("%.1f MB", size / (float) (1024 * 1024));
		} else if (size > 1024) { // kilo
			return String.format("%.1f KB", size / 1024.0f);
		} else {
			return String.format("%d B");
		}
	}
	
	// -------------------------------------------------------+
	// reporting methods
//	protected void showNotification(String ticker, String title, String content) {
//
//		Notification notification = new Notification(getNotificationIcon(),
//				ticker, System.currentTimeMillis());
//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//				new Intent(this, getIntentForLatestInfo()),
//				Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		notification.setLatestEventInfo(getApplicationContext(), title,
//				content, contentIntent);
//		notification.flags = getNotificationFlag();
//
//		notificationManager.notify(SERVICE_ID, notification);
//	}

	// -------------------------------------------------------+
}