package com.feedbacktower.ui.business_details

import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.manager.ReviewsManager
import com.feedbacktower.ui.base.BasePresenterImpl

class BusinessDetailPresenter : BasePresenterImpl<BusinessDetailContract.View>(),
    BusinessDetailContract.Presenter {

    override fun fetchPosts(businessId: String, timestamp: String?) {
        PostManager.getInstance()
            .getBusinessPosts(businessId, timestamp) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@getBusinessPosts
                }
                getView()?.onPostsFetched(response)
            }
    }

    override fun fetchReviews(businessId: String) {
        ReviewsManager.getInstance()
            .getBusinessReviews(businessId, "") { response, error ->
                //  getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@getBusinessReviews
                }
                getView()?.onReviewsFetched(response)
            }
    }

    override fun fetchBusinessDetails(businessId: String) {
        getView()?.showProgress()
        ProfileManager.getInstance()
            .getBusinessDetails(businessId) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@getBusinessDetails
                }
                getView()?.onBusinessDetailFetched(response)
            }
    }

}