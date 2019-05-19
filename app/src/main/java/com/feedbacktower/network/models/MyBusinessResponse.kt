package com.feedbacktower.network.models

import com.feedbacktower.data.models.Business
import com.feedbacktower.data.models.MyBusiness
import com.google.gson.annotations.SerializedName

data class MyBusinessResponse(
    @SerializedName("business")
    val business: MyBusiness
)