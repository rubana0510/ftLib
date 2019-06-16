package com.feedbacktower.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.net.toFile
import com.feedbacktower.R
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.util.*
import com.feedbacktower.utilities.compressor.Compressor
import kotlinx.android.synthetic.main.activity_image_preview.*
import org.jetbrains.anko.toast
import java.lang.IllegalArgumentException

class ImagePreviewActivity : AppCompatActivity() {
private var postId: String? = null
private var mediaUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)
        postId = intent?.getStringExtra("POST_ID")
        mediaUrl = intent?.getStringExtra("MEDIA_URL")
        val uri: Uri = intent?.getParcelableExtra("URI") ?: throw IllegalArgumentException("Invalid args")

        imageView.setImageURI(uri)
        postButton.setOnClickListener {
            val caption = postCaption.text.toString().trim()
            uploadImagePost(caption, uri)

        }
    }

    private fun uploadImagePost(caption: String, fileUri: Uri?) {

        progressBar.visible()
        postButton.disable()
        fileUri?.let {
            val rawFile = fileUri.toFile()
            Log.d("ImagePreview", "Raw image filesize: ${rawFile.length() / 1024}KB")
            val file = Compressor(this)
                .setQuality(Constants.IMAGE_COMPRESSION_QUALITY)
                .compressToFile(rawFile)
            Log.d("ImagePreview", "Compressed image filesize: ${file.length() / 1024}KB")
            PostManager.getInstance()
                .createPhotoPost(caption, file) { _, error ->
                    progressBar.gone()
                    if (error == null) {
                        toast("Post Shared")
                        finish()
                    } else {
                        postButton.enable()
                        toast(error.message ?: "Error")
                    }

                }
        }
    }
}
