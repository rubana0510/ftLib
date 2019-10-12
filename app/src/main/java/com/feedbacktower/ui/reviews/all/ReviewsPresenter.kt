package com.feedbacktower.ui.reviews.all

import com.feedbacktower.network.manager.ReviewsManager
import com.feedbacktower.ui.base.BasePresenterImpl

class ReviewsPresenter : BasePresenterImpl<ReviewsContract.View>(),
    ReviewsContract.Presenter {
    override fun fetchReviews(businessId: String, timestamp: String, initial: Boolean) {
        getView()?.showProgress()
        ReviewsManager.getInstance()
            .getBusinessReviews(businessId, timestamp) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@getBusinessReviews
                }
                getView()?.onReviewsFetched(response, initial)
            }
    }
}