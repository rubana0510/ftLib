package com.feedbacktower.network.models

import com.feedbacktower.data.models.City
import com.google.gson.annotations.SerializedName
data class AddCityResponse(
    @SerializedName("city")
    val city: City
)