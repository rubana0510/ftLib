package com.feedbacktower.network.models

import com.google.gson.annotations.SerializedName

data class PreRegResponse(
    @SerializedName("user")
    val user: User
) {
    data class User(
        @SerializedName("createdAt")
        val createdAt: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("otp")
        val otp: String,
        @SerializedName("otpRequestAttempts")
        val otpRequestAttempts: Int,
        @SerializedName("otpRequestedAt")
        val otpRequestedAt: String,
        @SerializedName("phone")
        val phone: String,
        @SerializedName("profileSetup")
        val profileSetup: Boolean,
        @SerializedName("updatedAt")
        val updatedAt: String,
        @SerializedName("userType")
        val userType: String,
        @SerializedName("verified")
        val verified: Boolean,
        @SerializedName("visible")
        val visible: Boolean
    )
}