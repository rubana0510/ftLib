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
    var isCategoriesLoading = false
    override fun fetch(keyword: String?, categoryId: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.searchBusinessAsync(keyword, categoryId).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onFetched(response.payload)
        }
    }

    override fun fetchCategories(offset: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            if (offset == 0) {
                view?.showProgress()
            }
            isCategoriesLoading = true
            val response = apiService.getCategoriesAsync(offset = offset).awaitNetworkRequest()
            isCategoriesLoading = false
            if (offset == 0) {
                view?.dismissProgress()
            }
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onCategoriesFetched(response.payload)
        }
    }
}