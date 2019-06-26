package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Transaction(
    @SerializedName("id")
    val id: String,
    @SerializedName("paymentStatus")
    val paymentStatus: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("businessId")
    val businessId: String,
    @SerializedName("subscriptionPlanId")
    val subscriptionPlanId: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("discount")
    val discount: Double,
    @SerializedName("sgst")
    val sgst: Double,
    @SerializedName("cgst")
    val cgst: Double,
    @SerializedName("igst")
    val igst: Double,
    @SerializedName("txnid")
    val txnid: String,
    @SerializedName("taxAmount")
    val taxAmount: Double,
    @SerializedName("totalAmount")
    val totalAmount: Double,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("createdAt")
    val createdAt: String
): Serializable