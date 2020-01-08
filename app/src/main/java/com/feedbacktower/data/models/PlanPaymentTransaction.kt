package com.feedbacktower.data.models


import com.google.gson.annotations.SerializedName

data class PlanPaymentTransaction(
    @SerializedName("id")
    val id: String,
    @SerializedName("userId")
    val userId: String,
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
    val mihpayid: String?,
    @SerializedName("mode")
    val mode: String?,
    @SerializedName("bankCode")
    val bankCode: String?,
    @SerializedName("errorCode")
    val errorCode: String?,
    @SerializedName("bankRefNum")
    val bankRefNum: String?,
    @SerializedName("payuMoneyId")
    val payuMoneyId: String?,
    @SerializedName("status")
    val status: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
) {

    enum class Status { FAILURE, PENDING, SUCCESS }

    val paymentStatus: Status
        get() = when (status) {
            "FAILURE" -> Status.FAILURE
            "SUCCESS" -> Status.SUCCESS
            else -> Status.PENDING
        }
}