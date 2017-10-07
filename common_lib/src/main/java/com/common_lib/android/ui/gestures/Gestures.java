package com.common_lib.android.ui.gestures;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.SparseArray;
import android.view.MotionEvent;


public class Gestures {

	SparseArray<Finger> fingers;
	

	// testing
	private Paint touchTest;
	private int[] colors = { Color.BLUE, Color.GREEN, Color.MAGENTA,
			Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
			Color.LTGRAY, Color.YELLOW };

	public Gestures() {

		fingers = new SparseArray<Finger>(5);
		touchTest = new Paint(Paint.ANTI_ALIAS_FLAG);
		// set painter color to a color you like
		touchTest.setColor(Color.BLUE);
		touchTest.setStyle(Paint.Style.FILL_AND_STROKE);
	}

	public void trackSypes(MotionEvent event) {

		Finger f;
		int pointerIndex = event.getActionIndex();
		int pointerId = event.getPointerId(pointerIndex);
//		int fingerCount = event.getPointerCount();
		// Log.d("RBI",count+ "ps");

		switch (event.getActionMasked()) {

		// new Finger
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
//			 Log.d("RBI","down");
			f = new Finger(
					event.getX(pointerIndex),
					event.getY(pointerIndex)
				);
			fingers.put(pointerId, f);
			break;

		// Finger Lifted
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
//			 Log.d("RBI","up");
			// !! ---------- add update here where abstract method will get start,end... see if what motion events has
			fingers.remove(pointerId);
			break;

		// fingers Moved
		case MotionEvent.ACTION_MOVE:
//			 Log.d("RBI", "move");
//			update(event);
			break;

		// others
		case MotionEvent.ACTION_CANCEL:
			// Log.d("RBI", "cancel");
			break;
		default:
			// Log.d("RBI", "Default");
			break;
		}
	}
	
	public void trackTouches(MotionEvent event) {

		Finger f;
		int pointerIndex = event.getActionIndex();
		int pointerId = event.getPointerId(pointerIndex);
//		int fingerCount = event.getPointerCount();
		// Log.d("RBI",count+ "ps");

		switch (event.getActionMasked()) {

		// new Finger
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
//			 Log.d("RBI","down");
			f = new Finger(
					event.getX(pointerIndex),
					event.getY(pointerIndex)
				);
			fingers.put(pointerId, f);
			break;

		// Finger Lifted
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
//			 Log.d("RBI","up");
			fingers.remove(pointerId);
			break;

		// fingers Moved
		case MotionEvent.ACTION_MOVE:
//			 Log.d("RBI", "move");
			update(event);
			break;

		// others
		case MotionEvent.ACTION_CANCEL:
			// Log.d("RBI", "cancel");
			break;
		default:
			// Log.d("RBI", "Default");
			break;
		}
	}

	public void update(MotionEvent event){
		
		Finger f;
		int pointId;
		int fingerCount = event.getPointerCount();
		for (int i = 0; i < fingerCount; ++i) {
			pointId = event.getPointerId(i);
			f = fingers.get(pointId);
			if (f != null) {
				f.update(event.getX(i), event.getY(i));
			}
		}
	}
	
	public void drawTouches(Canvas canvas) {

		Finger finger;
		int size = fingers.size();
//		Log.d("RBI","draw: "+size);
		for (int i = 0; i < size; ++i) {
			finger = fingers.valueAt(i);
			if (finger != null)
				touchTest.setColor(colors[i % 9]);
			canvas.drawCircle(finger.x(), finger.y(), 50, touchTest);
		}
	}

	//-----------------------------------------------+
	// abstract methods
	
	//-----------------------------------------------+
}