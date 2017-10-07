package com.common_lib.android.system;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class AudioHelper {

	public static MediaPlayer playAudioFile(Context context,Uri mediaUri){
		
		return MediaPlayer.create(context,mediaUri);
	}

	
}
