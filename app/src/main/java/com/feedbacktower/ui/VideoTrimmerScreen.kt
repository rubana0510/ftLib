package com.feedbacktower.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.net.toFile
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
        var file = File(filesDir , "Videos")
        if(!file.exists()){
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
                val file: File? = uriToFile(uri)
                file?.let {
                    Log.d("VideoTrimmer", "File Size: ${it.length()}")
                    val size = it.length() / (1024 * 1024)
                    if (size > Constants.Media.MAX_VIDEO_SIZE) {
                        toast("Video size must be less than ${Constants.Media.MAX_VIDEO_SIZE}")
                        finish()
                    } else {
                        toast("Uploading video")
                        compressVideo(uri, caption)
                    }
                }
            }

            override fun cancelAction() {
                finish()
            }
        })
    }

    private fun compressVideo(uri: Uri, caption: String) {
        uploadVideoPost(caption, uri)
//        val task = VideoCompress.compressVideoLow(
//            uri.toFile().absolutePath,
//            File(filesDir , "Videos").absolutePath,
//            object : VideoCompress.CompressListener {
//                override fun onStart() {
//                  toast("Compressing video")
//                }
//
//                override fun onSuccess() {
//                    //Finish successfully
//                    uploadVideoPost(caption, uri)
//                }
//
//                override fun onFail() {
//                    //Failed
//                }
//
//                override fun onProgress(percent: Float) {
//                    //Progress
//                }
//            })

    }

    private fun uploadVideoPost(caption: String, fileUri: Uri?) {
toast("Uploading...")
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
