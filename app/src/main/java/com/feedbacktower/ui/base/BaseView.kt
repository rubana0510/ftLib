package com.feedbacktower.ui.base

import com.feedbacktower.network.models.ApiResponse

interface BaseView {
    fun showProgress()
    fun dismissProgress()
    fun showNetworkError(error: ApiResponse.ErrorModel)
}