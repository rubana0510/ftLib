package com.feedbacktower.network.models

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("userId")
    val userId: String
)