package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName

data class Plan(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("businessCategoryId")
    val businessCategoryId: Any?,
    @SerializedName("masterBusinessCategoryId")
    val masterBusinessCategoryId: Int,
    @SerializedName("fee")
    val fee: Double,
    @SerializedName("maxWalletCashback")
    val maxWalletCashback: String,
    @SerializedName("maxTextPost")
    val maxTextPost: Int,
    @SerializedName("maxPhotoPost")
    val maxPhotoPost: Int,
    @SerializedName("maxVideoPost")
    val maxVideoPost: Int,
    @SerializedName("period")
    val period: Double,
    @SerializedName("periodType")
    val periodType: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("igst")
    val igst: Double,
    @SerializedName("cgst")
    val cgst: Double,
    @SerializedName("sgst")
    val sgst: Double
) {
    val payableAmount: Double
        get() = fee + igst + sgst + cgst
    var isSelected: Boolean = false
}