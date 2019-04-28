package com.feedbacktower.network.models

data class ApiResponse<T>(
    val status: Boolean,
    val msg: String,
    val payload: T
)