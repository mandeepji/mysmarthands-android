package com.msh.common.android.dictionary.view;

import com.google.android.gms.ads.AdView;
import com.msh.common.android.dictionary.Constants;
import com.msh.common.android.dictionary.AppInstance;
import com.msh.common.android.dictionary.R;
import com.msh.common.android.dictionary.database.CategoryCursorAdapter;
import com.msh.common.android.dictionary.database.MSHDatabaseAdapter;
import com.msh.common.android.dictionary.AdMobHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PlayMenuActivity extends Activity implements OnItemClickListener {

	ListView listView;
	public AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_menu_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// TITLE BAR
		TitleBarView titleBarView = (TitleBarView) findViewById(R.id.titleBarView);
		titleBarView.setTitle(getString(R.string.titleBarDefault));
		titleBarView.setHiddenLeftButton(true);
		titleBarView.setHiddenRightButton(true);

		// LIST SETUP
		listView = (ListView) findViewById(R.id.listView3);
		MSHDatabaseAdapter dbAdapter = MSHDatabaseAdapter.getInstance();
		Cursor c = dbAdapter.AllCategories();
		CategoryCursorAdapter cca = new CategoryCursorAdapter(this, c);
		listView.setAdapter(cca);
		listView.setOnItemClickListener(this);

		// AdView
		if (AppInstance.shouldShowAds(Constants.getString(Constants.CONST_KEY_ADMOB_HOME_ADID))) {
			adView = AdMobHelper.startGenericAdView(this,
					Constants.getString(Constants.CONST_KEY_ADMOB_HOME_ADID), R.id.root, true);
		}

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

		CategoryCursorAdapter cca = (CategoryCursorAdapter) listView
				.getAdapter();
		cca.getCursor().close();

		if (adView != null) {
			adView.destroy();
		}

		super.onDestroy();
	}

	// -------------------------- EVENTS --------------------------+

	public void onItemClick(AdapterView<?> arg0, View cell, int index, long arg3) {

		listView.setEnabled(false);

		CategoryCursorAdapter cca = (CategoryCursorAdapter) listView
				.getAdapter();
		Intent intent = new Intent();
		intent.setClass(this, PlayActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(getString(R.string.quizSetKEY),
				cca.getNameForIndex(index));
		intent.putExtras(bundle);
		startActivity(intent);

		listView.setEnabled(true);
	}

}
