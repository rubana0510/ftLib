package com.feedbacktower.data.models

import com.feedbacktower.data.type.DurationType
import com.google.gson.annotations.SerializedName

data class SubscriptionPlan(
    @SerializedName("businessCategoryId")
    val businessCategoryId: String?,
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
    val period: Int,
    @SerializedName("periodType")
    val periodType: String,
    @SerializedName("updatedAt")
    val updatedAt: String
) {

    var isSelected: Boolean = false
    val durationType: DurationType
        get() = when (periodType) {
            "YEAR" -> DurationType.YEAR
            "MONTH" -> DurationType.MONTH
            else -> DurationType.UNKNOWN
        }

}