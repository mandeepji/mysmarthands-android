package com.common_lib.android.maps.location;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.common_lib.R;
import com.common_lib.android.fragment.ErrorDialogFragment;
import com.common_lib.android.maps.geofencing.SimpleGeofence;
import com.common_lib.io.json.JsonArray;
import com.common_lib.io.json.JsonObject;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationHelper implements LocationListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	// Handle to SharedPreferences for this app
	SharedPreferences mPrefs;
	// Handle to a SharedPreferences editor
	SharedPreferences.Editor mEditor;

	private LocationRequest mLocationRequest; // A request to connect to
												// Location Services
	private LocationClient mLocationClient;

	// fix this so that you don't have to pause on resume... right now you have to call all the on*
	boolean mUpdatesRequested = false;
	Context context;
	List<LocationListener> listeners;

	List<SimpleGeofence> geofencesMonitored;

	@SuppressLint("CommitPrefEdits")
	public LocationHelper(FragmentActivity context, LocationListener listner,
			boolean startImmediately,LocationRequest request) {

		this.context = context;
		this.listeners = new ArrayList<LocationListener>();
		if (listner != null) {
			this.listeners.add(listner);
		}

		mUpdatesRequested = startImmediately;
		mLocationRequest = request;

		// Open Shared Preferences
		mPrefs = context.getSharedPreferences(LocationUtils.SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		// Get an editor
		mEditor = mPrefs.edit();

		mLocationClient = new LocationClient(context, this, this);
	}
	
	@SuppressLint("CommitPrefEdits")
	public LocationHelper(FragmentActivity context, LocationListener listner,
			boolean startImmediately) {

		this.context = context;
		this.listeners = new ArrayList<LocationListener>();
		if (listner != null) {
			this.listeners.add(listner);
		}

		mUpdatesRequested = startImmediately;

		// Create a new global location parameters object
		mLocationRequest = LocationRequest.create();
		mLocationRequest
				.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest
				.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

		// Open Shared Preferences
		mPrefs = context.getSharedPreferences(LocationUtils.SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		// Get an editor
		mEditor = mPrefs.edit();

		mLocationClient = new LocationClient(context, this, this);
	}

	// Put these calls in the corresponding on* of the containing
	// activity/fragment
	public void onStart() {

		mLocationClient.connect();
	}

	public void onStop() {

		// If the client is connected
		if (mLocationClient.isConnected()) {
			this.stopPeriodicUpdates();
		}

		// After disconnect() is called, the client is considered "dead".
		mLocationClient.disconnect();
	}

	public void onResume() {

		// If the app already has a setting for getting location updates, get it
		if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
			mUpdatesRequested = mPrefs.getBoolean(
					LocationUtils.KEY_UPDATES_REQUESTED, false);

			// Otherwise, turn off location updates until requested
		} else {
			mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
			mEditor.commit();
		}
	}

	public void onPause(boolean startRequestsOnResume) {

		// Save the current setting for updates
		mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED,
				startRequestsOnResume);
		mEditor.commit();
	}

	// optional
	/*
	 * Handle results returned to this Activity by other Activities started with
	 * startActivityForResult(). In particular, the method onConnectionFailed()
	 * in LocationUpdateRemover and LocationUpdateRequester may call
	 * startResolutionForResult() to start an Activity that handles Google Play
	 * services problems. The result of this call returns here, to
	 * onActivityResult.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		// Choose what to do based on the request code
		switch (requestCode) {

		// If the request code matches the code sent in onConnectionFailed
		case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST:

			switch (resultCode) {
			// If Google Play services resolved the problem
			case Activity.RESULT_OK:

				// Log the result
				Log.d(LocationUtils.APPTAG,
						context.getString(R.string.resolved));

				// Display the result
				// state.setText(R.string.connected);
				// status.setText(R.string.resolved);
				break;

			// If any other result was returned by Google Play services
			default:
				// Log the result
				Log.d(LocationUtils.APPTAG,
						context.getString(R.string.no_resolution));

				// Display the result
				// state.setText(R.string.disconnected);
				// status.setText(R.string.no_resolution);

				break;
			}

			// If any other request code was received
		default:
			// Report that this Activity received an unknown requestCode
			Log.d(LocationUtils.APPTAG, context.getString(
					R.string.unknown_activity_request_code, requestCode));

			break;
		}
	}

	public void startPeriodicUpdates() {

		mUpdatesRequested = true;
		if (servicesConnected()) {
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}
	}

	public void stopPeriodicUpdates() {

		mUpdatesRequested = false;
		if (servicesConnected()) {
			mLocationClient.removeLocationUpdates(this);
		}
	}

	public Location getLocation() {

		// If Google Play Services is available
		if (servicesConnected()) {
			// Get the current location
			return mLocationClient.getLastLocation();
		}
		return null;
	}

	@SuppressLint("NewApi")
	public void getAddress(Location location) {

		// In Gingerbread and later, use Geocoder.isPresent() to see if a
		// geocoder is available.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
				&& !Geocoder.isPresent()) {
			// No geocoder is present. Issue an error message
			Toast.makeText(context, R.string.no_geocoder_available,
					Toast.LENGTH_LONG).show();
			return;
		}

		if (servicesConnected()) {

			// call addressTaskDelegate.didBeginAddressLookup
			// Start the background task
			(new GetAddressTask(context)).execute(location);
		}
	}

	/**
	 * Verify that Google Play services is available before making a request.
	 * 
	 * @return true if Google Play services is available, otherwise false
	 */
	public boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d(LocationUtils.APPTAG,
					context.getString(R.string.play_services_available));

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
					(Activity) context, 0);
			if (dialog != null) {
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(dialog);
				errorFragment.show(((FragmentActivity) context)
						.getSupportFragmentManager(), LocationUtils.APPTAG);
			}
			return false;
		}
	}

	public void addListener(LocationListener listener) {

		listeners.add(listener);
	}

	public void removeListener(LocationListener listener) {

		listeners.remove(listener);
	}

	public void removeAllListeners() {

		for (LocationListener listener : listeners) {
			listeners.remove(listener);
		}
	}

	// -------------------------------------------------------------------+
	// Location convenience methods
	public static List<LatLng> locationsToLatLngs(List<Location> track) {

		List<LatLng> lls = new ArrayList<LatLng>(track.size());

		for (Location loc : track) {
			lls.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
		}

		return lls;
	}

	public static LatLng locationToLatLng(Location loc) {

		return new LatLng(loc.getLatitude(), loc.getLongitude());
	}

	public static String latLngToString(LatLng latlng){
		
		return latlng.latitude+","+latlng.longitude;
	}
	
	public static String locationTolatLngString(Location location){
		
		return location.getLatitude()+","+location.getLongitude();
	}
	
	public static String latLngsToString(List<LatLng> track) {

		StringBuilder sb = new StringBuilder();

		for (LatLng ll : track) {
			sb.append(ll.latitude).append(",").append(ll.longitude)
					.append("\n");
		}

		return sb.toString();
	}

	public static List<LatLng> stringToLatLngs(String track) {

		String[] points = track.split("\n");
		String[] ll;
		List<LatLng> ret = new ArrayList<LatLng>(points.length);
		for (String p : points) {
			ll = p.split(",");
			ret.add(new LatLng(Float.valueOf(ll[0]), Float.valueOf(ll[1])));
		}
		return ret;
	}

	public static JsonArray LatLngsToJson(List<LatLng> latLngs){
		
		JsonObject llj;
		JsonArray ret = new JsonArray();
		for (LatLng ll : latLngs) {
			llj = new JsonObject();
			llj.add("lattitude", ll.latitude);
			llj.add("longitude", ll.longitude);
			ret.add(llj);
		}
		
		return ret;
	}
	
	public static JsonArray RadialLatLngsToJson(List<? extends RadialLatLng> latLngs){
		
		JsonObject llj;
		JsonArray ret = new JsonArray();
		for (RadialLatLng ll : latLngs) {
			llj = new JsonObject();
			llj.add("lattitude", ll.latLng.latitude);
			llj.add("longitude", ll.latLng.longitude);
			llj.add("radius", ll.radius);
			ret.add(llj);
		}
		
		return ret;
	}
	
	// -------------------------------------------------------------------+
	// Bounds detection
	public static LatLngBounds latLngsToBounds(List<LatLng> latLngs){
		
		LatLngBounds.Builder b = LatLngBounds.builder();
		for (LatLng ll : latLngs) {
			b.include(ll);
		}
		return b.build();
	}
	
	public static boolean outlineContainsPoint(LatLng point,LatLngBounds b){
		
		return b.contains(point);
	}

	public static LatLng centroid(List<LatLng> points) {
        
		double[] centroid = { 0.0, 0.0 };

        for (int i = 0; i < points.size(); i++) {
            centroid[0] += points.get(i).latitude;
            centroid[1] += points.get(i).longitude;
        }

        int totalPoints = points.size();
        centroid[0] /=  totalPoints;
        centroid[1] /=  totalPoints;

        return new LatLng(centroid[0], centroid[1]);
    }
	
	// get centers don't work at the moment - use centroid instead
	public static LatLng getCenter(LatLngBounds bounds){
		
		double latD = Math.abs(bounds.southwest.latitude) - 
						Math.abs(bounds.northeast.latitude);
		
		double lngD = Math.abs(bounds.southwest.longitude) - 
				Math.abs(bounds.northeast.longitude);
		
		return new LatLng(
				bounds.northeast.latitude+latD,
				bounds.northeast.longitude+lngD);
	}
	
	public static LatLng getCenter(List<LatLng> outline){
		
		Double lLat,hLat,lLng,hLng;
		hLat = hLng = Double.MIN_VALUE;
		lLat = lLng = Double.MAX_VALUE;
		
		double lat,lng;
		for (LatLng ll : outline) {
			lat = ll.latitude;
			lng = ll.longitude;
			if(lat > hLat) hLat = lat;
			else if(lat < lLat) lLat = lat;
			if(lng > hLng) hLng = lng;
			else if(lng < lLng) lLng = lng;
		}
		// doesn't account for negative numbers ?? can't be the problem
		return new LatLng(
				lLat + ((Math.abs(hLat) - Math.abs(lLat)) / 2),
				lLng + ((Math.abs(hLng) - Math.abs(lLng)) / 2)
				);
	}
	
	public static boolean outlineContainsPoint(LatLng point, List<LatLng> vertices) {
	    
		int intersectCount = 0;
	    for(int j=0; j<vertices.size()-1; ++j) {
	        if( rayCastIntersect(point, vertices.get(j), vertices.get(j+1)) ) {
	            ++intersectCount;
	        }
	    }

	    return (intersectCount%2) == 1; // odd = inside, even = outside;
	}

	private static boolean rayCastIntersect(LatLng tap, LatLng vertA, LatLng vertB) {

	    double aY = vertA.latitude;
	    double bY = vertB.latitude;
	    double aX = vertA.longitude;
	    double bX = vertB.longitude;
	    double pY = tap.latitude;
	    double pX = tap.longitude;

	    if ( (aY>pY && bY>pY) || (aY<pY && bY<pY) || (aX<pX && bX<pX) ) {
	        return false; // a and b can't both be above or below pt.y, and a or b must be east of pt.x
	    }

	    double m = (aY-bY) / (aX-bX);               // Rise over run
	    double bee = (-aX) * m + aY;                // y = mx + b
	    double x = (pY - bee) / m;                  // algebra is neat!

	    return x > pX;
	}
	
	// -------------------------------------------------------------------+
	public static LocationTracker CreateLocationTracker(String id,
			FragmentActivity context, GoogleMap map, LocationListener listner,
			boolean startImmediately) {

		LocationHelper helper = new LocationHelper(context, listner,
				startImmediately);
		return new LocationTracker(helper, id, map);
	}

	public static LocationTracker CreateLocationTracker(String id,
			LocationHelper helper, GoogleMap map) {

		return new LocationTracker(helper, id, map);
	}

	// -------------------------------------------------------------------+
	// Delegate Methods
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult((Activity) context,
						LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */

			} catch (IntentSender.SendIntentException e) {

				// Log the error
				e.printStackTrace();
			}
		} else {

			// If no resolution is available, display a dialog to the user with
			// the error.
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	@Override
	public void onConnected(Bundle bundle) {

		if (mUpdatesRequested) {
			startPeriodicUpdates();
		}
	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onLocationChanged(Location location) {

//		Log.d("RBI",listeners.size()+"");
		for (LocationListener listner : listeners) {
			
			listner.onLocationChanged(location);
		}
	}

	private void showErrorDialog(int errorCode) {

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
				(Activity) context,
				LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {

			// Create a new DialogFragment in which to show the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();

			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);

			// Show the error dialog in the DialogFragment
			errorFragment.show(
					((FragmentActivity) context).getSupportFragmentManager(),
					LocationUtils.APPTAG);
		}
	}

	// -------------------------------------------------------------------+
	/*
	 * An AsyncTask that calls getFromLocation() in the background. The class
	 * uses the following generic types: Location - A {@link
	 * android.location.Location} object containing the current location, passed
	 * as the input parameter to doInBackground() Void - indicates that progress
	 * units are not used by this subclass String - An address passed to
	 * onPostExecute()
	 */
	protected class GetAddressTask extends AsyncTask<Location, Void, String> {

		// Store the context passed to the AsyncTask when the system
		// instantiates it.
		Context localContext;

		// Constructor called by the system to instantiate the task
		public GetAddressTask(Context context) {

			// Required by the semantics of AsyncTask
			super();

			// Set a Context for the background task
			localContext = context;
		}

		/**
		 * Get a geocoding service instance, pass latitude and longitude to it,
		 * format the returned address, and return the address to the UI thread.
		 */
		@Override
		protected String doInBackground(Location... params) {
			/*
			 * Get a new geocoding service instance, set for localized
			 * addresses. This example uses android.location.Geocoder, but other
			 * geocoders that conform to address standards can also be used.
			 */
			Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

			// Get the current location from the input parameter list
			Location location = params[0];

			// Create a list to contain the result address
			List<Address> addresses = null;

			// Try to get an address for the current location. Catch IO or
			// network problems.
			try {

				/*
				 * Call the synchronous getFromLocation() method with the
				 * latitude and longitude of the current location. Return at
				 * most 1 address.
				 */
				addresses = geocoder.getFromLocation(location.getLatitude(),
						location.getLongitude(), 1);

				// Catch network or other I/O problems.
			} catch (IOException exception1) {

				// Log an error and return an error message
				Log.e(LocationUtils.APPTAG, context
						.getString(R.string.IO_Exception_getFromLocation));

				// print the stack trace
				exception1.printStackTrace();

				// Return an error message
				return (context
						.getString(R.string.IO_Exception_getFromLocation));

				// Catch incorrect latitude or longitude values
			} catch (IllegalArgumentException exception2) {

				// Construct a message containing the invalid arguments
				String errorString = context.getString(
						R.string.illegal_argument_exception,
						location.getLatitude(), location.getLongitude());
				// Log the error and print the stack trace
				Log.e(LocationUtils.APPTAG, errorString);
				exception2.printStackTrace();

				//
				return errorString;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {

				// Get the first address
				Address address = addresses.get(0);

				// Format the first line of address
				String addressText = context.getString(
						R.string.address_output_string,

						// If there's a street address, add it
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "",

						// Locality is usually a city
						address.getLocality(),

						// The country of the address
						address.getCountryName());

				// Return the text
				return addressText;

				// If there aren't any addresses, post a message
			} else {
				return context.getString(R.string.no_address_found);
			}
		}

		/**
		 * A method that's called once doInBackground() completes. Set the text
		 * of the UI element that displays the address. This method runs on the
		 * UI thread.
		 */
		@Override
		protected void onPostExecute(String address) {

			// call addressTaskDelegate.didEndAddressLookup
		}
	}

	// -------------------------------------------------------------------+
	public interface LocationHelperLifeCycle {

		public void onStart();

		public void onStop();

		public void onResume();

		public void onPause(boolean startRequestsOnResume);
	}

	// -------------------------------------------------------------------+
}
