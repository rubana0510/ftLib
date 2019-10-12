package com.feedbacktower.ui.reviews.my

import com.feedbacktower.network.models.GetReviewsResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface MyReviewsContract {
    interface View : BaseView {
        fun onReviewsFetched(response: GetReviewsResponse?, initial: Boolean = false)
    }

    interface Presenter : BasePresenter<View> {
        fun fetchReviews(timestamp: String, initial: Boolean)
    }
}