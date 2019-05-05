package com.feedbacktower.network.models
import com.google.gson.annotations.SerializedName


data class GenerateHashRequest(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstname")
    val firstname: String,
    @SerializedName("productinfo")
    val productinfo: String,
    @SerializedName("txnid")
    val txnid: String,
    @SerializedName("udf1")
    val udf1: String,
    @SerializedName("udf2")
    val udf2: String,
    @SerializedName("udf3")
    val udf3: String,
    @SerializedName("udf4")
    val udf4: String,
    @SerializedName("udf5")
    val udf5: String
)