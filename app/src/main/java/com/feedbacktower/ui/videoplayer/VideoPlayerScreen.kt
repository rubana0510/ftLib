package com.feedbacktower.ui.videoplayer

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.feedbacktower.R
import com.feedbacktower.ui.base.BaseActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_video_player_screen.*


class VideoPlayerScreen : BaseActivity() {
    companion object {
        val URI_KEY = "URI"
    }

    private lateinit var player: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory

    private var trackSelector: DefaultTrackSelector? = null
    private var lastSeenTrackGroupArray: TrackGroupArray? = null
    private val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory()
    private var currentWindow: Int = 0
    private var playbackPosition: Long = 0
    private val ivHideControllerButton: ImageView by lazy { findViewById<ImageView>(R.id.exo_controller) }
    private lateinit var uri: Uri
    private var bandwidthMeter = DefaultBandwidthMeter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player_screen)


        val url = intent?.getStringExtra(URI_KEY)
            ?: throw  IllegalArgumentException("Video Uri cannot be null")
        uri = Uri.parse(url)
        
        Log.d("URI",""+URI_KEY)


        hideSystemUi()
        closeButton.setOnClickListener{finish()}
    }

    private fun initializePlayer() {

        trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        mediaDataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"),bandwidthMeter)

        val mediaSource = ExtractorMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(uri)

        Log.d("URL",""+uri)

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)

        playerView.setPlayer(player);


        with(player) {
            prepare(mediaSource, false, false)
            playWhenReady = true
            Log.d("REady",""+playWhenReady)
        }


        playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        playerView.player = player
        playerView.requestFocus()
        ivHideControllerButton.setOnClickListener { playerView.hideController() }


        lastSeenTrackGroupArray = null
    }


    private fun updateStartPosition() {

        with(player) {
            playbackPosition = currentPosition
            currentWindow = currentWindowIndex
            playWhenReady = playWhenReady
        }
    }

    private fun releasePlayer() {
        updateStartPosition()
        player.release()
        trackSelector = null
    }

    public override fun onStart() {
        super.onStart()

        Log.d("Start","start")

        if (Util.SDK_INT > 23) initializePlayer()
    }

    public override fun onResume() {
        super.onResume()

        Log.d("StartR","startR")


        if (Util.SDK_INT <= 23) initializePlayer()
    }

    public override fun onPause() {
        super.onPause()

        if (Util.SDK_INT <= 23) releasePlayer()
    }

    public override fun onStop() {
        super.onStop()

        if (Util.SDK_INT > 23) releasePlayer()
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        playerView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
}
