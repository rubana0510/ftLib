package com.feedbacktower.ui.home.post.video

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.feedbacktower.R
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.util.Constants
import com.feedbacktower.utilities.videotrimmer.interfaces.OnTrimVideoListener
import kotlinx.android.synthetic.main.activity_video_trimmer_screen.*
import org.jetbrains.anko.toast
import java.io.File


class VideoTrimmerScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_trimmer_screen)
        var file = File(filesDir, "Videos")
        if (!file.exists()) {
            file.mkdir()
        }
        val videoUri = intent?.getParcelableExtra<Uri>("URI") ?: throw  IllegalArgumentException("Invalid video path")
        videoTrimmer.setDestinationPath(Environment.getExternalStorageDirectory().absolutePath + File.separator + R.string.app_name)
        videoTrimmer.setMaxDuration(Constants.Media.MAX_VIDEO_LENGTH)
        videoTrimmer.setVideoURI(videoUri)
        videoTrimmer.maxFileSize = Constants.Media.MAX_VIDEO_SIZE
        videoTrimmer.setOnTrimVideoListener(object : OnTrimVideoListener {
            override fun getResult(caption: String, uri: Uri?) {
                // toast("Trimmed video: $uri")
                if (uri == null) return

                val f = File(uri.path)
                Log.d("VideoTrimmer", "FilePath: ${f.absolutePath}" )
                f.let {
                    Log.d("VideoTrimmer", "File Size: ${it.length()}")
                    val size = it.length() / (1024 * 1024)
                    if (size > Constants.Media.MAX_VIDEO_SIZE) {
                        toast("Video size must be less than ${Constants.Media.MAX_VIDEO_SIZE}")
                        finish()
                    } else {
                        toast("Uploading video")
                        uploadVideoPost(caption, it)
                    }
                }
            }

            override fun cancelAction() {
                finish()
            }
        })
    }

    private fun uploadVideoPost(caption: String, file: File) {
        toast("Uploading...")
            PostManager.getInstance()
                .createVideoPost(caption, file) { _, error ->
                    if (error == null) {
                        toast("Post Shared")
                        finish()
                    } else {
                        toast(error.message)
                    }
                }
    }
}
