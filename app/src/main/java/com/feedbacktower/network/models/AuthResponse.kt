package com.feedbacktower.network.models

import com.feedbacktower.data.models.User
import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("user")
    val user: User
)