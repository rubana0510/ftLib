package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName


data class User(
    @SerializedName("city")
    val city: Any?,
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
    @SerializedName("profileSetup")
    val profileSetup: Boolean,
    @SerializedName("userType")
    val userType: String,
    @SerializedName("business")
    val business: Business?
)