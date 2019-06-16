package com.feedbacktower.data.models

import com.feedbacktower.util.toPrice
import com.google.gson.annotations.SerializedName

data class Plan(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("businessCategoryId")
    val businessCategoryId: String,
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
    val period: Int,
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

    val totalGst: Double
        get() = igst + cgst + sgst
    val gstAmount: Double
        get() = (fee * (igst / 100 + sgst / 100 + cgst / 100)).toNextInt()
    private val payableAmount: Double
        get() = (fee + gstAmount).toNextInt()

    val payableAmountDisplay: String
        get() = payableAmount.toPrice()
    var isSelected: Boolean = false

    private fun Double.toNextInt(): Double {
        val diff = this - this.toInt()
        return if (diff > 0.0)
            (this.toInt() + 1).toDouble()
        else
            this
    }

}