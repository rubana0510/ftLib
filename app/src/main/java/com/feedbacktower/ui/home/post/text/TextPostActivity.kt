package com.feedbacktower.ui.home.post.text

import android.os.Bundle
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.ui.base.BaseViewActivityImpl
import com.feedbacktower.util.disable
import com.feedbacktower.util.enable
import kotlinx.android.synthetic.main.activity_post_text_screen.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class TextPostActivity : BaseViewActivityImpl(), TextPostContract.View {

    companion object {
        const val TEXT = "TEXT"
        const val POST_ID = "POST_ID"
    }

    @Inject
    lateinit var presenter: TextPostPresenter
    private var postId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_text_screen)
        (applicationContext as App).appComponent.uploadPostComponent().create().inject(this)
        presenter.attachView(this)
        intent?.apply {
            postId = getStringExtra(POST_ID)
            editText.setText(getStringExtra(TEXT))
        }
        postButton.setOnClickListener {
            val text: String? = editText.text.toString()

            if (text.isNullOrEmpty()) return@setOnClickListener

            if (postId == null) {
                presenter.postText(text)
            } else {
                presenter.editPostText(text, postId!!)
            }
        }
    }

    override fun showProgress() {
        super.showProgress()
        postButton.disable()
    }

    override fun dismissProgress() {
        super.dismissProgress()
        postButton.enable()
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        toast(error.message)
    }

    override fun onTextEdited() {
        toast("Post edited")
        finish()
    }

    override fun onTextPosted() {
        toast("Post shared")
        finish()
    }
}
