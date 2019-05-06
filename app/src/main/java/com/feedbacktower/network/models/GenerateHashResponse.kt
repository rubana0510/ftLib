package com.feedbacktower.network.models

import com.google.gson.annotations.SerializedName

data class GenerateHashResponse(
    @SerializedName("hash")
    val hash: String,
    @SerializedName("txn")
    val txn: Txn
) {
    data class Txn(
        @SerializedName("amount")
        val amount: Int,
        @SerializedName("businessId")
        val businessId: String,
        @SerializedName("cgst")
        val cgst: Int,
        @SerializedName("createdAt")
        val createdAt: String,
        @SerializedName("discount")
        val discount: Int,
        @SerializedName("id")
        val id: String,
        @SerializedName("igst")
        val igst: Int,
        @SerializedName("paymentStatus")
        val paymentStatus: String,
        @SerializedName("sgst")
        val sgst: Int,
        @SerializedName("status")
        val status: String,
        @SerializedName("subscriptionPlanId")
        val subscriptionPlanId: String,
        @SerializedName("taxAmount")
        val taxAmount: Double,
        @SerializedName("totalAmount")
        val totalAmount: Double,
        @SerializedName("txnid")
        val txnid: String,
        @SerializedName("updatedAt")
        val updatedAt: String
    )
}