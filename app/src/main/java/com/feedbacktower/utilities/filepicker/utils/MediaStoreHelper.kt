package com.feedbacktower.utilities.filepicker.utils

import android.content.ContentResolver
import android.os.Bundle

import java.util.Comparator

import com.feedbacktower.utilities.filepicker.cursors.DocScannerTask
import com.feedbacktower.utilities.filepicker.cursors.PhotoScannerTask
import com.feedbacktower.utilities.filepicker.cursors.loadercallbacks.FileMapResultCallback
import com.feedbacktower.utilities.filepicker.cursors.loadercallbacks.FileResultCallback
import com.feedbacktower.utilities.filepicker.models.Document
import com.feedbacktower.utilities.filepicker.models.FileType
import com.feedbacktower.utilities.filepicker.models.PhotoDirectory

object MediaStoreHelper {

    fun getDirs(contentResolver: ContentResolver, args: Bundle, resultCallback: FileResultCallback<PhotoDirectory>) {
        PhotoScannerTask(contentResolver,args,resultCallback).execute()
    }

    fun getDocs(contentResolver: ContentResolver,
                fileTypes: List<FileType>,
                comparator: Comparator<Document>?,
                fileResultCallback: FileMapResultCallback) {
        DocScannerTask(contentResolver, fileTypes, comparator, fileResultCallback).execute()
    }
}