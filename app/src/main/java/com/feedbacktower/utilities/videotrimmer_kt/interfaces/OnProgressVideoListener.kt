package com.feedbacktower.utilities.videotrimmer_kt.interfaces

interface OnProgressVideoListener {
    fun updateProgress(time: Int, max: Int, scale: Float)
}
