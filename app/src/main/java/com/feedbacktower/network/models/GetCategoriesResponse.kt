package com.feedbacktower.network.models

import com.feedbacktower.data.models.BusinessCategory
import com.google.gson.annotations.SerializedName

data class GetCategoriesResponse(
    @SerializedName("list")
    val featured: List<BusinessCategory>,
    @SerializedName("other")
    val other : List<BusinessCategory>
)