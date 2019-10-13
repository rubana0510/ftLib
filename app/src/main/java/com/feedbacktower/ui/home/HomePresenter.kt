package com.feedbacktower.ui.home

import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.ui.base.BasePresenterImpl

class HomePresenter : BasePresenterImpl<HomeContract.View>(),
    HomeContract.Presenter {
    override fun fetchAds() {
        PostManager.getInstance()
            .getAds{ response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showPostsError(error)
                    return@getAds
                }
                getView()?.onAdsFetched(response)
            }
    }

    override fun fetchPosts(timestamp: String?) {
        PostManager.getInstance()
            .getPosts(timestamp) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showAdsError(error)
                    return@getPosts
                }
                getView()?.onPostsFetched(response, timestamp)
            }
    }

}