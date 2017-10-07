package com.common_lib.android.storage.sqlite;

import android.database.Cursor;

public class CursorHelper {

	public static String getString(Cursor c, String columnName) {

		return c.getString(c.getColumnIndex(columnName));
	}

	public static int getInt(Cursor c, String columnName) {

		return c.getInt(c.getColumnIndex(columnName));
	}

	public static double getDouble(Cursor c, String columnName) {

		return c.getDouble(c.getColumnIndex(columnName));
	}

	public static long getLong(Cursor c, String columnName) {

		return c.getLong(c.getColumnIndex(columnName));
	}

	public static float getFloat(Cursor c, String columnName) {

		return c.getFloat(c.getColumnIndex(columnName));
	}
	
	public static Short getShort(Cursor c, String columnName){
		
		return c.getShort( c.getColumnIndex(columnName) );
	}
	
}
