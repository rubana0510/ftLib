package com.feedbacktower.data.models

import androidx.room.Ignore
import com.google.gson.annotations.SerializedName


data class City(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("stateId")
    val stateId: Int,
    @SerializedName("stateName")
    val stateName: String
) {
    @Ignore
    val isSelected: Boolean = false
}