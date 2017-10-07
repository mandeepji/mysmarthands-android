package com.msh.common.android.dictionary.view;

import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.ads.AdView;
import com.msh.common.android.dictionary.Constants;
import com.msh.common.android.dictionary.AppInstance;
import com.msh.common.android.dictionary.AppInstance.DatabaseChangeListener;
import com.msh.common.android.dictionary.R;
import com.msh.common.android.dictionary.SingleSelectionManager;
import com.msh.common.android.dictionary.database.CategoryCursorAdapter;
import com.msh.common.android.dictionary.database.MSHDatabaseAdapter;
import com.msh.common.android.dictionary.database.VideoCursorAdapter;
import com.msh.common.android.dictionary.view.ToolBarView.SegementListner;
import com.common_lib.android.storage.PreferencesHelper;
import com.common_lib.android.ui.DialogHelper;
import com.msh.common.android.dictionary.AdMobHelper;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.VideoView;


public class LearnActivityOld extends Activity implements View.OnClickListener,
		OnItemClickListener, TextWatcher, OnPreparedListener,
		OnCompletionListener, OnEditorActionListener, OnErrorListener,
		SingleSelectionManager, SegementListner, OnClickListener,
		DatabaseChangeListener{

	public TitleBarView titleBarView;
	public ToolBarView toolBarView;
	public VideoView videoView;
	public ListView listView;
	public View upBtn;
	public EditText editText;
	public TextView emptyTextView;
	public AdView adView;

	private MSHDatabaseAdapter dbAdapter;

	private final int TABLE_SLIDE_DURATION = 400;

	private final int TABLE_TYPE_ALPHA = 0;
	private final int TABLE_TYPE_CAT = 1;
	private final int TABLE_TYPE_FAV = 2;

	private final int TABLE_STATE_SEARCH = 3;
	private final int TABLE_CATSTATE_CAT = 4;
	private final int TABLE_CATSTATE_VID = 5;

	public int tableMode = 0;
	private int height = 0;
	private int selectedIndex = -1;

	private boolean playEnabled = false;
	private boolean firstPlayHack = true;
	private String lastCatSelected = null;

	public static int TEST_START_TIME = 1150;
	public static String VIDEO_PATH;
	public static String EXT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.learn_layout_old);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		emptyTextView = (TextView) findViewById(R.id.emptyText);
		emptyTextView.setVisibility(View.GONE);

		// videoView
		videoView = (VideoView) findViewById(R.id.videoView);
		videoView.setMediaController(null);
		videoView.setOnPreparedListener(this);
		videoView.setOnErrorListener(this);
		videoView.setOnCompletionListener(this);

		listView = (ListView) findViewById(R.id.listView);
		listView.setTextFilterEnabled(true);
		listView.setOnItemClickListener(this);
		// listView.setEnabled(false);

		upBtn = (View) findViewById(R.id.upBtn);
		upBtn.setOnClickListener(this);

		// TitleBar
		titleBarView = (TitleBarView) findViewById(R.id.titleBarView);
		titleBarView.setTitle(getString(R.string.titleBarDefault));
		titleBarView.setRightButtonListner(this);
		titleBarView.setRightButtonTitle(getString(R.string.goToBtnTxt));
		// titleBarView.setLeftButtonListner(this);
		// titleBarView.setLeftButtonTitle("Play All");
		titleBarView.setHiddenLeftButton(true);

		// Database Object Setup
		dbAdapter = MSHDatabaseAdapter.getInstance();
		AppInstance.getCastContext().dbChangeListener = this;

		// ToolBar
		toolBarView = (ToolBarView) findViewById(R.id.toolBarView);
		toolBarView.segListner = this;
		toolBarView.setMomentary(true);

		// SEARCH SETUP
		editText = (EditText) findViewById(R.id.editTxt);
		editText.addTextChangedListener(this);
		editText.setVisibility(View.GONE);
		editText.setOnEditorActionListener(this);
		SearchLayout s = (SearchLayout) findViewById(R.id.searchLL);
		s.setSearchActivity(this);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		height = displaymetrics.heightPixels;
		height *= 0.92; // shouldnt be hard coded

		this.viewSetup(TABLE_TYPE_ALPHA);

		// first play hack
		AppInstance.playDummyVid(videoView);
		
		if(AppInstance.shouldShowAds(Constants.getString(Constants.CONST_KEY_ADMOB_HOME_ADID))){
			adView = AdMobHelper.startGenericAdView(
					this,
					Constants.getString(Constants.CONST_KEY_ADMOB_HOME_ADID),
					R.id.searchLL,
					true);
		}
		
		// upgrade reminder
		if(!AppInstance.fullVersionPurchased()){
			upgradeReminder();
		}
		
//		PreferencesHelper.printAll(this);
	}

	@Override
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
		
		if (!isTableUp())
			toggleTable(null);

		if (dbAdapter == null)
			dbAdapter = MSHDatabaseAdapter.getInstance();
	}

	@Override
	protected void onDestroy() {

		CursorAdapter ca = (CursorAdapter) listView.getAdapter();
		ca.getCursor().close();
		dbAdapter.close();
		
		if(adView !=null){
			adView.destroy();
		}
		
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {

		// Back From Video Play
		if (!isTableUp())
			this.toggleTable(null);

		// Back from Category drillDown
		else if (tableMode == TABLE_CATSTATE_VID)
			this.viewSetup(TABLE_CATSTATE_CAT);

		// Hide Search
		else if (this.isSearching())
			this.toggleSearch();

		// Exit APP
		else
			super.onBackPressed();
	}

	public static String nameToPath(String name) {

		String word = (name + EXT).replace("/", "_");
		// Log.d("RBI", VIDEO_PATH + word);
		return VIDEO_PATH + word;
	}

	public static String nameToQuizPath(String name) {

		String word = (name + "_quiz" + EXT).replace("/", "_");
		// Log.d("RBI", VIDEO_PATH + word);
		return VIDEO_PATH + word;
	}

	public static String firstPath() {

		String word = MSHDatabaseAdapter.getInstance().getFirstVid()
				.replace("/", "_");
		// Log.d("RBI", VIDEO_PATH + word+EXT);
		return VIDEO_PATH + word + EXT;
	}
	
	// --------------------------------------------------------------------+	
	private List<String> getGoToMenuItems() {

		List<String> menuItems = new ArrayList<String>(4);
		menuItems.add("Quiz");
		menuItems.add("Play All");
		if (Constants.getBoolean(Constants.CONST_KEY_SONG_VIEW_ENABLED)) {
			menuItems.add("Songs");
		}
		if (Constants.getBoolean(Constants.CONST_KEY_USES_KEYBOARD)) {
			menuItems.add("Keyboard");
		}
		if (Constants.getBoolean(Constants.CONST_KEY_IAP_USES_IAP)) {
			menuItems.add("Upgrades");
		}

		return menuItems;
	}

	@Override
	public void onClick(DialogInterface dialog, int selected) {

		Intent intent = null;
		switch (selected) {
		case 0: // quiz
			intent = new Intent(this, QuizMenuActivity.class);
			break;

		case 1: // play all
			intent = new Intent(this, PlayMenuActivity.class);
			break;

		case 2: // songs OR keyboard OR IAP
			if(Constants.getBoolean(Constants.CONST_KEY_SONG_VIEW_ENABLED)){
				intent = new Intent(this, SongsActivity.class);
			}
			else if (Constants.getBoolean(Constants.CONST_KEY_USES_KEYBOARD)) {
				intent = new Intent(this, KeyboardActivity.class);
			}
			else {
				intent = new Intent(this, UpgradeActivity.class);
			}
			break;

		case 3: // keyboard OR IAP
			if (Constants.getBoolean(Constants.CONST_KEY_USES_KEYBOARD)) {
				intent = new Intent(this, KeyboardActivity.class);
			}
			else {
				intent = new Intent(this, UpgradeActivity.class);
			}
			break;
			
		case 4: // IAP
			intent = new Intent(this, UpgradeActivity.class);
			break;
		}
		if (intent != null) {
			startActivity(intent);
		}
	}

	private void goToBtnClicked() {

		DialogHelper.simpleListDialog(this, "Go to",
				getGoToMenuItems(), this).show();
	}
	
	// --------------------------------------------------------------------+
	public void onClick(View v) {

		if (v == upBtn) {
			this.toggleTable(null);
		}

		if (v == titleBarView.rightButton) {
			this.goToBtnClicked();
		}
	}

	public void onItemClick(AdapterView<?> arg0, View row, int index, long arg3) {

		if (tableMode == TABLE_CATSTATE_CAT) {
			CategoryCursorAdapter cca = (CategoryCursorAdapter) listView
					.getAdapter();
			lastCatSelected = cca.getName(row);
			this.viewSetup(TABLE_CATSTATE_VID, lastCatSelected);
		} else
    		this.wordRowClicked(row, index);

	}

	public void favClicked(View favBtn) {

		VideoCursorAdapter vca = (VideoCursorAdapter) listView.getAdapter();
		// Cursor c = vca.getCursor();
		ViewGroup row = (ViewGroup) favBtn.getParent();
		// String videoName = vca.getTitle(row);

		// Handles all the db updating
		vca.invertFavValue(row);

		// GET THIS TO RUN ON BACKGROUND THREAD??
		// Must stop and reset title cause requery forces table to reset to top
		// No notification hook for this I dont think
		this.viewSetup(tableMode);
		videoView.stopPlayback();
		titleBarView.setTitle(getString(R.string.titleBarDefault));
	}

	private void wordRowClicked(View row, int index) {

        VideoCursorAdapter vca = (VideoCursorAdapter) listView.getAdapter();
		String videoName = vca.getTitle(row);

		if (this.isSearching()) {
			this.toggleSearch();
		}

		if (isTableUp()) {
			this.toggleTable(videoName);
			// this.toggleTable(null);
		} else
			titleBarView.setTitle(videoName);

		playEnabled = true;

		AppInstance.playVideo(videoView, videoName, false);
		// old
        // videoView.setVideoPath(nameToPath(videoName));

		int r = vca.getPosition(videoName);
		// Log.d("RBI", "Row:"+r);
		listView.requestFocusFromTouch();
		listView.setSelection(r);
	}

	// TOOL BAR SEGMENT CHANGED VALUE
	public void indexChanged(int index) {

		viewSetup(index);
	}

	// TOOL BAR SEGMENT PRESSED
	public void segmentPressed(int index) {

		if (index == TABLE_TYPE_CAT && tableMode == TABLE_CATSTATE_VID)
			viewSetup(TABLE_CATSTATE_CAT);
	}

	// TOOL BAR PLAY ALL BUTTON PRESSED
	public void searchBtnPressed(Button b) {

		this.toggleSearch();
	}

	// --------------------------------------------------------------------+
	private void viewSetup(int tableType) {

		this.viewSetup(tableType, null);
	}

	private void viewSetup(int tableType, String name) {

		// when videoView.setPath(...) is called the table is returned to
		// if(!isTableUp())
		// toggleTable(null);
		// Log.d("RBI",listView.getTop()+"");

		titleBarView.setTitle(getString(R.string.titleBarDefault));
		if (!firstPlayHack)
			this.hideBacking(true);
		videoView.stopPlayback();

		switch (tableType) {

		case TABLE_TYPE_CAT:
		case TABLE_CATSTATE_CAT:
			tableMode = TABLE_CATSTATE_CAT;
			dataSetup(TABLE_TYPE_CAT);
			// toolBarView.rightBtn.setVisibility(View.INVISIBLE);
			break;
		case TABLE_CATSTATE_VID:
			tableMode = tableType;
			dataSetup(tableType, lastCatSelected);
			// toolBarView.rightBtn.setVisibility(View.INVISIBLE);
			break;
		case TABLE_STATE_SEARCH:
			dataSetup(tableType);
			break;
		case TABLE_TYPE_FAV:
			tableMode = tableType;
			dataSetup(tableType);
			// toolBarView.rightBtn.setVisibility(View.INVISIBLE);
			break;
		// case TABLE_TYPE_ALPHA:
		default:
			tableMode = tableType;
			dataSetup(tableType);
			// toolBarView.rightBtn.setVisibility(View.VISIBLE);
		}
	}

	private void dataSetup(int tableType) {

		this.dataSetup(tableType, null);
	}

	@SuppressWarnings("rawtypes")
	private void dataSetup(int tableType, String name) {

		// Log.d("RBI",""+toolBarView.rightBtn.isEnabled());

		Cursor c = null;
		boolean isCat = false;
		Class cl = VideoCursorAdapter.class;
		switch (tableType) {
		case TABLE_TYPE_ALPHA:
			c = dbAdapter.allVideoNamesandFavValues();
			break;

		case TABLE_TYPE_CAT:
			c = dbAdapter.AllCategoriesNoAll();
			isCat = true;
			cl = CategoryCursorAdapter.class;
			break;

		case TABLE_CATSTATE_VID:
			c = dbAdapter.videoNamesForCategoryWithFavValues(name);
			break;

		case TABLE_TYPE_FAV:
			c = dbAdapter.favoritesWithValues();
			break;

		case TABLE_STATE_SEARCH:
			editText.setText("");
			c = dbAdapter.allVideoNames();
		}

		if (c.getCount() <= 0)
			emptyTextView.setVisibility(View.VISIBLE);
		else
			emptyTextView.setVisibility(View.GONE);

		CursorAdapter adapter = (CursorAdapter) listView.getAdapter();
		CursorAdapter newAdapter = null;
		if (adapter == null) { // first time set
			if (isCat)
				adapter = new CategoryCursorAdapter(this, c);
			else
				adapter = new VideoCursorAdapter(this, this, c);
			listView.setAdapter(adapter);
		} else { // Change Adapter
			if (cl == adapter.getClass())
				adapter.changeCursor(c);
			else {
				if (isCat)
					newAdapter = new CategoryCursorAdapter(this, c);
				else
					newAdapter = new VideoCursorAdapter(this, this, c);
				listView.setAdapter(newAdapter);
				adapter.getCursor().close();
			}
		}
		// Log.d("RBI", "1");
	}

	public void hideBacking(boolean hide) {

		// EMPTY TABLE CONFIG
		if (hide) {
			videoView.setVisibility(View.INVISIBLE);
			upBtn.setVisibility(View.GONE);
		} else {
			videoView.setVisibility(View.VISIBLE);
			upBtn.setVisibility(View.VISIBLE);
		}
	}

	public void tableDownHack() {

		final View parent = (View) listView.getParent();
		final int deltaY = height / 2;

		listView.layout(listView.getLeft(), deltaY, listView.getRight(),
				parent.getHeight());
	}

	public void toggleTable(final String videoName) {

		final boolean downToUp = !this.isTableUp();
		if(Constants.DEBUG)
			Toast.makeText(LearnActivityOld.this, "toggle called with " + videoName + " and MovingUp=" + downToUp, Toast.LENGTH_SHORT).show();
		if (downToUp) {
			videoView.stopPlayback();
		} else {
			this.hideBacking(false);
		}

		listView.setEnabled(false);
		final View parent = (View) listView.getParent();
		final int deltaY = (downToUp) ? -(listView.getTop()) : height / 2;

		Animation animation = new TranslateAnimation(0, 0, 0, deltaY);

		animation.setDuration(TABLE_SLIDE_DURATION);

		animation.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				if (downToUp)
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

				if (downToUp)
					hideBacking(true);

				if (videoName == null || downToUp)
					titleBarView.setTitle(getString(R.string.titleBarDefault));
				else
					titleBarView.setTitle(videoName);
			}
		});

		listView.startAnimation(animation);
	}

	public void changeTitle(String title, boolean tableUp) {

		if (tableUp)
			titleBarView.setTitle(getString(R.string.titleBarDefault));
		else
			titleBarView.setTitle(title);
	}

	public boolean isSearching() {

		return (editText.getVisibility() == View.VISIBLE);
	}

	public boolean isPlaying() {

		return videoView.isPlaying();
	}

	public boolean isTableUp() {
		if(Constants.DEBUG)
			Toast.makeText(LearnActivityOld.this, "isTableUp checked " + listView.getTop(), Toast.LENGTH_SHORT).show();
		return listView.getTop() == 0;
	}

	public void toggleSearch() {

		if (!this.isSearching()) {
			editText.setVisibility(View.VISIBLE);
			titleBarView.setVisibility(View.GONE);
			toolBarView.setVisibility(View.GONE);
			editText.requestFocus();
			this.viewSetup(TABLE_TYPE_ALPHA); // hack
			this.viewSetup(TABLE_STATE_SEARCH);

		} else {
			editText.setVisibility(View.GONE);
			toolBarView.setVisibility(View.VISIBLE);
			titleBarView.setVisibility(View.VISIBLE);
			// this.viewSetup(tableMode);
			this.viewSetup(TABLE_TYPE_ALPHA);
			toolBarView.setSelected(0);
		}

		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// only will trigger it if no physical keyboard is open
		mgr.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
	}

	// --------------------------------------------------------------------+
	// KeyEvents
	public void afterTextChanged(Editable s) {
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {

		Filterable fAdapter = (Filterable) listView.getAdapter();
		fAdapter.getFilter().filter(s.toString());
	}

	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		if (actionId == EditorInfo.IME_ACTION_DONE) {
			toggleSearch();
			return false;
		}
		return true;
	}

	// --------------------------------------------------------------------+
	// VideoListeners
	public void onPrepared(MediaPlayer mp) {

		if (!firstPlayHack && playEnabled) {
			videoView.start();
			playEnabled = false;
			if (isTableUp())
				tableDownHack();
		} else {
			// this.hideBacking(true);
			firstPlayHack = false;
			listView.setEnabled(true);
		}
	}

	public void onCompletion(MediaPlayer mp) {

		videoView.stopPlayback();
		this.toggleTable(null);
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {

		// Log.d("RBI", what+" : "+extra);
		return false;
	}

	// --------------------------------------------------------------------+
	// SingleSelectionManager
	public int selectedRow() {

		return selectedIndex;
	}
	
	// --------------------------------------------------------------------+
	// DatabaseChangeListener
	@Override
	public void DatabaseDidReload(MSHDatabaseAdapter newInstance) {
		
		dbAdapter = newInstance;
		this.viewSetup(TABLE_TYPE_ALPHA);
		
		if(!AppInstance.shouldShowAds(Constants.getString(Constants.CONST_KEY_ADMOB_HOME_ADID)) &&
				adView !=null){
			AdMobHelper.removeAddView(adView);
			PreferencesHelper.printAll(this);
		}
		
	}
	
	// --------------------------------------------------------------------+
	// IAP Reminder
	public void upgradeReminder() {
		
		// disable message by making it blank
		if(Constants.getString(Constants.CONST_KEY_IAP_REMINDER_MESSAGE).length() <1){
			return;
		}
		
		// launches counted in the downloaderActivity
		int launches = 
			PreferencesHelper.get(this).getInt(Constants.LAUNCH_COUNTER_KEY,-1);
		
//		Logger.log(launches);
		
		if(launches >= Constants.UPGRADE_REMINDER_INTERVAL || launches ==-1){
			runOnUiThread(new Runnable() {
				public void run() {
					
					if(Constants.getString(Constants.CONST_KEY_PROMO_STORE_LINK).length() >0){
						DialogHelper.simpleAlertDialog(LearnActivityOld.this,
								"Don't Forget!", Constants.getString(Constants.CONST_KEY_IAP_REMINDER_MESSAGE),
								"Check it out", "Maybe later", 
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										switch(which){
										case DialogInterface.BUTTON_POSITIVE:
											DialogHelper.goToAppStoreListing(
													LearnActivityOld.this,
													Constants.getString(Constants.CONST_KEY_PROMO_STORE_LINK),
													!Constants.USE_EXTERNAL_XAPK_SOURCE);
											break;
										}
									}
								}).show();
					}
					else{
						DialogHelper.simpleAlertDialog(LearnActivityOld.this,
								"Don't Forget!",
								Constants.getString(Constants.CONST_KEY_IAP_REMINDER_MESSAGE)).show();
					}
				}
			});
			
			// reset counter
			PreferencesHelper.set(this,Constants.LAUNCH_COUNTER_KEY,0);
		}
		else{
			PreferencesHelper.increment(this, Constants.LAUNCH_COUNTER_KEY, 1);
		}
	}
	
	// --------------------------------------------------------------------+
}
