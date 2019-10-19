package com.feedbacktower.ui.business_details

import com.feedbacktower.network.models.BusinessDetailsResponse
import com.feedbacktower.network.models.GetPostsResponse
import com.feedbacktower.network.models.GetReviewsResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface BusinessDetailContract {
    interface View : BaseView {
        fun onReviewsFetched(response: GetReviewsResponse?)
        fun onBusinessDetailFetched(response: BusinessDetailsResponse?)
        fun onPostsFetched(response: GetPostsResponse?)
    }

    interface Presenter : BasePresenter<View> {
        fun fetchReviews(businessId: String)
        fun fetchBusinessDetails(businessId: String)
        fun fetchPosts(businessId: String, timestamp: String?)
    }
}