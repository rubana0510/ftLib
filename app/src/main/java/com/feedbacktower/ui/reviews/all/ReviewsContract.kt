package com.feedbacktower.ui.reviews.all

import com.feedbacktower.network.models.GetReviewsResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface ReviewsContract {
    interface View : BaseView {
        fun onReviewsFetched(response: GetReviewsResponse?, initial: Boolean = false)
    }

    interface Presenter : BasePresenter<View> {
        fun fetchReviews(businessId: String, timestamp: String, initial: Boolean)
    }
}