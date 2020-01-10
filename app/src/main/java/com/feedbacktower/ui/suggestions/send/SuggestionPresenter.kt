package com.feedbacktower.ui.suggestions.send

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SuggestionPresenter @Inject constructor(
    private val apiService: ApiService
) {
    private var view: SuggestionView? = null
    fun attachView(view: SuggestionView) {
        this.view = view
    }

    fun destroView() {
        view = null
    }

    fun sendSuggestion(businessId: String, message: String) {
        val map = hashMapOf<String, Any?>(
            "businessId" to businessId,
            "message" to message
        )
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.addSuggestionAsync(map).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onSentSuccess()
        }
    }

}