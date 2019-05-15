package com.feedbacktower.network.models

import com.feedbacktower.data.models.Business
import com.google.gson.annotations.SerializedName

data class QrStatusSenderResponse(
    @SerializedName("txn")
    val txn: QrTransaction,
    @SerializedName("receiver")
    val receiver: Business
)