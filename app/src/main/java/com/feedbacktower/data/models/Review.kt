package com.feedbacktower.data.models

import com.feedbacktower.BuildConfig
import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("id")
    val reviewId: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("status")
    val userFullName: String = "Unknown",
    val userProfileImage : String = BuildConfig.SERVER_BASE_URL + "/User/" + userId,
    val businessId: String,
    @SerializedName("comment")
    val reviewMessage: String,
    @SerializedName("rating")
    val reviewScore: String,
    val createdAt: String
)