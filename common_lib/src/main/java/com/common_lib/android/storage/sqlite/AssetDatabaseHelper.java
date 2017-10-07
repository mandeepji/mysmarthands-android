package com.common_lib.android.storage.sqlite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.common_lib.android.storage.StorageHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AssetDatabaseHelper extends SQLiteOpenHelper{
 
    protected SQLiteDatabase database; 
    protected final Context context;
    
    protected String dbPath;
    protected String dbName;
 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public AssetDatabaseHelper(Context context,String dbName) {
 
    	super(context, dbName, null, 1);
    	this.dbPath = StorageHelper.getAppFilesDir(context)+"/databases/";
        this.dbName = dbName;
    	this.context = context;
    }	
 
    public AssetDatabaseHelper(Context context,String dbName,String dbPath) {
    	 
    	super(context, dbName, null, 1);
        this.dbName = dbName;
        this.dbPath = dbPath;
    	this.context = context;
    }
    
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//do nothing - database already exist
    	}else{
 
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
 
        	try {
 
    			copyDatabase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = dbPath + dbName;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDatabase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = context.getAssets().open(dbName);
 
    	// Path to the just created empty db
    	String outFileName = dbPath + dbName;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDatabase() throws SQLException{
 
    	//Open the database
        String fullPath = dbPath + dbName;
    	database = SQLiteDatabase.openDatabase(fullPath, null, 
    					SQLiteDatabase.OPEN_READWRITE);
 
    }
 
    protected void openIfNotOpen() {
		
    	if (database == null || !database.isOpen())
			this.openDatabase();
	}
    
    @Override
	public synchronized void close() {
 
    	    if(database != null)
    		    database.close();
 
    	    super.close();
 
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	} 

}
