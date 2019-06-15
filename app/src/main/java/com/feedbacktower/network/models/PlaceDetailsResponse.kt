package com.feedbacktower.network.models

import com.google.gson.annotations.SerializedName

data class PlaceDetailsResponse(
    @SerializedName("result")
    val result: Result,
    @SerializedName("status")
    val status: String
) {
    data class Result(
        @SerializedName("address_components")
        val addressComponents: List<AddressComponent>
    ) {
        data class AddressComponent(
            @SerializedName("long_name")
            val longName: String,
            @SerializedName("short_name")
            val shortName: String,
            @SerializedName("types")
            val types: List<String>
        )
    }
}