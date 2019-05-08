package com.feedbacktower.network.models


import com.google.gson.annotations.SerializedName

data class SearchBusinessResponse(
    @SerializedName("businesses")
    val businesses: List<SearchBusiness>
) {
    data class Businesse(
        @SerializedName("businessCategory")
        val businessCategory: Any,
        @SerializedName("businessCategoryId")
        val businessCategoryId: Any,
        @SerializedName("city")
        val city: Any,
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("totalRating")
        val totalRating: Int,
        @SerializedName("userId")
        val userId: String
    )
}