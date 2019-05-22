package com.feedbacktower.utilities.filepicker.cursors.loadercallbacks

import com.feedbacktower.utilities.filepicker.models.Document
import com.feedbacktower.utilities.filepicker.models.FileType


/**
 * Created by gabriel on 10/2/17.
 */

interface FileMapResultCallback {
    fun onResultCallback(files: Map<FileType, List<Document>>)
}

