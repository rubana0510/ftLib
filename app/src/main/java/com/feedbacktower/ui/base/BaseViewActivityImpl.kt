package com.feedbacktower.ui.base

import com.feedbacktower.network.models.ApiResponse

open class BaseViewActivityImpl : BaseActivity(), BaseView {
    override fun showProgress() {
    }

    override fun dismissProgress() {
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {

    }
}