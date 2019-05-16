package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName

data class Suggestion(
    @SerializedName("businessId")
    val businessId: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("repliedAt")
    val repliedAt: String,
    @SerializedName("reply")
    val reply: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("visible")
    val visible: Boolean
) {
    val isReplied: Boolean
    get() {
        return !reply.isNullOrEmpty()
    }
    data class User(
        @SerializedName("firstName")
        val firstName: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("lastName")
        val lastName: String
    )
}