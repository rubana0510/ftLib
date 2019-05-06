package com.feedbacktower.network.models

import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    @SerializedName("mihpayid")
    val mihpayid: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("txnid")
    val txnid: String
)