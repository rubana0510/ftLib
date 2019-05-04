package com.feedbacktower.network.models

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("user")
    val user: User
) {
    data class User(
        @SerializedName("business")
        val business: Business,
        @SerializedName("city")
        val city: Any?,
        @SerializedName("dob")
        val dob: String,
        @SerializedName("emailId")
        val emailId: String,
        @SerializedName("firstName")
        val firstName: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("lastName")
        val lastName: String,
        @SerializedName("phone")
        val phone: String,
        @SerializedName("profileSetup")
        val profileSetup: Boolean,
        @SerializedName("userType")
        val userType: String
    ) {
        data class Business(
            @SerializedName("address")
            val address: String,
            @SerializedName("avgReview")
            val avgReview: Int,
            @SerializedName("businessCategory")
            val businessCategory: String,
            @SerializedName("businessCategoryId")
            val businessCategoryId: String,
            @SerializedName("cityId")
            val cityId: String,
            @SerializedName("createdAt")
            val createdAt: String,
            @SerializedName("discountAmount")
            val discountAmount: String,
            @SerializedName("emailId")
            val emailId: String,
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
            @SerializedName("rank")
            val rank: Int,
            @SerializedName("regNo")
            val regNo: String,
            @SerializedName("status")
            val status: String,
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
            data class Location(
                @SerializedName("coordinates")
                val coordinates: List<Double>?,
                @SerializedName("type")
                val type: String
            ) {
                val latitude: Double?
                    get() {
                        if (coordinates != null && coordinates.size > 1)
                            return coordinates[0]
                        return null
                    }
                val longitude: Double?
                    get() {
                        if (coordinates != null && coordinates.size > 1)
                            return coordinates[1]
                        return null
                    }
            }
        }
    }
}