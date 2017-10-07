package com.common_lib.android.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class FragmentHelper {

	public static FragmentTransaction addFragment(FragmentActivity activity,Fragment frag,int resID){
		
		FragmentTransaction ft = activity.getSupportFragmentManager()
				.beginTransaction();
		ft.add(resID,frag);
		ft.commit();
		
		return ft;
	}
	
	public static FragmentTransaction replaceFragment(FragmentActivity activity,Fragment frag,int resID){
		
		FragmentTransaction ft = activity.getSupportFragmentManager()
				.beginTransaction();
		ft.replace(resID,frag);
		ft.commit();
		
		return ft;
	}
	
	public static FragmentTransaction removeFragment(FragmentActivity activity,Fragment frag){
		
		FragmentTransaction ft = activity.getSupportFragmentManager()
				.beginTransaction();
		ft.remove(frag);
		ft.commit();
		
		return ft;
	}

	//---------------------------------------------+
	@SuppressLint("ValidFragment")
	public static SupportMapFragment addNewMapFragment(Fragment parent,int resID,final SupportMapDelegate delegate){
		
		SupportMapFragment supportMapFrag = new SupportMapFragment() {
            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                delegate.mapReady(this.getMap());
            }
        };
	    		
		FragmentTransaction fragmentTransaction =
	             parent.getChildFragmentManager().beginTransaction();
	     fragmentTransaction.add(resID, supportMapFrag);
	     fragmentTransaction.commit();
	     
	     return supportMapFrag;
	}
	
	public interface SupportMapDelegate{
		
		public void mapReady(GoogleMap map);
	}
	
	//---------------------------------------------+
}
