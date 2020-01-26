package com.feedbacktower.ui.home.feed

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomePresenter
@Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<HomeContract.View>(),
    HomeContract.Presenter {
    override fun fetchAds() {
        GlobalScope.launch(Dispatchers.Main) {
            val response = apiService.getAdsAsync().awaitNetworkRequest()
            if (response.error != null) {
                view?.showPostsError(response.error)
                return@launch
            }
            view?.onAdsFetched(response.payload)
        }
    }

    override fun fetchPosts(timestamp: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            //view?.showProgress()
            val response = apiService.getPostsAsync(timestamp).awaitNetworkRequest()
            //view?.dismissProgress()
            if (response.error != null) {
                view?.showPostsError(response.error)
                return@launch
            }
            view?.onPostsFetched(response.payload, timestamp)
        }
    }

    override fun likePost(postId: String, position: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = apiService.likePostAsync(postId).awaitNetworkRequest()
            if (response.error != null) {
                view?.showAdsError(response.error)
                return@launch
            }
            response.payload?.let {
                view?.onLikePostResponse(it.liked, position)
            }
        }
    }

}