package com.feedbacktower.network.models

import com.google.gson.annotations.SerializedName
data class AutoCompleteResponse(
    @SerializedName("predictions")
    val predictions: List<Prediction>,
    @SerializedName("status")
    val status: String
) {
    data class Prediction(
        @SerializedName("description")
        val description: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("place_id")
        val placeId: String,
        @SerializedName("reference")
        val reference: String,
        @SerializedName("types")
        val types: List<String>
    )
}