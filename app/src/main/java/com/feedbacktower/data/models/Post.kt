package com.feedbacktower.data.models

data class Post(
    val postId: String,
    val businessId: String,
    val businessName: String,
    val businessProfileImage: String,
    val postCaption: String,
    val postMediaUrl: String,
    val postType: String, // TEXT, PHOTO, VIDEO
    val postAddedAt: String,
    val postLikes: Int
)