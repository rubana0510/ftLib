package com.feedbacktower.network.models

import com.feedbacktower.data.models.City
import com.google.gson.annotations.SerializedName

data class GetCitiesResponse(
    @SerializedName("cities")
    val cities: List<City>
)