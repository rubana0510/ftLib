package com.feedbacktower.utilities.filepicker.cursors.loadercallbacks

interface FileResultCallback<T> {
    fun onResultCallback(files: List<T>)
}