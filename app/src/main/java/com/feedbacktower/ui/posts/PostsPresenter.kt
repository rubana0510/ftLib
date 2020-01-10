package com.feedbacktower.ui.posts

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class PostsPresenter @Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<PostsContract.View>(),
    PostsContract.Presenter {
    override fun fetchBusinessPosts(businessId: String, timestamp: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.getBusinessPostsAsync(businessId, timestamp).awaitNetworkRequest();
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onBusinessPostsFetched(response.payload, timestamp)
        }
    }

    override fun deletePost(postId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.deletePost(postId).awaitNetworkRequest();
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onPostDeleted(postId)
        }
    }

    override fun onEditPostCaption(postId: String, caption: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.editTextPostAsync(postId, hashMapOf("text" to caption)).awaitNetworkRequest();
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onPostEdited(postId, caption)
        }
    }

    override fun onLikeDislike(postId: String, position: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            //view?.showProgress()
            val response = apiService.likePostAsync(postId).awaitNetworkRequest();
            //view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onLikedDisliked(response.payload, position)
        }
    }
}