package com.feedbacktower.util.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment

class PermissionManager private constructor() {

    companion object {
        private var instance: PermissionManager? = null
        const val PERMISSION_CODE = 1011
        fun getInstance(): PermissionManager =
            instance ?: synchronized(this) {
                instance
                    ?: PermissionManager().also {
                    instance = it
                }
            }
    }

    fun requestMediaPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_CODE
            )
    }
    fun requestMediaPermission(fragment: Fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            fragment.requestPermissions(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_CODE
            )
    }

    fun cameraPermissionGranted(context: Context) =
        PermissionChecker.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PermissionChecker.PERMISSION_GRANTED

    fun writeStoragePermissionGranted(context: Context) =
        PermissionChecker.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PermissionChecker.PERMISSION_GRANTED

    fun readyToPickImage(context: Context) = cameraPermissionGranted(context) && writeStoragePermissionGranted(context)
    public fun requestCameraPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_CODE
            )
    }
}