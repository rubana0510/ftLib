package com.feedbacktower.network.models

data class SearchBusiness(
    val userId: String,
    val businessId: String,
    val businessName: String,
    val businessCategory: String,
    val cityName: String,
    val businessProfileImage: String,
    val averageRatings: Double
) {
    val averageRatingsDisplay: String
        get() {
            return averageRatings.toString()
        }
}