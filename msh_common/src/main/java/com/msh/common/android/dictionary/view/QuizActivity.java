package com.msh.common.android.dictionary.view;

import com.msh.common.android.dictionary.AppInstance;
import com.msh.common.android.dictionary.QuestionSetAdapter;
import com.msh.common.android.dictionary.QuizSet;
import com.msh.common.android.dictionary.R;
import com.msh.common.android.dictionary.database.MSHDatabaseAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.VideoView;


public class QuizActivity extends Activity implements OnItemClickListener,
		OnCompletionListener, OnPreparedListener, OnErrorListener, Runnable,
		OnTouchListener {

	public final int ANIMATION_DURATION = 1000;
	public final int PLAY_TIME_OFFSET = 0;

	TitleBarView titleBarView;
	VideoView videoView;
	ListView listView;
	Handler handler;
	View replayView;

	QuizSet quizSet;
	QuestionSetAdapter qsa;

	MSHDatabaseAdapter dbAdapter;
	int endTime = -1;

	boolean playPartial = false;
	Thread t = null;

	@Override
	protected void onCreate(Bundle bundle) {

		super.onCreate(bundle);
		setContentView(R.layout.quiz_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		handler = new Handler();

		quizSet = (QuizSet) this.getIntent().getExtras()
				.getSerializable(getString(R.string.quizSetKEY));

		videoView = (VideoView) findViewById(R.id.quizVideoView);
		listView = (ListView) findViewById(R.id.quizListView);

		titleBarView = (TitleBarView) findViewById(R.id.titleBarView);
		titleBarView.setHiddenLeftButton(true);
		titleBarView.setHiddenRightButton(true);

		videoView.setOnPreparedListener(this);
		videoView.setOnCompletionListener(this);
		videoView.setOnTouchListener(this);
		videoView.setClickable(true);
		videoView.setMediaController(null);
		videoView.setOnErrorListener(this);

		listView.setOnItemClickListener(this);

		View parent = (View) listView.getParent();
		int height = parent.getHeight();
		videoView.layout(0, 0, videoView.getRight(), height / 2);
		listView.layout(0, height / 2, listView.getRight(), height);

		dbAdapter = MSHDatabaseAdapter.getInstance();
//		playPartial = !dbAdapter.isQuizVidsSeperate();

		replayView = (View) findViewById(R.id.replay);
//		replayView.setVisibility(View.INVISIBLE);

		setupQuestion();
	}

	@Override
	protected void onResume() {

		super.onResume();

		if (dbAdapter == null)
			dbAdapter = MSHDatabaseAdapter.getInstance();
	}

	private void updateTitle() {

		titleBarView.setTitle("Question:" + quizSet.currentQuestion() + "/"
				+ quizSet.size());
	}

	@Override
	protected void onDestroy() {

		this.stopPlayback();
		super.onDestroy();
	}

	public void moveToResults() {

		Intent intent = new Intent();
		intent.setClass(this, ResultsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(getString(R.string.quizSetKEY), quizSet);
		intent.putExtras(bundle);
		startActivity(intent);

		this.finish();
	}

	private void setupQuestion() {

		if (!quizSet.hasNext()) {
			this.moveToResults();
			return;
		}

		this.updateTitle();
		qsa = new QuestionSetAdapter(this, quizSet.nextQuestion());

		listView.setAdapter(qsa);
		// listView.setEnabled(false);
		// reset startime
		endTime = -1;
		this.configureAndPlay();
	}

	private void configureAndPlay() {

//		Logger.log("configureAndPlay");
				
		String name = quizSet.currentAnswer();

		// seperateQuiz
		if (!playPartial) {
			if (endTime < 0) {
				listView.setEnabled(false);
				endTime = 1;
				AppInstance.playVideo(videoView, name, true);
				// old
				// path = LearnActivity.nameToQuizPath(name);

			} else
				this.onPrepared(null);
		}

		// non_seperateQuiz -- not used anymore
		else {
			if (endTime < 0) {
				listView.setEnabled(false);
				endTime = dbAdapter.quizStartTime(name) + PLAY_TIME_OFFSET;
				AppInstance.playVideo(videoView, name, true);
			} else
				this.onPrepared(null);
		}
	}

	public void onItemClick(AdapterView<?> arg0, View row, int answered,
			long arg3) {

		listView.setEnabled(false);
		qsa = new QuestionSetAdapter(this, qsa.qSet, answered);
		quizSet.answerCurrent(answered);
		listView.setAdapter(qsa);

		this.animateSelection();

		// listView.setEnabled(true);

		// this.stopPlayback();
	}

	private void animateSelection() {

		try {
			Thread n = new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(ANIMATION_DURATION);
						handler.post(new Runnable() {
							public void run() {
								QuizActivity.this.stopPlayback();
								setupQuestion();
							}
						});
					} catch (Exception e) {
					}
				}
			});
			n.start();
		} catch (Exception e) {
		}
	}

	public void onPrepared(MediaPlayer mp) {

		replayView.setVisibility(View.INVISIBLE);
//		videoView.seekTo(0);
		videoView.start();

		if (playPartial) {
			t = new Thread(this);
			t.start();
		}

		// Re-enabled after video is ready to play
		listView.setEnabled(true);
	}

	public void onCompletion(MediaPlayer player) {

//		Logger.log("onCompletion");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				replayView.setVisibility(View.VISIBLE);
			}
		});
	}

	public void run() {

		int pos;

		try {
			while (videoView.isPlaying()) {
				pos = videoView.getCurrentPosition();
				if (pos >= endTime) {
					handler.post(new Runnable() {
						public void run() {
							replayView.setVisibility(View.VISIBLE);
						}
					});
					videoView.pause();
				} else
					Thread.sleep(endTime - pos);
			}
		} catch (Exception e) {
		}
	}

	private void stopPlayback() {

		videoView.stopPlayback();
		try {
			if (t != null)
				t.join();
		} catch (Exception e) {
		}
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {

		return false;
	}

	public boolean onTouch(View v, MotionEvent event) {

		if (!videoView.isPlaying())
			this.configureAndPlay();

		return false;
	}
}
