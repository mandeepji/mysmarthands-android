package com.msh.common.android.dictionary.view;

import com.google.android.gms.ads.AdView;
import com.msh.common.android.dictionary.Constants;
import com.msh.common.android.dictionary.AppInstance;
import com.msh.common.android.dictionary.R;
import com.msh.common.android.dictionary.database.MSHDatabaseAdapter;
import com.msh.common.android.dictionary.AdMobHelper;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.widget.VideoView;


public class PlayActivity extends Activity implements OnPreparedListener,
		OnCompletionListener {

	VideoView videoView;
	Cursor cursor;
	AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		String cat = this.getIntent().getExtras()
				.getString(getString(R.string.quizSetKEY));

		videoView = (VideoView) findViewById(R.id.videoView1);
		// videoView.setMediaController( new MediaController(this) );
		videoView.setMediaController(null);
		videoView.setOnPreparedListener(this);
		videoView.setOnCompletionListener(this);

		cursor = MSHDatabaseAdapter.getInstance().videoNamesForCategory(cat);

		// Loads another video from the cursor
		this.onCompletion(null);
		
		// AdView
		if(AppInstance.shouldShowAds(Constants.getString(Constants.CONST_KEY_ADMOB_HOME_ADID))){
			adView = AdMobHelper.startGenericAdView(
					this,
					Constants.getString(Constants.CONST_KEY_ADMOB_HOME_ADID),
					R.id.adFrame,
					true);
		}

	}

	protected void onPause() {
		
		if(adView !=null){
			adView.pause();
		}
		
		super.onPause();
	}
	
	@Override
	protected void onResume() {

		super.onResume();
		if(adView !=null){
			adView.resume();
		}
	}
	
	@Override
	protected void onDestroy() {

		cursor.close();
		videoView.stopPlayback();
		
		if(adView !=null){
			adView.destroy();
		}
		
		super.onDestroy();
	}

	public void onCompletion(MediaPlayer mp) {

		videoView.stopPlayback();
		if (cursor.isLast()) {
			this.finish();
			return;
		}

		if (mp == null)
			cursor.moveToFirst();
		else
			cursor.moveToNext();

		// triggers on prepare
		AppInstance.playVideo(videoView, cursor.getString(0), false);
		// old
		// String path = LearnActivity.nameToPath(cursor.getString(0));
		// videoView.setVideoPath(path);

	}

	public void onPrepared(MediaPlayer mp) {

		videoView.start(); // triggers on completion
	}
}
