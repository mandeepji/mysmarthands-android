package com.common_lib.android.ui.gestures;

import android.graphics.PointF;

public class Finger {

	PointF pos;
	PointF delta;
	
	public Finger(float x,float y){
		
		pos = new PointF(x, y);
		delta = new PointF();
	}
	
	public void update(float x,float y){
		
		delta.x = x - pos.x;
		delta.y = y - pos.y;
		pos.x = x;
		pos.y = y;
	}
	
	public float x(){return pos.x;}
	
	public float y(){return pos.y;}

	public float xD(){return delta.x;}
	
	public float yD(){return delta.y;}
}
