package com.feedbacktower.network.models

import com.google.gson.annotations.SerializedName

data class QrPaymentStatus(
    @SerializedName("txn")
    val txn: QrTransaction
)