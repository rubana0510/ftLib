package com.feedbacktower.ui.search

import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchPresenter @Inject constructor(
    private val apiService: ApiService,
    private val appPrefs: ApplicationPreferences
) : BasePresenterImpl<SearchContract.View>(),
    SearchContract.Presenter {
    override fun fetch(keyword: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.searchBusinessAsync(keyword).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onFetched(response.payload)
        }
    }
}