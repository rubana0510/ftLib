package com.feedbacktower.ui.suggestions.my

import com.feedbacktower.network.manager.SuggestionsManager
import com.feedbacktower.ui.base.BasePresenterImpl

class MySuggestionsPresenter : BasePresenterImpl<MySuggestionsContract.View>(),
    MySuggestionsContract.Presenter {
    override fun fetch(timestamp: String, initial: Boolean) {
        getView()?.showProgress()
        SuggestionsManager.getInstance()
            .getMySuggestions(timestamp) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@getMySuggestions
                }
                getView()?.onFetched(response, initial)
            }
    }
}