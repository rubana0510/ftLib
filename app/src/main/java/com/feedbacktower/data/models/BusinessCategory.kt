package com.feedbacktower.data.models

import com.google.gson.annotations.SerializedName

data class BusinessCategory(
    @SerializedName("id")
    val id: String,
    @SerializedName("masterBusinessCategoryId")
    val masterBusinessCategoryId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("popular")
    val popular: Int,
    @SerializedName("featured")
    val featured: Boolean
) {
    var selected: Boolean = false
}
