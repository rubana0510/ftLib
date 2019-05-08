package com.feedbacktower.network.models


import com.feedbacktower.BuildConfig
import com.feedbacktower.data.models.Review
import com.feedbacktower.data.models.Suggestion
import com.google.gson.annotations.SerializedName

data class BusinessDetailsResponse(
    @SerializedName("business")
    val business: Business
) {
    data class Business(
        @SerializedName("address")
        val address: String,
        @SerializedName("businessCategory")
        val businessCategory: BusinessCategory,
        @SerializedName("businessCategoryId")
        val businessCategoryId: String,
        @SerializedName("city")
        val city: Any,
        @SerializedName("cityId")
        val cityId: Any,
        @SerializedName("createdAt")
        val createdAt: String,
        @SerializedName("currentLocation")
        val currentLocation: Any,
        @SerializedName("discountAmount")
        val discountAmount: String,
        @SerializedName("emailId")
        val emailId: Any,
        @SerializedName("featured")
        val featured: Boolean,
        @SerializedName("id")
        val id: String,
        @SerializedName("location")
        val location: Location,
        @SerializedName("name")
        val name: String,
        @SerializedName("phone")
        val phone: String,
        @SerializedName("planStatus")
        val planStatus: String,
        @SerializedName("rank")
        val rank: Int,
        @SerializedName("regNo")
        val regNo: String,
        @SerializedName("reviews")
        val reviews: List<Review>,
        @SerializedName("status")
        val status: String,
        @SerializedName("suggestions")
        val suggestions: List<Suggestion>,
        @SerializedName("totalRating")
        val totalRating: Int,
        @SerializedName("totalReviews")
        val totalReviews: Int,
        @SerializedName("totalSuggestions")
        val totalSuggestions: Int,
        @SerializedName("updatedAt")
        val updatedAt: String,
        @SerializedName("userId")
        val userId: String,
        @SerializedName("visible")
        val visible: Boolean,
        @SerializedName("walletAmount")
        val walletAmount: String,
        @SerializedName("website")
        val website: String
    ) {
        val businessProfileImage: String
            get() = BuildConfig.SERVER_BASE_URL + "/user/" + userId

        data class BusinessCategory(
            @SerializedName("createdAt")
            val createdAt: String,
            @SerializedName("featured")
            val featured: Boolean,
            @SerializedName("id")
            val id: String,
            @SerializedName("masterBusinessCategoryId")
            val masterBusinessCategoryId: Int,
            @SerializedName("name")
            val name: String,
            @SerializedName("popular")
            val popular: Int,
            @SerializedName("updatedAt")
            val updatedAt: String,
            @SerializedName("visible")
            val visible: Boolean
        )

        data class Location(
            @SerializedName("coordinates")
            val coordinates: List<Double>,
            @SerializedName("type")
            val type: String
        )
    }
}