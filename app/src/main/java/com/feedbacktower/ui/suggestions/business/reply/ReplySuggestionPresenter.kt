package com.feedbacktower.ui.suggestions.business.reply

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReplySuggestionPresenter @Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<ReplySuggestionContract.View>(),
    ReplySuggestionContract.Presenter {
    override fun reply(suggestionId: String, replyMessage: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.replySuggestionAsync(
                hashMapOf("id" to suggestionId, "reply" to replyMessage)
            ).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onReplySent()
        }
    }
}