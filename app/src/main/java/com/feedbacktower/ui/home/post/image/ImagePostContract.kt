package com.feedbacktower.ui.home.post.image

import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView
import java.io.File

interface ImagePostContract {
    interface View : BaseView {
        fun onImagePosted()
    }

    interface Presenter : BasePresenter<View> {
        fun postImage(file: File, caption: String)
    }
}