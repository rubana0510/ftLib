package com.feedbacktower.network.models

import com.feedbacktower.data.models.PlanPaymentTransaction
import com.feedbacktower.data.models.PlanTransaction
import com.google.gson.annotations.SerializedName

data class PaymentTxnResponse(
    @SerializedName("transaction")
    val transaction: PlanPaymentTransaction
)