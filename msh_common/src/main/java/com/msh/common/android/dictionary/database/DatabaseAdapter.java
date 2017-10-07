package com.msh.common.android.dictionary.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.common_lib.android.storage.StorageHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {

	protected DatabaseHelper dbHelper;
	protected SQLiteDatabase dbObj;
	protected final Context context;

	protected String dbPath = null;
	protected String dbName = null;

	protected static final int DATABASE_VERSION = 1;

	// use this for updating -- SHIP WITH NEW DATABASE VALUE MANUALLY!!
	protected static final int DATABASE_INTERNAL_VERSION = 1;
	
	protected boolean appUpdated = false;

	
	public DatabaseAdapter(Context context,String dbPath,String dbName) {

		//Log.d("RBI", dbPath);
		//Log.d("RBI", dbName);
		this.dbPath = dbPath;
		this.dbName = dbName;
		dbHelper = new DatabaseHelper(context, dbName);
		this.context = context;
	}
	
	public DatabaseAdapter(Context context,String dbName) {

		//Log.d("RBI", dbPath);
		//Log.d("RBI", dbName);
		this.dbPath = StorageHelper.getAppFilesDir(context)+"/databases/";
		this.dbName = dbName;
		dbHelper = new DatabaseHelper(context, dbName);
		this.context = context;
	}

	public String getFullPath(){
		
		return dbPath+dbName;
	}
	
	// ------------------------HELPER INNER CLASS--------------------+
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context,String dbName){
			
			super(context, dbName, null, DATABASE_VERSION);
		}
		
		/*
		DatabaseHelper(Context context) {

			super(context, dbName, null, DATABASE_VERSION);
		}
		*/

		@Override
		public void onCreate(SQLiteDatabase db) {

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			// File dbFile = new File(dbPath);
			// dbFile.delete();

			onCreate(db);
		}
	}
	
	// ------------------------ OPEN/CLOSE DB ----------------------+
	/*
	 * public DatabaseAdapter open() throws SQLException {
	 * 
	 * //dbHelper = new DatabaseHelper(context); //dbObj =
	 * dbHelper.getWritableDatabase(); return this; }
	 */
	public void openDatabase(int use) throws SQLException {

		String fullDbPath = dbPath + dbName;
		dbObj = SQLiteDatabase.openDatabase(fullDbPath, null, use);
	}

	public void close() {

		if (dbHelper != null)
			dbHelper.close();
		if (dbObj != null)
			dbObj.close();
	}

	public boolean isOpen() {

		if (dbObj == null)
			return false;

		return dbObj.isOpen();
	}

	public void openIfNotOpen(int use) {

		if (dbObj == null || !dbObj.isOpen())
			this.openDatabase(use);
	}

	// ------------------------ VERSIONING -------------------------+
	public void checkAndUpdateDB() {

		String currentVersion = "v"+DATABASE_INTERNAL_VERSION;
		boolean update = false;
		try {
			Cursor c = dbObj.rawQuery("SELECT value FROM app_metadata "
					+ "where functionName = 'databaseVersion';", new String[0]);

			c.moveToFirst();
			String ret = c.getString(0);
			c.close();
			update = (ret.compareTo(currentVersion) != 0);
		} catch (Exception e) {
			//Log.e("RBI","d",e);
			update = true;
		}
		
		if(update){
			try {
				//File dbFile = new File(dbPath + dbName);
				//dbFile.delete();
				this.copyDataBase();
				appUpdated = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// ------------------------ COPY OVER --------------------------+
	public void checkAndCopyDB(int use) {

		boolean dbCreated = false;
		try {
			if (!this.checkDataBase()) {
				dbHelper.getReadableDatabase();
				this.copyDataBase();
				dbCreated = true;
			}
			this.openDatabase(use);
			
			if(!dbCreated)
				checkAndUpdateDB();

		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public boolean checkDataBase() {

		File dbFile = new File(dbPath + dbName);
		return dbFile.exists();
	}

	public void copyDataBase() throws IOException {

		try {
			InputStream input = context.getAssets().open(dbName);
			String outPutFileName = dbPath + dbName;
			OutputStream output = new FileOutputStream(outPutFileName);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			output.flush();
			output.close();
			input.close();

		} catch (IOException e) {
			Log.e("RBI","debug",e);
		}
	}
}
