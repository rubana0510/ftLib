package com.feedbacktower.network.models


import com.google.gson.annotations.SerializedName

data class SearchBusinessResponse(
    @SerializedName("businesses")
    val businesses: List<SearchBusiness>
)