package com.feedbacktower.ui.qrtransfer.receiver.wait

import com.feedbacktower.network.models.QrPaymentStatus
import com.feedbacktower.network.models.QrStatusRecieverResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface ReceiverWaitContract {
    interface View : BaseView {
        fun onListenResponse(response: QrStatusRecieverResponse?)
        fun onListenShowProgress()
        fun onListenHideProgress()
        fun onPaymentRequestResponse(response: QrPaymentStatus?)
        fun onPaymentRequestShowProgress()
        fun onPaymentRequestHideProgress()
    }

    interface Presenter : BasePresenter<View> {
        fun listenForChanges(txId: String)
        fun sendPaymentRequest(txId: String, amount: Double)
    }
}