package com.feedbacktower.network.models

import com.feedbacktower.data.models.BaseModel
import com.feedbacktower.data.models.City
import com.feedbacktower.util.Constants
import com.google.gson.annotations.SerializedName

data class SearchBusiness(
    @SerializedName("businessCategory")
    val businessCategory: Any,
    @SerializedName("businessCategoryId")
    val businessCategoryId: String,
    @SerializedName("city")
    val city: City,
    @SerializedName("id")
    var businessId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("totalRating")
    val totalRating: Int,
    @SerializedName("totalReview")
    val totalReview: Int,
    @SerializedName("userId")
    val userId: String
) : BaseModel(businessId) {
    val businessProfileImage: String = Constants.Service.Secrets.BASE_URL
    val averageRatingsDisplay: String?
        get() {
            if (totalReview == 0) return null
            return (totalRating / totalReview).toString()
        }
}