package com.msh.common.android.dictionary;

import android.content.Context;
import java.util.HashMap;

public class Constants {

    // System Testing
    // for test activities see ExpansionDownloadActivity.validationSuccessful()
    public static final boolean DEBUG                       = true;
    public static final String TAG                          = "MSH";

    public static final int TESTING_ACTIVITY 				= 0; // 0 = NO_TEST
    public static final boolean OVERRIDE_IAP 			= false; // make sure false when not testing

    // COMMON
//	public static final int VALIDATION_INTERVAL 			= 100; // removed
    public static final boolean VIDEO_CONTENT_BUNDLED 		= false; // do not use - make false
    public static final String LAUNCH_COUNTER_KEY 			= "launchCount";
    public static final int UPGRADE_REMINDER_INTERVAL		= 15;

    //Amazon publishing
    public static final boolean USE_AMAZON_IAP				= true;
    public static final boolean USE_EXTERNAL_XAPK_SOURCE	= true;
    public static final String EXTERNAL_XAPK_SOURCE_URL		= "http://mysmarthands.com/androidr/xapks/";
//	public static final String EXTERNAL_XAPK_SOURCE_URL		= "http://192.168.2.91/nancy/rbi/dev/msh/xapks/";

    private static String PKG_NAME = null;

    public static void init(Context context)
    {
        PKG_NAME = context.getApplicationContext().getPackageName();
    }

    public static String getString(String key) {
        return (String) CONSTANTS.get(PKG_NAME + key);
    }

    public static Integer getInteger(String key) {
        return (Integer) CONSTANTS.get(PKG_NAME + key);
    }

    public static Long getLong(String key) {
        return (Long) CONSTANTS.get(PKG_NAME + key);
    }

    public static Boolean getBoolean(String key) {
        return (Boolean) CONSTANTS.get(PKG_NAME + key);
    }

    private static HashMap<String, Object> CONSTANTS        = new HashMap<String, Object>();

    private static String PKG_v1Lite                        = "com.rbi.msh_dictionary_lite";
    private static String PKG_v1Full                        = "com.rbi.msh_dictionary";
    private static String PKG_v2Full                        = "com.rbi.msh_dictionary_v2";
    private static String PKG_Anim                          = "com.rbi.msh_dictionary_anim";

    public static String CONST_KEY_XAPK_PUBLIC_KEY         = "XAPK_PUBLIC_KEY";
    public static String CONST_KEY_XAPK_MAIN_VERSION       = "XAPK_MAIN_VERSION";
    public static String CONST_KEY_XAPK_MAIN_SIZE          = "XAPK_MAIN_SIZE";
    public static String CONST_KEY_XAPK_PROVIDER_AUTHORITY	= "XAPK_PROVIDER_AUTHORITY";
    public static String CONST_KEY_VIDEO_FORMAT_EXT 		= "VIDEO_FORMAT_EXT";
    public static String CONST_KEY_USES_WORD_IMAGERY		= "USES_WORD_IMAGERY";
    public static String CONST_KEY_USES_KEYBOARD 			= "USES_KEYBOARD";
    public static String CONST_KEY_SONG_VIEW_ENABLED		= "SONG_VIEW_ENABLED";
    public static String CONST_KEY_IAP_USES_IAP 			= "IAP_USES_IAP";
    public static String CONST_KEY_IAP_REMINDER_MESSAGE	= "IAP_REMINDER_MESSAGE";
    public static String CONST_KEY_PROMO_STORE_LINK		= "PROMO_STORE_LINK";
    public static String CONST_KEY_IAP_SKU_FULL_UNLOCK 	= "IAP_SKU_FULL_UNLOCK";
    public static String CONST_KEY_IAP_FULL_DB 			= "IAP_FULL_DB";
    public static String CONST_KEY_IAP_LITE_DB 			= "IAP_LITE_DB";
    public static String CONST_KEY_NO_IAP_DB 				= "NO_IAP_DB";
    public static String CONST_KEY_USES_ADMOB				= "USES_ADMOB";
    public static String CONST_KEY_ADMOB_HOME_ADID			= "ADMOB_HOME_ADID";

    static
    {
        // v1Lite
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_XAPK_PUBLIC_KEY, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkVwNfSTFD4l+z8T4FO4ZCVx7/WIaKcv5P1ZDGvHMW4aG30G4OPojADZwTAtGL48JWBnQNUugiWF5/aZQBBTJsQfXaiDpsO+NOeItTVrqT4RyIejpMtVovxj79NhasF8mFkHC/LOJb20vrbl9NLQjivlP1gB37dxhLnZNhVoK5xFYB9q0KWQSzbDFh5p7ETBZ9STD88AYGjMj2BirfNu0XRtU2+OaTPYjXNN+/MIYFgjokri9B/crcq+MgayedPMV3j2SYMRnTpxfVlhhc3Pcds2mqn/3WrLs9svFORmzj4GuUoH4Urpitj4Mp5xAAmJ0cHxYyBw9pQY4z1IZl3qd0QIDAQAB");
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_XAPK_MAIN_VERSION, 16); // change on provider authority in manifest as well
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_XAPK_MAIN_SIZE, 127358052L);
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_XAPK_PROVIDER_AUTHORITY, "com.msh.android.dictionary.lite.ExpansionFileProvider"); // must match provider in manifest (<app_package_id>.ExpansionFileProvider)
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_VIDEO_FORMAT_EXT, ".mp4");
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_USES_WORD_IMAGERY, false);
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_USES_KEYBOARD, false);
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_SONG_VIEW_ENABLED, false);
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_IAP_USES_IAP, false);
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_IAP_REMINDER_MESSAGE, "You can get the full version with more signs and no ads from the Amazon and Google Play Stores");
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_PROMO_STORE_LINK, "com.rbi.msh_dictionary");
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_IAP_SKU_FULL_UNLOCK, "");
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_IAP_FULL_DB, "");
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_IAP_LITE_DB, "");
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_NO_IAP_DB, "mshDBv1Lite.sqlite");
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_USES_ADMOB, true);
        CONSTANTS.put(PKG_v1Lite + CONST_KEY_ADMOB_HOME_ADID, "ca-app-pub-6201275958966699/1535266903");

        // v1full
        CONSTANTS.put(PKG_v1Full + CONST_KEY_XAPK_PUBLIC_KEY, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkVwNfSTFD4l+z8T4FO4ZCVx7/WIaKcv5P1ZDGvHMW4aG30G4OPojADZwTAtGL48JWBnQNUugiWF5/aZQBBTJsQfXaiDpsO+NOeItTVrqT4RyIejpMtVovxj79NhasF8mFkHC/LOJb20vrbl9NLQjivlP1gB37dxhLnZNhVoK5xFYB9q0KWQSzbDFh5p7ETBZ9STD88AYGjMj2BirfNu0XRtU2+OaTPYjXNN+/MIYFgjokri9B/crcq+MgayedPMV3j2SYMRnTpxfVlhhc3Pcds2mqn/3WrLs9svFORmzj4GuUoH4Urpitj4Mp5xAAmJ0cHxYyBw9pQY4z1IZl3qd0QIDAQAB");
        CONSTANTS.put(PKG_v1Full + CONST_KEY_XAPK_MAIN_VERSION, 17); // change on provider authority in manifest as well
        CONSTANTS.put(PKG_v1Full + CONST_KEY_XAPK_MAIN_SIZE, 127358052L);
        CONSTANTS.put(PKG_v1Full + CONST_KEY_XAPK_PROVIDER_AUTHORITY, "com.msh.android.dictionary.ExpansionFileProvider"); // must match provider def in manifest
        CONSTANTS.put(PKG_v1Full + CONST_KEY_VIDEO_FORMAT_EXT, ".mp4");
        CONSTANTS.put(PKG_v1Full + CONST_KEY_USES_WORD_IMAGERY, false);
        CONSTANTS.put(PKG_v1Full + CONST_KEY_USES_KEYBOARD, false);
        CONSTANTS.put(PKG_v1Full + CONST_KEY_SONG_VIEW_ENABLED, false);
        CONSTANTS.put(PKG_v1Full + CONST_KEY_IAP_USES_IAP, false);
        CONSTANTS.put(PKG_v1Full + CONST_KEY_IAP_REMINDER_MESSAGE, "");
        CONSTANTS.put(PKG_v1Full + CONST_KEY_PROMO_STORE_LINK, "");
        CONSTANTS.put(PKG_v1Full + CONST_KEY_IAP_SKU_FULL_UNLOCK, "");
        CONSTANTS.put(PKG_v1Full + CONST_KEY_IAP_FULL_DB, "");
        CONSTANTS.put(PKG_v1Full + CONST_KEY_IAP_LITE_DB, "");
        CONSTANTS.put(PKG_v1Full + CONST_KEY_NO_IAP_DB, "mshDBv1.sqlite");
        CONSTANTS.put(PKG_v1Full + CONST_KEY_USES_ADMOB, false);
        CONSTANTS.put(PKG_v1Full + CONST_KEY_ADMOB_HOME_ADID, "");

        // v2
        CONSTANTS.put(PKG_v2Full + CONST_KEY_XAPK_PUBLIC_KEY, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtleN3pV9pxaU38AnO4d7mvfQUdwI+RESG8dYjElYNBHI4m3UUE6rq2PGoeHJ8G2aFxnba8UsTGOr+/1bKKhyMjCt/+mSfkexL5w7aa02j51z7uEbe5D7vQDFgI5XHfsDefEB9+SJeUwgNhIINGMpzYJIg6K0uhzB97MvgYpXh+Z4BhqfC+2UwEU5TY1Jc/DycbMA1L3ze48msssL1uWpQw4iE+gEslNktJRcjPxqoa00lCQd/NQxTKX4EwbI6Y33X5Q53zWAY3rok8J/eG3KgvpY4pNVv6TXMg+y0MB8BIyHlRi++hL5xgg2DrJDXp1J7vBb9hEvE8zaBnugf5RxOQIDAQAB");
        CONSTANTS.put(PKG_v2Full + CONST_KEY_XAPK_MAIN_VERSION, 18); // change on provider authority in manifest as well
        CONSTANTS.put(PKG_v2Full + CONST_KEY_XAPK_MAIN_SIZE, 106571825L);
        CONSTANTS.put(PKG_v2Full + CONST_KEY_XAPK_PROVIDER_AUTHORITY, "com.rbi.msh_dictionary_v2.ExpansionFileProvider"); // must match provider def in manifest
        CONSTANTS.put(PKG_v2Full + CONST_KEY_VIDEO_FORMAT_EXT, ".mp4");
        CONSTANTS.put(PKG_v2Full + CONST_KEY_USES_WORD_IMAGERY, false);
        CONSTANTS.put(PKG_v2Full + CONST_KEY_USES_KEYBOARD, false);
        CONSTANTS.put(PKG_v2Full + CONST_KEY_SONG_VIEW_ENABLED, false);
        CONSTANTS.put(PKG_v2Full + CONST_KEY_IAP_USES_IAP, true);
        CONSTANTS.put(PKG_v2Full + CONST_KEY_IAP_REMINDER_MESSAGE, "You can disable the ads and access the full content with a one-time licence purchase made right in the app. See the \"Upgrades\" section under the \"Go To\" menu");
        CONSTANTS.put(PKG_v2Full + CONST_KEY_PROMO_STORE_LINK, "");
        CONSTANTS.put(PKG_v2Full + CONST_KEY_IAP_SKU_FULL_UNLOCK, "com.rbi.msh_dictionary_v2.full_access");
        CONSTANTS.put(PKG_v2Full + CONST_KEY_IAP_FULL_DB, "mshDB2.sqlite");
        CONSTANTS.put(PKG_v2Full + CONST_KEY_IAP_LITE_DB, "mshDB2Lite.sqlite");
        CONSTANTS.put(PKG_v2Full + CONST_KEY_NO_IAP_DB, "");
        CONSTANTS.put(PKG_v2Full + CONST_KEY_USES_ADMOB, true);
        CONSTANTS.put(PKG_v2Full + CONST_KEY_ADMOB_HOME_ADID, "ca-app-pub-6201275958966699/2768751703");

        // animated
        CONSTANTS.put(PKG_Anim + CONST_KEY_XAPK_PUBLIC_KEY, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyq1a99O4wt8H7giryd0eDrC8MRsWBddao27OUhvPofqrUDRccjstRgl1+pL0DyNqChVaQpe10Rgh82G0JdddWvWX6kSBKrbtOYDu8sljDB+gU99I8yncehaUBWDefEpfGGGe1wlA1/EeXYdqcx8fi3xSCMIdQ2SShGTSB30buQuRxuc6wLifmKzknoWVTzVDIWYB1Ebi8eBUTNTkyLItFkNU7BSHsMWrX1OHfQtRf0I2u3XzZD7dfth19tTybWqd5N2wUUkPgwUqQtKxN6Ji5CwaW52kw//Bc4iD4taI9Q1ziKpeHtJL8tUbWC7uW2GVG54hCwmrYlRjeJN1QehbOQIDAQAB");
        CONSTANTS.put(PKG_Anim + CONST_KEY_XAPK_MAIN_VERSION, 4); // change on provider authority in manifest as well
        CONSTANTS.put(PKG_Anim + CONST_KEY_XAPK_MAIN_SIZE, 78822254L);
        CONSTANTS.put(PKG_Anim + CONST_KEY_XAPK_PROVIDER_AUTHORITY, "com.rbi.msh_dictionary_anim.ExpansionFileProvider"); // must match provider def in manifest
        CONSTANTS.put(PKG_Anim + CONST_KEY_VIDEO_FORMAT_EXT, ".mp4");
        CONSTANTS.put(PKG_Anim + CONST_KEY_USES_WORD_IMAGERY, true);
        CONSTANTS.put(PKG_Anim + CONST_KEY_USES_KEYBOARD, true);
        CONSTANTS.put(PKG_Anim + CONST_KEY_SONG_VIEW_ENABLED, true);
        CONSTANTS.put(PKG_Anim + CONST_KEY_IAP_USES_IAP, true);
        CONSTANTS.put(PKG_Anim + CONST_KEY_IAP_REMINDER_MESSAGE, "You can disable the ads and access the additional content with a one-time licence purchase made right in the app. See the \"Upgrades\" section under the \"Go To\" menu");
        CONSTANTS.put(PKG_Anim + CONST_KEY_PROMO_STORE_LINK, "");
        CONSTANTS.put(PKG_Anim + CONST_KEY_IAP_SKU_FULL_UNLOCK, "com.rbi.msh_dictionary_anim.full_access");
        CONSTANTS.put(PKG_Anim + CONST_KEY_IAP_FULL_DB, "mshDBanim.sqlite");
        CONSTANTS.put(PKG_Anim + CONST_KEY_IAP_LITE_DB, "mshDBanim.sqlite");
        CONSTANTS.put(PKG_Anim + CONST_KEY_NO_IAP_DB, "");
        CONSTANTS.put(PKG_Anim + CONST_KEY_USES_ADMOB, true);
        CONSTANTS.put(PKG_Anim + CONST_KEY_ADMOB_HOME_ADID, "ca-app-pub-6201275958966699/2048140908");

    }

/*	// Edit Constants and Manifest per version
	// !!! DON'T FORGET TO CLEAN PROJECT AND COPY R FILE FROM GEN TO _lib FOLDER !!!
	
	// System Testing
	// for test activities see ExpansionDonloadActivity.validationSuccessful()
	public static final int TESTING_ACTIVITY 				= 0; // 0 = NO_TEST
	public static final boolean OVERRIDE_IAP 			= false; // make sure false when not testing
	
	
	// COMMON
//	public static final int VALIDATION_INTERVAL 			= 100; // removed
	public static final boolean VIDEO_CONTENT_BUNDLED 		= false; // do not use - make false
	public static final String LAUNCH_COUNTER_KEY 			= "launchCount";
	public static final int UPGRADE_REMINDER_INTERVAL		= 15;
	
	//Amazon publishing
	public static final boolean USE_AMAZON_IAP				= true;
	public static final boolean USE_EXTERNAL_XAPK_SOURCE	= true;
	public static final String EXTERNAL_XAPK_SOURCE_URL		= "http://mysmarthands.com/androidr/xapks/";
//	public static final String EXTERNAL_XAPK_SOURCE_URL		= "http://192.168.2.91/nancy/rbi/dev/msh/xapks/";
	
	// v1Lite
	public static final String XAPK_PUBLIC_KEY			= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkVwNfSTFD4l+z8T4FO4ZCVx7/WIaKcv5P1ZDGvHMW4aG30G4OPojADZwTAtGL48JWBnQNUugiWF5/aZQBBTJsQfXaiDpsO+NOeItTVrqT4RyIejpMtVovxj79NhasF8mFkHC/LOJb20vrbl9NLQjivlP1gB37dxhLnZNhVoK5xFYB9q0KWQSzbDFh5p7ETBZ9STD88AYGjMj2BirfNu0XRtU2+OaTPYjXNN+/MIYFgjokri9B/crcq+MgayedPMV3j2SYMRnTpxfVlhhc3Pcds2mqn/3WrLs9svFORmzj4GuUoH4Urpitj4Mp5xAAmJ0cHxYyBw9pQY4z1IZl3qd0QIDAQAB";
	public static final int XAPK_MAIN_VERSION			= 16; // change on provider authority in manifest as well
	public static final long XAPK_MAIN_SIZE				= 127358052L;
	public static final String XAPK_PROVIDER_AUTHORITY	= "com.msh.android.dictionary.lite.ExpansionFileProvider"; // must match provider in manifest (<app_package_id>.ExpansionFileProvider)
	public static final String VIDEO_FORMAT_EXT 		= ".mp4";
	public static final boolean USES_WORD_IMAGERY		= false;
	public static final boolean USES_KEYBOARD 			= false;
	public static final boolean SONG_VIEW_ENABLED		= false;
	public static final boolean IAP_USES_IAP 			= false;
	public static final String IAP_REMINDER_MESSAGE		= "You can get the full version with more signs and no ads from the Amazon and Google Play Stores";
	public static final String PROMO_STORE_LINK			= "com.rbi.msh_dictionary";
	public static final String IAP_SKU_FULL_UNLOCK 		= "";
	public static final String IAP_FULL_DB 				= "";
	public static final String IAP_LITE_DB 				= "";
	public static final String NO_IAP_DB 				= "mshDBv1Lite.sqlite";
	public static final boolean USES_ADMOB				= true;
	public static final String ADMOB_HOME_ADID			= "ca-app-pub-6201275958966699/1535266903";
	
	
	// v1full
//	public static final String XAPK_PUBLIC_KEY				= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkVwNfSTFD4l+z8T4FO4ZCVx7/WIaKcv5P1ZDGvHMW4aG30G4OPojADZwTAtGL48JWBnQNUugiWF5/aZQBBTJsQfXaiDpsO+NOeItTVrqT4RyIejpMtVovxj79NhasF8mFkHC/LOJb20vrbl9NLQjivlP1gB37dxhLnZNhVoK5xFYB9q0KWQSzbDFh5p7ETBZ9STD88AYGjMj2BirfNu0XRtU2+OaTPYjXNN+/MIYFgjokri9B/crcq+MgayedPMV3j2SYMRnTpxfVlhhc3Pcds2mqn/3WrLs9svFORmzj4GuUoH4Urpitj4Mp5xAAmJ0cHxYyBw9pQY4z1IZl3qd0QIDAQAB";
//	public static final int XAPK_MAIN_VERSION				= 17; // change on provider authority in manifest as well
//	public static final long XAPK_MAIN_SIZE					= 127358052L;
//	public static final String XAPK_PROVIDER_AUTHORITY		= "com.rbi.msh_dictionary.ExpansionFileProvider"; // must match provider def in manifest
//	public static final String VIDEO_FORMAT_EXT 			= ".mp4";
//	public static final boolean USES_WORD_IMAGERY			= false;
//	public static final boolean USES_KEYBOARD 				= false;
//	public static final boolean SONG_VIEW_ENABLED			= false;
//	public static final boolean IAP_USES_IAP 				= false;
//	public static final String IAP_REMINDER_MESSAGE			= "";
//	public static final String PROMO_STORE_LINK				= "";
//	public static final String IAP_SKU_FULL_UNLOCK 			= "";
//	public static final String IAP_FULL_DB 					= "";
//	public static final String IAP_LITE_DB 					= "";
//	public static final String NO_IAP_DB 					= "mshDBv1.sqlite";
//	public static final boolean USES_ADMOB					= false;
//	public static final String ADMOB_HOME_ADID				= "";
	
	
	// v2
//	public static final String XAPK_PUBLIC_KEY			= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtleN3pV9pxaU38AnO4d7mvfQUdwI+RESG8dYjElYNBHI4m3UUE6rq2PGoeHJ8G2aFxnba8UsTGOr+/1bKKhyMjCt/+mSfkexL5w7aa02j51z7uEbe5D7vQDFgI5XHfsDefEB9+SJeUwgNhIINGMpzYJIg6K0uhzB97MvgYpXh+Z4BhqfC+2UwEU5TY1Jc/DycbMA1L3ze48msssL1uWpQw4iE+gEslNktJRcjPxqoa00lCQd/NQxTKX4EwbI6Y33X5Q53zWAY3rok8J/eG3KgvpY4pNVv6TXMg+y0MB8BIyHlRi++hL5xgg2DrJDXp1J7vBb9hEvE8zaBnugf5RxOQIDAQAB";
//	public static final int XAPK_MAIN_VERSION			= 18; // change on provider authority in manifest as well
//	public static final long XAPK_MAIN_SIZE				= 106571825L;
//	public static final String XAPK_PROVIDER_AUTHORITY	= "com.rbi.msh_dictionary_v2.ExpansionFileProvider"; // must match provider def in manifest
//	public static final String VIDEO_FORMAT_EXT 		= ".mp4";
//	public static final boolean USES_WORD_IMAGERY		= false;
//	public static final boolean USES_KEYBOARD 			= false;
//	public static final boolean SONG_VIEW_ENABLED		= false;
//	public static final boolean IAP_USES_IAP 			= true;
//	public static final String IAP_REMINDER_MESSAGE 	= "You can disable the ads and access the full content with a one-time licence purchase made right in the app. See the \"Upgrades\" section under the \"Go To\" menu";
//	public static final String PROMO_STORE_LINK			= "";
//	public static final String IAP_SKU_FULL_UNLOCK 		= "com.rbi.msh_dictionary_v2.full_access";
//	public static final String IAP_FULL_DB 				= "mshDB2.sqlite";
//	public static final String IAP_LITE_DB 				= "mshDB2Lite.sqlite";
//	public static final String NO_IAP_DB 				= "";
//	public static final boolean USES_ADMOB				= true;
//	public static final String ADMOB_HOME_ADID			= "ca-app-pub-6201275958966699/2768751703";
	
	
	// animated
//	public static final String XAPK_PUBLIC_KEY			= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyq1a99O4wt8H7giryd0eDrC8MRsWBddao27OUhvPofqrUDRccjstRgl1+pL0DyNqChVaQpe10Rgh82G0JdddWvWX6kSBKrbtOYDu8sljDB+gU99I8yncehaUBWDefEpfGGGe1wlA1/EeXYdqcx8fi3xSCMIdQ2SShGTSB30buQuRxuc6wLifmKzknoWVTzVDIWYB1Ebi8eBUTNTkyLItFkNU7BSHsMWrX1OHfQtRf0I2u3XzZD7dfth19tTybWqd5N2wUUkPgwUqQtKxN6Ji5CwaW52kw//Bc4iD4taI9Q1ziKpeHtJL8tUbWC7uW2GVG54hCwmrYlRjeJN1QehbOQIDAQAB";
//	public static final int XAPK_MAIN_VERSION			= 4; // change on provider authority in manifest as well
//	public static final long XAPK_MAIN_SIZE				= 78822254L;
//	public static final String XAPK_PROVIDER_AUTHORITY	= "com.rbi.msh_dictionary_anim.ExpansionFileProvider"; // must match provider def in manifest
//	public static final String VIDEO_FORMAT_EXT 		= ".mp4";
//	public static final boolean USES_WORD_IMAGERY		= true;
//	public static final boolean USES_KEYBOARD 			= true;
//	public static final boolean SONG_VIEW_ENABLED		= true;
//	public static final boolean IAP_USES_IAP 			= true;
//	public static final String IAP_REMINDER_MESSAGE 	= "You can disable the ads and access the additional content with a one-time licence purchase made right in the app. See the \"Upgrades\" section under the \"Go To\" menu";
//	public static final String PROMO_STORE_LINK			= "";
//	public static final String IAP_SKU_FULL_UNLOCK 		= "com.rbi.msh_dictionary_anim.full_access";
//	public static final String IAP_FULL_DB 				= "mshDBanim.sqlite";
//	public static final String IAP_LITE_DB 				= "mshDBanim.sqlite";
//	public static final String NO_IAP_DB 				= "";
//	public static final boolean USES_ADMOB				= true;
//	public static final String ADMOB_HOME_ADID			= "ca-app-pub-6201275958966699/2048140908";
*/
}
