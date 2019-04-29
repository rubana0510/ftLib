package com.feedbacktower.network.models

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("user")
    val user: User
) {
    data class User(
        @SerializedName("dob")
        val dob: String,
        @SerializedName("emailId")
        val emailId: String,
        @SerializedName("firstName")
        val firstName: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("lastName")
        val lastName: String,
        @SerializedName("phone")
        val phone: String,
        @SerializedName("userType")
        val userType: String
    )
}