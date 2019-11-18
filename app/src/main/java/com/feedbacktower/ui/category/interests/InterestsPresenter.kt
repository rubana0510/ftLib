package com.feedbacktower.ui.category.interests

import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class InterestsPresenter
@Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<InterestsContract.View>(),
    InterestsContract.Presenter {
    override fun toggleInterest(interest: BusinessCategory) {
        GlobalScope.launch(Dispatchers.Main) {
            getView()?.showProgress()
            val response = apiService.setBusinessCategoryInterestAsync(
                hashMapOf("businessCategoryId" to interest.id, "interest" to interest.selected)
            ).awaitNetworkRequest()
            getView()?.dismissProgress()
            if (response.error != null) {
                getView()?.showNetworkError(response.error)
                return@launch
            }
            getView()?.onToggled()
        }
    }

    override fun fetch(keyword: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            getView()?.showProgress()
            val response = apiService.getFeaturedCategoriesAsync(keyword).awaitNetworkRequest()
            getView()?.dismissProgress()
            if (response.error != null) {
                getView()?.showNetworkError(response.error)
                return@launch
            }
            getView()?.onFetched(response.payload)
        }
    }
}