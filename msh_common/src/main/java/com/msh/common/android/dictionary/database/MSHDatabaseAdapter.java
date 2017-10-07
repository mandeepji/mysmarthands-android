package com.msh.common.android.dictionary.database;

import com.msh.common.android.dictionary.Constants;
import com.msh.common.android.dictionary.AppInstance;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class MSHDatabaseAdapter extends DatabaseAdapter {

	// force video update on app update
	protected static final boolean VIDEO_UPDATE_REQUIRED = false;
	
	private static MSHDatabaseAdapter databaseAdapter = null;

	public static String NAME = "name";
	public static String CATEGORY = "cat";
	public static String FAV = "fav";
	public static String VID_EXT = ".mp4";
	//public static String VID_EXT = ".3gp";
	//public static Uri URI = Uri.parse(dbPath + dbName);
	
	public String WORD_DEVIDE = "/";

	public static MSHDatabaseAdapter getInstance() {

		if (databaseAdapter == null) {
			String dbName = null;
			if(Constants.getBoolean(Constants.CONST_KEY_IAP_USES_IAP)){
				if( AppInstance.fullVersionPurchased() || Constants.OVERRIDE_IAP){
					dbName = Constants.getString(Constants.CONST_KEY_IAP_FULL_DB);
				}
				else{
					dbName = Constants.getString(Constants.CONST_KEY_IAP_LITE_DB);
				}
			}
			else{
				dbName = Constants.getString(Constants.CONST_KEY_NO_IAP_DB);
			}
			databaseAdapter = new MSHDatabaseAdapter(
					AppInstance.getContext(),
					dbName
				);
			databaseAdapter.checkAndCopyDB(SQLiteDatabase.OPEN_READWRITE);
		}

		return databaseAdapter;
	}

	public static MSHDatabaseAdapter resetInstance(){
		
		databaseAdapter = null;
		return MSHDatabaseAdapter.getInstance();
	}
	
	private MSHDatabaseAdapter(Context context,String dbName) {

		super(context,dbName);
	}
	
	@Override
	public void close() {

		super.close();
		MSHDatabaseAdapter.databaseAdapter = null;
	}
	
	public boolean videoUpdateRequiredOnAppUpdated(){
		
		//Log.d("RBI", appUpdated+"");
		return (this.appUpdated && VIDEO_UPDATE_REQUIRED);
	}
	
	//-------------------------------------------------------+
	// Meta-data
	public String getUpdateURL() {

		Cursor c = dbObj.rawQuery("SELECT value FROM app_metadata "+
				"where functionName = 'updateCheck';", new String[0]);

		c.moveToFirst();
		String ret = c.getString(0);
		c.close();
		return ret;
	}
	
	public String getVideoURL() {

		Cursor c = dbObj.rawQuery("SELECT value FROM app_metadata "+
				"where functionName = 'videoSourceGet';", new String[0]);

		
		c.moveToFirst();
		String ret = c.getString(0);
		c.close();
		return ret;
	}
	
	public boolean isVersionValid(String ver){
		
		Cursor c = dbObj.rawQuery("SELECT value FROM app_metadata "+
				"where functionName = 'updateVersion';", new String[0]);

		c.moveToFirst();
		String ret = c.getString(0);
		c.close();
		
		//Log.d("RBI",ret);
		
		return  ret.compareTo(ver) == 0;
	}
	
	public void updateVersion(String ver){
		
		dbObj.execSQL("UPDATE app_metadata SET value = '"+ver+
				"' WHERE functionName = 'updateVersion';");
		
	}
	
	public boolean isFullEdition(){
		
		Cursor c = dbObj.rawQuery("SELECT value FROM app_metadata "+
				"where functionName = 'edition';", new String[0]);

		c.moveToFirst();
		String ret = c.getString(0);
		c.close();
		
		return ( ret.compareToIgnoreCase("full") == 0 );
	}
	
	public String getFullURL(){
		
		Cursor c = dbObj.rawQuery("SELECT value FROM app_metadata "+
				"where functionName = 'fullLink';", new String[0]);

		c.moveToFirst();
		String ret = c.getString(0);
		c.close();
		
		return ret;
	}

	public String getStorageDirValue(){
		
		Cursor c = dbObj.rawQuery("SELECT value FROM app_metadata "+
				"where functionName = 'storageDir';", new String[0]);

		c.moveToFirst();
		String ret = c.getString(0);
		c.close();
		
		return ret;
	}
	
	public void updateStorageDirValue(String dir){
		
		dbObj.execSQL("UPDATE app_metadata SET value = '"+dir+
				"' WHERE functionName = 'storageDir';");
	}

	public boolean isQuizVidsSeperate(){
		
		Cursor c = dbObj.rawQuery("SELECT value FROM app_metadata "+
				"where functionName = 'isQuizSeperate';", new String[0]);

		c.moveToFirst();
		String ret = c.getString(0);
		c.close();
		
		return ret.equalsIgnoreCase("true");
	}
	
	//-------------------------------------------------------+
	// Data
	public Cursor allVideoNames() {
		
		this.openIfNotOpen(SQLiteDatabase.OPEN_READWRITE);

		return dbObj.rawQuery("SELECT name AS _id FROM videoTable",
				new String[0]);
	}

	public Cursor allVideoNamesandFavValues() {
		
		this.openIfNotOpen(SQLiteDatabase.OPEN_READWRITE);

		return dbObj
				.rawQuery(
						"SELECT name as _id,1 as fav FROM catTable WHERE cat = 'Favorites' "
								+ "UNION SELECT name,0 FROM videoTable "
								+ "WHERE name NOT IN "
								+ "(SELECT name FROM catTable WHERE cat = 'Favorites');",
						new String[0]);
	}

	public Cursor videoNamesForCategory(String cat) {
		
		this.openIfNotOpen(SQLiteDatabase.OPEN_READWRITE);

		cat = cat.replaceAll("'","''");
		
		return dbObj.rawQuery("SELECT name As _id FROM catTable WHERE cat = '"
				+ cat + "' ORDER BY name", new String[0]);
	}

	public Cursor favoritesWithValues() {
		
		this.openIfNotOpen(SQLiteDatabase.OPEN_READWRITE);

		return dbObj.rawQuery("SELECT name As _id,1 FROM catTable "
				+ "WHERE cat = 'Favorites' ORDER BY name", new String[0]);
	}

	public Cursor videoNamesForCategoryWithFavValues(String cat) {
		
		this.openIfNotOpen(SQLiteDatabase.OPEN_READWRITE);

		return dbObj
				.rawQuery(
						"SELECT name as _id,1 as fav FROM catTable WHERE cat = '"
								+ cat
								+ "' AND name IN ( SELECT name FROM catTable WHERE cat = 'Favorites')"
								+ " UNION SELECT name,0 FROM catTable WHERE cat = '"
								+ cat
								+ "' AND name NOT IN (SELECT name FROM catTable WHERE cat = 'Favorites');",
						new String[0]);
	}

	public Cursor AllCategoriesLimited() {
		
		this.openIfNotOpen(SQLiteDatabase.OPEN_READWRITE);

		return dbObj
				.rawQuery(
						"SELECT cat,COUNT(*) AS _id FROM catTable GROUP BY cat HAVING COUNT(*) > 4 ORDER BY cat",
						new String[0]);
	}
	
	public Cursor AllCategories() {
		
		this.openIfNotOpen(SQLiteDatabase.OPEN_READWRITE);

		return dbObj
				.rawQuery(
						"SELECT cat,COUNT(*) AS _id FROM catTable GROUP BY cat HAVING COUNT(*) > 0 ORDER BY cat",
						new String[0]);
	}
	
	public Cursor AllCategoriesNoAll() {
		
		this.openIfNotOpen(SQLiteDatabase.OPEN_READWRITE);

		return dbObj
				.rawQuery(
						"SELECT cat,COUNT(*) AS _id FROM catTable WHERE cat <> 'All Categories' GROUP BY cat HAVING COUNT(*) > 0",
						new String[0]);
	}

	public Cursor videoNamesStartingWith(String sub) {
		
		this.openIfNotOpen(SQLiteDatabase.OPEN_READWRITE);
		
		sub = sub.replaceAll("'","''");
		
		return dbObj.rawQuery(
				"SELECT name As _id FROM videoTable WHERE name LIKE '" + sub
						+ "%' OR name LIKE '%"+WORD_DEVIDE+sub+"%'", new String[0]);
	}

	public Cursor videoNamesContaining(String sub) {
		
		this.openIfNotOpen(SQLiteDatabase.OPEN_READWRITE);

		sub = sub.replaceAll("'","''");
		
		return dbObj.rawQuery(
				"SELECT name As _id FROM videoTable WHERE name LIKE '%" + sub
						+ "%'", new String[0]);
	}

	public void setFavorite(String name) {
		
		this.openIfNotOpen(SQLiteDatabase.OPEN_READWRITE);

		name = name.replaceAll("'","''");
		
		dbObj.execSQL("INSERT INTO catTable VALUES ('Favorites','" + name
				+ "')");
	}

	public void removeFavorite(String name) {
		
		this.openIfNotOpen(SQLiteDatabase.OPEN_READWRITE);

		name = name.replaceAll("'","''");
		
		dbObj.execSQL("DELETE FROM catTable WHERE cat='Favorites' AND name='"
				+ name + "'");
	}

	public int quizStartTime(String name) {
		
		this.openIfNotOpen(SQLiteDatabase.OPEN_READWRITE);
		
		name = name.replaceAll("'","''");

		Cursor c = dbObj.rawQuery("SELECT start FROM videoTable WHERE name='"
				+ name + "'", new String[0]);

		c.moveToFirst();
		int ret = c.getInt(0);
		c.close();
		return ret;
	}
	
	public String getFirstVid() {
		
		this.openIfNotOpen(SQLiteDatabase.OPEN_READWRITE);

		Cursor c = dbObj.rawQuery("SELECT * FROM videoTable", new String[0]);

		c.moveToFirst();
		// IF quality=low move to next
		String ret = c.getString(0);
		c.close();
		return ret;
	}

	//-------------------------------------------------------+
}
