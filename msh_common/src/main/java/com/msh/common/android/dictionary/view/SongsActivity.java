package com.msh.common.android.dictionary.view;

import com.msh.common.android.dictionary.Constants;
import com.msh.common.android.dictionary.AppInstance;
import com.msh.common.android.dictionary.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SongsActivity extends Activity {

	ViewGroup videoBtns;
	TextView msgView;
	TextView codeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_layout);

		videoBtns = (ViewGroup) findViewById(R.id.videoBtns);
		msgView = (TextView) findViewById(R.id.msgView);
		codeView = (TextView) findViewById(R.id.codeText);

		boolean accessible = (!Constants.getBoolean(Constants.CONST_KEY_IAP_USES_IAP) || AppInstance
				.fullVersionPurchased());

		setAccessible(accessible);
	}

	public void setAccessible(boolean accessible) {

		int visibility = (accessible) ? View.VISIBLE : View.INVISIBLE;
		String msg = (accessible) ? "Download the MP3s at mysmarthands.com/abc"
				: "This content can be unlocked in the 'Upgrades' section";

		videoBtns.setVisibility(visibility);
		codeView.setVisibility(visibility);

		msgView.setText(msg);
	}

	public void playAlphabet(View view) {
		
		Intent i = new Intent(this, VideoPlayerActivity.class);
		i.putExtra(VideoPlayerActivity.SINGLE_VIDEO_SOURCE,"abcs");
		startActivity(i);
	}
	
	public void playPhonics(View view) {
		
		Intent i = new Intent(this, VideoPlayerActivity.class);
		i.putExtra(VideoPlayerActivity.SINGLE_VIDEO_SOURCE,"abcPhonics");
		startActivity(i);
	}
}
