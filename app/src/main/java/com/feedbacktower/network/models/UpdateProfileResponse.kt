package com.feedbacktower.network.models
import com.google.gson.annotations.SerializedName



data class UpdateProfileResponse(
    @SerializedName("userData")
    val userData: UserData
) {
    data class UserData(
        @SerializedName("dob")
        val dob: String,
        @SerializedName("emailId")
        val emailId: String,
        @SerializedName("firstName")
        val firstName: String,
        @SerializedName("lastName")
        val lastName: String
    )
}