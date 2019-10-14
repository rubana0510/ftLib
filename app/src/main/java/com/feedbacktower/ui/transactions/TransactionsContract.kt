package com.feedbacktower.ui.transactions

import com.feedbacktower.network.models.QrTransactionsResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface TransactionsContract {
    interface View : BaseView {
        fun onFetched(response: QrTransactionsResponse?, timestamp: String?)
    }

    interface Presenter : BasePresenter<View> {
        fun fetch(timestamp: String)
    }
}