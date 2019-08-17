package com.feedbacktower.network.models

import com.google.gson.annotations.SerializedName

class ApiResponse<T>(
    val error: ErrorModel?,
    val msg: String?,
    val payload: T?
) {
    data class ErrorModel(
        val code: String,
        @SerializedName("msg")
        val message: String
    )
}