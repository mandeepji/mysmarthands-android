package com.common_lib.android.fragment.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;

import com.common_lib.R;

import java.util.HashMap;


public abstract class ActionBarTabActivity extends ActionBarActivity implements
		ActionBar.TabListener {

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	protected HashMap<Tab, TabInfo> mapTabInfo = new HashMap<Tab, TabInfo>();
	protected TabInfo lastTabInfo = null;
	protected Tab lastTab = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(this.getContentResID());

		// Set up the action bar to show tabs.
		ActionBar actionBar = getsActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		this.addTabs();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getsActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getsActionBar()
				.getSelectedNavigationIndex());
	}

	protected ActionBar getsActionBar() {

		return getSupportActionBar();
	}

	public Tab addTab(String title, Class<?> fragClass, Bundle args) {

		// add resID for images
		ActionBar bar = getsActionBar();
		Tab tab = bar.newTab().setText(title).setTabListener(this);

		this.mapTabInfo.put(tab, new TabInfo(title, fragClass, args));
		bar.addTab(tab);
		
		return tab;
	}

	public Tab addTab(String title, Class<?> fragClass,Bundle args,Object tag){
		
		Tab tab = this.addTab(title, fragClass, args);
		return tab.setTag(tag);
	}

	public Fragment getFragment(Object tag){
		
		for (Tab tab : mapTabInfo.keySet()) {
			if(tag == tab.getTag()){
				return mapTabInfo.get(tab).fragment;
			}
		}
		return null;
	}
	
	public Fragment getFragment(Tab tab){
		
		return mapTabInfo.get(tab).fragment;
	}
	
	public Fragment getCurrentFragment(){
		
		return lastTabInfo.fragment;
	}
	
	public Tab getCurrentTab(){
		
		return lastTab;
	}
	
	// ------------------------------------------------------+
	// subclasses use this and addTab(...) to define tabs
	public abstract void addTabs();

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {

		TabInfo newTab = this.mapTabInfo.get(tab);
//		Logger.log("frag "+newTab.fragment);
//		Logger.log("args "+newTab.args);
		
		if (lastTabInfo != newTab) {
			FragmentTransaction ft = this.getSupportFragmentManager()
					.beginTransaction();
			if (lastTabInfo != null) {
				if (lastTabInfo.fragment != null) {
					// ft.detach(mLastTab.fragment);
					ft.hide(lastTabInfo.fragment);
				}
			}
			if (newTab != null) {
				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(this,
							newTab.clss.getName(), newTab.args);
					 ft.add(getTabContentResID(), newTab.fragment,
					 newTab.tag);
				} else {
					// ft.attach(newTab.fragment);
					ft.show(newTab.fragment);
				}
			}

			lastTabInfo = newTab;
			lastTab = tab;
			ft.commit();
			this.getSupportFragmentManager().executePendingTransactions();
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction arg1) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction arg1) {

	}

	public int getContentResID(){
		
		return R.layout.action_bar_tab_layout;
	}
	
	public int getTabContentResID(){
		
		return R.id.container;
	}
	
	// ------------------------------------------------------+
	private class TabInfo {

		private String tag;
		private Class<?> clss;
		private Bundle args;
		private Fragment fragment;

		TabInfo(String tag, Class<?> clazz, Bundle args) {
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
		}

	}

	// ------------------------------------------------------+
}