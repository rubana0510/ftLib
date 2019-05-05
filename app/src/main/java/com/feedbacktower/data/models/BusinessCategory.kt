package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName

data class BusinessCategory(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("popular")
    val popular: Int,
    @SerializedName("featured")
    val featured: Boolean
) {
    val image: String
        get() = "base_url" + id

    var selected: Boolean = false
}
