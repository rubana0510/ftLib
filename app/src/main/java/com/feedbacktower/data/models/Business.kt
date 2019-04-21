package com.feedbacktower.data.models

data class Business(
    val userId: String,
    val businessId: String,
    val businessName: String,
    val businessRegNo: String,
    val businessCategoryId: String,
    val businessCategoryName: String,
    val businessWebsite: String,
    val businessAddress: String,
    val businessWalletAmount: Double,
    val businessDiscountAmount: Double,
    val businessPhone: String,
    val businessStatus: String,
    val businessCreatedAt: String
)