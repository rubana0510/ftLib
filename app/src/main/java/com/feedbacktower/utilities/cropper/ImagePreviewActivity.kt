package com.feedbacktower.utilities.cropper

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.feedbacktower.R
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.util.uriToFile
import kotlinx.android.synthetic.main.activity_image_preview.*
import org.jetbrains.anko.toast

class ImagePreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)
        imageView.setImageURI(intent?.getParcelableExtra("URI"))
        postButton.setOnClickListener {
            val caption = postCaption.text.toString().trim()
            uploadImagePost(caption, intent?.getParcelableExtra<Uri>("URI"))

        }
    }

    private fun uploadImagePost(caption: String, fileUri: Uri?) {
        fileUri?.let {
            val file = uriToFile(it)
            PostManager.getInstance()
                .createPhotoPost(caption, file){_,error->

                    if(error == null){
                        toast("Uploaded successfully")
                        finish()
                    }else{
                        toast(error.message?:"Error")
                    }

                }
        }
    }
}
