package com.common_lib.android.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.common_lib.android.Logging.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PreferencesHelper {

	public static void set(Context context, String name, String val) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(name, val);
		edit.commit();

	}

	public static void set(Context context, String name, int val) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt(name, val);
		edit.commit();

	}

	public static void set(Context context, String name, long val) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.Editor edit = prefs.edit();
		edit.putLong(name, val);
		edit.commit();

	}

	public static void set(Context context, String name, boolean val) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(name, val);
		edit.commit();

	}

	public static void set(Context context, String name, float val) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.Editor edit = prefs.edit();
		edit.putFloat(name, val);
		edit.commit();

	}

	public static void set(Context context, Map<String, Object> vals) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		SharedPreferences.Editor edit = prefs.edit();
		String name;
		Object value;
		for (Entry<String, Object> entry : vals.entrySet()) {
			name = entry.getKey();
			value = entry.getValue();
			if (value instanceof String)
				edit.putString(name, (String) (value));
			else if (value instanceof Boolean)
				edit.putBoolean(name, (Boolean) (value));
			else if (value instanceof Integer)
				edit.putInt(name, (Integer) (value));
			else if (value instanceof Float)
				edit.putFloat(name, (Float) (value));
			else if (value instanceof Long)
				edit.putLong(name, (Long) (value));
		}
		edit.commit();

	}

	public static void remove(Context context, String key) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.remove(key);
		edit.commit();
	}

	public static void removeAll(Context context,Collection<String> keys){
		
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		for (String key : keys) {
			edit.remove(key);
		}
		edit.commit();
	}
	
	public static void removeAll(Context context){
		
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		
		
		SharedPreferences.Editor edit = prefs.edit();
		Map<String, ?> keys = prefs.getAll();
		for (Entry<String, ?> entry : keys.entrySet()) {
			edit.remove(entry.getKey());
		}
		edit.commit();
	}



	public static void set(Context context, List<String> keys) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		for (String key : keys) {
			edit.remove(key);
		}
		edit.commit();
	}

	public static boolean contains(Context context, String name) {

		return PreferenceManager.getDefaultSharedPreferences(context).contains(
				name);
	}

	public static SharedPreferences get(Context context) {

		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static SharedPreferences get(Fragment fragment) {

		return PreferenceManager.getDefaultSharedPreferences(fragment
				.getActivity());
	}

	public static int increment(Context context, String name, int incr) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		int val = prefs.getInt(name, 0) + incr;

		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt(name, val);
		edit.commit();

		return val;
	}

	public static void printAll(Context context) {

		Map<String, ?> keys =
				PreferenceManager.getDefaultSharedPreferences(context).getAll();

		Logger.log("Shared Preference Entrys:");
		for (Entry<String, ?> entry : keys.entrySet()) {
			Logger.log(entry.getKey() + ": "
					+ entry.getValue().toString());
		}
	}
}
