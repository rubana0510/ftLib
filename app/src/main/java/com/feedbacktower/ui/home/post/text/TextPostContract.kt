package com.feedbacktower.ui.home.post.text

import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface TextPostContract {
    interface View : BaseView {
        fun onTextPosted()
        fun onTextEdited()
    }

    interface Presenter : BasePresenter<View> {
        fun postText(caption: String)
        fun editPostText(caption: String, postId: String)
    }
}