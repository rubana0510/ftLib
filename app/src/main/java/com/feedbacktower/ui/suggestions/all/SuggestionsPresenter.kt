package com.feedbacktower.ui.suggestions.all

import com.feedbacktower.network.manager.SuggestionsManager
import com.feedbacktower.ui.base.BasePresenterImpl

class SuggestionsPresenter : BasePresenterImpl<SuggestionsContract.View>(),
    SuggestionsContract.Presenter {
    override fun fetch(timestamp: String, initial: Boolean) {
        getView()?.showProgress()
        SuggestionsManager.getInstance()
            .getSuggestions(timestamp) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@getSuggestions
                }
                getView()?.onFetched(response, initial)
            }
    }
}