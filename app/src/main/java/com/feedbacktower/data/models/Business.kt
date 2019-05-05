package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName


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
    val name: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("rank")
    val rank: Int,
    @SerializedName("regNo")
    val regNo: String?,
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
)