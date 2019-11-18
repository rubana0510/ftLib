package com.feedbacktower.ui.home.post.image

import android.net.Uri
import android.os.Bundle
import androidx.core.net.toFile
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.ui.base.BaseViewActivityImpl
import com.feedbacktower.util.*
import com.feedbacktower.utilities.compressor.Compressor
import kotlinx.android.synthetic.main.activity_image_preview.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class ImagePostActivity : BaseViewActivityImpl(), ImagePostContract.View {

    companion object {
        const val POST_ID = "POST_ID"
        const val MEDIA_URL = "MEDIA_URL"
        const val URI = "URI"
    }

    @Inject
    lateinit var presenter: ImagePostPresenter

    private var postId: String? = null
    private var mediaUrl: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)
        (applicationContext as App).appComponent.uploadPostComponent().create().inject(this)
        presenter.attachView(this)
        postId = intent?.getStringExtra(POST_ID)
        mediaUrl = intent?.getStringExtra(MEDIA_URL)
        val uri: Uri = intent?.getParcelableExtra(URI) ?: throw IllegalArgumentException("Invalid args")

        imageView.loadImage(uri)
        postButton.setOnClickListener {
            val caption = postCaption.text.toString().trim()
            uploadImagePost(caption, uri)
        }
    }

    override fun onImagePosted() {
        toast("Post Shared")
        finish()
    }

    override fun showProgress() {
        super.showProgress()
        progressBar.visible()
        postButton.disable()
    }

    override fun dismissProgress() {
        super.dismissProgress()
        progressBar.gone()
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        postButton.enable()
        toast(error.message)
    }

    private fun uploadImagePost(caption: String, fileUri: Uri?) {
        fileUri?.let {
            showProgress()
            doAsync {
                val rawFile = fileUri.toFile()
                val file = Compressor(this@ImagePostActivity)
                    .setQuality(Constants.IMAGE_COMPRESSION_QUALITY)
                    .compressToFile(rawFile)
                uiThread {
                    presenter.postImage(file, caption)
                }
            }

        }
    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }
}
