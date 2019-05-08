package com.feedbacktower.network.models

import com.google.gson.annotations.SerializedName

data class SearchBusiness(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("id")
    val businessId: String,
    @SerializedName("name")
    val businessName: String,
    @SerializedName("businessCategory")
    val businessCategory: String,
    @SerializedName("city")
    val cityName: String,
    @SerializedName("totalRating")
    val averageRatings: Double
) {
    val averageRatingsDisplay: String
        get() {
            return averageRatings.toString()
        }


    val businessProfileImage: String = ""
}