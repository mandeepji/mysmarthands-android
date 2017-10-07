package com.common_lib.android.maps.path;

import com.common_lib.android.maps.location.LocationHelper;
import com.common_lib.android.networking.HttpConnection;
import com.common_lib.android.networking.HttpConnection.AsyncHttpRequestDelegate;
import com.common_lib.io.json.JsonArray;
import com.common_lib.io.json.JsonObject;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class GoogleMapDirections {

	// QUERY & STATUS ENUM
	public static final String DIRECTIONS_URL 			= "http://maps.googleapis.com/maps/api/directions/json?";
	public static final String STATUS_KEY 				= "status";
	public static final String ERR_MSG_KEY 				= "error_message";
	public static final String STATUS_OK 				= "OK";

	// JSON KEYS
	public static final String ROUTES_KEY 				= "routes";
	public static final String ROUTE_POLY_KEY 			= "overview_polyline";
	public static final String ROUTE_LEGS_KEY			= "legs";
	public static final String ROUTE_STEPS_KEY			= "steps";
	public static final String STEPS_INSTRUCTION_KEY	= "html_instructions";
	public static final String START_LOC_KEY			= "start_location";
	public static final String END_LOC_KEY				= "end_location";
	public static final String DISTANCE_KEY				= "distance";
	public static final String DURATION_KEY				= "duration";
	public static final String BOUNDS_KEY				= "bounds";
	public static final String NORTHEAST_KEY			= "northeast";
	public static final String SOUTHWEST_KEY			= "southwest";
	public static final String POINTS_KEY				= "points";
	public static final String LATITUDE_KEY				= "lat";
	public static final String LONGITUDE_KEY			= "lng";
	public static final String TEXT_KEY					= "text";
	public static final String VALUE_KEY				= "value";

	JsonObject directions;
	JsonArray routes;

	public GoogleMapDirections(String results) {

		directions = JsonObject.readFrom(results);
		routes = directions.get(ROUTES_KEY).asArray();
	}

	public GoogleMapDirections(JsonObject directions) {

		this.directions = directions;
		routes = directions.get(ROUTES_KEY).asArray();
	}

	public List<LatLng> getPath(int routeIndex) {

		JsonObject route = routes.get(routeIndex).asObject();
		JsonObject line = route.get(ROUTE_POLY_KEY).asObject();
		
//		Log.d("RBI",line.get("points").asString());
//		Log.d("RBI",decode(line.get("points").asString()).toString());
		
		return decode(line.get(POINTS_KEY).asString());
	}
	
	// --------------------------------------------------------+
	// Routing
	public JsonObject getRoute(int routeIndex){
		
		return routes.get(routeIndex).asObject();
	}
	
	public JsonObject getLeg(int routeIndex,int legIndex){
		
		JsonObject route = routes.get(routeIndex).asObject();
		return route
				.get(ROUTE_LEGS_KEY).asArray()
				.get(legIndex).asObject();
	}
	
	public JsonObject getDefaultLeg(){
		
		return getLeg(0,0);
	}

	public JsonArray getSteps(int routeIndex){
		
		JsonObject route = routes.get(routeIndex).asObject();
		return route
				.get(ROUTE_LEGS_KEY).asArray()
				.get(0).asObject()
				.get(ROUTE_STEPS_KEY).asArray();
	}
	
	public static String getHTMLInstruction(JsonObject obj){
		
		return obj.get(STEPS_INSTRUCTION_KEY).asString();
	}
	
	public static LatLngBounds getBounds(JsonObject obj){
		
		List<LatLng> list = new ArrayList<LatLng>(2);
		list.add( jsonToLatLng( obj.get(START_LOC_KEY).asObject() ) );
		list.add( jsonToLatLng( obj.get(END_LOC_KEY).asObject() ) );
		return LocationHelper.latLngsToBounds( list );
	}
	
	public static String getDistanceStr(JsonObject obj){
		
		return obj
				.get(DISTANCE_KEY).asObject()
				.get(TEXT_KEY).asString();
	}
	
	public static String getDurationStr(JsonObject obj){
		
		return obj
				.get(DURATION_KEY).asObject()
				.get(TEXT_KEY).asString();
	}
	
	public static LatLng getStartLatLng(JsonObject obj){
		
		return jsonToLatLng( obj.get(START_LOC_KEY).asObject() );
	}
	
	public static LatLng getEndLatLng(JsonObject obj){
		
		return jsonToLatLng( obj.get(END_LOC_KEY).asObject() );
	}
	
 	public GoogleRoute getGoogleRoute(int routeIndex){
		
		return new GoogleRoute(this,routeIndex);
	}
 	
	// --------------------------------------------------------+
	public static void getDirections(LatLng origin, String Destination,
			final DirectionsListener l) {

		getDirections(origin.toString(), Destination, l);
	}

	public static void getDirections(LatLng origin, LatLng Destination,
			final DirectionsListener l) {

		getDirections(origin.toString(), Destination.toString(), l);
	}

	public static void getDirections(String origin, String Destination,
			final DirectionsListener l) {

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("sensor", "false"));
		nameValuePairs.add(new BasicNameValuePair("origin", origin));
		nameValuePairs.add(new BasicNameValuePair("destination", Destination));

		getDirections(nameValuePairs, l);
	}

	private static void getDirections(List<NameValuePair> nameValuePairs,
			final DirectionsListener l) {

		HttpConnection.asyncHttpRequestString(
				DIRECTIONS_URL,
				nameValuePairs,
				new AsyncHttpRequestDelegate() {
					@Override
					public void requestReturnedWithResult(String res) {
						JsonObject resultObj = JsonObject.readFrom(res);
						String status = resultObj.get(STATUS_KEY).asString();
						if (status !=null && status.compareTo(STATUS_OK) == 0) {
							l.directionsReceived(new GoogleMapDirections(
									resultObj));
						} else {
							String errMsg = resultObj.get(ERR_MSG_KEY)
									.asString();
							l.directionsFailed(res, status, errMsg);
						}
					}
					
					@Override
					public void requestFailed(Exception e) {
						
					}
				});
	}

	public interface DirectionsListener {

		public void directionsFailed(String result, String status, String msg);

		public void directionsReceived(GoogleMapDirections directions);
	}

	// --------------------------------------------------------+
	// helpers
	public static LatLng jsonToLatLng(JsonObject latLng){
		
		return new LatLng(
				latLng.get(LATITUDE_KEY).asDouble(),
				latLng.get(LONGITUDE_KEY).asDouble() 
			);
	}
	
	// --------------------------------------------------------+
	// Encode/Decode a sequence of LatLngs into an encoded path string.
	public static String encode(final List<LatLng> path) {
		long lastLat = 0;
		long lastLng = 0;

		final StringBuffer result = new StringBuffer();

		for (final LatLng point : path) {
			long lat = Math.round(point.latitude * 1e5);
			long lng = Math.round(point.longitude * 1e5);

			long dLat = lat - lastLat;
			long dLng = lng - lastLng;

			encode(dLat, result);
			encode(dLng, result);

			lastLat = lat;
			lastLng = lng;
		}
		return result.toString();
	}

	private static void encode(long v, StringBuffer result) {
		v = v < 0 ? ~(v << 1) : v << 1;
		while (v >= 0x20) {
			result.append(Character.toChars((int) ((0x20 | (v & 0x1f)) + 63)));
			v >>= 5;
		}
		result.append(Character.toChars((int) (v + 63)));
	}

	public static List<LatLng> decode(final String encodedPath) {

		int len = encodedPath.length();

		// For speed we preallocate to an upper bound on the final length, then
		// truncate the array before returning.
		final List<LatLng> path = new ArrayList<LatLng>();
		int index = 0;
		int lat = 0;
		int lng = 0;

		for (int pointIndex = 0; index < len; ++pointIndex) {
			int result = 1;
			int shift = 0;
			int b;
			do {
				b = encodedPath.charAt(index++) - 63 - 1;
				result += b << shift;
				shift += 5;
			} while (b >= 0x1f);
			lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

			result = 1;
			shift = 0;
			do {
				b = encodedPath.charAt(index++) - 63 - 1;
				result += b << shift;
				shift += 5;
			} while (b >= 0x1f);
			lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

			path.add(new LatLng(lat * 1e-5, lng * 1e-5));
		}

		return path;
	}

	// --------------------------------------------------------+
	// Status Codes
	{

		// OK indicates the response contains a valid result.
		// NOT_FOUND indicates at least one of the locations specified in the
		// requests's origin, destination, or waypoints could not be geocoded.
		// ZERO_RESULTS indicates no route could be found between the origin and
		// destination.
		// MAX_WAYPOINTS_EXCEEDED indicates that too many waypointss were
		// provided in the request The maximum allowed waypoints is 8, plus the
		// origin, and destination. ( Google Maps API for Business customers may
		// contain requests with up to 23 waypoints.)
		// INVALID_REQUEST indicates that the provided request was invalid.
		// Common causes of this status include an invalid parameter or
		// parameter value.
		// OVER_QUERY_LIMIT indicates the service has received too many requests
		// from your application within the allowed time period.
		// REQUEST_DENIED indicates that the service denied use of the
		// directions service by your application.
		// UNKNOWN_ERROR indicates a directions request could not be processed
		// due to a server error. The request may succeed if you try again.
	}
	// --------------------------------------------------------+

}
