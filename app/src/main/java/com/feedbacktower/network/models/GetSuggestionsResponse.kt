package com.feedbacktower.network.models

import com.feedbacktower.data.models.Review
import com.feedbacktower.data.models.Suggestion
import com.google.gson.annotations.SerializedName

data class GetSuggestionsResponse(
    @SerializedName("suggestion")
    val suggestions: List<Suggestion>
)