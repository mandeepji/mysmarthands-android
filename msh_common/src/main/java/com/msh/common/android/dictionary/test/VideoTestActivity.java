package com.msh.common.android.dictionary.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.android.vending.expansion.zipfile.ZipResourceFile.ZipEntryRO;
import com.common_lib.android.Logging.Logger;
import com.common_lib.android.apkexpansion.XAPKHelper;
import com.msh.common.android.dictionary.Constants;
import com.msh.common.android.dictionary.AppInstance;
import com.msh.common.android.dictionary.R;
import com.msh.common.android.dictionary.database.MSHDatabaseAdapter;
import com.common_lib.io.GeneralFileParser;
import android.app.Activity;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.VideoView;


public class VideoTestActivity extends Activity implements
		OnCompletionListener, OnErrorListener, OnPreparedListener {

	TextView tv;
	VideoView vv;
	Thread t;

	boolean testExistenceOnly = true;
	boolean playFullFiles = false;
	boolean testQuizVideos = true;
	List<String> testFiles;
	List<String> failed;

	// test state
	int testIndex;
	String currentFile;
	boolean currentIsQuiz;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.z_video_test_layout);
		
		tv = (TextView) findViewById(R.id.textView);
		
		// test files setup
		Cursor c = MSHDatabaseAdapter.getInstance().allVideoNames();
		testFiles = this.cursorToList(c);
		failed = new ArrayList<String>();
		
		//existence testing only
		if(testExistenceOnly){
			testExistence();
		}
		// videoPlayback testing
		else{
			// video view setup
			vv = (VideoView) findViewById(R.id.videoView);
			vv.setOnCompletionListener(this);
			vv.setOnErrorListener(this);
			vv.setOnPreparedListener(this);

			// test state setup
			testIndex = -1; // so that it starts at 0 on testNext() call
			currentIsQuiz = testQuizVideos; // so that it starts false on testNext() call

			// start
			this.testNext();
		}
	}

	private void testExistence() {
		
		XAPKHelper helper = new XAPKHelper(
				this.getApplicationContext(),
				Constants.getInteger(Constants.CONST_KEY_XAPK_MAIN_VERSION), 0);
		
		List<String> files = zipEntriesToNameList(helper.getZipEntries());
//		Logger.log(files);
		for (String videoName : testFiles) {
			videoName = AppInstance.applyVideoNamePathFilters(videoName);
			// test full_video path
			if( !files.contains(videoName+Constants.getString(Constants.CONST_KEY_VIDEO_FORMAT_EXT)) ){
				failed.add(videoName);
			}
			
			if(testQuizVideos){
				videoName += "_quiz";
				if( !files.contains(videoName+Constants.getString(Constants.CONST_KEY_VIDEO_FORMAT_EXT)) ){
					failed.add(videoName);
				}
			}
		}
		
		tv.setText(failed.size()+" error(s) found");
		for (String fail : failed) {
			Logger.log(fail);
		}
		Logger.log("Existence test complete: "+failed.size()+" error(s) found");
		Logger.log(testFiles.size()+" tested");
	}

	public void testNext() {

		int count = testFiles.size();
		if (++testIndex >= count) {
			this.testFinished();
			return;
		}
		
		if(testQuizVideos){
			currentIsQuiz = !currentIsQuiz;
		}
		
		currentFile = testFiles.get(testIndex);
		

		try {
			String quizStr = (currentIsQuiz)
								? "(Quiz)"
								: "";
			tv.setText(String.format("%d of %d - %s %s", testIndex + 1,
					count, currentFile,quizStr));
			AppInstance.playVideo(vv, currentFile, currentIsQuiz);
		} catch (Exception e) {
			e.printStackTrace();
			this.onError(null, 999, 999);
		}
	}

	public void testFinished() {

		tv.setText(String.format("Test Complete %d error(s) found",
				failed.size()));

		String results = (failed.isEmpty()) ? "NO ERRORS"
				: fileListToString(failed);

		// write to file test results
		File extern = Environment.getExternalStorageDirectory();
		String path = extern.getAbsolutePath() + "aa_mshTest.txt";
		GeneralFileParser.stringToFile(path, results);
	}

	// -------------------------------------------------------------+
	// video view play back loop
	@Override
	public void onPrepared(MediaPlayer mp) {

		Logger.log("prepared");
		vv.start();
		if (!playFullFiles) {
			t = new Thread(getTimer(vv));
			t.start();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {

		Logger.log("complete");
		this.testNext();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {

		Logger.log("ERROR: w" + what + "  e" + extra);

		failed.add(currentFile);

		// TRUE = onComplete is not called
		onCompletion(null);
		return true;
	}

	// -------------------------------------------------------------+
	// utility methods
	public List<String> cursorToList(Cursor c) {

		List<String> ret = new ArrayList<String>();

		if(testQuizVideos && !testExistenceOnly){
			c.moveToFirst();
			String name;
			while (!c.isAfterLast()) {
				name = c.getString(0);
				ret.add(name);
				ret.add(name); // add twice for quiz val
				c.moveToNext();
			}
		}
		else{
			c.moveToFirst();
			while (!c.isAfterLast()) {
				ret.add(c.getString(0));
				c.moveToNext();
			}
		}

		return ret;
	}

	public String fileListToString(List<String> files) {

		String ret = "";
		for (String file : files) {
			ret += file + "\n";
		}

		return ret;
	}

	private Runnable getTimer(final VideoView videoView){
		
		return new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(500);
					videoView.stopPlayback();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							onCompletion(null);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	public List<String> zipEntriesToNameList(ZipEntryRO[] entries){
		
		List<String> ret = new ArrayList<String>( entries.length );
		for (ZipEntryRO zip : entries) {
			ret.add( zip.mFileName );
		}
		
		return ret;
	}
	
	// -------------------------------------------------------------+
}
