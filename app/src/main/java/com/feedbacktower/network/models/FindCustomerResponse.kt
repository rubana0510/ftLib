package com.feedbacktower.network.models

import com.feedbacktower.data.models.User
import com.google.gson.annotations.SerializedName

data class FindCustomerResponse(
    @SerializedName("user")
    val user: User
)