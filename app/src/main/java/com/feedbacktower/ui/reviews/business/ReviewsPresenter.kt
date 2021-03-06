package com.feedbacktower.ui.reviews.business

import com.feedbacktower.di.reviews.ReviewsScope
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@ReviewsScope
class ReviewsPresenter @Inject constructor(
    val apiService: ApiService
) : BasePresenterImpl<ReviewsContract.View>(),
    ReviewsContract.Presenter {
    override fun fetchReviews(businessId: String, timestamp: String, initial: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.getBusinessReviewsAsync(
                businessId = businessId,
                timestamp = timestamp
            ).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onReviewsFetched(response.payload, initial)
        }
    }
}