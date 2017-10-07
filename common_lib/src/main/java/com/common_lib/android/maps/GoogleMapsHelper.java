package com.common_lib.android.maps;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;

import com.common_lib.android.maps.location.LocationHelper;
import com.common_lib.android.system.SystemTools;
import com.common_lib.android.ui.DialogHelper;
import com.common_lib.android.ui.utils.ImageHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleMapsHelper {

	public static final int MAP_OPTIONS_VISIBLE = 0;
	public static final int MAP_OPTIONS_DRAGABLE = 1;

	public static Map<String, BitmapDescriptor> overlayCache = new HashMap<String, BitmapDescriptor>();

	public static GroundOverlay addGroundOverlay(Context context,
			GoogleMap map, int overlayResID, LatLng topLeft, float w,
			float bearing, float scalar, String cacheKey) {

		BitmapDescriptor cache = null;
		if (cacheKey != null) {
			cache = overlayCache.get(cacheKey);
		}

		if (cache == null) {
			// Log.d("RBI", "created");
			Point p = SystemTools.getScreenSize(context);
			cache = BitmapDescriptorFactory.fromBitmap(ImageHelper
					.decodeScaledBitmap(context.getResources(), overlayResID,
							(int) (p.x / scalar), (int) (p.y / scalar)));
			// .fromBitmap(BitmapFactory.decodeResource(context.getResources(),
			// overlayResID));
			// .fromResource(overlayResID); // can't use cause its fuckered
			// memory wise
			overlayCache.put(cacheKey, cache);
		}

		GroundOverlay ret = map.addGroundOverlay(new GroundOverlayOptions()
				.image(cache).anchor(0, 0).bearing(bearing)
				.position(topLeft, w));

		return ret;
	}

	public static GroundOverlay addGroundOverlay(GoogleMap map,
			int overlayResID, LatLng topLeft, float w, float h, float bearing) {

		GroundOverlay ret = map.addGroundOverlay(new GroundOverlayOptions()
				.image(BitmapDescriptorFactory.fromResource(overlayResID))
				.anchor(0, 0).bearing(bearing).position(topLeft, w, h));

		return ret;
	}

	public static MapCameraBounds addCameraBounds(GoogleMap map,
			LatLngBounds bounds, float minZoom, float maxZoom) {

		MapCameraBounds cb = new MapCameraBounds(map, bounds, minZoom, maxZoom);
		map.setOnCameraChangeListener(cb);
		return cb;
	}

	public static Polyline addPolyline(GoogleMap map, List<LatLng> points) {

		PolylineOptions rectOptions = new PolylineOptions().width(5)
				.color(Color.RED).addAll(points);

		// Get back the mutable Polyline
		return map.addPolyline(rectOptions);
	}

	public static Polyline addPolylineFromMarkers(GoogleMap map,
			List<Marker> markers) {

		List<LatLng> points = markersToLatLngs(markers);

		PolylineOptions rectOptions = new PolylineOptions().width(5)
				.color(Color.RED).addAll(points);

		// Get back the mutable Polyline
		return map.addPolyline(rectOptions);
	}

	public static Polyline addPolylineFromIMarkers(GoogleMap map,
			List<? extends IMarker> markers) {

		List<LatLng> points = imarkersToLatLngs(markers);

		PolylineOptions rectOptions = new PolylineOptions().width(5)
				.color(Color.RED).addAll(points);

		// Get back the mutable Polyline
		return map.addPolyline(rectOptions);
	}

	// ------------------------------------------------------------------+
	public static void setTiltGesturesEnabled(GoogleMap map, boolean enabled) {

		map.getUiSettings().setTiltGesturesEnabled(enabled);
	}

	public static void setTilt(GoogleMap map, float tilt) {

		CameraPosition current = map.getCameraPosition();
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(current.target).zoom(current.zoom)
				.bearing(current.bearing) // Sets the orientation of the camera
											// to east
				.tilt(tilt).build(); // Creates a CameraPosition from the
										// builder

		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	// ------------------------------------------------------------------+
	public static void moveCameraToLatLng(GoogleMap map, LatLng latLng,
			float zoom) {

		if (zoom < 0) {
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
					map.getCameraPosition().zoom));
		} else {
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
		}
	}

	public static void moveCameraToBounds(GoogleMap map, LatLngBounds bounds,
			int padding) {

		map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

	}

	public static void moveCameraToBounds(GoogleMap map, LatLngBounds bounds,
			int w, int h, int padding) {

		map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, w, h,
				padding));

	}

	public static void moveCameraPosition(GoogleMap map, CameraPosition pos) {

		map.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
	}

	public static LatLngBounds moveCameraToPath(GoogleMap map,
			List<LatLng> path, LatLngBounds bounds, int padding) {

		LatLngBounds ret;
		if (bounds == null) {
			ret = LocationHelper.latLngsToBounds(path);
		} else {
			ret = bounds;
		}

		moveCameraToBounds(map, ret, padding);
		return ret;
	}

	// ------------------------------------------------------------------+
	public static void startClickLogger(GoogleMap map) {

		map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				Log.d("RBI", point.toString());
			}
		});

		map.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng point) {
				Log.d("RBI", point.toString());
			}
		});
	}

	public static void stopClickLogger(GoogleMap map) {

		map.setOnMapClickListener(null);
		map.setOnMapLongClickListener(null);
	}

	public static LongPressLocationSource startLongPressLocationSource(
			GoogleMap map, OnLocationChangedListener l) {

		LongPressLocationSource source = new LongPressLocationSource();
		map.setLocationSource(source);
		map.setOnMapLongClickListener(source);
		map.setMyLocationEnabled(true);
		if (l != null) {
			source.addListener(l);
		}

		return source;
	}

	public static void stopLongPressLocationSource(GoogleMap map) {

		map.setLocationSource(null);
		map.setOnMapLongClickListener(null);
		map.setMyLocationEnabled(false);
	}

	// ------------------------------------------------------------------+
	public static List<LatLng> markersToLatLngs(List<Marker> markers) {

		List<LatLng> ret = new ArrayList<LatLng>(markers.size());
		for (Marker m : markers) {
			ret.add(m.getPosition());
		}
		return ret;
	}

	public static void markersGroupSet(List<Marker> markers, int mapOption,
			boolean value) {

		switch (mapOption) {
		case MAP_OPTIONS_VISIBLE:
			for (Marker m : markers) {
				m.setVisible(value);
			}
			break;
		case MAP_OPTIONS_DRAGABLE:
			for (Marker m : markers) {
				m.setDraggable(value);
			}
			break;
		}
	}

	public static List<LatLng> imarkersToLatLngs(List<? extends IMarker> markers) {

		List<LatLng> ret = new ArrayList<LatLng>(markers.size());
		for (IMarker m : markers) {
			ret.add(m.getPosition());
		}
		return ret;
	}

	public static void imarkersGroupSet(List<? extends IMarker> markers,
			int mapOption, boolean value) {

		switch (mapOption) {
		case MAP_OPTIONS_VISIBLE:
			for (IMarker m : markers) {
				m.setVisible(value);
			}
			break;
		case MAP_OPTIONS_DRAGABLE:
			for (IMarker m : markers) {
				m.setDraggable(value);
			}
			break;
		}
	}

	public interface IMarker {

		public LatLng getPosition();

		public void setVisible(boolean visible);

		public void setDraggable(boolean draggable);

		public Object getTag();

		public void setTag(Object obj);
	}

	// ------------------------------------------------------------------+
	public static boolean isPlayServicesAvailable(final Context context,
			final OnClickListener listener) {

		int res = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		boolean ret = (res == ConnectionResult.SUCCESS);

		if (!ret) {
			DialogHelper.simpleAlertDialog(
							context,
							"Google Play Services",
							"The libraries required to use maps is missing or outdated. Would you like to download them from Google Play?",
							"Play Store", "Download later",
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case DialogInterface.BUTTON_POSITIVE:
										Intent intent = new Intent(
												Intent.ACTION_VIEW,
												Uri.parse("market://details?id="
														+ GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE));
										try {
											context.startActivity(intent);
										} 
										catch (Exception e) {
											DialogHelper.simpleAlertDialog(
													context,
													"Google Play store unavailable",
													"Error").show();
										}
										break;

									default:
										break;
									}

									if (listener != null) {
										listener.onClick(dialog, which);
									}

								}
							}).show();
		}

		return ret;
	}

	// ------------------------------------------------------------------+

}
