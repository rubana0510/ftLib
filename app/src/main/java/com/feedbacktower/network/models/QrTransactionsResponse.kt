package com.feedbacktower.network.models

import com.feedbacktower.data.models.QrTransaction
import com.google.gson.annotations.SerializedName

data class QrTransactionsResponse(
    @SerializedName("txns")
    val transactions: List<QrTransaction>
)