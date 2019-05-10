package com.feedbacktower.data.models

import com.feedbacktower.BuildConfig
import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id")
    val postId: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("businessId")
    val businessId: String,
    @SerializedName("businessName")
    val businessName: String,
    @SerializedName("text")
    val postCaption: String,
    @SerializedName("media")
    val postMediaUrl: String,
    @SerializedName("type")
    val postType: String,
    val postAddedAt: String,
    @SerializedName("totalLikes")
    var postLikes: Int,
    @SerializedName("business")
    val business: Business,
    @SerializedName("liked")
    var liked: Int,
    @SerializedName("user")
    val user: User
) {

    val businessProfileImage: String
        get() = BuildConfig.SERVER_BASE_URL + "/user/" + userId

    data class Business(
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String
    )

    data class User(
        @SerializedName("firstName")
        val firstName: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("lastName")
        val lastName: String
    )
}