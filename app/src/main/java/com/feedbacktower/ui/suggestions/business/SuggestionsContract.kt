package com.feedbacktower.ui.suggestions.business
import com.feedbacktower.network.models.GetSuggestionsResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface SuggestionsContract {
    interface View : BaseView {
        fun onFetched(response: GetSuggestionsResponse?, initial: Boolean = false)
    }

    interface Presenter : BasePresenter<View> {
        fun fetch(timestamp: String, initial: Boolean)
    }
}