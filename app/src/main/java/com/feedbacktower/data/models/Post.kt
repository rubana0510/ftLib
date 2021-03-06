package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

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
) : BaseModel(id), Serializable {

    val isLiked: Boolean
        get() = liked == 1
    val likeText: String
        get() {
            return when (totalLikes) {
                0 -> "Like"
                1 -> "$totalLikes like"
                else -> "$totalLikes likes"
            }
        }
    val isPhoto: Boolean
        get() = type == "PHOTO"

}