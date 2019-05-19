package com.feedbacktower.network.models

import com.feedbacktower.data.models.Plan
import com.google.gson.annotations.SerializedName

data class PlanListResponse(
    @SerializedName("list")
    val list: List<Plan>
)
