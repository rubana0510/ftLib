package com.feedbacktower.ui.suggestions.my

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MySuggestionsPresenter @Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<MySuggestionsContract.View>(),
    MySuggestionsContract.Presenter {
    override fun fetch(timestamp: String, initial: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            getView()?.showProgress()
            val response = apiService.getMySuggestionsAsync(timestamp).awaitNetworkRequest()
            getView()?.dismissProgress()
            if (response.error != null) {
                getView()?.showNetworkError(response.error)
                return@launch
            }
            getView()?.onFetched(response.payload, initial)
        }
    }
}