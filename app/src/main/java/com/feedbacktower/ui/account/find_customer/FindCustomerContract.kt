package com.feedbacktower.ui.account.find_customer

import com.feedbacktower.network.models.FindCustomerResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface FindCustomerContract {
    interface View : BaseView {
        fun onFoundResponse(response: FindCustomerResponse)
    }

    interface Presenter : BasePresenter<View> {
        fun findCustomer(qrData: String)
    }
}