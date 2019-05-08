package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName

data class Suggestion(
    @SerializedName("id")
    val suggestionId: String,
    @SerializedName("userId")
    val userId: String,
    val userFullName: String,
    val userProfileImage: String,
    val businessId: String,
    @SerializedName("message")
    val message: String,
    val reply: String?,
    val suggesterType: String,
    val repliedAt: String,
    val createdAt: String
) {
    val isReplied: Boolean
        get() {
            return !reply.isNullOrEmpty()
        }
}