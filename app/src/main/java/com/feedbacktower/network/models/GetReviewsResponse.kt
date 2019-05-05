package com.feedbacktower.network.models

import com.feedbacktower.data.models.Review
import com.google.gson.annotations.SerializedName

data class GetReviewsResponse(
    @SerializedName("review")
    val review: List<Review>
)