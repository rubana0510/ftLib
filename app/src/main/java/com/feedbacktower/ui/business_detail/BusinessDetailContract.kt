package com.feedbacktower.ui.business_detail

import com.feedbacktower.network.models.BusinessDetailsResponse
import com.feedbacktower.network.models.GetPostsResponse
import com.feedbacktower.network.models.GetReviewsResponse
import com.feedbacktower.network.models.LikeUnlikeResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface BusinessDetailContract {
    interface View : BaseView {
        fun onReviewsFetched(response: GetReviewsResponse?)
        fun onBusinessDetailFetched(response: BusinessDetailsResponse?)
        fun onPostsFetched(response: GetPostsResponse?)
        fun onLikePostResponse(liked: Boolean, position: Int)
    }

    interface Presenter : BasePresenter<View> {
        fun fetchReviews(businessId: String)
        fun fetchBusinessDetails(businessId: String)
        fun fetchPosts(businessId: String, timestamp: String?)
        fun likePost(postId: String, position: Int)
    }
}