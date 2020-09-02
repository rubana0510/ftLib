package com.feedbacktower.ui.location.live;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavDeepLinkBuilder;
import com.feedbacktower.App;
import com.feedbacktower.BuildConfig;
import com.feedbacktower.R;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import static com.feedbacktower.util.Constants.TRACKING_NOTIFICATION_CHANNEL;
import static com.feedbacktower.util.Constants.TRACKING_NOTIFICATION_CHANNEL_ID;


public class TrackerService extends Service {

    @Inject
    com.feedbacktower.ui.location.live.LocationManager locationManager;

    private final LocationServiceBinder binder = new LocationServiceBinder();
    private final String TAG = "TrackingService";
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;

    private final int LOCATION_INTERVAL = 10000;
   // private final int LOCATION_DISTANCE = 1000;
    private final int LOCATION_DISTANCE = 200;
    public static String INTENT_FILTER = BuildConfig.APPLICATION_ID + ".LOC_RX";

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private class LocationListener implements android.location.LocationListener {
        private Location lastLocation = null;
        private final String TAG = "LocationListener";
        private Location mLastLocation;
        private Context context;

        public LocationListener(Context context, String provider) {
            mLastLocation = new Location(provider);
            this.context = context;
        }


        @Override
        public void onLocationChanged(Location location) {
            mLastLocation = location;
            locationManager
                    .saveCurrentLocation(new LatLng(location.getLatitude(), location.getLongitude()),
                            (emptyResponse, error) -> {
                                if (error != null) {
                                    Log.e(TAG, "Error saving location: " + error.getMessage());
                                } else {
                                    Log.i(TAG, "LocationSaved to Server ");
                                }
                                return null;
                            });

            Intent intent = getIntent(location.getLatitude(), location.getLongitude());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            Log.i(TAG, "LocationChanged: " + location);

        }

        private Intent getIntent(double latitude, double longitude) {
            Intent intent = new Intent(TrackerService.INTENT_FILTER);
            intent.putExtra("LAT", latitude);
            intent.putExtra("LONG", longitude);
            return intent;
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + status);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        ((App) getApplication()).getAppComponent().accountComponent().create().inject(this);
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    stopForeground(true);

                }
                stopSelf();
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);
            }
        }
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void startTracking() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(12345678, getNotification());
        initializeLocationManager();
        mLocationListener = new LocationListener(this, LocationManager.GPS_PROVIDER);

        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);
            Log.d(TAG, "Updates requested");
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

    }

    public void stopTracking() {
        this.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNotification() {
        Intent intentAction = new Intent(this, ActionReceiver.class);
        intentAction.putExtra("action", "open_app");
        // PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intentAction, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = new NavDeepLinkBuilder(this)
                .setGraph(R.navigation.business_main_nav_graph)
                .setDestination(R.id.mapTrackingFragment)
                .createPendingIntent();
        NotificationChannel channel = new NotificationChannel(TRACKING_NOTIFICATION_CHANNEL_ID, TRACKING_NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        Notification.Builder builder = new Notification.Builder(getApplicationContext(), TRACKING_NOTIFICATION_CHANNEL_ID).setAutoCancel(true);
        builder.setContentTitle("Location tracking is enabled");
        builder.setSmallIcon(R.drawable.ic_notification_icon);
        builder.setContentText("Your location is being updated as you move.");
        builder.addAction(new Notification.Action(R.drawable.ic_notification_icon, "SHOW IN APP", pendingIntent));
        return builder.build();
    }


    public class LocationServiceBinder extends Binder {
        public TrackerService getService() {
            return TrackerService.this;
        }
    }

}
