package com.feedbacktower.ui.base

import com.feedbacktower.network.models.ApiResponse

open class BaseViewFragmentImpl : BaseFragment(), BaseView {
    override fun showNetworkError(error: ApiResponse.ErrorModel) {
    }

    override fun showProgress() {
    }

    override fun dismissProgress() {
    }
}