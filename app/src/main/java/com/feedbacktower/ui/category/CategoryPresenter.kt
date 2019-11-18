package com.feedbacktower.ui.category

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryPresenter
@Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<CategoryContract.View>(),
    CategoryContract.Presenter {
    override fun fetch(keyword: String) {
        GlobalScope.launch(Dispatchers.Main) {
            getView()?.showProgress()
            val response = apiService.getCategoriesAsync(keyword).awaitNetworkRequest()
            getView()?.dismissProgress()
            if (response.error != null) {
                getView()?.showNetworkError(response.error)
                return@launch
            }
            getView()?.onFetched(response.payload)
        }
    }
}