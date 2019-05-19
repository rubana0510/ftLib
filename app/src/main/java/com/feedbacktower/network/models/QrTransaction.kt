package com.feedbacktower.network.models

import com.google.gson.annotations.SerializedName

data class QrTransaction(
    @SerializedName("id")
    val id: String,
    @SerializedName("maxAmount")
    val maxAmount: Int,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("senderId")
    val senderId: String,
    @SerializedName("senderBusinessId")
    val senderBusinessId: String,
    @SerializedName("amountAvailable")
    val amountAvailable: Double,
    @SerializedName("status")
    val status: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("createdAt")
    val createdAt: String
){
    val txStatus: QrTxStatus
    get() {
        return when(status){
            "GENERATED" -> QrTxStatus.GENERATED
            "SCANNED" -> QrTxStatus.SCANNED
            "REQUESTED" -> QrTxStatus.REQUESTED
            "APPROVED" -> QrTxStatus.APPROVED
            "REJECTED" -> QrTxStatus.REJECTED
            "FAILED" -> QrTxStatus.FAILED
            else -> QrTxStatus.UNKNOWN
        }
    }
}