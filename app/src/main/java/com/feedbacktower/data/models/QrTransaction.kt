package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName

data class QrTransaction(
    @SerializedName("id")
    val id: String,
    @SerializedName("senderId")
    val senderId: String,
    @SerializedName("receiverId")
    val receiverId: String,
    @SerializedName("senderBusinessId")
    val senderBusinessId: String,
    @SerializedName("receiverBusinessId")
    val receiverBusinessId: String,
    @SerializedName("amountAvailable")
    val amountAvailable: String,
    @SerializedName("maxAmount")
    val maxAmount: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("sender")
    val sender: User,
    @SerializedName("receiver")
    val receiver: User,
    @SerializedName("isDebit")
    val isDebit: Int
) : BaseModel(id) {
    val user: User
        get() = if (isDebit == 1) receiver else sender
    val displayAmount: String
        get() = if (isDebit == 1) "- Rs.$amount" else "+ Rs.$amount"
}