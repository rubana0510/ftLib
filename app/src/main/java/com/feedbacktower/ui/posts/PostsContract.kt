package com.feedbacktower.ui.posts

import com.feedbacktower.data.models.Post
import com.feedbacktower.network.models.GetPostsResponse
import com.feedbacktower.network.models.GetSuggestionsResponse
import com.feedbacktower.network.models.LikeUnlikeResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface PostsContract {
    interface View : BaseView {
        fun onBusinessPostsFetched(response: GetPostsResponse?, timestamp: String?)
        fun onPostDeleted(postId: String)
        fun onPostEdited(postId: String, caption: String)
        fun onLikedDisliked(response: LikeUnlikeResponse?, position: Int)
    }

    interface Presenter : BasePresenter<View> {
        fun fetchBusinessPosts(businessId: String, timestamp: String?)
        fun deletePost(postId: String)
        fun onEditPostCaption(postId: String, caption: String)
        fun onLikeDislike(postId: String, position: Int)
    }
}