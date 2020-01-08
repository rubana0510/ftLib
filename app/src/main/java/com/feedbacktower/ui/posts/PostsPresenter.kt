package com.feedbacktower.ui.posts

import com.feedbacktower.data.models.Post
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.network.manager.SuggestionsManager
import com.feedbacktower.ui.base.BasePresenterImpl

class PostsPresenter : BasePresenterImpl<PostsContract.View>(),
    PostsContract.Presenter {
    override fun fetchBusinessPosts(businessId: String, timestamp: String?) {
        view?.showProgress()
        PostManager.getInstance().getBusinessPosts(businessId, timestamp) { response, error ->
            view?.dismissProgress()
            if (error != null) {
                view?.showNetworkError(error)
                return@getBusinessPosts
            }
            view?.onBusinessPostsFetched(response, timestamp)
        }
    }

    override fun deletePost(postId: String) {
        view?.showProgress()
        PostManager.getInstance().deletePost(postId) onResponse@{ _, error ->
            view?.dismissProgress()
            if (error != null) {
                view?.showNetworkError(error)
                return@onResponse
            }
            view?.onPostDeleted(postId)
        }
    }

    override fun onEditPostCaption(postId: String, caption: String) {
        view?.showProgress()
        PostManager.getInstance()
            .editTextPost(postId, caption) { _, error ->
            view?.dismissProgress()
            if (error != null) {
                view?.showNetworkError(error)
                return@editTextPost
            }
            view?.onPostDeleted(postId)
        }
    }

    override fun onLikeDislike(postId: String, position: Int) {
        //view?.showProgress()
        PostManager.getInstance()
            .likePost(postId) { response, error ->
                //view?.dismissProgress()
                if (error != null) {
                    view?.showNetworkError(error)
                    return@likePost
                }
                view?.onLikedDisliked(response, position)
            }
    }
}