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
    var isCategoriesLoading = false
    override fun toggleInterest(interest: BusinessCategory) {
        GlobalScope.launch(Dispatchers.Main) {
            //view?.showProgress()
            val response = apiService.setBusinessCategoryInterestAsync(
                hashMapOf("businessCategoryId" to interest.id, "interest" to interest.selected)
            ).awaitNetworkRequest()
            // view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onToggled()
        }
    }

    override fun fetch(offset: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            if (offset == 0)
                view?.showProgress()
            isCategoriesLoading = true
            val response = apiService.getCategoriesAsync(offset = offset).awaitNetworkRequest()
            isCategoriesLoading = false
            if (offset == 0)
                view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onFetched(offset, response.payload)
        }
    }
}