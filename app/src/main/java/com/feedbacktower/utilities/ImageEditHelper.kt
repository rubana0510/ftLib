package com.feedbacktower.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.fragment.app.Fragment
import com.feedbacktower.util.Constants
import com.feedbacktower.utilities.cropper.CropImage

/**
 * Created by sanket on 10-12-2018.
 */
object ImageEditHelper {
    const val EDIT_REQUEST_CODE = 1999
    const val CROP_REQUEST_CODE = 2999

    fun openCropper(context: Context, fragment: Fragment, uri: Uri) {
        CropImage.activity(uri)
            .setAspectRatio(1, 1)
            .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
            .setActivityTitle("Crop image")
            .setActivityMenuIconColor(Color.WHITE)
            .setOutputCompressQuality(Constants.CROP_IMAGE_QUALITY)
            .start(context, fragment)
    }
}