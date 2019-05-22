package com.feedbacktower.ui.videoplayer

import android.util.Log
import android.view.Surface
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.decoder.DecoderCounters
import com.google.android.exoplayer2.video.VideoRendererEventListener

class ComponentListener : Player.DefaultEventListener(),
    VideoRendererEventListener, AudioRendererEventListener {
    private val TAG = "ComponentListener "


    override fun onDroppedFrames(count: Int, elapsedMs: Long) {

    }

    override fun onVideoEnabled(counters: DecoderCounters?) {

    }

    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {

    }

    override fun onVideoDisabled(counters: DecoderCounters?) {

    }

    override fun onVideoDecoderInitialized(
        decoderName: String?,
        initializedTimestampMs: Long,
        initializationDurationMs: Long
    ) {

    }

    override fun onVideoInputFormatChanged(format: Format?) {

    }

    override fun onRenderedFirstFrame(surface: Surface?) {

    }

    override fun onAudioSinkUnderrun(bufferSize: Int, bufferSizeMs: Long, elapsedSinceLastFeedMs: Long) {

    }

    override fun onAudioEnabled(counters: DecoderCounters?) {

    }

    override fun onAudioInputFormatChanged(format: Format?) {

    }

    override fun onAudioSessionId(audioSessionId: Int) {

    }

    override fun onAudioDecoderInitialized(
        decoderName: String?,
        initializedTimestampMs: Long,
        initializationDurationMs: Long
    ) {

    }

    override fun onAudioDisabled(counters: DecoderCounters?) {

    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        val stateString: String = when (playbackState) {
            Player.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
            Player.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
            Player.STATE_READY -> "ExoPlayer.STATE_READY     -"
            Player.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
            else -> "UNKNOWN_STATE             -"
        }
        Log.d(TAG, "changed state to $stateString playWhenReady: $playWhenReady")
    }


}