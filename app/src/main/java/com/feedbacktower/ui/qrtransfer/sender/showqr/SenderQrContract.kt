package com.feedbacktower.ui.qrtransfer.sender.showqr

import com.feedbacktower.network.models.QrPaymentStatus
import com.feedbacktower.network.models.QrStatusSenderResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface SenderQrContract {
    interface View : BaseView {
        fun showQrDataFetchProgress()
        fun onQrDataFetched(response: QrPaymentStatus?)
        fun hideQrDataFetchProgress()
        fun showQrPaymentStatusProgress()
        fun onQrPaymentStatusResponse(response: QrStatusSenderResponse?)
    }

    interface Presenter : BasePresenter<View> {
        fun fetchQrData()
        fun listenForChanges(txId: String)
    }
}