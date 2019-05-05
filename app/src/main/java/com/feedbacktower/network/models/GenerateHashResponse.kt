package com.feedbacktower.network.models

import com.google.gson.annotations.SerializedName

data class GenerateHashResponse(
    @SerializedName("hash")
    val hash: String
)