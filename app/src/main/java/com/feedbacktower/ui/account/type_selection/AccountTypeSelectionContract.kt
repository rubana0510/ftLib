package com.feedbacktower.ui.account.type_selection

import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface AccountTypeSelectionContract {
    interface View : BaseView {
        fun onBusinessRegistered()
        fun onContinueCustomerResponse()
    }

    interface Presenter : BasePresenter<View> {
        fun continueAsCustomer()
        fun registerAsBusiness()
    }
}