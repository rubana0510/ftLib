package com.feedbacktower.ui.suggestions.business.reply

import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface ReplySuggestionContract {
    interface View : BaseView {
        fun onReplySent()
    }

    interface Presenter : BasePresenter<View> {
        fun reply(suggestionId: String, replyMessage: String)
    }
}