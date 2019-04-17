package com.feedbacktower.data.models

data class Suggestion(
    val suggestionId: String,
    val userId: String,
    val userFullName: String,
    val userProfileImage: String,
    val businessId: String,
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