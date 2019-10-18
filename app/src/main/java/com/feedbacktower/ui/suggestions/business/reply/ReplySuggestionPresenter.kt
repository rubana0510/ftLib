package com.feedbacktower.ui.suggestions.business.reply

import com.feedbacktower.network.manager.SuggestionsManager
import com.feedbacktower.ui.base.BasePresenterImpl

class ReplySuggestionPresenter : BasePresenterImpl<ReplySuggestionContract.View>(),
    ReplySuggestionContract.Presenter {
    override fun reply(suggestionId: String, replyMessage: String) {
        getView()?.showProgress()
        SuggestionsManager.getInstance()
            .replySuggestion(suggestionId, replyMessage) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@replySuggestion
                }
                getView()?.onReplySent()
            }
    }
}