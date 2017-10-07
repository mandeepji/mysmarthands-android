package com.msh.common.android.dictionary.view;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.ads.AdView;
import com.msh.common.android.dictionary.AppInstance;
import com.msh.common.android.dictionary.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class KeyboardActivity extends Activity implements OnClickListener {

	List<Button> keyboard;
	Button keyBoardSwitchBtn,abcBtn;
	ImageView imageView;
	AdView adView;

	boolean isQwertyLayout = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.keyboard_layout);

		imageView = (ImageView) findViewById(R.id.imageView);
		keyBoardSwitchBtn = (Button) findViewById(R.id.keySwitchBtn);
		keyBoardSwitchBtn.setOnClickListener(this);
		
		abcBtn = (Button) findViewById(R.id.abcBtn);
		abcBtn.setOnClickListener(this);
		abcBtn.setVisibility(View.GONE);

		keyboardSetup();
		toggleKeyboardLayout();

		// removed... no room
//		if (AppInstance.shouldShowAds(Constants.getString(Constants.CONST_KEY_ADMOB_HOME_ADID))) {
//			adView = AdMobHelper.startGenericAdView(this,
//					Constants.ADMOB_HOME_ADID, R.id.adFrame, true);
//		}
	}

	protected void onPause() {

		if (adView != null) {
			adView.pause();
		}

		super.onPause();
	}

	@Override
	protected void onResume() {

		super.onResume();
		if (adView != null) {
			adView.resume();
		}
	}

	@Override
	protected void onDestroy() {

		if (adView != null) {
			adView.destroy();
		}

		super.onDestroy();
	}

	private void keyboardSetup() {

		int[] rows = { R.id.row1, R.id.row2, R.id.row3 };
		keyboard = new ArrayList<Button>();

		ViewGroup row;
		Button temp;
		for (int rowID : rows) {
			row = (ViewGroup) findViewById(rowID);
			for (int btn = 0; btn < row.getChildCount(); ++btn) {
				temp = (Button) row.getChildAt(btn);
				keyboard.add(temp);
				temp.setOnClickListener(this);
			}
		}
		
		//remove last button in keyboard (abcs button)
		keyboard.remove(keyboard.size()-1);
	}

	public void toggleKeyboardLayout() {

		if (isQwertyLayout) {
			setKeyLayoutAlpha();
		} else {
			setKeyLayoutQwerty();
		}
	}

	public void setKeyLayoutAlpha() {

		isQwertyLayout = false;
		keyBoardSwitchBtn.setText("qwerty");

		Character letter = 'A';
		for (Button btn : keyboard) {
			btn.setText(letter.toString());
			letter++;
		}
	}

	public void setKeyLayoutQwerty() {

		isQwertyLayout = true;
		keyBoardSwitchBtn.setText("alpha");

		char[] qwerty = getString(R.string.qwertyString).toCharArray();
		int charIndex = 0;
		for (Button btn : keyboard) {
			btn.setText(Character.toString(qwerty[charIndex++]));
		}
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onClick(View v) {

		// qwerty/alpha layout key
		if (v == keyBoardSwitchBtn) {
			toggleKeyboardLayout();
		}
		// abc video key
		else if(v==abcBtn){
			playABCVideo();
		}
		// letter key
		else{
			Button b = (Button) v;
			String letter = b.getText().toString().toLowerCase();
			AppInstance.loadImage(imageView, letter);
		}
		
	}

	private void playABCVideo() {
	
		Intent i = new Intent(this, VideoPlayerActivity.class);
		i.putExtra(VideoPlayerActivity.SINGLE_VIDEO_SOURCE,"testVid");
		startActivity(i);
	}

}
