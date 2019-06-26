package com.feedbacktower.network.models

import com.feedbacktower.data.models.Transaction
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GenerateHashResponse(
    @SerializedName("hash")
    val hash: String,
    @SerializedName("txn")
    val txn: Transaction
): Serializable