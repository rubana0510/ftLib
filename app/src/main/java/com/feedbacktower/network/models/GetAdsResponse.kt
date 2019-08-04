package com.feedbacktower.network.models
import com.feedbacktower.data.models.Ad

data class GetAdsResponse(
    val ads: List<Ad>
)