package com.feedbacktower.network.models

class ApiResponse<T>(
    val error: ErrorModel?,
    val msg: String?,
    val payload: T?
) {
    data class ErrorModel(
        val code: String,
        val msg: String
    )
}