package com.msh.common.android.dictionary.view;

import java.util.ArrayList;
import java.util.Collections;

import com.google.android.gms.ads.AdView;
import com.msh.common.android.dictionary.Constants;
import com.msh.common.android.dictionary.AppInstance;
import com.msh.common.android.dictionary.QuizSet;
import com.msh.common.android.dictionary.R;
import com.msh.common.android.dictionary.SingleSelectionManager;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class QuizMenuActivity extends Activity implements OnItemClickListener,
		SingleSelectionManager {

	private Spinner answers;
	private Spinner questions;
	private ListView listView;
	public AdView adView;

	private int selectedCatRow = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz_menu_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// TITLE BAR
		TitleBarView titleBarView = (TitleBarView) findViewById(R.id.titleBarView);
		titleBarView.setTitle(getString(R.string.titleBarDefault));
		// titleBarView.setRightButtonListner(this);
		// titleBarView.setRightButtonTitle(getString(R.string.quizBtnTxt));
		titleBarView.setHiddenRightButton(true);
		titleBarView.setHiddenLeftButton(true);

		// LIST SETUP
		listView = (ListView) findViewById(R.id.listView2);
		MSHDatabaseAdapter dbAdapter = MSHDatabaseAdapter.getInstance();
		Cursor c = dbAdapter.AllCategoriesLimited();
		CategoryCursorAdapter cca = new CategoryCursorAdapter(this, this, c);
		listView.setAdapter(cca);
		listView.setOnItemClickListener(this);

		// SPINNER SETUP
		answers = (Spinner) findViewById(R.id.spinner1);
		this.spinnerSetup(answers);
		answers.setSelection(1);
		questions = (Spinner) findViewById(R.id.spinner2);
		this.spinnerSetup(questions);

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

		selectedCatRow = index;
		CategoryCursorAdapter cca = (CategoryCursorAdapter) listView
				.getAdapter();
		cca.notifyDataSetChanged();
		this.questionCheck(cca.getCountForIndex(index));

		this.onClick(cell, index);
	}

	public void onClick(View v, int index) {

		MSHDatabaseAdapter dbAdapter = MSHDatabaseAdapter.getInstance();

		CategoryCursorAdapter cca = (CategoryCursorAdapter) listView
				.getAdapter();
		Cursor cursor = dbAdapter.videoNamesForCategory(cca
		// .getNameForIndex(selectedCatRow));
				.getNameForIndex(index));

		// Convert Cursor to list
		ArrayList<String> words = new ArrayList<String>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			words.add(cursor.getString(0));
			cursor.moveToNext();
		}
		cursor.close();

		Collections.shuffle(words);

		ArrayList<String> sub = new ArrayList<String>(words.subList(0,
				this.getSpinnerValue(questions)));

		QuizSet qSet = new QuizSet(this.getSpinnerValue(answers), sub, words);

		Intent intent = new Intent();
		intent.setClass(this, QuizActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(getString(R.string.quizSetKEY), qSet);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	// --------------------------SingleSelectionManager--------------------------+
	public int selectedRow() {

		return selectedCatRow;
	}

	// -------------------------- HELPERS --------------------------+
	private int getSpinnerValue(Spinner spinner) {

		return Integer.valueOf(spinner.getSelectedItem().toString());
	}

	private void questionCheck(int catTotal) {

		int newIndex = catTotal / 5;
		int qs = this.getSpinnerValue(questions);
		int curIndex = questions.getSelectedItemPosition();

		// this.spinnerSetup(questions);

		if (catTotal < qs) {
			if (newIndex > 0)
				newIndex--;
			questions.setSelection(newIndex, true);
		} else
			questions.setSelection(curIndex, false);
	}

	private void spinnerSetup(Spinner spinner) {

		ArrayAdapter<CharSequence> adapter = null;
		// Answers
		if (spinner == answers) {
			adapter = ArrayAdapter
					.createFromResource(this, R.array.answers_array,
							android.R.layout.simple_spinner_item);
		}

		// Questions
		else {
			adapter = ArrayAdapter
					.createFromResource(this, R.array.question_array,
							android.R.layout.simple_spinner_item);
			
			// not sure what this was for ??
			/*CategoryCursorAdapter cca = (CategoryCursorAdapter) listView
					.getAdapter();

			int catTotal = cca.getCountForIndex(selectedCatRow);
			int itemCount = catTotal / 5;
			if (itemCount > 0)
				itemCount--;

			if (itemCount > 3)
				itemCount = 3;

			adapter = new ArrayAdapter<CharSequence>(this,
					android.R.layout.simple_spinner_item);
			for (int i = 1; i <= itemCount + 1; i++)
				adapter.add((5 * i + ""));
			*/

		}
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
}
