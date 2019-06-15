package com.feedbacktower.network.models

import com.feedbacktower.data.models.Transaction
import com.google.gson.annotations.SerializedName

data class GenerateHashResponse(
    @SerializedName("hash")
    val hash: String,
    @SerializedName("txn")
    val txn: Transaction
)