package com.feedbacktower.data.models

data class BusinessCategory(
    val catId: String,
    val catName: String,
    val catType: String,
    val catFee: String,
    var catSelected: Boolean
)
