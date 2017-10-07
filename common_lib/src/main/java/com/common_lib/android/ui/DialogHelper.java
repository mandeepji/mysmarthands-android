package com.common_lib.android.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;

import java.util.List;

public class DialogHelper {
	
	public static AlertDialog simpleAlertDialog(Context context,
			String title,String msg){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setMessage(msg)
		       .setTitle(title)
		       .setNeutralButton("OK", null);

		AlertDialog dialog = builder.create();
		
		return dialog;
	}
	
	public static AlertDialog simpleAlertDialog(Context context,
			String title,String msg,OnClickListener listener){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setMessage(msg)
		       .setTitle(title)
		       .setNeutralButton("OK", listener);

		AlertDialog dialog = builder.create();
		
		return dialog;
	}
	
	public static AlertDialog simpleAlertDialog(Context context,
			String title,String msg,
			String posBtn,String negBtn,
			OnClickListener listener){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(title)
		       .setMessage(msg);

		if(posBtn !=null){
			builder.setPositiveButton(posBtn, listener);
		}
		if(negBtn !=null){
			builder.setNegativeButton(negBtn, listener);
		}
		AlertDialog dialog = builder.create();
	
		return dialog;
	}

	public static AlertDialog simpleAlertDialog(Context context,
			String title,String msg,
			String posBtn,String neuBtn,String negBtn,
			OnClickListener listener){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(title)
		       .setMessage(msg);

		if(posBtn !=null){
			builder.setPositiveButton(posBtn, listener);
		}
		if(neuBtn !=null){
			builder.setNeutralButton(neuBtn, listener);
		}
		if(negBtn !=null){
			builder.setNegativeButton(negBtn, listener);
		}
		AlertDialog dialog = builder.create();
	
		return dialog;
	}
	
	public static AlertDialog simpleListDialog(Context context,
			String title,String[] items,OnClickListener listener){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		builder.setTitle(title)
		       .setItems(items,listener);
		
		return builder.create();
	}
	
	public static AlertDialog simpleListDialog(Context context,
			String title,List<String> items,OnClickListener listener){
		
		String[] itms = new String[items.size()];
		for (int i=0 ;i<items.size() ;++i) {
			itms[i] = items.get(i);
		}
		
		return simpleListDialog(context,title,itms,listener);
	}

	
	//----------------------------------------------------------+
	// move this later!
	public static void goToAppStoreListing(Context context,String appPackageName,boolean googleTAmazonF){
		
		String store,http;
		if(googleTAmazonF){
			store = "market://details?id=";
			http = "http://play.google.com/store/apps/details?id=";
		}
		else{
			// p= (package)
			// s= (search_term)
			// asin= (category ASIN)
			// showAll=1 (all apps by developer)
			store = "amzn://apps/android?p=";
			http = "http://www.amazon.com/gp/mas/dl/android?p=";
		}
		goToAppStoreListing(context, appPackageName, store, http);
	}
	
	public static void goToAppStoreListing(Context context,String appPackageName,String storeLink,String httpLink){
		
		try {
			context.startActivity(new Intent(Intent.ACTION_VIEW, 
		    		Uri.parse(storeLink + appPackageName)));
		} 
		catch (android.content.ActivityNotFoundException anfe) {
			context.startActivity(new Intent(
		    		Intent.ACTION_VIEW, 
		    		Uri.parse(httpLink + appPackageName)));
		}
	}
	
	//----------------------------------------------------------+
}
