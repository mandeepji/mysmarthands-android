package com.common_lib.android.fragment.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

import java.util.HashMap;

public abstract class FragmentTabActivity extends FragmentActivity 
	implements OnTabChangeListener {

//	protected FragmentTabHost tabHost; // doesn't work
	protected TabHost tabHost;
	protected HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabInfo>();
	protected TabInfo lastTab = null;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if(!this.setContentView()){
			setContentView(getLayoutResID());
		}
		initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null) {
			tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
	}

	private void initialiseTabHost(Bundle args) {

		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
//		tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
//		tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		
		addTabs(args);
		tabHost.setOnTabChangedListener(this);
		this.onTabChanged(defaultTabID());
	}

	private void addTab(TabHost.TabSpec tabSpec, TabInfo tabInfo) {

		// Attach a Tab view factory to the spec
		tabSpec.setContent(new TabFactory(this));
		String tag = tabSpec.getTag();

		// Check to see if we already have a fragment for this tab, probably
		// from a previously saved state. If so, deactivate it, because our
		// initial state is that a tab isn't shown.
		tabInfo.fragment = 
				this.getSupportFragmentManager().findFragmentByTag(tag);
		if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
			FragmentTransaction ft 
				= this.getSupportFragmentManager().beginTransaction();
			ft.detach(tabInfo.fragment);
			ft.commit();
			this.getSupportFragmentManager().executePendingTransactions();
		}

		tabHost.addTab(tabSpec);
		this.mapTabInfo.put(tabInfo.tag,tabInfo);
	}

	public void addTab(String label, Class<?> fragClass, Bundle bundle) {

		TabHost.TabSpec spec = tabHost.newTabSpec(label);
		View tabIndicatorView = this.getTabIndicatorView(label,LayoutInflater.from(this),bundle);
		if( tabIndicatorView == null ){
			spec.setIndicator(label);
		}
		else{
			spec.setIndicator(tabIndicatorView);
		}

		TabInfo tabInfo = new TabInfo(label, fragClass, bundle);
		this.addTab(spec, tabInfo);
	}

	public Fragment getTabFragment(String fragID){
		
		return mapTabInfo.get(fragID).fragment;
	}
	
	public String getCurrentTabTag(){
		
		return tabHost.getCurrentTabTag();
	}
	
	public Fragment getCurrentFragment(){
		
		return getTabFragment( getCurrentTabTag() );
	}
	
	// ------------------------------------------------+
	// customization overides
	public View getTabIndicatorView(String label,LayoutInflater inflater,Bundle bundle){
		
		return null;
	}

	public boolean setContentView(){
		
		return false;
	}
	
	public void addTabs(Bundle bundle){
		
	}
	
	public abstract String defaultTabID();
	
	public int getLayoutResID(){
		
		return 0;
	}

	public abstract int getTabContentResID();
	
	// ------------------------------------------------+
	@Override
	public void onTabChanged(String tabId) {

		TabInfo newTab = this.mapTabInfo.get(tabId);
		if (lastTab != newTab) {
			FragmentTransaction ft = this.getSupportFragmentManager()
					.beginTransaction();
			if (lastTab != null) {
				if (lastTab.fragment != null) {
					// ft.detach(mLastTab.fragment);
					ft.hide(lastTab.fragment);
				}
			}
			if (newTab != null) {
				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(this,
							newTab.clss.getName(), newTab.args);
					ft.add(getTabContentResID(), newTab.fragment, newTab.tag);
				} else {
					// ft.attach(newTab.fragment);
					ft.show(newTab.fragment);
				}
			}

			lastTab = newTab;
			ft.commit();
			this.getSupportFragmentManager().executePendingTransactions();
		}
	}

	// ------------------------------------------------+
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

	// ------------------------------------------------+
	class TabFactory implements TabContentFactory {

		private final Context mContext;

		public TabFactory(Context context) {
			mContext = context;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see TabContentFactory#createTabContent(String)
		 */
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}

	}

	// ------------------------------------------------+
}
