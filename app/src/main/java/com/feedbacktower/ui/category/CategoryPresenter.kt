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
    var isCategoriesLoading = false
    override fun fetch(keyword: String, offset: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            isCategoriesLoading = true
            val response = apiService.getCategoriesAsync(keyword, offset).awaitNetworkRequest()
            isCategoriesLoading = false
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onFetched(keyword, offset, response.payload)
        }
    }
}