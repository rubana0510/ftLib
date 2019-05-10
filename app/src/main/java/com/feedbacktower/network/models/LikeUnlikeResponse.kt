package com.feedbacktower.network.models

import com.google.gson.annotations.SerializedName

data class LikeUnlikeResponse(
    @SerializedName("liked")
    val liked: Boolean
)