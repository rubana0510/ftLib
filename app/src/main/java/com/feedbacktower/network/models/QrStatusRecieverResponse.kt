package com.feedbacktower.network.models

import com.feedbacktower.data.models.Business
import com.google.gson.annotations.SerializedName

data class QrStatusRecieverResponse(
    @SerializedName("txn")
    val txn: QrTransaction,
    @SerializedName("sender")
    val sender: Business
)