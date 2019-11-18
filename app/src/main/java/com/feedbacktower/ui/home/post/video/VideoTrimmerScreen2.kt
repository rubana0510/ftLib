package com.feedbacktower.ui.home.post.video

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.feedbacktower.R
import com.feedbacktower.util.Constants
import com.feedbacktower.util.logd
import com.feedbacktower.utilities.videotrimmer_kt.interfaces.VideoTrimmingListener
import kotlinx.android.synthetic.main.activity_video_trimmer_screen2.*
import java.io.File

class VideoTrimmerScreen2 : AppCompatActivity(), VideoTrimmingListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_trimmer_screen2)
        val inputVideoUri: Uri? = intent?.getParcelableExtra("EXTRA_INPUT_URI")
        if (inputVideoUri == null) {
            finish()
            return
        }

        videoTrimmerView.setMaxDurationInMs(Constants.Media.MAX_VIDEO_LENGTH)
        videoTrimmerView.setOnK4LVideoListener(this)
        val parentFolder = getExternalFilesDir(null)!!
        parentFolder.mkdirs()
        val fileName = "trimmedVideo_${System.currentTimeMillis()}.mp4"
        val trimmedVideoFile = File(parentFolder, fileName)
        videoTrimmerView.setDestinationFile(trimmedVideoFile)
        videoTrimmerView.setVideoURI(inputVideoUri)
        videoTrimmerView.setVideoInformationVisibility(true)
        sendButton.setOnClickListener { videoTrimmerView.initiateTrimming() }
    }

    override fun onTrimStarted() {
        trimmingProgressView.visibility = View.VISIBLE
    }

    override fun onFinishedTrimming(uri: Uri?) {
        logd("onFinishedTrimming: ${uri?.path}")
    }

    override fun onErrorWhileViewingVideo(what: Int, extra: Int) {
        trimmingProgressView.visibility = View.GONE
        Toast.makeText(this@VideoTrimmerScreen2, "error while previewing video", Toast.LENGTH_SHORT).show()
    }

    override fun onVideoPrepared() {
        //        Toast.makeText(TrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
    }

    override fun onCompressStarted() {
        logd("onCompressStarted")
    }

    override fun onFinishedCompressing(uri: Uri?) {
        logd("onFinishedCompressing: ${uri?.path}")
        trimmingProgressView.visibility = View.GONE

        if (uri == null) {
            Toast.makeText(this@VideoTrimmerScreen2, "failed trimming", Toast.LENGTH_SHORT).show()
        } else {
            val msg = "At ${uri.path}"
            Toast.makeText(this@VideoTrimmerScreen2, msg, Toast.LENGTH_SHORT).show()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setDataAndType(uri, "video/mp4")
            startActivity(intent)
        }
        finish()
    }

    override fun onCompressFailed() {
        logd("onCompressFailed")
    }

    override fun onCompressProgress(progress: Float?) {
        logd("onCompressProgress: ${progress}")
    }

}
