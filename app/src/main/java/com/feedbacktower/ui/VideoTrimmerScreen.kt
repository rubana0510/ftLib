package com.feedbacktower.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.feedbacktower.R
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.util.Constants
import com.feedbacktower.util.uriToFile
import com.feedbacktower.utilities.videotrimmer.interfaces.OnTrimVideoListener
import kotlinx.android.synthetic.main.activity_video_trimmer_screen.*
import org.jetbrains.anko.toast
import java.io.File

class VideoTrimmerScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_trimmer_screen)
        val videoUri = intent?.getParcelableExtra<Uri>("URI") ?: throw  IllegalArgumentException("Invalid video path")
        videoTrimmer.setDestinationPath(Environment.getExternalStorageDirectory().absolutePath + File.separator + R.string.app_name)
        videoTrimmer.setMaxDuration(Constants.Media.MAX_VIDEO_LENGTH)
        videoTrimmer.setVideoURI(videoUri)
        videoTrimmer.maxFileSize = Constants.Media.MAX_VIDEO_SIZE
        videoTrimmer.setOnTrimVideoListener(object : OnTrimVideoListener {
            override fun getResult(uri: Uri?) {
                toast("Trimmed video: $uri")
                uploadVideoPost("", uri)
            }

            override fun cancelAction() {

            }
        })
    }

    private fun uploadVideoPost(caption: String, fileUri: Uri?) {

        fileUri?.let {
            val file = uriToFile(it)
            PostManager.getInstance()
                .createVideoPost(caption, file) { _, error ->

                    if (error == null) {
                        toast("Post Shared")
                        finish()
                    } else {

                        toast(error.message ?: "Error")
                    }

                }

        }
    }
}
