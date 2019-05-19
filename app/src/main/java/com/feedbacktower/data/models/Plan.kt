package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName

data class Plan(
    @SerializedName("businessCategoryId")
    val businessCategoryId: Any?,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("fee")
    val fee: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("masterBusinessCategoryId")
    val masterBusinessCategoryId: Int,
    @SerializedName("maxPhotoPost")
    val maxPhotoPost: Int,
    @SerializedName("maxTextPost")
    val maxTextPost: Int,
    @SerializedName("maxVideoPost")
    val maxVideoPost: Int,
    @SerializedName("maxWalletCashback")
    val maxWalletCashback: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("period")
    val period: String,
    @SerializedName("periodType")
    val periodType: String,
    @SerializedName("updatedAt")
    val updatedAt: String
) {
    var isSelected: Boolean = false
}