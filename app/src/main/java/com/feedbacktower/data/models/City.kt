package com.feedbacktower.data.models

data class City(
    val cityId: String,
    val cityName: String,
    val stateId: String,
    val stateName: String,
    var isSelected: Boolean = false
)