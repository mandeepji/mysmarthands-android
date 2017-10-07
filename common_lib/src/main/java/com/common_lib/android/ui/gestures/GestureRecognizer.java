package com.common_lib.android.ui.gestures;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;


public class GestureRecognizer extends SimpleOnGestureListener {

	public int flingMinDistance = 120;
	public int flingMaxOffPath = 250;
	public int flingVelocityThreshold = 200;

	GestureListener listener;
	GestureDetector detector;

	public GestureRecognizer(Context context, GestureListener listener) {

		detector = new GestureDetector(context, this);
		this.listener = listener;
	}

	public boolean onTouchEvent(MotionEvent ev) {

		return detector.onTouchEvent(ev);
	}

	// -----------------------------------------------+
	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {

		return listener.onSingleTap();
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {

		
		return listener.onDoubleTap();
	}

	@Override
	public void onLongPress(MotionEvent e) {

		listener.onLongPress();
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		try {
			if (Math.abs(e1.getY() - e2.getY()) > flingMaxOffPath) {
				return false;
			}
			// Left Swipe
			if (e1.getX() - e2.getX() > flingMinDistance
					&& Math.abs(velocityX) > flingVelocityThreshold) {
				listener.onLeftSwype();
			}
			// right swype
			else if (e2.getX() - e1.getX() > flingMinDistance
					&& Math.abs(velocityX) > flingVelocityThreshold) {
				listener.onRightSwype();
			}
		} catch (Exception e) {
			// nothing
		}
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
	
		// has to return true or everything breaks
		return true;
	}
	// -----------------------------------------------+
	public interface GestureListener{
		
		public boolean onLeftSwype();
		public boolean onRightSwype();
		public boolean onSingleTap();
		public boolean onDoubleTap();
		public void onLongPress();
	}
	
	// -----------------------------------------------+
}
