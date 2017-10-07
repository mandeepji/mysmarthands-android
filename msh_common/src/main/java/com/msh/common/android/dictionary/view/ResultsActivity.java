package com.msh.common.android.dictionary.view;

import java.text.DecimalFormat;

import com.google.android.gms.ads.AdView;
import com.msh.common.android.dictionary.Constants;
import com.msh.common.android.dictionary.AppInstance;
import com.msh.common.android.dictionary.QuizSet;
import com.msh.common.android.dictionary.QuizSetAdapter;
import com.msh.common.android.dictionary.R;
import com.msh.common.android.dictionary.AdMobHelper;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.VideoView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ResultsActivity extends Activity implements OnItemClickListener,
		OnPreparedListener, OnCompletionListener {

	ListView listView;
	VideoView videoView;
	AdView adView;

	private final int TABLE_SLIDE_DURATION = 400;
	private int height = 0;
	private boolean playEnabled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		QuizSet quizSet = (QuizSet) this.getIntent().getExtras()
				.getSerializable(getString(R.string.quizSetKEY));

		// TITLE BAR
		TitleBarView titleBarView = (TitleBarView) findViewById(R.id.titleBarView);

		String res = new DecimalFormat("#.##").format(quizSet.tally() * 100);
		titleBarView.setTitle("Score: " + res + "%");
		titleBarView.setHiddenLeftButton(true);
		titleBarView.setHiddenRightButton(true);

		// LISTVIEW
		listView = (ListView) findViewById(R.id.listView);
		QuizSetAdapter adapter = new QuizSetAdapter(this, quizSet);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setEnabled(false);

		// videoView
		videoView = (VideoView) findViewById(R.id.videoView);
		videoView.setMediaController(null);
		videoView.setOnPreparedListener(this);
		videoView.setOnCompletionListener(this);

		AppInstance.playDummyVid(videoView);
		// old
		// videoView.setVideoPath(LearnActivity.firstPath());

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		height = displaymetrics.heightPixels;
		height *= 0.92;
		
		// AdView
		if(AppInstance.shouldShowAds(Constants.getString(Constants.CONST_KEY_ADMOB_HOME_ADID))){
			adView = AdMobHelper.startGenericAdView(
					this,
					Constants.getString(Constants.CONST_KEY_ADMOB_HOME_ADID),
					R.id.root,
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

		videoView.stopPlayback();
		
		if(adView !=null){
			adView.destroy();
		}
		
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {

		if (!isTableUp())
			toggleTable();
		else
			super.onBackPressed();
	}

	public boolean isTableUp() {

		return listView.getTop() == 0;
	}

	public void hideBacking(boolean hide) {

		// EMPTY TABLE CONFIG
		if (hide)
			videoView.setVisibility(View.INVISIBLE);
		else
			videoView.setVisibility(View.VISIBLE);
	}

	public void toggleTable() {

		final boolean up = !this.isTableUp();

		// if (up && videoView.isPlaying())
		// videoView.stopPlayback();

		listView.setEnabled(false);
		final View parent = (View) listView.getParent();
		final int deltaY = (!up) ? height / 2 : -(listView.getTop());

		Animation animation = new TranslateAnimation(0, 0, 0, deltaY);

		animation.setDuration(TABLE_SLIDE_DURATION);

		animation.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				if (up)
					listView.layout(listView.getLeft(), listView.getTop(),
							listView.getRight(),
							parent.getHeight() + Math.abs(deltaY));
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {

				int top = (deltaY < 0) ? 0 : deltaY;
				listView.layout(listView.getLeft(), top, listView.getRight(),
						parent.getHeight());

				listView.setEnabled(true);
				if (up)
					hideBacking(up);
			}
		});

		listView.startAnimation(animation);
	}

	public void onItemClick(AdapterView<?> arg0, View row, int index, long arg3) {

		hideBacking(false);
		if (videoView.isPlaying()) {
			videoView.stopPlayback();
			toggleTable();
		}

		playEnabled = true;
		QuizSetAdapter qsa = (QuizSetAdapter) listView.getAdapter();
		String name = qsa.name(row);
		AppInstance.playVideo(videoView, name, false);
		// videoView.setVideoPath(LearnActivity.nameToPath(name));

		listView.requestFocusFromTouch();
		listView.setSelection(index);
	}

	public void onPrepared(MediaPlayer mp) {

		if (playEnabled) {
			toggleTable();
			videoView.start();
			playEnabled = false;
		} else {
			// this.hideBacking(true);
		}

		listView.setEnabled(true);
	}

	public void onCompletion(MediaPlayer mp) {

		this.videoView.stopPlayback();
		toggleTable();
	}
}
