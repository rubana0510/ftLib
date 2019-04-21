package com.feedbacktower.network.models

data class BusinessDetails(
    val userId: String,
    val businessId: String,
    val businessName: String,
    val businessRegNo: String,
    val businessCategoryId: String,
    val businessCategoryName: String,
    val businessProfileImage: String,
    val businessWebsite: String,
    val businessTotalReviews: Int,
    val businessAddress: String,
    val businessPhone: String,
    val businessAverageRatings: String,
    val businessStatus: String,
    val businessCreatedAt: String
)