package com.feedbacktower.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class PermissionUtils {
      public static final int PERM_LOCATION_CODE = 198;

      public static void requestLocationPermission(Activity activity) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                  Toast.makeText(activity, "Location permission is necessary for the app to run, go to settings and grant permission", Toast.LENGTH_LONG).show();
                  ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERM_LOCATION_CODE);
            } else {
                  ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERM_LOCATION_CODE);
            }
      }

      public static boolean locationPermissionsGranted(Activity activity) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
            return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
      }
}
