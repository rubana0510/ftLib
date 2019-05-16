package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName


data class Business(
    @SerializedName("address")
    var address: String,
    @SerializedName("businessCategory")
    var businessCategory: BusinessCategory,
    @SerializedName("businessCategoryId")
    var businessCategoryId: String,
    @SerializedName("cityId")
    var cityId: String,
    @SerializedName("createdAt")
    var createdAt: String,
    @SerializedName("discountAmount")
    var discountAmount: String,
    @SerializedName("emailId")
    var emailId: String,
    @SerializedName("featured")
    var featured: Boolean,
    @SerializedName("id")
    var id: String,
    @SerializedName("location")
    var location: Location,
    @SerializedName("city")
    val city: City,
    @SerializedName("name")
    var name: String?,
    @SerializedName("phone")
    var phone: String?,
    @SerializedName("rank")
    var rank: Int,
    @SerializedName("regNo")
    var regNo: String?,
    @SerializedName("status")
    var status: String,
    @SerializedName("totalReviews")
    var totalReviews: Int,
    @SerializedName("totalSuggestions")
    var totalSuggestions: Int,
    @SerializedName("totalRating")
    var totalRating: Double,
    @SerializedName("updatedAt")
    var updatedAt: String,
    @SerializedName("userId")
    var userId: String,
    @SerializedName("visible")
    var visible: Boolean,
    @SerializedName("walletAmount")
    var walletAmount: Double,
    @SerializedName("website")
    var website: String
) {
    val averageRatings: String
        get() {
            if (totalReviews == 0) return "-"
            else if (totalRating == 0.0) return "-"
            else return String.format("%.1f", (totalRating / totalReviews))
        }
}