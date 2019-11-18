package com.feedbacktower.network.models

import com.google.gson.annotations.SerializedName

class ApiResponse<T>(
    val error: ErrorModel?,
    val msg: String?,
    val payload: T?
) {

    companion object {
        fun <T> toError(code: String, msg: String, type: ErrorType): ApiResponse<T> {
            return ApiResponse(
                error = ErrorModel(
                    code,
                    msg,
                    type
                ), msg = null, payload = null
            )
        }
        fun <T> toResponse(response: T): ApiResponse<T> {
            return ApiResponse(
                error = null, msg = null, payload = response
            )
        }
    }

    data class ErrorModel(
        val code: String,
        @SerializedName("msg")
        val message: String,
        val error: ErrorType?
    )

    enum class ErrorType { NO_INTERNET, HTTP_EXCEPTION, TIMEOUT, UNKNOWN }

}

fun getUnknownError(status: String? = null): ApiResponse.ErrorModel {
    return ApiResponse.ErrorModel(
        status ?: "unknown",
        "Network error occurred",
        ApiResponse.ErrorType.UNKNOWN
    )
}