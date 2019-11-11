package com.feedbacktower.ui.reviews.business

import com.feedbacktower.di.reviews.ReviewsScope
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.makeNetworkRequest
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
            getView()?.showProgress()
            val response = apiService.getBusinessReviewsAsync(
                businessId = businessId,
                timestamp = timestamp
            ).makeNetworkRequest()
            getView()?.dismissProgress()
            if (response.error != null) {
                getView()?.showNetworkError(response.error)
                return@launch
            }
            getView()?.onReviewsFetched(response.payload, initial)
        }
    }
}