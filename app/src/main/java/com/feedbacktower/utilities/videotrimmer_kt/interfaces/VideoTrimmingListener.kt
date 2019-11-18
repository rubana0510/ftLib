package com.feedbacktower.utilities.videotrimmer_kt.interfaces

import android.net.Uri
import androidx.annotation.UiThread

interface VideoTrimmingListener {
    @UiThread
    fun onVideoPrepared()

    @UiThread
    fun onTrimStarted()

    /**
     * @param uri the result, trimmed video, or null if failed
     */
    @UiThread
    fun onFinishedTrimming(uri: Uri?)

    /**
     * check {[android.media.MediaPlayer.OnErrorListener]}
     */
    @UiThread
    fun onErrorWhileViewingVideo(what: Int, extra: Int)

    @UiThread
    fun onCompressStarted()

    @UiThread
    fun onFinishedCompressing(uri: Uri?)

    @UiThread
    fun onCompressFailed()

    @UiThread
    fun onCompressProgress(progress: Float?)

}
