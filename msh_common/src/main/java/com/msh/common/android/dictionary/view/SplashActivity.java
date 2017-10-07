package com.msh.common.android.dictionary.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import com.msh.common.android.dictionary.R;
import com.msh.common.android.dictionary.database.MSHDatabaseAdapter;
import com.msh.common.android.dictionary.services.DownloadIntentService;
import com.msh.common.android.dictionary.services.DownloadIntentService.Directory;
import com.common_lib.android.networking.HttpConnection;
import com.common_lib.android.networking.NetworkHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends Activity {

	Handler handler;

	// public final int TIMEOUT = 50000000;
	public final int SLEEP_MILISEC = 800;

	public TextView info;
	public View buttons;
	public Button fullBtn;
	public ProgressBar progressBar;
	public TextView pBarText;

	private static boolean closeDB;
	private static boolean kill;
	private static boolean error = false;

	public static final boolean LOCAL_TESTING = false;
	public static final boolean LOCAL_TESTING_All = false;
	private static int DOWNLOAD_SIZE;

	public static Directory APP_STORAGE_DIRECTORY;
	MSHDatabaseAdapter db;
	private static Thread t = null;

	// setup download directories and corresponding read directories
	// fix db issue by closing/reopening onpause/onresume

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		this.setContentView(R.layout.splash);

		info = (TextView) this.findViewById(R.id.infoText);

		buttons = (View) this.findViewById(R.id.buttons);
		buttons.setVisibility(View.INVISIBLE);

		fullBtn = (Button) this.findViewById(R.id.fullBtn);
		fullBtn.setVisibility(View.INVISIBLE);

		progressBar = (ProgressBar) this.findViewById(R.id.pBar);
		pBarText = (TextView) this.findViewById(R.id.pBarText);
		// updateProgressBar(0,1);
		progressBar.setVisibility(View.GONE);
		pBarText.setVisibility(View.GONE);

		db = MSHDatabaseAdapter.getInstance();
		db.checkAndCopyDB(SQLiteDatabase.OPEN_READWRITE);

		closeDB = true;
		kill = false;

		if (LOCAL_TESTING || LOCAL_TESTING_All) {
			startApp();
			return;
		}

		handler = new Handler();

		if (!DownloadIntentService.isRunning())
			checkContent();

		// Download Running
		else {
			info.setText(getString(R.string.downloadMsg));
			progressBar.setVisibility(View.VISIBLE);
			pBarText.setVisibility(View.VISIBLE);
			load();
		}
	}

	@Override
	protected void onDestroy() {

		if (t != null) {
			kill = true;
			t = null;
		}

		if (closeDB)
			db.close();

		super.onDestroy();
	}

	// --------------------CONTENT_MANAGEMENT-----------------------+
	private void checkContent() {

		SplashActivity.DOWNLOAD_SIZE = this.getDownloadSize();

		// Database pushed update
		if (db.videoUpdateRequiredOnAppUpdated()) {
			this.deleteVideoFiles();
			this.deleteChecks();
		}

		// ERONEOUS CONTENT
		if (serviceCheck(DownloadIntentService.SERVICE_INCOMPLETE_FILENAME)) {
			showIncompleteWarning();
			return;
		}

		// NO CONTENT
		else if (!serviceCheck(DownloadIntentService.SERVICE_CHECK_FILENAME)) {

			showDownloadWarning();
		}

		// UPDATE CONTENT
		else if (NetworkHelper.isNetworkAvailable(this) && !vidVersionCheck()) {
			showUpdateWarning();
			return;
		}

		this.load();
	}

	public void load() {

		t = new Thread(new Runnable() {

			public void run() {
				// long endTime = System.currentTimeMillis() + TIMEOUT;

				while (true) {
					synchronized (this) {
						// Log.d("RBI","LOAD RUNNING");
						try {
							if (kill) {
								kill = false;
								break;
							}
							if (serviceCheck(DownloadIntentService.SERVICE_CHECK_FILENAME)
									&& db.isOpen()) {
								handler.post(new Runnable() {
									public void run() {
										SplashActivity.this.startApp();
									}
								});
								break;
							}

							if (!error
									&& serviceCheck(DownloadIntentService.SERVICE_INCOMPLETE_FILENAME)) {
								handler.post(new Runnable() {
									public void run() {
										SplashActivity.this
												.showIncompleteWarning();
									}
								});
								break;
							}

							else {
								final int[] prg = SplashActivity.this
										.progressCheck();
								if (prg != null) {
									handler.post(new Runnable() {
										public void run() {
											SplashActivity.this
													.updateProgressBar(prg[0],
															prg[1]);
										}
									});
								}
							}
							wait(SLEEP_MILISEC);

						} catch (Exception e) {
						}
					}
				}
			}
		});
		t.start();
	}

	public void startApp() {

		APP_STORAGE_DIRECTORY = Directory.valueOf(db.getStorageDirValue());
		closeDB = false;
		info.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		pBarText.setVisibility(View.GONE);

		if (db.isFullEdition())
			fullBtn.setVisibility(View.GONE);
		else
			fullBtn.setVisibility(View.VISIBLE);
		buttons.setVisibility(View.VISIBLE);
	}

	private void updateProgressBar(int prog, int count) {

		progressBar.setProgress(prog);
		progressBar.setMax(count);
		pBarText.setText(prog + "/" + count);
	}

	private boolean serviceCheck(String name) {

		File file = getFileStreamPath(name);
		return file.exists();
	}

	private int[] progressCheck() {

		try {
			File prgrsFile = getFileStreamPath(DownloadIntentService.SERVICE_PROGRESS_FILENAME);
			BufferedReader reader = new BufferedReader(
					new FileReader(prgrsFile));
			String line = reader.readLine();
			String[] nums = line
					.split(DownloadIntentService.SERVICE_PROGRESS_DIV);
			int[] ret = { Integer.valueOf(nums[0]), Integer.valueOf(nums[1]) };
			return ret;

		} catch (Exception e) {
			return null;
		}
	}

	private void deleteChecks() {

		deleteFile(DownloadIntentService.SERVICE_CHECK_FILENAME);
		deleteFile(DownloadIntentService.SERVICE_INCOMPLETE_FILENAME);
	}

	public ArrayList<String> convertWords() {

		MSHDatabaseAdapter db = MSHDatabaseAdapter.getInstance();
		Cursor cursor = db.allVideoNames();
		// Convert Cursor to list
		ArrayList<String> words = new ArrayList<String>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			words.add(cursor.getString(0));
			cursor.moveToNext();
		}
		cursor.close();

		return words;
	}

	public double getStorageAvailable(String environmentPath) {

		try {
			// StatFs stat = new
			// StatFs(Environment.getExternalStorageDirectory().getPath());
			StatFs stat = new StatFs(environmentPath);
			double sdAvailSize = (double) stat.getAvailableBlocks()
					* (double) stat.getBlockSize();
			// One binary gigabyte equals 1,073,741,824 bytes.
			// double gbAvilable = sdAvailSize / 1073741824; //1024^3
			double mbAvailable = sdAvailSize / 1048576; // 1024^2
			// double kbAvailable = sdAvailSize / 1024;

			return mbAvailable;
		} catch (Exception e) {
			return 0;
		}
	}

	// --------------------------FILE MANAGEMENT----------------------------+
	public void deleteVideoFiles() {

		// EXTERNAL DELETION
		String packageName = this.getApplicationContext().getPackageName();
		File externalPath = Environment.getExternalStorageDirectory();
		File extenalVidFolder = new File(externalPath.getAbsolutePath()
				+ "/Android/data/" + packageName + "/videos");
		extenalVidFolder.delete();

		// INTERNAL DELETION
		String[] files = fileList();
		for (String name : files) {
			if (name.endsWith(MSHDatabaseAdapter.VID_EXT))
				deleteFile(name);
		}
	}

	// --------------------------------Networking------------------------------------+
	private int getDownloadSize() {

		String valueStr = null;
		String url = null;
		try {
			// URL of VERSION CHECK SCRIPT
			url = HttpConnection.getHttpRequestString(db.getVideoURL())
					+ DownloadIntentService.SERVER_SCRIPT_DOWNLSIZE;

			valueStr = HttpConnection.getHttpRequestString(url);
			return Integer.valueOf(valueStr);
		} catch (Exception e) {
			Log.e("RBI", "df", e);
			Log.d("RBI","url: "+valueStr);
			Log.d("RBI","valueStr: "+valueStr);
			return 0;
		}

	}

	private boolean vidVersionCheck() {

		try {
			// Server Pushed update
			// --

			// URL of VERSION CHECK SCRIPT
			String serverVersion = HttpConnection.getHttpRequestString(db.getVideoURL())
					+ DownloadIntentService.SERVER_SCRIPT_VIDEOUPDATE;

			// SERVER VERSION
			serverVersion = HttpConnection.getHttpRequestString(serverVersion);

			Log.d("RBI", serverVersion);
			return db.isVersionValid(serverVersion);
		}
		catch(IOException e)
		{
			Log.e("RBI", e.getMessage());
			return false;
		}
	}

	private void updateVideoVersion() {

        try {
            // URL of VERSION CHECK SCRIPT
            String serverVersion = HttpConnection.getHttpRequestString(db.getVideoURL())
                    + DownloadIntentService.SERVER_SCRIPT_VIDEOUPDATE;

            // SERVER VERSION
            serverVersion = HttpConnection.getHttpRequestString(serverVersion);

            db.updateVersion(serverVersion);
        }
        catch (IOException e)
        {
            Log.e("RBI", e.getMessage());
        }
	}

	private void startDownload(boolean err, boolean checkWifi) {

		if (checkWifi && !NetworkHelper.isWifiConnected(this)) {
			showNetworkWarning(err);
			return;
		}

		deleteChecks();
		updateVideoVersion();

		error = false;
		progressBar.setVisibility(View.VISIBLE);
		pBarText.setVisibility(View.VISIBLE);

		// if(!err)
		// this.deleteVideoFiles();

		info.setText(getString(R.string.downloadMsg));
		Intent intent = new Intent(this, DownloadIntentService.class);
		ArrayList<String> words = this.convertWords();
		Bundle bundle = new Bundle();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		bundle.putInt(getString(R.string.resHeight),
				displaymetrics.heightPixels);

		bundle.putStringArrayList(getString(R.string.quizSetKEY), words);
		bundle.putString(getString(R.string.urlKEY), db.getVideoURL());
		bundle.putBoolean(getString(R.string.serviceErrKey), err);
		bundle.putString(getString(R.string.storageKey),
				APP_STORAGE_DIRECTORY.toString());
		bundle.putBoolean(getString(R.string.seperateQuizKey), db.isQuizVidsSeperate());
		intent.putExtras(bundle);

		MSHDatabaseAdapter.getInstance().updateStorageDirValue(
				APP_STORAGE_DIRECTORY.toString());

		startService(intent);
	}

	// --------------------------------BUTTONS----------------------------------------+
	public void babyPressed(View v) {

		Intent i = new Intent(this, LearnActivity.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean(getString(R.string.quizSetKEY), true); // change key
		i.putExtras(bundle);
		startActivity(i);
		this.finish();
	}

	public void aslPressed(View v) {

		Intent i = new Intent(this, LearnActivity.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean(getString(R.string.quizSetKEY), false); // change key
		i.putExtras(bundle);
		startActivity(i);
		this.finish();
	}

	public void getFullPressed(View v) {

		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(db
				.getFullURL()));
		// Intent browserIntent = new Intent(Intent.ACTION_VIEW,
		// Uri.parse("market://details?id=com.wolinlabs.SuperScorepad") );
		startActivity(browserIntent);
	}

	// --------------------------------WARNINGS----------------------------------------+

	private void showDownloadWarning() {

		int internal = (int) getStorageAvailable(Environment.getDataDirectory()
				.getPath());
		int external = (int) getStorageAvailable(Environment
				.getExternalStorageDirectory().getPath());

		int contentSize = SplashActivity.DOWNLOAD_SIZE; // temp
//Log.d("RBI", "contentSize: "+contentSize);
		if (contentSize == 0) {
			this.showNoConnectionWarning();
			return;
		}

		String replace = (contentSize == 0) ? "Unkown (NO CONNECTION)" : String
				.valueOf(contentSize) + " MB";

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		// builder.setIcon(R.drawable.icon);
		builder.setTitle("Content Download");
		builder.setPositiveButton("Internal:\n" + internal + " MB",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						SplashActivity.APP_STORAGE_DIRECTORY = Directory.INTERNAL_DIRECTORY;
						SplashActivity.this.startDownload(false, true);
						dialog.cancel();
					}
				});

		builder.setNeutralButton("External:\n" + external + " MB",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						SplashActivity.APP_STORAGE_DIRECTORY = Directory.EXTERNAL_DIRECTORY;
						SplashActivity.this.startDownload(false, true);
						dialog.cancel();
					}
				});

		builder.setNegativeButton("Not Now",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						dialog.cancel();
						SplashActivity.this.finish();
					}
				});

		int dirChecks = 0;
		// Internal Check
		if (internal <= contentSize)
			dirChecks += 1;

		// External Check
		String state = Environment.getExternalStorageState();
		if (external <= contentSize && Environment.MEDIA_MOUNTED.equals(state))
			dirChecks += 2;

		String temp;
		if (dirChecks < 3) {
			temp = getString(R.string.downloadWarning);
			builder.setMessage(temp.replace("<SIZE> MB", replace));
		} else {
			temp = getString(R.string.insufficientSpace);
			builder.setMessage(temp.replace("<SIZE> MB", replace));
		}

		AlertDialog alert = builder.create();
		alert.show();

		// Disable buttons after show due to bug
		switch (dirChecks) {
		case 0:
			break;
		case 1:
			alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			break;
		case 2:
			alert.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
			break;
		default:
			alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			alert.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
		}

	}

	private void showNoConnectionWarning() {

		int contentSize = SplashActivity.DOWNLOAD_SIZE; // temp
		String replace = (contentSize == 0) ? "Unkown (NO CONNECTION)" : String
				.valueOf(contentSize) + " MB";

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				getString(R.string.noNetwork).replace("<SIZE> MB", replace))
				.setCancelable(false)
				.setPositiveButton("Close",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								SplashActivity.this.finish();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showNetworkWarning(final boolean err) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		int contentSize = SplashActivity.DOWNLOAD_SIZE; // temp
		String replace = (contentSize == 0) ? "Unkown (NO CONNECTION)" : String
				.valueOf(contentSize) + " MB";

		builder.setMessage(
				getString(R.string.wifiWarning).replace("<SIZE> MB", replace))
				.setCancelable(false)
				.setPositiveButton("Close",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								SplashActivity.this.finish();
							}
						})
				.setNegativeButton("Start",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								if (!NetworkHelper.isNetworkAvailable(SplashActivity.this))
									SplashActivity.this
											.showNoConnectionWarning();
								else
									SplashActivity.this.startDownload(err,
											false);
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showUpdateWarning() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.updateWarning))
				.setCancelable(false)
				.setPositiveButton("Remind Me Later",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								// SplashActivity.this.load();
								SplashActivity.this.startApp();
							}
						})
				.setNegativeButton("Start Now",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								SplashActivity.this.startDownload(false, true);
								SplashActivity.this.load();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showIncompleteWarning() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.incompleteWarning))
				.setCancelable(false)
				.setPositiveButton("Do it later",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								SplashActivity.this.finish();
							}
						})
				.setNegativeButton("Start Now",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								APP_STORAGE_DIRECTORY = Directory.valueOf(db
										.getStorageDirValue());
								SplashActivity.error = true;
								SplashActivity.this.startDownload(true, true);
								SplashActivity.this.load();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
