package com.feedbacktower.ui.suggestions.business

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SuggestionsPresenter @Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<SuggestionsContract.View>(),
    SuggestionsContract.Presenter {
    override fun fetch(timestamp: String, initial: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.getSuggestionsAsync(timestamp).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onFetched(response.payload, initial)
        }
    }
}