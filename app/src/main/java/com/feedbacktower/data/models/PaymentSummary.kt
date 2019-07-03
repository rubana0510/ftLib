package com.feedbacktower.data.models

import java.io.Serializable

data class PaymentSummary(
    val id: String,
    val txid: String,
    val mihpayid: String?,
    val status: String?,
    val errorCode: String?,
    val mode: String?,
    val bankCode: String?,
    val bankRefNo: String?,
    val payuMoneyId: String?,
    val productinfo: String?,
    val firstname: String?,
    val email: String?,
    val udf1: String?,
    val udf2: String?,
    val udf3: String?,
    val udf4: String?,
    val udf5: String?,
    val createdAt: String
): Serializable