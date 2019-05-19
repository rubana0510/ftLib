package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName

data class PlanTransaction(
    @SerializedName("id")
    val id: String,
    @SerializedName("businessId")
    val businessId: String,
    @SerializedName("subscriptionPlanId")
    val subscriptionPlanId: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("discount")
    val discount: String,
    @SerializedName("sgst")
    val sgst: String,
    @SerializedName("cgst")
    val cgst: String,
    @SerializedName("igst")
    val igst: String,
    @SerializedName("taxAmount")
    val taxAmount: String,
    @SerializedName("totalAmount")
    val totalAmount: String,
    @SerializedName("txnid")
    val txnid: String,
    @SerializedName("mihpayid")
    val mihpayid: Any?,
    @SerializedName("paymentStatus")
    val paymentStatus: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("subscriptionPlan")
    val subscriptionPlan: SubscriptionPlan
)