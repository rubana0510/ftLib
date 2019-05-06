package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName


data class Business(
    @SerializedName("address")
    var address: String,
    @SerializedName("avgReview")
    var avgReview: Int,
    @SerializedName("businessCategory")
    var businessCategory: String,
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
    @SerializedName("updatedAt")
    var updatedAt: String,
    @SerializedName("userId")
    var userId: String,
    @SerializedName("visible")
    var visible: Boolean,
    @SerializedName("walletAmount")
    var walletAmount: String,
    @SerializedName("website")
    var website: String
)