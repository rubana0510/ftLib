package com.feedbacktower.util;

import android.content.Context;
import android.location.LocationManager;
import com.google.android.gms.maps.model.LatLng;


public class LocationUtils {
      private static LocationUtils instance = null;

      private LatLng lastKnownLocation;

      public static LocationUtils getInstance() {
            if (instance == null) {
                  synchronized (LocationUtils.class) {
                        if (instance == null){
                              instance = new LocationUtils();
                        }
                  }
            }
            return instance;
      }

      public boolean isLocationEnabled(final Context context) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager == null) return false;

            boolean isGpsEnabled = false;
            boolean isNetworkEnabled = false;
            try {
                  isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                  e.printStackTrace();
            }
            try {
                  isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception e) {
                  e.printStackTrace();
            }
            return isGpsEnabled && isNetworkEnabled;
      }

      public LatLng getLastKnownLocation() {
            return lastKnownLocation;
      }

      public void setLastKnownLocation(LatLng lastKnownLocation) {
            this.lastKnownLocation = lastKnownLocation;
      }
}
