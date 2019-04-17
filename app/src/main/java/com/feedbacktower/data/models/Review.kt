package com.feedbacktower.data.models

data class Review(
    val reviewId: String,
    val userId: String,
    val userFullName: String,
    val userProfileImage : String,
    val businessId: String,
    val reviewMessage: String,
    val reviewScore: String,
    val createdAt: String
)