package com.feedbacktower.ui.home.feed

import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetAdsResponse
import com.feedbacktower.network.models.GetPostsResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface HomeContract {
    interface View : BaseView {
        fun onAdsFetched(response: GetAdsResponse?)
        fun onPostsFetched(response: GetPostsResponse?, timestamp: String?)
        fun showAdsError(error: ApiResponse.ErrorModel)
        fun showPostsError(error: ApiResponse.ErrorModel)
    }

    interface Presenter : BasePresenter<View> {
        fun fetchAds()
        fun fetchPosts(timestamp: String?)
    }
}