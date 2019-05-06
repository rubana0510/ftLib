package com.feedbacktower.data.models

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
    val isSelected: Boolean = false
}