package com.feedbacktower.network.models

import com.feedbacktower.data.models.PlanTransaction
import com.google.gson.annotations.SerializedName

data class PlanTransactionsResponse(
    @SerializedName("transactions")
    val transactions: List<PlanTransaction>
)