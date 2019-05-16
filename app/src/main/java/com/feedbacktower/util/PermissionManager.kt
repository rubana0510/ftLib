package com.feedbacktower.util

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.core.app.ActivityCompat

const val PERMISSION_CODE = 1011
class PermissionManager private constructor(){

    companion object {
        private var instance: PermissionManager? = null
        fun getInstance(): PermissionManager =
            instance ?: synchronized(this) {
                instance ?: PermissionManager().also {
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
    public fun requestCameraPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_CODE
            )
    }
}