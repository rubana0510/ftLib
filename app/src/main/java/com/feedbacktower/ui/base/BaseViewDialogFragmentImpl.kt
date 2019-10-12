package com.feedbacktower.ui.base

import com.feedbacktower.network.models.ApiResponse

open class BaseViewDialogFragmentImpl : BaseDialogFragment(), BaseView {
    override fun showNetworkError(error: ApiResponse.ErrorModel) {
    }

    override fun showProgress() {
    }

    override fun dismissProgress() {
    }
}