package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Location(
    @SerializedName("coordinates")
    val coordinates: List<Double>?,
    @SerializedName("type")
    val type: String
): Serializable {
    val latitude: Double?
        get() {
            if (coordinates != null && coordinates.size > 1)
                return coordinates[0]
            return null
        }
    val longitude: Double?
        get() {
            if (coordinates != null && coordinates.size > 1)
                return coordinates[1]
            return null
        }
}