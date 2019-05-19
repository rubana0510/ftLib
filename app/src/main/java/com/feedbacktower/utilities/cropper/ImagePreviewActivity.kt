package com.feedbacktower.utilities.cropper

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.feedbacktower.R
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.util.*
import kotlinx.android.synthetic.main.activity_image_preview.*
import org.jetbrains.anko.toast
import java.lang.IllegalArgumentException

class ImagePreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)
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
            val file = uriToFile(it)
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
