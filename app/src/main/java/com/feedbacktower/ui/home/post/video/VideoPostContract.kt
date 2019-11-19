package com.feedbacktower.ui.home.post.video

import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView
import java.io.File

interface VideoPostContract {
    interface View : BaseView {
        fun onVideoPosted()
    }

    interface Presenter : BasePresenter<View> {
        fun postVideo(file: File, caption: String)
    }
}