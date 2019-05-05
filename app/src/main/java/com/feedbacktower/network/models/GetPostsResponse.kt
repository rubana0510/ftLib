package com.feedbacktower.network.models

import com.feedbacktower.data.models.Post
import com.google.gson.annotations.SerializedName

data class GetPostsResponse(
    @SerializedName("posts")
    val posts: List<Post>
)