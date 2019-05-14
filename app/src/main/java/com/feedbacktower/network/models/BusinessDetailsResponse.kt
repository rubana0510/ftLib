package com.feedbacktower.network.models

import com.feedbacktower.data.models.Business
import com.google.gson.annotations.SerializedName

data class BusinessDetailsResponse(
    @SerializedName("business")
    val business: Business
)