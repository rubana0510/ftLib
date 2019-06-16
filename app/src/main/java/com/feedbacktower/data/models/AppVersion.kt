package com.feedbacktower.data.models


import com.google.gson.annotations.SerializedName

data class AppVersion(
    @SerializedName("id")
    val id: Int,
    @SerializedName("version")
    val version: String,
    @SerializedName("code")
    val code: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
) {
    enum class UpdateType { HARD, SOFT }

    val updateType: UpdateType
        get() = if (type == "HARD") UpdateType.HARD else UpdateType.SOFT
}