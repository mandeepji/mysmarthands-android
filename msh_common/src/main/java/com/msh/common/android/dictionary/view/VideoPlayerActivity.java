package com.msh.common.android.dictionary.view;

import com.msh.common.android.dictionary.AppInstance;
import com.msh.common.android.dictionary.R;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.widget.VideoView;


public class VideoPlayerActivity extends Activity implements OnCompletionListener {
	
	VideoView videoView;
	
	public static final String SINGLE_VIDEO_SOURCE = "singleVidSrc";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.video_player_layout);
		
		videoView = (VideoView) findViewById(R.id.videoView);
		videoView.setOnCompletionListener(this);
		
		String vidName = this.getIntent().getStringExtra(SINGLE_VIDEO_SOURCE);
//		Logger.log(vidName);
		if(vidName !=null){
			AppInstance.playVideo(videoView, vidName, false);
			videoView.start();
		}
		else{
			this.finish();
		}
		
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		this.finish();
	}
	
	
	
}
