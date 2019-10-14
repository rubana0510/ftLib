package com.feedbacktower.ui.posts

import com.feedbacktower.data.models.Post
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.network.manager.SuggestionsManager
import com.feedbacktower.ui.base.BasePresenterImpl

class PostsPresenter : BasePresenterImpl<PostsContract.View>(),
    PostsContract.Presenter {
    override fun fetchBusinessPosts(businessId: String, timestamp: String?) {
        getView()?.showProgress()
        PostManager.getInstance().getBusinessPosts(businessId, timestamp) { response, error ->
            getView()?.dismissProgress()
            if (error != null) {
                getView()?.showNetworkError(error)
                return@getBusinessPosts
            }
            getView()?.onBusinessPostsFetched(response, timestamp)
        }
    }

    override fun deletePost(postId: String) {
        getView()?.showProgress()
        PostManager.getInstance().deletePost(postId) onResponse@{ _, error ->
            getView()?.dismissProgress()
            if (error != null) {
                getView()?.showNetworkError(error)
                return@onResponse
            }
            getView()?.onPostDeleted(postId)
        }
    }

    override fun onEditPostCaption(postId: String, caption: String) {
        getView()?.showProgress()
        PostManager.getInstance()
            .editTextPost(postId, caption) { _, error ->
            getView()?.dismissProgress()
            if (error != null) {
                getView()?.showNetworkError(error)
                return@editTextPost
            }
            getView()?.onPostDeleted(postId)
        }
    }

    override fun onLikeDislike(postId: String, position: Int) {
        //getView()?.showProgress()
        PostManager.getInstance()
            .likePost(postId) { response, error ->
                //getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@likePost
                }
                getView()?.onLikedDisliked(response, position)
            }
    }
}