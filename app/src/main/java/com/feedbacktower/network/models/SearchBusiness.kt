package com.feedbacktower.network.models

import com.feedbacktower.data.models.BaseModel
import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.data.models.City
import com.google.gson.annotations.SerializedName

data class SearchBusiness(
    @SerializedName("businessCategory")
    val businessCategory: BusinessCategory,
    @SerializedName("businessCategoryId")
    val businessCategoryId: String,
    @SerializedName("city")
    val city: City,
    @SerializedName("id")
    var businessId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("available")
    var available: Boolean,
    @SerializedName("totalRating")
    val totalRating: Int,
    @SerializedName("avgRating")
    var avgRating: Double,
    @SerializedName("totalReview")
    val totalReview: Int,
    @SerializedName("userId")
    val userId: String
) : BaseModel(businessId)