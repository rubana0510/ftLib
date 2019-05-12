package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("businessId")
    val businessId: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("media")
    val media: String?,
    @SerializedName("visible")
    val visible: Boolean,
    @SerializedName("totalLikes")
    var totalLikes: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("liked")
    var liked: Int,
    @SerializedName("user")
    val user: User,
    @SerializedName("business")
    val business: Business
): BaseModel(id) {

    data class Business(
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String
    )

    data class User(
        @SerializedName("id")
        val id: String,
        @SerializedName("firstName")
        val firstName: String,
        @SerializedName("lastName")
        val lastName: String
    )
}