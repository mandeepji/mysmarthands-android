package com.common_lib.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.common_lib.R;
import com.common_lib.android.networking.NetworkHelper;
import com.common_lib.android.networking.NetworkHelper.WifiDownloadDialogDelegate;

public abstract class SplashDownloaderActivity extends Activity implements
		WifiDownloadDialogDelegate {

	DownloaderDelegate delegate;
	private Handler handler;

	private ProgressBar progressBar;
	private TextView progressTV;
	private int progressStatus;
	
	Thread progressThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setXMLContentView(R.layout.splash_downloader_activity);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressTV = (TextView) findViewById(R.id.progressTV);
		handler = new Handler();

		delegate = getDelegate();
		// checkContent();
	}
	
	protected void setXMLContentView(int defaultResID){
		
		setContentView(defaultResID);
	}

	@Override
	protected void onResume() {

		checkContent();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
	
		stopProgressTracking();
		super.onPause();
	}

	public void checkContent() {

		if(delegate.isDownloadInProgress()){
			startProgressTracking(delegate);
			return;
		}
			
		if (!delegate.isContentValid()) {
			NetworkHelper.downloadOverWifiDialogs(this, this);
		}
		else{
			startApp();
		}
	}
	
	public void startApp(){
		
		startActivity(getOpeningIntent());
		this.finish();
	}

	public void startProgressTracking(final DownloaderDelegate delegate) {

		progressStatus = 0;

		if (delegate.useProgressFeedback()) {
			progressThread = new Thread(new Runnable() {
				@Override
				public void run() {
					
					while (progressStatus < 100) {
						// Update the progress bar
						progressStatus = delegate.getProgressFeedback();
						handler.post(new Runnable() {
							public void run() {
								progressBar.setProgress(progressStatus);
								progressTV.setText("Downloading: "+progressStatus+"%");
							}
						});
						try {
							Thread.sleep(500);
						} catch (Exception e) {
							
						}
					}
				}
			});
			
			progressThread.start();
		}
		else{
			progressBar.setIndeterminate(true);
			progressTV.setText("Downloading...");
		}
	}
	
	public void stopProgressTracking(){
		
		progressStatus = 100;
		progressThread = null;
	}

	public void downloadDidEnd() {

		stopProgressTracking();
		startApp();
	}

	// ----------------------------------------------+
	// abstract methods
	public abstract DownloaderDelegate getDelegate();

	public abstract Intent getOpeningIntent();

	// ----------------------------------------------+
	// WifiDownloadDialogDelegate methods
	@Override
	public void downloadNowSelected() {

		startProgressTracking(delegate);
		delegate.startDownload(this);
	}

	@Override
	public void downloadLaterSelected() {

		// close app
		this.finish();
	}

	// ----------------------------------------------+
	// delegate interface
	public interface DownloaderDelegate {
		
		public boolean isContentValid();

		public boolean isDownloadInProgress();
		
		public boolean useProgressFeedback();

		public int getProgressFeedback();

		public void startDownload(SplashDownloaderActivity activity);
	}

	// ----------------------------------------------+
}
