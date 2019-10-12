package com.feedbacktower.ui.suggestions.my
import com.feedbacktower.network.models.GetSuggestionsResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface MySuggestionsContract {
    interface View : BaseView {
        fun onFetched(response: GetSuggestionsResponse?, initial: Boolean = false)
    }

    interface Presenter : BasePresenter<View> {
        fun fetch(timestamp: String, initial: Boolean)
    }
}