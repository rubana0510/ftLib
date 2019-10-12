package com.feedbacktower.ui.reviews.my

import com.feedbacktower.network.manager.ReviewsManager
import com.feedbacktower.ui.base.BasePresenterImpl

class MyReviewsPresenter : BasePresenterImpl<MyReviewsContract.View>(),
    MyReviewsContract.Presenter {
    override fun fetchReviews(timestamp: String, initial: Boolean) {
        getView()?.showProgress()
        ReviewsManager.getInstance()
            .getMyReviews(timestamp) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@getMyReviews
                }
                getView()?.onReviewsFetched(response, initial)
            }
    }
}